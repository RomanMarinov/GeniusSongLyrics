package com.dev_marinov.geniussonglyrics.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dev_marinov.geniussonglyrics.R

class FragmentList : Fragment() {

    lateinit var viewModelSharedUrl: ViewModelSharedUrl
    lateinit var viewModelListArtist: ViewModelListArtist
    lateinit var viewModelAddLoadData: ViewModelAddLoadData

    lateinit var adapterList: AdapterList
    var z = 20  // переменная для увеличения значения для метода getData
    var recyclerView: RecyclerView? = null
    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
    lateinit var lastVisibleItemPositions: IntArray // массив для помощи в передачи последнего видимомо элемента

    var flagLoading: Boolean = true // булева для работы с offset запросами +20 элементов в recyclerView

    var myViewGroup: ViewGroup? = null
    // layoutInflater класс, используемый для преобразования XML-файла макета в объекты представления динамическим способом
    var myLayoutInflater: LayoutInflater? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.e("333", "-зашел FragmentList=")
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

            myRecyclerLayoutManagerAdapter(view, 1)
        } else {
            view = layoutInflater.inflate(R.layout.fragment_list, myViewGroup, false)

            myRecyclerLayoutManagerAdapter(view, 2)
        }

        if (viewModelListArtist.getHashMapArtists().value!!.size == 0) {
            // говорим viewmodel чтобы запросить данные по сети
                // ПРИШЛОСЬ ПЕРЕДАТЬ КОНТЕКСТ, Я ТАК И НЕ ПОНЯЛ ПОЧЕМУ, В АНАЛОГИЧНОМ ПРОЕКТЕ ВСЕ РАБОТАЛО
            viewModelListArtist.setParams(z, context)

        } else {
            Log.e("333", "FragmentHome arrayList.size()  НЕ ПУСТОЙ=")
        }

        return view // в onCreateView() возвращаем объект View, который является корневым элементом разметки фрагмента.
    }

    // метод для установки recyclerview, GridLayoutManager и AdapterListHome
    fun myRecyclerLayoutManagerAdapter(view: View, column: Int) {

        viewModelListArtist = ViewModelProvider(this).get(ViewModelListArtist::class.java)
        viewModelSharedUrl = ViewModelProvider(requireActivity()).get(ViewModelSharedUrl::class.java)
        viewModelAddLoadData = ViewModelProvider(requireActivity()).get(ViewModelAddLoadData::class.java)

        recyclerView = view.findViewById(R.id.recyclerView)
        // setHasFixedSize(true), то подразумеваете, что размеры самого RecyclerView будет оставаться неизменными.
        // Если вы используете setHasFixedSize(false), то при каждом добавлении/удалении элементов RecyclerView
        // будет перепроверять свои размеры
        recyclerView?.setHasFixedSize(false)

        // staggeredGridLayoutManager - шахматный порядок
        staggeredGridLayoutManager = StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL)
        recyclerView?.setLayoutManager(staggeredGridLayoutManager)

        adapterList = AdapterList(this.requireActivity())
        recyclerView?.adapter = adapterList


        adapterList.setOnItemClickListenerPageSong(object : AdapterList.onItemClickListenerPageSong {
            override fun onItemClickPageSong(position: Int) {
                getClickPositionPageSong(position)

            }
        })

        adapterList.setOnItemClickListenerPageArtist(object : AdapterList.onItemClickListenerPageArtist{
            override fun onItemClickPageArtist(position: Int) {
                getClickPositionPageArtist(position)

            }
        })

        // наблюдатель об изменениях в массиве, чтобы передать его в адаптер и обновить его
        viewModelListArtist.getHashMapArtists().observe(requireActivity(), androidx.lifecycle.Observer {
            it.let { adapterList.refreshListArtists(it) } // it - обновленный список
        })

            // этот интерфейс сработает тогда когда заполниться hashmap
            // тут обновиться адаптер и измениться flagLoading
        (requireActivity() as MainActivity).setInterFaceAdapter(object : MainActivity.MyInterFaceAdapter{
            override fun methodMyInterFaceAdapter() {
                Log.e("333", "-зашел setInterFaceAdapter(object : MainActivity-")
                adapterList.notifyDataSetChanged()
                flagLoading = false
            }
        })

        // слушатель RecyclerView для получения последнего видимомого элемента, чтобы использовать при повороте
        val mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // эта часть отвечает за срабатывание запроса на получение дополнительных данных для записи в hashMap
                // totalCountItem переменная всегода равно размеру hashmap в который добавляется + 20
                viewModelAddLoadData.totalCountItem = staggeredGridLayoutManager?.itemCount!!

                // эта часть отвечет только за передачу последнего видимомо элемента
                lastVisibleItemPositions = staggeredGridLayoutManager?.findFirstVisibleItemPositions(null)!!
                //Log.e("zzz","-lastVisibleItemPositions=" + lastVisibleItemPositions.length);
                viewModelAddLoadData.lastVisibleItem = getMaxPosition(lastVisibleItemPositions)

                // эта часть должна срабатывать при достижении прокрутки
                // totalCountItem - общее, lastVisibleItem - последний видимый
                if (flagLoading == false && (viewModelAddLoadData.totalCountItem - 5) == viewModelAddLoadData.lastVisibleItem)
                {
                    // тут я запускаю новый запрос даных на сервер с offset
                    val runnable = Runnable {
                        Log.e("333", "-зашел offset-")
                        z = z + 20 // переменная для увеличения значения offset

                        viewModelListArtist.setParams(z, context)
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

    }


    fun getClickPositionPageSong(position: Int) {

        val urlPageSong = viewModelListArtist.getHashMapArtists().value!![position]!!.urlPageSong

        val fragmentWebView = FragmentWebView()
        viewModelSharedUrl.sendMessageUrl(urlPageSong)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.animator.card_flip_right_enter,
                R.animator.card_flip_right_exit,
                R.animator.card_flip_left_enter,
                R.animator.card_flip_left_exit
            )
            .replace(R.id.llFragWebView, fragmentWebView, "llFragWebView")
            .addToBackStack("llFragWebView")
            .commit()

    }
    fun getClickPositionPageArtist(position: Int) {

        val urlPageArtist = viewModelListArtist.getHashMapArtists().value!![position]!!.urlPageArtist

        val fragmentWebView = FragmentWebView()
        viewModelSharedUrl.sendMessageUrl(urlPageArtist)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.animator.card_flip_right_enter,
                R.animator.card_flip_right_exit,
                R.animator.card_flip_left_enter,
                R.animator.card_flip_left_exit
            )
            .replace(R.id.llFragWebView, fragmentWebView, "llFragWebView")
            .addToBackStack("llFragWebView")
            .commit()
    }

}