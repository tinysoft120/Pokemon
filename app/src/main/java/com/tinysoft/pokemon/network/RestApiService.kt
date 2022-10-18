package com.tinysoft.pokemon.network

import com.tinysoft.pokemon.network.models.PokemonDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RestApiService {
    @GET
    suspend fun getPokemonInfo(@Url url: String): Response<PokemonDetails>
}