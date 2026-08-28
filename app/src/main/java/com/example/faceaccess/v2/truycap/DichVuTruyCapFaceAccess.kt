package com.example.faceaccess.v2.truycap

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.faceaccess.v2.R
import com.example.faceaccess.v2.contro.BoQuanLyConTroOverlay
import com.example.faceaccess.v2.contro.BoChonMucTieuConTro
import com.example.faceaccess.v2.chedo.CheDoDieuKhien
import com.example.faceaccess.v2.dieuphoi.contro.LenhConTro
import kotlin.math.abs

class DichVuTruyCapFaceAccess : AccessibilityService() {

    @Volatile
    private var dangCuonBangCuChi =
        false

    private data class DauVetNodeDieuHuong(
        val windowId: Int,
        val bounds: Rect,
        val viewId: String?,
        val className: String?,
        val text: String?,
        val contentDescription: String?
    )

    private data class MucDieuHuong(
        val nodeFocus: AccessibilityNodeInfo,
        val nodeClick: AccessibilityNodeInfo,
        val bounds: Rect,
        val nhan: String?
    )

    private var dauVetNodeDieuHuongDangChon:
            DauVetNodeDieuHuong? =
        null

    private var viewFocusDieuHuong:
            View? =
        null

    private var windowManagerFocusDieuHuong:
            WindowManager? =
        null

    // SYSTEM FEEDBACK OVERLAY

    private lateinit var boQuanLyTrangThaiOverlay:
            BoQuanLyTrangThaiOverlayNoiBo

    private fun batHoTroTruyXuatNhieuCuaSo() {
        val thongTinDichVu =
            serviceInfo

        thongTinDichVu.flags =
            thongTinDichVu.flags or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS

        setServiceInfo(
            thongTinDichVu
        )
    }

    // CURSOR OVERLAY

    private lateinit var boQuanLyConTroOverlay:
            BoQuanLyConTroOverlay

    private val boChonMucTieuConTro =
        BoChonMucTieuConTro()

    private var mucTieuConTroDangChon:
            BoChonMucTieuConTro.KetQua? =
        null

    @Volatile
    private var conTroDangKhoa =
        false

    @Volatile
    private var dangVuotConTro =
        false

    private val mainHandlerThongBao =
        Handler(
            Looper.getMainLooper()
        )

    private var viewThongBaoHeThong:
            TextView? =
        null

    private var windowManagerThongBao:
            WindowManager? =
        null

    private val anThongBaoRunnable =
        Runnable {

            anThongBaoHeThongNoiBo()
        }

    override fun onServiceConnected() {
        super.onServiceConnected()

        batHoTroTruyXuatNhieuCuaSo()

        phienBanDangHoatDong = this

        if (
            !::boQuanLyConTroOverlay.isInitialized
        ) {
            boQuanLyConTroOverlay =
                BoQuanLyConTroOverlay(this)
        }

        if (
            !::boQuanLyTrangThaiOverlay.isInitialized
        ) {
            boQuanLyTrangThaiOverlay =
                BoQuanLyTrangThaiOverlayNoiBo(this)
        }

        dongBoTrangThaiOverlayNoiBo()

        Log.d(
            TAG,
            "Dich vu truy cap da ket noi"
        )
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {
        if (event == null) {
            return
        }

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ->
                capNhatKhiWindowUngDungThayDoi()

            AccessibilityEvent.TYPE_WINDOWS_CHANGED ->
                kiemTraWindowDieuHuongConTonTai()
        }
    }

    private fun capNhatKhiWindowUngDungThayDoi() {
        val dauVet =
            dauVetNodeDieuHuongDangChon
                ?: return

        val windowIdHienTai =
            rootInActiveWindow
                ?.windowId
                ?: return

        if (
            windowIdHienTai ==
            dauVet.windowId
        ) {
            return
        }

        Log.d(
            TAG_FOCUS,
            "RESET_VIRTUAL_FOCUS | " +
                    "window=${dauVet.windowId}->$windowIdHienTai"
        )

        dauVetNodeDieuHuongDangChon =
            null

        anFocusDieuHuongOverlayNoiBo()
    }

    private fun kiemTraWindowDieuHuongConTonTai() {
        val dauVet =
            dauVetNodeDieuHuongDangChon
                ?: return

        val windowConTonTai =
            windows.any { window ->
                window.id ==
                        dauVet.windowId &&
                        laWindowDieuHuongHopLe(
                            window
                        )
            }

        if (windowConTonTai) {
            return
        }

        Log.d(
            TAG_FOCUS,
            "RESET_VIRTUAL_FOCUS | " +
                    "window=${dauVet.windowId} da dong"
        )

        dauVetNodeDieuHuongDangChon =
            null

        anFocusDieuHuongOverlayNoiBo()
    }

    override fun onInterrupt() {

        Log.d(
            TAG,
            "Dich vu truy cap bi interrupt"
        )
    }

    override fun onDestroy() {

        mainHandlerThongBao.removeCallbacks(
            anThongBaoRunnable
        )

        anThongBaoHeThongNoiBo()
        anFocusDieuHuongOverlayNoiBo()

        if (
            ::boQuanLyConTroOverlay.isInitialized
        ) {
            boQuanLyConTroOverlay.dong()
        }

        if (
            ::boQuanLyTrangThaiOverlay.isInitialized
        ) {
            boQuanLyTrangThaiOverlay.dong()
        }

        if (phienBanDangHoatDong === this) {
            phienBanDangHoatDong = null
        }

        Log.d(
            TAG,
            "Dich vu truy cap da dung"
        )

        super.onDestroy()
    }

    // CURSOR OVERLAY

    private fun batConTroNoiBo(): Boolean {
        if (
            !::boQuanLyConTroOverlay.isInitialized
        ) {
            boQuanLyConTroOverlay =
                BoQuanLyConTroOverlay(this)
        }

        val daHienThi =
            boQuanLyConTroOverlay
                .dangHienThi()

        val thanhCong =
            boQuanLyConTroOverlay.bat()

        if (
            thanhCong &&
            !daHienThi
        ) {
            mucTieuConTroDangChon =
                null

            conTroDangKhoa =
                false

            boQuanLyConTroOverlay
                .datKhoa(
                    false
                )
        }

        return thanhCong
    }

    private fun tatConTroNoiBo(): Boolean {
        mucTieuConTroDangChon =
            null

        conTroDangKhoa =
            false

        dangVuotConTro =
            false

        if (
            !::boQuanLyConTroOverlay.isInitialized
        ) {
            return true
        }

        boQuanLyConTroOverlay
            .datKhoa(
                false
            )

        return boQuanLyConTroOverlay.tat()
    }

    private fun diChuyenConTroNoiBo(
        lenh: LenhConTro
    ): Boolean {
        if (conTroDangKhoa) {
            return thucThiVuotConTroNoiBo(
                lenh
            )
        }

        if (
            !::boQuanLyConTroOverlay.isInitialized
        ) {
            return false
        }

        val viTriConTro =
            boQuanLyConTroOverlay
                .layTamConTro()
                ?: return false

        val metrics =
            resources.displayMetrics

        val cacRootConTro =
            layDanhSachRootConTro()

        val mucTieu =
            boChonMucTieuConTro.timMucTieu(
                roots = cacRootConTro,
                viTriConTro = viTriConTro,
                lenh = lenh,
                chieuRongManHinh = metrics.widthPixels,
                chieuCaoManHinh = metrics.heightPixels,
                matDo = metrics.density
            )

        val thanhCong =
            boQuanLyConTroOverlay.diChuyen(
                lenh = lenh,
                mucTieu = mucTieu?.bounds
            )

        if (thanhCong) {
            mucTieuConTroDangChon =
                mucTieu
        }

        Log.d(
            TAG_CON_TRO,
            "MOVE=$lenh | " +
                    "ROOTS=${cacRootConTro.size} | " +
                    "TARGET=${mucTieu?.nhan ?: "NONE"} | " +
                    "WINDOW=${mucTieu?.windowId ?: -1} | " +
                    "OK=$thanhCong"
        )

        return thanhCong
    }

    private fun layDanhSachRootConTro():
            List<AccessibilityNodeInfo> {

        val ketQua =
            mutableListOf<AccessibilityNodeInfo>()

        val cacWindow =
            windows
                .filter {
                    laWindowConTroHopLe(
                        it
                    )
                }
                .sortedWith(
                    compareByDescending<AccessibilityWindowInfo> {
                        it.isFocused ||
                                it.isActive
                    }
                        .thenByDescending {
                            it.layer
                        }
                )

        for (window in cacWindow) {
            val root =
                window.root
                    ?: continue

            if (!root.isVisibleToUser) {
                continue
            }

            if (
                ketQua.none {
                    it.windowId ==
                            root.windowId
                }
            ) {
                ketQua.add(
                    root
                )
            }
        }

        val rootActive =
            rootInActiveWindow

        if (
            rootActive != null &&
            rootActive.isVisibleToUser &&
            ketQua.none {
                it.windowId ==
                        rootActive.windowId
            }
        ) {
            ketQua.add(
                rootActive
            )
        }

        Log.d(
            TAG_CON_TRO,
            "CURSOR_WINDOWS=" +
                    cacWindow.joinToString(
                        separator = " | "
                    ) {
                        "id=${it.id}," +
                                "type=${it.type}," +
                                "layer=${it.layer}," +
                                "active=${it.isActive}," +
                                "focused=${it.isFocused}"
                    }
        )

        return ketQua
    }

    private fun laWindowConTroHopLe(
        window: AccessibilityWindowInfo
    ): Boolean {

        return window.type !=
                AccessibilityWindowInfo.TYPE_ACCESSIBILITY_OVERLAY
    }

    private fun layRootConTroTheoWindowId(
        windowId: Int
    ): AccessibilityNodeInfo? {

        val rootTheoWindow =
            windows
                .firstOrNull {
                    it.id ==
                            windowId &&
                            laWindowConTroHopLe(
                                it
                            )
                }
                ?.root

        if (
            rootTheoWindow != null &&
            rootTheoWindow.isVisibleToUser
        ) {
            return rootTheoWindow
        }

        val rootActive =
            rootInActiveWindow

        return rootActive
            ?.takeIf {
                it.windowId ==
                        windowId &&
                        it.isVisibleToUser
            }
    }

