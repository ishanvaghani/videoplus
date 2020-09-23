package com.example.videoplus

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplus.Adapter.ActorAdapter
import com.example.videoplus.Model.Actor
import com.example.videoplus.Services.FloatingWidgetServices
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.security.Permission
import java.security.Permissions

class MovieDetailsActivity : AppCompatActivity() {

    private var player: SimpleExoPlayer ?= null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private lateinit var playerView: PlayerView
    private lateinit var progressBar: CircularProgressBar
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var movieName: TextView
    private lateinit var movieDescription: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var like: ImageView
    private lateinit var download: ImageView
    private lateinit var share: ImageView
    private lateinit var thumbnail: ImageView
    private lateinit var scrollView: ScrollView

    private lateinit var movieId: String
    private lateinit var video: String
    private lateinit var movieNameString: String
    private var orientation: Int ?= null
    private var isLike = false

    private var noOfActors: ArrayList<String> = ArrayList()
    private var noOfLikes: ArrayList<String> = ArrayList()
    private val currentUserId: String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        playerView = findViewById(R.id.playerView_movieDetails)
        val linerLayout: LinearLayout = findViewById(R.id.linerLayout_movieDetails)
        val expandButton: ImageView = findViewById(R.id.expand_movieDetails)
        like = findViewById(R.id.like_moviesDetails)
        download = findViewById(R.id.download_moviesDetails)
        share = findViewById(R.id.share_moviesDetails)
        movieName = findViewById(R.id.movieName_movieDetails)
        movieDescription = findViewById(R.id.movieDescription_movieDetails)
        recyclerView = findViewById(R.id.actorsRecyclerView_movieDetails)
        progressBar = findViewById(R.id.progressBar_movieDetails)
        progressBarLoading = findViewById(R.id.progressBarLoading_movieDetails)
        scrollView = findViewById(R.id.scrollView_movieDetails)
        thumbnail = findViewById(R.id.image_movieDetails)
        val btFullScreen: ImageView = findViewById(R.id.bt_fullscren)
        val expandLayout: RelativeLayout = findViewById(R.id.expandLayout_movieDetails)
        val pictureInPicture: ImageView = findViewById(R.id.pictureInPicture_customLayout)

        askPermission()

        orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
            btFullScreen.setImageResource(R.drawable.ic_fullscreen_exit)
        }
        else {
            btFullScreen.setImageResource(R.drawable.ic_fullscreen)
        }

        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

        movieId = intent.getStringExtra("id")!!

        btFullScreen.setOnClickListener {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }

        like.setOnClickListener {
            likeMovie()
        }

        share.setOnClickListener {
            val text = "$movieNameString\n\nWatch :- $video"
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(Intent.createChooser(intent, "Share via"))
        }

        download.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                }
                else {
                    downloadVideo()
                }
            }
            else {
                downloadVideo()
            }
        }

        expandLayout.setOnClickListener {
            if(linerLayout.visibility == View.GONE) {
                linerLayout.visibility = View.VISIBLE
                expandButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                linerLayout.visibility = View.GONE
                expandButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        if(savedInstanceState != null) {
            currentWindow = savedInstanceState.getInt("currentWindow")
            playWhenReady = savedInstanceState.getBoolean("playWhenReady")
            playbackPosition = savedInstanceState.getLong("playbackPosition")
        }

        pictureInPicture.setOnClickListener {
            player!!.playWhenReady = false
            releasePlayer()
            val serviceIntent = Intent(this, FloatingWidgetServices::class.java)
            serviceIntent.putExtra("videoUrl", video)
            serviceIntent.putExtra("currentWindow", currentWindow)
            serviceIntent.putExtra("playbackPosition", playbackPosition)
            serviceIntent.putExtra("id", movieId)
            startService(serviceIntent)
            finish()
        }
    }

    private fun downloadVideo() {
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(video))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setTitle(movieNameString)
        request.setDescription("Downloading...")
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, movieNameString)

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        Toast.makeText(this, "$movieNameString downloading...", Toast.LENGTH_SHORT).show()
    }

    private fun likeMovie() {
        if(isLike) {
            val reference = FirebaseFirestore.getInstance().collection("Movies").document(movieId)
            reference.update("Likes", FieldValue.arrayRemove(currentUserId))

            like.setImageResource(R.drawable.ic_baseline_thumb_up_24)
            isLike = false
        }
        else {
            val reference = FirebaseFirestore.getInstance().collection("Movies").document(movieId)
            reference.update("Likes", FieldValue.arrayUnion(currentUserId))

            like.setImageResource(R.drawable.ic_baseline_thumb_up_done_24)
            isLike = true
        }
    }

    private fun fetchMovieDetails() {

        val reference = FirebaseFirestore.getInstance().collection("Movies").document(movieId)

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    movieName.text = document.getString("name")
                    movieNameString = document.getString("name")!!
                    movieDescription.text = document.getString("description")
                    video = document.getString("video")!!
                    Glide.with(this).load(document.getString("carouselImage")).into(thumbnail)
                    noOfActors = document.get("Actors") as ArrayList<String>
                    noOfLikes = document.get("Likes") as ArrayList<String>
                    fetchActors()
                    isLiked()
                    progressBarLoading.visibility = View.GONE
                    scrollView.visibility = View.VISIBLE
                    initializePlayer()
                } else {
                    Toast.makeText(this, "Something Wrong!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Something Wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLiked() {
        for(a in noOfLikes) {
            if(a == currentUserId) {
                like.setImageResource(R.drawable.ic_baseline_thumb_up_done_24)
                isLike = true
            }
            else {
                like.setImageResource(R.drawable.ic_baseline_thumb_up_24)
                isLike = false
            }
        }
    }

    private fun askPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M  && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 2004)
        }
    }

    private fun initializePlayer() {

        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player
        playerView.keepScreenOn = true

        val uri = Uri.parse(video)
        val mediaSource: MediaSource = buildMediaSource(uri)

        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.prepare(mediaSource, false, false)

        player!!.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.visibility = View.GONE
                    thumbnail.visibility = View.GONE
                }
            }
        })
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,
            "exoplayer-codelab"
        )
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.release()
            player = null
        }
    }

    private fun fetchActors() {
        val actors: ArrayList<Actor> = ArrayList()

        for(a in noOfActors) {
            val reference = FirebaseFirestore.getInstance().collection("Actors")
            reference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        if (document.id == a) {
                            val actor = Actor(
                                document.getString("id")!!,
                                document.getString("name")!!,
                                document.getString(
                                    "image"
                                )!!
                            )
                            actors.add(actor)
                        }
                    }
                    val actorAdapter = ActorAdapter(this, actors)
                    recyclerView.adapter = actorAdapter
                } else {
                    Toast.makeText(this, "Something Wrong 3!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            fetchMovieDetails()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23) {
            fetchMovieDetails()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("currentWindow", currentWindow)
        outState.putBoolean("playWhenReady", playWhenReady)
        outState.putLong("playbackPosition", playbackPosition)
    }
}