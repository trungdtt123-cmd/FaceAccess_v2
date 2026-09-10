// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2

import com.example.faceaccess.v2.R
import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.provider.Settings
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.MotionEvent
import kotlin.math.abs
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import com.example.faceaccess.v2.huongdan.CheDoHuongDan
import com.example.faceaccess.v2.huongdan.DuLieuHuongDanCuChi
import com.example.faceaccess.v2.huongdan.MucHuongDanCuChi
import com.airbnb.lottie.LottieAnimationView
import com.example.faceaccess.v2.ai.hieuchinh.BoChuanHoaDuLieuKhuonMat
import com.example.faceaccess.v2.ai.hieuchinh.BoDieuKhienHieuChinh
import com.example.faceaccess.v2.ai.hieuchinh.BoHocNguongThichNghi
import com.example.faceaccess.v2.ai.hieuchinh.BoThuThapMauHieuChinh
import com.example.faceaccess.v2.ai.hieuchinh.BuocHieuChinh
import com.example.faceaccess.v2.ai.hieuchinh.TrangThaiHieuChinh
import com.example.faceaccess.v2.caidat.CaiDatActivity
import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhNhanDienCuChi
import com.example.faceaccess.v2.cuchi.cauhinh.HanhDongTuyChinhCuChi
import com.example.faceaccess.v2.cuchi.cauhinh.KhoCauHinhNhanDienCuChi
import com.example.faceaccess.v2.camera.QuanLyCamera
import com.example.faceaccess.v2.chedo.BoDinhTuyenCheDo
import com.example.faceaccess.v2.chedo.CheDoDieuKhien
import com.example.faceaccess.v2.cuchi.nghiengdau.HuongNghiengDau
import com.example.faceaccess.v2.cuchi.nghiengdau.NhanDienNghiengDau
import com.example.faceaccess.v2.cuchi.huongdau.HuongDau
import com.example.faceaccess.v2.cuchi.huongdau.NhanDienHuongDau
import com.example.faceaccess.v2.cuchi.mieng.NhanDienMoMieng
import com.example.faceaccess.v2.cuchi.mieng.NhanDienMoMiengHaiLan
import com.example.faceaccess.v2.cuchi.mat.NhanDienNhamHaiMat
import com.example.faceaccess.v2.dichvu.DichVuTheoDoiFaceAccess
import com.example.faceaccess.v2.dieuphoi.DieuPhoiCuChi
import com.example.faceaccess.v2.dieuphoi.LenhToanCuc
import com.example.faceaccess.v2.dieuphoi.dieuhuong.LenhDieuHuong
import com.example.faceaccess.v2.dieuphoi.media.LenhMedia
import com.example.faceaccess.v2.dieuphoi.media.BoDieuKhienMedia
import com.example.faceaccess.v2.dieuphoi.hotro.LenhHoTro
import com.example.faceaccess.v2.dieuphoi.hotro.BoDieuKhienLienHeHoTro
import com.example.faceaccess.v2.dieuphoi.SuKienCuChi
import com.example.faceaccess.v2.khuonmat.DuLieuKhuonMat
import com.example.faceaccess.v2.khuonmat.PhanTichKhungHinhKhuonMat
import com.example.faceaccess.v2.khuonmat.TrichXuatDuLieuKhuonMat
import com.example.faceaccess.v2.khuonmat.XuLyKhuonMat
import com.example.faceaccess.v2.truycap.DichVuTruyCapFaceAccess
import com.example.faceaccess.v2.thongbao.ThongBaoFaceAccess
import com.example.faceaccess.v2.dieuphoi.hotro.DanhSachLienHeHoTroActivity
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import java.util.Locale

class ManHinhChinhActivity : AppCompatActivity() {

    // CAMERA

    private lateinit var quanLyCamera: QuanLyCamera

    private lateinit var khungCamera: PreviewView

    private lateinit var txtTrangThaiCamera: TextView

    @Volatile
    private var cameraDangBat = false

    @Volatile
    private var cameraDangKhoiDong = false

    @Volatile
    private var theoDoiDangHoatDong = false

    @Volatile
    private var dangChoCameraNenNhaQuyen = false

    @Volatile
    private var dangChoBatDichVuTruyCap = false

    private var daDangKyBoNhanBanGiaoCamera = false

    private var soLanThuBanGiaoCamera = 0

    // DETECTOR MỞ MIỆNG

    private fun khoiTaoNhanDienMoMieng() {

        nhanDienMoMieng =
            NhanDienMoMieng(
                cauHinh =
                    cauHinhNhanDienCuChi.moMieng
            ) {

                Log.d(
                    TAG_CU_CHI_MIENG,
                    "APP: MO MIENG"
                )

                dieuPhoiCuChi.xuLy(
                    SuKienCuChi.MoMieng
                )
            }
    }

    private fun khoiTaoNhanDienMoMiengHaiLan() {

        nhanDienMoMiengHaiLan =
            NhanDienMoMiengHaiLan(
                cauHinh =
                    cauHinhNhanDienCuChi.moMiengHaiLan,
                khiMoMotLan = {

                    // Mở giữ do detector đơn xử lý
                    Log.d(
                        TAG_CU_CHI_MIENG,
                        "APP: MO MIENG GIU - DETECTOR DON"
                    )
                },
                khiMoHaiLan = {

                    Log.d(
                        TAG_CU_CHI_MIENG,
                        "APP: MO MIENG HAI LAN - DOI KHOA CON TRO"
                    )

                    dieuPhoiCuChi.xuLy(
                        SuKienCuChi.MoMiengHaiLan
                    )
                }
            )
    }

    private fun canTheoDoiMoMiengHaiLan(
        cheDoHienTai: CheDoDieuKhien
    ): Boolean {

        return when (
            cauHinhNhanDienCuChi
                .hanhDong
                .moMiengHaiLan
        ) {
            HanhDongTuyChinhCuChi.KHONG_SU_DUNG ->
                false

            HanhDongTuyChinhCuChi.THEO_CHE_DO ->
                cheDoHienTai ==
                        CheDoDieuKhien.CON_TRO

            else ->
                true
        }
    }

    private fun datLaiNhanDienMieng() {
        if (::nhanDienMoMieng.isInitialized) {
            nhanDienMoMieng.datLai()
        }

        if (::nhanDienMoMiengHaiLan.isInitialized) {
            nhanDienMoMiengHaiLan.datLai()
        }
    }

    // DETECTOR HƯỚNG ĐẦU YAW / PITCH

    private fun khoiTaoNhanDienHuongDau() {

        nhanDienHuongDau =
            NhanDienHuongDau(
                cauHinh =
                    cauHinhNhanDienCuChi.huongDau
            ) { huong ->

                val tenHuong =
                    when (huong) {

                        HuongDau.TRAI ->
                            "TRAI"

                        HuongDau.PHAI ->
                            "PHAI"

                        HuongDau.LEN ->
                            "LEN"

                        HuongDau.XUONG ->
                            "XUONG"
                    }

                Log.d(
                    TAG_CU_CHI_HUONG_DAU,
                    "APP: HUONG $tenHuong"
                )

                dieuPhoiCuChi.xuLy(
                    SuKienCuChi.DieuHuongDau(
                        huong = huong
                    )
                )
            }
    }

    private fun datLaiNhanDienMat() {
        if (::nhanDienNhamHaiMat.isInitialized) {
            nhanDienNhamHaiMat.datLai()
        }
    }

    private fun khoiTaoNhanDienNhamHaiMat() {

        nhanDienNhamHaiMat =
            NhanDienNhamHaiMat(
                cauHinh =
                    cauHinhNhanDienCuChi.nhamHaiMat
            ) {

                dieuPhoiCuChi.xuLy(
                    SuKienCuChi.NhamHaiMat
                )
            }
    }

    // MEDIAPIPE

    private lateinit var xuLyKhuonMat: XuLyKhuonMat

    private lateinit var phanTichKhungHinhKhuonMat:
            PhanTichKhungHinhKhuonMat

    private lateinit var trichXuatDuLieuKhuonMat:
            TrichXuatDuLieuKhuonMat

    // CẤU HÌNH NHẬN DIỆN

    private lateinit var khoCauHinhNhanDienCuChi:
            KhoCauHinhNhanDienCuChi

    private lateinit var cauHinhNhanDienCuChi:
            CauHinhNhanDienCuChi

    private lateinit var boChuanHoaDuLieuKhuonMat:
            BoChuanHoaDuLieuKhuonMat

    // HIỆU CHỈNH CÁ NHÂN

    private lateinit var boThuThapMauHieuChinh:
            BoThuThapMauHieuChinh

    private lateinit var boDieuKhienHieuChinh:
            BoDieuKhienHieuChinh

    private val boHocNguongThichNghi =
        BoHocNguongThichNghi()

    @Volatile
    private var dangHieuChinh =
        false

    @Volatile
    private var dangChuyenBuocHieuChinh =
        false

    private var viTriBuocHieuChinh =
        0

    private val danhSachBuocHieuChinh =
        listOf(
            BuocHieuChinh.TRUNG_TINH,
            BuocHieuChinh.QUAY_TRAI,
            BuocHieuChinh.QUAY_PHAI,
            BuocHieuChinh.NHIN_LEN,
            BuocHieuChinh.NHIN_XUONG,
            BuocHieuChinh.NGHIENG_TRAI,
            BuocHieuChinh.NGHIENG_PHAI,
            BuocHieuChinh.NHAM_HAI_MAT,
            BuocHieuChinh.MO_MIENG
        )

    // NHẬN DIỆN CỬ CHỈ

    private lateinit var nhanDienNghiengDau:
            NhanDienNghiengDau

    private lateinit var nhanDienMoMieng:
            NhanDienMoMieng

    private lateinit var nhanDienMoMiengHaiLan:
            NhanDienMoMiengHaiLan

    private lateinit var nhanDienHuongDau:
            NhanDienHuongDau

    private lateinit var nhanDienNhamHaiMat:
            NhanDienNhamHaiMat


    // ĐIỀU PHỐI CỬ CHỈ

    private lateinit var dieuPhoiCuChi:
            DieuPhoiCuChi

    private lateinit var boDieuKhienMedia:
            BoDieuKhienMedia

    private lateinit var boDieuKhienLienHeHoTro:
            BoDieuKhienLienHeHoTro

    // CHẾ ĐỘ

    private lateinit var boDinhTuyenCheDo:
            BoDinhTuyenCheDo

    // GIAO DIỆN HỆ THỐNG

    private lateinit var btnBatDauTheoDoi: Button

    private lateinit var navTrangChu: TextView

    private lateinit var navHuongDan: TextView

    private lateinit var navHieuChinh: TextView

    private lateinit var navCaiDat: TextView

    private lateinit var txtTrangThaiHeThong: TextView

    private lateinit var khungTrangThaiHeThong: View

    private lateinit var khungTieuDeCamera: View

    private lateinit var khungCameraContainer: View

    private lateinit var khungCameraChinh: View

    private lateinit var noiDungCuonManHinhChinh: View

    private lateinit var noiDungChinh: View

    // GIAO DIỆN HƯỚNG DẪN

    private lateinit var khungHeaderChinh: View

    private lateinit var khungHuongDan: View

    private lateinit var guideDieuHuong: TextView

    private lateinit var guideMedia: TextView

    private lateinit var guideHoTro: TextView

    private lateinit var guideConTro: TextView

    private lateinit var khungTheHuongDan: View

    private lateinit var txtSoThuTuCuChi: TextView

    private lateinit var txtTenCuChi: TextView

    private lateinit var txtMoTaCuChi: TextView

    private lateinit var txtNhanCheDo: TextView

    private lateinit var txtHanhDongCuChi: TextView

    private lateinit var txtGoiYCuChi: TextView

    private lateinit var txtTrangHuongDan: TextView

    private lateinit var animationHuongDan: LottieAnimationView

