package com.example.faceaccess.v2.truycap

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
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

class DichVuTruyCapFaceAccess : AccessibilityService() {

    /**
     * Chặn hai animation scroll chồng lên nhau.
     *
     * Detector PITCH đã one-shot + neutral re-arm, nhưng guard này
     * vẫn cần để bảo vệ khi người dùng re-arm rất nhanh hoặc khi
     * callback từ foreground/background đến gần nhau.
     */
    @Volatile
    private var dangCuonBangCuChi =
        false

    // =========================================================
    // SYSTEM FEEDBACK OVERLAY
    // =========================================================

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

        Log.d(
            TAG,
            "Dich vu truy cap da ket noi"
        )
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {
        /*
         * FaceAccess hiện không cần đọc AccessibilityEvent.
         *
         * Service chỉ dùng performGlobalAction()
         * cho các thao tác điều hướng Android.
         */
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

        if (phienBanDangHoatDong === this) {
            phienBanDangHoatDong = null
        }

        Log.d(
            TAG,
            "Dich vu truy cap da dung"
        )

        super.onDestroy()
    }


    // =========================================================
    // FEEDBACK OVERLAY - KHÔNG DÙNG TOAST
    // =========================================================

    /**
     * Hiển thị banner hệ thống của FaceAccess trên mọi ứng dụng.
     *
     * TYPE_ACCESSIBILITY_OVERLAY:
     * - không cần SYSTEM_ALERT_WINDOW;
     * - chỉ hoạt động khi AccessibilityService FaceAccess đang bật;
     * - không nhận touch/focus nên không chặn người dùng.
     *
     * Mỗi command mới cập nhật text NGAY LẬP TỨC và reset thời gian ẩn.
     * Vì vậy YAW:
     *
     * Mẹ -> Bố -> Anh
     *
     * sẽ lần lượt cập nhật:
     *
     * Đã chuyển sang số của Mẹ
     * Đã chuyển sang số của Bố
     * Đã chuyển sang số của Anh
     *
     * Không phụ thuộc queue/rate-limit của Android Toast.
     */
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


        /*
         * Nếu banner đang hiện, chỉ update nội dung.
         * Nếu chưa hiện, add vào accessibility overlay.
         */
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


        /*
         * Mỗi event mới phải được nhìn thấy ngay.
         * Không xếp hàng như Toast.
         */
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


    // =========================================================
    // GLOBAL ACTIONS
    // =========================================================

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


    // =========================================================
    // DIALER - XÓA SỐ NHƯNG GIỮ APP ĐIỆN THOẠI
    // =========================================================

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


        /*
         * Không bao giờ thao tác node của app khác.
         */
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


        /*
         * CÁCH 1:
         * Tìm field số hỗ trợ ACTION_SET_TEXT và set = "".
         */
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


            /*
             * Nếu field đang có một số khác hẳn thì không chạm vào nó.
             */
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


        /*
         * CÁCH 2:
         * Chỉ tìm Backspace/Delete sau khi xác nhận đúng số đang xuất hiện
         * trong cây Accessibility của Dialer.
         */
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


        /*
         * Nhiều Dialer, trong đó có các bản Samsung,
         * dùng nhấn giữ Backspace để xóa toàn bộ.
         */
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


        /*
         * Fallback:
         * click Backspace đúng số chữ số.
         *
         * Re-fetch node mỗi lần để tránh stale AccessibilityNodeInfo.
         */
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


    // =========================================================
    // ACCESSIBILITY FOCUS NAVIGATION
    // =========================================================

    /**
     * Chuyển Accessibility Focus tới phần tử tiếp theo.
     *
     * Không dùng performGlobalAction() vì Android không có
     * global action NEXT/PREVIOUS. Ta di chuyển trực tiếp
     * trên cây AccessibilityNodeInfo.
     */
    private fun thucThiTiepTheoNoiBo(): Boolean {

        return diChuyenAccessibilityFocus(
            huong = View.FOCUS_FORWARD
        )
    }


    /**
     * Chuyển Accessibility Focus về phần tử trước đó.
     */
    private fun thucThiTruocNoiBo(): Boolean {

        return diChuyenAccessibilityFocus(
            huong = View.FOCUS_BACKWARD
        )
    }


    /**
     * Tìm node đang được Accessibility Focus.
     *
     * Nếu chưa có Accessibility Focus, thử Input Focus.
     * Nếu vẫn chưa có thì dùng root làm điểm bắt đầu.
     */
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


        /*
         * KHÔNG dùng focusSearch() từ root nữa.
         *
         * Trên nhiều màn hình Android, đặc biệt khi chưa có
         * Accessibility Focus ban đầu, root.focusSearch()
         * có thể trả về null dù cây UI vẫn có đầy đủ node.
         *
         * Thay vào đó:
         * 1. Duyệt cây Accessibility.
         * 2. Thu thập các node có thể tương tác.
         * 3. Tìm node đang Accessibility Focus.
         * 4. Chọn phần tử trước / tiếp theo theo thứ tự cây.
         */
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

        /*
         * ACTION_ACCESSIBILITY_FOCUS không phải lúc nào cũng được
         * widget/ROM hỗ trợ nếu service không hoạt động như screen reader.
         *
         * Vì vậy ưu tiên tìm trạng thái hiện tại theo:
         * 1. Accessibility Focus
         * 2. System/Input Focus
         */
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

