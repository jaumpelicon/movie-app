package com.example.movieapp.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.db.entity.MovieEntity
import com.squareup.picasso.Picasso

class FavoriteMovieAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var movies: List<MovieEntity> = ArrayList()
    var itemClick: ((MovieEntity) -> Unit)? = null
    var itemClickRemove: ((MovieEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_favorites, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieViewHolder -> {
                holder.bind(movies[position], itemClick, itemClickRemove)
            }
        }
    }

    fun updateDataMovie(moviesList: List<MovieEntity>) {
        this.movies = moviesList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = movies.size

    class MovieViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val movieName: TextView = itemView.findViewById(R.id.movieTitleText)
        private val moviePoster: ImageView = itemView.findViewById(R.id.moviePosterImage)
        private val trash: ImageView = itemView.findViewById(R.id.removeMovie)

        fun bind(
            movieList: MovieEntity,
            listener: ((MovieEntity) -> Unit)?,
            listenerRemove: ((MovieEntity) -> Unit)?
        ) {

            trash.setOnClickListener {
                listenerRemove?.invoke(movieList)
            }

            itemView.setOnClickListener {
                listener?.invoke(movieList)
            }

            movieName.text = movieList.title
            movieList.posterPath.let {
                when {
                    it != null -> {
                        Picasso.get()
                            .load("https://image.tmdb.org/t/p/w500/${movieList.posterPath}")
                            .into(moviePoster)
                    }
                    else -> {
                        moviePoster.setImageResource(R.drawable.ic_baseline_image_not_supported_24)
                    }
                }
            }
        }
    }
}