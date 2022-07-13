package com.aasoftware.redplate.ui.mainUI.onboarding.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aasoftware.redplate.R
import com.aasoftware.redplate.databinding.ItemPostBinding
import com.aasoftware.redplate.domain.Post

class HomePostsAdapter(): ListAdapter<Post, HomePostsAdapter.HomePostViewHolder>(DiffCallback){

    companion object DiffCallback: DiffUtil.ItemCallback<Post>(){
        // As TreasureEntity is a data class, kotlin's triple equal sign checks if all fields are the same
        override fun areItemsTheSame(oldItem:Post, newItem: Post): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id
    }

    class HomePostViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(post: Post){
            // TODO: Handle post and profile picture loading cases. Handle empty description case.
            binding.post = post
            binding.profileImage.setImageResource(
                when(post.user.username){
                "McQueen" -> R.drawable.rayito
                    else -> R.drawable.ic_person_24
                }
            )
            binding.postImage.setImageResource(
                when(post.user.username){
                    "McQueen" -> R.drawable.rayito
                    else -> R.drawable.background_image
                }
            )

            if (post.description.isNullOrEmpty()){
                binding.description.visibility = View.GONE
            } else {
                binding.description.text = post.description
            }

            val elapsedTime: Long = System.currentTimeMillis() - post.postTimeMillis

            // TODO: Create extension function, create yearMillis, monthMillis... variables, create system translatable strings
            binding.timestampTextview.text =
                if(elapsedTime > 31601664000) "${elapsedTime / 31601664000} years ago"
                else if (elapsedTime > 2633472000) "${elapsedTime / 2633472000} months ago"
                else if (elapsedTime > 86400000) "${elapsedTime / 86400000} days ago"
                else if (elapsedTime > 3600000) "${elapsedTime / 3600000} hours ago"
                else if (elapsedTime > 60000) "${elapsedTime / 60000} minutes ago"
                else "Now"
            binding.likes.text =
                if(post.likes > 1000000) "${(post.likes / 100000).toFloat() / 10}M likes"
                else if (post.likes > 1000) "${(post.likes / 100).toFloat() / 10}k likes"
                else "${post.likes} likes"

            if (post.liked){
                binding.likeButton.setImageResource(R.drawable.heart_filled)
            }

            // perform data binding: binding.post = post
            // This method updates all the data
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostViewHolder {
        return HomePostViewHolder(
            ItemPostBinding.inflate(
            LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: HomePostViewHolder, position: Int) {
        if(!currentList.isNullOrEmpty()){
            //holder.itemView.setOnClickListener {onItemClick(currentList[position])}
            holder.bind(currentList[position])
        }
    }
}