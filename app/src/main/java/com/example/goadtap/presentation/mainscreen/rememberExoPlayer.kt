package com.example.goadtap.presentation.mainscreen

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.video.spherical.SphericalGLSurfaceView

@OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayer(uri: String): ExoPlayer {

    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {

            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            repeatMode = ExoPlayer.REPEAT_MODE_ALL

            setVideoSurfaceView(SphericalGLSurfaceView(context))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    return player
}