    private fun doiKhoaConTroNoiBo(): Boolean {
        if (
            !::boQuanLyConTroOverlay.isInitialized ||
            !boQuanLyConTroOverlay.dangHienThi()
        ) {
            return false
        }

        val khoaMoi =
            !conTroDangKhoa

        conTroDangKhoa =
            khoaMoi

        mucTieuConTroDangChon =
            null

        val thanhCong =
            boQuanLyConTroOverlay
                .datKhoa(
                    khoaMoi
                )

        if (!thanhCong) {
            conTroDangKhoa =
                !khoaMoi
            return false
        }

        hienThiThongBaoHeThongNoiBo(
            if (khoaMoi) {
                "Đã khóa con trỏ - xoay đầu để vuốt"
            } else {
                "Đã mở khóa con trỏ"
            }
        )

        Log.d(
            TAG_CON_TRO,
            "CURSOR_LOCK=$khoaMoi"
        )

        return true
    }

    private fun thucThiVuotConTroNoiBo(
        lenh: LenhConTro
    ): Boolean {
        if (!conTroDangKhoa) {
            return false
        }

        if (dangVuotConTro) {
            return true
        }

        val metrics =
            resources.displayMetrics

        val rong =
            metrics.widthPixels
                .toFloat()

        val cao =
            metrics.heightPixels
                .toFloat()

        if (
            rong <= 0f ||
            cao <= 0f
        ) {
            return false
        }

        val xGiua =
            rong * 0.50f

        val yGiua =
            cao * 0.50f

        val xTrai =
            rong * TY_LE_VUOT_THAP

        val xPhai =
            rong * TY_LE_VUOT_CAO

        val yTren =
            cao * TY_LE_VUOT_THAP

        val yDuoi =
            cao * TY_LE_VUOT_CAO

        val diem =
            when (lenh) {
                LenhConTro.TRAI ->
                    Pair(
                        Pair(xPhai, yGiua),
                        Pair(xTrai, yGiua)
                    )

                LenhConTro.PHAI ->
                    Pair(
                        Pair(xTrai, yGiua),
                        Pair(xPhai, yGiua)
                    )

                LenhConTro.LEN ->
                    Pair(
                        Pair(xGiua, yDuoi),
                        Pair(xGiua, yTren)
                    )

                LenhConTro.XUONG ->
                    Pair(
                        Pair(xGiua, yTren),
                        Pair(xGiua, yDuoi)
                    )
            }

        val path =
            Path().apply {
                moveTo(
                    diem.first.first,
                    diem.first.second
                )

                lineTo(
                    diem.second.first,
                    diem.second.second
                )
            }

        val gesture =
            GestureDescription
                .Builder()
                .addStroke(
                    GestureDescription
                        .StrokeDescription(
                            path,
                            0L,
                            THOI_GIAN_VUOT_CON_TRO_MS
                        )
                )
                .build()

        dangVuotConTro =
            true

        val daNhan =
            dispatchGesture(
                gesture,
                object :
                    GestureResultCallback() {

                    override fun onCompleted(
                        gestureDescription:
                        GestureDescription?
                    ) {
                        dangVuotConTro =
                            false

                        Log.d(
                            TAG_CON_TRO,
                            "SWIPE_$lenh | COMPLETED"
                        )
                    }

                    override fun onCancelled(
                        gestureDescription:
                        GestureDescription?
                    ) {
                        dangVuotConTro =
                            false

                        Log.e(
                            TAG_CON_TRO,
                            "SWIPE_$lenh | CANCELLED"
                        )
                    }
                },
                null
            )

        if (!daNhan) {
            dangVuotConTro =
                false
        }

        Log.d(
            TAG_CON_TRO,
            "SWIPE_$lenh | DISPATCH=$daNhan"
        )

        return daNhan
    }

    private fun thucThiClickConTroNoiBo(): Boolean {
        if (conTroDangKhoa) {
            Log.d(
                TAG_CON_TRO,
                "CLICK_BO_QUA | CURSOR_DANG_KHOA"
            )
            return false
        }

        if (
            !::boQuanLyConTroOverlay.isInitialized ||
            !boQuanLyConTroOverlay.dangHienThi()
        ) {
            return false
        }

        val mucTieu =
            mucTieuConTroDangChon

        if (mucTieu != null) {
            val root =
                layRootConTroTheoWindowId(
                    mucTieu.windowId
                )

            val node =
                root?.let {
                    timNodeMucTieu(
                        root = it,
                        mucTieu = mucTieu
                    )
                }

            val nodeClickable =
                timNodeClickable(
                    node
                )

            if (
                nodeClickable?.performAction(
                    AccessibilityNodeInfo.ACTION_CLICK
                ) == true
            ) {
                Log.d(
                    TAG_CON_TRO,
                    "CLICK_TARGET_NODE | " +
                            "${mucTieu.nhan ?: mucTieu.viewId ?: "UNKNOWN"} | " +
                            "WINDOW=${mucTieu.windowId}"
                )

                boQuanLyConTroOverlay
                    .phanHoiClickThanhCong()

                return true
            }

            return thucThiTapConTro(
                x = mucTieu.bounds.centerX(),
                y = mucTieu.bounds.centerY(),
                nguon = "TARGET"
            )
        }

        val tamConTro =
            boQuanLyConTroOverlay
                .layTamConTro()
                ?: return false

        return thucThiTapConTro(
            x = tamConTro.x,
            y = tamConTro.y,
            nguon = "CURSOR"
        )
    }

    private fun timNodeMucTieu(
        root: AccessibilityNodeInfo,
        mucTieu: BoChonMucTieuConTro.KetQua
    ): AccessibilityNodeInfo? {
        var nodeTotNhat:
                AccessibilityNodeInfo? =
            null

        var diemTotNhat =
            Int.MIN_VALUE

        fun duyet(
            node: AccessibilityNodeInfo
        ) {
            if (!node.isVisibleToUser) {
                return
            }

            val diem =
                chamDiemNodeMucTieu(
                    node = node,
                    mucTieu = mucTieu
                )

            if (diem > diemTotNhat) {
                diemTotNhat =
                    diem

                nodeTotNhat =
                    node
            }

            for (
            index in 0 until
                    node.childCount
            ) {
                val child =
                    node.getChild(index)
                        ?: continue

                duyet(
                    child
                )
            }
        }

        duyet(
            root
        )

        return nodeTotNhat
            ?.takeIf {
                diemTotNhat >
                        Int.MIN_VALUE
            }
    }

    private fun chamDiemNodeMucTieu(
        node: AccessibilityNodeInfo,
        mucTieu: BoChonMucTieuConTro.KetQua
    ): Int {
        val bounds =
            Rect().also {
                node.getBoundsInScreen(it)
            }

        if (bounds.isEmpty) {
            return Int.MIN_VALUE
        }

        val khoangCach =
            abs(
                bounds.centerX() -
                        mucTieu.bounds.centerX()
            ) +
                    abs(
                        bounds.centerY() -
                                mucTieu.bounds.centerY()
                    )

        val ganMucTieu =
            Rect.intersects(
                bounds,
                mucTieu.bounds
            ) ||
                    khoangCach <=
                    dp(KHOANG_TAI_NHAN_DIEN_TARGET_DP)

        if (!ganMucTieu) {
            return Int.MIN_VALUE
        }

        var diem =
            1000 -
                    khoangCach
                        .coerceAtMost(1000)

        if (bounds == mucTieu.bounds) {
            diem +=
                1000
        }

        if (
            !mucTieu.viewId.isNullOrBlank() &&
            node.viewIdResourceName ==
            mucTieu.viewId
        ) {
            diem +=
                1200
        }

        val nhanNode =
            node.contentDescription
                ?.toString()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: node.text
                    ?.toString()
                    ?.takeIf {
                        it.isNotBlank()
                    }

        if (
            !mucTieu.nhan.isNullOrBlank() &&
            nhanNode ==
            mucTieu.nhan
        ) {
            diem +=
                800
        }

        if (
            !mucTieu.tenLop.isNullOrBlank() &&
            node.className
                ?.toString() ==
            mucTieu.tenLop
        ) {
            diem +=
                100
        }

        if (node.isClickable) {
            diem +=
                200
        }

        return diem
    }

    private fun timNodeClickable(
        nodeBanDau: AccessibilityNodeInfo?
    ): AccessibilityNodeInfo? {
        var node =
            nodeBanDau

        repeat(
            SO_CAP_PARENT_CLICK_TOI_DA
        ) {
            val hienTai =
                node
                    ?: return null

            val coActionClick =
                hienTai.actionList.any {
                    it.id ==
                            AccessibilityNodeInfo.ACTION_CLICK
                }

            if (
                hienTai.isVisibleToUser &&
                (
                        hienTai.isClickable ||
                                coActionClick
                        )
            ) {
                return hienTai
            }

            node =
                hienTai.parent
        }

        return null
    }

    private fun thucThiTapConTro(
        x: Int,
        y: Int,
        nguon: String
    ): Boolean {
        val path =
            Path().apply {
                moveTo(
                    x.toFloat(),
                    y.toFloat()
                )
            }

        val gesture =
            GestureDescription
                .Builder()
                .addStroke(
                    GestureDescription
                        .StrokeDescription(
                            path,
                            0L,
                            THOI_GIAN_TAP_CON_TRO_MS
                        )
                )
                .build()

        val daNhan =
            dispatchGesture(
                gesture,
                object :
                    GestureResultCallback() {

                    override fun onCompleted(
                        gestureDescription:
                        GestureDescription?
                    ) {
                        boQuanLyConTroOverlay
                            .phanHoiClickThanhCong()

                        Log.d(
                            TAG_CON_TRO,
                            "CLICK_$nguon | GESTURE_COMPLETED"
                        )
                    }

                    override fun onCancelled(
                        gestureDescription:
                        GestureDescription?
                    ) {
                        Log.e(
                            TAG_CON_TRO,
                            "CLICK_$nguon | GESTURE_CANCELLED"
                        )
                    }
                },
                null
            )

        Log.d(
            TAG_CON_TRO,
            "CLICK_$nguon | DISPATCH=$daNhan | x=$x | y=$y"
        )

        return daNhan
    }

