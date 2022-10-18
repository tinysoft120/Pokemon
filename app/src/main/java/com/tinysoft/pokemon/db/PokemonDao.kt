package com.tinysoft.pokemon.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: Pokemon)

    @Query("SELECT * FROM pokemon_table")
    fun getAllPokemon(): Flow<List<Pokemon>>

    @Query("SELECT * FROM pokemon_table ORDER BY pokemon_id")
    suspend fun getAllPokemonOrderedByID(): List<Pokemon>

    @Query("SELECT * FROM pokemon_table ORDER BY pokemon_name")
    suspend fun getAllPokemonOrderedByName(): List<Pokemon>

    @Query("SELECT * FROM pokemon_table ORDER BY pokemon_type1, pokemon_type2")
    suspend fun getAllPokemonOrderedByType(): List<Pokemon>

    @Query("SELECT * FROM pokemon_table")
    suspend fun getCurrentPokemonList(): List<Pokemon>

    @Query("SELECT * FROM pokemon_table WHERE pokemon_id IN (:id)")
    suspend fun getPokemonById(id: Int): Pokemon?

    @Query("SELECT * FROM pokemon_table WHERE pokemon_name LIKE '%' || :word || '%'")
    suspend fun getPokemonListBySearch(word: String): List<Pokemon>
}