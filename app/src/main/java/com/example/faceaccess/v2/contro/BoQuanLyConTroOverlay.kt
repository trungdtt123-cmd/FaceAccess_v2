package com.example.faceaccess.v2.contro

import android.accessibilityservice.AccessibilityService
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.faceaccess.v2.R
import com.example.faceaccess.v2.dieuphoi.contro.LenhConTro

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
    private var layoutParamsConTro: WindowManager.LayoutParams? = null
    private var animatorDiChuyen: ValueAnimator? = null

    private var phienConTroId = 0L
    private var xLogic = 0
    private var yLogic = 0

    @Volatile
    private var dangBat = false

    @Volatile
    private var dangDiChuyen = false

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

    fun diChuyen(lenh: LenhConTro): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { diChuyenNoiBo(lenh) }
            return true
        }

        return diChuyenNoiBo(lenh)
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
        }

        huyAnimation()
        phienConTroId += 1L

        val kichThuoc =
            dp(KICH_THUOC_CON_TRO_DP)

        val metrics =
            accessibilityService.resources.displayMetrics

        val xGiua =
            (metrics.widthPixels - kichThuoc) / 2

        val yGiua =
            (metrics.heightPixels - kichThuoc) / 2

        val viewMoi =
            taoViewConTro()

        val params =
            taoLayoutParams(
                kichThuoc = kichThuoc,
                x = xGiua,
                y = yGiua
            )

        return try {
            windowManager.addView(
                viewMoi,
                params
            )

            viewConTro = viewMoi
            layoutParamsConTro = params
            xLogic = xGiua
            yLogic = yGiua
            dangBat = true

            Log.d(
                TAG,
                "BAT | session=$phienConTroId"
            )

            true
        } catch (exception: Exception) {
            viewConTro = null
            layoutParamsConTro = null
            dangBat = false

            Log.e(
                TAG,
                "Khong the tao cursor",
                exception
            )

            false
        }
    }

    private fun diChuyenNoiBo(lenh: LenhConTro): Boolean {
        val view =
            viewConTro
                ?: return false

        val params =
            layoutParamsConTro
                ?: return false

        if (
            !dangBat ||
            !view.isAttachedToWindow ||
            dangDiChuyen
        ) {
            return false
        }

        val dichChuyen =
            dp(BUOC_DI_CHUYEN_DP)

        val deltaX =
            when (lenh) {
                LenhConTro.TRAI -> -dichChuyen
                LenhConTro.PHAI -> dichChuyen
                else -> 0
            }

        val deltaY =
            when (lenh) {
                LenhConTro.LEN -> -dichChuyen
                LenhConTro.XUONG -> dichChuyen
                else -> 0
            }

        val dich =
            gioiHanViTri(
                x = xLogic + deltaX,
                y = yLogic + deltaY,
                kichThuoc = params.width
            )

        if (
            dich.first == xLogic &&
            dich.second == yLogic
        ) {
            Log.d(
                TAG,
                "BIEN | lenh=$lenh"
            )
            return true
        }

        val xBatDau = params.x
        val yBatDau = params.y

        xLogic = dich.first
        yLogic = dich.second

        batDauAnimation(
            view = view,
            params = params,
            xBatDau = xBatDau,
            yBatDau = yBatDau,
            xDich = xLogic,
            yDich = yLogic
        )

        Log.d(
            TAG,
            "MOVE | lenh=$lenh | x=$xLogic | y=$yLogic"
        )

        return true
    }

    private fun batDauAnimation(
        view: View,
        params: WindowManager.LayoutParams,
        xBatDau: Int,
        yBatDau: Int,
        xDich: Int,
        yDich: Int
    ) {
        huyAnimation()

        val session = phienConTroId
        dangDiChuyen = true

        animatorDiChuyen =
            ValueAnimator.ofFloat(
                0f,
                1f
            ).apply {
                duration = THOI_GIAN_DI_CHUYEN_MS
                interpolator = DecelerateInterpolator()

                addUpdateListener { animator ->
                    if (
                        session != phienConTroId ||
                        !view.isAttachedToWindow
                    ) {
                        return@addUpdateListener
                    }

                    val tiLe =
                        animator.animatedValue as Float

                    params.x =
                        noiSuy(
                            xBatDau,
                            xDich,
                            tiLe
                        )

                    params.y =
                        noiSuy(
                            yBatDau,
                            yDich,
                            tiLe
                        )

                    try {
                        windowManager.updateViewLayout(
                            view,
                            params
                        )
                    } catch (_: Exception) {
                    }
                }

                addListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(
                            animation: Animator
                        ) {
                            if (session == phienConTroId) {
                                params.x = xDich
                                params.y = yDich
                                dangDiChuyen = false
                            }

                            if (animatorDiChuyen === animation) {
                                animatorDiChuyen = null
                            }
                        }

                        override fun onAnimationCancel(
                            animation: Animator
                        ) {
                            if (session == phienConTroId) {
                                dangDiChuyen = false
                            }
                        }
                    }
                )

                start()
            }
    }

    private fun gioiHanViTri(
        x: Int,
        y: Int,
        kichThuoc: Int
    ): Pair<Int, Int> {
        val metrics =
            accessibilityService.resources.displayMetrics

        val xMin =
            dp(LE_NGANG_DP)

        val xMax =
            (
                    metrics.widthPixels -
                            kichThuoc -
                            dp(LE_NGANG_DP)
                    ).coerceAtLeast(xMin)

        val yMin =
            dp(LE_TREN_DP)

        val yMax =
            (
                    metrics.heightPixels -
                            kichThuoc -
                            dp(LE_DUOI_DP)
                    ).coerceAtLeast(yMin)

        return Pair(
            x.coerceIn(
                xMin,
                xMax
            ),
            y.coerceIn(
                yMin,
                yMax
            )
        )
    }

    private fun tatNoiBo(): Boolean {
        phienConTroId += 1L
        dangBat = false

        huyAnimation()

        val viewCu = viewConTro

        viewConTro = null
        layoutParamsConTro = null

        if (viewCu != null) {
            xoaViewAnToan(viewCu)
        }

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

    private fun huyAnimation() {
        animatorDiChuyen?.cancel()
        animatorDiChuyen = null
        dangDiChuyen = false
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
            gravity =
                Gravity.TOP or Gravity.START

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

    private fun noiSuy(
        batDau: Int,
        ketThuc: Int,
        tiLe: Float
    ): Int {
        return (
                batDau +
                        (ketThuc - batDau) *
                        tiLe
                ).toInt()
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
        private const val BUOC_DI_CHUYEN_DP = 64

        private const val LE_NGANG_DP = 8
        private const val LE_TREN_DP = 56
        private const val LE_DUOI_DP = 80

        private const val THOI_GIAN_DI_CHUYEN_MS = 180L
        private const val ALPHA_CON_TRO = 0.95f
    }
}