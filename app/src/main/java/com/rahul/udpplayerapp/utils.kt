package com.rahul.udpplayerapp

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.URI

/**
 * Created by konrad on 3/14/18.
 */
object utils {
    fun callHTTP(context: Context?) {
        val client = OkHttpClient()
        val startpreview = Request.Builder()
            .url(HttpUrl.get(URI.create("http://10.5.5.9/gp/gpControl/execute?p1=gpStream&c1=restart")))
            .build()
        client.newCall(startpreview).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
//                    Toast.makeText(context, "Camera not connected!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun loadFFmpeg(con: Context?) {
        val ffmpeg = FFmpeg.getInstance(con)
        try {
            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {
                override fun onStart() {
                    Log.d("LoadBinary", "Start")
                }

                override fun onFailure() {
                    Log.d("LoadBinary", "Fail")
                }

                override fun onSuccess() {
                    Log.d("LoadBinary", "Success")
                }

                override fun onFinish() {
                    Log.d("LoadBinary", "Finish")
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            // Handle if FFmpeg is not supported by device
        }
    }

    fun sendMagicPacket() {
        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket()
            val sendMessage = "GPHD:0:0:2:0.000000\n"
            val sendData = sendMessage.toByteArray()
            val IPAddress = InetAddress.getByName("10.5.5.9")
            val sendPacket = DatagramPacket(sendData, sendData.size, IPAddress, 8554)
            socket.send(sendPacket)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                socket?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    object sendAsyncMagicPacket: AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            var socket: DatagramSocket? = null
            try {
                socket = DatagramSocket()
                val sendMessage = "GPHD:0:0:2:0.000000\n"
                val sendData = sendMessage.toByteArray()
                val IPAddress = InetAddress.getByName("10.5.5.9")
                val sendPacket = DatagramPacket(sendData, sendData.size, IPAddress, 8554)
                socket.send(sendPacket)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    socket?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return null
        }

    }
}
