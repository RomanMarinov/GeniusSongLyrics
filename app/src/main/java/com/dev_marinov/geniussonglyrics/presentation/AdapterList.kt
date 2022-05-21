package com.dev_marinov.geniussonglyrics.presentation

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.geniussonglyrics.data.ObjectList
import com.dev_marinov.geniussonglyrics.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class AdapterList (val context: Context) : RecyclerView.Adapter<AdapterList.ViewHolder>() {

    var hashMap: HashMap<Int, ObjectList> = HashMap()

    lateinit var myListenerPageSongPageSong: onItemClickListenerPageSong // клик для pageSong
    lateinit var myListenerPageArtist: onItemClickListenerPageArtist // клик для pageArtist

    interface onItemClickListenerPageSong {
        fun onItemClickPageSong(position: Int)
    }
    fun setOnItemClickListenerPageSong(listenerPageSong: onItemClickListenerPageSong) {
        myListenerPageSongPageSong = listenerPageSong
    }

    interface onItemClickListenerPageArtist {
        fun onItemClickPageArtist(position: Int)
    }
    fun setOnItemClickListenerPageArtist(listenerPageArtist: onItemClickListenerPageArtist) {
        myListenerPageArtist = listenerPageArtist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_list, parent, false)
        return ViewHolder(view, myListenerPageSongPageSong, myListenerPageArtist)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val objectList = hashMap[position]

        if (objectList != null) {
            holder.cardView.animation = AnimationUtils.loadAnimation(context, R.anim.scale_up_1) // анимация
            holder.img.animation = AnimationUtils.loadAnimation(context, R.anim.scale_up_1) // анимация
            holder.tvTitle.animation = AnimationUtils.loadAnimation(context, R.anim.scale_up_1) // анимация
            holder.tvArtist.animation = AnimationUtils.loadAnimation(context, R.anim.scale_up_1) // анимация

            holder.tvArtist.text = objectList.nameArtist
            holder.tvTitle.text = objectList.title

            Picasso.get().load(objectList.urlPictureSong).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.img) // -----> картинка
        }

    }

    override fun getItemCount(): Int {
        Log.e("333", "-зашел hashMap.size=" + hashMap.size)
        return hashMap.size
    }

    //передаем данные и оповещаем адаптер о необходимости обновления списка
    fun refreshListArtists(hashMap: HashMap<Int, ObjectList>) {
        this.hashMap = hashMap
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View, listenerPageSong: onItemClickListenerPageSong, mListenerPageArtist: onItemClickListenerPageArtist) : RecyclerView.ViewHolder(itemView) {
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val img: ImageView = itemView.findViewById(R.id.img)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val btSong: Button = itemView.findViewById(R.id.btSong)
        val btSinger: Button = itemView.findViewById(R.id.btSinger)

        init {
            btSong.setOnClickListener {
                // обязательно добавить в gradle implementation "androidx.recyclerview:recyclerview:1.2.1"
                listenerPageSong.onItemClickPageSong(bindingAdapterPosition)
            }

            btSinger.setOnClickListener {
                // обязательно добавить в gradle implementation "androidx.recyclerview:recyclerview:1.2.1"
                mListenerPageArtist.onItemClickPageArtist(bindingAdapterPosition)
            }
        }

    }

}