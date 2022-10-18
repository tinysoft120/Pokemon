package com.tinysoft.pokemon.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialFadeThrough
import com.tinysoft.pokemon.GlideApp
import com.tinysoft.pokemon.R
import com.tinysoft.pokemon.databinding.FragmentPokemonDetailsBinding
import com.tinysoft.pokemon.db.Pokemon
import com.tinysoft.pokemon.dialog.CustomProgressDialog
import com.tinysoft.pokemon.network.ResultWrapper
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PokemonDetailsFragment : AbsMainFragment(R.layout.fragment_pokemon_details) {

    companion object {
        private const val TAG = "PokemonDetailsFragment"
    }

    private var _binding: FragmentPokemonDetailsBinding? = null
    private val binding get() = _binding!!

    private val arguments by navArgs<PokemonDetailsFragmentArgs>()
    private val viewModel by viewModel<PokemonDetailsViewModel> {
        parametersOf(arguments.extraPokemon)
    }
    private lateinit var pokemon: Pokemon
    private val progressDialog: CustomProgressDialog by lazy { CustomProgressDialog() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        _binding = FragmentPokemonDetailsBinding.bind(view)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.title = null
        updateUI(arguments.extraPokemon)

        setupToolbar()

        viewModel.pokemon.observe(viewLifecycleOwner) { result ->
            when (result) {
                ResultWrapper.Loading -> showLoading("Loading...",true)
                is ResultWrapper.Success -> {
                    updateUI(result.value)
                    dismissLoading()
                }
                is ResultWrapper.GenericError -> {
                    Log.e(TAG, "error: ${result.message}" )
                    dismissLoading()
                }
                ResultWrapper.NetworkError -> {
                    Log.e(TAG, "network error" )
                    dismissLoading()
                }
            }
        }
        ViewCompat.setTransitionName(binding.coverImage, pokemon.id.toString())

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        viewModel.fetchDetail()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.titleText.text = pokemon.name
    }

    private fun updateUI(pokemon: Pokemon) {
        this.pokemon = pokemon
        GlideApp.with(requireContext()).asDrawable()
            .load(pokemon.image)
            .into(binding.coverImage)
        binding.pokemonIDText.text = String.format("Pokemon ID: %d", pokemon.id)
        binding.heightText.text = String.format("Height: %.1f", pokemon.height)
        binding.weightText.text = String.format("Weight: %.1f", pokemon.weight)
        binding.type1Text.text = String.format("Type1: %s", pokemon.type1)
        binding.type2Text.text = String.format("Type2: %s", pokemon.type2)
        binding.attackText.text = String.format("Attack: %d", pokemon.attack)
        binding.defenseText.text = String.format("Defense: %d", pokemon.defense)
        binding.spAttackText.text = String.format("Special Attack: %d", pokemon.spAttack)
        binding.spDefenseText.text = String.format("Special Defense: %d", pokemon.spDefense)
        binding.speedText.text = String.format("Speed: %d", pokemon.speed)
    }

    private fun showLoading(desc: String? = null, force: Boolean = false) {
        if (force || !progressDialog.isShowing) {
            progressDialog.show(requireActivity(), desc)
        }
    }

    private fun dismissLoading() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

}