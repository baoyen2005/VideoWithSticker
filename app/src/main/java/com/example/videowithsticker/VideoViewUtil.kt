package com.example.videowithsticker

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.VideoView

class VideoViewUtil {

    var realWidthVideo = 0
    var realHeightVideo = 0
    fun playVideo(context: Context, videoView: VideoView , uri: Uri){
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context,uri)
        realWidthVideo = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
        realHeightVideo = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        retriever.release()
        Log.d("videov", "width Video: $realWidthVideo \t height video = $realHeightVideo")

    }
}