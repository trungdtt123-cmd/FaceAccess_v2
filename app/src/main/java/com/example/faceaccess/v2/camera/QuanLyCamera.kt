package com.example.faceaccess.v2.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QuanLyCamera(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val boPhanTichKhungHinh: ImageAnalysis.Analyzer
) {

    private val cameraExecutor: ExecutorService =
        Executors.newSingleThreadExecutor()

    private var cameraProvider: ProcessCameraProvider? = null

    private var imageAnalysis: ImageAnalysis? = null

    /**
     * Bật camera trước.
     *
     * Camera sẽ chạy đồng thời:
     *
     * 1. Preview cho người dùng nhìn thấy.
     * 2. ImageAnalysis để gửi frame sang MediaPipe.
     */
    fun batCamera(
        khiThanhCong: () -> Unit,
        khiLoi: (Throwable) -> Unit
    ) {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            try {

                val provider =
                    cameraProviderFuture.get()

                cameraProvider = provider

                /**
                 * Preview CameraX.
                 */
                val preview =
                    Preview.Builder()
                        .build()
                        .also { cameraPreview ->

                            cameraPreview.surfaceProvider =
                                previewView.surfaceProvider
                        }

                /**
                 * ImageAnalysis dùng để gửi từng frame
                 * sang MediaPipe.
                 */
                val phanTichAnh =
                    ImageAnalysis.Builder()

                        // Không tích hàng đợi frame cũ.
                        // Nếu xử lý chưa kịp thì giữ frame mới nhất.
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )

                        // PhanTichKhungHinhKhuonMat đang đọc
                        // dữ liệu RGBA từ plane đầu tiên.
                        .setOutputImageFormat(
                            ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
                        )

                        .build()

                phanTichAnh.setAnalyzer(
                    cameraExecutor,
                    boPhanTichKhungHinh
                )

                imageAnalysis =
                    phanTichAnh

                /**
                 * FaceAccess hiện chỉ sử dụng camera trước.
                 */
                val cameraSelector =
                    CameraSelector.DEFAULT_FRONT_CAMERA

                /**
                 * Bỏ binding cũ trước khi binding mới.
                 */
                provider.unbindAll()

                /**
                 * Bind đồng thời:
                 *
                 * Preview
                 * +
                 * ImageAnalysis
                 */
                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    phanTichAnh
                )

                /**
                 * Chỉ báo thành công sau khi bind camera
                 * thực sự thành công.
                 */
                khiThanhCong()

            } catch (exception: Exception) {

                imageAnalysis?.clearAnalyzer()
                imageAnalysis = null

                khiLoi(exception)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Dừng camera và dừng gửi frame
     * sang bộ phân tích.
     */
    fun tatCamera() {

        imageAnalysis?.clearAnalyzer()

        imageAnalysis = null

        cameraProvider?.unbindAll()
    }

    /**
     * Giải phóng tài nguyên khi Activity bị hủy.
     */
    fun dong() {

        imageAnalysis?.clearAnalyzer()

        imageAnalysis = null

        cameraProvider?.unbindAll()

        cameraProvider = null

        cameraExecutor.shutdown()
    }
}