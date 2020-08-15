package com.service.codingtest.view.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.service.codingtest.R
import com.service.codingtest.databinding.FragImageBinding
import com.service.codingtest.network.Constant
import com.service.codingtest.view.adapters.ImageAdapter
import com.service.codingtest.view.adapters.ImageLoadStateAdapter
import com.service.codingtest.viewmodel.ImageListViewModel
import kotlinx.android.synthetic.main.frag_image.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter


class ImageFragment : Fragment() {

    private lateinit var binding: FragImageBinding

    private lateinit var adapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.frag_image, container, false)
        binding = FragImageBinding.bind(view)
        binding.vm = ViewModelProvider(this, defaultViewModelProviderFactory)
            .get(ImageListViewModel::class.java)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initImageListView()
        initSwipeToRefresh()
        initSearchEditText()
    }

    private fun initImageListView() {
        adapter = ImageAdapter()
        rv_image.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ImageLoadStateAdapter(adapter),
            footer = ImageLoadStateAdapter(adapter)
        )

        binding.vm!!.apply {
            filterList.clear()
            lifecycleScope.launchWhenCreated {
                @OptIn(ExperimentalCoroutinesApi::class)
                posts.collectLatest {
                    adapter.submitData(it)
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            @OptIn(ExperimentalCoroutinesApi::class)
            adapter.loadStateFlow.collectLatest { loadStates ->
                layout_swipe_refresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            @OptIn(FlowPreview::class)
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { rv_image.scrollToPosition(0) }
        }
    }

    private fun initSwipeToRefresh() {
        layout_swipe_refresh.setOnRefreshListener { adapter.refresh() }
    }

    private fun initSearchEditText() =
        et_search.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.vm!!.showSubreddit()
                return@OnKeyListener true
            }
            false
        })


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.fragment_menu_list, menu)

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            else -> false
        }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menu.add(Constant.MENU_ALL)

            for (item in binding.vm!!.filterList)
                menu.add(item)

            setOnMenuItemClickListener {
                binding.vm!!.filterSelected = it.title.toString()
                binding.vm!!.showSubreddit()
                true
            }
            show()
        }
    }
}