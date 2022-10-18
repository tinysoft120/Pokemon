package com.tinysoft.pokemon.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinysoft.pokemon.db.Pokemon
import com.tinysoft.pokemon.network.ResultWrapper
import com.tinysoft.pokemon.repository.Repository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class PokemonDetailsViewModel(private val cachePokemon: Pokemon,
                              private val repository: Repository) : ViewModel() {

    private val _pokemon: MutableLiveData<ResultWrapper<Pokemon>> = MutableLiveData<ResultWrapper<Pokemon>>()
    val pokemon: LiveData<ResultWrapper<Pokemon>> = _pokemon

    fun fetchDetail() = viewModelScope.launch(IO) {
        _pokemon.postValue(ResultWrapper.Loading)

        when (val response = repository.getPokemonInfo(cachePokemon.id)) {
            is ResultWrapper.Success -> {
                repository.insertPokemonDetails(response.value)
                val updated = repository.getCachePokemon(cachePokemon.id)
                updated?.let { _pokemon.postValue(ResultWrapper.Success(it)) }
            }
            is ResultWrapper.GenericError -> {
                val error = ResultWrapper.GenericError(response.code, response.message)
                _pokemon.postValue(error)
            }
            ResultWrapper.NetworkError -> _pokemon.postValue(ResultWrapper.NetworkError)
            else -> Log.d(TAG, "ignore loading event")
        }
    }

    companion object {
        private const val TAG = "PokemonDetailsViewModel"
    }
}