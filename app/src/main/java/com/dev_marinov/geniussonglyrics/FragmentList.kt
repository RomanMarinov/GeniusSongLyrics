package com.dev_marinov.geniussonglyrics

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class FragmentList : Fragment() {

    lateinit var adapterList: AdapterList
    var z = 20  // переменная для увеличения значения для метода getData
    var recyclerView: RecyclerView? = null
    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
    lateinit var lastVisibleItemPositions: IntArray // массив для помощи в передачи последнего видимомо элемента

    var totalCountItem: Int = 0
//    var lastVisibleItem: Int = 0


    var flagLoading: Boolean = true

    var myViewGroup: ViewGroup? = null
    // layoutInflater класс, используемый для преобразования XML-файла макета в объекты представления динамическим способом
    var myLayoutInflater: LayoutInflater? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // установить контейнер viewGroup и обработчик инфлятора
        myViewGroup = container
        myLayoutInflater = inflater
        // отображать желаемую разметку и возвращать view в initInterface .
        // onCreateView() возвращает объект View, который является корневым элементом разметки фрагмента.
        return initInterface()

    }

    // https://stackoverflow.com/questions/54266160/changing-a-recyclerviews-layout-upon-orientation-change-only-works-on-the-first
    fun initInterface(): View? { // удалить android:configChanges из манифеста для применения данной стратегии
        val view: View
        // если уже есть надутый макет, удалить его.
        if (myViewGroup != null) {
            myViewGroup!!.removeAllViewsInLayout() // отличается от removeAllView
        }
        // получить экран ориентации
        val orientation = requireActivity().resources.configuration.orientation
        // раздуть соответствующий макет в зависимости от ориентации экрана
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = layoutInflater.inflate(R.layout.fragment_list, myViewGroup, false)

            myRecyclerLayoutManagerAdapter(view, 1, (activity as MainActivity?)?.lastVisibleItem)
        } else {
            view = layoutInflater.inflate(R.layout.fragment_list, myViewGroup, false)

            myRecyclerLayoutManagerAdapter(view, 2, (activity as MainActivity?)?.lastVisibleItem)
        }
        if ((activity as MainActivity?)?.hashMap?.size == 0) {
            Log.e("444", "arrayList.size()=" + (activity as MainActivity?)?.hashMap?.size)
            getData(z)
        } else {
            Log.e("444", "FragmentHome arrayList.size()  НЕ ПУСТОЙ=")
        }

        return view // в onCreateView() возвращаем объект View, который является корневым элементом разметки фрагмента.
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.e("444", "-зашел FragmentHome onConfigurationChanged-")
        // ДО СОЗДАНИЯ НОВОГО МАКЕТА ПИШЕМ ПЕРЕМЕННЫЕ В КОТОРЫЕ СОХРАНЯЕМ ЛЮБЫЕ ДАННЫЕ ИЗ ТЕКУЩИХ VIEW
//        // создать новый макет------------------------------
        val view: View = initInterface()!!
        // ПОСЛЕ СОЗДАНИЯ НОВОГО МАКЕТА ПЕРЕДАЕМ СОХРАНЕННЫЕ ДАННЫЕ В СТАРЫЕ(ТЕ КОТОРЫЕ ТЕКУЩИЕ) VIEW
        // отображать новую раскладку на экране
        myViewGroup?.addView(view)
        super.onConfigurationChanged(newConfig)
    }

    // метод для установки recyclerview, GridLayoutManager и AdapterListHome
    fun myRecyclerLayoutManagerAdapter(view: View, column: Int, lastVisableItem: Int?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        // setHasFixedSize(true), то подразумеваете, что размеры самого RecyclerView будет оставаться неизменными.
        // Если вы используете setHasFixedSize(false), то при каждом добавлении/удалении элементов RecyclerView
        // будет перепроверять свои размеры
        recyclerView?.setHasFixedSize(false)

        // staggeredGridLayoutManager - шахматный порядок
        staggeredGridLayoutManager = StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL)
        recyclerView?.setLayoutManager(staggeredGridLayoutManager)

        adapterList = AdapterList(this.requireActivity(), (activity as MainActivity?)!!.hashMap, recyclerView!!)
        recyclerView?.adapter = adapterList

        // слушатель RecyclerView для получения последнего видимомого элемента, чтобы использовать при повороте
        val mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // эта часть отвечает за срабатывание запроса на получение дополнительных данных для записи в hashMap
                // totalCountItem переменная всегода равно размеру hashmap в который добавляется + 20
                totalCountItem = staggeredGridLayoutManager?.itemCount!!

                // эта часть отвечет только за передачу последнего видимомо элемента
                lastVisibleItemPositions = staggeredGridLayoutManager?.findLastVisibleItemPositions(null)!!
                //Log.e("zzz","-lastVisibleItemPositions=" + lastVisibleItemPositions.length);
                (context as MainActivity).lastVisibleItem = getMaxPosition(lastVisibleItemPositions)

                Log.e("444", "-проверка totalCountItem-" + totalCountItem +
                        "-(context as MainActivity).lastVisibleItem-" + (context as MainActivity).lastVisibleItem)

                // эта часть должна срабатывать при достижении прокрутки
                // totalCountItem - общее, lastVisibleItem - последний видимый
                if (flagLoading == false && (totalCountItem - 5) == (context as MainActivity).lastVisibleItem)
                {
                    // тут я запускаю новый запрос даных на сервер с offset
                    val runnable = Runnable {
                        Log.e("444", "-зашел offset-")
                        z = z + 20 // переменная для увеличения значения offset
                        getData(z) /// + 20;
                    }
                    Handler(Looper.getMainLooper()).postDelayed(runnable, 100)

                    flagLoading = true // и возвращаю flagLoading в исходное состояние
                }


            }
            fun getMaxPosition(positions: IntArray): Int {
                return positions[0]
            }





        }
        recyclerView?.addOnScrollListener(mScrollListener)

        val runnable = Runnable { // установка последнего элемента в главном потоке
            try {
                requireActivity().runOnUiThread {
                    staggeredGridLayoutManager!!.scrollToPositionWithOffset(lastVisableItem!!, 0)
                }
            } catch (e: Exception) {
                Log.e("444", "-try catch FragmentHome 1-$e")
            }

        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 500)
    }

    fun getData(num: Int) { // метод получения данных из сети
        Log.e("444", "-зашел getData-")
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

                            //Log.e("Name", "artist" + artist);
                            (activity as MainActivity?)!!.hashMap[n] =
                                ObjectList(newArtist2, urlPictureSong, title, urlPageSong, urlPageArtist)
                        }

                        activity?.runOnUiThread {
                            adapterList.notifyDataSetChanged()
                            //adapterList.setLoading() // присваиваем flagLoading = false в методе setLoading()
                            flagLoading = false // это значит что новые данные записались в hashMap

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