                        /*
                         * Chưa có focus:
                         * bắt đầu từ phần tử đầu tiên.
                         */
                        0

                    } else {

                        viTriHienTai + 1
                    }
                }


                View.FOCUS_BACKWARD -> {

                    if (viTriHienTai < 0) {

                        /*
                         * Chưa có focus:
                         * bắt đầu từ phần tử cuối cùng.
                         */
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


        /*
         * Xóa focus cũ trước khi đặt focus mới.
         */
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


        /*
         * Thử Accessibility Focus trước.
         *
         * Nếu ROM/widget từ chối, fallback sang ACTION_FOCUS
         * (system/input focus). Đây là focus phù hợp hơn với kiểu
         * điều hướng hands-free của FaceAccess và không yêu cầu
         * biến service thành screen reader/touch-exploration service.
         */
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


    /**
     * Duyệt cây Accessibility theo thứ tự hiển thị và thu thập
     * các node có khả năng tương tác với người dùng.
     *
     * Không lấy mọi TextView vì như vậy một hàng Settings có thể
     * bị tách thành nhiều điểm focus gây trải nghiệm rất khó dùng.
     */
    private fun thuThapNodeCoTheDieuHuong(
        node: AccessibilityNodeInfo,
        ketQua: MutableList<AccessibilityNodeInfo>
    ) {

        if (!node.isVisibleToUser) {
            return
        }


        /*
         * Chỉ giữ node thực sự có ý nghĩa để điều hướng.
         *
         * Không đưa container thuần như RecyclerView / ScrollView vào
         * danh sách chỉ vì chúng focusable/scrollable. Scroll sẽ được
         * xử lý riêng bằng CUON_LEN / CUON_XUONG.
         */
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


    // =========================================================
    // VERTICAL SCROLL NAVIGATION
    // =========================================================

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


    /**
     * Scroll mới:
     *
     * 1. Ưu tiên dispatchGesture() để mô phỏng một cú vuốt ngắn,
     *    nhờ vậy UI cuộn có animation tự nhiên hơn.
     *
     * 2. Nếu Android từ chối gesture ngay từ đầu, fallback về
     *    AccessibilityNodeInfo ACTION_SCROLL_* đang hoạt động ổn.
     *
     * PITCH:
     * - LEN   -> vuốt xuống -> nội dung cuộn lên.
     * - XUONG -> vuốt lên   -> nội dung cuộn xuống.
     */
    private fun thucThiCuonDoc(
        cuonXuong: Boolean
    ): Boolean {

        val tenHuong =
            if (cuonXuong) {
                "DOWN"
            } else {
                "UP"
            }


        /*
         * Một gesture scroll đang chạy thì không nhận thêm
         * animation mới chồng lên nó.
         */
        if (dangCuonBangCuChi) {

            Log.d(
                TAG_SCROLL,
                "BO_QUA[$tenHuong]: animation scroll dang chay"
            )

            /*
             * Request được consume để Activity/Service không
             * fallback hoặc phát thêm thao tác khác.
             */
            return true
        }


        val gestureDaNhan =
            thucThiCuonBangCuChi(
                cuonXuong = cuonXuong
            )


        if (gestureDaNhan) {

            return true
        }


        /*
         * Fallback cho ROM/app không nhận dispatchGesture().
         */
        Log.d(
            TAG_SCROLL,
            "FALLBACK[$tenHuong]: dispatchGesture=false -> ACTION_SCROLL"
        )


        return thucThiCuonBangNode(
            cuonXuong = cuonXuong
        )
    }


    /**
     * Tạo một cú vuốt ngắn ở giữa màn hình.
     *
     * Khoảng di chuyển khoảng 30% chiều cao và duration 300ms,
     * đủ mượt nhưng không chậm, không nhảy thẳng cả trang.
     */
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


        /*
         * Nội dung cuộn XUỐNG:
         * ngón tay phải vuốt từ dưới lên.
         *
         * Nội dung cuộn LÊN:
         * ngón tay phải vuốt từ trên xuống.
         */
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


                        /*
                         * Gesture đã được hệ thống nhận nhưng bị hủy
                         * giữa chừng. Fallback về node scroll.
                         */
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


    /**
     * Fallback AccessibilityNodeInfo.
     *
     * Đây là thuật toán scroll cũ đang chạy ổn:
     * ưu tiên scroll container là parent của node đang focus,
     * sau đó mới duyệt toàn bộ cây UI.
     */
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


    /**
     * Đi từ node đang focus lên các parent để tìm container cuộn.
     */
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


    /**
     * Fallback: duyệt cây để tìm scroll container đang hiển thị.
     */
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

        /**
         * Khoảng vuốt nằm trong vùng giữa màn hình để tránh
         * status bar / navigation bar và giữ cảm giác tự nhiên.
         */
        private const val TY_LE_Y_BAT_DAU_CUON_XUONG =
            0.68f

        private const val TY_LE_Y_KET_THUC_CUON_XUONG =
            0.38f

        /**
         * 300ms đủ mượt nhưng vẫn phản hồi nhanh.
         */
        private const val THOI_GIAN_CUON_MUOT_MS =
            300L

        @Volatile
        private var phienBanDangHoatDong:
                DichVuTruyCapFaceAccess? = null


        fun dangHoatDong(): Boolean {

            return phienBanDangHoatDong != null
        }


        /**
         * Feedback chính thức của FaceAccess khi Accessibility đang chạy.
         *
         * Trả true nếu request đã được nhận.
         */
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