//package com.rahul.udpplayerapp
//
//import android.R.id.message
//import android.util.Log
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import androidx.annotation.OptIn
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.ui.PlayerView
//import java.io.IOException
//import java.net.DatagramPacket
//import java.net.DatagramSocket
//import java.net.InetAddress
//
//
//@OptIn(UnstableApi::class) @Composable
//fun VideoPlayer(modifier: Modifier) {
//    val context = LocalContext.current
//
//    val test = remember<String> {
//        UdpReceiverThread().start()
//
//        ""
//    }
//
//    // create our player
////    val exoPlayer = remember {
////        print("------Ra-----")
////        ExoPlayer.Builder(context).build().apply {
//////            val gpSource = Uri.parse("udp://10.5.5.9:8554")
//////            val dataSpec = DataSpec(gpSource)
////////            val source = DefaultDataSource.Factory(context)
//////
////////            val dataSourceFactory = DefaultDataSource.Factory(context).apply {
////////                val dataSource1 = UdpDataSource()
////////                dataSource1.open(dataSpec)
//////////                data
////////            }
//////
//////            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context, object: DataSource.Factory {
//////                override fun createDataSource(): DataSource {
//////                    val udpDataSource = UdpDataSource()
//////                    return udpDataSource
//////                }
//////            })
//////
//////            // Create a progressive media source pointing to a stream uri.
//////            val mediaSource: MediaSource =
//////                ProgressiveMediaSource.Factory(dataSourceFactory)
//////                    .createMediaSource(MediaItem.fromUri(gpSource))
//////            // Create a player instance.
//////            val player = ExoPlayer.Builder(context).build()
//////            // Set the media source to be played.
//////            player.setMediaSource(mediaSource)
//////            // Prepare the player.
//////            player.prepare()
////            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)
////
////            val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
////            val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(MediaItem.fromUri("udp://@0.0.0.0:8554"))
////            setPlayWhenReady(true);
////            prepare(mediaSource);
////        }
////    }
//    AndroidView(
//        modifier = modifier,
//        factory = {
//            PlayerView(context).apply {
////                player = exoPlayer
//                layoutParams =
//                    FrameLayout.LayoutParams(
//                        ViewGroup.LayoutParams
//                            .MATCH_PARENT,
//                        ViewGroup.LayoutParams
//                            .MATCH_PARENT
//                    )
//            }
//        }
//    )
//}
