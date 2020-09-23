package com.example.videoplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplus.Adapter.ActorAdapter
import com.example.videoplus.Adapter.MovieAdapter
import com.example.videoplus.Model.Movie
import com.google.firebase.firestore.FirebaseFirestore

class ActorActivity : AppCompatActivity() {

    private lateinit var actorImage: ImageView
    private lateinit var actorName: TextView
    private lateinit var actorDetails: TextView
    private lateinit var actorMovies: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var linearLayout: LinearLayout

    private lateinit var id: String
    private var noOfMovies: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor)

        actorImage = findViewById(R.id.actorImage_actor)
        actorName = findViewById(R.id.actorName_actor)
        actorDetails = findViewById(R.id.actorDetails_actor)
        actorMovies = findViewById(R.id.actorMovies_actor)
        progressBar = findViewById(R.id.progressBar_actor)
        linearLayout = findViewById(R.id.linearLayout_actor)

        val gridLayoutManager = GridLayoutManager(this, 3)
        actorMovies.setHasFixedSize(true)
        actorMovies.layoutManager = gridLayoutManager

        id = intent.getStringExtra("id")!!
        fetchActorDetails()
    }

    private fun fetchActorDetails() {

        val reference = FirebaseFirestore.getInstance().collection("Actors").document(id)

        reference.get().addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val document = task.result
                if(document!!.exists()) {
                    actorName.text = document.getString("name")
                    actorDetails.text = document.getString("description")
                    noOfMovies = document.get("movies") as ArrayList<String>
                    Glide.with(this).load(document.getString("image")).into(actorImage)
                    fetchActorMovies()
                }
            }
        }
    }

    private fun fetchActorMovies() {

        val movies: ArrayList<Movie> = ArrayList()

        for(a in noOfMovies) {
            val reference = FirebaseFirestore.getInstance().collection("Movies")
            reference.get().addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    for(document in task.result!!) {
                        if(document.id == a) {
                            movies.add(Movie(document.getString("thumbnail")!!, document.getString("id")!!))
                        }
                    }
                    val movieAdapter = MovieAdapter(this, movies)
                    actorMovies.adapter = movieAdapter
                }
                else {
                    Toast.makeText(this, "Something Wrong 3!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        progressBar.visibility = View.GONE
        linearLayout.visibility = View.VISIBLE
    }
}