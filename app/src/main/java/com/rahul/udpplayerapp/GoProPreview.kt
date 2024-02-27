package com.rahul.udpplayerapp

import android.app.Dialog
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.rahul.udpplayerapp.utils.callHTTP
import com.rahul.udpplayerapp.utils.loadFFmpeg
import com.rahul.udpplayerapp.utils.sendAsyncMagicPacket
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.IVLCVout
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URI


class GoProPreview : ComponentActivity(), IVLCVout.Callback {
    private var mFilePath: String? = null
    private var mSurface: SurfaceView? = null
    private var holder: SurfaceHolder? = null
    private var libvlc: LibVLC? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mVideoWidth = 0
    private var mVideoHeight = 0
    private val client = OkHttpClient()
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_pro_preview)
        loadFFmpeg(getApplicationContext())
        val restart = findViewById(R.id.startPreview) as Button
        restart.setOnClickListener {
            Stream()
            createPlayer(mFilePath)
        }
        val stop = findViewById(R.id.stoppreviewbtn) as Button
        stop.setOnClickListener {
            val ffmpeg = FFmpeg.getInstance(getApplicationContext())
            if (ffmpeg.isFFmpegCommandRunning) {
                ffmpeg.killRunningProcesses()
            }
            releasePlayer()
            Process.killProcess(Process.myPid())
        }
        val setHQ = findViewById(R.id.setQuality) as Button
        setHQ.setOnClickListener {
            val dialog = Dialog(this@GoProPreview)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.radiobutton_dialog)
            val stringList: MutableList<String> =
                ArrayList() // here is list
            val resolutions = arrayOf("720p", "480p", "240p")
            for (i in resolutions.indices) {
                stringList.add(resolutions[i])
            }
            val rg = dialog.findViewById<View>(R.id.radio_group) as RadioGroup
            for (i in stringList.indices) {
                val rb =
                    RadioButton(this@GoProPreview) // dynamically creating RadioButton and adding to RadioGroup.
                rb.text = stringList[i]
                rg.addView(rb)
            }
            dialog.show()
            rg.setOnCheckedChangeListener { group, checkedId ->
                val childCount = group.childCount
                for (x in 0 until childCount) {
                    val btn = group.getChildAt(x) as RadioButton
                    if (btn.id == checkedId) {
                        when (btn.text.toString()) {
                            "720p" -> {
                                GoProSet("64", "7")
                                setBitrate()
                                GoProSet("64", "4")
                                setBitrate()
                                GoProSet("64", "1")
                                setBitrate()
                            }

                            "480p" -> {
                                GoProSet("64", "4")
                                setBitrate()
                                GoProSet("64", "1")
                                setBitrate()
                            }

                            "240p" -> {
                                GoProSet("64", "1")
                                setBitrate()
                            }
                        }
                    }
                }
            }
        }
        Stream()
        mFilePath = "udp://@:8555/gopro"
        Log.d(TAG, "Playing: $mFilePath")
        mSurface = findViewById(R.id.surface) as SurfaceView?
        holder = mSurface!!.holder
        setSize(mVideoWidth, mVideoHeight)
    }

    fun setBitrate() {
        val dialog = Dialog(this@GoProPreview)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.radiobutton_dialog)
        val stringList: MutableList<String> = ArrayList() // here is list
        val bitrates = arrayOf("4 Mbps", "2 Mbps", "1 Mbps", "600 Kbps", "250 Kbps")
        for (i in bitrates.indices) {
            stringList.add(bitrates[i])
        }
        val rg = dialog.findViewById<View>(R.id.radio_group) as RadioGroup
        for (i in stringList.indices) {
            val rb =
                RadioButton(this@GoProPreview) // dynamically creating RadioButton and adding to RadioGroup.
            rb.text = stringList[i]
            rg.addView(rb)
        }
        dialog.show()
        rg.setOnCheckedChangeListener { group, checkedId ->
            val childCount = group.childCount
            for (x in 0 until childCount) {
                val btn = group.getChildAt(x) as RadioButton
                if (btn.id == checkedId) {
                    when (btn.text.toString()) {
                        "4 Mbps" -> {
                            GoProSet("62", "4000000")
                            GoProSet("64", "2000000")
                            GoProSet("64", "1000000")
                            GoProSet("64", "600000")
                            GoProSet("64", "250000")
                        }

                        "2 Mbps" -> {
                            GoProSet("64", "2000000")
                            GoProSet("64", "1000000")
                            GoProSet("64", "600000")
                            GoProSet("64", "250000")
                        }

                        "1 Mbps" -> {
                            GoProSet("64", "1000000")
                            GoProSet("64", "600000")
                            GoProSet("64", "250000")
                        }

                        "600 Kbps" -> {
                            GoProSet("64", "600000")
                            GoProSet("64", "250000")
                        }

                        "250 Kbps" -> GoProSet("64", "250000")
                    }
                    Stream()
                    createPlayer(mFilePath)
                }
            }
        }
    }

    fun GoProSet(param: String, value: String) {
        val startpreview = Request.Builder()
            .url(HttpUrl.get(URI.create("http://10.5.5.9/gp/gpControl/setting/$param/$value")))
            .build()
        client.newCall(startpreview).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.d("GoPro", "Camera not connected")
                }
            }
        })
    }

    fun Stream() {

        //Call http://10.5.5.9/gp/gpControl/execute?p1=gpStream&a1=proto_v2&c1=restart
        callHTTP(getApplicationContext())
        try {
            val cmd = arrayOf(
                "-fflags",
                "nobuffer",
                "-f",
                "mpegts",
                "-i",
                "udp://:8554",
                "-f",
                "mpegts",
                "udp://127.0.0.1:8555/gopro?pkt_size=64"
            )
            //String[] cmd = {"-f", "mpegts", "-i", "udp://:8554", "-f", "mpegts","udp://127.0.0.1:8555/gopro?pkt_size=64"};
            val ffmpeg = FFmpeg.getInstance(getApplicationContext())
            ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {
                    count += 1
                    if (count == 7) {
                        count = 0
                        utils.sendAsyncMagicPacket.execute()
                    }
                }

                override fun onProgress(message: String) {
                    Log.d("FFmpeg", message)
                    callHTTP(getApplicationContext())
                }

                override fun onFailure(message: String) {
                    Toast.makeText(getApplicationContext(), "Stream fail", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onSuccess(message: String) {}
                override fun onFinish() {}
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            // Handle if FFmpeg is already running
            Log.e(TAG, e.stackTraceToString())
        }
        //Preview();
        createPlayer(mFilePath)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setSize(mVideoWidth, mVideoHeight)
    }

    override fun onResume() {
        super.onResume()
        createPlayer(mFilePath)
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    /**
     * Used to set size for SurfaceView
     *
     * @param width
     * @param height
     */
    private fun setSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
        if (mVideoWidth * mVideoHeight <= 1) return
        if (holder == null || mSurface == null) return
        var w: Int = getWindow().getDecorView().getWidth()
        var h: Int = getWindow().getDecorView().getHeight()
        val isPortrait =
            getResources().getConfiguration().orientation === Configuration.ORIENTATION_PORTRAIT
        if (w > h && isPortrait || w < h && !isPortrait) {
            val i = w
            w = h
            h = i
        }
        val videoAR = mVideoWidth.toFloat() / mVideoHeight.toFloat()
        val screenAR = w.toFloat() / h.toFloat()
        if (screenAR < videoAR) h = (w / videoAR).toInt() else w = (h * videoAR).toInt()
        holder!!.setFixedSize(mVideoWidth, mVideoHeight)
        val lp = mSurface!!.layoutParams
        lp.width = w
        lp.height = h
        mSurface!!.layoutParams = lp
        mSurface!!.invalidate()
        if (width * height == 0) return

        // store video size
        mVideoWidth = width
        mVideoHeight = height
        setSize(mVideoWidth, mVideoHeight)
    }

    /**
     * Creates MediaPlayer and plays video
     *
     * @param media
     */
    private fun createPlayer(media: String?) {
        releasePlayer()
        try {
            if (media!!.length > 0) {
                val toast: Toast = Toast.makeText(this, media, Toast.LENGTH_LONG)
                toast.setGravity(
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0,
                    0
                )
                toast.show()
            }

            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            val options = ArrayList<String>()
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles")
            options.add("--audio-time-stretch") // time stretching
            options.add("-vvv") // verbosity
            libvlc = LibVLC(this, options)
            holder!!.setKeepScreenOn(true)

            // Creating media player
            mMediaPlayer = MediaPlayer(libvlc)
            mMediaPlayer!!.setEventListener(mPlayerListener)

            // Seting up video output
            val vout = mMediaPlayer!!.vlcVout
            vout.setVideoView(mSurface)
            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this)
            vout.attachViews()
            val m = Media(libvlc, Uri.parse(media))
            mMediaPlayer!!.media = m
            mMediaPlayer!!.play()
        } catch (e: Exception) {
            Toast.makeText(this, "Error in creating player!", Toast.LENGTH_LONG).show()
        }
    }

    private fun releasePlayer() {
        if (libvlc == null) return
        mMediaPlayer!!.stop()
        val vout = mMediaPlayer!!.vlcVout
        vout.removeCallback(this)
        vout.detachViews()
        holder = null
        libvlc!!.release()
        libvlc = null
        mVideoWidth = 0
        mVideoHeight = 0
    }

    /**
     * Registering callbacks
     */
    private val mPlayerListener: MediaPlayer.EventListener = MyPlayerListener(this)
    override fun onSurfacesCreated(vout: IVLCVout) {}
    override fun onSurfacesDestroyed(vout: IVLCVout) {}
    private class MyPlayerListener(owner: GoProPreview) : MediaPlayer.EventListener {
        private val mOwner: WeakReference<GoProPreview>

        init {
            mOwner = WeakReference(owner)
        }

        override fun onEvent(event: MediaPlayer.Event) {
            val player = mOwner.get()
            when (event.type) {
                MediaPlayer.Event.EndReached -> {
                    Log.d(TAG, "MediaPlayerEndReached")
                    player!!.releasePlayer()
                }

                MediaPlayer.Event.Playing, MediaPlayer.Event.Paused, MediaPlayer.Event.Stopped -> {}
                else -> {}
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}