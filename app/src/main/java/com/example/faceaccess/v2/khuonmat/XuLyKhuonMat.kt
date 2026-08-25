package com.example.faceaccess.v2.khuonmat

import android.content.Context
import android.util.Log
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class XuLyKhuonMat(
    context: Context,
    private val langNghe: LangNgheXuLyKhuonMat
) {

    private var faceLandmarker: FaceLandmarker? = null

    init {
        khoiTao(context)
    }

    private fun khoiTao(context: Context) {

        try {

            val baseOptions =
                BaseOptions.builder()
                    .setModelAssetPath(TEN_MODEL)
                    .build()

            val options =
                FaceLandmarker.FaceLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setRunningMode(RunningMode.LIVE_STREAM)

                    // FaceAccess chỉ cần theo dõi người dùng chính.
                    .setNumFaces(1)

                    .setMinFaceDetectionConfidence(0.5f)
                    .setMinFacePresenceConfidence(0.5f)
                    .setMinTrackingConfidence(0.5f)

                    // Sau này dùng cho mắt và miệng.
                    .setOutputFaceBlendshapes(true)

                    // Sau này dùng tính ROLL / YAW / PITCH.
                    .setOutputFacialTransformationMatrixes(true)

                    .setResultListener { result, inputImage ->

                        xuLyKetQua(
                            result = result,
                            inputImage = inputImage
                        )
                    }

                    .setErrorListener { exception ->

                        xuLyLoi(exception)
                    }

                    .build()

            faceLandmarker =
                FaceLandmarker.createFromOptions(
                    context,
                    options
                )

            Log.d(
                TAG,
                "Khoi tao Face Landmarker thanh cong"
            )

            langNghe.khiKhoiTaoThanhCong()

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Khong the khoi tao Face Landmarker",
                exception
            )

            langNghe.khiCoLoi(
                exception.message
                    ?: "Khong the khoi tao Face Landmarker"
            )
        }
    }

    /**
     * Sau này QuanLyCamera sẽ gửi frame camera vào hàm này.
     */
    fun xuLyAnh(
        mpImage: MPImage,
        thoiGianMs: Long
    ) {

        try {

            faceLandmarker?.detectAsync(
                mpImage,
                thoiGianMs
            )

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Loi khi gui frame vao MediaPipe",
                exception
            )

            langNghe.khiCoLoi(
                exception.message
                    ?: "Loi khi xu ly frame"
            )
        }
    }

    private fun xuLyKetQua(
        result: FaceLandmarkerResult,
        inputImage: MPImage
    ) {

        if (result.faceLandmarks().isEmpty()) {

            langNghe.khiKhongThayKhuonMat()

            return
        }

        langNghe.khiCoKetQua(
            result = result,
            chieuRongAnh = inputImage.width,
            chieuCaoAnh = inputImage.height
        )
    }

    private fun xuLyLoi(
        exception: RuntimeException
    ) {

        Log.e(
            TAG,
            "Face Landmarker gap loi",
            exception
        )

        langNghe.khiCoLoi(
            exception.message
                ?: "MediaPipe gap loi"
        )
    }

    /**
     * Giải phóng MediaPipe.
     */
    fun dong() {

        faceLandmarker?.close()

        faceLandmarker = null

        Log.d(
            TAG,
            "Da dong Face Landmarker"
        )
    }

    interface LangNgheXuLyKhuonMat {

        fun khiKhoiTaoThanhCong()

        fun khiCoKetQua(
            result: FaceLandmarkerResult,
            chieuRongAnh: Int,
            chieuCaoAnh: Int
        )

        fun khiKhongThayKhuonMat()

        fun khiCoLoi(
            thongBao: String
        )
    }

    companion object {

        private const val TAG =
            "XuLyKhuonMat"

        private const val TEN_MODEL =
            "models/face_landmarker (1).task"
    }
}