package com.example.faceaccess.v2.truycap

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
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
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.faceaccess.v2.R
import com.example.faceaccess.v2.contro.BoQuanLyConTroOverlay
import com.example.faceaccess.v2.contro.BoChonMucTieuConTro
import com.example.faceaccess.v2.dieuphoi.contro.LenhConTro
import kotlin.math.abs

class DichVuTruyCapFaceAccess : AccessibilityService() {

    @Volatile
    private var dangCuonBangCuChi =
        false

    // SYSTEM FEEDBACK OVERLAY

    // CURSOR OVERLAY

    private lateinit var boQuanLyConTroOverlay:
            BoQuanLyConTroOverlay

    private val boChonMucTieuConTro =
        BoChonMucTieuConTro()

    private var mucTieuConTroDangChon:
            BoChonMucTieuConTro.KetQua? =
        null

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

        phienBanDangHoatDong = this

        if (
            !::boQuanLyConTroOverlay.isInitialized
        ) {
            boQuanLyConTroOverlay =
                BoQuanLyConTroOverlay(this)
        }

        Log.d(
            TAG,
            "Dich vu truy cap da ket noi"
        )
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {

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

        if (
            ::boQuanLyConTroOverlay.isInitialized
        ) {
            boQuanLyConTroOverlay.dong()
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
        }

        return thanhCong
    }

    private fun tatConTroNoiBo(): Boolean {
        mucTieuConTroDangChon =
            null

        if (
            !::boQuanLyConTroOverlay.isInitialized
        ) {
            return true
        }

        return boQuanLyConTroOverlay.tat()
    }

    private fun diChuyenConTroNoiBo(
        lenh: LenhConTro
    ): Boolean {
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

        val mucTieu =
            rootInActiveWindow
                ?.let { root ->
                    boChonMucTieuConTro.timMucTieu(
                        root = root,
                        viTriConTro = viTriConTro,
                        lenh = lenh,
                        chieuRongManHinh = metrics.widthPixels,
                        chieuCaoManHinh = metrics.heightPixels,
                        matDo = metrics.density
                    )
                }

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
            "MOVE=$lenh | TARGET=${mucTieu?.nhan ?: "NONE"} | OK=$thanhCong"
        )

