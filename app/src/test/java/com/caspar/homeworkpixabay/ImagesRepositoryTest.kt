package com.caspar.homeworkpixabay

import com.caspar.homeworkpixabay.di.HomeworkPixabayApplication
import com.caspar.homeworkpixabay.model.ImagesRepositoryImpl
import com.caspar.homeworkpixabay.model.dataClass.Hit
import com.caspar.homeworkpixabay.model.dataClass.ImageResult
import com.caspar.homeworkpixabay.model.interfaceDefine.Images
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions
import retrofit2.Response

class ImagesRepositoryTest {
    private val previewUrl = "https://tinyurl.com/yppe7uwv"
    private val imageUrl = "https://tinyurl.com/4fkxnzp8"
    private val imageData = listOf(
        Hit(id = 1, type = "photo", previewURL = previewUrl, imageURL = imageUrl, imageHeight = 1500, imageWidth = 991),
        Hit(id = 2, type = "photo", previewURL = previewUrl, imageURL = imageUrl, imageHeight = 1500, imageWidth = 991),
        Hit(id = 3, type = "photo", previewURL = previewUrl, imageURL = imageUrl, imageHeight = 1500, imageWidth = 991),
        Hit(id = 4, type = "photo", previewURL = previewUrl, imageURL = imageUrl, imageHeight = 1500, imageWidth = 991),
    )
    private lateinit var stubService: Images

    @Before
    fun setup() {
        val stubApp = spyk(HomeworkPixabayApplication)
        stubApp.appContext = mockk(relaxed = true)
        stubService = mockk<Images>()
    }

    @Test
    fun testFetchPhotos_normal() = runTest {
        // set Remote
        val stubResponse = Response.success(ImageResult(1, 100, imageData))
        coEvery { stubService.fetchImages(any(), any()) } returns stubResponse

        // set Local
        val imagesRepos = spyk(ImagesRepositoryImpl(stubService))

        // run Test
        val result = imagesRepos.fetchPhotos("normal", "normal")

        // verify
        Assertions.assertNotNull(result)
        Assertions.assertEquals(imageData, result)
    }

    @Test
    fun testFetchPhotos_offline() = runTest {
        // set Remote
        val stubResponse = Response.error<ImageResult>(444, mockk(relaxed = true))
        coEvery { stubService.fetchImages(any(), any()) } returns stubResponse

        // set Local
        val imageRepos = spyk(ImagesRepositoryImpl(stubService))

        // run Test
        val result = imageRepos.fetchPhotos("error", "error")

        // verify
        Assertions.assertNull(result)
    }


    @After
    fun tearDown() {
        unmockkAll()
    }
}