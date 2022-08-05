package com.dev_marinov.geniussonglyrics.presentation.splash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.*
import com.dev_marinov.geniussonglyrics.CircularRevealTransition
import com.dev_marinov.geniussonglyrics.R
import com.dev_marinov.geniussonglyrics.databinding.FragmentSplashBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    lateinit var rootScene1: ViewGroup
    lateinit var rootScene2: ViewGroup
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        Log.e("333", "создался SplashFragment")

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootContainer = binding.rootContainer

        setUpViews(rootContainer)

        lifecycleScope.launch(Dispatchers.Main) {
            showSceneProgressBar(rootContainer)
            showSceneNextFragment(rootContainer, progressBar)
        }
    }

    private fun setUpViews(rootContainer: RelativeLayout) {
        (layoutInflater.inflate(R.layout.scene_animation_1, rootContainer, false) as? ViewGroup)?.let {
            rootScene1 = it
        }
        (layoutInflater.inflate(R.layout.scene_animation_2, rootContainer, false) as? ViewGroup)?.let {
            rootScene2 = it
        }
        progressBar = rootScene1.findViewById(R.id.progress_bar_scene)
    }

     private suspend fun showSceneProgressBar(rootContainer: RelativeLayout) {
         Log.e("555", "зашел showScene1")
         progressBar.visibility = View.VISIBLE // показываем progress_bar_scene

         val scene = Scene(rootContainer, rootScene1)
         TransitionManager.go(scene, null) // делаем транзакцию в следующую scene_animation_2

         delay(2000)
         progressBar.visibility = View.INVISIBLE
    }

    private suspend fun showSceneNextFragment(rootContainer: RelativeLayout, progressBar: ProgressBar) {
        Log.e("555", "зашел showScene2")

        val scene = Scene(rootContainer, rootScene2)
        val transition = makeCircularTransition(rootContainer)
        TransitionManager.go(scene, transition)

        delay(500)
        findNavController().navigate(R.id.action_splashFragment_to_artistsFragment)
    }

    private fun makeCircularTransition(rootContainer: RelativeLayout): Transition {
        Log.e("555", "зашел getScene2Transition")
        val transitionSet = TransitionSet()

        //ChangeBounds переход, меняет позицию
        val changeBounds = ChangeBounds()
        changeBounds.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                Log.e("555", "зашел getScene2Transition changeTransform.addListener")
                // скрыть progress_bar в конце анимации
                rootContainer.findViewById<View>(R.id.progress_bar_scene).visibility = View.INVISIBLE
            }
        })

        changeBounds.addTarget(R.id.progress_bar_scene)
        changeBounds.duration = 300

        // путь дуги
        val arcMotion = ArcMotion()
        arcMotion.maximumAngle = 45F
        arcMotion.minimumHorizontalAngle = 90F
        arcMotion.minimumVerticalAngle = 0F

        changeBounds.setPathMotion(arcMotion)
        transitionSet.addTransition(changeBounds)

        // прменяем созданный класс анимации CircularRevealTransition
        val circularRevealTransition = CircularRevealTransition()
        circularRevealTransition.addTarget(R.id.cl_scene)
        circularRevealTransition.startDelay = 200
        circularRevealTransition.duration = 600
        transitionSet.addTransition(circularRevealTransition)

        return transitionSet
    }
}