    private fun dongBoTrangThaiOverlayNoiBo(): Boolean {
        if (
            !::boQuanLyTrangThaiOverlay.isInitialized
        ) {
            boQuanLyTrangThaiOverlay =
                BoQuanLyTrangThaiOverlayNoiBo(this)
        }

        if (!trangThaiOverlayDangBat) {
            return boQuanLyTrangThaiOverlay.an()
        }

        return boQuanLyTrangThaiOverlay.hienThi(
            cheDo = trangThaiOverlayCheDo,
            coKhuonMat = trangThaiOverlayCoKhuonMat
        )
    }

    // FEEDBACK OVERLAY - KHÔNG DÙNG TOAST

    private fun hienThiThongBaoHeThongNoiBo(
        noiDung: String
    ): Boolean {

        if (
            noiDung.isBlank()
        ) {
            return false
        }

        if (
            Looper.myLooper() !=
            Looper.getMainLooper()
        ) {

            mainHandlerThongBao.post {

                hienThiThongBaoHeThongNoiBo(
                    noiDung
                )
            }

            return true
        }

        val windowManager =
            windowManagerThongBao
                ?: (
                        getSystemService(
                            WINDOW_SERVICE
                        ) as? WindowManager
                        )
                    ?.also {

                        windowManagerThongBao =
                            it
                    }
                ?: return false

        val textView =
            viewThongBaoHeThong
                ?: taoViewThongBaoHeThong()
                    .also {
                        viewThongBaoHeThong =
                            it
                    }

        textView.text =
            noiDung

        if (
            !textView.isAttachedToWindow
        ) {

            val params =
                WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    android.graphics.PixelFormat.TRANSLUCENT
                ).apply {

                    gravity =
                        Gravity.TOP or
                                Gravity.CENTER_HORIZONTAL

                    y =
                        dp(
                            72
                        )
                }

            try {

                windowManager.addView(
                    textView,
                    params
                )

            } catch (
                exception: Exception
            ) {

                Log.e(
                    TAG_THONG_BAO,
                    "Khong the hien thi accessibility overlay",
                    exception
                )

                viewThongBaoHeThong =
                    null

                return false
            }
        }

        textView.alpha =
            1f

        mainHandlerThongBao.removeCallbacks(
            anThongBaoRunnable
        )

        mainHandlerThongBao.postDelayed(
            anThongBaoRunnable,
            THOI_GIAN_HIEN_THONG_BAO_MS
        )

        Log.d(
            TAG_THONG_BAO,
            "HIEN_THI: $noiDung"
        )

