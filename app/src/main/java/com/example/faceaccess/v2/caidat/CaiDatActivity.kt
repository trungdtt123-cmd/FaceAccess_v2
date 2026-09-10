// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.caidat

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.faceaccess.v2.R
import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhHanhDongCuChi
import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhNhanDienCuChi
import com.example.faceaccess.v2.cuchi.cauhinh.HanhDongTuyChinhCuChi
import com.example.faceaccess.v2.cuchi.cauhinh.KhoCauHinhNhanDienCuChi
import com.example.faceaccess.v2.truycap.DichVuTruyCapFaceAccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class CaiDatActivity : AppCompatActivity() {

    private lateinit var khoCauHinh:
            KhoCauHinhNhanDienCuChi

    private lateinit var cauHinhBanDau:
            CauHinhNhanDienCuChi

    private var hanhDongTam =
        CauHinhNhanDienCuChi.macDinh().hanhDong

    private var coThayDoiChuaLuu =
        false

    private var cauHinhDaThayDoi =
        false

    private var yeuCauDatLaiDoNhay =
        false

    private var yeuCauDatLaiPhanHoi =
        false

    private lateinit var txtTrangThaiLuu: TextView
    private lateinit var txtTrangThaiHieuChinh: TextView
    private lateinit var txtTrangThaiBanSao: TextView
    private lateinit var txtTrangThaiTroNang: TextView
    private lateinit var txtTrangThaiQuyenCamera: TextView

    private lateinit var seekDoNhayQuayTrai: SeekBar
    private lateinit var seekDoNhayQuayPhai: SeekBar
    private lateinit var seekDoNhayNhinLen: SeekBar
    private lateinit var seekDoNhayNhinXuong: SeekBar
    private lateinit var seekDoNhayNghiengTrai: SeekBar
    private lateinit var seekDoNhayNghiengPhai: SeekBar
    private lateinit var seekDoNhayNhamMat: SeekBar
    private lateinit var seekDoNhayMoMieng: SeekBar
    private lateinit var seekDoNhayMoMiengHaiLan: SeekBar

    private lateinit var txtDoNhayQuayTrai: TextView
    private lateinit var txtDoNhayQuayPhai: TextView
    private lateinit var txtDoNhayNhinLen: TextView
    private lateinit var txtDoNhayNhinXuong: TextView
    private lateinit var txtDoNhayNghiengTrai: TextView
    private lateinit var txtDoNhayNghiengPhai: TextView
    private lateinit var txtDoNhayNhamMat: TextView
    private lateinit var txtDoNhayMoMieng: TextView
    private lateinit var txtDoNhayMoMiengHaiLan: TextView

    private lateinit var btnHanhDongNghiengTrai: Button
    private lateinit var btnHanhDongNghiengPhai: Button
    private lateinit var btnHanhDongMoMieng: Button
    private lateinit var btnDatLaiCuChiToanCuc: Button

    private lateinit var seekThoiGianHuongDau: SeekBar
    private lateinit var seekThoiGianNghiengDau: SeekBar
    private lateinit var seekThoiGianNhamMat: SeekBar
    private lateinit var seekThoiGianMoMieng: SeekBar
    private lateinit var seekKhoangMoMiengHaiLan: SeekBar

    private lateinit var txtThoiGianHuongDau: TextView
    private lateinit var txtThoiGianNghiengDau: TextView
    private lateinit var txtThoiGianNhamMat: TextView
    private lateinit var txtThoiGianMoMieng: TextView
    private lateinit var txtKhoangMoMiengHaiLan: TextView

    private lateinit var btnQuayLai: TextView
    private lateinit var btnHieuChinhLai: Button
    private lateinit var btnDatLaiDoNhay: Button
    private lateinit var btnDatLaiPhanHoi: Button
    private lateinit var btnLuuThayDoi: Button
    private lateinit var btnTaiLenCauHinh: Button
    private lateinit var btnTaiXuongCauHinh: Button
    private lateinit var btnDatLaiTatCa: Button
    private lateinit var btnMoCaiDatTroNang: Button
    private lateinit var btnMoQuyenUngDung: Button

    private lateinit var noiDungCuonCaiDat: View

    private lateinit var navTrangChu: TextView
    private lateinit var navHuongDan: TextView
    private lateinit var navHieuChinh: TextView
    private lateinit var navCaiDat: TextView

    private var noiDungDangMo: View? = null
    private var muiTenDangMo: TextView? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        cauHinhThanhHeThong()

        setContentView(
            R.layout.activity_cai_dat
        )

        khoCauHinh =
            KhoCauHinhNhanDienCuChi(
                applicationContext
            )

        anhXaGiaoDien()
        apDungPhongCachNut()
        capNhatMenuDuoiCaiDat()
        thietLapMucThuGon()

        cauHinhBanDau =
            khoCauHinh.layCauHinh()

        ganCauHinhLenGiaoDien(
            cauHinhBanDau
        )

        ganSuKien()
        capNhatTrangThai()
        taoHieuUngMoNoiDungCaiDat()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    xuLyQuayLai()
                }
            }
        )
    }

    private fun cauHinhThanhHeThong() {

        window.statusBarColor =
            Color.parseColor(
                "#F6FBF8"
            )

        window.navigationBarColor =
            Color.parseColor(
                "#F6FBF8"
            )

        WindowCompat
            .getInsetsController(
                window,
                window.decorView
            )
            .apply {
                isAppearanceLightStatusBars =
                    true

                isAppearanceLightNavigationBars =
                    true
            }
    }


    private fun taoHieuUngMoNoiDungCaiDat() {

        noiDungCuonCaiDat
            .animate()
            .cancel()

        noiDungCuonCaiDat
            .scrollTo(
                0,
                0
            )

        noiDungCuonCaiDat.alpha =
            0f

        noiDungCuonCaiDat.translationY =
            DO_DICH_CHUYEN_NOI_DUNG_DP *
                    resources
                        .displayMetrics
                        .density

        // Chỉ nội dung trượt lên, menu dưới đứng yên
        noiDungCuonCaiDat
            .animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(
                THOI_GIAN_HIEU_UNG_NOI_DUNG_MS
            )
            .start()
    }

    private fun dongManHinhKhongHieuUng() {

        finish()

        // Không cho toàn bộ màn hình và menu chạy theo transition Activity
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()

        if (::txtTrangThaiTroNang.isInitialized) {
            capNhatTrangThaiHeThong()
        }
    }

    private fun anhXaGiaoDien() {

        noiDungCuonCaiDat =
            findViewById(R.id.noiDungCuonCaiDat)

        txtTrangThaiLuu =
            findViewById(R.id.txtTrangThaiLuu)

        txtTrangThaiHieuChinh =
            findViewById(R.id.txtTrangThaiHieuChinh)

        txtTrangThaiBanSao =
            findViewById(R.id.txtTrangThaiBanSao)

        txtTrangThaiTroNang =
            findViewById(R.id.txtTrangThaiTroNang)

        txtTrangThaiQuyenCamera =
            findViewById(R.id.txtTrangThaiQuyenCamera)

        seekDoNhayQuayTrai =
            findViewById(R.id.seekDoNhayQuayTrai)

        seekDoNhayQuayPhai =
            findViewById(R.id.seekDoNhayQuayPhai)

        seekDoNhayNhinLen =
            findViewById(R.id.seekDoNhayNhinLen)

        seekDoNhayNhinXuong =
            findViewById(R.id.seekDoNhayNhinXuong)

        seekDoNhayNghiengTrai =
            findViewById(R.id.seekDoNhayNghiengTrai)

        seekDoNhayNghiengPhai =
            findViewById(R.id.seekDoNhayNghiengPhai)

        seekDoNhayNhamMat =
            findViewById(R.id.seekDoNhayNhamMat)

        seekDoNhayMoMieng =
            findViewById(R.id.seekDoNhayMoMieng)

        seekDoNhayMoMiengHaiLan =
            findViewById(R.id.seekDoNhayMoMiengHaiLan)

        txtDoNhayQuayTrai =
            findViewById(R.id.txtDoNhayQuayTrai)

        txtDoNhayQuayPhai =
            findViewById(R.id.txtDoNhayQuayPhai)

        txtDoNhayNhinLen =
            findViewById(R.id.txtDoNhayNhinLen)

        txtDoNhayNhinXuong =
            findViewById(R.id.txtDoNhayNhinXuong)

        txtDoNhayNghiengTrai =
            findViewById(R.id.txtDoNhayNghiengTrai)

        txtDoNhayNghiengPhai =
            findViewById(R.id.txtDoNhayNghiengPhai)

        txtDoNhayNhamMat =
            findViewById(R.id.txtDoNhayNhamMat)

        txtDoNhayMoMieng =
            findViewById(R.id.txtDoNhayMoMieng)

        txtDoNhayMoMiengHaiLan =
            findViewById(R.id.txtDoNhayMoMiengHaiLan)

        btnHanhDongNghiengTrai =
            findViewById(R.id.btnHanhDongNghiengTrai)

        btnHanhDongNghiengPhai =
            findViewById(R.id.btnHanhDongNghiengPhai)

        btnHanhDongMoMieng =
            findViewById(R.id.btnHanhDongMoMieng)

        btnDatLaiCuChiToanCuc =
            findViewById(R.id.btnDatLaiCuChiToanCuc)

        seekThoiGianHuongDau =
            findViewById(R.id.seekThoiGianHuongDau)

        seekThoiGianNghiengDau =
            findViewById(R.id.seekThoiGianNghiengDau)

        seekThoiGianNhamMat =
            findViewById(R.id.seekThoiGianNhamMat)

        seekThoiGianMoMieng =
            findViewById(R.id.seekThoiGianMoMieng)

        seekKhoangMoMiengHaiLan =
            findViewById(R.id.seekKhoangMoMiengHaiLan)

        txtThoiGianHuongDau =
            findViewById(R.id.txtThoiGianHuongDau)

        txtThoiGianNghiengDau =
            findViewById(R.id.txtThoiGianNghiengDau)

        txtThoiGianNhamMat =
            findViewById(R.id.txtThoiGianNhamMat)

        txtThoiGianMoMieng =
            findViewById(R.id.txtThoiGianMoMieng)

        txtKhoangMoMiengHaiLan =
            findViewById(R.id.txtKhoangMoMiengHaiLan)

        btnQuayLai =
            findViewById(R.id.btnQuayLai)

        btnHieuChinhLai =
            findViewById(R.id.btnHieuChinhLai)

        btnDatLaiDoNhay =
            findViewById(R.id.btnDatLaiDoNhay)

        btnDatLaiPhanHoi =
            findViewById(R.id.btnDatLaiPhanHoi)

        btnLuuThayDoi =
            findViewById(R.id.btnLuuThayDoi)

        btnTaiLenCauHinh =
            findViewById(R.id.btnTaiLenCauHinh)

        btnTaiXuongCauHinh =
            findViewById(R.id.btnTaiXuongCauHinh)

        btnDatLaiTatCa =
            findViewById(R.id.btnDatLaiTatCa)

        btnMoCaiDatTroNang =
            findViewById(R.id.btnMoCaiDatTroNang)

        btnMoQuyenUngDung =
            findViewById(R.id.btnMoQuyenUngDung)

        navTrangChu =
            findViewById(R.id.navTrangChu)

        navHuongDan =
            findViewById(R.id.navHuongDan)

        navHieuChinh =
            findViewById(R.id.navHieuChinh)

        navCaiDat =
            findViewById(R.id.navCaiDat)
    }

    private fun thietLapMucThuGon() {

        val cacMuc =
            listOf(
                Triple(
                    R.id.mucHieuChinh,
                    R.id.noiDungHieuChinh,
                    R.id.muiTenHieuChinh
                ),
                Triple(
                    R.id.mucDoNhay,
                    R.id.noiDungDoNhay,
                    R.id.muiTenDoNhay
                ),
                Triple(
                    R.id.mucCuChiToanCuc,
                    R.id.noiDungCuChiToanCuc,
                    R.id.muiTenCuChiToanCuc
                ),
                Triple(
                    R.id.mucPhanHoi,
                    R.id.noiDungPhanHoi,
                    R.id.muiTenPhanHoi
                ),
                Triple(
                    R.id.mucDuLieu,
                    R.id.noiDungDuLieu,
                    R.id.muiTenDuLieu
                ),
                Triple(
                    R.id.mucHeThong,
                    R.id.noiDungHeThong,
                    R.id.muiTenHeThong
                )
            )

        cacMuc.forEach { (mucId, noiDungId, muiTenId) ->

            val muc =
                findViewById<View>(mucId)

            val noiDung =
                findViewById<View>(noiDungId)

            val muiTen =
                findViewById<TextView>(muiTenId)

            noiDung.visibility =
                View.GONE

            muiTen.text =
                "›"

            muc.setOnClickListener {
                doiTrangThaiMucThuGon(
                    noiDung = noiDung,
                    muiTen = muiTen
                )
            }
        }
    }

    private fun doiTrangThaiMucThuGon(
        noiDung: View,
        muiTen: TextView
    ) {

        val dangMo =
            noiDung.visibility == View.VISIBLE

        if (dangMo) {
            noiDung.visibility =
                View.GONE

            muiTen.text =
                "›"

            if (noiDungDangMo === noiDung) {
                noiDungDangMo =
                    null

                muiTenDangMo =
                    null
            }

            return
        }

        noiDungDangMo?.let {
            it.visibility =
                View.GONE
        }

        muiTenDangMo?.text =
            "›"

        noiDung.visibility =
            View.VISIBLE

        muiTen.text =
            "⌄"

        noiDungDangMo =
            noiDung

        muiTenDangMo =
            muiTen
    }


    private fun apDungPhongCachNut() {

        val cacNut =
            listOf(
                btnHieuChinhLai,
                btnDatLaiDoNhay,
                btnHanhDongNghiengTrai,
                btnHanhDongNghiengPhai,
                btnHanhDongMoMieng,
                btnDatLaiCuChiToanCuc,
                btnDatLaiPhanHoi,
                btnLuuThayDoi,
                btnTaiLenCauHinh,
                btnTaiXuongCauHinh,
                btnDatLaiTatCa,
                btnMoCaiDatTroNang,
                btnMoQuyenUngDung
            )

        cacNut.forEach { nut ->
            nut.backgroundTintList = null
            nut.isAllCaps = false
        }
    }

    private fun capNhatMenuDuoiCaiDat() {

        val mauDangChon =
            Color.parseColor("#218A68")

        val mauThuong =
            Color.parseColor("#7D8F88")

        fun capNhatNut(
            nut: TextView,
            dangChon: Boolean
        ) {

            val mau =
                if (dangChon) {
                    mauDangChon
                } else {
                    mauThuong
                }

            nut.animate().cancel()

            nut.setTextColor(mau)
            nut.setBackgroundColor(Color.TRANSPARENT)

            nut.compoundDrawablesRelative
                .forEach { drawable ->
                    drawable
                        ?.mutate()
                        ?.setTint(mau)
                }

            // Giữ toàn bộ menu cố định
            nut.alpha = 1f
            nut.scaleX = 1f
            nut.scaleY = 1f
            nut.translationX = 0f
            nut.translationY = 0f
        }

        capNhatNut(
            navTrangChu,
            false
        )

        capNhatNut(
            navHuongDan,
            false
        )

        capNhatNut(
            navHieuChinh,
            false
        )

        capNhatNut(
            navCaiDat,
            true
        )
    }

    private fun ganSuKien() {

        btnQuayLai.setOnClickListener {
            xuLyQuayLai()
        }

        navTrangChu.setOnClickListener {
            xuLyVeTrangChu()
        }

        navHuongDan.setOnClickListener {
            ketThucVaYeuCauHuongDan()
        }

        navHieuChinh.setOnClickListener {
            xuLyHieuChinhLai()
        }

        navCaiDat.setOnClickListener {
            // Đang ở màn Cài đặt
        }

        btnHieuChinhLai.setOnClickListener {
            xuLyHieuChinhLai()
        }

        btnHanhDongNghiengTrai.setOnClickListener {
            chonHanhDongToanCuc(
                CuChiToanCuc.NGHIENG_TRAI
            )
        }

        btnHanhDongNghiengPhai.setOnClickListener {
            chonHanhDongToanCuc(
                CuChiToanCuc.NGHIENG_PHAI
            )
        }

        btnHanhDongMoMieng.setOnClickListener {
            chonHanhDongToanCuc(
                CuChiToanCuc.MO_MIENG
            )
        }

        btnDatLaiCuChiToanCuc.setOnClickListener {
            datLaiCuChiToanCuc()
        }

        btnDatLaiDoNhay.setOnClickListener {
            datLaiDoNhay()
        }

        btnDatLaiPhanHoi.setOnClickListener {
            datLaiPhanHoi()
        }

        btnLuuThayDoi.setOnClickListener {
            luuThayDoi(
                hienThongBao = true
            )
        }

        btnTaiLenCauHinh.setOnClickListener {
            xuLyTaoBanSao()
        }

        btnTaiXuongCauHinh.setOnClickListener {
            xuLyKhoiPhucBanSao()
        }

        btnDatLaiTatCa.setOnClickListener {
            xacNhanDatLaiTatCa()
        }

        btnMoCaiDatTroNang.setOnClickListener {
            moCaiDatTroNang()
        }

        btnMoQuyenUngDung.setOnClickListener {
            moCaiDatUngDung()
        }

        ganLangNgheSeekBar()
    }

    private fun ganLangNgheSeekBar() {

        val cacSeekBar =
            listOf(
                seekDoNhayQuayTrai,
                seekDoNhayQuayPhai,
                seekDoNhayNhinLen,
                seekDoNhayNhinXuong,
                seekDoNhayNghiengTrai,
                seekDoNhayNghiengPhai,
                seekDoNhayNhamMat,
                seekDoNhayMoMieng,
                seekDoNhayMoMiengHaiLan,
                seekThoiGianHuongDau,
                seekThoiGianNghiengDau,
                seekThoiGianNhamMat,
                seekThoiGianMoMieng,
                seekKhoangMoMiengHaiLan
            )

        cacSeekBar.forEach { seekBar ->

            seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {

                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {

                        capNhatNhanDoNhay()
                        capNhatNhanPhanHoi()

                        if (fromUser) {
                            danhDauCoThayDoi()
                        }
                    }

                    override fun onStartTrackingTouch(
                        seekBar: SeekBar?
                    ) = Unit

                    override fun onStopTrackingTouch(
                        seekBar: SeekBar?
                    ) = Unit
                }
            )
        }
    }

    private fun ganCauHinhLenGiaoDien(
        cauHinh: CauHinhNhanDienCuChi
    ) {

        seekDoNhayQuayTrai.progress =
            mucDoTuNguong(
                cauHinh.huongDau.layNguongQuayTrai(),
                NGUONG_YAW_NHAY_NHAT,
                NGUONG_YAW_ON_DINH_NHAT
            )

        seekDoNhayQuayPhai.progress =
            mucDoTuNguong(
                cauHinh.huongDau.layNguongQuayPhai(),
                NGUONG_YAW_NHAY_NHAT,
                NGUONG_YAW_ON_DINH_NHAT
            )

        seekDoNhayNhinLen.progress =
            mucDoTuNguong(
                cauHinh.huongDau.layNguongNhinLen(),
                NGUONG_PITCH_NHAY_NHAT,
                NGUONG_PITCH_ON_DINH_NHAT
            )

        seekDoNhayNhinXuong.progress =
            mucDoTuNguong(
                cauHinh.huongDau.layNguongNhinXuong(),
                NGUONG_PITCH_NHAY_NHAT,
                NGUONG_PITCH_ON_DINH_NHAT
            )

        seekDoNhayNghiengTrai.progress =
            mucDoTuNguong(
                cauHinh.nghiengDau.nguongTrai.absoluteValue,
                NGUONG_NGHIENG_NHAY_NHAT,
                NGUONG_NGHIENG_ON_DINH_NHAT
            )

        seekDoNhayNghiengPhai.progress =
            mucDoTuNguong(
                cauHinh.nghiengDau.nguongPhai.absoluteValue,
                NGUONG_NGHIENG_NHAY_NHAT,
                NGUONG_NGHIENG_ON_DINH_NHAT
            )

        seekDoNhayNhamMat.progress =
            mucDoTuNguong(
                cauHinh.nhamHaiMat.nguongDong,
                NGUONG_MAT_NHAY_NHAT,
                NGUONG_MAT_ON_DINH_NHAT
            )

        seekDoNhayMoMieng.progress =
            mucDoTuNguong(
                cauHinh.moMieng.nguongMo,
                NGUONG_MIENG_NHAY_NHAT,
                NGUONG_MIENG_ON_DINH_NHAT
            )

        seekDoNhayMoMiengHaiLan.progress =
            mucDoTuNguong(
                cauHinh.moMiengHaiLan.nguongMo,
                NGUONG_MIENG_HAI_LAN_NHAY_NHAT,
                NGUONG_MIENG_HAI_LAN_ON_DINH_NHAT
            )

        seekThoiGianHuongDau.progress =
            thoiGianThanhProgress(
                (
                        cauHinh.huongDau.thoiGianGiuYawMs +
                                cauHinh.huongDau.thoiGianGiuPitchMs
                        ) / 2L,
                THOI_GIAN_HUONG_DAU_MIN,
                THOI_GIAN_HUONG_DAU_MAX
            )

        seekThoiGianNghiengDau.progress =
            thoiGianThanhProgress(
                cauHinh.nghiengDau.thoiGianGiuMs,
                THOI_GIAN_NGHIENG_DAU_MIN,
                THOI_GIAN_NGHIENG_DAU_MAX
            )

        seekThoiGianNhamMat.progress =
            thoiGianThanhProgress(
                cauHinh.nhamHaiMat.thoiGianNhamXacNhanMs,
                THOI_GIAN_NHAM_MAT_MIN,
                THOI_GIAN_NHAM_MAT_MAX
            )

        seekThoiGianMoMieng.progress =
            thoiGianThanhProgress(
                cauHinh.moMieng.thoiGianGiuBackMs,
                THOI_GIAN_MIENG_MIN,
                THOI_GIAN_MIENG_MAX
            )

        seekKhoangMoMiengHaiLan.progress =
            thoiGianThanhProgress(
                cauHinh.moMiengHaiLan.khoangChoLanHaiMs,
                THOI_GIAN_HAI_LAN_MIN,
                THOI_GIAN_HAI_LAN_MAX
            )

        hanhDongTam =
            cauHinh.hanhDong
                .chuanHoaChoCuChiToanCuc()

        capNhatNutHanhDong()
        capNhatNhanDoNhay()
        capNhatNhanPhanHoi()

        coThayDoiChuaLuu =
            false

        yeuCauDatLaiDoNhay =
            false

        yeuCauDatLaiPhanHoi =
            false

        capNhatTrangThaiLuu()
    }

    private fun capNhatNhanDoNhay() {

        txtDoNhayQuayTrai.text =
            moTaDoNhay(
                seekDoNhayQuayTrai.progress
            )

        txtDoNhayQuayPhai.text =
            moTaDoNhay(
                seekDoNhayQuayPhai.progress
            )

        txtDoNhayNhinLen.text =
            moTaDoNhay(
                seekDoNhayNhinLen.progress
            )

        txtDoNhayNhinXuong.text =
            moTaDoNhay(
                seekDoNhayNhinXuong.progress
            )

        txtDoNhayNghiengTrai.text =
            moTaDoNhay(
                seekDoNhayNghiengTrai.progress
            )

        txtDoNhayNghiengPhai.text =
            moTaDoNhay(
                seekDoNhayNghiengPhai.progress
            )

        txtDoNhayNhamMat.text =
            moTaDoNhay(
                seekDoNhayNhamMat.progress
            )

        txtDoNhayMoMieng.text =
            moTaDoNhay(
                seekDoNhayMoMieng.progress
            )

        txtDoNhayMoMiengHaiLan.text =
            moTaDoNhay(
                seekDoNhayMoMiengHaiLan.progress
            )
    }

    private fun capNhatNhanPhanHoi() {

        txtThoiGianHuongDau.text =
            "${layThoiGianHuongDau()} ms"

        txtThoiGianNghiengDau.text =
            "${layThoiGianNghiengDau()} ms"

        txtThoiGianNhamMat.text =
            "${layThoiGianNhamMat()} ms"

        txtThoiGianMoMieng.text =
            "${layThoiGianMoMieng()} ms"

        txtKhoangMoMiengHaiLan.text =
            "${layKhoangMoMiengHaiLan()} ms"
    }

    private fun capNhatNutHanhDong() {

        btnHanhDongNghiengTrai.text =
            hanhDongTam.nghiengTrai.tenHienThi

        btnHanhDongNghiengPhai.text =
            hanhDongTam.nghiengPhai.tenHienThi

        btnHanhDongMoMieng.text =
            hanhDongTam.moMieng.tenHienThi
    }

    private fun chonHanhDongToanCuc(
        cuChi: CuChiToanCuc
    ) {

        val danhSach =
            listOf(
                HanhDongTuyChinhCuChi.BACK,
                HanhDongTuyChinhCuChi.HOME,
                HanhDongTuyChinhCuChi.DOI_CHE_DO
            )

        val hienTai =
            layHanhDongToanCuc(
                cuChi
            )

        val tenHienThi =
            danhSach
                .map {
                    it.tenHienThi
                }
                .toTypedArray()

        val viTriHienTai =
            danhSach.indexOf(
                hienTai
            )

        AlertDialog.Builder(this)
            .setTitle(
                "Chức năng • ${cuChi.tenHienThi}"
            )
            .setSingleChoiceItems(
                tenHienThi,
                viTriHienTai
            ) { hopThoai, viTri ->

                val moi =
                    danhSach[viTri]

                if (moi != hienTai) {
                    hoanDoiHanhDongToanCuc(
                        cuChi = cuChi,
                        hanhDongMoi = moi
                    )
                    capNhatNutHanhDong()
                    danhDauCoThayDoi()
                }

                hopThoai.dismiss()
            }
            .setNegativeButton(
                "HỦY",
                null
            )
            .show()
    }

    private fun hoanDoiHanhDongToanCuc(
        cuChi: CuChiToanCuc,
        hanhDongMoi: HanhDongTuyChinhCuChi
    ) {

        val hanhDongCu =
            layHanhDongToanCuc(
                cuChi
            )

        val cuChiDangDungHanhDongMoi =
            CuChiToanCuc.values()
                .firstOrNull {
                    it != cuChi &&
                            layHanhDongToanCuc(it) ==
                            hanhDongMoi
                }

        var cauHinhMoi =
            datHanhDongToanCuc(
                hanhDongTam,
                cuChi,
                hanhDongMoi
            )

        if (
            cuChiDangDungHanhDongMoi !=
            null
        ) {
            cauHinhMoi =
                datHanhDongToanCuc(
                    cauHinhMoi,
                    cuChiDangDungHanhDongMoi,
                    hanhDongCu
                )
        }

        hanhDongTam =
            cauHinhMoi
                .chuanHoaChoCuChiToanCuc()
    }

    private fun layHanhDongToanCuc(
        cuChi: CuChiToanCuc
    ): HanhDongTuyChinhCuChi =
        when (cuChi) {
            CuChiToanCuc.NGHIENG_TRAI ->
                hanhDongTam.nghiengTrai

            CuChiToanCuc.NGHIENG_PHAI ->
                hanhDongTam.nghiengPhai

            CuChiToanCuc.MO_MIENG ->
                hanhDongTam.moMieng
        }

    private fun datHanhDongToanCuc(
        cauHinh:
        CauHinhHanhDongCuChi,
        cuChi: CuChiToanCuc,
        hanhDong: HanhDongTuyChinhCuChi
    ): CauHinhHanhDongCuChi =
        when (cuChi) {
            CuChiToanCuc.NGHIENG_TRAI ->
                cauHinh.copy(
                    nghiengTrai = hanhDong
                )

            CuChiToanCuc.NGHIENG_PHAI ->
                cauHinh.copy(
                    nghiengPhai = hanhDong
                )

            CuChiToanCuc.MO_MIENG ->
                cauHinh.copy(
                    moMieng = hanhDong
                )
        }

    private fun datLaiCuChiToanCuc() {

        val macDinh =
            CauHinhNhanDienCuChi
                .macDinh()
                .hanhDong
                .chuanHoaChoCuChiToanCuc()

        hanhDongTam =
            hanhDongTam.copy(
                nghiengTrai =
                    macDinh.nghiengTrai,
                nghiengPhai =
                    macDinh.nghiengPhai,
                moMieng =
                    macDinh.moMieng
            ).chuanHoaChoCuChiToanCuc()

        capNhatNutHanhDong()
        danhDauCoThayDoi()
    }

    private fun datLaiDoNhay() {

        val macDinh =
            CauHinhNhanDienCuChi.macDinh()

        seekDoNhayQuayTrai.progress =
            mucDoTuNguong(
                macDinh.huongDau.layNguongQuayTrai(),
                NGUONG_YAW_NHAY_NHAT,
                NGUONG_YAW_ON_DINH_NHAT
            )

        seekDoNhayQuayPhai.progress =
            mucDoTuNguong(
                macDinh.huongDau.layNguongQuayPhai(),
                NGUONG_YAW_NHAY_NHAT,
                NGUONG_YAW_ON_DINH_NHAT
            )

        seekDoNhayNhinLen.progress =
            mucDoTuNguong(
                macDinh.huongDau.layNguongNhinLen(),
                NGUONG_PITCH_NHAY_NHAT,
                NGUONG_PITCH_ON_DINH_NHAT
            )

        seekDoNhayNhinXuong.progress =
            mucDoTuNguong(
                macDinh.huongDau.layNguongNhinXuong(),
                NGUONG_PITCH_NHAY_NHAT,
                NGUONG_PITCH_ON_DINH_NHAT
            )

        seekDoNhayNghiengTrai.progress =
            mucDoTuNguong(
                macDinh.nghiengDau.nguongTrai.absoluteValue,
                NGUONG_NGHIENG_NHAY_NHAT,
                NGUONG_NGHIENG_ON_DINH_NHAT
            )

        seekDoNhayNghiengPhai.progress =
            mucDoTuNguong(
                macDinh.nghiengDau.nguongPhai.absoluteValue,
                NGUONG_NGHIENG_NHAY_NHAT,
                NGUONG_NGHIENG_ON_DINH_NHAT
            )

        seekDoNhayNhamMat.progress =
            mucDoTuNguong(
                macDinh.nhamHaiMat.nguongDong,
                NGUONG_MAT_NHAY_NHAT,
                NGUONG_MAT_ON_DINH_NHAT
            )

        seekDoNhayMoMieng.progress =
            mucDoTuNguong(
                macDinh.moMieng.nguongMo,
                NGUONG_MIENG_NHAY_NHAT,
                NGUONG_MIENG_ON_DINH_NHAT
            )

        seekDoNhayMoMiengHaiLan.progress =
            mucDoTuNguong(
                macDinh.moMiengHaiLan.nguongMo,
                NGUONG_MIENG_HAI_LAN_NHAY_NHAT,
                NGUONG_MIENG_HAI_LAN_ON_DINH_NHAT
            )

        yeuCauDatLaiDoNhay =
            true

        capNhatNhanDoNhay()
        danhDauCoThayDoi()
    }

    private fun datLaiPhanHoi() {

        val macDinh =
            CauHinhNhanDienCuChi.macDinh()

        seekThoiGianHuongDau.progress =
            thoiGianThanhProgress(
                (
                        macDinh.huongDau.thoiGianGiuYawMs +
                                macDinh.huongDau.thoiGianGiuPitchMs
                        ) / 2L,
                THOI_GIAN_HUONG_DAU_MIN,
                THOI_GIAN_HUONG_DAU_MAX
            )

        seekThoiGianNghiengDau.progress =
            thoiGianThanhProgress(
                macDinh.nghiengDau.thoiGianGiuMs,
                THOI_GIAN_NGHIENG_DAU_MIN,
                THOI_GIAN_NGHIENG_DAU_MAX
            )

        seekThoiGianNhamMat.progress =
            thoiGianThanhProgress(
                macDinh.nhamHaiMat.thoiGianNhamXacNhanMs,
                THOI_GIAN_NHAM_MAT_MIN,
                THOI_GIAN_NHAM_MAT_MAX
            )

        seekThoiGianMoMieng.progress =
            thoiGianThanhProgress(
                macDinh.moMieng.thoiGianGiuBackMs,
                THOI_GIAN_MIENG_MIN,
                THOI_GIAN_MIENG_MAX
            )

        seekKhoangMoMiengHaiLan.progress =
            thoiGianThanhProgress(
                macDinh.moMiengHaiLan.khoangChoLanHaiMs,
                THOI_GIAN_HAI_LAN_MIN,
                THOI_GIAN_HAI_LAN_MAX
            )

        yeuCauDatLaiPhanHoi =
            true

        capNhatNhanPhanHoi()
        danhDauCoThayDoi()
    }

    private fun taoCauHinhTuGiaoDien():
            CauHinhNhanDienCuChi {

        val hienTai =
            khoCauHinh.layCauHinh()

        val macDinh =
            CauHinhNhanDienCuChi.macDinh()

        val doiQuayTrai =
            seekDoNhayQuayTrai.progress !=
                    mucDoTuNguong(
                        cauHinhBanDau.huongDau.layNguongQuayTrai(),
                        NGUONG_YAW_NHAY_NHAT,
                        NGUONG_YAW_ON_DINH_NHAT
                    )

        val doiQuayPhai =
            seekDoNhayQuayPhai.progress !=
                    mucDoTuNguong(
                        cauHinhBanDau.huongDau.layNguongQuayPhai(),
                        NGUONG_YAW_NHAY_NHAT,
                        NGUONG_YAW_ON_DINH_NHAT
                    )

        val doiNhinLen =
            seekDoNhayNhinLen.progress !=
                    mucDoTuNguong(
                        cauHinhBanDau.huongDau.layNguongNhinLen(),
                        NGUONG_PITCH_NHAY_NHAT,
                        NGUONG_PITCH_ON_DINH_NHAT
                    )

        val doiNhinXuong =
            seekDoNhayNhinXuong.progress !=
                    mucDoTuNguong(
                        cauHinhBanDau.huongDau.layNguongNhinXuong(),
                        NGUONG_PITCH_NHAY_NHAT,
                        NGUONG_PITCH_ON_DINH_NHAT
                    )

        val doiNghiengTrai =
            seekDoNhayNghiengTrai.progress !=
                    mucDoTuNguong(
                        cauHinhBanDau.nghiengDau.nguongTrai.absoluteValue,
                        NGUONG_NGHIENG_NHAY_NHAT,
                        NGUONG_NGHIENG_ON_DINH_NHAT
                    )

        val doiNghiengPhai =
            seekDoNhayNghiengPhai.progress !=
                    mucDoTuNguong(
                        cauHinhBanDau.nghiengDau.nguongPhai.absoluteValue,
                        NGUONG_NGHIENG_NHAY_NHAT,
                        NGUONG_NGHIENG_ON_DINH_NHAT
                    )

        val doiNhamMat =
            seekDoNhayNhamMat.progress !=
                    mucDoTuNguong(
                        cauHinhBanDau.nhamHaiMat.nguongDong,
                        NGUONG_MAT_NHAY_NHAT,
                        NGUONG_MAT_ON_DINH_NHAT
                    )

        val doiMoMieng =
            seekDoNhayMoMieng.progress !=
                    mucDoTuNguong(
                        cauHinhBanDau.moMieng.nguongMo,
                        NGUONG_MIENG_NHAY_NHAT,
                        NGUONG_MIENG_ON_DINH_NHAT
                    )

        val doiMoMiengHaiLan =
            seekDoNhayMoMiengHaiLan.progress !=
                    mucDoTuNguong(
                        cauHinhBanDau.moMiengHaiLan.nguongMo,
                        NGUONG_MIENG_HAI_LAN_NHAY_NHAT,
                        NGUONG_MIENG_HAI_LAN_ON_DINH_NHAT
                    )

        val huongDauMoi =
            hienTai.huongDau.copy(
                nguongQuayTrai =
                    if (
                        yeuCauDatLaiDoNhay ||
                        doiQuayTrai
                    ) {
                        nguongTuMucDo(
                            seekDoNhayQuayTrai.progress,
                            NGUONG_YAW_NHAY_NHAT,
                            NGUONG_YAW_ON_DINH_NHAT
                        )
                    } else {
                        hienTai.huongDau.nguongQuayTrai
                    },

                nguongQuayPhai =
                    if (
                        yeuCauDatLaiDoNhay ||
                        doiQuayPhai
                    ) {
                        nguongTuMucDo(
                            seekDoNhayQuayPhai.progress,
                            NGUONG_YAW_NHAY_NHAT,
                            NGUONG_YAW_ON_DINH_NHAT
                        )
                    } else {
                        hienTai.huongDau.nguongQuayPhai
                    },

                nguongNhinLen =
                    if (
                        yeuCauDatLaiDoNhay ||
                        doiNhinLen
                    ) {
                        nguongTuMucDo(
                            seekDoNhayNhinLen.progress,
                            NGUONG_PITCH_NHAY_NHAT,
                            NGUONG_PITCH_ON_DINH_NHAT
                        )
                    } else {
                        hienTai.huongDau.nguongNhinLen
                    },

                nguongNhinXuong =
                    if (
                        yeuCauDatLaiDoNhay ||
                        doiNhinXuong
                    ) {
                        nguongTuMucDo(
                            seekDoNhayNhinXuong.progress,
                            NGUONG_PITCH_NHAY_NHAT,
                            NGUONG_PITCH_ON_DINH_NHAT
                        )
                    } else {
                        hienTai.huongDau.nguongNhinXuong
                    },

                thoiGianGiuYawMs =
                    if (
                        yeuCauDatLaiPhanHoi ||
                        doiThoiGianHuongDau()
                    ) {
                        layThoiGianHuongDau()
                    } else {
                        hienTai.huongDau.thoiGianGiuYawMs
                    },

                thoiGianGiuPitchMs =
                    if (
                        yeuCauDatLaiPhanHoi ||
                        doiThoiGianHuongDau()
                    ) {
                        layThoiGianHuongDau()
                    } else {
                        hienTai.huongDau.thoiGianGiuPitchMs
                    }
            )

        val nguongMatDongMoi =
            nguongTuMucDo(
                seekDoNhayNhamMat.progress,
                NGUONG_MAT_NHAY_NHAT,
                NGUONG_MAT_ON_DINH_NHAT
            )

        val nguongMiengMoMoi =
            nguongTuMucDo(
                seekDoNhayMoMieng.progress,
                NGUONG_MIENG_NHAY_NHAT,
                NGUONG_MIENG_ON_DINH_NHAT
            )

        val nguongMiengHaiLanMoi =
            nguongTuMucDo(
                seekDoNhayMoMiengHaiLan.progress,
                NGUONG_MIENG_HAI_LAN_NHAY_NHAT,
                NGUONG_MIENG_HAI_LAN_ON_DINH_NHAT
            )

        return hienTai.copy(

            huongDau =
                huongDauMoi,

            nghiengDau =
                hienTai.nghiengDau.copy(
                    nguongTrai =
                        if (
                            yeuCauDatLaiDoNhay ||
                            doiNghiengTrai
                        ) {
                            -nguongTuMucDo(
                                seekDoNhayNghiengTrai.progress,
                                NGUONG_NGHIENG_NHAY_NHAT,
                                NGUONG_NGHIENG_ON_DINH_NHAT
                            )
                        } else {
                            hienTai.nghiengDau.nguongTrai
                        },

                    nguongPhai =
                        if (
                            yeuCauDatLaiDoNhay ||
                            doiNghiengPhai
                        ) {
                            nguongTuMucDo(
                                seekDoNhayNghiengPhai.progress,
                                NGUONG_NGHIENG_NHAY_NHAT,
                                NGUONG_NGHIENG_ON_DINH_NHAT
                            )
                        } else {
                            hienTai.nghiengDau.nguongPhai
                        },

                    thoiGianGiuMs =
                        if (
                            yeuCauDatLaiPhanHoi ||
                            doiThoiGianNghiengDau()
                        ) {
                            layThoiGianNghiengDau()
                        } else {
                            hienTai.nghiengDau.thoiGianGiuMs
                        }
                ),

            nhamHaiMat =
                hienTai.nhamHaiMat.copy(
                    nguongDong =
                        if (
                            yeuCauDatLaiDoNhay ||
                            doiNhamMat
                        ) {
                            nguongMatDongMoi
                        } else {
                            hienTai.nhamHaiMat.nguongDong
                        },

                    nguongMo =
                        if (
                            yeuCauDatLaiDoNhay ||
                            doiNhamMat
                        ) {
                            (
                                    nguongMatDongMoi -
                                            0.30f
                                    ).coerceIn(
                                    0.18f,
                                    0.52f
                                )
                        } else {
                            hienTai.nhamHaiMat.nguongMo
                        },

                    thoiGianNhamXacNhanMs =
                        if (
                            yeuCauDatLaiPhanHoi ||
                            doiThoiGianNhamMat()
                        ) {
                            layThoiGianNhamMat()
                        } else {
                            hienTai.nhamHaiMat.thoiGianNhamXacNhanMs
                        }
                ),

            moMieng =
                hienTai.moMieng.copy(
                    nguongMo =
                        if (
                            yeuCauDatLaiDoNhay ||
                            doiMoMieng
                        ) {
                            nguongMiengMoMoi
                        } else {
                            hienTai.moMieng.nguongMo
                        },

                    nguongDong =
                        if (
                            yeuCauDatLaiDoNhay ||
                            doiMoMieng
                        ) {
                            (
                                    nguongMiengMoMoi *
                                            0.30f
                                    ).coerceIn(
                                    0.07f,
                                    0.18f
                                )
                        } else {
                            hienTai.moMieng.nguongDong
                        },

                    thoiGianGiuBackMs =
                        if (
                            yeuCauDatLaiPhanHoi ||
                            doiThoiGianMoMieng()
                        ) {
                            layThoiGianMoMieng()
                        } else {
                            hienTai.moMieng.thoiGianGiuBackMs
                        }
                ),

            moMiengHaiLan =
                hienTai.moMiengHaiLan.copy(
                    nguongMo =
                        if (
                            yeuCauDatLaiDoNhay ||
                            doiMoMiengHaiLan
                        ) {
                            nguongMiengHaiLanMoi
                        } else {
                            hienTai.moMiengHaiLan.nguongMo
                        },

                    nguongDong =
                        if (
                            yeuCauDatLaiDoNhay ||
                            doiMoMiengHaiLan
                        ) {
                            (
                                    nguongMiengHaiLanMoi *
                                            0.60f
                                    ).coerceIn(
                                    0.10f,
                                    0.25f
                                )
                        } else {
                            hienTai.moMiengHaiLan.nguongDong
                        },

                    khoangChoLanHaiMs =
                        if (
                            yeuCauDatLaiPhanHoi ||
                            doiKhoangMoMiengHaiLan()
                        ) {
                            layKhoangMoMiengHaiLan()
                        } else {
                            hienTai.moMiengHaiLan.khoangChoLanHaiMs
                        }
                ),

            hanhDong =
                hanhDongTam
                    .chuanHoaChoCuChiToanCuc()
        )
    }

    private fun luuThayDoi(
        hienThongBao: Boolean
    ): CauHinhNhanDienCuChi {

        val cauHinhMoi =
            taoCauHinhTuGiaoDien()

        khoCauHinh.luuCauHinhTuyChinh(
            cauHinhMoi
        )

        cauHinhBanDau =
            cauHinhMoi

        cauHinhDaThayDoi =
            true

        ganCauHinhLenGiaoDien(
            cauHinhMoi
        )

        capNhatTrangThai()

        if (hienThongBao) {
            Toast.makeText(
                this,
                "Đã lưu cấu hình",
                Toast.LENGTH_SHORT
            ).show()
        }

        return cauHinhMoi
    }

    private fun xuLyTaoBanSao() {

        if (coThayDoiChuaLuu) {

            AlertDialog.Builder(this)
                .setTitle(
                    "Có thay đổi chưa lưu"
                )
                .setMessage(
                    "Lưu cấu hình trước khi tạo bản sao?"
                )
                .setPositiveButton(
                    "LƯU VÀ TẠO"
                ) { _, _ ->

                    luuThayDoi(false)
                    taoBanSao()
                }
                .setNegativeButton(
                    "HỦY",
                    null
                )
                .show()

            return
        }

        taoBanSao()
    }

    private fun taoBanSao() {

        khoCauHinh.saoLuuCauHinhCaNhan()

        capNhatTrangThaiBanSao()

        Toast.makeText(
            this,
            "Đã tạo bản sao trên thiết bị",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun xuLyKhoiPhucBanSao() {

        if (!khoCauHinh.coBanSaoCauHinh()) {

            Toast.makeText(
                this,
                "Chưa có bản sao cấu hình",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        AlertDialog.Builder(this)
            .setTitle(
                "Khôi phục bản sao?"
            )
            .setMessage(
                "Cấu hình hiện tại sẽ được thay bằng bản sao đã lưu."
            )
            .setPositiveButton(
                "KHÔI PHỤC"
            ) { _, _ ->

                val thanhCong =
                    khoCauHinh.khoiPhucCauHinhTuBanSao()

                if (!thanhCong) {

                    Toast.makeText(
                        this,
                        "Không thể khôi phục bản sao",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                cauHinhBanDau =
                    khoCauHinh.layCauHinh()

                ganCauHinhLenGiaoDien(
                    cauHinhBanDau
                )

                cauHinhDaThayDoi =
                    true

                capNhatTrangThai()

                Toast.makeText(
                    this,
                    "Đã khôi phục cấu hình",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(
                "HỦY",
                null
            )
            .show()
    }

    private fun xacNhanDatLaiTatCa() {

        AlertDialog.Builder(this)
            .setTitle(
                "Đặt lại toàn bộ?"
            )
            .setMessage(
                "Độ nhạy, hành động và dữ liệu hiệu chỉnh đang dùng sẽ về mặc định. Bản sao cá nhân vẫn được giữ."
            )
            .setPositiveButton(
                "ĐẶT LẠI"
            ) { _, _ ->

                khoCauHinh.datLaiCauHinhMacDinh()

                cauHinhBanDau =
                    khoCauHinh.layCauHinh()

                ganCauHinhLenGiaoDien(
                    cauHinhBanDau
                )

                cauHinhDaThayDoi =
                    true

                capNhatTrangThai()

                Toast.makeText(
                    this,
                    "Đã khôi phục mặc định",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(
                "HỦY",
                null
            )
            .show()
    }

    private fun xuLyHieuChinhLai() {

        if (!coThayDoiChuaLuu) {
            ketThucVaYeuCauHieuChinh()
            return
        }

        AlertDialog.Builder(this)
            .setTitle(
                "Có thay đổi chưa lưu"
            )
            .setMessage(
                "Bạn muốn lưu trước khi hiệu chỉnh?"
            )
            .setPositiveButton(
                "LƯU"
            ) { _, _ ->

                luuThayDoi(false)
                ketThucVaYeuCauHieuChinh()
            }
            .setNeutralButton(
                "KHÔNG LƯU"
            ) { _, _ ->
                ketThucVaYeuCauHieuChinh()
            }
            .setNegativeButton(
                "HỦY",
                null
            )
            .show()
    }

    private fun xuLyVeTrangChu() {

        if (!coThayDoiChuaLuu) {
            ketThucVaYeuCauTrangChu()
            return
        }

        AlertDialog.Builder(this)
            .setTitle(
                "Có thay đổi chưa lưu"
            )
            .setMessage(
                "Bạn muốn lưu trước khi về Trang chủ?"
            )
            .setPositiveButton(
                "LƯU"
            ) { _, _ ->

                luuThayDoi(false)
                ketThucVaYeuCauTrangChu()
            }
            .setNeutralButton(
                "BỎ THAY ĐỔI"
            ) { _, _ ->
                ketThucVaYeuCauTrangChu()
            }
            .setNegativeButton(
                "Ở LẠI",
                null
            )
            .show()
    }

    private fun xuLyQuayLai() {

        if (!coThayDoiChuaLuu) {
            ketThucBinhThuong()
            return
        }

        AlertDialog.Builder(this)
            .setTitle(
                "Có thay đổi chưa lưu"
            )
            .setMessage(
                "Bạn muốn lưu trước khi quay lại?"
            )
            .setPositiveButton(
                "LƯU"
            ) { _, _ ->

                luuThayDoi(false)
                ketThucBinhThuong()
            }
            .setNeutralButton(
                "BỎ THAY ĐỔI"
            ) { _, _ ->
                ketThucBinhThuong()
            }
            .setNegativeButton(
                "Ở LẠI",
                null
            )
            .show()
    }


    private fun ketThucVaYeuCauTrangChu() {

        val duLieu =
            Intent().apply {
                putExtra(
                    EXTRA_YEU_CAU_TRANG_CHU,
                    true
                )
                putExtra(
                    EXTRA_CAU_HINH_DA_THAY_DOI,
                    cauHinhDaThayDoi
                )
            }

        setResult(
            RESULT_OK,
            duLieu
        )

        dongManHinhKhongHieuUng()
    }

    private fun ketThucVaYeuCauHuongDan() {

        if (coThayDoiChuaLuu) {
            luuThayDoi(
                hienThongBao = false
            )
        }

        val duLieu =
            Intent().apply {
                putExtra(
                    EXTRA_YEU_CAU_HUONG_DAN,
                    true
                )
                putExtra(
                    EXTRA_CAU_HINH_DA_THAY_DOI,
                    cauHinhDaThayDoi
                )
            }

        setResult(
            RESULT_OK,
            duLieu
        )

        dongManHinhKhongHieuUng()
    }

    private fun ketThucVaYeuCauHieuChinh() {

        val duLieu =
            Intent().apply {
                putExtra(
                    EXTRA_YEU_CAU_HIEU_CHINH,
                    true
                )
                putExtra(
                    EXTRA_CAU_HINH_DA_THAY_DOI,
                    cauHinhDaThayDoi
                )
            }

        setResult(
            RESULT_OK,
            duLieu
        )

        dongManHinhKhongHieuUng()
    }

    private fun ketThucBinhThuong() {

        val duLieu =
            Intent().apply {
                putExtra(
                    EXTRA_CAU_HINH_DA_THAY_DOI,
                    cauHinhDaThayDoi
                )
            }

        setResult(
            RESULT_OK,
            duLieu
        )

        dongManHinhKhongHieuUng()
    }

    private fun capNhatTrangThai() {
        capNhatTrangThaiHieuChinh()
        capNhatTrangThaiBanSao()
        capNhatTrangThaiHeThong()
        capNhatTrangThaiLuu()
    }

    private fun capNhatTrangThaiHieuChinh() {

        txtTrangThaiHieuChinh.text =
            if (khoCauHinh.daHieuChinh()) {
                "Đã có hồ sơ hiệu chỉnh cá nhân"
            } else {
                "Chưa hiệu chỉnh • Đang dùng cấu hình mặc định"
            }
    }

    private fun capNhatTrangThaiBanSao() {

        val thoiGian =
            khoCauHinh.layThoiGianBanSao()

        txtTrangThaiBanSao.text =
            if (thoiGian == null) {
                "Chưa có bản sao cấu hình"
            } else {

                val dinhDang =
                    SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    )

                "Bản sao: ${
                    dinhDang.format(
                        Date(thoiGian)
                    )
                }"
            }

        btnTaiXuongCauHinh.isEnabled =
            khoCauHinh.coBanSaoCauHinh()
    }

    private fun capNhatTrangThaiHeThong() {

        txtTrangThaiTroNang.text =
            if (dichVuTruyCapDaBat()) {
                "Đã bật"
            } else {
                "Chưa bật"
            }

        val coQuyenCamera =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        txtTrangThaiQuyenCamera.text =
            if (coQuyenCamera) {
                "Đã cấp quyền"
            } else {
                "Chưa cấp quyền"
            }
    }

    private fun capNhatTrangThaiLuu() {

        txtTrangThaiLuu.text =
            if (coThayDoiChuaLuu) {
                "Chưa lưu"
            } else {
                "Đã lưu"
            }

        txtTrangThaiLuu.setTextColor(
            ContextCompat.getColor(
                this,
                if (coThayDoiChuaLuu) {
                    R.color.do_trang_thai
                } else {
                    R.color.xanh_chinh
                }
            )
        )

        btnLuuThayDoi.isEnabled =
            coThayDoiChuaLuu
    }

    private fun danhDauCoThayDoi() {
        coThayDoiChuaLuu =
            true

        capNhatTrangThaiLuu()
    }

    private fun dichVuTruyCapDaBat(): Boolean {

        val accessibilityManager =
            getSystemService(
                AccessibilityManager::class.java
            )

        val componentFaceAccess =
            ComponentName(
                this,
                DichVuTruyCapFaceAccess::class.java
            )

        return accessibilityManager
            .getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            )
            .any { thongTinDichVu ->

                val serviceInfo =
                    thongTinDichVu
                        .resolveInfo
                        .serviceInfo

                ComponentName(
                    serviceInfo.packageName,
                    serviceInfo.name
                ) == componentFaceAccess
            }
    }

    private fun moCaiDatTroNang() {

        val componentFaceAccess =
            ComponentName(
                this,
                DichVuTruyCapFaceAccess::class.java
            )

        val intentChiTiet =
            Intent(
                ACTION_ACCESSIBILITY_DETAILS_SETTINGS
            ).apply {
                putExtra(
                    Intent.EXTRA_COMPONENT_NAME,
                    componentFaceAccess
                )
            }

        try {
            startActivity(
                intentChiTiet
            )
            return
        } catch (
            exception: ActivityNotFoundException
        ) {
            // Mở trang chung
        } catch (
            exception: SecurityException
        ) {
            // Mở trang chung
        }

        try {
            startActivity(
                Intent(
                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                )
            )
        } catch (
            exception: ActivityNotFoundException
        ) {
            Toast.makeText(
                this,
                "Không thể mở cài đặt Trợ năng",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun moCaiDatUngDung() {

        try {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse(
                        "package:$packageName"
                    )
                )
            )
        } catch (
            exception: ActivityNotFoundException
        ) {
            Toast.makeText(
                this,
                "Không thể mở cài đặt ứng dụng",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun doiThoiGianHuongDau(): Boolean =
        seekThoiGianHuongDau.progress !=
                thoiGianThanhProgress(
                    (
                            cauHinhBanDau.huongDau.thoiGianGiuYawMs +
                                    cauHinhBanDau.huongDau.thoiGianGiuPitchMs
                            ) / 2L,
                    THOI_GIAN_HUONG_DAU_MIN,
                    THOI_GIAN_HUONG_DAU_MAX
                )

    private fun doiThoiGianNghiengDau(): Boolean =
        seekThoiGianNghiengDau.progress !=
                thoiGianThanhProgress(
                    cauHinhBanDau.nghiengDau.thoiGianGiuMs,
                    THOI_GIAN_NGHIENG_DAU_MIN,
                    THOI_GIAN_NGHIENG_DAU_MAX
                )

    private fun doiThoiGianNhamMat(): Boolean =
        seekThoiGianNhamMat.progress !=
                thoiGianThanhProgress(
                    cauHinhBanDau.nhamHaiMat.thoiGianNhamXacNhanMs,
                    THOI_GIAN_NHAM_MAT_MIN,
                    THOI_GIAN_NHAM_MAT_MAX
                )

    private fun doiThoiGianMoMieng(): Boolean =
        seekThoiGianMoMieng.progress !=
                thoiGianThanhProgress(
                    cauHinhBanDau.moMieng.thoiGianGiuBackMs,
                    THOI_GIAN_MIENG_MIN,
                    THOI_GIAN_MIENG_MAX
                )

    private fun doiKhoangMoMiengHaiLan(): Boolean =
        seekKhoangMoMiengHaiLan.progress !=
                thoiGianThanhProgress(
                    cauHinhBanDau.moMiengHaiLan.khoangChoLanHaiMs,
                    THOI_GIAN_HAI_LAN_MIN,
                    THOI_GIAN_HAI_LAN_MAX
                )

    private fun moTaDoNhay(
        progress: Int
    ): String =
        when {
            progress >= 70 ->
                "Nhạy"

            progress <= 30 ->
                "Ổn định"

            else ->
                "Cân bằng"
        }

    private fun mucDoTuNguong(
        nguong: Float,
        nguongNhayNhat: Float,
        nguongOnDinhNhat: Float
    ): Int {

        val doRong =
            nguongOnDinhNhat -
                    nguongNhayNhat

        if (doRong <= 0f) {
            return 50
        }

        return (
                (
                        nguongOnDinhNhat -
                                nguong
                        ) /
                        doRong *
                        100f
                )
            .roundToInt()
            .coerceIn(
                0,
                100
            )
    }

    private fun nguongTuMucDo(
        mucDo: Int,
        nguongNhayNhat: Float,
        nguongOnDinhNhat: Float
    ): Float {

        val tyLe =
            (
                    mucDo /
                            100f
                    ).coerceIn(
                    0f,
                    1f
                )

        return nguongOnDinhNhat -
                tyLe *
                (
                        nguongOnDinhNhat -
                                nguongNhayNhat
                        )
    }

    private fun thoiGianThanhProgress(
        giaTri: Long,
        toiThieu: Long,
        toiDa: Long
    ): Int {

        if (toiDa <= toiThieu) {
            return 0
        }

        return (
                (
                        giaTri.coerceIn(
                            toiThieu,
                            toiDa
                        ) -
                                toiThieu
                        ).toFloat() /
                        (
                                toiDa -
                                        toiThieu
                                ).toFloat() *
                        100f
                )
            .roundToInt()
            .coerceIn(
                0,
                100
            )
    }

    private fun progressThanhThoiGian(
        progress: Int,
        toiThieu: Long,
        toiDa: Long,
        buoc: Long
    ): Long {

        val giaTri =
            toiThieu +
                    (
                            (
                                    toiDa -
                                            toiThieu
                                    ) *
                                    (
                                            progress.coerceIn(
                                                0,
                                                100
                                            ) /
                                                    100f
                                            )
                            )
                        .roundToLong()

        return (
                (
                        giaTri.toFloat() /
                                buoc.toFloat()
                        )
                    .roundToLong() *
                        buoc
                )
            .coerceIn(
                toiThieu,
                toiDa
            )
    }

    private fun layThoiGianHuongDau(): Long =
        progressThanhThoiGian(
            seekThoiGianHuongDau.progress,
            THOI_GIAN_HUONG_DAU_MIN,
            THOI_GIAN_HUONG_DAU_MAX,
            5L
        )

    private fun layThoiGianNghiengDau(): Long =
        progressThanhThoiGian(
            seekThoiGianNghiengDau.progress,
            THOI_GIAN_NGHIENG_DAU_MIN,
            THOI_GIAN_NGHIENG_DAU_MAX,
            10L
        )

    private fun layThoiGianNhamMat(): Long =
        progressThanhThoiGian(
            seekThoiGianNhamMat.progress,
            THOI_GIAN_NHAM_MAT_MIN,
            THOI_GIAN_NHAM_MAT_MAX,
            25L
        )

    private fun layThoiGianMoMieng(): Long =
        progressThanhThoiGian(
            seekThoiGianMoMieng.progress,
            THOI_GIAN_MIENG_MIN,
            THOI_GIAN_MIENG_MAX,
            25L
        )

    private fun layKhoangMoMiengHaiLan(): Long =
        progressThanhThoiGian(
            seekKhoangMoMiengHaiLan.progress,
            THOI_GIAN_HAI_LAN_MIN,
            THOI_GIAN_HAI_LAN_MAX,
            25L
        )

    private enum class CuChiToanCuc(
        val tenHienThi: String
    ) {
        NGHIENG_TRAI("Nghiêng trái"),
        NGHIENG_PHAI("Nghiêng phải"),
        MO_MIENG("Há miệng")
    }

    companion object {

        private const val DO_DICH_CHUYEN_NOI_DUNG_DP =
            10f

        private const val THOI_GIAN_HIEU_UNG_NOI_DUNG_MS =
            220L

        const val EXTRA_CAU_HINH_DA_THAY_DOI =
            "cau_hinh_da_thay_doi"

        const val EXTRA_YEU_CAU_HIEU_CHINH =
            "yeu_cau_hieu_chinh"

        const val EXTRA_YEU_CAU_HUONG_DAN =
            "yeu_cau_huong_dan"

        const val EXTRA_YEU_CAU_TRANG_CHU =
            "yeu_cau_trang_chu"

        private const val ACTION_ACCESSIBILITY_DETAILS_SETTINGS =
            "android.settings.ACCESSIBILITY_DETAILS_SETTINGS"

        private const val NGUONG_YAW_NHAY_NHAT =
            8f

        private const val NGUONG_YAW_ON_DINH_NHAT =
            22f

        private const val NGUONG_PITCH_NHAY_NHAT =
            6f

        private const val NGUONG_PITCH_ON_DINH_NHAT =
            18f

        private const val NGUONG_NGHIENG_NHAY_NHAT =
            9f

        private const val NGUONG_NGHIENG_ON_DINH_NHAT =
            22f

        private const val NGUONG_MAT_NHAY_NHAT =
            0.48f

        private const val NGUONG_MAT_ON_DINH_NHAT =
            0.82f

        private const val NGUONG_MIENG_NHAY_NHAT =
            0.22f

        private const val NGUONG_MIENG_ON_DINH_NHAT =
            0.50f

        private const val NGUONG_MIENG_HAI_LAN_NHAY_NHAT =
            0.18f

        private const val NGUONG_MIENG_HAI_LAN_ON_DINH_NHAT =
            0.45f

        private const val THOI_GIAN_HUONG_DAU_MIN =
            80L

        private const val THOI_GIAN_HUONG_DAU_MAX =
            350L

        private const val THOI_GIAN_NGHIENG_DAU_MIN =
            150L

        private const val THOI_GIAN_NGHIENG_DAU_MAX =
            500L

        private const val THOI_GIAN_NHAM_MAT_MIN =
            250L

        private const val THOI_GIAN_NHAM_MAT_MAX =
            800L

        private const val THOI_GIAN_MIENG_MIN =
            300L

        private const val THOI_GIAN_MIENG_MAX =
            900L

        private const val THOI_GIAN_HAI_LAN_MIN =
            450L

        private const val THOI_GIAN_HAI_LAN_MAX =
            1000L
    }
}
