package com.dev_marinov.geniussonglyrics.presentation.artists

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.geniussonglyrics.R
import com.dev_marinov.geniussonglyrics.databinding.FragmentArtistsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtistsFragment : Fragment() {
    private lateinit var binding: FragmentArtistsBinding
    private val viewModel by viewModels<ArtistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        Log.e("333", "создалась ArtistsFragment")
        return initInterFace(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
    }

    private fun initInterFace(inflater: LayoutInflater, container: ViewGroup?): View {
        container?.let { container.removeAllViewsInLayout() }

        val orientation = requireActivity().resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artists, container, false)
            setRecyclerView(1)
        } else {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artists, container, false)
            setRecyclerView(2)
        }
        return binding.root
    }

    private fun setUpNavigation(){
        viewModel.uploadData.observe(viewLifecycleOwner) {
            navigateToWebViewFragment(it)
        }
    }

    private fun setRecyclerView(column: Int) {
        val artistsAdapter = ArtistsAdapter(viewModel::onClick)
        val gridLayoutManager = GridLayoutManager(context, column)

        binding.tvTitleList.setOnClickListener {
            Log.e("333","click=")
            binding.img.setBackgroundResource(R.drawable.ic_favorite_on)
        }

        binding.recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = gridLayoutManager
            adapter = artistsAdapter
        }

        viewModel.artist.observe(viewLifecycleOwner) {
            artistsAdapter.submitList(it)
        }

        viewModel.flagLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }

        val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.onScroll(gridLayoutManager.itemCount, gridLayoutManager.findLastVisibleItemPosition() )
            }
        }
        binding.recyclerView.addOnScrollListener(scrollListener)
    }

    private fun navigateToWebViewFragment(url: String) {
        val action = ArtistsFragmentDirections.actionArtistsFragmentToWebViewFragment(url)
        findNavController().navigate(action)
    }

}