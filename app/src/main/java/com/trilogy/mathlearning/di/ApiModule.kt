package com.trilogy.mathlearning.di

import com.trilogy.mathlearning.network.api.AnswerApi
import com.trilogy.mathlearning.network.api.AuthApi
import com.trilogy.mathlearning.network.api.MathConfigApi
import com.trilogy.mathlearning.network.api.PracticeApi
import com.trilogy.mathlearning.network.api.QuestionApi
import com.trilogy.mathlearning.network.api.StatisticApi
import com.trilogy.mathlearning.network.api.UserApi
import com.trilogy.mathlearning.utils.BASE_DOMAIN
import com.trilogy.mathlearning.utils.tokenApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideAuthInter(): Interceptor {
        return Interceptor { chain ->
            val token = tokenApi
            val request = chain.request().newBuilder()
                .apply {
                    token?.let {
                        addHeader("Authorization", "Bearer $it")
                    }
                }
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(authInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .pingInterval(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://$BASE_DOMAIN/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    fun provideQuestionApi(retrofit: Retrofit): QuestionApi = retrofit.create(QuestionApi::class.java)

    @Provides
    fun provideAnswerApi(retrofit: Retrofit): AnswerApi = retrofit.create(AnswerApi::class.java)

    @Provides
    fun provideMathConfigApi(retrofit: Retrofit): MathConfigApi = retrofit.create(MathConfigApi::class.java)

    @Provides
    fun providePracticeApi(retrofit: Retrofit): PracticeApi = retrofit.create(PracticeApi::class.java)

    @Provides
    fun provideStatisticApi(retrofit: Retrofit): StatisticApi = retrofit.create(StatisticApi::class.java)

}