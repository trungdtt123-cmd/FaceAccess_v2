package com.example.faceaccess.v2.contro

import android.graphics.Point
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import com.example.faceaccess.v2.dieuphoi.contro.LenhConTro
import kotlin.math.abs
import kotlin.math.max

class BoChonMucTieuConTro {

    data class KetQua(
        val bounds: Rect,
        val nhan: String?,
        val viewId: String?,
        val tenLop: String?
    )

    fun timMucTieu(
        root: AccessibilityNodeInfo,
        viTriConTro: Point,
        lenh: LenhConTro,
        chieuRongManHinh: Int,
        chieuCaoManHinh: Int,
        matDo: Float
    ): KetQua? {
        val ungVien =
            mutableListOf<UngVien>()

        thuThapUngVien(
            node = root,
            ketQua = ungVien,
            chieuRongManHinh = chieuRongManHinh,
            chieuCaoManHinh = chieuCaoManHinh
        )

        val khoangCachToiDa =
            dp(
                GIOI_HAN_TRUC_CHINH_DP,
                matDo
            )

        val lechTrucToiDa =
            dp(
                GIOI_HAN_TRUC_PHU_DP,
                matDo
            )

        val khoangCachToiThieu =
            dp(
                KHOANG_CACH_TOI_THIEU_DP,
                matDo
            )

        return ungVien
            .asSequence()
            .mapNotNull { ungVienHienTai ->
                chamDiem(
                    ungVien = ungVienHienTai,
                    viTriConTro = viTriConTro,
                    lenh = lenh,
                    khoangCachToiDa = khoangCachToiDa,
                    lechTrucToiDa = lechTrucToiDa,
                    khoangCachToiThieu = khoangCachToiThieu,
                    chieuRongManHinh = chieuRongManHinh,
                    chieuCaoManHinh = chieuCaoManHinh
                )
            }
            .minByOrNull {
                it.diem
            }
            ?.let {
                KetQua(
                    bounds = Rect(it.ungVien.bounds),
                    nhan = it.ungVien.nhan,
                    viewId = it.ungVien.viewId,
                    tenLop = it.ungVien.tenLop
                )
            }
    }

    private fun thuThapUngVien(
        node: AccessibilityNodeInfo,
        ketQua: MutableList<UngVien>,
        chieuRongManHinh: Int,
        chieuCaoManHinh: Int
    ) {
        if (!node.isVisibleToUser) {
            return
        }

        val bounds =
            Rect().also {
                node.getBoundsInScreen(it)
            }

        if (
            laMucTieuHopLe(
                node = node,
                bounds = bounds,
                chieuRongManHinh = chieuRongManHinh,
                chieuCaoManHinh = chieuCaoManHinh
            )
        ) {
            val nhan =
                node.contentDescription
                    ?.toString()
                    ?.takeIf { it.isNotBlank() }
                    ?: node.text
                        ?.toString()
                        ?.takeIf { it.isNotBlank() }

            ketQua.add(
                UngVien(
                    bounds = bounds,
                    nhan = nhan,
                    viewId = node.viewIdResourceName,
                    tenLop =
                        node.className
                            ?.toString()
                )
            )
        }

        for (index in 0 until node.childCount) {
            val child =
                node.getChild(index)
                    ?: continue

            thuThapUngVien(
                node = child,
                ketQua = ketQua,
                chieuRongManHinh = chieuRongManHinh,
                chieuCaoManHinh = chieuCaoManHinh
            )
        }
    }

    private fun laMucTieuHopLe(
        node: AccessibilityNodeInfo,
        bounds: Rect,
        chieuRongManHinh: Int,
        chieuCaoManHinh: Int
    ): Boolean {
        if (
            bounds.isEmpty ||
            bounds.right <= 0 ||
            bounds.bottom <= 0 ||
            bounds.left >= chieuRongManHinh ||
            bounds.top >= chieuCaoManHinh
        ) {
            return false
        }

        val coNhan =
            !node.text.isNullOrBlank() ||
                    !node.contentDescription.isNullOrBlank()

        val tuongTacTrucTiep =
            node.isClickable ||
                    node.isCheckable ||
                    node.isEditable

        val focusCoYNghia =
            node.isFocusable &&
                    coNhan &&
                    !node.isScrollable

        if (
            !tuongTacTrucTiep &&
            !focusCoYNghia
        ) {
            return false
        }

        val dienTichManHinh =
            chieuRongManHinh.toLong() *
                    chieuCaoManHinh.toLong()

        val dienTichNode =
            bounds.width().toLong() *
                    bounds.height().toLong()

        if (
            dienTichManHinh > 0 &&
            dienTichNode.toDouble() /
            dienTichManHinh.toDouble() >
            TY_LE_DIEN_TICH_TOI_DA
        ) {
            return false
        }

        return true
    }

    private fun chamDiem(
        ungVien: UngVien,
        viTriConTro: Point,
        lenh: LenhConTro,
        khoangCachToiDa: Int,
        lechTrucToiDa: Int,
        khoangCachToiThieu: Int,
        chieuRongManHinh: Int,
        chieuCaoManHinh: Int
    ): UngVienCoDiem? {
        val centerX =
            ungVien.bounds.centerX()

        val centerY =
            ungVien.bounds.centerY()

        val dx =
            centerX - viTriConTro.x

        val dy =
            centerY - viTriConTro.y

        val trucChinh =
            when (lenh) {
                LenhConTro.TRAI -> -dx
                LenhConTro.PHAI -> dx
                LenhConTro.LEN -> -dy
                LenhConTro.XUONG -> dy
            }

        val trucPhu =
            when (lenh) {
                LenhConTro.TRAI,
                LenhConTro.PHAI ->
                    abs(dy)

                LenhConTro.LEN,
                LenhConTro.XUONG ->
                    abs(dx)
            }

        if (
            trucChinh < khoangCachToiThieu ||
            trucChinh > khoangCachToiDa ||
            trucPhu > lechTrucToiDa
        ) {
            return null
        }

        val dienTichManHinh =
            max(
                1L,
                chieuRongManHinh.toLong() *
                        chieuCaoManHinh.toLong()
            )

        val dienTichNode =
            ungVien.bounds.width().toLong() *
                    ungVien.bounds.height().toLong()

        val phatKichThuoc =
            (
                    dienTichNode.toDouble() /
                            dienTichManHinh.toDouble() *
                            PHAT_KICH_THUOC
                    ).toFloat()

        val diem =
            trucChinh +
                    trucPhu *
                    TRONG_SO_LECH_TRUC +
                    phatKichThuoc

        return UngVienCoDiem(
            ungVien = ungVien,
            diem = diem
        )
    }

    private fun dp(
        giaTri: Int,
        matDo: Float
    ): Int {
        return (
                giaTri *
                        matDo
                ).toInt()
    }

    private data class UngVien(
        val bounds: Rect,
        val nhan: String?,
        val viewId: String?,
        val tenLop: String?
    )

    private data class UngVienCoDiem(
        val ungVien: UngVien,
        val diem: Float
    )

    companion object {
        private const val GIOI_HAN_TRUC_CHINH_DP = 280
        private const val GIOI_HAN_TRUC_PHU_DP = 150
        private const val KHOANG_CACH_TOI_THIEU_DP = 12

        private const val TRONG_SO_LECH_TRUC = 2.2f
        private const val PHAT_KICH_THUOC = 180f
        private const val TY_LE_DIEN_TICH_TOI_DA = 0.65
    }
}