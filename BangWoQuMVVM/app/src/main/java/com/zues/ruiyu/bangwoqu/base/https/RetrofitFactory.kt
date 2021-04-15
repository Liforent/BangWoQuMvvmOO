package com.zues.ruiyu.bangwoqu.base.https

import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException


/**
 *@Author liforent
 *@create 2020/8/24 14:56
 */
class RetrofitFactory private constructor() {
    companion object {
        val instance by lazy {
            RetrofitFactory()
        }
    }

    private val retrofit: Retrofit
    fun <T> create(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(Constant.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(initOkHttpClient())
            .build()
    }

    private fun initOkHttpClient(): OkHttpClient {

        val trustAllCert: X509TrustManager = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {

                return arrayOf<X509Certificate>()
            }


        }

        val spec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
            .tlsVersions(
                TlsVersion.TLS_1_2,
                TlsVersion.TLS_1_1,
                TlsVersion.TLS_1_0,
                TlsVersion.SSL_3_0
            )
            .allEnabledCipherSuites()
            .build()
        val spec1 = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build()
        return OkHttpClient.Builder()
//            .addInterceptor(initCookieIntercept())
//            .addInterceptor(initLoginIntercept())
            .addInterceptor(initCommonInterceptor())
            .hostnameVerifier { _, _ ->
                true
            }
            //.sslSocketFactory(SSLSocketFactory.getDefault() as SSLSocketFactory,trustAllCert)
            //.connectionSpecs(listOf(spec, spec1))
            .build()
    }


    private fun initCommonInterceptor(): Interceptor {
        val interceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("charset", "UTF-8")
                .build()
            ZLog.e(request.toString())
            chain.proceed(request)
        }

        return interceptor
    }
}