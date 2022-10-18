package com.tinysoft.pokemon

import androidx.room.Room
import com.tinysoft.pokemon.activities.MainViewModel
import com.tinysoft.pokemon.db.Pokemon
import com.tinysoft.pokemon.db.PokemonDatabase
import com.tinysoft.pokemon.fragments.PokemonDetailsViewModel
import com.tinysoft.pokemon.fragments.SearchViewModel
import com.tinysoft.pokemon.network.provideOkHttp
import com.tinysoft.pokemon.network.providePokemonApiService
import com.tinysoft.pokemon.repository.RepositoryImpl
import com.tinysoft.pokemon.repository.Repository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    factory {
        provideOkHttp(get())
    }
    single {
        providePokemonApiService(get())
    }
}

private var dataModule = module {
    factory {
        get<PokemonDatabase>().pokemonDao()
    }

    single {
        Room.databaseBuilder(androidContext(), PokemonDatabase::class.java, "pokemon.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        RepositoryImpl(get(), get())
    } bind Repository::class
}

private var viewModules = module {
    viewModel {
        MainViewModel(get())
    }

    viewModel { (pokemon: Pokemon) ->
        PokemonDetailsViewModel(pokemon, get())
    }

    viewModel {
        SearchViewModel(get())
    }
}

val appModules = listOf(networkModule, dataModule, viewModules)