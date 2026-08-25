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

    /*
     * Activity:
     * truyền PreviewView để hiển thị Camera.
     *
     * Foreground Service:
     * có thể truyền null vì không cần giao diện Preview.
     */
    private val previewView: PreviewView? = null,

    private val boPhanTichKhungHinh: ImageAnalysis.Analyzer
) {

    // =========================================================
    // EXECUTOR
    // =========================================================

    private val cameraExecutor: ExecutorService =
        Executors.newSingleThreadExecutor()


    // =========================================================
    // CAMERA STATE
    // =========================================================

    private var cameraProvider:
            ProcessCameraProvider? = null

    private var imageAnalysis:
            ImageAnalysis? = null


    // =========================================================
    // BẬT CAMERA
    // =========================================================

    /**
     * Bật Camera trước.
     *
     * Nếu có PreviewView:
     *
     * Camera
     * ├── Preview
     * └── ImageAnalysis
     *
     * Nếu không có PreviewView:
     *
     * Camera
     * └── ImageAnalysis
     *
     * Trường hợp không Preview được dùng cho
     * Foreground Service chạy nền.
     */
    fun batCamera(
        khiThanhCong: () -> Unit,
        khiLoi: (Throwable) -> Unit
    ) {

        val cameraProviderFuture =
            ProcessCameraProvider
                .getInstance(context)

        cameraProviderFuture.addListener({

            try {

                val provider =
                    cameraProviderFuture.get()

                cameraProvider =
                    provider


                // =================================================
                // IMAGE ANALYSIS
                // =================================================

                val phanTichAnh =
                    ImageAnalysis.Builder()

                        /*
                         * Không tích các frame cũ.
                         *
                         * Nếu MediaPipe đang xử lý chưa xong,
                         * CameraX chỉ giữ frame mới nhất.
                         */
                        .setBackpressureStrategy(
                            ImageAnalysis
                                .STRATEGY_KEEP_ONLY_LATEST
                        )

                        /*
                         * PhanTichKhungHinhKhuonMat
                         * hiện đang xử lý RGBA_8888.
                         */
                        .setOutputImageFormat(
                            ImageAnalysis
                                .OUTPUT_IMAGE_FORMAT_RGBA_8888
                        )

                        .build()


                phanTichAnh.setAnalyzer(
                    cameraExecutor,
                    boPhanTichKhungHinh
                )


                imageAnalysis =
                    phanTichAnh


                // =================================================
                // CAMERA TRƯỚC
                // =================================================

                val cameraSelector =
                    CameraSelector
                        .DEFAULT_FRONT_CAMERA


                // =================================================
                // HỦY BINDING CŨ
                // =================================================

                provider.unbindAll()


                // =================================================
                // BIND CAMERA
                // =================================================

                val previewViewHienTai =
                    previewView


                if (previewViewHienTai != null) {

                    /*
                     * Trường hợp Activity:
                     *
                     * Preview + ImageAnalysis
                     */

                    val preview =
                        Preview.Builder()
                            .build()
                            .also { cameraPreview ->

                                cameraPreview.surfaceProvider =
                                    previewViewHienTai
                                        .surfaceProvider
                            }


                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        phanTichAnh
                    )

                } else {

                    /*
                     * Trường hợp Foreground Service:
                     *
                     * Không tạo Preview.
                     * Chỉ cần ImageAnalysis để MediaPipe
                     * tiếp tục nhận frame.
                     */

                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        phanTichAnh
                    )
                }


                // =================================================
                // THÀNH CÔNG
                // =================================================

                khiThanhCong()

            } catch (exception: Exception) {

                imageAnalysis
                    ?.clearAnalyzer()

                imageAnalysis =
                    null

                khiLoi(
                    exception
                )
            }

        }, ContextCompat.getMainExecutor(context))
    }


    // =========================================================
    // DỪNG CAMERA
    // =========================================================

    /**
     * Dừng Camera và ngừng đưa frame
     * sang ImageAnalysis.
     *
     * Có thể gọi khi người dùng bấm
     * DỪNG THEO DÕI.
     */
    fun tatCamera() {

        imageAnalysis
            ?.clearAnalyzer()

        imageAnalysis =
            null

        cameraProvider
            ?.unbindAll()
    }


    // =========================================================
    // GIẢI PHÓNG
    // =========================================================

    /**
     * Giải phóng hoàn toàn tài nguyên Camera.
     *
     * Sau khi gọi dong(), đối tượng QuanLyCamera
     * không nên được sử dụng lại.
     */
    fun dong() {

        imageAnalysis
            ?.clearAnalyzer()

        imageAnalysis =
            null

        cameraProvider
            ?.unbindAll()

        cameraProvider =
            null


        if (!cameraExecutor.isShutdown) {

            cameraExecutor
                .shutdown()
        }
    }
}