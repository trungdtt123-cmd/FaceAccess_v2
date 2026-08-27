package com.example.faceaccess.v2.thongbao

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.faceaccess.v2.truycap.DichVuTruyCapFaceAccess

object ThongBaoFaceAccess {

    private val mainHandler =
        Handler(
            Looper.getMainLooper()
        )

    @Volatile
    private var toastFallback: Toast? =
        null

    fun hienThi(
        noiDung: String
    ): Boolean {

        if (noiDung.isBlank()) {
            return false
        }

        val overlayDaNhan =
            DichVuTruyCapFaceAccess
                .hienThiThongBaoHeThong(
                    noiDung
                )

        if (overlayDaNhan) {
            huyToastFallback()
        }

        return overlayDaNhan
    }

    fun hienThi(
        context: Context,
        noiDung: String
    ) {

        if (noiDung.isBlank()) {
            return
        }

        if (hienThi(noiDung)) {
            return
        }

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