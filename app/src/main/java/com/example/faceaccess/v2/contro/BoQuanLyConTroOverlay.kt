package com.example.faceaccess.v2.contro

import android.accessibilityservice.AccessibilityService
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
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

    private var viewMucTieu: View? = null
    private var layoutParamsMucTieu: WindowManager.LayoutParams? = null

    private var animatorDiChuyen: ValueAnimator? = null

    private var phienConTroId = 0L
    private var xLogic = 0
    private var yLogic = 0

    @Volatile
    private var dangBat = false

    @Volatile
    private var dangDiChuyen = false

    @Volatile
    private var dangAnimationClick = false

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

    fun diChuyen(
        lenh: LenhConTro,
        mucTieu: Rect?
    ): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post {
                diChuyenNoiBo(
                    lenh = lenh,
                    mucTieu = mucTieu
                )
            }
            return true
        }

        return diChuyenNoiBo(
            lenh = lenh,
            mucTieu = mucTieu
        )
    }

    fun layTamConTro(): Point? {
        val params =
            layoutParamsConTro
                ?: return null

        if (
            !dangBat ||
            viewConTro?.isAttachedToWindow != true
        ) {
            return null
        }

        return Point(
            params.x + params.width / 2,
            params.y + params.height / 2
        )
    }

    fun phanHoiClickThanhCong(): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post {
                phanHoiClickThanhCongNoiBo()
            }
            return true
        }

        return phanHoiClickThanhCongNoiBo()
    }

    fun anMucTieu(): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post {
                anMucTieuNoiBo()
            }
            return true
        }

        anMucTieuNoiBo()
        return true
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

    private fun diChuyenNoiBo(
        lenh: LenhConTro,
        mucTieu: Rect?
    ): Boolean {
        val view =
            viewConTro
                ?: return false

        val params =
            layoutParamsConTro
                ?: return false

        if (
            !dangBat ||
            !view.isAttachedToWindow ||
            dangDiChuyen ||
            dangAnimationClick
        ) {
            return false
        }

        val kichThuoc =
            params.width

        val dich =
            if (mucTieu != null) {
                val xMucTieu =
                    mucTieu.centerX() -
                            kichThuoc / 2

                val yMucTieu =
                    mucTieu.centerY() -
                            kichThuoc / 2

                gioiHanViTri(
                    x = xMucTieu,
                    y = yMucTieu,
                    kichThuoc = kichThuoc
                )
            } else {
                val buoc =
                    dp(BUOC_DI_CHUYEN_DP)

                val deltaX =
                    when (lenh) {
                        LenhConTro.TRAI -> -buoc
                        LenhConTro.PHAI -> buoc
                        else -> 0
                    }

                val deltaY =
                    when (lenh) {
                        LenhConTro.LEN -> -buoc
                        LenhConTro.XUONG -> buoc
                        else -> 0
                    }

                gioiHanViTri(
                    x = xLogic + deltaX,
                    y = yLogic + deltaY,
                    kichThuoc = kichThuoc
                )
            }

        if (
            dich.first == xLogic &&
            dich.second == yLogic
        ) {
            if (mucTieu != null) {
                hienThiMucTieuNoiBo(
                    mucTieu
                )
            }
            return true
        }

        val xBatDau =
            params.x

        val yBatDau =
            params.y

        xLogic =
            dich.first

        yLogic =
            dich.second

        if (mucTieu != null) {
            hienThiMucTieuNoiBo(
                mucTieu
            )
        } else {
            anMucTieuNoiBo()
        }

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
            "MOVE | lenh=$lenh | snap=${mucTieu != null} | x=$xLogic | y=$yLogic"
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
        val vungAnToan =
            layVungAnToanManHinh(
                kichThuoc
            )

        return Pair(
            x.coerceIn(
                vungAnToan.trai,
                vungAnToan.phai
            ),
            y.coerceIn(
                vungAnToan.tren,
                vungAnToan.duoi
            )
        )
    }

    private fun layVungAnToanManHinh(
        kichThuoc: Int
    ): VungAnToan {
        val leAnToan =
            dp(LE_AN_TOAN_DP)

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.R
        ) {
            val metrics =
                windowManager.currentWindowMetrics

            val bounds =
                metrics.bounds

            val insets =
                metrics.windowInsets
                    .getInsetsIgnoringVisibility(
                        WindowInsets.Type.systemBars()
                    )

            val trai =
                bounds.left +
                        insets.left +
                        leAnToan

            val tren =
                bounds.top +
                        insets.top +
                        leAnToan

            val phai =
                (
                        bounds.right -
                                insets.right -
                                kichThuoc -
                                leAnToan
                        ).coerceAtLeast(trai)

            val duoi =
                (
                        bounds.bottom -
                                insets.bottom -
                                kichThuoc -
                                leAnToan
                        ).coerceAtLeast(tren)

            return VungAnToan(
                trai = trai,
                tren = tren,
                phai = phai,
                duoi = duoi
            )
        }

        val metrics =
            accessibilityService.resources.displayMetrics

        val insetTren =
            layKichThuocHeThong(
                "status_bar_height"
            )

        val insetDuoi =
            layKichThuocHeThong(
                "navigation_bar_height"
            )

        val trai =
            leAnToan

        val tren =
            insetTren +
                    leAnToan

        val phai =
            (
                    metrics.widthPixels -
                            kichThuoc -
                            leAnToan
                    ).coerceAtLeast(trai)

        val duoi =
            (
                    metrics.heightPixels -
                            insetDuoi -
                            kichThuoc -
                            leAnToan
                    ).coerceAtLeast(tren)

        return VungAnToan(
            trai = trai,
            tren = tren,
            phai = phai,
            duoi = duoi
        )
    }

    private fun layKichThuocHeThong(
        tenTaiNguyen: String
    ): Int {
        val id =
            accessibilityService.resources
                .getIdentifier(
                    tenTaiNguyen,
                    "dimen",
                    "android"
                )

        if (id <= 0) {
            return 0
        }

        return accessibilityService.resources
            .getDimensionPixelSize(id)
    }

    private fun tatNoiBo(): Boolean {
        phienConTroId += 1L
        dangBat = false

        huyAnimation()

        val viewCu = viewConTro

        viewConTro = null
        layoutParamsConTro = null

        anMucTieuNoiBo()

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

        val view =
            viewConTro

        view?.animate()
            ?.cancel()

        if (view != null) {
            view.scaleX =
                1f

            view.scaleY =
                1f

            datMauConTroMacDinh(
                view
            )
        }

        dangAnimationClick =
            false
    }

    private fun phanHoiClickThanhCongNoiBo(): Boolean {
        val view =
            viewConTro
                ?: return false

        if (
            !dangBat ||
            !view.isAttachedToWindow ||
            dangAnimationClick
        ) {
            return false
        }

        val session =
            phienConTroId

        dangAnimationClick =
            true

        datMauConTroClick(
            view
        )

        view.animate()
            .cancel()

        view.animate()
            .scaleX(TY_LE_THU_NHO_CLICK)
            .scaleY(TY_LE_THU_NHO_CLICK)
            .setDuration(THOI_GIAN_THU_NHO_CLICK_MS)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                if (
                    session != phienConTroId ||
                    !view.isAttachedToWindow
                ) {
                    return@withEndAction
                }

                mainHandler.postDelayed(
                    {
                        if (
                            session != phienConTroId ||
                            !view.isAttachedToWindow
                        ) {
                            return@postDelayed
                        }

                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(THOI_GIAN_PHONG_LAI_CLICK_MS)
                            .setInterpolator(DecelerateInterpolator())
                            .withEndAction {
                                if (
                                    session ==
                                    phienConTroId
                                ) {
                                    datMauConTroMacDinh(
                                        view
                                    )

                                    dangAnimationClick =
                                        false
                                }
                            }
                            .start()
                    },
                    THOI_GIAN_GIU_MAU_CLICK_MS
                )
            }
            .start()

        return true
    }

    private fun datMauConTroMacDinh(
        view: View
    ) {
        val nen =
            view.background as?
                    GradientDrawable
                ?: return

        nen.setColor(
            ContextCompat.getColor(
                accessibilityService,
                R.color.xanh_chinh
            )
        )
    }

    private fun datMauConTroClick(
        view: View
    ) {
        val nen =
            view.background as?
                    GradientDrawable
                ?: return

        nen.setColor(
            android.graphics.Color.rgb(
                34,
                197,
                94
            )
        )
    }

    private fun hienThiMucTieuNoiBo(
        bounds: Rect
    ) {
        val padding =
            dp(PADDING_HIGHLIGHT_DP)

        val trai =
            (bounds.left - padding)
                .coerceAtLeast(0)

        val tren =
            (bounds.top - padding)
                .coerceAtLeast(0)

        val rong =
            bounds.width() +
                    padding * 2

        val cao =
            bounds.height() +
                    padding * 2

        val view =
            viewMucTieu
                ?: taoViewMucTieu()
                    .also {
                        viewMucTieu = it
                    }

        val params =
            layoutParamsMucTieu
                ?: taoLayoutParamsMucTieu()
                    .also {
                        layoutParamsMucTieu = it
                    }

        params.x = trai
        params.y = tren
        params.width = rong
        params.height = cao

        try {
            if (view.isAttachedToWindow) {
                windowManager.updateViewLayout(
                    view,
                    params
                )
            } else {
                windowManager.addView(
                    view,
                    params
                )
            }
        } catch (exception: Exception) {
            Log.w(
                TAG,
                "Khong the hien highlight",
                exception
            )
        }
    }

    private fun anMucTieuNoiBo() {
        val view =
            viewMucTieu
                ?: return

        viewMucTieu = null
        layoutParamsMucTieu = null

        xoaViewAnToan(
            view
        )
    }

    private fun taoViewMucTieu(): View {
        return View(accessibilityService).apply {
            importantForAccessibility =
                View.IMPORTANT_FOR_ACCESSIBILITY_NO

            isClickable = false
            isFocusable = false

            background =
                GradientDrawable().apply {
                    shape =
                        GradientDrawable.RECTANGLE

                    cornerRadius =
                        dp(BAN_KINH_HIGHLIGHT_DP)
                            .toFloat()

                    setColor(
                        Color.TRANSPARENT
                    )

                    setStroke(
                        dp(DO_DAY_HIGHLIGHT_DP),
                        ContextCompat.getColor(
                            accessibilityService,
                            R.color.xanh_chinh
                        )
                    )
                }
        }
    }

    private fun taoLayoutParamsMucTieu():
            WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            1,
            1,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity =
                Gravity.TOP or Gravity.START
        }
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

    private data class VungAnToan(
        val trai: Int,
        val tren: Int,
        val phai: Int,
        val duoi: Int
    )

    companion object {
        private const val TAG = "FaceAccessCursor"

        private const val KICH_THUOC_CON_TRO_DP = 28
        private const val BUOC_DI_CHUYEN_DP = 64

        private const val PADDING_HIGHLIGHT_DP = 4
        private const val BAN_KINH_HIGHLIGHT_DP = 10
        private const val DO_DAY_HIGHLIGHT_DP = 2

        private const val LE_AN_TOAN_DP = 8

        private const val THOI_GIAN_DI_CHUYEN_MS = 180L

        private const val THOI_GIAN_THU_NHO_CLICK_MS = 90L
        private const val THOI_GIAN_GIU_MAU_CLICK_MS = 80L
        private const val THOI_GIAN_PHONG_LAI_CLICK_MS = 130L

        private const val TY_LE_THU_NHO_CLICK = 0.72f
        private const val ALPHA_CON_TRO = 0.95f
    }
}