    private var chamHuongDan: List<View> =
        emptyList()

    // GIAO DIỆN HIỆU CHỈNH

    private lateinit var khungHieuChinh: View

    private lateinit var khungChuanBiHieuChinh: View

    private lateinit var khungTienTrinhHieuChinh: View

    private lateinit var btnBatDauHieuChinh: Button

    private lateinit var txtBuocHieuChinh: TextView

    private lateinit var txtDongTacHieuChinh: TextView

    private lateinit var txtHuongDanHieuChinh: TextView

    private lateinit var txtDemNguocHieuChinh: TextView

    private lateinit var progressHieuChinh: ProgressBar

    private lateinit var txtTienDoHieuChinh: TextView

    private lateinit var txtPhanHoiHieuChinh: TextView

    private lateinit var btnHuyHieuChinh: Button

    // GIAO DIỆN CHẾ ĐỘ

    private lateinit var txtCheDoHienTai: TextView

    private lateinit var cardDieuHuong: TextView

    private lateinit var cardMedia: TextView

    private lateinit var cardHoTro: TextView

    private lateinit var cardConTro: TextView

    // GIAO DIỆN DEBUG

    private lateinit var txtRoll: TextView

    private lateinit var txtYaw: TextView

    private lateinit var txtPitch: TextView

    private lateinit var txtTrangThaiMat: TextView

    private lateinit var txtTrangThaiMieng: TextView

    // TRẠNG THÁI

    private var dangThayKhuonMat: Boolean? = null

    private var thoiGianCapNhatUiGanNhat = 0L

    private var dangChoHieuChinhTuCaiDat = false

    private var dangMoTrangHieuChinh = false

    private var dangMoTrangHuongDan = false

    private var cheDoHuongDanDangXem =
        CheDoDieuKhien.DIEU_HUONG

    private var viTriCuChiHuongDan = 0

    private var danhSachHuongDan:
            List<MucHuongDanCuChi> =
        emptyList()

    // FOREGROUND TRACKING SERVICE + BÀN GIAO CAMERA

    private fun batDichVuTheoDoi() {

        val intent =
            Intent(
                this,
                DichVuTheoDoiFaceAccess::class.java
            )

        ContextCompat.startForegroundService(
            this,
            intent
        )

        Log.d(
            TAG_DICH_VU,
            "Yeu cau BAT dich vu theo doi"
        )
    }

    private fun tatDichVuTheoDoi() {

        val intent =
            Intent(
                this,
                DichVuTheoDoiFaceAccess::class.java
            )

        stopService(intent)

        Log.d(
            TAG_DICH_VU,
            "Yeu cau DUNG dich vu theo doi"
        )
    }

    private fun yeuCauBatCameraNen() {

        val intent =
            Intent(
                this,
                DichVuTheoDoiFaceAccess::class.java
            ).apply {

                action =
                    DichVuTheoDoiFaceAccess
                        .HANH_DONG_BAT_CAMERA_NEN
            }

        try {
            startService(intent)

            Log.d(
                TAG_BAN_GIAO_CAMERA,
                "Activity da nha Camera -> yeu cau Service BAT Camera nen"
            )
        } catch (exception: Exception) {
            Log.e(
                TAG_BAN_GIAO_CAMERA,
                "Khong the yeu cau Service BAT Camera nen",
                exception
            )
        }
    }

    private fun yeuCauTatCameraNenDeNhanLaiCamera() {

        if (dangChoCameraNenNhaQuyen) {
            return
        }

        dangChoCameraNenNhaQuyen =
            true

        soLanThuBanGiaoCamera =
            0

        guiYeuCauTatCameraNen()
    }

    private fun guiYeuCauTatCameraNen() {

        val intent =
            Intent(
                this,
                DichVuTheoDoiFaceAccess::class.java
            ).apply {

                action =
                    DichVuTheoDoiFaceAccess
                        .HANH_DONG_TAT_CAMERA_NEN
            }

        try {
            startService(intent)

            Log.d(
                TAG_BAN_GIAO_CAMERA,
                "Activity yeu cau Service TAT Camera nen"
            )
        } catch (exception: Exception) {
            Log.e(
                TAG_BAN_GIAO_CAMERA,
                "Khong the gui yeu cau TAT Camera nen",
                exception
            )
        }

        window.decorView.postDelayed(
            {
                if (
                    !dangChoCameraNenNhaQuyen ||
                    !theoDoiDangHoatDong ||
                    !lifecycle.currentState.isAtLeast(
                        Lifecycle.State.STARTED
                    )
                ) {
                    return@postDelayed
                }

                if (
                    soLanThuBanGiaoCamera <
                    SO_LAN_THU_LAI_BAN_GIAO_CAMERA
                ) {
                    soLanThuBanGiaoCamera++

                    Log.w(
                        TAG_BAN_GIAO_CAMERA,
                        "Chua nhan ACK Camera nen DA TAT -> thu lai lan $soLanThuBanGiaoCamera"
                    )

                    guiYeuCauTatCameraNen()
                } else {
                    dangChoCameraNenNhaQuyen =
                        false

                    Log.e(
                        TAG_BAN_GIAO_CAMERA,
                        "Het thoi gian cho Service nha Camera"
                    )

                    capNhatTrangThaiHeThong(
                        "● Chưa thể nhận lại Camera - hãy thử lại"
                    )
                }
            },
            THOI_GIAN_CHO_BAN_GIAO_CAMERA_MS
        )
    }

    private val boNhanBanGiaoCamera =
        object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {

