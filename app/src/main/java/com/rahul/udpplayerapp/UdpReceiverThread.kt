package com.rahul.udpplayerapp

import android.graphics.BitmapFactory
import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.nio.ByteBuffer


class UdpReceiverThread: Thread() {
    private val MAX_UDP_DATAGRAM_LEN = 16384
    private val UDP_SERVER_PORT = 8554

    fun log(message: String) {
        Log.d("Thead", message)
    }

    override fun run() {
        val receiverSocket = DatagramSocket(UDP_SERVER_PORT)
//        receiverSocket.connect(InetAddress.getLocalHost(), UDP_SERVER_PORT)
//        receiverSocket.bind()
//        receiverSocket.broadcast = false
//        receiverSocket.soTimeout = 5000
//        val address = InetSocketAddress("0.0.0.0", UDP_SERVER_PORT)
        var lText: String
        val lMsg = ByteArray(MAX_UDP_DATAGRAM_LEN)
        val dp = DatagramPacket(lMsg, lMsg.size)

        try {
            while (true) {
                //disable timeout for testing
                //ds.setSoTimeout(100000);
                log("will receive packet.")
                receiverSocket.receive(dp)
                lText = String(dp.data)
                val inputStream = ByteArrayInputStream(dp.data)
                val bitmap = BitmapFactory.decodeStream(inputStream)
//                val mpeg = MJpegInputStream(inputStream)
//                mpeg.readMJpegFrame()
                Log.i("UDP packet received", lText)
                dp.length = lMsg.size
//            data.setText(lText)
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            receiverSocket.close()
        }
    }
}
