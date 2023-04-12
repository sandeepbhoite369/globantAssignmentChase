package com.example.data.repository

import com.example.data.entity.CurrentWeatherDataMock
import com.example.data.source.remote.LocationRemoteSource
import com.example.domain.repository.LocationSearchRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class LocationSearchImplTest {

    private val mockKey = "mockKey"
    private val mockLatitde = 10.0
    private val mockLongitude = 10.0
    lateinit var repository: LocationSearchRepository

    @Mock
    lateinit var remoteSource: LocationRemoteSource

    @BeforeEach
    fun setup() {
        repository = LocationSearchImpl(remoteSource)
    }

    @Test
    fun `Should fetch all top locations`() {
        runBlocking {
            whenever(
                remoteSource.getLocation(mockLatitde, mockLongitude, mockKey)
            ).thenReturn(
                CurrentWeatherDataMock
            )

            val result = remoteSource.getLocation(mockLatitde, mockLongitude, mockKey)
            verify(remoteSource).getLocation(mockLatitde, mockLongitude, mockKey)
            assertEquals(result, CurrentWeatherDataMock)
        }
    }
}