package com.example.videoplus.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.videoplus.Model.Slider
import com.example.videoplus.MovieDetailsActivity
import com.example.videoplus.R
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(private val mContext: Context, private val sliders: ArrayList<Slider>) : SliderViewAdapter<SliderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.slider_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val slider = sliders[position]

        Glide.with(mContext).load(slider.image).into(viewHolder.image)
        val id = slider.id

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(mContext, MovieDetailsActivity::class.java)
            intent.putExtra("id", id)
            mContext.startActivity(intent)
        }
    }

    override fun getCount(): Int {
        return sliders.size
    }

    class ViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.image_sliderItem)

    }
}