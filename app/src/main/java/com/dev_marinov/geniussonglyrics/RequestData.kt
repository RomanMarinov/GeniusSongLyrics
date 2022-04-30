package com.dev_marinov.geniussonglyrics

import android.content.Context
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class RequestData() {

    fun getData(num: Int, context: Context?) { // метод получения данных из сети
        Log.e("333", "-зашел getData-")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://genius.p.rapidapi.com/artists/16775/songs?per_page=$num")
            .get()
            .addHeader("X-RapidAPI-Host", "genius.p.rapidapi.com")
            .addHeader("X-RapidAPI-Key", "c7ab72f3e6msh7e8e62955218901p1a9717jsnc77db06de783")
            .build()

        try {
            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {}

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    Log.e("response", "response" + response.message)
                    //Log.e("response","body"+response.body().string().toString());
                    val s = response.body?.string()
                    try {
                        val jsonObject = JSONObject(s)
                        val k = jsonObject.getJSONObject("response").getJSONArray("songs").length()

                        for (n in 0 until k) {
                            val artist = jsonObject.getJSONObject("response").getJSONArray("songs")
                                .getJSONObject(n).getString("artist_names")

                            // если приходит в строку artist больше двух слов, то обрезаю с помощью цикла
                            var count = 0
                            var newArtist1 = ""
                            for (i in 0 until artist.length) { // until - это без последнего элемента
                                val ch = artist[i]
                                newArtist1 += ch
                                if (ch == ' ') {
                                    count++
                                    if (count == 2) {
                                        break
                                    }
                                }
                            }
                            val newArtist2 = newArtist1.substring(0, newArtist1.length - 1)
                            val urlPictureSong = jsonObject.getJSONObject("response").getJSONArray("songs")
                                .getJSONObject(n).getString("header_image_thumbnail_url")
                            val title = jsonObject.getJSONObject("response").getJSONArray("songs")
                                .getJSONObject(n).getString("title")
                            val urlPageSong = jsonObject.getJSONObject("response").getJSONArray("songs")
                                .getJSONObject(n).getString("url")
                            val urlPageArtist = jsonObject.getJSONObject("response").getJSONArray("songs")
                                .getJSONObject(n).getJSONObject("primary_artist").getString("url")

                            (context as MainActivity?)?.hashMap?.set(n,
                                ObjectList(newArtist2, urlPictureSong, title, urlPageSong, urlPageArtist))
                        }

                        (context as MainActivity?)?.runOnUiThread {
                            MainActivity.interFaceAdapter.myInterFaceAdapter()
                            Log.e("333", "должен был сработать myInterFaceAdapter()")
                        }

                    } catch (e: Exception) {
                        Log.e("Name", "try catch$e")
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("333", "-try catch-$e")
        }
    }
}