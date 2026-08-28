package com.example.faceaccess.v2.truycap

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.faceaccess.v2.R
import com.example.faceaccess.v2.chedo.CheDoDieuKhien

class BoQuanLyTrangThaiOverlay(
    private val accessibilityService: AccessibilityService
) {

    private val mainHandler =
        Handler(
            Looper.getMainLooper()
        )

    private val windowManager =
        accessibilityService.getSystemService(
            AccessibilityService.WINDOW_SERVICE
        ) as WindowManager

    private var viewTrangThai:
            LinearLayout? =
        null

    private var txtChamTrangThai:
            TextView? =
        null

    private var txtCheDo:
            TextView? =
        null

    fun hienThi(
        cheDo: CheDoDieuKhien,
        coKhuonMat: Boolean
    ): Boolean {

        if (
            Looper.myLooper() !=
            Looper.getMainLooper()
        ) {
            mainHandler.post {
                hienThiNoiBo(
                    cheDo = cheDo,
                    coKhuonMat = coKhuonMat
                )
            }

            return true
        }

        return hienThiNoiBo(
            cheDo = cheDo,
            coKhuonMat = coKhuonMat
        )
    }

    fun an(): Boolean {

        if (
            Looper.myLooper() !=
            Looper.getMainLooper()
        ) {
            mainHandler.post {
                anNoiBo()
            }

            return true
        }

        return anNoiBo()
    }

    fun dong() {

        if (
            Looper.myLooper() !=
            Looper.getMainLooper()
        ) {
            mainHandler.post {
                anNoiBo()
            }

            return
        }

        anNoiBo()
    }

    private fun hienThiNoiBo(
        cheDo: CheDoDieuKhien,
        coKhuonMat: Boolean
    ): Boolean {

        val view =
            viewTrangThai
                ?: taoViewTrangThai().also {
                    viewTrangThai = it
                }

        capNhatNoiDung(
            cheDo = cheDo,
            coKhuonMat = coKhuonMat
        )

        if (view.isAttachedToWindow) {
            return true
        }

        return try {
            windowManager.addView(
                view,
                taoLayoutParams()
            )

            true
        } catch (_: Exception) {
            viewTrangThai = null
            txtChamTrangThai = null
            txtCheDo = null

            false
        }
    }

    private fun anNoiBo(): Boolean {

        val view =
            viewTrangThai
                ?: return true

        if (view.isAttachedToWindow) {
            try {
                windowManager.removeView(
                    view
                )
            } catch (_: Exception) {
                return false
            }
        }

        viewTrangThai = null
        txtChamTrangThai = null
        txtCheDo = null

        return true
    }

    private fun taoViewTrangThai():
            LinearLayout {

        val chamTrangThai =
            TextView(
                accessibilityService
            ).apply {
                text = "●"
                textSize = 9f
                includeFontPadding = false
            }

        val cheDo =
            TextView(
                accessibilityService
            ).apply {
                textSize = 12f
                includeFontPadding = false

                setTextColor(
                    ContextCompat.getColor(
                        accessibilityService,
                        R.color.chu_phu
                    )
                )

                typeface =
                    Typeface.create(
                        "sans-serif-medium",
                        Typeface.NORMAL
                    )
            }

        txtChamTrangThai =
            chamTrangThai

        txtCheDo =
            cheDo

        return LinearLayout(
            accessibilityService
        ).apply {
            orientation =
                LinearLayout.HORIZONTAL

            gravity =
                Gravity.CENTER_VERTICAL

            addView(
                chamTrangThai,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            addView(
                cheDo,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart =
                        dp(6)
                }
            )
        }
    }

    private fun taoLayoutParams():
            WindowManager.LayoutParams {

        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity =
                Gravity.TOP or
                        Gravity.START

            x =
                dp(16)

            y =
                layChieuCaoThanhTrangThai() +
                        dp(6)
        }
    }

    private fun capNhatNoiDung(
        cheDo: CheDoDieuKhien,
        coKhuonMat: Boolean
    ) {

        txtChamTrangThai
            ?.setTextColor(
                ContextCompat.getColor(
                    accessibilityService,
                    if (coKhuonMat) {
                        R.color.xanh_trang_thai
                    } else {
                        R.color.do_trang_thai
                    }
                )
            )

        txtCheDo?.text =
            when (cheDo) {
                CheDoDieuKhien.DIEU_HUONG ->
                    "Điều hướng"

                CheDoDieuKhien.MEDIA ->
                    "Media"

                CheDoDieuKhien.HO_TRO ->
                    "Hỗ trợ"

                CheDoDieuKhien.CON_TRO ->
                    "Con trỏ"
            }
    }

    private fun layChieuCaoThanhTrangThai():
            Int {

        val resourceId =
            accessibilityService
                .resources
                .getIdentifier(
                    "status_bar_height",
                    "dimen",
                    "android"
                )

        return if (resourceId > 0) {
            accessibilityService
                .resources
                .getDimensionPixelSize(
                    resourceId
                )
        } else {
            dp(24)
        }
    }

    private fun dp(
        giaTri: Int
    ): Int {

        return (
                giaTri *
                        accessibilityService
                            .resources
                            .displayMetrics
                            .density
                ).toInt()
    }
}