package com.example.videowithsticker

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arthenica.mobileffmpeg.ExecuteCallback
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFmpegExecution

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var mediaController: MediaController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnGetVideoFromLocal.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    chooseVideoFromLocal()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 100)
                    Toast.makeText(
                        this,
                        "Please give permission to choose video!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                chooseVideoFromLocal()
            }

        }
    }

    private fun chooseVideoFromLocal() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, 100)
    }

    var uri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            uri = data?.data
            setVideoView(uri)
            btnExportVideo.setOnClickListener {
                //saveImage()
                Merge(uri)
            }
        }
    }
        var currentTime = 0
        var mHandler = Handler()
        var widthVideo = 0
        var heightVideo = 0
        private fun setVideoView(uri: Uri?) {
            if (mediaController == null) {
                mediaController = MediaController(this)
                mediaController!!.setAnchorView(videoView);

                // Set MediaController for VideoView
                videoView.setMediaController(mediaController);
                val videoViewUtils = VideoViewUtil()
                videoViewUtils.playVideo(this, videoView, uri!!)
                widthVideo = videoViewUtils.realWidthVideo
                heightVideo = videoViewUtils.realHeightVideo

                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)

                var widthScreen = displayMetrics.widthPixels
                var heightScreen = displayMetrics.heightPixels
                Log.e("videov", "width: $widthScreen + height = $heightScreen")

                val heightVideoView = heightVideo * widthScreen / widthVideo
                val layoutParams = FrameLayout.LayoutParams(widthScreen, heightVideoView)
                framelayout.layoutParams = layoutParams
                videoView.start()
                videoView.requestFocus()
                if (mHandler != null) {
                    mHandler.postDelayed(UpdateProgress, 1000)
                }
            }
        }

        private val UpdateProgress: Runnable = object : Runnable {
            override fun run() {
                currentTime = videoView.currentPosition
                if (currentTime in 3000..7500) {
                    imageViewUtil.visibility = View.VISIBLE
                } else {
                    imageViewUtil.visibility = View.GONE
                }
                mHandler.postDelayed(this, 1000)
            }
        }

        //    private fun saveImage() {
//        val videoViewWidth = videoView.measuredWidth
//        val videoViewHeight = videoView.measuredHeight
//        Log.d("yenlb", "realVideoWidth = $widthVideo \t  realVideoHeight = $heightVideo")
//        Log.e("yenlb", "videoViewWidth = $videoViewWidth \t videoViewHeight = $videoViewHeight")
//        val ff = FFmpeg.getInstance(this)
//        ff.loadBinary(object : FFmpegLoadBinaryResponseHandler {
//            override fun onStart() {
//                Log.e("FFmpegLoad", "onStart")
//            }
//
//            override fun onFinish() {
//                Log.e("FFmpegLoad", "onFinish")
//            }
//
//            override fun onFailure() {
//                Log.e("FFmpegLoad", "onFailure")
//            }
//
//
//            override fun onSuccess() {
//                Log.e("FFmpegLoad", "onSuccess")
//                val command = arrayOf("-i", uri.toString(), "-vf", "scale=$videoViewWidth:$videoViewHeight", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path) //iw:ih
//               // val command = arrayOf("-i", uri.toString(), "-i", R.drawable.sticker.toString(), "-filter_complex", "overlay=0:main_h-overlay_h",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() )
//                    try {
//                        ff.execute(command, object : ExecuteBinaryResponseHandler() {
//                            override fun onSuccess(message: String?) {
//                                super.onSuccess(message)
//                                Log.e("TAG", "onSuccess: " + message!!)
//                            }
//
//
//                            override fun onProgress(message: String?) {
//                                super.onProgress(message)
//                                Log.e("TAG", "onProgress: " + message!!)
//                            }
//
//
//                            override fun onFailure(message: String?) {
//                                super.onFailure(message)
//                                Log.e("TAG", "onFailure: " + message!!)
//                            }
//
//
//                            override fun onStart() {
//                                super.onStart()
//                                Log.e("TAG", "onStart: ")
//                            }
//
//
//                            override fun onFinish() {
//                                super.onFinish()
//                                Log.e("TAG", "onFinish: ")
//                            }
//                        })
//                    } catch (e: FFmpegCommandAlreadyRunningException) {
//                    }
//            }
//        })
//    }
        fun Merge(uri: Uri?) {
//            val command = arrayOf("-i", uri.toString(), "-vf", "scale=$videoViewWidth:$videoViewHeight",
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path)
            val c = arrayOf(
                "-i", uri.toString(), "-c", "copy", "-metadata:s:v:0", "rotate=90",
                Environment.getExternalStorageDirectory().path
                        + "/Download/2MergeVideo.mp4"
            )
            MergeVideo(c)
        }

        private fun MergeVideo(co: Array<String>) {
            FFmpeg.executeAsync(co) { executionId, returnCode ->
                Log.d("hello", "return  $returnCode")
                Log.d("hello", "executionID  $executionId")
                Log.d("hello", "FFMPEG  " + FFmpegExecution(executionId, co))
            }
        }
}


