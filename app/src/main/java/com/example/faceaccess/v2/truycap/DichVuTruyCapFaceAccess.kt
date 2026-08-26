package com.example.faceaccess.v2.truycap

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class DichVuTruyCapFaceAccess : AccessibilityService() {

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


    companion object {

        private const val TAG =
            "DichVuTruyCap"

        private const val TAG_FOCUS =
            "FocusNavigation"

        @Volatile
        private var phienBanDangHoatDong:
                DichVuTruyCapFaceAccess? = null


        fun dangHoatDong(): Boolean {

            return phienBanDangHoatDong != null
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
    }
}
