package com.example.faceaccess.v2.contro

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.faceaccess.v2.R

class BoQuanLyConTroOverlay(
    private val accessibilityService: AccessibilityService
) {

    private val mainHandler =
        Handler(Looper.getMainLooper())

    private val windowManager =
        accessibilityService.getSystemService(
            AccessibilityService.WINDOW_SERVICE
        ) as WindowManager

    private var viewConTro: View? = null
    private var phienConTroId = 0L

    @Volatile
    private var dangBat = false

    fun bat(): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { batNoiBo() }
            return true
        }

        return batNoiBo()
    }

    fun tat(): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { tatNoiBo() }
            return true
        }

        return tatNoiBo()
    }

    fun dong() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { dongNoiBo() }
            return
        }

        dongNoiBo()
    }

    fun dangHienThi(): Boolean {
        return dangBat &&
                viewConTro?.isAttachedToWindow == true
    }

    private fun batNoiBo(): Boolean {
        val viewHienTai = viewConTro

        // Chặn tạo nhiều cursor.
        if (
            dangBat &&
            viewHienTai != null &&
            viewHienTai.isAttachedToWindow
        ) {
            return true
        }

        if (viewHienTai != null) {
            xoaViewAnToan(viewHienTai)
            viewConTro = null
        }

        phienConTroId += 1L
        val phienMoi = phienConTroId

        val kichThuoc =
            dp(KICH_THUOC_CON_TRO_DP)

        val viewMoi =
            taoViewConTro()

        val metrics =
            accessibilityService.resources.displayMetrics

        val xGiua =
            (metrics.widthPixels - kichThuoc) / 2

        val yGiua =
            (metrics.heightPixels - kichThuoc) / 2

        val params =
            taoLayoutParams(
                kichThuoc = kichThuoc,
                x = xGiua,
                y = yGiua
            )

        return try {
            windowManager.addView(viewMoi, params)

            // Từ chối view thuộc phiên cũ.
            if (phienMoi != phienConTroId) {
                xoaViewAnToan(viewMoi)
                false
            } else {
                viewConTro = viewMoi
                dangBat = true

                Log.d(
                    TAG,
                    "BAT | session=$phienMoi | x=$xGiua | y=$yGiua"
                )

                true
            }
        } catch (exception: Exception) {
            dangBat = false
            viewConTro = null

            Log.e(
                TAG,
                "Khong the tao cursor overlay",
                exception
            )

            false
        }
    }

    private fun tatNoiBo(): Boolean {
        phienConTroId += 1L
        dangBat = false

        val viewCu = viewConTro
        viewConTro = null

        if (viewCu == null) {
            return true
        }

        xoaViewAnToan(viewCu)

        Log.d(
            TAG,
            "TAT | session=$phienConTroId"
        )

        return true
    }

    private fun dongNoiBo() {
        tatNoiBo()
        mainHandler.removeCallbacksAndMessages(null)
    }

    private fun taoViewConTro(): View {
        return View(accessibilityService).apply {
            importantForAccessibility =
                View.IMPORTANT_FOR_ACCESSIBILITY_NO

            isClickable = false
            isFocusable = false
            alpha = ALPHA_CON_TRO

            background =
                GradientDrawable().apply {
                    shape = GradientDrawable.OVAL

                    setColor(
                        ContextCompat.getColor(
                            accessibilityService,
                            R.color.xanh_chinh
                        )
                    )
                }
        }
    }

    private fun taoLayoutParams(
        kichThuoc: Int,
        x: Int,
        y: Int
    ): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            kichThuoc,
            kichThuoc,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            this.x = x
            this.y = y
        }
    }

    private fun xoaViewAnToan(view: View) {
        if (!view.isAttachedToWindow) {
            return
        }

        try {
            windowManager.removeViewImmediate(view)
        } catch (exception: Exception) {
            Log.w(
                TAG,
                "Bo qua loi remove cursor",
                exception
            )
        }
    }

    private fun dp(giaTri: Int): Int {
        return (
                giaTri *
                        accessibilityService.resources.displayMetrics.density
                ).toInt()
    }

    companion object {
        private const val TAG = "FaceAccessCursor"
        private const val KICH_THUOC_CON_TRO_DP = 28
        private const val ALPHA_CON_TRO = 0.95f
    }
}
