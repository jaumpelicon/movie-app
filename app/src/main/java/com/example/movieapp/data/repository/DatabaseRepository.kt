package com.example.movieapp.data.repository

import com.example.movieapp.data.db.DatabaseResult
import com.example.movieapp.data.db.dao.MovieDAO
import com.example.movieapp.data.db.entity.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DatabaseRepository(private val database: MovieDAO) {

    suspend fun getAll(): Flow<DatabaseResult<List<MovieEntity>>> {
        return flow {
            val result = database.getAllMovies()
            emit(DatabaseResult.success(result))
        }.catch {
            emit(DatabaseResult.failure("Erro ao buscar filmes!"))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addMovie(movie: MovieEntity) = database.insert(movie)

    suspend fun deleteAllFavoriteMovies() = database.deleteAllMovies()

    suspend fun findMovie(id: Int): Flow<DatabaseResult<Boolean>> {
        return flow {
            val result = database.findMovie(id)
            emit(DatabaseResult.success(result))
        }.catch {
            emit(DatabaseResult.failure("Erro ao encontrar filme"))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun removeMovie(idMovie: Int) = database.deleteMovie(idMovie)
}