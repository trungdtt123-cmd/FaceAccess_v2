package com.example.faceaccess.v2.khuonmat

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder

class PhanTichKhungHinhKhuonMat(
    private val xuLyKhuonMat: XuLyKhuonMat,
    private val laCameraTruoc: Boolean = true
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {

        try {

            val thoiGianFrame =
                SystemClock.uptimeMillis()

            val gocXoay =
                imageProxy.imageInfo.rotationDegrees

            /*
             * ImageAnalysis sau này sẽ được cấu hình
             * OUTPUT_IMAGE_FORMAT_RGBA_8888.
             *
             * Vì vậy frame có một plane chứa dữ liệu RGBA.
             */
            val bitmapGoc =
                Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )

            /*
             * Copy dữ liệu camera trước khi đóng ImageProxy.
             */
            bitmapGoc.copyPixelsFromBuffer(
                imageProxy.planes[0].buffer
            )

            /*
             * Sau khi copy xong, CameraX có thể giải phóng frame.
             */
            imageProxy.close()

            /*
             * Camera sensor có thể trả ảnh xoay 90/180/270 độ.
             * Cần đưa ảnh về đúng chiều trước khi MediaPipe xử lý.
             */
            val maTran =
                Matrix().apply {

                    postRotate(
                        gocXoay.toFloat()
                    )

                    /*
                     * Camera trước cần mirror để dữ liệu
                     * nhất quán với hình người dùng nhìn thấy.
                     */
                    if (laCameraTruoc) {

                        postScale(
                            -1f,
                            1f,
                            bitmapGoc.width.toFloat(),
                            bitmapGoc.height.toFloat()
                        )
                    }
                }

            val bitmapDaXuLy =
                Bitmap.createBitmap(
                    bitmapGoc,
                    0,
                    0,
                    bitmapGoc.width,
                    bitmapGoc.height,
                    maTran,
                    true
                )

            /*
             * Bitmap -> MPImage
             */
            val mpImage =
                BitmapImageBuilder(
                    bitmapDaXuLy
                ).build()

            /*
             * Gửi sang tầng MediaPipe.
             */
            xuLyKhuonMat.xuLyAnh(
                mpImage = mpImage,
                thoiGianMs = thoiGianFrame
            )

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Loi khi chuyen frame CameraX sang MediaPipe",
                exception
            )

            /*
             * Nếu lỗi xảy ra trước imageProxy.close(),
             * bắt buộc phải đóng frame để CameraX
             * không bị nghẽn pipeline.
             */
            try {
                imageProxy.close()
            } catch (_: Exception) {
                // Không cần xử lý thêm.
            }
        }
    }

    companion object {

        private const val TAG =
            "PhanTichKhungHinh"
    }
}