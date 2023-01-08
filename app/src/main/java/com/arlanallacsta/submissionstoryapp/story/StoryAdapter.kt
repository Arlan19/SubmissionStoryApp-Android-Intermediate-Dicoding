package com.arlanallacsta.submissionstoryapp.story

import android.annotation.SuppressLint
import androidx.core.util.Pair
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.arlanallacsta.submissionstoryapp.databinding.ItemStoryUserBinding
import com.arlanallacsta.submissionstoryapp.main.ListStory
import com.bumptech.glide.Glide

class StoryAdapter(private val context: Context, private val clickListener: OnItemClickCallback) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private val listStory = ArrayList<ListStory>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<ListStory>){
        listStory.clear()
        listStory.addAll(list)
        notifyDataSetChanged()
    }

    inner class StoryViewHolder(private val binding: ItemStoryUserBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(listStory: ListStory){
            with(binding){
                tvNamaPengguna.text = listStory.name
                tvDescription.text = listStory.description
                Glide.with(context).load(listStory.photoUrl).circleCrop().into(imgGambarUser)
                root.setOnClickListener{
                    val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as Activity,
                        Pair(binding.imgGambarUser, "profile"),
                        Pair(binding.tvNamaPengguna, "name"),
                        Pair(binding.tvDescription, "description")
                    )
                    clickListener.onItemClicked(listStory, optionsCompat)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StoryViewHolder(
        ItemStoryUserBinding.inflate(LayoutInflater.from(context), parent, false)
    )

    override fun onBindViewHolder(holder: StoryAdapter.StoryViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size

    interface OnItemClickCallback {
        fun onItemClicked(listStory: ListStory, optionsCompat: ActivityOptionsCompat)
    }
}