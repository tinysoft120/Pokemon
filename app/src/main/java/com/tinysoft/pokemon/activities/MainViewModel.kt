package com.tinysoft.pokemon.activities

import android.util.Log
import androidx.lifecycle.*
import com.tinysoft.pokemon.Constants
import com.tinysoft.pokemon.db.Pokemon
import com.tinysoft.pokemon.network.ResultWrapper
import com.tinysoft.pokemon.repository.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*

class MainViewModel(private val repository: Repository) : ViewModel() {
    private var fetchJob: Job? = null
    private val _sortedPokemonListFlow = MutableStateFlow<List<Pokemon>>(listOf())
    val sortedPokemonListFlow: Flow<List<Pokemon>> = _sortedPokemonListFlow.asStateFlow()

    fun fetchPokemonContent() = viewModelScope.launch(IO) {
        fetchJob?.cancelAndJoin() // cancel previous
        fetchJob = viewModelScope.launch(IO) {
            try {
                for (i in 1..Constants.POKEMON_LIMIT) {
                    when (val item = repository.getPokemonInfo(i)) {
                        is ResultWrapper.Success -> {
                            repository.insertPokemonDetails(item.value)
                        }
                        is ResultWrapper.GenericError -> {
                            Log.d(TAG, "ignore poke item since of ${item.message}")
                        }
                        ResultWrapper.NetworkError -> {
                            Log.d(TAG, "stop fetching task since of network error")
                            break
                        }
                        else -> Log.d(TAG, "ignore loading event")
                    }
                    forceReload()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching")
                e.printStackTrace()
                if (e is CancellationException) throw e
            }
            forceReload()
        }
    }

    fun forceReload() = viewModelScope.launch(IO) {
        _sortedPokemonListFlow.emit(repository.getCacheSortedPokemon())
    }

    fun stopFetchingContent() {
        fetchJob?.cancel()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}