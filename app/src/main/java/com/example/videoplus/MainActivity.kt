package com.example.videoplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplus.Adapter.MovieAdapter
import com.example.videoplus.Adapter.SliderAdapter
import com.example.videoplus.Model.Movie
import com.example.videoplus.Model.Slider
import com.google.firebase.firestore.FirebaseFirestore
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderView

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sliderView = findViewById<SliderView>(R.id.imageSlider)
        val popularRecyclerView = findViewById<RecyclerView>(R.id.popular_recyclerView)
        val actionRecyclerView = findViewById<RecyclerView>(R.id.action_recyclerView)
        val comedyRecyclerView = findViewById<RecyclerView>(R.id.comedy_recyclerView)
        val scifiRecyclerView = findViewById<RecyclerView>(R.id.scifi_recyclerView)
        val horrorRecyclerView = findViewById<RecyclerView>(R.id.horror_recyclerView)
        val romanticRecyclerView = findViewById<RecyclerView>(R.id.romantic_recyclerView)
        progressBar = findViewById(R.id.progressBar_main)
        linearLayout = findViewById(R.id.linerLayout_main)
        val search: ImageView = findViewById(R.id.searchButton)

        val popularLinearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        popularLinearLayoutManager.stackFromEnd = true
        popularRecyclerView.setHasFixedSize(true)
        popularRecyclerView.layoutManager = popularLinearLayoutManager
        fetchPopularMovies(popularRecyclerView)

        val actionLinearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        actionLinearLayoutManager.stackFromEnd = true
        actionRecyclerView.setHasFixedSize(true)
        actionRecyclerView.layoutManager = actionLinearLayoutManager
        fetchActionMovies(actionRecyclerView)

        val comedyLinearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        comedyLinearLayoutManager.stackFromEnd = true
        comedyRecyclerView.setHasFixedSize(true)
        comedyRecyclerView.layoutManager = comedyLinearLayoutManager
        fetchComedyMovies(comedyRecyclerView)

        val scifiLinearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        scifiLinearLayoutManager.stackFromEnd = true
        scifiRecyclerView.setHasFixedSize(true)
        scifiRecyclerView.layoutManager = scifiLinearLayoutManager
        fetchSciFiMovies(scifiRecyclerView)

        val horrorLinearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        horrorLinearLayoutManager.stackFromEnd = true
        horrorRecyclerView.setHasFixedSize(true)
        horrorRecyclerView.layoutManager = horrorLinearLayoutManager
        fetchHorrorMovies(horrorRecyclerView)

        val romanticLinearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        romanticLinearLayoutManager.stackFromEnd = true
        romanticRecyclerView.setHasFixedSize(true)
        romanticRecyclerView.layoutManager = romanticLinearLayoutManager
        fetchRomanticMovies(romanticRecyclerView)

        fetchSlider(sliderView)

        search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun fetchSlider(sliderView: SliderView) {
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)

        val images: ArrayList<Slider> = ArrayList()

        val reference = FirebaseFirestore.getInstance().collection("Movies")

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if(document.getBoolean("carousel") == true) {
                        val slider = Slider(document.getString("carouselImage")!!, document.getString("id")!!)
                        images.add(slider)
                    }
                }
                val sliderAdapter = SliderAdapter(this, images)
                sliderView.setSliderAdapter(sliderAdapter)
                sliderView.startAutoCycle()
                linearLayout.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            } else {
                Toast.makeText(this, "Something Wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchRomanticMovies(romanticRecyclerView: RecyclerView?) {
        val movies: ArrayList<Movie> = ArrayList()
        val reference = FirebaseFirestore.getInstance().collection("Movies")

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if (document.getString("type") == "Romantic") {
                        val movie = Movie(document.getString("thumbnail")!!, document.getString("id")!!)
                        movies.add(movie)
                    }
                }
                val movieAdapter = MovieAdapter(this, movies)
                romanticRecyclerView!!.adapter = movieAdapter
            } else {
                Toast.makeText(this, "Something Wrong 3!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchHorrorMovies(horrorRecyclerView: RecyclerView?) {
        val movies: ArrayList<Movie> = ArrayList()
        val reference = FirebaseFirestore.getInstance().collection("Movies")

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if (document.getString("type") == "Horror") {
                        val movie = Movie(document.getString("thumbnail")!!, document.getString("id")!!)
                        movies.add(movie)
                    }
                }
                val movieAdapter = MovieAdapter(this, movies)
                horrorRecyclerView!!.adapter = movieAdapter
            } else {
                Toast.makeText(this, "Something Wrong 3!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchSciFiMovies(scifiRecyclerView: RecyclerView?) {
        val movies: ArrayList<Movie> = ArrayList()
        val reference = FirebaseFirestore.getInstance().collection("Movies")

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if (document.getString("type") == "Sci-fi") {
                        val movie = Movie(document.getString("thumbnail")!!, document.getString("id")!!)
                        movies.add(movie)
                    }
                }
                val movieAdapter = MovieAdapter(this, movies)
                scifiRecyclerView!!.adapter = movieAdapter
            } else {
                Toast.makeText(this, "Something Wrong 3!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchComedyMovies(comedyRecyclerView: RecyclerView?) {
        val movies: ArrayList<Movie> = ArrayList()
        val reference = FirebaseFirestore.getInstance().collection("Movies")

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if (document.getString("type") == "Comedy") {
                        val movie = Movie(document.getString("thumbnail")!!, document.getString("id")!!)
                        movies.add(movie)
                    }
                }
                val movieAdapter = MovieAdapter(this, movies)
                comedyRecyclerView!!.adapter = movieAdapter
            } else {
                Toast.makeText(this, "Something Wrong 3!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchActionMovies(actionRecyclerView: RecyclerView?) {
        val movies: ArrayList<Movie> = ArrayList()
        val reference = FirebaseFirestore.getInstance().collection("Movies")

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if (document.getString("type") == "Action") {
                        val movie = Movie(document.getString("thumbnail")!!, document.getString("id")!!)
                        movies.add(movie)
                    }
                }
                val movieAdapter = MovieAdapter(this, movies)
                actionRecyclerView!!.adapter = movieAdapter
            } else {
                Toast.makeText(this, "Something Wrong 3!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchPopularMovies(popularRecyclerView: RecyclerView?) {
        val movies: ArrayList<Movie> = ArrayList()

        val reference = FirebaseFirestore.getInstance().collection("Movies")

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val movie = Movie(document.getString("thumbnail")!!, document.getString("id")!!)
                    movies.add(movie)
                }
                val movieAdapter = MovieAdapter(this, movies)
                popularRecyclerView!!.adapter = movieAdapter
            } else {
                Toast.makeText(this, "Something Wrong 3!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}