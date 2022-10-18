package com.tinysoft.pokemon.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.tinysoft.pokemon.R
import com.tinysoft.pokemon.adapters.ItemListAdapter
import com.tinysoft.pokemon.databinding.FragmentPokemonListBinding
import com.tinysoft.pokemon.helper.SortOrder
import com.tinysoft.pokemon.utils.PreferenceUtil
import kotlinx.coroutines.launch

class PokemonListFragment: AbsMainFragment(R.layout.fragment_pokemon_list) {
    companion object {
        private const val TAG = "PokemonListFragment"
    }

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding!!

    private lateinit var listAdapter: ItemListAdapter
    private lateinit var listLayoutManager: LinearLayoutManager
    private var sortOrder: SortOrder? = null
    private var gridSize: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPokemonListBinding.bind(view)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.title = null

        setHasOptionsMenu(true)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        initLayoutManager()
        initAdapter()
        setupRecyclerView()
        setupToolbar()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.sortedPokemonListFlow.collect {
                    Log.e(TAG, "update local Pokemon List Num = ${it.size}")
                    listAdapter.swapDataSet(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        setUpSortOrderMenu(menu.findItem(R.id.action_sort_order).subMenu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (handleSortOrderMenuItem(item)) {
            return true
        }
        if (handleGridMenuItem(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpSortOrderMenu(subMenu: SubMenu) {
        val order: SortOrder = getSortOrder()
        subMenu.clear()
        for (item in SortOrder.values()) {
            createId(subMenu, item.resId, item.titleRes, item == order)
        }
        subMenu.setGroupCheckable(0, true, true)
    }

    private fun getSortOrder(): SortOrder {
        if (sortOrder == null) {
            sortOrder = PreferenceUtil.listSortOrder
        }
        return sortOrder!!
    }

    private fun handleSortOrderMenuItem(item: MenuItem): Boolean {
        val sortOrder: SortOrder = SortOrder.values()
            .firstOrNull { it.resId == item.itemId } ?: PreferenceUtil.listSortOrder
        if (sortOrder != PreferenceUtil.listSortOrder) {
            item.isChecked = true
            setAndSaveSortOrder(sortOrder)
            return true
        }
        return false
    }

    private fun createId(menu: SubMenu, id: Int, title: Int, checked: Boolean) {
        menu.add(0, id, 0, title).isChecked = checked
    }

    private fun setAndSaveSortOrder(sort: SortOrder) {
        PreferenceUtil.listSortOrder = sort
        this.sortOrder = sort
        mainViewModel.forceReload()
    }

    private fun setAndSaveGridSize(gridSize: Int) {
        val oldLayoutRes = itemLayoutRes()
        this.gridSize = gridSize
        PreferenceUtil.listGridSize = gridSize

        binding.recyclerView.isVisible = false
        invalidateLayoutManager()

        // only recreate the adapter and layout manager if the layout currentLayoutRes has changed
        if (oldLayoutRes != itemLayoutRes()) {
            invalidateAdapter()
        } else {
            setGridSize(gridSize)
        }
        val transition = MaterialFade().apply {
            addTarget(binding.recyclerView)
        }
        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.recyclerView.isVisible = true
    }

    private fun getGridSize(): Int {
        if (gridSize == 0) {
            gridSize = PreferenceUtil.listGridSize
        }
        return gridSize
    }

    private fun setGridSize(gridSize: Int) {
        (listLayoutManager as? GridLayoutManager)?.spanCount = gridSize
        listAdapter.notifyDataSetChanged()
    }

    private fun itemLayoutRes(): Int {
        return if (getGridSize() > 1) {
            R.layout.item_poke_card
        } else R.layout.item_list
    }

    private fun handleGridMenuItem(item: MenuItem):Boolean {
        if (item.itemId == R.id.action_grid_size) {
            val minSize = resources.getInteger(R.integer.default_grid_columns)
            val maxSize = resources.getInteger(R.integer.second_grid_columns)
            val gridSize = when (getGridSize()) {
                minSize -> maxSize
                maxSize -> minSize
                else -> minSize
            }
            setAndSaveGridSize(gridSize)
            return true
        }
        return false
    }

    private fun invalidateLayoutManager() {
        initLayoutManager()
        binding.recyclerView.layoutManager = listLayoutManager
    }

    private fun invalidateAdapter() {
        initAdapter()
        binding.recyclerView.adapter = listAdapter
    }

    private fun initLayoutManager() {
        val gridSize = getGridSize()
        listLayoutManager = if (gridSize > 1) {
            GridLayoutManager(requireContext(), gridSize)
        } else {
            LinearLayoutManager(requireContext())
        }
    }

    private fun initAdapter() {
        val dataSet = if (::listAdapter.isInitialized) listAdapter.dataSet else emptyList()

        listAdapter = ItemListAdapter(requireActivity(), dataSet, itemLayoutRes())
        listAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                val height = dipToPix(56)
                Log.e(TAG, "onChanged: 56dp = $height px")
                binding.recyclerView.setPadding(0, 0, 0, height)
            }
        })
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).addTarget(binding.root)
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            findNavController().navigate(R.id.searchFragment, null, navOptions)
        }
        binding.appNameText.text = getString(R.string.app_name)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = listLayoutManager
            adapter = listAdapter
        }
    }
}