package com.example.faceaccess.v2.khuonmat

import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import kotlin.math.asin
import kotlin.math.atan2

class TrichXuatDuLieuKhuonMat {

    fun trichXuat(
        result: FaceLandmarkerResult
    ): DuLieuKhuonMat {

        val gocDau =
            tinhGocDau(result)

        val doNhamMatTrai =
            layBlendshape(
                result,
                "eyeBlinkLeft"
            )

        val doNhamMatPhai =
            layBlendshape(
                result,
                "eyeBlinkRight"
            )

        val doMoMieng =
            layBlendshape(
                result,
                "jawOpen"
            )

        return DuLieuKhuonMat(

            roll = gocDau.roll,

            yaw = gocDau.yaw,

            pitch = gocDau.pitch,

            doNhamMatTrai =
                doNhamMatTrai,

            doNhamMatPhai =
                doNhamMatPhai,

            doMoMieng =
                doMoMieng
        )
    }

    /**
     * Lấy score của một blendshape.
     *
     * Ví dụ:
     * eyeBlinkLeft
     * eyeBlinkRight
     * jawOpen
     */
    private fun layBlendshape(
        result: FaceLandmarkerResult,
        ten: String
    ): Float? {

        val danhSachKhuonMat =
            result.faceBlendshapes()
                .orElse(emptyList())

        val blendshapes =
            danhSachKhuonMat.firstOrNull()
                ?: return null

        return blendshapes
            .firstOrNull {
                it.categoryName() == ten
            }
            ?.score()
    }

    /**
     * Chuyển rotation matrix 4x4 của MediaPipe
     * thành 3 góc Euler.
     *
     * Đây mới chỉ là dữ liệu thô.
     *
     * Sau khi chạy trên thiết bị,
     * chúng ta sẽ kiểm tra lại:
     *
     * - dấu của ROLL
     * - dấu của YAW
     * - dấu của PITCH
     *
     * trước khi dùng cho gesture.
     */
    private fun tinhGocDau(
        result: FaceLandmarkerResult
    ): GocDau {

        val danhSachMaTran =
            result
                .facialTransformationMatrixes()
                .orElse(emptyList())

        val maTran =
            danhSachMaTran.firstOrNull()
                ?: return GocDau()

        if (maTran.size < 16) {
            return GocDau()
        }

        /*
         * Rotation matrix:
         *
         * r00 r01 r02
         * r10 r11 r12
         * r20 r21 r22
         */

        val r00 =
            maTran[0].toDouble()

        val r10 =
            maTran[4].toDouble()

        val r20 =
            maTran[8].toDouble()

        val r21 =
            maTran[9].toDouble()

        val r22 =
            maTran[10].toDouble()

        /*
         * X rotation = PITCH
         * Y rotation = YAW
         * Z rotation = ROLL
         */

        val pitchRad =
            atan2(
                r21,
                r22
            )

        val yawRad =
            asin(
                (-r20)
                    .coerceIn(
                        -1.0,
                        1.0
                    )
            )

        val rollRad =
            atan2(
                r10,
                r00
            )

        return GocDau(

            roll =
                Math.toDegrees(
                    rollRad
                ).toFloat(),

            yaw =
                Math.toDegrees(
                    yawRad
                ).toFloat(),

            pitch =
                Math.toDegrees(
                    pitchRad
                ).toFloat()
        )
    }

    private data class GocDau(

        val roll: Float? = null,

        val yaw: Float? = null,

        val pitch: Float? = null
    )
}