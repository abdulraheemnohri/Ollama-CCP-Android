package com.ollama.ccp.core

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection

class HFDownloader(private val client: OkHttpClient) {

    interface DownloadListener {
        fun onProgress(progress: Float, bytesRead: Long, totalBytes: Long)
        fun onComplete(file: File)
        fun onError(e: Exception)
    }

    fun downloadModel(repo: String, filename: String, destFile: File, listener: DownloadListener) {
        val url = "https://huggingface.co/$repo/resolve/main/$filename"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                listener.onError(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    listener.onError(Exception("Unexpected code $response"))
                    return
                }

                val body = response.body ?: run {
                    listener.onError(Exception("Empty body"))
                    return
                }

                val totalBytes = body.contentLength()
                var bytesRead: Long = 0

                try {
                    body.byteStream().use { input ->
                        FileOutputStream(destFile).use { output ->
                            val buffer = ByteArray(8192)
                            var read: Int
                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                                bytesRead += read
                                listener.onProgress(bytesRead.toFloat() / totalBytes, bytesRead, totalBytes)
                            }
                        }
                    }
                    listener.onComplete(destFile)
                } catch (e: Exception) {
                    listener.onError(e)
                }
            }
        })
    }
}