                when (intent?.action) {

                    DichVuTheoDoiFaceAccess
                        .HANH_DONG_CAMERA_NEN_DA_TAT -> {

                        dangChoCameraNenNhaQuyen =
                            false

                        soLanThuBanGiaoCamera =
                            0

                        Log.d(
                            TAG_BAN_GIAO_CAMERA,
                            "Activity DA NHAN ACK Camera nen DA TAT"
                        )

                        if (
                            theoDoiDangHoatDong &&
                            !cameraDangBat &&
                            lifecycle.currentState
                                .isAtLeast(
                                    Lifecycle.State.STARTED
                                )
                        ) {

                            batLaiCameraActivitySauBanGiao()
                        }
                    }
                }
            }
        }

    private fun dangKyBoNhanBanGiaoCamera() {

        if (daDangKyBoNhanBanGiaoCamera) {
            return
        }

        val boLoc =
            IntentFilter(
                DichVuTheoDoiFaceAccess
                    .HANH_DONG_CAMERA_NEN_DA_TAT
            )

        ContextCompat.registerReceiver(
            this,
            boNhanBanGiaoCamera,
            boLoc,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        daDangKyBoNhanBanGiaoCamera =
            true
    }

    private fun huyDangKyBoNhanBanGiaoCamera() {

        if (!daDangKyBoNhanBanGiaoCamera) {
            return
        }

        unregisterReceiver(
            boNhanBanGiaoCamera
        )

        daDangKyBoNhanBanGiaoCamera =
            false
    }

    private fun capNhatGiaoDienNutTheoDoi(
        dangTheoDoi: Boolean
    ) {
        btnBatDauTheoDoi.text =
            if (dangTheoDoi) {
                "DỪNG THEO DÕI"
            } else {
                "BẮT ĐẦU THEO DÕI"
            }

        // Bỏ tint mặc định của theme
        btnBatDauTheoDoi.backgroundTintList =
            null

        btnBatDauTheoDoi.setBackgroundResource(
            R.drawable.fa_home_button_tracking
        )

        btnBatDauTheoDoi.setTextColor(
            Color.WHITE
        )

        btnBatDauTheoDoi.alpha = 1f
        btnBatDauTheoDoi.scaleX = 1f
        btnBatDauTheoDoi.scaleY = 1f
    }

    private fun ganHieuUngNhanNutTheoDoi() {
        btnBatDauTheoDoi.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate()
                        .scaleX(0.955f)
                        .scaleY(0.955f)
                        .alpha(0.78f)
                        .setDuration(90L)
                        .start()
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(130L)
                        .start()
                }
            }

            false
        }
    }

    private fun cauHinhGiaoDienHieuChinh() {

        fun datNutXanh(
            nut: Button
        ) {
            // Bỏ màu mặc định của theme
            nut.backgroundTintList =
                null

            nut.setBackgroundResource(
                R.drawable.fa_calibration_button
            )

            nut.setTextColor(
                Color.WHITE
            )

            nut.alpha =
                1f

            nut.scaleX =
                1f

            nut.scaleY =
                1f
        }

        fun ganHieuUngNhan(
            view: View
        ) {
            view.setOnTouchListener { nut, event ->

                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        nut.animate()
                            .scaleX(0.96f)
                            .scaleY(0.96f)
                            .alpha(0.78f)
                            .setDuration(90L)
                            .start()
                    }

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        nut.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(130L)
                            .start()
                    }
                }

                false
            }
        }

        datNutXanh(
            btnBatDauHieuChinh
        )

        datNutXanh(
            btnHuyHieuChinh
        )

        ganHieuUngNhan(
            btnBatDauHieuChinh
        )

        ganHieuUngNhan(
            btnHuyHieuChinh
        )
    }

    // QUYỀN CAMERA

    private val yeuCauQuyenCamera =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { duocCapQuyen ->

            if (duocCapQuyen) {

                batCamera()

            } else {

                dangChoHieuChinhTuCaiDat =
                    false

                hienThiCameraDaDung(
                    "CAMERA\nChưa được cấp quyền"
                )

                capNhatTrangThaiHeThong(
                    "● Chưa được cấp quyền Camera"
                )
            }
        }

    // QUYỀN THÔNG BÁO

    private val yeuCauQuyenThongBao =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { duocCapQuyen ->

            Log.d(
                TAG_DICH_VU,
                "Quyen thong bao: $duocCapQuyen"
            )
        }

    private fun kiemTraVaYeuCauQuyenThongBao() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val daCoQuyenThongBao =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!daCoQuyenThongBao) {

                yeuCauQuyenThongBao.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private val moManHinhCaiDat =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { ketQua ->

            if (ketQua.resultCode != RESULT_OK) {
                return@registerForActivityResult
            }

            val duLieu =
                ketQua.data

            val cauHinhDaThayDoi =
                duLieu?.getBooleanExtra(
                    CaiDatActivity.EXTRA_CAU_HINH_DA_THAY_DOI,
                    false
                ) == true

            val yeuCauHieuChinh =
                duLieu?.getBooleanExtra(
                    CaiDatActivity.EXTRA_YEU_CAU_HIEU_CHINH,
                    false
                ) == true

            val yeuCauHuongDan =
                duLieu?.getBooleanExtra(
                    CaiDatActivity.EXTRA_YEU_CAU_HUONG_DAN,
                    false
                ) == true

            val yeuCauTrangChu =
                duLieu?.getBooleanExtra(
                    CaiDatActivity.EXTRA_YEU_CAU_TRANG_CHU,
                    false
                ) == true

            if (cauHinhDaThayDoi) {
                taiLaiCauHinhNhanDien()

                if (dangMoTrangHuongDan) {
                    capNhatNoiDungHuongDan(
                        cheDoHuongDanDangXem
                    )
                }
            }

            when {

                yeuCauTrangChu -> {

                    val dangOTrangChu =
                        !dangHieuChinh &&
                                !dangMoTrangHieuChinh &&
                                !dangMoTrangHuongDan

                    when {
                        dangHieuChinh ->
                            huyHieuChinh()

                        dangMoTrangHieuChinh ->
                            anGiaoDienHieuChinh()

                        dangMoTrangHuongDan ->
                            anGiaoDienHuongDan()

                        dangOTrangChu ->
                            taoHieuUngMoNoiDungManHinhChinh()
                    }
                }

                yeuCauHieuChinh -> {

                    if (dangMoTrangHuongDan) {
                        anGiaoDienHuongDan()
                    }

                    if (
                        !dangHieuChinh &&
                        !dangMoTrangHieuChinh
                    ) {
                        moTrangHieuChinh()
                    } else if (
                        !dangHieuChinh &&
                        dangMoTrangHieuChinh
                    ) {
                        taoHieuUngMoNoiDungManHinhChinh()
                    }
                }

                yeuCauHuongDan -> {

                    when {
                        dangHieuChinh ->
                            huyHieuChinh()

                        dangMoTrangHieuChinh ->
                            anGiaoDienHieuChinh()
                    }

                    if (!dangMoTrangHuongDan) {
                        moTrangHuongDan()
                    } else {
                        taoHieuUngMoNoiDungManHinhChinh()
                    }
                }
            }
        }

    // ON CREATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cauHinhThanhHeThong()

        setContentView(R.layout.activity_main)

        anhXaGiaoDien()

        khoiTaoHuongDanCuChi()

        cauHinhGiaoDienHieuChinh()

        theoDoiDangHoatDong =
            DichVuTheoDoiFaceAccess
                .dangTheoDoiHoatDong()

        capNhatGiaoDienNutTheoDoi(
            dangTheoDoi = theoDoiDangHoatDong
        )

        ganHieuUngNhanNutTheoDoi()

        dangKyBoNhanBanGiaoCamera()

        kiemTraVaYeuCauQuyenThongBao()

        khoiTaoTrichXuatDuLieu()

        khoiTaoCauHinhNhanDien()

        khoiTaoBoDinhTuyenCheDo()

        khoiTaoBoDieuKhienMedia()

        khoiTaoBoDieuKhienLienHeHoTro()

        khoiTaoDieuPhoiCuChi()

        khoiTaoNhanDienNghiengDau()

        khoiTaoNhanDienMoMieng()

        khoiTaoNhanDienMoMiengHaiLan()

        khoiTaoNhanDienHuongDau()

        khoiTaoNhanDienNhamHaiMat()

        khoiTaoXuLyKhuonMat()

        khoiTaoCamera()

        ganSuKien()

        hienThiCameraDaDung(
            "CAMERA\nChưa khởi động"
        )

        datLaiThongTinNhanDien()

        capNhatMenuDuoi(
            dangHieuChinh = false
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


    // ÁNH XẠ UI

    private fun anhXaGiaoDien() {

        // Camera
        khungCamera =
            findViewById(R.id.khungCamera)

        txtTrangThaiCamera =
            findViewById(R.id.txtTrangThaiCamera)

        // Hệ thống
        btnBatDauTheoDoi =
            findViewById(R.id.btnBatDauTheoDoi)

        navTrangChu =
            findViewById(R.id.navTrangChu)

        navHuongDan =
            findViewById(R.id.navHuongDan)

        navHieuChinh =
            findViewById(R.id.navHieuChinh)

        navCaiDat =
            findViewById(R.id.navCaiDat)

        txtTrangThaiHeThong =
            findViewById(R.id.txtTrangThaiHeThong)

        khungTrangThaiHeThong =
            findViewById(R.id.khungTrangThaiHeThong)

        khungTieuDeCamera =
            findViewById(R.id.khungTieuDeCamera)

        khungCameraContainer =
            findViewById(R.id.khungCameraContainer)

        khungCameraChinh =
            findViewById(R.id.khungCameraChinh)

        noiDungCuonManHinhChinh =
            findViewById(R.id.noiDungCuonManHinhChinh)

        noiDungChinh =
            findViewById(R.id.noiDungChinh)

        // Hướng dẫn
        khungHeaderChinh =
            findViewById(R.id.khungHeaderChinh)

        khungHuongDan =
            findViewById(R.id.khungHuongDan)

        guideDieuHuong =
            findViewById(R.id.guideDieuHuong)

        guideMedia =
            findViewById(R.id.guideMedia)

        guideHoTro =
            findViewById(R.id.guideHoTro)

        guideConTro =
            findViewById(R.id.guideConTro)

        khungTheHuongDan =
            findViewById(R.id.khungTheHuongDan)

        txtSoThuTuCuChi =
            findViewById(R.id.txtSoThuTuCuChi)

        txtTenCuChi =
            findViewById(R.id.txtTenCuChi)

        txtMoTaCuChi =
            findViewById(R.id.txtMoTaCuChi)

        txtNhanCheDo =
            findViewById(R.id.txtNhanCheDo)

        txtHanhDongCuChi =
            findViewById(R.id.txtHanhDongCuChi)

        txtGoiYCuChi =
            findViewById(R.id.txtGoiYCuChi)

        txtTrangHuongDan =
            findViewById(R.id.txtTrangHuongDan)

        animationHuongDan =
            findViewById(R.id.animationHuongDan)

        chamHuongDan =
            listOf(
                findViewById(R.id.dotHuongDan1),
                findViewById(R.id.dotHuongDan2),
                findViewById(R.id.dotHuongDan3),
                findViewById(R.id.dotHuongDan4),
                findViewById(R.id.dotHuongDan5),
                findViewById(R.id.dotHuongDan6),
                findViewById(R.id.dotHuongDan7),
                findViewById(R.id.dotHuongDan8)
            )

        // Hiệu chỉnh
        khungHieuChinh =
            findViewById(R.id.khungHieuChinh)

        khungChuanBiHieuChinh =
            findViewById(R.id.khungChuanBiHieuChinh)

        khungTienTrinhHieuChinh =
            findViewById(R.id.khungTienTrinhHieuChinh)

        btnBatDauHieuChinh =
            findViewById(R.id.btnBatDauHieuChinh)

        txtBuocHieuChinh =
            findViewById(R.id.txtBuocHieuChinh)

        txtDongTacHieuChinh =
            findViewById(R.id.txtDongTacHieuChinh)

        txtHuongDanHieuChinh =
            findViewById(R.id.txtHuongDanHieuChinh)

        txtDemNguocHieuChinh =
            findViewById(R.id.txtDemNguocHieuChinh)

        progressHieuChinh =
            findViewById(R.id.progressHieuChinh)

        txtTienDoHieuChinh =
            findViewById(R.id.txtTienDoHieuChinh)

        txtPhanHoiHieuChinh =
            findViewById(R.id.txtPhanHoiHieuChinh)

        btnHuyHieuChinh =
            findViewById(R.id.btnHuyHieuChinh)

        // Chế độ
        txtCheDoHienTai =
            findViewById(R.id.txtCheDoHienTai)

        cardDieuHuong =
            findViewById(R.id.cardDieuHuong)

        cardMedia =
            findViewById(R.id.cardMedia)

        cardHoTro =
            findViewById(R.id.cardHoTro)

        cardConTro =
            findViewById(R.id.cardConTro)

        // Debug
        txtRoll =
            findViewById(R.id.txtRoll)

        txtYaw =
            findViewById(R.id.txtYaw)

        txtPitch =
            findViewById(R.id.txtPitch)

        txtTrangThaiMat =
            findViewById(R.id.txtTrangThaiMat)

        txtTrangThaiMieng =
            findViewById(R.id.txtTrangThaiMieng)
    }

    private fun khoiTaoHuongDanCuChi() {

        val nguongVuotPx =
            56f * resources.displayMetrics.density

        var xBatDau = 0f
        var yBatDau = 0f

        khungTheHuongDan.setOnTouchListener { view, event ->

            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN -> {
                    xBatDau = event.x
                    yBatDau = event.y
                    view.parent
                        .requestDisallowInterceptTouchEvent(false)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx =
                        event.x - xBatDau

                    val dy =
                        event.y - yBatDau

                    if (
                        abs(dx) > abs(dy) &&
                        abs(dx) > 12f * resources.displayMetrics.density
                    ) {
                        view.parent
                            .requestDisallowInterceptTouchEvent(true)
                    } else {
                        view.parent
                            .requestDisallowInterceptTouchEvent(false)
                    }

                    true
                }

                MotionEvent.ACTION_UP -> {
                    view.parent
                        .requestDisallowInterceptTouchEvent(false)

                    val dx =
                        event.x - xBatDau

                    val dy =
                        event.y - yBatDau

                    if (
                        abs(dx) >= nguongVuotPx &&
                        abs(dx) > abs(dy)
                    ) {
                        if (dx < 0f) {
                            chuyenCuChiHuongDan(1)
                        } else {
                            chuyenCuChiHuongDan(-1)
                        }
                    }

                    view.performClick()
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    view.parent
                        .requestDisallowInterceptTouchEvent(false)
                    true
                }

                else ->
                    false
            }
        }

        chamHuongDan.forEachIndexed { index, cham ->
            cham.setOnClickListener {
                if (index in danhSachHuongDan.indices) {
                    viTriCuChiHuongDan = index
                    hienThiCuChiHuongDan()
                }
            }
        }
    }

    private fun chuyenCuChiHuongDan(
        buoc: Int
    ) {

        if (danhSachHuongDan.isEmpty()) {
            return
        }

        val tongSo =
            danhSachHuongDan.size

        viTriCuChiHuongDan =
            (viTriCuChiHuongDan + buoc + tongSo) % tongSo

        hienThiCuChiHuongDan()
    }

    private fun hienThiCuChiHuongDan() {

        if (danhSachHuongDan.isEmpty()) {
            return
        }

        viTriCuChiHuongDan =
            viTriCuChiHuongDan.coerceIn(
                0,
                danhSachHuongDan.lastIndex
            )

        val muc =
            danhSachHuongDan[viTriCuChiHuongDan]

        txtSoThuTuCuChi.text =
            muc.soThuTu.toString()

        txtTenCuChi.text =
            muc.tenCuChi

        txtMoTaCuChi.text =
            muc.moTaCuChi

        txtNhanCheDo.text =
            muc.nhanCheDo

        txtHanhDongCuChi.text =
            muc.hanhDongCuChi

        txtGoiYCuChi.text =
            muc.goiYCuChi

        txtTrangHuongDan.text =
            "${viTriCuChiHuongDan + 1} / ${danhSachHuongDan.size}"

        capNhatChamHuongDan()

        animationHuongDan.cancelAnimation()
        animationHuongDan.setAnimation(
            muc.animationResId
        )
        animationHuongDan.repeatCount =
            if (muc.lapLaiAnimation) {
                -1
            } else {
                0
            }
        animationHuongDan.progress = 0f
        animationHuongDan.alpha = 0f
        animationHuongDan.playAnimation()
        animationHuongDan.animate()
            .alpha(1f)
            .setDuration(160L)
            .start()
    }

    private fun capNhatChamHuongDan() {

        chamHuongDan.forEachIndexed { index, cham ->
            cham.setBackgroundResource(
                if (index == viTriCuChiHuongDan) {
                    R.drawable.hd_dot_active
                } else {
                    R.drawable.hd_dot_idle
                }
            )
        }
    }

    // TRÍCH XUẤT DỮ LIỆU

    private fun khoiTaoTrichXuatDuLieu() {

        trichXuatDuLieuKhuonMat =
            TrichXuatDuLieuKhuonMat()
    }

    // CẤU HÌNH NHẬN DIỆN

    private fun khoiTaoCauHinhNhanDien() {

        khoCauHinhNhanDienCuChi =
            KhoCauHinhNhanDienCuChi(
                applicationContext
            )

        cauHinhNhanDienCuChi =
            khoCauHinhNhanDienCuChi
                .layCauHinh()

        boChuanHoaDuLieuKhuonMat =
            BoChuanHoaDuLieuKhuonMat(
                cauHinhNhanDienCuChi.chuanHoa
            )
    }

    // HỆ THỐNG CHẾ ĐỘ

    private fun khoiTaoBoDinhTuyenCheDo() {

        boDinhTuyenCheDo =
            BoDinhTuyenCheDo { cheDoMoi ->

                Log.d(
                    TAG_CHE_DO,
                    "Che do moi: $cheDoMoi"
                )

                DichVuTruyCapFaceAccess
                    .capNhatCheDoTrangThaiOverlay(
                        cheDoMoi
                    )

                if (
                    cheDoMoi !=
                    CheDoDieuKhien.HO_TRO &&
                    ::boDieuKhienLienHeHoTro.isInitialized
                ) {

                    boDieuKhienLienHeHoTro
                        .datLaiPhien()
                }

                datLaiNhanDienMieng()
                datLaiNhanDienMat()

                capNhatTrangThaiConTroTheoCheDo(
                    cheDoMoi
                )

                capNhatGiaoDienCheDo(
                    cheDoMoi
                )

                if (dangMoTrangHuongDan) {
                    capNhatNoiDungHuongDan(
                        cheDoMoi
                    )
                }
            }

        val cheDoBanDau =
            boDinhTuyenCheDo
                .layCheDoHienTai()

        DichVuTruyCapFaceAccess
            .capNhatCheDoTrangThaiOverlay(
                cheDoBanDau
            )

        capNhatTrangThaiConTroTheoCheDo(
            cheDoBanDau
        )

        capNhatGiaoDienCheDo(
            cheDoBanDau
        )
    }

    // CURSOR - ĐỒNG BỘ THEO MODE

    private fun capNhatTrangThaiConTroTheoCheDo(
        cheDo: CheDoDieuKhien
    ) {

        if (
            !theoDoiDangHoatDong ||
            cheDo != CheDoDieuKhien.CON_TRO
        ) {
            DichVuTruyCapFaceAccess.tatConTro()
            return
        }

        val thanhCong =
            DichVuTruyCapFaceAccess.batConTro()

        Log.d(
            TAG_CON_TRO,
            if (thanhCong) {
                "APP: CURSOR BAT"
            } else {
                "APP: CURSOR CHUA BAT - AccessibilityService chua san sang"
            }
        )
    }

    // BỘ ĐIỀU KHIỂN MEDIA

    private fun khoiTaoBoDieuKhienMedia() {

        boDieuKhienMedia =
            BoDieuKhienMedia(
                applicationContext
            )
    }

    // BỘ ĐIỀU KHIỂN LIÊN HỆ HỖ TRỢ

    private fun khoiTaoBoDieuKhienLienHeHoTro() {

        boDieuKhienLienHeHoTro =
            BoDieuKhienLienHeHoTro(
                applicationContext
            )
    }

    // ĐIỀU PHỐI CỬ CHỈ

    private fun khoiTaoDieuPhoiCuChi() {

        dieuPhoiCuChi =
            DieuPhoiCuChi(
                layCheDoHienTai = {

                    boDinhTuyenCheDo
                        .layCheDoHienTai()
                },
                layCauHinhHanhDong = {
                    cauHinhNhanDienCuChi.hanhDong
                },
                khiCoHuongTheoCheDo = {
                        cheDo,
                        huong ->

                    Log.d(
                        TAG_CU_CHI_THEO_CHE_DO,
                        "APP: MODE=$cheDo | HUONG=$huong"
                    )
                },
                khiCoLenhDieuHuong = { lenhDieuHuong ->

                    Log.d(
                        TAG_LENH_DIEU_HUONG,
                        "APP: LENH_DIEU_HUONG=$lenhDieuHuong"
                    )

                    when (lenhDieuHuong) {

                        LenhDieuHuong.TRUOC -> {

                            runOnUiThread {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiTruoc()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "APP: TRUOC Accessibility THANH_CONG"
                                    } else {
                                        "APP: TRUOC Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }

                        LenhDieuHuong.TIEP_THEO -> {

                            runOnUiThread {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiTiepTheo()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "APP: TIEP_THEO Accessibility THANH_CONG"
                                    } else {
                                        "APP: TIEP_THEO Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }

                        LenhDieuHuong.CUON_LEN -> {

                            runOnUiThread {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiCuonLen()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "APP: CUON_LEN Accessibility THANH_CONG"
                                    } else {
                                        "APP: CUON_LEN Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }

                        LenhDieuHuong.CUON_XUONG -> {

                            runOnUiThread {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiCuonXuong()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "APP: CUON_XUONG Accessibility THANH_CONG"
                                    } else {
                                        "APP: CUON_XUONG Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }

                        LenhDieuHuong.XAC_NHAN -> {

                            runOnUiThread {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiXacNhanDieuHuong()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "APP: XAC_NHAN Accessibility THANH_CONG"
                                    } else {
                                        "APP: XAC_NHAN Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }
                    }
                },
                khiCoLenhMedia = { lenhMedia ->

                    Log.d(
                        TAG_LENH_MEDIA,
                        "APP: LENH_MEDIA=$lenhMedia"
                    )

                    runOnUiThread {

                        val thanhCong =
                            boDieuKhienMedia
                                .thucThi(
                                    lenhMedia
                                )

                        Log.d(
                            TAG_LENH_MEDIA,
                            if (thanhCong) {
                                "APP: MEDIA_ACTION=$lenhMedia THANH_CONG"
                            } else {
                                "APP: MEDIA_ACTION=$lenhMedia THAT_BAI"
                            }
                        )
                    }
                },

                khiCoLenhHoTro = { lenhHoTro ->

                    Log.d(
                        TAG_LENH_HO_TRO,
                        "APP: LENH_HO_TRO=$lenhHoTro"
                    )

                    runOnUiThread {

                        val ketQua =
                            boDieuKhienLienHeHoTro
                                .thucThi(
                                    lenhHoTro
                                )

                        Log.d(
                            TAG_LENH_HO_TRO,
                            "APP: HO_TRO_ACTION=$lenhHoTro | " +
                                    "THANH_CONG=${ketQua.thanhCong} | " +
                                    "THONG_BAO=${ketQua.thongBao}"
                        )

                        ThongBaoFaceAccess.hienThi(
                            context = this,
                            noiDung = ketQua.thongBao
                        )
                    }
                },

                khiCoLenhConTro = { lenhConTro ->

                    runOnUiThread {
                        val thanhCong =
                            DichVuTruyCapFaceAccess
                                .thucThiDiChuyenConTro(
                                    lenhConTro
                                )

                        Log.d(
                            TAG_CON_TRO,
                            "APP: MOVE=$lenhConTro | OK=$thanhCong"
                        )
                    }
                },

                khiCoXacNhanConTro = {

                    runOnUiThread {
                        val thanhCong =
                            DichVuTruyCapFaceAccess
                                .thucThiClickConTro()

                        Log.d(
                            TAG_CON_TRO,
                            "APP: EYE_CLICK | OK=$thanhCong"
                        )
                    }
                },

                khiCoLenh = { lenh ->

                    when (lenh) {

                        LenhToanCuc.HOME -> {

                            Log.d(
                                TAG_LENH,
                                "LENH HOME"
                            )

                            runOnUiThread {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiHome()

                                if (thanhCong) {

                                    Log.d(
                                        TAG_LENH,
                                        "HOME Android: THANH_CONG"
                                    )

                                } else {

                                    Log.e(
                                        TAG_LENH,
                                        "HOME Android: THAT_BAI - AccessibilityService chua san sang"
                                    )

                                    capNhatTrangThaiHeThong(
                                        "● Hãy bật dịch vụ trợ năng FaceAccess"
                                    )
                                }
                            }
                        }

                        LenhToanCuc.DOI_CHE_DO -> {

                            Log.d(
                                TAG_LENH,
                                "LENH DOI_CHE_DO"
                            )

                            boDinhTuyenCheDo
                                .chuyenCheDoTiepTheo()
                        }

                        LenhToanCuc.BACK -> {

                            Log.d(
                                TAG_LENH,
                                "LENH BACK"
                            )

                            runOnUiThread {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiBack()

                                if (thanhCong) {

                                    Log.d(
                                        TAG_LENH,
                                        "BACK Android: THANH_CONG"
                                    )

                                } else {

                                    Log.e(
                                        TAG_LENH,
                                        "BACK Android: THAT_BAI - AccessibilityService chua san sang"
                                    )

                                    capNhatTrangThaiHeThong(
                                        "● Hãy bật dịch vụ trợ năng FaceAccess"
                                    )
                                }
                            }
                        }

                        LenhToanCuc.DOI_KHOA_CON_TRO -> {

                            runOnUiThread {
                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .doiKhoaConTro()

                                Log.d(
                                    TAG_CON_TRO,
                                    "APP: MOUTH_DOUBLE_TOGGLE_LOCK | OK=$thanhCong"
                                )
                            }
                        }
                    }
                }
            )
    }

    // DETECTOR ROLL

    private fun khoiTaoNhanDienNghiengDau() {

        nhanDienNghiengDau =
            NhanDienNghiengDau(
                cauHinh =
                    cauHinhNhanDienCuChi.nghiengDau
            ) { huong ->

                when (huong) {

                    HuongNghiengDau.TRAI -> {

                        dieuPhoiCuChi.xuLy(
                            SuKienCuChi.NghiengTrai
                        )
                    }

                    HuongNghiengDau.PHAI -> {

                        dieuPhoiCuChi.xuLy(
                            SuKienCuChi.NghiengPhai
                        )
                    }
                }
            }
    }

    // MEDIAPIPE

    private fun khoiTaoXuLyKhuonMat() {

        xuLyKhuonMat =
            XuLyKhuonMat(
                context = this,
                langNghe =
                    object :
                        XuLyKhuonMat.LangNgheXuLyKhuonMat {

                        override fun khiKhoiTaoThanhCong() {

                            Log.d(
                                TAG_MEDIAPIPE,
                                "Face Landmarker da san sang"
                            )
                        }

                        override fun khiCoKetQua(
                            result: FaceLandmarkerResult,
                            chieuRongAnh: Int,
                            chieuCaoAnh: Int
                        ) {

                            if (!cameraDangBat) {
                                return
                            }

                            val duLieuGoc =
                                trichXuatDuLieuKhuonMat
                                    .trichXuat(result)

                            val thoiGianHienTai =
                                SystemClock.uptimeMillis()

                            // Khi hiệu chỉnh chỉ thu mẫu
                            if (dangHieuChinh) {

                                capNhatTrangThaiKhuonMat(
                                    coKhuonMat = true
                                )

                                capNhatDuLieuKhuonMat(
                                    duLieuGoc
                                )

                                xuLyDuLieuHieuChinh(
                                    duLieu = duLieuGoc,
                                    thoiGianMs = thoiGianHienTai
                                )

                                return
                            }

                            // Bù lệch tư thế trung tính
                            val duLieu =
                                boChuanHoaDuLieuKhuonMat
                                    .chuanHoa(
                                        duLieuGoc
                                    )


                            // KHUÔN MẶT

                            capNhatTrangThaiKhuonMat(
                                coKhuonMat = true
                            )

                            // DEBUG REALTIME

                            capNhatDuLieuKhuonMat(
                                duLieu
                            )

                            // DETECTOR ROLL

                            nhanDienNghiengDau.capNhat(
                                roll = duLieu.roll,
                                yaw = duLieu.yaw,
                                pitch = duLieu.pitch,
                                thoiGianMs =
                                    thoiGianHienTai
                            )

                            nhanDienHuongDau.capNhat(
                                roll = duLieu.roll,
                                yaw = duLieu.yaw,
                                pitch = duLieu.pitch,
                                thoiGianMs =
                                    thoiGianHienTai
                            )

                            val cheDoHienTai =
                                boDinhTuyenCheDo
                                    .layCheDoHienTai()

                            // Mở giữ hoạt động ở mọi chế độ
                            nhanDienMoMieng.capNhat(
                                doMoMieng =
                                    duLieu.doMoMieng,
                                thoiGianMs =
                                    thoiGianHienTai
                            )

                            // Mở hai lần chỉ chạy khi cần
                            if (
                                canTheoDoiMoMiengHaiLan(
                                    cheDoHienTai
                                )
                            ) {
                                nhanDienMoMiengHaiLan.capNhat(
                                    doMoMieng =
                                        duLieu.doMoMieng,
                                    thoiGianMs =
                                        thoiGianHienTai
                                )
                            } else {
                                nhanDienMoMiengHaiLan.datLai()
                            }

                            val thoiGianXacNhanNhamMat =
                                when (cheDoHienTai) {
                                    CheDoDieuKhien.HO_TRO ->
                                        BoDieuKhienLienHeHoTro
                                            .THOI_GIAN_NHAM_XAC_NHAN_MS

                                    CheDoDieuKhien.DIEU_HUONG,
                                    CheDoDieuKhien.MEDIA,
                                    CheDoDieuKhien.CON_TRO ->
                                        cauHinhNhanDienCuChi
                                            .nhamHaiMat
                                            .thoiGianNhamXacNhanMs
                                }

                            if (thoiGianXacNhanNhamMat != null) {
                                nhanDienNhamHaiMat.capNhat(
                                    doNhamMatTrai =
                                        duLieu.doNhamMatTrai,
                                    doNhamMatPhai =
                                        duLieu.doNhamMatPhai,
                                    thoiGianMs =
                                        thoiGianHienTai,
                                    thoiGianXacNhanMs =
                                        thoiGianXacNhanNhamMat
                                )
                            } else {
                                datLaiNhanDienMat()
                            }

                        }

                        override fun khiKhongThayKhuonMat() {

                            if (!cameraDangBat) {
                                return
                            }

                            nhanDienNghiengDau.datLai()

                            datLaiNhanDienMieng()

                            nhanDienHuongDau.datLai()

                            datLaiNhanDienMat()

                            capNhatTrangThaiKhuonMat(
                                coKhuonMat = false
                            )

                            if (dangHieuChinh) {

                                runOnUiThread {

                                    txtPhanHoiHieuChinh.text =
                                        "⚠ Không thấy khuôn mặt, hãy nhìn vào camera"
                                }
                            }

                            datLaiThongTinNhanDien()
                        }

                        override fun khiCoLoi(
                            thongBao: String
                        ) {

                            Log.e(
                                TAG_MEDIAPIPE,
                                thongBao
                            )

                            DichVuTruyCapFaceAccess
                                .capNhatKhuonMatTrangThaiOverlay(
                                    false
                                )

                            runOnUiThread {

                                if (cameraDangBat) {

                                    capNhatTrangThaiHeThong(
                                        "● Lỗi MediaPipe: $thongBao"
                                    )
                                }
                            }
                        }
                    }
            )

        phanTichKhungHinhKhuonMat =
            PhanTichKhungHinhKhuonMat(
                xuLyKhuonMat = xuLyKhuonMat,
                laCameraTruoc = true
            )
    }

    // CAMERA

    private fun khoiTaoCamera() {

        quanLyCamera =
            QuanLyCamera(
                context = this,
                lifecycleOwner = this,
                previewView = khungCamera,
                boPhanTichKhungHinh =
                    phanTichKhungHinhKhuonMat
            )
    }

    // TRỢ NĂNG

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

    private fun moTrangBatDichVuTruyCap() {

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

            Log.w(
                TAG_TRUY_CAP,
                "May khong co trang chi tiet Accessibility",
                exception
            )

        } catch (
            exception: SecurityException
        ) {

            Log.w(
                TAG_TRUY_CAP,
                "May chan mo trang chi tiet Accessibility",
                exception
            )
        }

        try {

            startActivity(
                Intent(
                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                )
            )

            Toast.makeText(
                this,
                "Hãy chọn FaceAccess_v2 và bật dịch vụ",
                Toast.LENGTH_LONG
            ).show()

        } catch (
            exception: ActivityNotFoundException
        ) {

            dangChoBatDichVuTruyCap =
                false

            capNhatTrangThaiHeThong(
                "● Không thể mở cài đặt Trợ năng trên thiết bị này"
            )
        }
    }

    private fun tiepTucTheoDoiSauKhiBatTruyCap(
        lanThu: Int = 0
    ) {

        if (!dangChoBatDichVuTruyCap) {
            return
        }

        if (dichVuTruyCapDaBat()) {

            dangChoBatDichVuTruyCap =
                false

            window.decorView.postDelayed(
                {

                    if (
                        !cameraDangBat &&
                        !theoDoiDangHoatDong
                    ) {

                        kiemTraVaBatCamera()
                    }

                },
                350L
            )

            return
        }

        if (lanThu < SO_LAN_CHO_DICH_VU_TRUY_CAP) {

            window.decorView.postDelayed(
                {

                    tiepTucTheoDoiSauKhiBatTruyCap(
                        lanThu + 1
                    )

                },
                THOI_GIAN_CHO_DICH_VU_TRUY_CAP_MS
            )

            return
        }

        dangChoBatDichVuTruyCap =
            false

        capNhatTrangThaiHeThong(
            "● FaceAccess_v2 chưa được bật trong Trợ năng"
        )
    }

    // SỰ KIỆN UI

    private fun ganSuKien() {

        btnBatDauTheoDoi.setOnClickListener {

            if (theoDoiDangHoatDong) {

                tatCamera()

                return@setOnClickListener
            }

            if (!dichVuTruyCapDaBat()) {

                dangChoBatDichVuTruyCap =
                    true

                capNhatTrangThaiHeThong(
                    "● Hãy bật FaceAccess_v2 trong Trợ năng"
                )

                moTrangBatDichVuTruyCap()

                return@setOnClickListener
            }

            kiemTraVaBatCamera()
        }

        cardHoTro.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    DanhSachLienHeHoTroActivity::class.java
                )
            )
        }

        navTrangChu.setOnClickListener {

            val dangOTrangChu =
                !dangHieuChinh &&
                        !dangMoTrangHieuChinh &&
                        !dangMoTrangHuongDan

            when {
                dangHieuChinh ->
                    huyHieuChinh()

                dangMoTrangHieuChinh ->
                    anGiaoDienHieuChinh()

                dangMoTrangHuongDan ->
                    anGiaoDienHuongDan()

                dangOTrangChu ->
                    taoHieuUngMoNoiDungManHinhChinh()
            }
        }

        navHuongDan.setOnClickListener {

            when {
                dangHieuChinh ->
                    huyHieuChinh()

                dangMoTrangHieuChinh ->
                    anGiaoDienHieuChinh()
            }

            if (!dangMoTrangHuongDan) {
                moTrangHuongDan()
            } else {
                taoHieuUngMoNoiDungManHinhChinh()
            }
        }

        navHieuChinh.setOnClickListener {

            if (dangMoTrangHuongDan) {
                anGiaoDienHuongDan()
            }

            if (
                !dangHieuChinh &&
                !dangMoTrangHieuChinh
            ) {
                moTrangHieuChinh()
            } else if (
                !dangHieuChinh &&
                dangMoTrangHieuChinh
            ) {
                taoHieuUngMoNoiDungManHinhChinh()
            }
        }

        navCaiDat.setOnClickListener {

            // Không chuyển màn hiện tại về Trang chủ trước khi mở Cài đặt
            if (dangHieuChinh) {
                huyHieuChinhKhiMoCaiDat()
            }

            moManHinhCaiDat.launch(
                Intent(
                    this,
                    CaiDatActivity::class.java
                )
            )

            // Giữ menu dưới không chạy theo transition Activity
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }

        guideDieuHuong.setOnClickListener {
            capNhatNoiDungHuongDan(
                CheDoDieuKhien.DIEU_HUONG
            )
        }

        guideMedia.setOnClickListener {
            capNhatNoiDungHuongDan(
                CheDoDieuKhien.MEDIA
            )
        }

        guideHoTro.setOnClickListener {
            capNhatNoiDungHuongDan(
                CheDoDieuKhien.HO_TRO
            )
        }

        guideConTro.setOnClickListener {
            capNhatNoiDungHuongDan(
                CheDoDieuKhien.CON_TRO
            )
        }

        btnBatDauHieuChinh.setOnClickListener {

            if (dangHieuChinh) {
                return@setOnClickListener
            }

            if (cameraDangBat) {

                batDauHieuChinh()

            } else {

                dangChoHieuChinhTuCaiDat =
                    true

                kiemTraVaBatCamera()
            }
        }

        btnHuyHieuChinh.setOnClickListener {

            huyHieuChinh()
        }
    }

    private fun taiLaiCauHinhNhanDien() {

        cauHinhNhanDienCuChi =
            khoCauHinhNhanDienCuChi
                .layCauHinh()

        boChuanHoaDuLieuKhuonMat =
            BoChuanHoaDuLieuKhuonMat(
                cauHinhNhanDienCuChi.chuanHoa
            )

        khoiTaoNhanDienNghiengDau()
        khoiTaoNhanDienMoMieng()
        khoiTaoNhanDienMoMiengHaiLan()
        khoiTaoNhanDienHuongDau()
        khoiTaoNhanDienNhamHaiMat()

        Log.d(
            TAG_CAI_DAT,
            "Da tai lai cau hinh nhan dien"
        )
    }

    private fun moTrangHuongDan() {

        dangMoTrangHuongDan =
            true

        khungHeaderChinh.visibility =
            View.GONE

        khungTrangThaiHeThong.visibility =
            View.GONE

        // Ẩn toàn bộ card Camera để không còn ô trắng dư
        khungCameraChinh.visibility =
            View.GONE

        khungTieuDeCamera.visibility =
            View.GONE

        khungCameraContainer.visibility =
            View.GONE

        noiDungChinh.visibility =
            View.GONE

        khungHieuChinh.visibility =
            View.GONE

        khungHuongDan.visibility =
            View.VISIBLE

        khungHuongDan.alpha =
            1f

        khungHuongDan.translationY =
            0f

        val cheDoHienTai =
            boDinhTuyenCheDo
                .layCheDoHienTai()

        capNhatNoiDungHuongDan(
            cheDoHienTai
        )

        capNhatMenuDuoi(
            dangHieuChinh = false,
            dangHuongDan = true
        )

        taoHieuUngMoNoiDungManHinhChinh()
    }

    private fun anGiaoDienHuongDan() {

        dangMoTrangHuongDan =
            false

        if (::animationHuongDan.isInitialized) {
            animationHuongDan.cancelAnimation()
        }

        danhSachHuongDan =
            emptyList()

        khungHuongDan.visibility =
            View.GONE

        khungHeaderChinh.visibility =
            View.VISIBLE

        khungTrangThaiHeThong.visibility =
            View.VISIBLE

        khungCameraChinh.visibility =
            View.VISIBLE

        khungTieuDeCamera.visibility =
            View.VISIBLE

        khungCameraContainer.visibility =
            View.VISIBLE

        noiDungChinh.visibility =
            View.VISIBLE

        capNhatMenuDuoi(
            dangHieuChinh = false
        )

        taoHieuUngMoNoiDungManHinhChinh()
    }

    private fun capNhatNoiDungHuongDan(
        cheDo: CheDoDieuKhien
    ) {

        cheDoHuongDanDangXem =
            cheDo

        val cheDoHuongDan =
            when (cheDo) {
                CheDoDieuKhien.DIEU_HUONG ->
                    CheDoHuongDan.DIEU_HUONG

                CheDoDieuKhien.MEDIA ->
                    CheDoHuongDan.MEDIA

                CheDoDieuKhien.HO_TRO ->
                    CheDoHuongDan.HO_TRO

                CheDoDieuKhien.CON_TRO ->
                    CheDoHuongDan.CON_TRO
            }

        val hanhDongToanCuc =
            cauHinhNhanDienCuChi
                .hanhDong
                .chuanHoaChoCuChiToanCuc()

        danhSachHuongDan =
            DuLieuHuongDanCuChi
                .taoDanhSach(
                    cheDoHuongDan
                )
                .map { muc ->

                    when (muc.soThuTu) {

                        5 ->
                            muc.copy(
                                hanhDongCuChi =
                                    tenHanhDongHuongDan(
                                        hanhDongToanCuc
                                            .nghiengTrai
                                    )
                            )

                        6 ->
                            muc.copy(
                                hanhDongCuChi =
                                    tenHanhDongHuongDan(
                                        hanhDongToanCuc
                                            .nghiengPhai
                                    )
                            )

                        8 ->
                            muc.copy(
                                hanhDongCuChi =
                                    tenHanhDongHuongDan(
                                        hanhDongToanCuc
                                            .moMieng
                                    )
                            )

                        else ->
                            muc
                    }
                }

        if (danhSachHuongDan.isNotEmpty()) {
            viTriCuChiHuongDan =
                viTriCuChiHuongDan.coerceIn(
                    0,
                    danhSachHuongDan.lastIndex
                )

            hienThiCuChiHuongDan()
        }

        capNhatNutCheDoHuongDan(
            cheDo
        )
    }

    private fun tenHanhDongHuongDan(
        hanhDong: HanhDongTuyChinhCuChi
    ): String =
        when (hanhDong) {
            HanhDongTuyChinhCuChi.BACK ->
                "Quay lại"

            HanhDongTuyChinhCuChi.HOME ->
                "Về Home"

            HanhDongTuyChinhCuChi.DOI_CHE_DO ->
                "Đổi chế độ"

            else ->
                hanhDong.tenHienThi
        }

    private fun capNhatNutCheDoHuongDan(
        cheDo: CheDoDieuKhien
    ) {

        val cacNut =
            listOf(
                guideDieuHuong to
                        CheDoDieuKhien.DIEU_HUONG,
                guideMedia to
                        CheDoDieuKhien.MEDIA,
                guideHoTro to
                        CheDoDieuKhien.HO_TRO,
                guideConTro to
                        CheDoDieuKhien.CON_TRO
            )

        cacNut.forEach {
                (nut, cheDoCuaNut) ->

            val dangChon =
                cheDo ==
                        cheDoCuaNut

            nut.setTextColor(
                if (dangChon) {
                    Color.WHITE
                } else {
                    Color.parseColor(
                        "#61736C"
                    )
                }
            )

            nut.setBackgroundResource(
                if (dangChon) {
                    R.drawable.hd_pill_active
                } else {
                    R.drawable.hd_pill_idle
                }
            )

            nut.animate()
                .scaleX(
                    if (dangChon) {
                        1.03f
                    } else {
                        1f
                    }
                )
                .scaleY(
                    if (dangChon) {
                        1.03f
                    } else {
                        1f
                    }
                )
                .setDuration(150L)
                .start()
        }
    }

    private fun moTrangHieuChinh() {

        if (dangHieuChinh) {
            return
        }

        dangMoTrangHieuChinh =
            true

        dangChoHieuChinhTuCaiDat =
            false

        khungHeaderChinh.visibility =
            View.VISIBLE

        khungTrangThaiHeThong.visibility =
            View.GONE

        noiDungChinh.visibility =
            View.GONE

        khungHieuChinh.visibility =
            View.VISIBLE

        khungChuanBiHieuChinh.visibility =
            View.VISIBLE

        khungTienTrinhHieuChinh.visibility =
            View.GONE

        capNhatMenuDuoi(
            dangHieuChinh = true
        )

        taoHieuUngMoNoiDungManHinhChinh()
    }

    private fun batDauHieuChinhTuCaiDat() {

        if (
            !dangChoHieuChinhTuCaiDat ||
            !cameraDangBat ||
            dangHieuChinh
        ) {
            return
        }

        dangChoHieuChinhTuCaiDat =
            false

        batDauHieuChinh()
    }

    // HIỆU CHỈNH

    private fun batDauHieuChinh() {

        if (!cameraDangBat) {

            capNhatTrangThaiHeThong(
                "● Hãy bật Camera trước khi hiệu chỉnh"
            )

            return
        }

        if (dangHieuChinh) {
            return
        }

        boThuThapMauHieuChinh =
            BoThuThapMauHieuChinh()

        boDieuKhienHieuChinh =
            BoDieuKhienHieuChinh(
                boThuThap =
                    boThuThapMauHieuChinh
            )

        viTriBuocHieuChinh =
            0

        dangChuyenBuocHieuChinh =
            false

        dangHieuChinh =
            true

        btnBatDauTheoDoi.isEnabled =
            false

        nhanDienNghiengDau.datLai()
        nhanDienHuongDau.datLai()
        datLaiNhanDienMieng()
        datLaiNhanDienMat()

        hienThiGiaoDienHieuChinh()

        batDauBuocHieuChinhHienTai()
    }

    private fun batDauBuocHieuChinhHienTai() {

        if (!dangHieuChinh) {
            return
        }

        if (
            viTriBuocHieuChinh !in
            danhSachBuocHieuChinh.indices
        ) {

            hoanTatHieuChinh()
            return
        }

        val buoc =
            danhSachBuocHieuChinh[
                viTriBuocHieuChinh
            ]

        dangChuyenBuocHieuChinh =
            false

        boDieuKhienHieuChinh
            .batDauBuoc(
                buoc = buoc,
                thoiGianMs =
                    SystemClock.uptimeMillis()
            )

        runOnUiThread {

            txtBuocHieuChinh.text =
                "Bước ${viTriBuocHieuChinh + 1} / " +
                        "${danhSachBuocHieuChinh.size}"

            txtDongTacHieuChinh.text =
                boDieuKhienHieuChinh
                    .layTenDongTac(
                        buoc
                    )

            txtHuongDanHieuChinh.text =
                boDieuKhienHieuChinh
                    .layHuongDan(
                        buoc
                    )

            txtDemNguocHieuChinh.text =
                "Chuẩn bị..."

            progressHieuChinh.progress =
                0

            txtTienDoHieuChinh.text =
                "0%"

            txtPhanHoiHieuChinh.text =
                "Đọc hướng dẫn và chuẩn bị"
        }
    }

    private fun xuLyDuLieuHieuChinh(
        duLieu: DuLieuKhuonMat,
        thoiGianMs: Long
    ) {

        if (
            !dangHieuChinh ||
            dangChuyenBuocHieuChinh
        ) {
            return
        }

        val ketQua =
            boDieuKhienHieuChinh
                .xuLy(
                    duLieu = duLieu,
                    thoiGianMs = thoiGianMs
                )

        runOnUiThread {

            progressHieuChinh.progress =
                ketQua.tienDoPhanTram

            txtTienDoHieuChinh.text =
                "${ketQua.tienDoPhanTram}%"

            when (ketQua.trangThai) {

                TrangThaiHieuChinh.CHUAN_BI -> {

                    txtDemNguocHieuChinh.text =
                        if (
                            ketQua.soGiayChuanBiConLai > 0
                        ) {
                            "${ketQua.soGiayChuanBiConLai}"
                        } else {
                            "Chuẩn bị..."
                        }

                    txtPhanHoiHieuChinh.text =
                        "Chuẩn bị thực hiện động tác"
                }

                TrangThaiHieuChinh.CHO_DUNG_TU_THE -> {

                    txtDemNguocHieuChinh.text =
                        ""

                    txtPhanHoiHieuChinh.text =
                        ketQua.thongDiep
                }

                TrangThaiHieuChinh.DANG_GIU -> {

                    txtDemNguocHieuChinh.text =
                        ""

                    txtPhanHoiHieuChinh.text =
                        "✓ ${ketQua.thongDiep}"
                }

                TrangThaiHieuChinh.HOAN_THANH -> {

                    txtDemNguocHieuChinh.text =
                        ""

                    progressHieuChinh.progress =
                        100

                    txtTienDoHieuChinh.text =
                        "100%"

                    txtPhanHoiHieuChinh.text =
                        "✓ Hoàn thành"
                }
            }
        }

        if (
            ketQua.trangThai !=
            TrangThaiHieuChinh.HOAN_THANH
        ) {
            return
        }

        dangChuyenBuocHieuChinh =
            true

        boDieuKhienHieuChinh
            .ketThucBuoc()

        viTriBuocHieuChinh++

        if (
            viTriBuocHieuChinh >=
            danhSachBuocHieuChinh.size
        ) {

            hoanTatHieuChinh()
            return
        }

        khungHieuChinh.postDelayed(
            {

                if (dangHieuChinh) {
                    batDauBuocHieuChinhHienTai()
                }

            },
            THOI_GIAN_CHUYEN_BUOC_HIEU_CHINH_MS
        )
    }

    private fun hoanTatHieuChinh() {

        val cauHinhMoi =
            boHocNguongThichNghi
                .hoc(
                    boThuThapMauHieuChinh
                )

        if (cauHinhMoi == null) {

            ketThucTrangThaiHieuChinh()

            runOnUiThread {
                capNhatTrangThaiHeThong(
                    "● Hiệu chỉnh thất bại, vui lòng thử lại"
                )
            }

            return
        }

        val cauHinhHienTai =
            cauHinhNhanDienCuChi

        val cauHinhDaHopNhat =
            cauHinhMoi.copy(

                huongDau =
                    cauHinhMoi.huongDau.copy(
                        thoiGianGiuYawMs =
                            cauHinhHienTai
                                .huongDau
                                .thoiGianGiuYawMs,
                        thoiGianGiuPitchMs =
                            cauHinhHienTai
                                .huongDau
                                .thoiGianGiuPitchMs,
                        thoiGianGraceMs =
                            cauHinhHienTai
                                .huongDau
                                .thoiGianGraceMs,
                        thoiGianTrungTinhMs =
                            cauHinhHienTai
                                .huongDau
                                .thoiGianTrungTinhMs
                    ),

                nghiengDau =
                    cauHinhMoi.nghiengDau.copy(
                        thoiGianGiuMs =
                            cauHinhHienTai
                                .nghiengDau
                                .thoiGianGiuMs
                    ),

                nhamHaiMat =
                    cauHinhMoi.nhamHaiMat.copy(
                        thoiGianNhamXacNhanMs =
                            cauHinhHienTai
                                .nhamHaiMat
                                .thoiGianNhamXacNhanMs,
                        thoiGianMoDeRearmMs =
                            cauHinhHienTai
                                .nhamHaiMat
                                .thoiGianMoDeRearmMs,
                        thoiGianNhieuChoPhepMs =
                            cauHinhHienTai
                                .nhamHaiMat
                                .thoiGianNhieuChoPhepMs
                    ),

                moMieng =
                    cauHinhMoi.moMieng.copy(
                        thoiGianGiuBackMs =
                            cauHinhHienTai
                                .moMieng
                                .thoiGianGiuBackMs,
                        thoiGianDongDeRearmMs =
                            cauHinhHienTai
                                .moMieng
                                .thoiGianDongDeRearmMs
                    ),

                moMiengHaiLan =
                    cauHinhMoi.moMiengHaiLan.copy(
                        thoiGianGiuBackMs =
                            cauHinhHienTai
                                .moMiengHaiLan
                                .thoiGianGiuBackMs,
                        thoiGianMoNganToiThieuMs =
                            cauHinhHienTai
                                .moMiengHaiLan
                                .thoiGianMoNganToiThieuMs,
                        khoangChoLanHaiMs =
                            cauHinhHienTai
                                .moMiengHaiLan
                                .khoangChoLanHaiMs,
                        thoiGianDongDeRearmMs =
                            cauHinhHienTai
                                .moMiengHaiLan
                                .thoiGianDongDeRearmMs,
                        thoiGianNhieuChoPhepMs =
                            cauHinhHienTai
                                .moMiengHaiLan
                                .thoiGianNhieuChoPhepMs
                    ),

                hanhDong =
                    cauHinhHienTai.hanhDong
            )

        khoCauHinhNhanDienCuChi
            .luuCauHinh(
                cauHinhDaHopNhat
            )

        cauHinhNhanDienCuChi =
            cauHinhDaHopNhat

        boChuanHoaDuLieuKhuonMat =
            BoChuanHoaDuLieuKhuonMat(
                cauHinhNhanDienCuChi.chuanHoa
            )

        khoiTaoNhanDienNghiengDau()
        khoiTaoNhanDienMoMieng()
        khoiTaoNhanDienMoMiengHaiLan()
        khoiTaoNhanDienHuongDau()
        khoiTaoNhanDienNhamHaiMat()

        ketThucTrangThaiHieuChinh()

        runOnUiThread {
            capNhatTrangThaiHeThong(
                noiDung =
                    "● Hiệu chỉnh cá nhân hoàn tất",
                mauChu =
                    R.color.xanh_trang_thai
            )
        }
    }

    private fun huyHieuChinhKhiMoCaiDat() {

        if (!dangHieuChinh) {
            return
        }

        if (
            ::boThuThapMauHieuChinh
                .isInitialized
        ) {
            boThuThapMauHieuChinh
                .datLai()
        }

        if (
            ::boDieuKhienHieuChinh
                .isInitialized
        ) {
            boDieuKhienHieuChinh
                .datLai()
        }

        dangHieuChinh =
            false

        dangChuyenBuocHieuChinh =
            false

        viTriBuocHieuChinh =
            0

        btnBatDauTheoDoi.isEnabled =
            true

        progressHieuChinh.progress =
            0

        txtTienDoHieuChinh.text =
            "0%"

        txtDemNguocHieuChinh.text =
            ""

        txtPhanHoiHieuChinh.text =
            ""

        khungChuanBiHieuChinh.visibility =
            View.VISIBLE

        khungTienTrinhHieuChinh.visibility =
            View.GONE

        // Giữ nguyên trang Hiệu chỉnh phía sau Cài đặt
        dangMoTrangHieuChinh =
            true

        khungHieuChinh.visibility =
            View.VISIBLE

        capNhatMenuDuoi(
            dangHieuChinh = true
        )
    }

    private fun huyHieuChinh() {

        if (!dangHieuChinh) {
            return
        }

        if (
            ::boThuThapMauHieuChinh
                .isInitialized
        ) {

            boThuThapMauHieuChinh
                .datLai()
        }

        if (
            ::boDieuKhienHieuChinh
                .isInitialized
        ) {

            boDieuKhienHieuChinh
                .datLai()
        }

        ketThucTrangThaiHieuChinh()

        capNhatTrangThaiHeThong(
            "● Đã hủy hiệu chỉnh"
        )
    }

    private fun ketThucTrangThaiHieuChinh() {

        dangHieuChinh =
            false

        dangChuyenBuocHieuChinh =
            false

        viTriBuocHieuChinh =
            0

        if (
            ::boDieuKhienHieuChinh
                .isInitialized
        ) {

            boDieuKhienHieuChinh
                .datLai()
        }

        runOnUiThread {

            btnBatDauTheoDoi.isEnabled =
                true

            progressHieuChinh.progress =
                0

            txtTienDoHieuChinh.text =
                "0%"

            txtDemNguocHieuChinh.text =
                ""

            txtPhanHoiHieuChinh.text =
                ""

            anGiaoDienHieuChinh()
        }
    }

    private fun hienThiGiaoDienHieuChinh() {

        runOnUiThread {

            dangMoTrangHieuChinh =
                true

            khungTrangThaiHeThong.visibility =
                View.GONE

            noiDungChinh.visibility =
                View.GONE

            khungHuongDan.visibility =
                View.GONE

            khungHieuChinh.visibility =
                View.VISIBLE

            khungChuanBiHieuChinh.visibility =
                View.GONE

            khungTienTrinhHieuChinh.visibility =
                View.VISIBLE

            capNhatMenuDuoi(
                dangHieuChinh = true
            )
        }
    }

    private fun anGiaoDienHieuChinh() {

        dangMoTrangHieuChinh =
            false

        dangChoHieuChinhTuCaiDat =
            false

        khungChuanBiHieuChinh.visibility =
            View.VISIBLE

        khungTienTrinhHieuChinh.visibility =
            View.GONE

        khungHieuChinh.visibility =
            View.GONE

        khungTrangThaiHeThong.visibility =
            View.VISIBLE

        noiDungChinh.visibility =
            View.VISIBLE

        capNhatMenuDuoi(
            dangHieuChinh = false
        )

        taoHieuUngMoNoiDungManHinhChinh()
    }

    private fun taoHieuUngMoNoiDungManHinhChinh() {

        noiDungCuonManHinhChinh
            .animate()
            .cancel()

        noiDungCuonManHinhChinh
            .scrollTo(
                0,
                0
            )

        noiDungCuonManHinhChinh.alpha =
            0f

        noiDungCuonManHinhChinh.translationY =
            DO_DICH_CHUYEN_NOI_DUNG_DP *
                    resources
                        .displayMetrics
                        .density

        // Chỉ nội dung trượt lên, menu dưới đứng yên
        noiDungCuonManHinhChinh
            .animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(
                THOI_GIAN_HIEU_UNG_NOI_DUNG_MS
            )
            .start()
    }

    private fun capNhatMenuDuoi(
        dangHieuChinh: Boolean,
        dangHuongDan: Boolean = false
    ) {

        val mauDangChon =
            Color.parseColor(
                "#218A68"
            )

        val mauThuong =
            Color.parseColor(
                "#7D8F88"
            )

        val trangChuDangChon =
            !dangHieuChinh &&
                    !dangHuongDan

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

            nut.setTextColor(
                mau
            )

            nut.setBackgroundColor(
                Color.TRANSPARENT
            )

            nut.compoundDrawablesRelative
                .forEach { drawable ->
                    drawable
                        ?.mutate()
                        ?.setTint(
                            mau
                        )
                }

            // Không phóng to hoặc dịch chuyển mục đang chọn
            nut.alpha = 1f
            nut.scaleX = 1f
            nut.scaleY = 1f
            nut.translationX = 0f
            nut.translationY = 0f
        }

        capNhatNut(
            navTrangChu,
            trangChuDangChon
        )

        capNhatNut(
            navHuongDan,
            dangHuongDan
        )

        capNhatNut(
            navHieuChinh,
            dangHieuChinh
        )

        capNhatNut(
            navCaiDat,
            false
        )
    }

    // QUYỀN CAMERA

    private fun kiemTraVaBatCamera() {

        val daCoQuyenCamera =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (daCoQuyenCamera) {

            batCamera()

        } else {

            yeuCauQuyenCamera.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    // BẬT CAMERA

    private fun batCamera() {

        if (cameraDangBat || cameraDangKhoiDong) {
            return
        }

        theoDoiDangHoatDong =
            true

        cameraDangKhoiDong =
            true

        txtTrangThaiCamera.visibility =
            View.GONE

        khungCamera.visibility =
            View.VISIBLE

        capNhatTrangThaiHeThong(
            "● Đang khởi động Camera..."
        )

        btnBatDauTheoDoi.isEnabled =
            false

        nhanDienNghiengDau.datLai()

        datLaiNhanDienMieng()

        datLaiNhanDienMat()

        batDichVuTheoDoi()

        quanLyCamera.batCamera(

            khiThanhCong = {

                cameraDangKhoiDong =
                    false

                cameraDangBat =
                    true

                theoDoiDangHoatDong =
                    true

                capNhatTrangThaiConTroTheoCheDo(
                    boDinhTuyenCheDo
                        .layCheDoHienTai()
                )

                dangThayKhuonMat =
                    null

                thoiGianCapNhatUiGanNhat =
                    0L

                nhanDienNghiengDau.datLai()

                datLaiNhanDienMieng()

                datLaiNhanDienMat()

                capNhatTrangThaiHeThong(
                    "● Camera đang hoạt động - đang tìm khuôn mặt"
                )

                capNhatGiaoDienNutTheoDoi(
                    dangTheoDoi = true
                )

                btnBatDauTheoDoi.isEnabled =
                    true

                runOnUiThread {
                    batDauHieuChinhTuCaiDat()
                }
            },

            khiLoi = { exception ->

                cameraDangKhoiDong =
                    false

                dangChoHieuChinhTuCaiDat =
                    false

                tatDichVuTheoDoi()

                DichVuTruyCapFaceAccess
                    .tatTrangThaiOverlay()

                cameraDangBat =
                    false

                theoDoiDangHoatDong =
                    false

                DichVuTruyCapFaceAccess
                    .tatConTro()

                dangThayKhuonMat =
                    null

                nhanDienNghiengDau.datLai()

                datLaiNhanDienMat()

                hienThiCameraDaDung(
                    "CAMERA\nKhông thể khởi động"
                )

                datLaiThongTinNhanDien()

                capNhatTrangThaiHeThong(
                    "● Lỗi Camera: ${
                        exception.message
                            ?: "Không xác định"
                    }"
                )
                capNhatGiaoDienNutTheoDoi(
                    dangTheoDoi = false
                )

                btnBatDauTheoDoi.isEnabled =
                    true
            }
        )
    }

    // NHẬN LẠI CAMERA TỪ SERVICE

    private fun batLaiCameraActivitySauBanGiao() {

        if (
            !theoDoiDangHoatDong ||
            cameraDangBat ||
            cameraDangKhoiDong
        ) {
            return
        }

        Log.d(
            TAG_BAN_GIAO_CAMERA,
            "Bat lai Camera tren Activity sau ban giao"
        )

        nhanDienNghiengDau.datLai()

        cameraDangKhoiDong =
            true

        quanLyCamera.batCamera(

            khiThanhCong = {

                cameraDangKhoiDong =
                    false

                cameraDangBat =
                    true

                capNhatTrangThaiConTroTheoCheDo(
                    boDinhTuyenCheDo
                        .layCheDoHienTai()
                )

                dangThayKhuonMat =
                    null

                thoiGianCapNhatUiGanNhat =
                    0L

                nhanDienNghiengDau.datLai()

                datLaiNhanDienMat()

                runOnUiThread {

                    khungCamera.visibility =
                        View.VISIBLE

                    txtTrangThaiCamera.visibility =
                        View.GONE

                    capNhatTrangThaiHeThong(
                        "● Camera đang hoạt động - đang tìm khuôn mặt"
                    )

                    capNhatGiaoDienNutTheoDoi(
                        dangTheoDoi = true
                    )

                    btnBatDauTheoDoi.isEnabled =
                        true

                    batDauHieuChinhTuCaiDat()
                }

                Log.d(
                    TAG_BAN_GIAO_CAMERA,
                    "Activity da nhan lai Camera thanh cong"
                )
            },

            khiLoi = { exception ->

                cameraDangKhoiDong =
                    false

                dangChoHieuChinhTuCaiDat =
                    false

                cameraDangBat =
                    false

                Log.e(
                    TAG_BAN_GIAO_CAMERA,
                    "Activity khong the nhan lai Camera",
                    exception
                )

                runOnUiThread {

                    capNhatTrangThaiHeThong(
                        "● Không thể nhận lại Camera: ${
                            exception.message
                                ?: "Không xác định"
                        }"
                    )
                }
            }
        )
    }

    // DỪNG CAMERA

    private fun tatCamera() {

        if (dangHieuChinh) {
            huyHieuChinh()
        }

        theoDoiDangHoatDong =
            false

        cameraDangKhoiDong =
            false

        DichVuTruyCapFaceAccess
            .tatConTro()

        DichVuTruyCapFaceAccess
            .tatTrangThaiOverlay()

        if (
            ::boDieuKhienLienHeHoTro.isInitialized
        ) {

            boDieuKhienLienHeHoTro
                .datLaiPhien()
        }

        dangChoCameraNenNhaQuyen =
            false

        soLanThuBanGiaoCamera =
            0

        cameraDangBat =
            false

        nhanDienNghiengDau.datLai()

        datLaiNhanDienMieng()

        datLaiNhanDienMat()

        quanLyCamera.tatCamera()

        tatDichVuTheoDoi()

        dangThayKhuonMat =
            null

        thoiGianCapNhatUiGanNhat =
            0L

        hienThiCameraDaDung(
            "CAMERA\nĐã dừng"
        )

        datLaiThongTinNhanDien()

        capNhatTrangThaiHeThong(
            "● Đã dừng theo dõi"
        )

        capNhatGiaoDienNutTheoDoi(
            dangTheoDoi = false
        )
    }

    // CẬP NHẬT GIAO DIỆN CHẾ ĐỘ

    private fun capNhatGiaoDienCheDo(
        cheDo: CheDoDieuKhien
    ) {

        runOnUiThread {

            datTatCaCardVeTrangThaiThuong()

            when (cheDo) {

                CheDoDieuKhien.DIEU_HUONG -> {

                    txtCheDoHienTai.text =
                        "ĐIỀU HƯỚNG"

                    danhDauCardDangChon(
                        cardDieuHuong
                    )
                }

                CheDoDieuKhien.MEDIA -> {

                    txtCheDoHienTai.text =
                        "MEDIA"

                    danhDauCardDangChon(
                        cardMedia
                    )
                }

                CheDoDieuKhien.HO_TRO -> {

                    txtCheDoHienTai.text =
                        "HỖ TRỢ"

                    danhDauCardDangChon(
                        cardHoTro
                    )
                }

                CheDoDieuKhien.CON_TRO -> {

                    txtCheDoHienTai.text =
                        "CON TRỎ"

                    danhDauCardDangChon(
                        cardConTro
                    )
                }
            }

            txtCheDoHienTai
                .animate()
                .cancel()

            txtCheDoHienTai.alpha =
                0f

            txtCheDoHienTai.translationY =
                4f *
                        resources
                            .displayMetrics
                            .density

            txtCheDoHienTai
                .animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(180L)
                .start()
        }
    }

    private fun datTatCaCardVeTrangThaiThuong() {

        val mauChuThuong =
            Color.parseColor(
                "#61736C"
            )

        val elevationThuong =
            resources
                .displayMetrics
                .density

        val danhSachCard =
            listOf(
                cardDieuHuong,
                cardMedia,
                cardHoTro,
                cardConTro
            )

        danhSachCard.forEach { card ->

            card.animate()
                .cancel()

            card.setBackgroundResource(
                R.drawable.fa_home_mode_idle
            )

            card.setTextColor(
                mauChuThuong
            )

            card.compoundDrawablesRelative
                .forEach { drawable ->
                    drawable
                        ?.mutate()
                        ?.setTint(
                            mauChuThuong
                        )
                }

            card.scaleX =
                1f

            card.scaleY =
                1f

            card.elevation =
                elevationThuong
        }
    }

    private fun danhDauCardDangChon(
        card: TextView
    ) {

        card.setBackgroundResource(
            R.drawable.fa_home_mode_selected
        )

        card.setTextColor(
            Color.WHITE
        )

        card.compoundDrawablesRelative
            .forEach { drawable ->
                drawable
                    ?.mutate()
                    ?.setTint(
                        Color.WHITE
                    )
            }

        card.elevation =
            4f *
                    resources
                        .displayMetrics
                        .density

        card.scaleX =
            0.97f

        card.scaleY =
            0.97f

        card.animate()
            .scaleX(1.035f)
            .scaleY(1.035f)
            .setDuration(180L)
            .start()
    }

    // TRẠNG THÁI KHUÔN MẶT

    private fun capNhatTrangThaiHeThong(
        noiDung: String,
        mauChu: Int = R.color.do_trang_thai
    ) {
        val mauTrangThai =
            ContextCompat.getColor(
                this,
                mauChu
            )

        txtTrangThaiHeThong.text =
            noiDung

        txtTrangThaiHeThong.setTextColor(
            mauTrangThai
        )
    }

    private fun capNhatTrangThaiKhuonMat(
        coKhuonMat: Boolean
    ) {

        if (!cameraDangBat) {
            return
        }

        if (
            dangThayKhuonMat ==
            coKhuonMat
        ) {
            return
        }

        dangThayKhuonMat =
            coKhuonMat

        DichVuTruyCapFaceAccess
            .capNhatKhuonMatTrangThaiOverlay(
                coKhuonMat
            )

        runOnUiThread {

            if (!cameraDangBat) {
                return@runOnUiThread
            }

            if (coKhuonMat) {

                capNhatTrangThaiHeThong(
                    noiDung = "● Đã phát hiện khuôn mặt",
                    mauChu = R.color.xanh_trang_thai
                )

            } else {

                capNhatTrangThaiHeThong(
                    "● Không thấy khuôn mặt"
                )
            }
        }
    }

    // DỮ LIỆU REALTIME

    private fun capNhatDuLieuKhuonMat(
        duLieu: DuLieuKhuonMat
    ) {

        if (!cameraDangBat) {
            return
        }

        val thoiGianHienTai =
            SystemClock.uptimeMillis()

        if (
            thoiGianHienTai -
            thoiGianCapNhatUiGanNhat <
            KHOANG_CAP_NHAT_UI_MS
        ) {
            return
        }

        thoiGianCapNhatUiGanNhat =
            thoiGianHienTai

        runOnUiThread {

            if (!cameraDangBat) {
                return@runOnUiThread
            }

            txtRoll.text =
                "ROLL : ${
                    dinhDangGoc(
                        duLieu.roll
                    )
                }"

            txtYaw.text =
                "YAW : ${
                    dinhDangGoc(
                        duLieu.yaw
                    )
                }"

            txtPitch.text =
                "PITCH : ${
                    dinhDangGoc(
                        duLieu.pitch
                    )
                }"

            txtTrangThaiMat.text =
                "MẮT : L=${
                    dinhDangDiem(
                        duLieu.doNhamMatTrai
                    )
                } | R=${
                    dinhDangDiem(
                        duLieu.doNhamMatPhai
                    )
                }"

            txtTrangThaiMieng.text =
                "MIỆNG : ${
                    dinhDangDiem(
                        duLieu.doMoMieng
                    )
                }"
        }
    }

    // FORMAT

    private fun dinhDangGoc(
        giaTri: Float?
    ): String {

        if (giaTri == null) {
            return "--"
        }

        return String.format(
            Locale.US,
            "%.2f°",
            giaTri
        )
    }

    private fun dinhDangDiem(
        giaTri: Float?
    ): String {

        if (giaTri == null) {
            return "--"
        }

        return String.format(
            Locale.US,
            "%.3f",
            giaTri
        )
    }

    // RESET DEBUG UI

    private fun datLaiThongTinNhanDien() {

        runOnUiThread {

            txtRoll.text =
                "ROLL : --"

            txtYaw.text =
                "YAW : --"

            txtPitch.text =
                "PITCH : --"

            txtTrangThaiMat.text =
                "MẮT : --"

            txtTrangThaiMieng.text =
                "MIỆNG : --"
        }
    }

    // CAMERA PLACEHOLDER

    private fun hienThiCameraDaDung(
        noiDung: String
    ) {

        khungCamera.visibility =
            View.GONE

        txtTrangThaiCamera.text =
            noiDung

        txtTrangThaiCamera.visibility =
            View.VISIBLE
    }

    // ACTIVITY LIFECYCLE - BÀN GIAO CAMERA

    override fun onResume() {
        super.onResume()

        if (dangChoBatDichVuTruyCap) {

            tiepTucTheoDoiSauKhiBatTruyCap()
        }
    }

    override fun onStart() {
        super.onStart()

        if (::boDinhTuyenCheDo.isInitialized) {

            capNhatGiaoDienCheDo(
                boDinhTuyenCheDo
                    .layCheDoHienTai()
            )

            if (dangMoTrangHuongDan) {
                capNhatNoiDungHuongDan(
                    cheDoHuongDanDangXem
                )

            }
        }

        if (
            theoDoiDangHoatDong &&
            !cameraDangBat &&
            ::quanLyCamera.isInitialized
        ) {

            yeuCauTatCameraNenDeNhanLaiCamera()
        }
    }

    override fun onStop() {

        if (dangHieuChinh) {
            huyHieuChinh()
        }

        if (
            theoDoiDangHoatDong &&
            ::quanLyCamera.isInitialized
        ) {

            cameraDangKhoiDong =
                false

            cameraDangBat =
                false

            nhanDienNghiengDau.datLai()

            datLaiNhanDienMieng()

            datLaiNhanDienMat()

            quanLyCamera.tatCamera()

            Log.d(
                TAG_BAN_GIAO_CAMERA,
                "Activity onStop -> da nha Camera"
            )

            yeuCauBatCameraNen()
        }

        super.onStop()
    }

    // DESTROY

    override fun onDestroy() {

        if (::animationHuongDan.isInitialized) {
            animationHuongDan.cancelAnimation()
        }

        if (
            ::nhanDienNghiengDau.isInitialized
        ) {

            nhanDienNghiengDau.datLai()
        }

        if (
            ::nhanDienMoMieng.isInitialized
        ) {

            datLaiNhanDienMieng()
        }

        if (
            ::nhanDienHuongDau.isInitialized
        ) {

            nhanDienHuongDau.datLai()
        }

        datLaiNhanDienMat()

        if (
            ::quanLyCamera.isInitialized
        ) {

            quanLyCamera.dong()
        }

        if (
            ::xuLyKhuonMat.isInitialized
        ) {

            xuLyKhuonMat.dong()
        }

        huyDangKyBoNhanBanGiaoCamera()

        super.onDestroy()
    }

    // CONSTANT

    companion object {

        private const val DO_DICH_CHUYEN_NOI_DUNG_DP =
            10f

        private const val THOI_GIAN_HIEU_UNG_NOI_DUNG_MS =
            220L

        private const val ACTION_ACCESSIBILITY_DETAILS_SETTINGS =
            "android.settings.ACCESSIBILITY_DETAILS_SETTINGS"

        private const val SO_LAN_CHO_DICH_VU_TRUY_CAP =
            5

        private const val THOI_GIAN_CHO_DICH_VU_TRUY_CAP_MS =
            300L

        private const val THOI_GIAN_CHO_BAN_GIAO_CAMERA_MS =
            1200L

        private const val SO_LAN_THU_LAI_BAN_GIAO_CAMERA =
            2

        // Khoảng nghỉ ngắn trước khi chuyển sang bước tiếp theo
        private const val THOI_GIAN_CHUYEN_BUOC_HIEU_CHINH_MS =
            650L

        private const val KHOANG_CAP_NHAT_UI_MS =
            100L

        private const val TAG_LENH =
            "LenhToanCuc"

        private const val TAG_MEDIAPIPE =
            "FaceAccessMediaPipe"

        private const val TAG_CHE_DO =
            "CheDoDieuKhien"

        private const val TAG_DICH_VU =
            "DichVuTheoDoi"

        private const val TAG_CAI_DAT =
            "FaceAccessSettings"

        private const val TAG_TRUY_CAP =
            "FaceAccessAccessibility"

        private const val TAG_CON_TRO =
            "FaceAccessCursor"

        private const val TAG_BAN_GIAO_CAMERA =
            "BanGiaoCamera"

        private const val TAG_CU_CHI_MIENG =
            "CuChiMieng"

        private const val TAG_CU_CHI_HUONG_DAU =
            "CuChiHuongDau"

        private const val TAG_CU_CHI_THEO_CHE_DO =
            "CuChiTheoCheDo"

        private const val TAG_LENH_DIEU_HUONG =
            "LenhDieuHuong"

        private const val TAG_LENH_MEDIA =
            "LenhMedia"

        private const val TAG_LENH_HO_TRO =
            "LenhHoTro"
    }
}
