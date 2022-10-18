package com.tinysoft.pokemon.network.models

data class PokemonDetails(
    val name: String,
    val id: Int,
    val types: List<Types>,
    val moves: List<Move>,
    val sprites: Sprites,
    val height: Double,
    val weight: Double,
    val stats: List<Stats>
)
