package com.dev_marinov.geniussonglyrics

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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class AdapterList (val context: Context, var hashMap: HashMap<Int, ObjectList> = HashMap<Int, ObjectList>(),)
    : RecyclerView.Adapter<AdapterList.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterList.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterList.ViewHolder, position: Int) {
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

        holder.btSong.setOnClickListener {
            val fragmentWebView = FragmentWebView()
            fragmentWebView.setParam(objectList?.urlPageSong)
            (context as MainActivity).supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.animator.card_flip_right_enter,
                    R.animator.card_flip_right_exit,
                    R.animator.card_flip_left_enter,
                    R.animator.card_flip_left_exit)
            // помещаем в контейнер R.id.clActMain фрагмент new FragmentSearch()
            // т.е. из контейнера удалится его текущий фрагмент (если он там есть) и добавится новый фрагмент
                .replace(R.id.llFragWebView, fragmentWebView, "llFragWebView")
                .addToBackStack("llFragWebView")
                .commit()
        }

        holder.btSinger.setOnClickListener {
            val fragmentWebView = FragmentWebView()
            fragmentWebView.setParam(objectList?.urlPageArtist)
            (context as MainActivity).supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.animator.card_flip_right_enter,
                    R.animator.card_flip_right_exit,
                    R.animator.card_flip_left_enter,
                    R.animator.card_flip_left_exit)
                .replace(R.id.llFragWebView, fragmentWebView, "llFragWebView")
                .addToBackStack("llFragWebView")
                .commit()
        }

    }

    override fun getItemCount(): Int {
        Log.e("333", "-зашел hashMap.size=" + hashMap.size)
        return hashMap.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val img: ImageView = itemView.findViewById(R.id.img)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val btSong: Button = itemView.findViewById(R.id.btSong)
        val btSinger: Button = itemView.findViewById(R.id.btSinger)
    }

}