        return true
    }

    private fun taoViewThongBaoHeThong():
            TextView {

        val nen =
            GradientDrawable().apply {

                shape =
                    GradientDrawable.RECTANGLE

                cornerRadius =
                    dp(
                        16
                    ).toFloat()

                setColor(
                    ContextCompat.getColor(
                        this@DichVuTruyCapFaceAccess,
                        R.color.nen_man_hinh
                    )
                )

                setStroke(
                    dp(
                        2
                    ),
                    ContextCompat.getColor(
                        this@DichVuTruyCapFaceAccess,
                        R.color.xanh_chinh
                    )
                )
            }

        return TextView(
            this
        ).apply {

            setTextColor(
                ContextCompat.getColor(
                    this@DichVuTruyCapFaceAccess,
                    R.color.chu_chinh
                )
            )

            textSize =
                15f

            gravity =
                Gravity.CENTER

            maxLines =
                2

            setPadding(
                dp(
                    20
                ),
                dp(
                    12
                ),
                dp(
                    20
                ),
                dp(
                    12
                )
            )

            background =
                nen

            elevation =
                dp(
                    8
                ).toFloat()
        }
    }

    private fun anThongBaoHeThongNoiBo() {

        if (
            Looper.myLooper() !=
            Looper.getMainLooper()
        ) {

            mainHandlerThongBao.post {

                anThongBaoHeThongNoiBo()
            }

            return
        }

        val textView =
            viewThongBaoHeThong
                ?: return

        val windowManager =
            windowManagerThongBao

        if (
            textView.isAttachedToWindow &&
            windowManager !=
            null
        ) {

            try {

                windowManager.removeView(
                    textView
                )

            } catch (
                exception: Exception
            ) {

                Log.w(
                    TAG_THONG_BAO,
                    "Khong the remove overlay",
                    exception
                )
            }
        }

        viewThongBaoHeThong =
            null
    }

    private fun dp(
        giaTri: Int
    ): Int {

        return (
                giaTri *
                        resources.displayMetrics.density
                )
            .toInt()
    }

    // GLOBAL ACTIONS

    private fun thucThiHomeNoiBo(): Boolean {

        return performGlobalAction(
            GLOBAL_ACTION_HOME
        )
    }

    private fun thucThiBackNoiBo(): Boolean {

        return performGlobalAction(
            GLOBAL_ACTION_BACK
        )
    }

    // DIALER - XÓA SỐ NHƯNG GIỮ APP ĐIỆN THOẠI

    private fun xoaSoTrinhQuaySoNoiBo(
        packageDialerMongDoi: String?,
        soDienThoai: String
    ): Boolean {

        val root =
            rootInActiveWindow
                ?: return false

        val packageDangHoatDong =
            root.packageName
                ?.toString()

        if (
            !packageDialerMongDoi.isNullOrBlank() &&
            packageDangHoatDong !=
            packageDialerMongDoi
        ) {

            Log.e(
                TAG_DIALER,
                "CHAN_XOA_SO | active=$packageDangHoatDong | expected=$packageDialerMongDoi"
            )

            return false
        }

        val soMongDoi =
            chiLayChuSo(
                soDienThoai
            )

        if (
            soMongDoi.isBlank()
        ) {
            return false
        }

        val nodeSetText =
            mutableListOf<AccessibilityNodeInfo>()

        thuThapNodeSetText(
            node = root,
            ketQua = nodeSetText
        )

        val nodeTheoDiem =
            nodeSetText
                .sortedByDescending {
                        node ->

                    diemNodeSo(
                        node = node,
                        soMongDoi =
                            soMongDoi
                    )
                }

        for (
        node in nodeTheoDiem
        ) {

            val chuSoNode =
                chiLayChuSo(
                    node.text
                        ?.toString()
                        .orEmpty()
                )

            if (
                chuSoNode.isNotBlank() &&
                !haiSoKhopNhau(
                    a = chuSoNode,
                    b = soMongDoi
                )
            ) {
                continue
            }

            val arguments =
                Bundle().apply {

                    putCharSequence(
                        AccessibilityNodeInfo
                            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        ""
                    )
                }

            val daSet =
                node.performAction(
                    AccessibilityNodeInfo.ACTION_SET_TEXT,
                    arguments
                )

            if (
                daSet
            ) {

                Log.d(
                    TAG_DIALER,
                    "XOA_SO: ACTION_SET_TEXT da duoc chap nhan"
                )

                return true
            }
        }

        if (
            !cayDangHienThiSo(
                node = root,
                soMongDoi =
                    soMongDoi
            )
        ) {

            Log.e(
                TAG_DIALER,
                "XOA_SO: khong xac minh duoc so hien tai tren Dialer"
            )

            return false
        }

        val nutXoa =
            timNutXoaSo(
                root
            )
                ?: return false

        val coLongClick =
            nutXoa.isLongClickable ||
                    nutXoa.actionList.any {
                            action ->

                        action.id ==
                                AccessibilityNodeInfo.ACTION_LONG_CLICK
                    }

        if (
            coLongClick &&
            nutXoa.performAction(
                AccessibilityNodeInfo.ACTION_LONG_CLICK
            )
        ) {

            Log.d(
                TAG_DIALER,
                "XOA_SO: LONG_CLICK Backspace"
            )

            return true
        }

        val soLanCanXoa =
            soMongDoi.length
                .coerceIn(
                    1,
                    20
                )

        var soLanThanhCong =
            0

        repeat(
            soLanCanXoa
        ) {

            val rootMoi =
                rootInActiveWindow
                    ?: return@repeat

            val packageMoi =
                rootMoi.packageName
                    ?.toString()

            if (
                !packageDialerMongDoi.isNullOrBlank() &&
                packageMoi !=
                packageDialerMongDoi
            ) {

                return@repeat
            }

            val nutXoaMoi =
                timNutXoaSo(
                    rootMoi
                )
                    ?: return@repeat

            if (
                nutXoaMoi.performAction(
                    AccessibilityNodeInfo.ACTION_CLICK
                )
            ) {

                soLanThanhCong +=
                    1
            }
        }

        Log.d(
            TAG_DIALER,
            "XOA_SO: BACKSPACE $soLanThanhCong/$soLanCanXoa"
        )

        return soLanThanhCong >
                0
    }

    private fun batDauCuocGoiTrenDialerNoiBo(
        packageDialerMongDoi: String?
    ): Boolean {
        val root =
            rootInActiveWindow
                ?: return false

        val packageDangHoatDong =
            root.packageName
                ?.toString()

        if (
            !packageDialerMongDoi.isNullOrBlank() &&
            packageDangHoatDong !=
            packageDialerMongDoi
        ) {
            Log.e(
                TAG_DIALER,
                "CHAN_GOI | active=$packageDangHoatDong | expected=$packageDialerMongDoi"
            )
            return false
        }

        val nutGoi =
            timNutGoiDien(root)
                ?: return false

        val thanhCong =
            nutGoi.performAction(
                AccessibilityNodeInfo.ACTION_CLICK
            )

        Log.d(
            TAG_DIALER,
            "GOI_THAT | package=$packageDangHoatDong | ok=$thanhCong"
        )

        return thanhCong
    }

    private fun ketThucCuocGoiNeuDangCoNoiBo(): Boolean {
        val root =
            rootInActiveWindow
                ?: return false

        val nutKetThuc =
            timNutKetThucCuocGoi(root)
                ?: return false

        val thanhCong =
            nutKetThuc.performAction(
                AccessibilityNodeInfo.ACTION_CLICK
            )

        Log.d(
            TAG_DIALER,
            "KET_THUC_CUOC_GOI | ok=$thanhCong"
        )

        return thanhCong
    }

    private fun dangCoCuocGoiDangHienThiNoiBo(): Boolean {
        val root =
            rootInActiveWindow
                ?: return false

        return timNutKetThucCuocGoi(root) != null
    }

    private fun timNutGoiDien(
        node: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {
        val ungVien =
            mutableListOf<Pair<Int, AccessibilityNodeInfo>>()

        thuThapNutGoiDien(
            node = node,
            ketQua = ungVien
        )

        return ungVien
            .maxByOrNull { it.first }
            ?.second
    }

    private fun thuThapNutGoiDien(
        node: AccessibilityNodeInfo,
        ketQua: MutableList<Pair<Int, AccessibilityNodeInfo>>
    ) {
        if (!node.isVisibleToUser) {
            return
        }

        val diem =
            diemNutGoiDien(node)

        if (diem > 0) {
            ketQua.add(
                Pair(diem, node)
            )
        }

        for (index in 0 until node.childCount) {
            val child =
                node.getChild(index)
                    ?: continue

            thuThapNutGoiDien(
                node = child,
                ketQua = ketQua
            )
        }
    }

    private fun diemNutGoiDien(
        node: AccessibilityNodeInfo
    ): Int {
        val coTheBam =
            node.isClickable ||
                    node.actionList.any {
                        it.id ==
                                AccessibilityNodeInfo.ACTION_CLICK
                    }

        if (!coTheBam) {
            return 0
        }

        val nhan =
            nhanNode(node)

        val id =
            node.viewIdResourceName
                ?.lowercase()
                .orEmpty()

        if (
            nhan.contains("video") ||
            nhan.contains("cuộc gọi video")
        ) {
            return 0
        }

        var diem = 0

        if (
            id.contains("dial_button") ||
            id.contains("dialbutton") ||
            id.contains("call_button") ||
            id.contains("callbutton")
        ) {
            diem += 100
        }

        if (
            nhan == "gọi" ||
            nhan == "call" ||
            nhan == "gọi điện" ||
            nhan == "thực hiện cuộc gọi"
        ) {
            diem += 90
        }

        if (
            nhan.startsWith("gọi bằng") ||
            nhan.startsWith("call with") ||
            nhan.startsWith("call using")
        ) {
            diem += 70
        }

        return diem
    }

    private fun timNutKetThucCuocGoi(
        node: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {
        if (!node.isVisibleToUser) {
            return null
        }

        val coTheBam =
            node.isClickable ||
                    node.actionList.any {
                        it.id ==
                                AccessibilityNodeInfo.ACTION_CLICK
                    }

        if (
            coTheBam &&
            laNutKetThucCuocGoi(node)
        ) {
            return node
        }

        for (index in 0 until node.childCount) {
            val child =
                node.getChild(index)
                    ?: continue

            val ketQua =
                timNutKetThucCuocGoi(child)

            if (ketQua != null) {
                return ketQua
            }
        }

        return null
    }

    private fun laNutKetThucCuocGoi(
        node: AccessibilityNodeInfo
    ): Boolean {
        val nhan =
            nhanNode(node)

        val id =
            node.viewIdResourceName
                ?.lowercase()
                .orEmpty()

        val nhanHopLe =
            nhan.contains("kết thúc cuộc gọi") ||
                    nhan.contains("ngắt cuộc gọi") ||
                    nhan.contains("end call") ||
                    nhan.contains("hang up") ||
                    nhan.contains("disconnect call")

        val idHopLe =
            id.contains("end_call") ||
                    id.contains("endcall") ||
                    id.contains("hangup") ||
                    id.contains("disconnect")

        return nhanHopLe || idHopLe
    }

    private fun nhanNode(
        node: AccessibilityNodeInfo
    ): String {
        return listOfNotNull(
            node.text?.toString(),
            node.contentDescription?.toString()
        )
            .joinToString(" ")
            .trim()
            .lowercase()
    }

    private fun thuThapNodeSetText(
        node: AccessibilityNodeInfo,
        ketQua: MutableList<AccessibilityNodeInfo>
    ) {

        if (
            !node.isVisibleToUser
        ) {
            return
        }

        val hoTroSetText =
            node.actionList.any {
                    action ->

                action.id ==
                        AccessibilityNodeInfo.ACTION_SET_TEXT
            }

        if (
            node.isEditable ||
            hoTroSetText
        ) {

            ketQua.add(
                node
            )
        }

        for (
        index in 0 until
                node.childCount
        ) {

            val child =
                node.getChild(
                    index
                )
                    ?: continue

            thuThapNodeSetText(
                node = child,
                ketQua = ketQua
            )
        }
    }

    private fun diemNodeSo(
        node: AccessibilityNodeInfo,
        soMongDoi: String
    ): Int {

        var diem =
            0

        val text =
            node.text
                ?.toString()
                .orEmpty()

        val chuSo =
            chiLayChuSo(
                text
            )

        if (
            chuSo.isNotBlank() &&
            haiSoKhopNhau(
                a = chuSo,
                b = soMongDoi
            )
        ) {

            diem +=
                100
        }

        val id =
            node.viewIdResourceName
                ?.lowercase()
                .orEmpty()

        if (
            id.contains(
                "digit"
            ) ||
            id.contains(
                "number"
            ) ||
            id.contains(
                "phone"
            ) ||
            id.contains(
                "dial"
            )
        ) {

            diem +=
                40
        }

        if (
            node.isEditable
        ) {

            diem +=
                20
        }

        if (
            node.actionList.any {
                    action ->

                action.id ==
                        AccessibilityNodeInfo.ACTION_SET_TEXT
            }
        ) {

            diem +=
                20
        }

        return diem
    }

    private fun cayDangHienThiSo(
        node: AccessibilityNodeInfo,
        soMongDoi: String
    ): Boolean {

        if (
            !node.isVisibleToUser
        ) {
            return false
        }

        val textSo =
            chiLayChuSo(
                node.text
                    ?.toString()
                    .orEmpty()
            )

        if (
            textSo.isNotBlank() &&
            haiSoKhopNhau(
                a = textSo,
                b = soMongDoi
            )
        ) {

            return true
        }

        val descSo =
            chiLayChuSo(
                node.contentDescription
                    ?.toString()
                    .orEmpty()
            )

        if (
            descSo.isNotBlank() &&
            haiSoKhopNhau(
                a = descSo,
                b = soMongDoi
            )
        ) {

            return true
        }

        for (
        index in 0 until
                node.childCount
        ) {

            val child =
                node.getChild(
                    index
                )
                    ?: continue

            if (
                cayDangHienThiSo(
                    node = child,
                    soMongDoi =
                        soMongDoi
                )
            ) {

                return true
            }
        }

        return false
    }

    private fun timNutXoaSo(
        node: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {

        if (
            !node.isVisibleToUser
        ) {
            return null
        }

        val nhan =
            listOfNotNull(
                node.text
                    ?.toString(),
                node.contentDescription
                    ?.toString()
            )
                .joinToString(
                    separator = " "
                )
                .trim()
                .lowercase()

        val id =
            node.viewIdResourceName
                ?.lowercase()
                .orEmpty()

        val nhanHopLe =
            nhan ==
                    "xóa" ||
                    nhan ==
                    "xoá" ||
                    nhan ==
                    "delete" ||
                    nhan ==
                    "backspace" ||
                    nhan.contains(
                        "xóa số"
                    ) ||
                    nhan.contains(
                        "xoá số"
                    ) ||
                    nhan.contains(
                        "delete digit"
                    ) ||
                    nhan.contains(
                        "delete number"
                    )

        val idHopLe =
            id.contains(
                "backspace"
            ) ||
                    id.contains(
                        "delete"
                    )

        val coTheBam =
            node.isClickable ||
                    node.isLongClickable ||
                    node.actionList.any {
                            action ->

                        action.id ==
                                AccessibilityNodeInfo.ACTION_CLICK ||
                                action.id ==
                                AccessibilityNodeInfo.ACTION_LONG_CLICK
                    }

        if (
            coTheBam &&
            (
                    nhanHopLe ||
                            idHopLe
                    )
        ) {

            return node
        }

        for (
        index in 0 until
                node.childCount
        ) {

            val child =
                node.getChild(
                    index
                )
                    ?: continue

            val ketQua =
                timNutXoaSo(
                    child
                )

            if (
                ketQua !=
                null
            ) {

                return ketQua
            }
        }

        return null
    }

    private fun chiLayChuSo(
        giaTri: String
    ): String {

        return giaTri
            .filter {
                it.isDigit()
            }
    }

    private fun haiSoKhopNhau(
        a: String,
        b: String
    ): Boolean {

        if (
            a.isBlank() ||
            b.isBlank()
        ) {
            return false
        }

        return a ==
                b ||
                a.endsWith(
                    b
                ) ||
                b.endsWith(
                    a
                )
    }

    // ACCESSIBILITY FOCUS NAVIGATION

    private fun thucThiTiepTheoNoiBo(): Boolean {

        return diChuyenAccessibilityFocus(
            huong = View.FOCUS_FORWARD
        )
    }

    private fun thucThiTruocNoiBo(): Boolean {

        return diChuyenAccessibilityFocus(
            huong = View.FOCUS_BACKWARD
        )
    }

    private fun thucThiXacNhanDieuHuongNoiBo(): Boolean {

        val cacRoot =
            layDanhSachRootDieuHuong()

        val rootUuTien =
            timRootCoMucDieuHuong(
                cacRoot
            )
                ?: return false

        val nodeTheoVirtualFocus =
            timNodeTheoDauVet(
                root = rootUuTien,
                dauVet = dauVetNodeDieuHuongDangChon
            )

        val nodeDangFocus =
            nodeTheoVirtualFocus
                ?: timNodeDangFocusTrongRoot(
                    rootUuTien
                )

        if (nodeDangFocus == null) {
            Log.d(
                TAG_FOCUS,
                "XAC_NHAN THAT_BAI | khong co node dang chon trong window=${rootUuTien.windowId}"
            )

            return false
        }

        val nodeXacNhan =
            timNodeXacNhanDieuHuong(
                nodeDangFocus
            )
                ?: nodeDangFocus
                    .takeIf {
                        it.isVisibleToUser
                    }

        if (nodeXacNhan == null) {
            Log.d(
                TAG_FOCUS,
                "XAC_NHAN THAT_BAI | khong tim thay node xac nhan"
            )

            return false
        }

        val actionClickThanhCong =
            nodeXacNhan.performAction(
                AccessibilityNodeInfo.ACTION_CLICK
            )

        Log.d(
            TAG_FOCUS,
            "XAC_NHAN_ACTION_CLICK | " +
                    "virtual=${nodeTheoVirtualFocus != null} | " +
                    "text=${nodeXacNhan.text} | " +
                    "desc=${nodeXacNhan.contentDescription} | " +
                    "OK=$actionClickThanhCong"
        )

        if (actionClickThanhCong) {
            ketThucLuaChonDieuHuongSauXacNhan()
            return true
        }

        val tapThanhCong =
            thucThiTapNodeDieuHuong(
                node = nodeXacNhan
            )

        Log.d(
            TAG_FOCUS,
            "XAC_NHAN_TAP_FALLBACK | " +
                    "virtual=${nodeTheoVirtualFocus != null} | " +
                    "OK=$tapThanhCong"
        )

        if (tapThanhCong) {
            ketThucLuaChonDieuHuongSauXacNhan()
        }

        return tapThanhCong
    }

    private fun thucThiTapNodeDieuHuong(
        node: AccessibilityNodeInfo
    ): Boolean {

        val bounds =
            Rect().also {
                node.getBoundsInScreen(
                    it
                )
            }

        if (
            bounds.isEmpty ||
            bounds.width() <= 0 ||
            bounds.height() <= 0
        ) {
            return false
        }

        val x =
            bounds.centerX()
                .toFloat()

        val y =
            bounds.centerY()
                .toFloat()

        val path =
            Path().apply {
                moveTo(
                    x,
                    y
                )
            }

        val gesture =
            GestureDescription
                .Builder()
                .addStroke(
                    GestureDescription
                        .StrokeDescription(
                            path,
                            0L,
                            THOI_GIAN_TAP_DIEU_HUONG_MS
                        )
                )
                .build()

        return dispatchGesture(
            gesture,
            object :
                GestureResultCallback() {

                override fun onCompleted(
                    gestureDescription:
                    GestureDescription?
                ) {
                    Log.d(
                        TAG_FOCUS,
                        "XAC_NHAN_TAP_COMPLETED | x=$x | y=$y"
                    )
                }

                override fun onCancelled(
                    gestureDescription:
                    GestureDescription?
                ) {
                    Log.e(
                        TAG_FOCUS,
                        "XAC_NHAN_TAP_CANCELLED | x=$x | y=$y"
                    )
                }
            },
            null
        )
    }

    private fun ketThucLuaChonDieuHuongSauXacNhan() {

        dauVetNodeDieuHuongDangChon =
            null

        anFocusDieuHuongOverlayNoiBo()
    }

    private fun timNodeXacNhanDieuHuong(
        nodeBanDau: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {

        var node:
                AccessibilityNodeInfo? =
            nodeBanDau

        repeat(
            SO_CAP_PARENT_CLICK_TOI_DA
        ) {
            val hienTai =
                node
                    ?: return null

            if (
                hienTai.isVisibleToUser &&
                coActionClick(
                    hienTai
                )
            ) {
                return hienTai
            }

            node =
                hienTai.parent
        }

        return null
    }

    private fun diChuyenAccessibilityFocus(
        huong: Int
    ): Boolean {

        val tenHuong =
            when (huong) {
                View.FOCUS_FORWARD ->
                    "FORWARD"

                View.FOCUS_BACKWARD ->
                    "BACKWARD"

                else -> {
                    Log.e(
                        TAG_FOCUS,
                        "THAT_BAI: huong focus khong hop le=$huong"
                    )

                    return false
                }
            }

        val cacRoot =
            layDanhSachRootDieuHuong()

        if (cacRoot.isEmpty()) {
            Log.e(
                TAG_FOCUS,
                "THAT_BAI[$tenHuong]: khong co accessibility window hop le"
            )

            return false
        }

        val rootUuTien =
            timRootCoMucDieuHuong(
                cacRoot
            )
                ?: run {
                    Log.e(
                        TAG_FOCUS,
                        "THAT_BAI[$tenHuong]: khong co muc co the dieu huong"
                    )

                    return false
                }

        val danhSachMuc =
            taoDanhSachMucDieuHuong(
                rootUuTien
            )

        if (danhSachMuc.isEmpty()) {
            return false
        }

        val nodeFocusHeThong =
            timNodeDangFocusTrongRoot(
                rootUuTien
            )

        val viTriTheoFocusHeThong =
            timViTriMucDangFocus(
                nodeDangFocus = nodeFocusHeThong,
                danhSachMuc = danhSachMuc
            )

        val viTriTheoDauVet =
            timViTriTheoDauVet(
                danhSachMuc = danhSachMuc,
                dauVet = dauVetNodeDieuHuongDangChon
            )

        val viTriHienTai =
            when {
                viTriTheoDauVet >= 0 ->
                    viTriTheoDauVet

                viTriTheoFocusHeThong >= 0 ->
                    viTriTheoFocusHeThong

                else ->
                    -1
            }

        Log.d(
            TAG_FOCUS,
            "BAT_DAU[$tenHuong] | " +
                    "soWindow=${cacRoot.size} | " +
                    "windowId=${rootUuTien.windowId} | " +
                    "soMuc=${danhSachMuc.size} | " +
                    "indexHeThong=$viTriTheoFocusHeThong | " +
                    "indexFaceAccess=$viTriTheoDauVet"
        )

        if (
            nodeFocusHeThong != null &&
            viTriTheoDauVet < 0
        ) {
            val nodeTheoHeThong =
                timNodeTheoFocusSearch(
                    nodeDangFocus = nodeFocusHeThong,
                    huong = huong
                )

            if (
                nodeTheoHeThong != null &&
                nodeTheoHeThong.windowId ==
                rootUuTien.windowId &&
                !laCungNode(
                    a = nodeTheoHeThong,
                    b = nodeFocusHeThong
                )
            ) {
                val mucTheoHeThong =
                    taoMucDieuHuongTuNode(
                        node = nodeTheoHeThong,
                        nodeGoiYFocus = nodeTheoHeThong
                    )

                if (
                    mucTheoHeThong != null &&
                    datFocusVaoMuc(
                        muc = mucTheoHeThong,
                        tenHuong = tenHuong,
                        nguon = "FOCUS_SEARCH"
                    )
                ) {
                    luuNodeDieuHuongDangChon(
                        mucTheoHeThong.nodeClick
                    )

                    return true
                }
            }
        }

        val viTriDich =
            tinhViTriDich(
                huong = huong,
                viTriHienTai = viTriHienTai,
                soLuong = danhSachMuc.size
            )

        val mucDich =
            danhSachMuc[
                viTriDich
            ]

        Log.d(
            TAG_FOCUS,
            "MUC_DICH[$tenHuong] | " +
                    "index=$viTriHienTai->$viTriDich/${danhSachMuc.lastIndex} | " +
                    "window=${mucDich.nodeClick.windowId} | " +
                    "label=${mucDich.nhan ?: "NONE"} | " +
                    "focusClass=${mucDich.nodeFocus.className} | " +
                    "clickClass=${mucDich.nodeClick.className}"
        )

        val thanhCong =
            datFocusVaoMuc(
                muc = mucDich,
                tenHuong = tenHuong,
                nguon = "SEMANTIC_LIST"
            )

        if (thanhCong) {
            luuNodeDieuHuongDangChon(
                mucDich.nodeClick
            )
        }

        return thanhCong
    }

    private fun layDanhSachRootDieuHuong():
            List<AccessibilityNodeInfo> {

        val ketQua =
            mutableListOf<AccessibilityNodeInfo>()

        val cacWindow =
            windows
                .filter {
                    laWindowDieuHuongHopLe(
                        it
                    )
                }
                .sortedWith(
                    compareByDescending<AccessibilityWindowInfo> {
                        it.isFocused ||
                                it.isActive
                    }
                        .thenByDescending {
                            it.layer
                        }
                )

        Log.d(
            TAG_FOCUS,
            "WINDOWS=" +
                    cacWindow.joinToString(
                        separator = " | "
                    ) {
                        "id=${it.id}," +
                                "type=${it.type}," +
                                "layer=${it.layer}," +
                                "active=${it.isActive}," +
                                "focused=${it.isFocused}"
                    }
        )

        for (window in cacWindow) {
            val root =
                window.root
                    ?: continue

            if (!root.isVisibleToUser) {
                continue
            }

            if (
                ketQua.none {
                    it.windowId ==
                            root.windowId
                }
            ) {
                ketQua.add(
                    root
                )
            }
        }

        val rootActive =
            rootInActiveWindow

        if (
            rootActive != null &&
            rootActive.isVisibleToUser &&
            ketQua.none {
                it.windowId ==
                        rootActive.windowId
            }
        ) {
            ketQua.add(
                rootActive
            )
        }

        return ketQua
    }

    private fun laWindowDieuHuongHopLe(
        window: AccessibilityWindowInfo
    ): Boolean {

        return when (window.type) {
            AccessibilityWindowInfo.TYPE_ACCESSIBILITY_OVERLAY,
            AccessibilityWindowInfo.TYPE_INPUT_METHOD ->
                false

            else ->
                true
        }
    }

    private fun timRootCoMucDieuHuong(
        cacRoot: List<AccessibilityNodeInfo>
    ): AccessibilityNodeInfo? {

        for (root in cacRoot) {
            if (
                taoDanhSachMucDieuHuong(
                    root
                ).isNotEmpty()
            ) {
                return root
            }
        }

        return null
    }

    private fun timNodeDangFocusTrongRoot(
        root: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {

        return root.findFocus(
            AccessibilityNodeInfo.FOCUS_ACCESSIBILITY
        )
            ?: root.findFocus(
                AccessibilityNodeInfo.FOCUS_INPUT
            )
    }

    private fun timNodeTheoFocusSearch(
        nodeDangFocus: AccessibilityNodeInfo,
        huong: Int
    ): AccessibilityNodeInfo? {

        return try {
            nodeDangFocus.focusSearch(
                huong
            )
        } catch (
            exception: Exception
        ) {
            Log.w(
                TAG_FOCUS,
                "focusSearch that bai",
                exception
            )

            null
        }
    }

    private fun taoDanhSachMucDieuHuong(
        root: AccessibilityNodeInfo
    ): List<MucDieuHuong> {

        val ungVien =
            mutableListOf<AccessibilityNodeInfo>()

        thuThapNodeUngVienDieuHuong(
            node = root,
            ketQua = ungVien
        )

        val ketQua =
            mutableListOf<MucDieuHuong>()

        for (node in ungVien) {
            val muc =
                taoMucDieuHuongTuNode(
                    node = node
                )
                    ?: continue

            val daTonTai =
                ketQua.any {
                    laCungNode(
                        a = it.nodeClick,
                        b = muc.nodeClick
                    )
                }

            if (!daTonTai) {
                ketQua.add(
                    muc
                )
            }
        }

        return ketQua
    }

    private fun taoMucDieuHuongTuNode(
        node: AccessibilityNodeInfo,
        nodeGoiYFocus: AccessibilityNodeInfo? = null
    ): MucDieuHuong? {

        if (!node.isVisibleToUser) {
            return null
        }

        val nodeClick =
            timNodeXacNhanDieuHuong(
                node
            )
                ?: node

        val nodeFocus =
            nodeGoiYFocus
                ?.takeIf {
                    it.isVisibleToUser
                }
                ?: timNodeFocusSemantics(
                    nodeClick
                )

        val bounds =
            Rect().also {
                nodeClick.getBoundsInScreen(
                    it
                )
            }

        if (
            bounds.isEmpty ||
            bounds.width() <= 0 ||
            bounds.height() <= 0
        ) {
            return null
        }

        val nhan =
            layNhanNodeDieuHuong(
                nodeFocus
            )
                ?: layNhanNodeDieuHuong(
                    nodeClick
                )
                ?: timNhanTrongCayCon(
                    nodeClick
                )

        return MucDieuHuong(
            nodeFocus = nodeFocus,
            nodeClick = nodeClick,
            bounds = Rect(bounds),
            nhan = nhan
        )
    }

    private fun timNodeFocusSemantics(
        nodeGoc: AccessibilityNodeInfo
    ): AccessibilityNodeInfo {

        var nodeTotNhat =
            nodeGoc

        var diemTotNhat =
            chamDiemNodeFocusSemantics(
                nodeGoc
            )

        fun duyet(
            node: AccessibilityNodeInfo
        ) {
            if (!node.isVisibleToUser) {
                return
            }

            val diem =
                chamDiemNodeFocusSemantics(
                    node
                )

            if (diem > diemTotNhat) {
                diemTotNhat =
                    diem

                nodeTotNhat =
                    node
            }

            for (
            index in 0 until
                    node.childCount
            ) {
                val child =
                    node.getChild(
                        index
                    )
                        ?: continue

                duyet(
                    child
                )
            }
        }

        duyet(
            nodeGoc
        )

        return nodeTotNhat
    }

    private fun chamDiemNodeFocusSemantics(
        node: AccessibilityNodeInfo
    ): Int {

        if (!node.isVisibleToUser) {
            return Int.MIN_VALUE
        }

        var diem =
            0

        val coNhan =
            layNhanNodeDieuHuong(
                node
            ) != null

        if (coNhan) {
            diem +=
                500
        }

        if (
            hoTroHanhDong(
                node = node,
                action = AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS
            )
        ) {
            diem +=
                900
        }

        if (node.isFocusable) {
            diem +=
                600
        }

        val tenLop =
            node.className
                ?.toString()
                .orEmpty()

        if (
            tenLop.contains(
                "TextView",
                ignoreCase = true
            ) ||
            tenLop.contains(
                "Button",
                ignoreCase = true
            )
        ) {
            diem +=
                120
        }

        return diem
    }

    private fun layNhanNodeDieuHuong(
        node: AccessibilityNodeInfo
    ): String? {

        return node.contentDescription
            ?.toString()
            ?.trim()
            ?.takeIf {
                it.isNotBlank()
            }
            ?: node.text
                ?.toString()
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
    }

    private fun timNhanTrongCayCon(
        node: AccessibilityNodeInfo
    ): String? {

        val nhanHienTai =
            layNhanNodeDieuHuong(
                node
            )

        if (nhanHienTai != null) {
            return nhanHienTai
        }

        for (
        index in 0 until
                node.childCount
        ) {
            val child =
                node.getChild(
                    index
                )
                    ?: continue

            val nhan =
                timNhanTrongCayCon(
                    child
                )

            if (nhan != null) {
                return nhan
            }
        }

        return null
    }

    private fun datFocusVaoMuc(
        muc: MucDieuHuong,
        tenHuong: String,
        nguon: String
    ): Boolean {

        if (
            thuDatFocusVaoNode(
                node = muc.nodeFocus,
                tenHuong = tenHuong,
                nguon = "$nguon/FOCUS_NODE"
            )
        ) {
            anFocusDieuHuongOverlayNoiBo()

            Log.d(
                TAG_FOCUS,
                "SEMANTIC_FOCUS[$tenHuong] | " +
                        "label=${muc.nhan ?: "NONE"} | native=FOCUS_NODE"
            )

            return true
        }

        if (
            !laCungNode(
                a = muc.nodeFocus,
                b = muc.nodeClick
            ) &&
            thuDatFocusVaoNode(
                node = muc.nodeClick,
                tenHuong = tenHuong,
                nguon = "$nguon/CLICK_NODE"
            )
        ) {
            anFocusDieuHuongOverlayNoiBo()

            Log.d(
                TAG_FOCUS,
                "SEMANTIC_FOCUS[$tenHuong] | " +
                        "label=${muc.nhan ?: "NONE"} | native=CLICK_NODE"
            )

            return true
        }

        val virtualFocusThanhCong =
            hienThiFocusDieuHuongOverlayNoiBo(
                muc.nodeClick
            )

        Log.d(
            TAG_FOCUS,
            "VIRTUAL_FOCUS[$tenHuong] | " +
                    "nguon=$nguon | " +
                    "label=${muc.nhan ?: "NONE"} | " +
                    "overlay=$virtualFocusThanhCong"
        )

        return virtualFocusThanhCong
    }

    private fun thuDatFocusVaoNode(
        node: AccessibilityNodeInfo,
        tenHuong: String,
        nguon: String
    ): Boolean {

        if (!node.isVisibleToUser) {
            return false
        }

        val accessibilityThanhCong =
            node.performAction(
                AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS
            )

        if (accessibilityThanhCong) {
            Log.d(
                TAG_FOCUS,
                "THANH_CONG[$tenHuong] | " +
                        "nguon=$nguon | ACTION_ACCESSIBILITY_FOCUS"
            )

            return true
        }

        val systemFocusThanhCong =
            node.performAction(
                AccessibilityNodeInfo.ACTION_FOCUS
            )

        if (systemFocusThanhCong) {
            Log.d(
                TAG_FOCUS,
                "THANH_CONG[$tenHuong] | " +
                        "nguon=$nguon | ACTION_FOCUS"
            )
        }

        return systemFocusThanhCong
    }

    private fun hienThiFocusDieuHuongOverlayNoiBo(
        node: AccessibilityNodeInfo
    ): Boolean {

        val bounds =
            Rect().also {
                node.getBoundsInScreen(
                    it
                )
            }

        if (
            bounds.isEmpty ||
            bounds.width() <= 0 ||
            bounds.height() <= 0
        ) {
            return false
        }

        val windowManager =
            windowManagerFocusDieuHuong
                ?: (
                        getSystemService(
                            WINDOW_SERVICE
                        ) as? WindowManager
                        )
                    ?.also {
                        windowManagerFocusDieuHuong =
                            it
                    }
                ?: return false

        val focusView =
            viewFocusDieuHuong
                ?: taoViewFocusDieuHuong()
                    .also {
                        viewFocusDieuHuong =
                            it
                    }

        val params =
            WindowManager.LayoutParams(
                bounds.width(),
                bounds.height(),
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
                    bounds.left

                y =
                    bounds.top
            }

        return try {
            if (focusView.isAttachedToWindow) {
                windowManager.updateViewLayout(
                    focusView,
                    params
                )
            } else {
                windowManager.addView(
                    focusView,
                    params
                )
            }

            true
        } catch (
            exception: Exception
        ) {
            Log.w(
                TAG_FOCUS,
                "Khong the hien thi virtual focus overlay",
                exception
            )

            false
        }
    }

    private fun taoViewFocusDieuHuong():
            View {

        val nen =
            GradientDrawable().apply {
                shape =
                    GradientDrawable.RECTANGLE

                cornerRadius =
                    dp(
                        8
                    ).toFloat()

                setColor(
                    android.graphics.Color.TRANSPARENT
                )

                setStroke(
                    dp(
                        2
                    ),
                    ContextCompat.getColor(
                        this@DichVuTruyCapFaceAccess,
                        R.color.xanh_chinh
                    )
                )
            }

        return View(
            this
        ).apply {
            background =
                nen
        }
    }

    private fun anFocusDieuHuongOverlayNoiBo() {

        val focusView =
            viewFocusDieuHuong
                ?: return

        val windowManager =
            windowManagerFocusDieuHuong

        if (
            focusView.isAttachedToWindow &&
            windowManager != null
        ) {
            try {
                windowManager.removeView(
                    focusView
                )
            } catch (
                exception: Exception
            ) {
                Log.w(
                    TAG_FOCUS,
                    "Khong the remove virtual focus overlay",
                    exception
                )
            }
        }

        viewFocusDieuHuong =
            null
    }

    private fun luuNodeDieuHuongDangChon(
        node: AccessibilityNodeInfo
    ) {

        val bounds =
            Rect().also {
                node.getBoundsInScreen(
                    it
                )
            }

        dauVetNodeDieuHuongDangChon =
            DauVetNodeDieuHuong(
                windowId = node.windowId,
                bounds = Rect(bounds),
                viewId = node.viewIdResourceName,
                className = node.className
                    ?.toString(),
                text = node.text
                    ?.toString(),
                contentDescription =
                    node.contentDescription
                        ?.toString()
            )
    }

    private fun timViTriTheoDauVet(
        danhSachMuc: List<MucDieuHuong>,
        dauVet: DauVetNodeDieuHuong?
    ): Int {

        if (dauVet == null) {
            return -1
        }

        return danhSachMuc.indexOfFirst {
            laNodeKhopDauVet(
                node = it.nodeClick,
                dauVet = dauVet
            )
        }
    }

    private fun timNodeTheoDauVet(
        root: AccessibilityNodeInfo,
        dauVet: DauVetNodeDieuHuong?
    ): AccessibilityNodeInfo? {

        if (
            dauVet == null ||
            root.windowId !=
            dauVet.windowId
        ) {
            return null
        }

        if (
            laNodeKhopDauVet(
                node = root,
                dauVet = dauVet
            )
        ) {
            return root
        }

        for (
        index in 0 until
                root.childCount
        ) {
            val child =
                root.getChild(
                    index
                )
                    ?: continue

            val ketQua =
                timNodeTheoDauVet(
                    root = child,
                    dauVet = dauVet
                )

            if (ketQua != null) {
                return ketQua
            }
        }

        return null
    }

    private fun laNodeKhopDauVet(
        node: AccessibilityNodeInfo,
        dauVet: DauVetNodeDieuHuong
    ): Boolean {

        if (
            node.windowId !=
            dauVet.windowId
        ) {
            return false
        }

        val bounds =
            Rect().also {
                node.getBoundsInScreen(
                    it
                )
            }

        if (
            !dauVet.viewId.isNullOrBlank() &&
            node.viewIdResourceName ==
            dauVet.viewId &&
            bounds ==
            dauVet.bounds
        ) {
            return true
        }

        return bounds ==
                dauVet.bounds &&
                node.className
                    ?.toString() ==
                dauVet.className &&
                node.text
                    ?.toString() ==
                dauVet.text &&
                node.contentDescription
                    ?.toString() ==
                dauVet.contentDescription
    }

    private fun timViTriMucDangFocus(
        nodeDangFocus: AccessibilityNodeInfo?,
        danhSachMuc: List<MucDieuHuong>
    ): Int {

        if (nodeDangFocus == null) {
            return -1
        }

        var nodeHienTai:
                AccessibilityNodeInfo? =
            nodeDangFocus

        repeat(
            SO_CAP_PARENT_FOCUS_TOI_DA
        ) {
            val hienTai =
                nodeHienTai
                    ?: return@repeat

            val viTri =
                danhSachMuc.indexOfFirst {
                    laCungNode(
                        a = it.nodeFocus,
                        b = hienTai
                    ) ||
                            laCungNode(
                                a = it.nodeClick,
                                b = hienTai
                            )
                }

            if (viTri >= 0) {
                return viTri
            }

            nodeHienTai =
                hienTai.parent
        }

        return -1
    }

    private fun laCungNode(
        a: AccessibilityNodeInfo,
        b: AccessibilityNodeInfo
    ): Boolean {

        if (a == b) {
            return true
        }

        if (a.windowId != b.windowId) {
            return false
        }

        val boundsA =
            Rect().also {
                a.getBoundsInScreen(
                    it
                )
            }

        val boundsB =
            Rect().also {
                b.getBoundsInScreen(
                    it
                )
            }

        if (boundsA != boundsB) {
            return false
        }

        val idA =
            a.viewIdResourceName

        val idB =
            b.viewIdResourceName

        if (
            !idA.isNullOrBlank() &&
            !idB.isNullOrBlank()
        ) {
            return idA == idB
        }

        return a.className ==
                b.className &&
                a.text ==
                b.text &&
                a.contentDescription ==
                b.contentDescription
    }

    private fun tinhViTriDich(
        huong: Int,
        viTriHienTai: Int,
        soLuong: Int
    ): Int {

        if (soLuong <= 1) {
            return 0
        }

        if (viTriHienTai < 0) {
            return if (
                huong ==
                View.FOCUS_FORWARD
            ) {
                0
            } else {
                soLuong - 1
            }
        }

        return if (
            huong ==
            View.FOCUS_FORWARD
        ) {
            (
                    viTriHienTai + 1
                    ) % soLuong
        } else {
            (
                    viTriHienTai - 1 +
                            soLuong
                    ) % soLuong
        }
    }

    private fun laNodeUngVienDieuHuong(
        node: AccessibilityNodeInfo
    ): Boolean {

        if (!node.isVisibleToUser) {
            return false
        }

        val coNhan =
            layNhanNodeDieuHuong(
                node
            ) != null

        val laDichTuongTacTrucTiep =
            coActionClick(
                node
            ) ||
                    node.isCheckable ||
                    node.isEditable

        val laFocusableCoYNgia =
            node.isFocusable &&
                    coNhan &&
                    !node.isScrollable

        return laDichTuongTacTrucTiep ||
                laFocusableCoYNgia
    }

    private fun coActionClick(
        node: AccessibilityNodeInfo
    ): Boolean {

        return node.isClickable ||
                node.actionList.any {
                    it.id ==
                            AccessibilityNodeInfo.ACTION_CLICK
                }
    }

    private fun thuThapNodeUngVienDieuHuong(
        node: AccessibilityNodeInfo,
        ketQua: MutableList<AccessibilityNodeInfo>
    ) {

        if (!node.isVisibleToUser) {
            return
        }

        if (
            laNodeUngVienDieuHuong(
                node
            )
        ) {
            ketQua.add(
                node
            )
        }

        for (
        index in 0 until
                node.childCount
        ) {
            val child =
                node.getChild(
                    index
                )
                    ?: continue

            thuThapNodeUngVienDieuHuong(
                node = child,
                ketQua = ketQua
            )
        }
    }

    // VERTICAL SCROLL NAVIGATION

    private fun thucThiCuonLenNoiBo(): Boolean {

        return thucThiCuonDoc(
            cuonXuong = false
        )
    }

    private fun thucThiCuonXuongNoiBo(): Boolean {

        return thucThiCuonDoc(
            cuonXuong = true
        )
    }

    private fun thucThiCuonDoc(
        cuonXuong: Boolean
    ): Boolean {

        val tenHuong =
            if (cuonXuong) {
                "DOWN"
            } else {
                "UP"
            }

        if (dangCuonBangCuChi) {

            Log.d(
                TAG_SCROLL,
                "BO_QUA[$tenHuong]: animation scroll dang chay"
            )

            return true
        }

        val gestureDaNhan =
            thucThiCuonBangCuChi(
                cuonXuong = cuonXuong
            )

        if (gestureDaNhan) {

            return true
        }

        Log.d(
            TAG_SCROLL,
            "FALLBACK[$tenHuong]: dispatchGesture=false -> ACTION_SCROLL"
        )

        return thucThiCuonBangNode(
            cuonXuong = cuonXuong
        )
    }

    private fun thucThiCuonBangCuChi(
        cuonXuong: Boolean
    ): Boolean {

        val metrics =
            resources.displayMetrics

        val chieuRong =
            metrics.widthPixels.toFloat()

        val chieuCao =
            metrics.heightPixels.toFloat()

        if (
            chieuRong <= 0f ||
            chieuCao <= 0f
        ) {

            Log.e(
                TAG_SCROLL,
                "THAT_BAI_GESTURE: kich thuoc man hinh khong hop le"
            )

            return false
        }

        val x =
            chieuRong * 0.50f

        val yBatDau =
            if (cuonXuong) {

                chieuCao *
                        TY_LE_Y_BAT_DAU_CUON_XUONG

            } else {

                chieuCao *
                        TY_LE_Y_KET_THUC_CUON_XUONG
            }

        val yKetThuc =
            if (cuonXuong) {

                chieuCao *
                        TY_LE_Y_KET_THUC_CUON_XUONG

            } else {

                chieuCao *
                        TY_LE_Y_BAT_DAU_CUON_XUONG
            }

        val path =
            Path().apply {

                moveTo(
                    x,
                    yBatDau
                )

                lineTo(
                    x,
                    yKetThuc
                )
            }

        val stroke =
            GestureDescription
                .StrokeDescription(
                    path,
                    0L,
                    THOI_GIAN_CUON_MUOT_MS
                )

        val gesture =
            GestureDescription
                .Builder()
                .addStroke(
                    stroke
                )
                .build()

        val tenHuong =
            if (cuonXuong) {
                "DOWN"
            } else {
                "UP"
            }

        dangCuonBangCuChi =
            true

        val daNhan =
            dispatchGesture(
                gesture,
                object :
                    GestureResultCallback() {

                    override fun onCompleted(
                        gestureDescription:
                        GestureDescription?
                    ) {

                        dangCuonBangCuChi =
                            false

                        Log.d(
                            TAG_SCROLL,
                            "THANH_CONG[$tenHuong]: SMOOTH_GESTURE_COMPLETED"
                        )
                    }

                    override fun onCancelled(
                        gestureDescription:
                        GestureDescription?
                    ) {

                        dangCuonBangCuChi =
                            false

                        Log.e(
                            TAG_SCROLL,
                            "CANCEL[$tenHuong]: smooth gesture bi huy -> thu ACTION_SCROLL"
                        )

                        thucThiCuonBangNode(
                            cuonXuong = cuonXuong
                        )
                    }
                },
                null
            )

        if (!daNhan) {

            dangCuonBangCuChi =
                false

            Log.e(
                TAG_SCROLL,
                "THAT_BAI[$tenHuong]: dispatchGesture() tra ve false"
            )
        } else {

            Log.d(
                TAG_SCROLL,
                "BAT_DAU[$tenHuong]: SMOOTH_GESTURE " +
                        "y=$yBatDau -> $yKetThuc | " +
                        "duration=${THOI_GIAN_CUON_MUOT_MS}ms"
            )
        }

        return daNhan
    }

    private fun thucThiCuonBangNode(
        cuonXuong: Boolean
    ): Boolean {

        val root =
            rootInActiveWindow

        if (root == null) {

            Log.e(
                TAG_SCROLL,
                "THAT_BAI_NODE: rootInActiveWindow = null"
            )

            return false
        }

        val actionDoc =
            if (cuonXuong) {

                AccessibilityNodeInfo
                    .AccessibilityAction
                    .ACTION_SCROLL_DOWN
                    .id

            } else {

                AccessibilityNodeInfo
                    .AccessibilityAction
                    .ACTION_SCROLL_UP
                    .id
            }

        val actionFallback =
            if (cuonXuong) {

                AccessibilityNodeInfo
                    .ACTION_SCROLL_FORWARD

            } else {

                AccessibilityNodeInfo
                    .ACTION_SCROLL_BACKWARD
            }

        val tenHuong =
            if (cuonXuong) {
                "DOWN"
            } else {
                "UP"
            }

        val accessibilityFocus =
            root.findFocus(
                AccessibilityNodeInfo.FOCUS_ACCESSIBILITY
            )

        val inputFocus =
            root.findFocus(
                AccessibilityNodeInfo.FOCUS_INPUT
            )

        val nodeDangFocus =
            accessibilityFocus
                ?: inputFocus

        val nodeCuonTuFocus =
            timNodeCuonTuNodeDangFocus(
                nodeBatDau = nodeDangFocus,
                actionDoc = actionDoc,
                actionFallback = actionFallback
            )

        val nodeCuon =
            nodeCuonTuFocus
                ?: timNodeCuonTrongCay(
                    node = root,
                    actionDoc = actionDoc,
                    actionFallback = actionFallback
                )

        if (nodeCuon == null) {

            Log.e(
                TAG_SCROLL,
                "THAT_BAI[$tenHuong]: khong tim thay scroll container phu hop"
            )

            return false
        }

        Log.d(
            TAG_SCROLL,
            "NODE_CUON[$tenHuong] | " +
                    "class=${nodeCuon.className} | " +
                    "scrollable=${nodeCuon.isScrollable} | " +
                    "tuFocus=${nodeCuonTuFocus != null}"
        )

        if (
            hoTroHanhDong(
                node = nodeCuon,
                action = actionDoc
            )
        ) {

            val thanhCong =
                nodeCuon.performAction(
                    actionDoc
                )

            if (thanhCong) {

                Log.d(
                    TAG_SCROLL,
                    "THANH_CONG[$tenHuong]: ACTION_SCROLL_DOC"
                )

                return true
            }

            Log.d(
                TAG_SCROLL,
                "FALLBACK[$tenHuong]: ACTION_SCROLL_DOC=false"
            )
        }

        if (
            hoTroHanhDong(
                node = nodeCuon,
                action = actionFallback
            )
        ) {

            val thanhCong =
                nodeCuon.performAction(
                    actionFallback
                )

            if (thanhCong) {

                Log.d(
                    TAG_SCROLL,
                    "THANH_CONG[$tenHuong]: ACTION_SCROLL_FALLBACK"
                )

            } else {

                Log.e(
                    TAG_SCROLL,
                    "THAT_BAI[$tenHuong]: performAction scroll tra ve false"
                )
            }

            return thanhCong
        }

        Log.e(
            TAG_SCROLL,
            "THAT_BAI[$tenHuong]: node khong ho tro action cuon can thiet"
        )

        return false
    }

    private fun timNodeCuonTuNodeDangFocus(
        nodeBatDau: AccessibilityNodeInfo?,
        actionDoc: Int,
        actionFallback: Int
    ): AccessibilityNodeInfo? {

        var nodeHienTai =
            nodeBatDau

        while (nodeHienTai != null) {

            if (
                nodeHienTai.isVisibleToUser &&
                coTheCuonTheoHuong(
                    node = nodeHienTai,
                    actionDoc = actionDoc,
                    actionFallback = actionFallback
                )
            ) {

                return nodeHienTai
            }

            nodeHienTai =
                nodeHienTai.parent
        }

        return null
    }

    private fun timNodeCuonTrongCay(
        node: AccessibilityNodeInfo,
        actionDoc: Int,
        actionFallback: Int
    ): AccessibilityNodeInfo? {

        if (!node.isVisibleToUser) {
            return null
        }

        if (
            coTheCuonTheoHuong(
                node = node,
                actionDoc = actionDoc,
                actionFallback = actionFallback
            )
        ) {

            return node
        }

        for (
        index in 0 until
                node.childCount
        ) {

            val child =
                node.getChild(
                    index
                )
                    ?: continue

            val ketQua =
                timNodeCuonTrongCay(
                    node = child,
                    actionDoc = actionDoc,
                    actionFallback = actionFallback
                )

            if (ketQua != null) {
                return ketQua
            }
        }

        return null
    }

    private fun coTheCuonTheoHuong(
        node: AccessibilityNodeInfo,
        actionDoc: Int,
        actionFallback: Int
    ): Boolean {

        return node.isScrollable &&
                (
                        hoTroHanhDong(
                            node = node,
                            action = actionDoc
                        ) ||
                                hoTroHanhDong(
                                    node = node,
                                    action = actionFallback
                                )
                        )
    }

    private fun hoTroHanhDong(
        node: AccessibilityNodeInfo,
        action: Int
    ): Boolean {

        return node.actionList.any {
                accessibilityAction ->

            accessibilityAction.id ==
                    action
        }
    }

    private class BoQuanLyTrangThaiOverlayNoiBo(
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
            } catch (
                exception: Exception
            ) {
                Log.w(
                    TAG_THONG_BAO,
                    "Khong the hien thi status overlay",
                    exception
                )

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
                } catch (
                    exception: Exception
                ) {
                    Log.w(
                        TAG_THONG_BAO,
                        "Khong the an status overlay",
                        exception
                    )

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
                            dpNoiBo(
                                6
                            )
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
                    dpNoiBo(
                        16
                    )

                y =
                    layChieuCaoThanhTrangThai() +
                            dpNoiBo(
                                6
                            )
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
                dpNoiBo(
                    24
                )
            }
        }

        private fun dpNoiBo(
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

    companion object {
        private const val TAG_CON_TRO = "FaceAccessCursorTarget"

        private const val THOI_GIAN_TAP_CON_TRO_MS =
            60L

        private const val THOI_GIAN_TAP_DIEU_HUONG_MS =
            70L

        private const val KHOANG_TAI_NHAN_DIEN_TARGET_DP =
            48

        private const val TY_LE_VUOT_THAP =
            0.20f

        private const val TY_LE_VUOT_CAO =
            0.80f

        private const val THOI_GIAN_VUOT_CON_TRO_MS =
            300L

        private const val SO_CAP_PARENT_CLICK_TOI_DA =
            6

        private const val SO_CAP_PARENT_FOCUS_TOI_DA =
            8

        private const val TAG =
            "DichVuTruyCap"

        private const val TAG_FOCUS =
            "FocusNavigation"

        private const val TAG_SCROLL =
            "ScrollNavigation"

        private const val TAG_DIALER =
            "SupportDialer"

        private const val TAG_THONG_BAO =
            "FaceAccessFeedback"

        private const val THOI_GIAN_HIEN_THONG_BAO_MS =
            1400L

        private const val TY_LE_Y_BAT_DAU_CUON_XUONG =
            0.68f

        private const val TY_LE_Y_KET_THUC_CUON_XUONG =
            0.38f

        private const val THOI_GIAN_CUON_MUOT_MS =
            300L

        @Volatile
        private var phienBanDangHoatDong:
                DichVuTruyCapFaceAccess? = null

        @Volatile
        private var trangThaiOverlayDangBat =
            false

        @Volatile
        private var trangThaiOverlayCoKhuonMat =
            false

        @Volatile
        private var trangThaiOverlayCheDo =
            CheDoDieuKhien.DIEU_HUONG

        fun dangHoatDong(): Boolean {

            return phienBanDangHoatDong != null
        }

        fun batTrangThaiOverlay(
            cheDo: CheDoDieuKhien
        ): Boolean {
            trangThaiOverlayDangBat =
                true

            trangThaiOverlayCheDo =
                cheDo

            trangThaiOverlayCoKhuonMat =
                false

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .dongBoTrangThaiOverlayNoiBo()
        }

        fun tatTrangThaiOverlay(): Boolean {
            trangThaiOverlayDangBat =
                false

            trangThaiOverlayCoKhuonMat =
                false

            val dichVu =
                phienBanDangHoatDong
                    ?: return true

            return dichVu
                .dongBoTrangThaiOverlayNoiBo()
        }

        fun capNhatKhuonMatTrangThaiOverlay(
            coKhuonMat: Boolean
        ): Boolean {
            trangThaiOverlayCoKhuonMat =
                coKhuonMat

            if (!trangThaiOverlayDangBat) {
                return true
            }

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .dongBoTrangThaiOverlayNoiBo()
        }

        fun capNhatCheDoTrangThaiOverlay(
            cheDo: CheDoDieuKhien
        ): Boolean {
            trangThaiOverlayCheDo =
                cheDo

            if (!trangThaiOverlayDangBat) {
                return true
            }

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .dongBoTrangThaiOverlayNoiBo()
        }

        fun batConTro(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu.batConTroNoiBo()
        }

        fun tatConTro(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu.tatConTroNoiBo()
        }

        fun doiKhoaConTro(): Boolean {
            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .doiKhoaConTroNoiBo()
        }

        fun thucThiDiChuyenConTro(
            lenh: LenhConTro
        ): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu.diChuyenConTroNoiBo(
                lenh
            )
        }

        fun thucThiClickConTro(): Boolean {
            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiClickConTroNoiBo()
        }

        fun hienThiThongBaoHeThong(
            noiDung: String
        ): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .hienThiThongBaoHeThongNoiBo(
                    noiDung
                )
        }

        fun layPackageDangHoatDong():
                String? {

            val dichVu =
                phienBanDangHoatDong
                    ?: return null

            return dichVu
                .rootInActiveWindow
                ?.packageName
                ?.toString()
        }

        fun thucThiBatDauCuocGoiTrenDialer(
            packageDialerMongDoi: String?
        ): Boolean {
            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .batDauCuocGoiTrenDialerNoiBo(
                    packageDialerMongDoi
                )
        }

        fun thucThiKetThucCuocGoiNeuDangCo(): Boolean {
            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .ketThucCuocGoiNeuDangCoNoiBo()
        }

        fun dangCoCuocGoiDangHienThi(): Boolean {
            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .dangCoCuocGoiDangHienThiNoiBo()
        }

        fun thucThiXoaSoTrinhQuaySo(
            packageDialerMongDoi: String?,
            soDienThoai: String
        ): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .xoaSoTrinhQuaySoNoiBo(
                    packageDialerMongDoi =
                        packageDialerMongDoi,
                    soDienThoai =
                        soDienThoai
                )
        }

        fun thucThiHome(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiHomeNoiBo()
        }

        fun thucThiBack(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiBackNoiBo()
        }

        fun thucThiTiepTheo(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiTiepTheoNoiBo()
        }

        fun thucThiTruoc(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiTruocNoiBo()
        }

        fun thucThiXacNhanDieuHuong(): Boolean {
            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiXacNhanDieuHuongNoiBo()
        }

        fun thucThiCuonLen(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiCuonLenNoiBo()
        }

        fun thucThiCuonXuong(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiCuonXuongNoiBo()
        }
    }
}
