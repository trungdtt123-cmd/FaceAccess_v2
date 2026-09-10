// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QuanLyCamera(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView? = null,
    private val boPhanTichKhungHinh: ImageAnalysis.Analyzer
) {

    private val cameraExecutor: ExecutorService =
        Executors.newSingleThreadExecutor()

    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var imageAnalysis: ImageAnalysis? = null

    @Volatile
    private var daDong = false

    @Volatile
    private var yeuCauCameraDangBat = false

    @Volatile
    private var phienYeuCau = 0L

    fun batCamera(
        khiThanhCong: () -> Unit,
        khiLoi: (Throwable) -> Unit
    ) {
        if (daDong) {
            khiLoi(
                IllegalStateException(
                    "QuanLyCamera da dong va khong the su dung lai"
                )
            )
            return
        }

        yeuCauCameraDangBat = true
        val phienHienTai = ++phienYeuCau

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // Bỏ callback cũ nếu Camera đã bị tắt hoặc manager đã đóng.
            if (
                daDong ||
                !yeuCauCameraDangBat ||
                phienHienTai != phienYeuCau
            ) {
                return@addListener
            }

            var phanTichAnhMoi: ImageAnalysis? = null
            var previewMoi: Preview? = null

            try {
                if (
                    lifecycleOwner.lifecycle.currentState ==
                    Lifecycle.State.DESTROYED
                ) {
                    throw IllegalStateException(
                        "LifecycleOwner da DESTROYED"
                    )
                }

                val provider = cameraProviderFuture.get()
                cameraProvider = provider

                phanTichAnhMoi =
                    ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )
                        .setOutputImageFormat(
                            ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
                        )
                        .build()

                phanTichAnhMoi.setAnalyzer(
                    cameraExecutor,
                    boPhanTichKhungHinh
                )

                val cameraSelector =
                    CameraSelector.DEFAULT_FRONT_CAMERA

                // Chỉ gỡ UseCase do chính manager này sở hữu.
                boRangBuocUseCaseCuaManager(provider)

                if (
                    daDong ||
                    !yeuCauCameraDangBat ||
                    phienHienTai != phienYeuCau
                ) {
                    phanTichAnhMoi.clearAnalyzer()
                    return@addListener
                }

                val previewViewHienTai = previewView

                if (previewViewHienTai != null) {
                    previewMoi =
                        Preview.Builder()
                            .build()
                            .also { cameraPreview ->
                                cameraPreview.surfaceProvider =
                                    previewViewHienTai.surfaceProvider
                            }

                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        previewMoi,
                        phanTichAnhMoi
                    )
                } else {
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        phanTichAnhMoi
                    )
                }

                preview = previewMoi
                imageAnalysis = phanTichAnhMoi

                khiThanhCong()

            } catch (exception: Exception) {
                phanTichAnhMoi?.clearAnalyzer()

                try {
                    val provider = cameraProvider
                    if (provider != null) {
                        val useCases = mutableListOf<UseCase>()
                        previewMoi?.let(useCases::add)
                        phanTichAnhMoi?.let(useCases::add)

                        if (useCases.isNotEmpty()) {
                            provider.unbind(*useCases.toTypedArray())
                        }
                    }
                } catch (_: Exception) {
                    // Không che mất lỗi gốc.
                }

                if (
                    !daDong &&
                    yeuCauCameraDangBat &&
                    phienHienTai == phienYeuCau
                ) {
                    khiLoi(exception)
                }
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun tatCamera() {
        yeuCauCameraDangBat = false
        phienYeuCau++

        imageAnalysis?.clearAnalyzer()

        cameraProvider?.let { provider ->
            boRangBuocUseCaseCuaManager(provider)
        }
    }

    fun dong() {
        if (daDong) {
            return
        }

        daDong = true
        yeuCauCameraDangBat = false
        phienYeuCau++

        imageAnalysis?.clearAnalyzer()

        cameraProvider?.let { provider ->
            boRangBuocUseCaseCuaManager(provider)
        }

        cameraProvider = null

        if (!cameraExecutor.isShutdown) {
            cameraExecutor.shutdown()
        }
    }

    private fun boRangBuocUseCaseCuaManager(
        provider: ProcessCameraProvider
    ) {
        val useCases = mutableListOf<UseCase>()

        preview?.let(useCases::add)
        imageAnalysis?.let(useCases::add)

        if (useCases.isNotEmpty()) {
            provider.unbind(*useCases.toTypedArray())
        }

        preview = null
        imageAnalysis = null
    }
}
