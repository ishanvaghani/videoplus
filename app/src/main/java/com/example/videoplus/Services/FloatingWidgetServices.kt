package com.example.videoplus.Services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.example.videoplus.MovieDetailsActivity
import com.example.videoplus.R
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class FloatingWidgetServices : Service() {

    private var mWindowManager: WindowManager ?= null
    private var mFloatingWidget: View ?= null
    private lateinit var videoUri: Uri
    private var exoPlayer: SimpleExoPlayer ?= null
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: CircularProgressBar

    private var currentWindow = 0
    private var playbackPosition = 0L
    private var movieId = ""

    override fun onBind(intent: Intent): IBinder {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(intent != null) {
            videoUri = Uri.parse(intent.getStringExtra("videoUrl"))
            currentWindow = intent.getIntExtra("currentWindow", 0)
            playbackPosition = intent.getLongExtra("playbackPosition", 0L)
            movieId = intent.getStringExtra("id")!!

            if(mWindowManager != null && mFloatingWidget!!.isShown && exoPlayer != null) {
                mWindowManager!!.removeView(mFloatingWidget)
                mFloatingWidget = null
                mWindowManager = null
                exoPlayer!!.playWhenReady = false
                exoPlayer!!.release()
                exoPlayer = null
            }

            val params: WindowManager.LayoutParams
            mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.custom_pop_up_window, null)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params = WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
            }
            else {
                params = WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
            }

            mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            mWindowManager!!.addView(mFloatingWidget, params)

            exoPlayer = SimpleExoPlayer.Builder(this).build()

            playerView = mFloatingWidget!!.findViewById(R.id.playerView)
            val imageViewClose: ImageView = mFloatingWidget!!.findViewById(R.id.imageViewDismiss)
            val imageViewMaximize: ImageView = mFloatingWidget!!.findViewById(R.id.imageViewMaximize)
            progressBar = mFloatingWidget!!.findViewById(R.id.progressBar_popUp)

            imageViewMaximize.setOnClickListener {
                if(mWindowManager != null && mFloatingWidget!!.isShown && exoPlayer != null) {
                    mWindowManager!!.removeView(mFloatingWidget)
                    mFloatingWidget = null
                    mWindowManager = null
                    exoPlayer!!.playWhenReady = false
                    exoPlayer!!.release()
                    exoPlayer = null
                    stopSelf()

                    val activityIntent = Intent(this, MovieDetailsActivity::class.java)
                    activityIntent.putExtra("currentWindow", currentWindow)
                    activityIntent.putExtra("playbackPosition", playbackPosition)
                    activityIntent.putExtra("id", movieId)
                    activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(activityIntent)
                }
            }

            imageViewClose.setOnClickListener {
                if(mWindowManager != null && mFloatingWidget!!.isShown && exoPlayer != null) {
                    mWindowManager!!.removeView(mFloatingWidget)
                    mFloatingWidget = null
                    mWindowManager = null
                    exoPlayer!!.playWhenReady = false
                    exoPlayer!!.release()
                    exoPlayer = null
                    stopSelf()
                }
            }

            playVideo()

            mFloatingWidget!!.findViewById<View>(R.id.relativeLayoutPopUp)
                .setOnTouchListener(object : View.OnTouchListener {
                    private var initialX = 0
                    private var initialY = 0
                    private var initialTouchX = 0f
                    private var initialTouchY = 0f
                    override fun onTouch(v: View?, event: MotionEvent): Boolean {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                initialX = params.x
                                initialY = params.y
                                initialTouchX = event.rawX
                                initialTouchY = event.rawY
                                return true
                            }
                            MotionEvent.ACTION_UP -> {
                                return true
                            }
                            MotionEvent.ACTION_MOVE -> {
                                params.x = initialX + (event.rawX - initialTouchX).toInt()
                                params.y = initialY + (event.rawY - initialTouchY).toInt()
                                mWindowManager!!.updateViewLayout(mFloatingWidget, params)
                                return true
                            }
                        }
                        return false
                    }
                })
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun playVideo() {

        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,
            "exoplayer-codelab"
        )
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            videoUri
        )
        playerView.player = exoPlayer
        exoPlayer!!.playWhenReady = true
        exoPlayer!!.seekTo(currentWindow, playbackPosition)
        exoPlayer!!.prepare(mediaSource, false, false)

        exoPlayer!!.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mFloatingWidget != null) {
            mWindowManager!!.removeView(mFloatingWidget)
        }
    }
}