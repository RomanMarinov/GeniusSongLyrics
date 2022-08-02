package com.dev_marinov.geniussonglyrics.presentation.artists

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.geniussonglyrics.R

import com.dev_marinov.geniussonglyrics.databinding.ItemArtistBinding
import com.dev_marinov.geniussonglyrics.domain.Song
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class ArtistsAdapter(
    private val onClick: (url: String) -> Unit
) : ListAdapter<Song, ArtistsAdapter.ViewHolder>(ArtistsDiffUtilCallBack()) {

    private var songs: List<Song> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemArtistBinding.inflate(inflater, parent, false)
        return ViewHolder(itemBinding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        songs[position].let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun submitList(list: List<Song>?) {
        super.submitList(list)
        list?.let { this.songs = it.toList() }
    }

    inner class ViewHolder( // inner вложенный класс, который может обращаться к компонентам внешнего класса
        private val binding: ItemArtistBinding,
        private val onClick: (url: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.itemArtists = song

            Picasso.get().load(song.image).memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(binding.img) // -----> картинка

            setAnimation()

            binding.btSong.setOnClickListener {
                onClick(song.urlPageSong)
            }
            binding.btSinger.setOnClickListener {
                onClick(song.urlPageArtist.url)
            }
        }

        private fun setAnimation() {
            binding.cardView.animation =
                AnimationUtils.loadAnimation(binding.root.context, R.anim.scale_up_1) // анимация
            binding.img.animation =
                AnimationUtils.loadAnimation(binding.root.context, R.anim.scale_up_1) // анимация
            binding.tvTitle.animation =
                AnimationUtils.loadAnimation(binding.root.context, R.anim.scale_up_1) // анимация
            binding.tvArtist.animation =
                AnimationUtils.loadAnimation(binding.root.context, R.anim.scale_up_1) // анимация

        }

    }

    class ArtistsDiffUtilCallBack : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

    }
}


