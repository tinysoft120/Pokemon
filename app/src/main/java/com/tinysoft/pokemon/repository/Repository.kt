package com.tinysoft.pokemon.repository

import androidx.annotation.WorkerThread
import com.tinysoft.pokemon.db.Pokemon
import com.tinysoft.pokemon.db.PokemonDao
import com.tinysoft.pokemon.helper.SortOrder
import com.tinysoft.pokemon.network.RestApiService
import com.tinysoft.pokemon.network.ResultWrapper
import com.tinysoft.pokemon.network.models.PokemonDetails
import com.tinysoft.pokemon.utils.PreferenceUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.util.*

/**
 * A Repository is an interface that abstracts access to network and room database data sources.
 */
interface Repository {
    /**
     * Get Pokemon details information via rest api.
     * Return result wrapper for Pokemon details.
     * This method is suspendable synchronous.
     * @param index Pokemon index
     */
    suspend fun getPokemonInfo(index: Int) : ResultWrapper<PokemonDetails>

    /**
     * Get all Pokemon data from local databases.
     * Return list of [Pokemon] as a Flow.
     */
    fun getCachePokemonAll(): Flow<List<Pokemon>>

    /**
     * Get all Pokemon data from local databases.
     * Return list of [Pokemon] sorted by order settings.
     * This method is synchronous.
     */
    suspend fun getCacheSortedPokemon(): List<Pokemon>

    /**
     * Get a specific Pokemon data from local databases by ID.
     * Return [Pokemon] directly.
     * @param pokemonId Pokemon ID
     */
    suspend fun getCachePokemon(pokemonId: Int): Pokemon?

    /**
     * Get all Pokemon data from local databases without sorting.
     * Return list of [Pokemon] as a Flow.
     * This method is synchronous.
     */
    suspend fun getCurrentPokemonList(): List<Pokemon>

    /**
     * Insert Pokemon from api [PokemonDetails] into Database.
     * @param details Pokemon details from api
     */
    suspend fun insertPokemonDetails(details: PokemonDetails)

    /**
     * Search by query in name list of Pokemon
     * This method is synchronous.
     * @param query Search keyword
     */
    suspend fun getPokemonListBySearch(query: String): List<Pokemon>
}

/**
 * Implementation class of Repository
 */
class RepositoryImpl(
    private val apiService: RestApiService,
    private val pokemonDao: PokemonDao
) : Repository {

    override suspend fun getPokemonInfo(index: Int): ResultWrapper<PokemonDetails> {
        return apiCall { apiService.getPokemonInfo("$index") }
    }

    private suspend fun <T : Any> apiCall(call: suspend () -> Response<T>): ResultWrapper<T> {
        val response: Response<T>
        try {
            response = call.invoke()
        } catch (t: Throwable) {
            if (t is CancellationException) throw t
            // TODO: should handle error depend on Exception type. Ex: IOException, HttpException, SocketTimeoutException, ...
            return ResultWrapper.NetworkError
        }

        return if (!response.isSuccessful) {
            //val errorBody = response.errorBody()
            ResultWrapper.GenericError(response.code(), response.message())
        } else if (response.body() == null) {
            // Empty response error
            ResultWrapper.GenericError(1001, "response.body() can't be null")
        } else {
            ResultWrapper.Success(response.body()!!)
        }
    }

    override fun getCachePokemonAll(): Flow<List<Pokemon>> = pokemonDao.getAllPokemon()

    @WorkerThread
    override suspend fun getCacheSortedPokemon(): List<Pokemon> = when (PreferenceUtil.listSortOrder) {
        SortOrder.BY_NUMBER -> pokemonDao.getAllPokemonOrderedByID()
        SortOrder.BY_NAME -> pokemonDao.getAllPokemonOrderedByName()
        SortOrder.BY_TYPE -> pokemonDao.getAllPokemonOrderedByType()
    }

    @WorkerThread
    override suspend fun getCachePokemon(pokemonId: Int): Pokemon? = pokemonDao.getPokemonById(pokemonId)

    @WorkerThread
    override suspend fun getCurrentPokemonList(): List<Pokemon> = pokemonDao.getCurrentPokemonList()

    @WorkerThread
    override suspend fun insertPokemonDetails(details: PokemonDetails) {
        val pokemon: Pokemon
        with(details) {
            val type1 = details.types.firstOrNull { it.slot == 1 }
            val type2 = details.types.firstOrNull { it.slot == 2 }
            val attack = details.stats.firstOrNull { it.stat.name == "attack" }
            val defense = details.stats.firstOrNull { it.stat.name == "defense" }
            val spAttack = details.stats.firstOrNull { it.stat.name == "special-attack" }
            val spDefense = details.stats.firstOrNull { it.stat.name == "special-defense" }
            val speed = details.stats.firstOrNull { it.stat.name == "speed" }
            pokemon = Pokemon(id, name,
                Date(), sprites.other.officialArtwork.frontDefault,
                height, weight,
                type1?.type?.name ?: "", type2?.type?.name ?: "",
                attack?.baseStat ?: -1, defense?.baseStat ?: -1,
                spAttack?.baseStat ?: -1, spDefense?.baseStat ?: -1,
                speed?.baseStat ?: -1
            )
        }
        pokemonDao.insert(pokemon)
    }

    @WorkerThread
    override suspend fun getPokemonListBySearch(query: String): List<Pokemon> = pokemonDao.getPokemonListBySearch(query)
}