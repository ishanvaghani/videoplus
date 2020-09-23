package com.example.videoplus

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplus.Adapter.MovieAdapter
import com.example.videoplus.Adapter.SearchMovieAdapter
import com.example.videoplus.Model.Movie
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {

    private lateinit var searchMovieAdapter: SearchMovieAdapter
    private lateinit var progressBar: ProgressBar

    private var movies: ArrayList<Movie> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recyclerView: RecyclerView = findViewById(R.id.search_recyclerView)
        progressBar = findViewById(R.id.progressBar_search)
        val searchMovie: EditText = findViewById(R.id.search)

        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = gridLayoutManager

        fetchMovies(recyclerView)

        searchMovie.requestFocus()
        searchMovie.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                search(s.toString())
            }
        })
    }

    private fun fetchMovies(recyclerView: RecyclerView) {
        val reference = FirebaseFirestore.getInstance().collection("Movies")

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val movie = Movie(document.getString("name")!!, document.getString("thumbnail")!!, document.getString("id")!!)
                    movies.add(movie)
                }
                searchMovieAdapter = SearchMovieAdapter(this, movies!!)
                recyclerView.adapter = searchMovieAdapter
                progressBar.visibility = View.GONE
            } else {
                Toast.makeText(this, "Something Wrong 3!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun search(string: String) {
        val searchMovies: ArrayList<Movie> = ArrayList()
        for (movie in movies) {
            if (movie.name.toLowerCase().contains(string.toLowerCase())) {
                searchMovies.add(movie)
            }
        }

        searchMovieAdapter.searchList(searchMovies)
    }
}