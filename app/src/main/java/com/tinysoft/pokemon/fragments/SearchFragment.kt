package com.tinysoft.pokemon.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialSharedAxis
import com.tinysoft.pokemon.R
import com.tinysoft.pokemon.adapters.ItemListAdapter
import com.tinysoft.pokemon.databinding.FragmentSearchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : AbsMainFragment(R.layout.fragment_search), TextWatcher {

    companion object {
        private const val TAG = "SearchFragment"
        const val QUERY = "query"
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: ItemListAdapter
    private var query: String? = null

    private val viewModel by viewModel<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.title = null

        viewModel.clearSearchResult()
        setupRecyclerView()
        setupToolbar()

        binding.clearText.setOnClickListener { binding.searchView.text = null }
        binding.searchView.apply {
            addTextChangedListener(this@SearchFragment)
            focusAndShowKeyboard()
        }
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(QUERY)
            binding.searchView.setText(query)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) {
            Log.d(TAG, "search result count = ${it.size}")
            searchAdapter.swapDataSet(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        mainActivity.setBottomBarVisibility(false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(QUERY, binding.searchView.text.toString())
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = ItemListAdapter(requireActivity(), emptyList(), R.layout.item_list)
        searchAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                binding.empty.isVisible = searchAdapter.itemCount < 1
                val height = dipToPix(52)
                binding.recyclerView.setPadding(0, 0, 0, height)
            }
        })
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
    }

    private fun search(query: String) {
        Log.e(TAG, "search: $query")
        this.query = query
        TransitionManager.beginDelayedTransition(binding.appBarLayout)
        binding.clearText.isVisible = query.isNotEmpty()
        viewModel.search(query)
    }

    override fun afterTextChanged(newText: Editable?) {
        if (!newText.isNullOrEmpty()) {
            search(newText.toString())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}

fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

fun Fragment.dipToPix(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}