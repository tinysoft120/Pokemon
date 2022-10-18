package com.tinysoft.pokemon.fragments

import androidx.lifecycle.*
import com.tinysoft.pokemon.db.Pokemon
import com.tinysoft.pokemon.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: Repository) : ViewModel() {
    private var searchJob: Job? = null
    private val _searchResults = MutableLiveData<List<Pokemon>>()
    val searchResults: LiveData<List<Pokemon>> = _searchResults

    fun clearSearchResult() {
        searchJob?.cancel()
        viewModelScope.launch {
            _searchResults.postValue(emptyList())
        }
    }

    fun search(query: String?) {
        searchJob?.cancel() // cancel previous search job
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(300)
            if (query.isNullOrEmpty()) {
                _searchResults.postValue(emptyList())
                return@launch
            }

            val result = repository.getPokemonListBySearch(query)
            _searchResults.postValue(result)
        }
    }
}