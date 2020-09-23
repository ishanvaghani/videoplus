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
import com.example.videoplus.ActorActivity
import com.example.videoplus.Model.Actor
import com.example.videoplus.Model.Movie
import com.example.videoplus.R

class ActorAdapter(private var mContext: Context, private var actors: ArrayList<Actor>) : RecyclerView.Adapter<ActorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.actor_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val actor = actors[position]

        holder.actorName.text = actor.actorName
        Glide.with(mContext).load(actor.actorImage).into(holder.actorImage)
        val id = actor.actorId

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ActorActivity::class.java)
            intent.putExtra("id", id)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return actors.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val actorImage: ImageView = itemView.findViewById(R.id.actorImage_actorItem)
        val actorName: TextView = itemView.findViewById(R.id.actorName_actorItem)
    }
}