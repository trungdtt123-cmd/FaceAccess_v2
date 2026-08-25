package com.example.faceaccess.v2.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

class QuanLyCamera(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView
) {

    fun batCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val cameraProvider =
                cameraProviderFuture.get()

            val preview =
                Preview.Builder()
                    .build()
                    .also { cameraPreview ->
                        cameraPreview.surfaceProvider =
                            previewView.surfaceProvider
                    }

            val cameraSelector =
                CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )

            } catch (exception: Exception) {
                exception.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun tatCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val cameraProvider =
                cameraProviderFuture.get()

            cameraProvider.unbindAll()

        }, ContextCompat.getMainExecutor(context))
    }
}