        return thanhCong
    }

    private fun thucThiClickConTroNoiBo(): Boolean {
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
                rootInActiveWindow

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
                    "CLICK_TARGET_NODE | ${mucTieu.nhan ?: mucTieu.viewId ?: "UNKNOWN"}"
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

            if (
                hienTai.isVisibleToUser &&
                hienTai.isClickable
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

    private fun diChuyenAccessibilityFocus(
        huong: Int
    ): Boolean {

        val tenHuong =
            when (huong) {

                View.FOCUS_FORWARD ->
                    "FORWARD"

                View.FOCUS_BACKWARD ->
                    "BACKWARD"

                else ->
                    huong.toString()
            }

        val root =
            rootInActiveWindow

        if (root == null) {

            Log.e(
                TAG_FOCUS,
                "THAT_BAI[$tenHuong]: rootInActiveWindow = null"
            )

            return false
        }

        val danhSachNode =
            mutableListOf<AccessibilityNodeInfo>()

        thuThapNodeCoTheDieuHuong(
            node = root,
            ketQua = danhSachNode
        )

        Log.d(
            TAG_FOCUS,
            "BAT_DAU[$tenHuong] | soNode=${danhSachNode.size}"
        )

        if (danhSachNode.isEmpty()) {

            Log.e(
                TAG_FOCUS,
                "THAT_BAI[$tenHuong]: khong co node co the dieu huong"
            )

            return false
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

        val viTriHienTai =
            if (nodeDangFocus == null) {

                -1

            } else {

                danhSachNode.indexOfFirst { node ->

                    node == nodeDangFocus
                }
            }

        Log.d(
            TAG_FOCUS,
            "FOCUS_HIEN_TAI[$tenHuong] | " +
                    "accessibility=${accessibilityFocus != null} | " +
                    "input=${inputFocus != null} | " +
                    "index=$viTriHienTai"
        )

        val viTriDich =
            when (huong) {

                View.FOCUS_FORWARD -> {

                    if (viTriHienTai < 0) {

                        0

                    } else {

                        viTriHienTai + 1
                    }
                }

                View.FOCUS_BACKWARD -> {

                    if (viTriHienTai < 0) {

                        danhSachNode.lastIndex

                    } else {

                        viTriHienTai - 1
                    }
                }

                else -> {

                    Log.e(
                        TAG_FOCUS,
                        "THAT_BAI[$tenHuong]: huong focus khong hop le"
                    )

                    return false
                }
            }

        if (
            viTriDich < 0 ||
            viTriDich > danhSachNode.lastIndex
        ) {

            Log.d(
                TAG_FOCUS,
                "HET_DANH_SACH[$tenHuong] | viTriHienTai=$viTriHienTai"
            )

            return false
        }

        accessibilityFocus?.performAction(
            AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS
        )

        inputFocus?.performAction(
            AccessibilityNodeInfo.ACTION_CLEAR_FOCUS
        )

        val nodeDich =
            danhSachNode[
                viTriDich
            ]

        Log.d(
            TAG_FOCUS,
            "NODE_DICH[$tenHuong] | " +
                    "index=$viTriDich/${danhSachNode.lastIndex} | " +
                    "class=${nodeDich.className} | " +
                    "text=${nodeDich.text} | " +
                    "desc=${nodeDich.contentDescription} | " +
                    "clickable=${nodeDich.isClickable} | " +
                    "focusable=${nodeDich.isFocusable}"
        )

        val accessibilityThanhCong =
            nodeDich.performAction(
                AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS
            )

        if (accessibilityThanhCong) {

            Log.d(
                TAG_FOCUS,
                "THANH_CONG[$tenHuong]: ACTION_ACCESSIBILITY_FOCUS"
            )

            return true
        }

        Log.d(
            TAG_FOCUS,
            "FALLBACK[$tenHuong]: ACTION_ACCESSIBILITY_FOCUS=false -> thu ACTION_FOCUS"
        )

        val systemFocusThanhCong =
            nodeDich.performAction(
                AccessibilityNodeInfo.ACTION_FOCUS
            )

        if (systemFocusThanhCong) {

            Log.d(
                TAG_FOCUS,
                "THANH_CONG[$tenHuong]: ACTION_FOCUS"
            )

        } else {

            Log.e(
                TAG_FOCUS,
                "THAT_BAI[$tenHuong]: ca ACTION_ACCESSIBILITY_FOCUS va ACTION_FOCUS deu false"
            )
        }

        return systemFocusThanhCong
    }

    private fun thuThapNodeCoTheDieuHuong(
        node: AccessibilityNodeInfo,
        ketQua: MutableList<AccessibilityNodeInfo>
    ) {

        if (!node.isVisibleToUser) {
            return
        }

        val coNhan =
            !node.text.isNullOrBlank() ||
                    !node.contentDescription.isNullOrBlank()

        val laDichTuongTacTrucTiep =
            node.isClickable ||
                    node.isCheckable ||
                    node.isEditable

        val laFocusableCoYNgia =
            node.isFocusable &&
                    coNhan &&
                    !node.isScrollable

        val coTheDieuHuong =
            laDichTuongTacTrucTiep ||
                    laFocusableCoYNgia

        if (coTheDieuHuong) {

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

            thuThapNodeCoTheDieuHuong(
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

    companion object {
        private const val TAG_CON_TRO = "FaceAccessCursorTarget"

        private const val THOI_GIAN_TAP_CON_TRO_MS =
            60L

        private const val KHOANG_TAI_NHAN_DIEN_TARGET_DP =
            48

        private const val SO_CAP_PARENT_CLICK_TOI_DA =
            6

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

        fun dangHoatDong(): Boolean {

            return phienBanDangHoatDong != null
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