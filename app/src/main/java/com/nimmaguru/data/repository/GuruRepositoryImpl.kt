package com.nimmaguru.data.repository

import com.nimmaguru.data.remote.FirestoreGuruDataSource
import com.nimmaguru.domain.model.AppUser
import com.nimmaguru.domain.repository.GuruRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuruRepositoryImpl @Inject constructor(
    private val guruDataSource: FirestoreGuruDataSource,
) : GuruRepository {

    override fun observeGurus(): Flow<List<AppUser>> = guruDataSource.observeAllGurus()
}
