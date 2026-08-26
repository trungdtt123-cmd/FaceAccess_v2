package com.example.faceaccess.v2.thongbao

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.faceaccess.v2.truycap.DichVuTruyCapFaceAccess

/**
 * Cổng feedback duy nhất của FaceAccess.
 *
 * Ưu tiên:
 * 1. Accessibility overlay của FaceAccess.
 * 2. Toast chỉ là fallback khi Accessibility chưa hoạt động.
 *
 * Không dùng Toast làm cơ chế chính vì Android có thể rate-limit
 * hoặc gộp/bỏ bớt Toast khi app đang chạy nền hoặc khi thông báo
 * xuất hiện liên tục.
 */
object ThongBaoFaceAccess {

    private val mainHandler =
        Handler(
            Looper.getMainLooper()
        )

    @Volatile
    private var toastFallback:
            Toast? =
        null


    fun hienThi(
        context: Context,
        noiDung: String
    ) {

        if (
            noiDung.isBlank()
        ) {
            return
        }


        /*
         * Accessibility overlay là đường chính.
         *
         * Khi YAW liên tục:
         * mỗi message mới update trực tiếp banner hiện tại,
         * không đi qua hàng đợi Toast.
         */
        val overlayDaNhan =
            DichVuTruyCapFaceAccess
                .hienThiThongBaoHeThong(
                    noiDung
                )


        if (
            overlayDaNhan
        ) {

            huyToastFallback()

            return
        }


        /*
         * Fallback duy nhất nếu Accessibility chưa sẵn sàng.
         */
        val appContext =
            context.applicationContext


        mainHandler.post {

            toastFallback
                ?.cancel()


            toastFallback =
                Toast.makeText(
                    appContext,
                    noiDung,
                    Toast.LENGTH_SHORT
                ).also {
                    it.show()
                }
        }
    }


    fun huy() {

        huyToastFallback()
    }


    private fun huyToastFallback() {

        mainHandler.post {

            toastFallback
                ?.cancel()

            toastFallback =
                null
        }
    }
}
