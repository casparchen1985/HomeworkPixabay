package com.caspar.homeworkpixabay.model

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Collections
import java.util.concurrent.TimeUnit

object ApiManager {
    private const val defaultTimeOut = 10L

    private var httpClient: OkHttpClient? = null
        get() {
            if (field == null) {
                field = OkHttpClient()
                    .newBuilder()
                    .connectTimeout(defaultTimeOut, TimeUnit.SECONDS)
                    .readTimeout(defaultTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(defaultTimeOut, TimeUnit.SECONDS)
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .addInterceptor(logInterceptor)
                    .addInterceptor(exceptionInterceptor)
                    .build()
            }
            return field
        }

    private val exceptionInterceptor = Interceptor { chain ->
        val request = chain.request()
        try {
            val response = chain.proceed(request)
            val body = response.body?.string() ?: throw NullPointerException("Extract response body as String is Null or failed")
            response
                .newBuilder()
                .body(body.toResponseBody(response.body!!.contentType()))
                .build()
        } catch (e: Exception) {
            val code = when (e) {
                is HttpException -> e.code()
                is SocketTimeoutException -> 408
                is NullPointerException -> 878
                is UnknownHostException -> 787
                else -> 999
            }
            Response.Builder()
                .request(request)
                .message(e.toString())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .body(e.toString().toResponseBody(null))
                .build()
        }
    }

    private val logInterceptor = HttpLoggingInterceptor { msg ->
        Log.i("HttpLog", msg)
    }.apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private var gsonConvert: GsonConverterFactory? = null
        get() {
            if (field == null) {
                field = GsonConverterFactory.create(
                    GsonBuilder().setLenient().disableHtmlEscaping().create()
                )
            }
            return field
        }

    var retrofit: Retrofit? = null
        get() {
            if (field == null) {
                field = Retrofit
                    .Builder()
                    .baseUrl("https://pixabay.com/api/")
                    .client(httpClient!!)
                    .addConverterFactory(gsonConvert!!)
                    .build()
            }
            return field
        }
}