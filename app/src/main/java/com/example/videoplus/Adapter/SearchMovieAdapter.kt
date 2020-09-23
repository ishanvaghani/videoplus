package com.example.videoplus.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplus.Model.Movie
import com.example.videoplus.MovieDetailsActivity
import com.example.videoplus.R

class SearchMovieAdapter(private var mContext: Context, private var movies: ArrayList<Movie>) : RecyclerView.Adapter<SearchMovieAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.search_movie_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]

        Glide.with(mContext).load(movie.thumbnail).into(holder.movieImage)
        val id = movie.id

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, MovieDetailsActivity::class.java)
            intent.putExtra("id", id)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val movieImage: ImageView = itemView.findViewById(R.id.movieImage_searchMovieItem)
    }

    fun searchList(searchMovies: ArrayList<Movie>) {
        movies = searchMovies
        notifyDataSetChanged()
    }
}