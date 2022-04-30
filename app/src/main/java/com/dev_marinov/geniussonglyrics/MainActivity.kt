package com.dev_marinov.geniussonglyrics

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.transition.*
import com.google.android.material.circularreveal.CircularRevealCompat
import com.google.android.material.circularreveal.CircularRevealFrameLayout
import com.google.android.material.circularreveal.CircularRevealWidget
import java.sql.Array
import kotlin.math.max
import android.transition.CircularPropagation as TransitionCircularPropagation
import androidx.transition.TransitionPropagation as TransitionPropagation1
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentManager


class MainActivity : AppCompatActivity() {

    lateinit var hashMap: HashMap<Int, ObjectList>
    lateinit var previous: MutableList<String>
    var flag: Boolean = false
    var lastVisibleItem: Int? = 0

    lateinit var btYes: Button // lateinit отложенная инициализация
    lateinit var btNo: Button // lateinit отложенная инициализация

    lateinit var viewGroup: ViewGroup
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.e("333", "создалась MainActivity")

        handler = Handler(Looper.getMainLooper())
        hashMap = HashMap<Int, ObjectList>()
        previous = ArrayList<String>()

        setWindow() // сетинг для статус бара и для бара навигации
        hideSystemUI() // сетинг для фул скрин по соответствующему сдк
        supportActionBar?.hide() // скрыть экшенбар

        // берем белый frameLayout, который растянут во весь экран и который находиться в activity_main
        viewGroup = findViewById(R.id.fl_viewGroup)

        showScene1(false)

    }



    fun showScene1(bool: Boolean){

        // scene_animation_1 - это белый FrameLayout только с progress_bar_scene
        // Чтобы избежать исключений, оператор безопасного as? приведения , который возвращается nullв случае сбоя.
        val root = layoutInflater.inflate(R.layout.scene_animation_1, viewGroup, false) as? ViewGroup
        val progressBar = root?.findViewById<ProgressBar>(R.id.progress_bar_scene)
        progressBar?.visibility = View.VISIBLE // показываем progress_bar_scene

        // скрываем progress_bar_scene через 2сек
        val runnable = Runnable {
            showScene2()
            progressBar?.visibility = View.INVISIBLE
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 2000)

        // при входе удалит все дочерние элементы из контейнера viewGroup и добавит объект root
        // макета в качестве нового дочернего элемента этого контейнера.
        if(root != null) {
            val scene = Scene(viewGroup, root)
            TransitionManager.go(scene, null) // делаем транзакцию в следующую scene_animation_2
        }
    }

    fun showScene2(){
        val root = layoutInflater.inflate(R.layout.scene_animation_2, viewGroup, false) as? ViewGroup
        if(root != null) {
            val scene = Scene(viewGroup, root)
            val transition = getScene2Transition()
            TransitionManager.go(scene, transition)
        }

        // как только scene_animation_2 закончила показ, через 0.5 сек мы переходим в FragmentMain
        val runnable = Runnable {
            val fragmentList = FragmentList()
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.llFragList, fragmentList)
            fragmentTransaction.commit()
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 500)

    }

    fun getScene2Transition() : Transition {
        val transitionSet: TransitionSet = TransitionSet()

        //ChangeBounds переход, меняет позицию
        val changeBounds: ChangeBounds = ChangeBounds()
        changeBounds.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                Log.e("333", "зашел getScene2Transition changeTransform.addListener")
                // скрыть progress_bar в конце анимации
                viewGroup.findViewById<View>(R.id.progress_bar_scene).visibility = View.INVISIBLE
            }
        })
        changeBounds.addTarget(R.id.progress_bar_scene)
        changeBounds.duration = 300

        // путь дуги
        val arcMotion: ArcMotion = ArcMotion()
        arcMotion.maximumAngle = 45F
        arcMotion.minimumHorizontalAngle = 90F
        arcMotion.minimumVerticalAngle = 0F

        changeBounds.setPathMotion(arcMotion)
        transitionSet.addTransition(changeBounds)

        // прменяем созданный класс анимации CircularRevealTransition
        val circularRevealTransition: CircularRevealTransition = CircularRevealTransition()
        circularRevealTransition.addTarget(R.id.cl_scene)
        circularRevealTransition.setStartDelay(200)
        circularRevealTransition.setDuration(600)
        transitionSet.addTransition(circularRevealTransition)

        return transitionSet
    }

    fun setWindow() {
        val window = window

        // FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS Флаг, указывающий, что это Окно отвечает за отрисовку фона для системных полос.
        // Если установлено, системные панели отображаются с прозрачным фоном, а соответствующие области в этом окне заполняются
        // цветами, указанными в Window#getStatusBarColor()и Window#getNavigationBarColor().
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent); // прозрачный статус бар
        window.navigationBarColor  = ContextCompat.getColor(this, android.R.color.black); // черный бар навигации
    }



    private fun hideSystemUI() {
        // если сдк устройства больше или равно сдк приложения
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else { // иначе
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    //or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    //or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    //or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    //or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }


    override fun onBackPressed() {
        if(flag) {
            val size = previous.size
            if(size > 0) {
                val fragmentWebView: FragmentWebView = supportFragmentManager.findFragmentById(R.id.llFragWebView) as FragmentWebView
                if(fragmentWebView != null) {
                    Log.e("333", " onBackPressed size > 0 и previous[size - 1]=" + (previous[size - 1]))
                    fragmentWebView.webView.loadUrl(previous[size - 1])
                }
                // removeAt Удаляет элемент по указанному индексу из списка. remove - устарело
                Log.e("333", " previous.removeAt до удаления =" + (size - 1))
                previous.removeAt(size - 1)
                Log.e("333", " previous.removeAt после удаления =" + (size - 1))
            }
            else{
                flag = false
                Log.e("333", "super.onBackPressed")
                super.onBackPressed()
            }
        }
        else if(!flag) { //  если flag false значит работаем с backstack fragment
            supportFragmentManager.popBackStack() // удаление фрагментов из транзакции
            myAlertDialog() // метод реализации диалога с пользователем закрыть приложение или нет
        }
    }

    fun myAlertDialog() {
        val dialog: Dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.windows_alertdialog)
        dialog.setCancelable(false)
        dialog.show()

        btNo = dialog.findViewById<Button>(R.id.btNo)
        btYes = dialog.findViewById<Button>(R.id.btYes)

        btNo.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        btYes.setOnClickListener{
            dialog.dismiss()
            finish()
        }


    }
}