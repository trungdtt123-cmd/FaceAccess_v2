// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.dichvu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.example.faceaccess.v2.R
import com.example.faceaccess.v2.camera.QuanLyCamera
import com.example.faceaccess.v2.ai.hieuchinh.BoChuanHoaDuLieuKhuonMat
import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhNhanDienCuChi
import com.example.faceaccess.v2.cuchi.cauhinh.KhoCauHinhNhanDienCuChi
import com.example.faceaccess.v2.cuchi.cauhinh.HanhDongTuyChinhCuChi
import com.example.faceaccess.v2.cuchi.nghiengdau.HuongNghiengDau
import com.example.faceaccess.v2.cuchi.nghiengdau.NhanDienNghiengDau
import com.example.faceaccess.v2.cuchi.huongdau.HuongDau
import com.example.faceaccess.v2.cuchi.huongdau.NhanDienHuongDau
import com.example.faceaccess.v2.cuchi.mieng.NhanDienMoMieng
import com.example.faceaccess.v2.cuchi.mieng.NhanDienMoMiengHaiLan
import com.example.faceaccess.v2.cuchi.mat.NhanDienNhamHaiMat
import com.example.faceaccess.v2.chedo.BoDinhTuyenCheDo
import com.example.faceaccess.v2.chedo.CheDoDieuKhien
import com.example.faceaccess.v2.dieuphoi.DieuPhoiCuChi
import com.example.faceaccess.v2.dieuphoi.LenhToanCuc
import com.example.faceaccess.v2.dieuphoi.dieuhuong.LenhDieuHuong
import com.example.faceaccess.v2.dieuphoi.media.LenhMedia
import com.example.faceaccess.v2.dieuphoi.media.BoDieuKhienMedia
import com.example.faceaccess.v2.dieuphoi.hotro.LenhHoTro
import com.example.faceaccess.v2.dieuphoi.hotro.BoDieuKhienLienHeHoTro
import com.example.faceaccess.v2.dieuphoi.SuKienCuChi
import com.example.faceaccess.v2.truycap.DichVuTruyCapFaceAccess
import com.example.faceaccess.v2.thongbao.ThongBaoFaceAccess
import com.example.faceaccess.v2.khuonmat.TrichXuatDuLieuKhuonMat
import com.example.faceaccess.v2.khuonmat.PhanTichKhungHinhKhuonMat
import com.example.faceaccess.v2.khuonmat.XuLyKhuonMat
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class DichVuTheoDoiFaceAccess :
    Service(),
    LifecycleOwner {


    private val lifecycleRegistry =
        LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry


    private lateinit var xuLyKhuonMat:
            XuLyKhuonMat

    private lateinit var phanTichKhungHinhKhuonMat:
            PhanTichKhungHinhKhuonMat

    private lateinit var quanLyCamera:
            QuanLyCamera

    private lateinit var trichXuatDuLieuKhuonMat:
            TrichXuatDuLieuKhuonMat

    private lateinit var khoCauHinhNhanDienCuChi:
            KhoCauHinhNhanDienCuChi

    private lateinit var cauHinhNhanDienCuChi:
            CauHinhNhanDienCuChi

    private lateinit var boChuanHoaDuLieuKhuonMat:
            BoChuanHoaDuLieuKhuonMat

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


    private lateinit var dieuPhoiCuChi:
            DieuPhoiCuChi

    private lateinit var boDieuKhienMedia:
            BoDieuKhienMedia

    private lateinit var boDieuKhienLienHeHoTro:
            BoDieuKhienLienHeHoTro

    private lateinit var boDinhTuyenCheDo:
            BoDinhTuyenCheDo

    private val mainHandler =
        Handler(
            Looper.getMainLooper()
        )

    private val tacVuTaiLaiCauHinh =
        Runnable {

            if (
                ::khoCauHinhNhanDienCuChi
                    .isInitialized
            ) {
                taiLaiCauHinhNhanDienNen()

                Log.d(
                    TAG,
                    "Da ap dung cau hinh moi khi dang chay nen"
                )
            }
        }

    private val boLangNgheCauHinh =
        SharedPreferences
            .OnSharedPreferenceChangeListener {
                    _,
                    _ ->

                mainHandler.removeCallbacks(
                    tacVuTaiLaiCauHinh
                )

                mainHandler.postDelayed(
                    tacVuTaiLaiCauHinh,
                    120L
                )
            }

    @Volatile
    private var cameraNenDangBat =
        false

    @Volatile
    private var cameraNenDangKhoiDong =
        false

    @Volatile
    private var dangChoXacNhanCameraNenDaTat =
        false

    private var thoiGianLogGanNhat =
        0L

    private var dangThayKhuonMatNen: Boolean? =
        null


    override fun onCreate() {
        super.onCreate()

        lifecycleRegistry.currentState =
            Lifecycle.State.CREATED

        Log.d(
            TAG,
            "Dich vu theo doi da duoc tao"
        )

        taoKenhThongBao()

        batForeground()

        khoiTaoCauHinhNhanDienNen()

        khoCauHinhNhanDienCuChi
            .dangKyBoLangNgheThayDoi(
                boLangNgheCauHinh
            )

        khoiTaoBoDinhTuyenCheDoNen()

        DichVuTruyCapFaceAccess
            .batTrangThaiOverlay(
                boDinhTuyenCheDo
                    .layCheDoHienTai()
            )

        khoiTaoBoDieuKhienMediaNen()

        khoiTaoBoDieuKhienLienHeHoTroNen()

        khoiTaoDieuPhoiCuChiNen()

        khoiTaoNhanDienCuChiNen()

        khoiTaoNhanDienMoMiengNen()

        khoiTaoNhanDienMoMiengHaiLanNen()

        khoiTaoNhanDienHuongDauNen()

        khoiTaoNhanDienNhamHaiMatNen()

        khoiTaoXuLyKhuonMatNen()

        khoiTaoCameraNen()

        dichVuDangHoatDong =
            true
    }


    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        lifecycleRegistry.currentState =
            Lifecycle.State.STARTED

        Log.d(
            TAG,
            "Dich vu theo doi dang chay"
        )

        when (intent?.action) {

            HANH_DONG_BAT_CAMERA_NEN -> {

                Log.d(
                    TAG_CAMERA_NEN,
                    "Nhan yeu cau BAT Camera nen"
                )

                taiLaiCauHinhNhanDienNen()
                batCameraNen()
            }

            HANH_DONG_TAT_CAMERA_NEN -> {

                if (dangChoXacNhanCameraNenDaTat) {

                    Log.d(
                        TAG_BAN_GIAO_CAMERA,
                        "Dang cho ACK DA_TAT - bo qua lenh TAT trung lap"
                    )

                } else {

                    dangChoXacNhanCameraNenDaTat =
                        true

                    Log.d(
                        TAG_CAMERA_NEN,
                        "Nhan yeu cau TAT Camera nen"
                    )

                    tatCameraNen()
                }
            }
        }

        // Camera FGS không nên tự hồi sinh ở nền với trạng thái mơ hồ.
        return START_NOT_STICKY
    }

    // Cấu hình nhận diện

    private fun khoiTaoCauHinhNhanDienNen() {

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

    private fun taiLaiCauHinhNhanDienNen() {

        // Xóa trạng thái detector cũ trước khi thay instance mới.
        // Nếu double-mouth đang giữa chừng, trạng thái chặn toàn cục cũng phải được giải phóng.
        datLaiNhanDienMieng()

        cauHinhNhanDienCuChi =
            khoCauHinhNhanDienCuChi
                .layCauHinh()

        boChuanHoaDuLieuKhuonMat =
            BoChuanHoaDuLieuKhuonMat(
                cauHinhNhanDienCuChi.chuanHoa
            )

        if (::dieuPhoiCuChi.isInitialized) {
            khoiTaoDieuPhoiCuChiNen()
        }

        if (::nhanDienNghiengDau.isInitialized) {
            khoiTaoNhanDienCuChiNen()
        }

        if (::nhanDienMoMieng.isInitialized) {
            khoiTaoNhanDienMoMiengNen()
        }

        if (::nhanDienMoMiengHaiLan.isInitialized) {
            khoiTaoNhanDienMoMiengHaiLanNen()
        }

        if (::nhanDienHuongDau.isInitialized) {
            khoiTaoNhanDienHuongDauNen()
        }

        if (::nhanDienNhamHaiMat.isInitialized) {
            khoiTaoNhanDienNhamHaiMatNen()
        }

        Log.d(
            TAG_CAU_HINH,
            "Da tai lai cau hinh nen"
        )
    }

    // Chế độ

    private fun khoiTaoBoDinhTuyenCheDoNen() {

        boDinhTuyenCheDo =
            BoDinhTuyenCheDo { cheDoMoi ->

                Log.d(
                    TAG_CU_CHI_NEN,
                    "NEN: CHE DO MOI = $cheDoMoi"
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

                // Không để cử chỉ đã bắt đầu ở chế độ cũ
                // hoàn tất và phát lệnh trong chế độ mới.
                if (::nhanDienNghiengDau.isInitialized) {
                    nhanDienNghiengDau.datLai()
                }

                if (::nhanDienHuongDau.isInitialized) {
                    nhanDienHuongDau.datLai()
                }

                datLaiNhanDienMieng()
                datLaiNhanDienMat()

                capNhatTrangThaiConTroTheoCheDoNen(
                    cheDoMoi
                )
            }

        val cheDoBanDau =
            boDinhTuyenCheDo
                .layCheDoHienTai()

        DichVuTruyCapFaceAccess
            .capNhatCheDoTrangThaiOverlay(
                cheDoBanDau
            )

        capNhatTrangThaiConTroTheoCheDoNen(
            cheDoBanDau
        )
    }


    private fun capNhatTrangThaiConTroTheoCheDoNen(
        cheDo: CheDoDieuKhien
    ) {

        if (
            cheDo == CheDoDieuKhien.CON_TRO
        ) {
            val thanhCong =
                DichVuTruyCapFaceAccess.batConTro()

            Log.d(
                TAG_CON_TRO,
                if (thanhCong) {
                    "NEN: CURSOR BAT"
                } else {
                    "NEN: CURSOR CHUA BAT - AccessibilityService chua san sang"
                }
            )
        } else {
            DichVuTruyCapFaceAccess.tatConTro()
        }
    }


    private fun khoiTaoBoDieuKhienMediaNen() {

        boDieuKhienMedia =
            BoDieuKhienMedia(
                applicationContext
            )
    }


    private fun khoiTaoBoDieuKhienLienHeHoTroNen() {

        boDieuKhienLienHeHoTro =
            BoDieuKhienLienHeHoTro(
                applicationContext
            )
    }


    private fun khoiTaoDieuPhoiCuChiNen() {

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
                        "NEN: MODE=$cheDo | HUONG=$huong"
                    )
                },
                khiCoLenhDieuHuong = { lenhDieuHuong ->

                    Log.d(
                        TAG_LENH_DIEU_HUONG,
                        "NEN: LENH_DIEU_HUONG=$lenhDieuHuong"
                    )

                    when (lenhDieuHuong) {

                        LenhDieuHuong.TRUOC -> {

                            mainHandler.post {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiTruoc()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "NEN: TRUOC Accessibility THANH_CONG"
                                    } else {
                                        "NEN: TRUOC Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }

                        LenhDieuHuong.TIEP_THEO -> {

                            mainHandler.post {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiTiepTheo()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "NEN: TIEP_THEO Accessibility THANH_CONG"
                                    } else {
                                        "NEN: TIEP_THEO Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }

                        LenhDieuHuong.CUON_LEN -> {

                            mainHandler.post {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiCuonLen()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "NEN: CUON_LEN Accessibility THANH_CONG"
                                    } else {
                                        "NEN: CUON_LEN Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }

                        LenhDieuHuong.CUON_XUONG -> {

                            mainHandler.post {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiCuonXuong()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "NEN: CUON_XUONG Accessibility THANH_CONG"
                                    } else {
                                        "NEN: CUON_XUONG Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }

                        LenhDieuHuong.XAC_NHAN -> {

                            mainHandler.post {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiXacNhanDieuHuong()

                                Log.d(
                                    TAG_LENH_DIEU_HUONG,
                                    if (thanhCong) {
                                        "NEN: XAC_NHAN Accessibility THANH_CONG"
                                    } else {
                                        "NEN: XAC_NHAN Accessibility THAT_BAI"
                                    }
                                )
                            }
                        }
                    }
                },
                khiCoLenhMedia = { lenhMedia ->

                    Log.d(
                        TAG_LENH_MEDIA,
                        "NEN: LENH_MEDIA=$lenhMedia"
                    )

                    mainHandler.post {

                        val thanhCong =
                            boDieuKhienMedia
                                .thucThi(
                                    lenhMedia
                                )

                        Log.d(
                            TAG_LENH_MEDIA,
                            if (thanhCong) {
                                "NEN: MEDIA_ACTION=$lenhMedia THANH_CONG"
                            } else {
                                "NEN: MEDIA_ACTION=$lenhMedia THAT_BAI"
                            }
                        )
                    }
                },

                khiCoLenhHoTro = { lenhHoTro ->

                    Log.d(
                        TAG_LENH_HO_TRO,
                        "NEN: LENH_HO_TRO=$lenhHoTro"
                    )

                    mainHandler.post {

                        val ketQua =
                            boDieuKhienLienHeHoTro
                                .thucThi(
                                    lenhHoTro
                                )

                        Log.d(
                            TAG_LENH_HO_TRO,
                            "NEN: HO_TRO_ACTION=$lenhHoTro | " +
                                    "THANH_CONG=${ketQua.thanhCong} | " +
                                    "THONG_BAO=${ketQua.thongBao}"
                        )

                        ThongBaoFaceAccess.hienThi(
                            context = applicationContext,
                            noiDung = ketQua.thongBao
                        )
                    }
                },

                khiCoLenhConTro = { lenhConTro ->

                    mainHandler.post {
                        val thanhCong =
                            DichVuTruyCapFaceAccess
                                .thucThiDiChuyenConTro(
                                    lenhConTro
                                )

                        Log.d(
                            TAG_CON_TRO,
                            "NEN: MOVE=$lenhConTro | OK=$thanhCong"
                        )
                    }
                },

                khiCoXacNhanConTro = {

                    mainHandler.post {
                        val thanhCong =
                            DichVuTruyCapFaceAccess
                                .thucThiClickConTro()

                        Log.d(
                            TAG_CON_TRO,
                            "NEN: EYE_CLICK | OK=$thanhCong"
                        )
                    }
                },

                khiCoLenh = { lenh ->

                    when (lenh) {

                        LenhToanCuc.HOME -> {

                            Log.d(
                                TAG_CU_CHI_NEN,
                                "NEN: LENH HOME"
                            )

                            mainHandler.post {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiHome()

                                if (thanhCong) {

                                    Log.d(
                                        TAG_CU_CHI_NEN,
                                        "NEN: HOME Android THANH_CONG"
                                    )

                                } else {

                                    Log.e(
                                        TAG_CU_CHI_NEN,
                                        "NEN: HOME Android THAT_BAI - AccessibilityService chua san sang"
                                    )
                                }
                            }
                        }

                        LenhToanCuc.DOI_CHE_DO -> {

                            Log.d(
                                TAG_CU_CHI_NEN,
                                "NEN: LENH DOI_CHE_DO"
                            )

                            boDinhTuyenCheDo
                                .chuyenCheDoTiepTheo()
                        }

                        LenhToanCuc.BACK -> {

                            Log.d(
                                TAG_CU_CHI_MIENG,
                                "NEN: LENH BACK"
                            )

                            mainHandler.post {

                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .thucThiBack()

                                if (thanhCong) {

                                    Log.d(
                                        TAG_CU_CHI_MIENG,
                                        "NEN: BACK Android THANH_CONG"
                                    )

                                } else {

                                    Log.e(
                                        TAG_CU_CHI_MIENG,
                                        "NEN: BACK Android THAT_BAI - AccessibilityService chua san sang"
                                    )
                                }
                            }
                        }

                        LenhToanCuc.DOI_KHOA_CON_TRO -> {

                            mainHandler.post {
                                val thanhCong =
                                    DichVuTruyCapFaceAccess
                                        .doiKhoaConTro()

                                Log.d(
                                    TAG_CON_TRO,
                                    "NEN: MOUTH_DOUBLE_TOGGLE_LOCK | OK=$thanhCong"
                                )
                            }
                        }
                    }
                }
            )
    }


    private fun khoiTaoNhanDienCuChiNen() {

        trichXuatDuLieuKhuonMat =
            TrichXuatDuLieuKhuonMat()

        nhanDienNghiengDau =
            NhanDienNghiengDau(
                cauHinh =
                    cauHinhNhanDienCuChi.nghiengDau
            ) { huong ->

                when (huong) {

                    HuongNghiengDau.TRAI -> {

                        Log.d(
                            TAG_CU_CHI_NEN,
                            "NEN: NGHIENG TRAI"
                        )

                        dieuPhoiCuChi.xuLy(
                            SuKienCuChi.NghiengTrai
                        )
                    }

                    HuongNghiengDau.PHAI -> {

                        Log.d(
                            TAG_CU_CHI_NEN,
                            "NEN: NGHIENG PHAI"
                        )

                        dieuPhoiCuChi.xuLy(
                            SuKienCuChi.NghiengPhai
                        )
                    }
                }
            }
    }


    private fun khoiTaoNhanDienMoMiengNen() {

        nhanDienMoMieng =
            NhanDienMoMieng(
                cauHinh =
                    cauHinhNhanDienCuChi.moMieng
            ) {

                Log.d(
                    TAG_CU_CHI_MIENG,
                    "NEN: MO MIENG"
                )

                dieuPhoiCuChi.xuLy(
                    SuKienCuChi.MoMieng
                )
            }
    }

    private fun khoiTaoNhanDienMoMiengHaiLanNen() {
        nhanDienMoMiengHaiLan =
            NhanDienMoMiengHaiLan(
                cauHinh =
                    cauHinhNhanDienCuChi.moMiengHaiLan,
                khiMoMotLan = {
                    Log.d(
                        TAG_CU_CHI_MIENG,
                        "NEN: MO MIENG GIU - DETECTOR DON"
                    )
                },
                khiMoHaiLan = {

                    // Không để cùng lần mở thứ hai tiếp tục kích hoạt
                    // detector mở-giữ sau khi double-mouth đã được xác nhận.
                    if (::nhanDienMoMieng.isInitialized) {
                        nhanDienMoMieng.chanChoDenKhiDong()
                    }

                    Log.d(
                        TAG_CU_CHI_MIENG,
                        "NEN: MO MIENG HAI LAN"
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


    private fun khoiTaoNhanDienHuongDauNen() {

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
                    "NEN: HUONG $tenHuong"
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

    private fun khoiTaoNhanDienNhamHaiMatNen() {
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


    private fun khoiTaoXuLyKhuonMatNen() {

        xuLyKhuonMat =
            XuLyKhuonMat(
                context = this,
                langNghe =
                    object :
                        XuLyKhuonMat.LangNgheXuLyKhuonMat {

                        override fun khiKhoiTaoThanhCong() {

                            Log.d(
                                TAG_CAMERA_NEN,
                                "MediaPipe nen da san sang"
                            )
                        }

                        override fun khiCoKetQua(
                            result: FaceLandmarkerResult,
                            chieuRongAnh: Int,
                            chieuCaoAnh: Int
                        ) {

                            if (!cameraNenDangBat) {
                                return
                            }

                            val hienTai =
                                SystemClock.uptimeMillis()

                            val duLieuGoc =
                                trichXuatDuLieuKhuonMat
                                    .trichXuat(result)

                            val duLieu =
                                boChuanHoaDuLieuKhuonMat
                                    .chuanHoa(
                                        duLieuGoc
                                    )

                            capNhatTrangThaiKhuonMatOverlayNen(
                                true
                            )

                            nhanDienNghiengDau.capNhat(
                                roll = duLieu.roll,
                                yaw = duLieu.yaw,
                                pitch = duLieu.pitch,
                                thoiGianMs = hienTai
                            )

                            nhanDienHuongDau.capNhat(
                                roll = duLieu.roll,
                                yaw = duLieu.yaw,
                                pitch = duLieu.pitch,
                                thoiGianMs = hienTai
                            )

                            val cheDoHienTai =
                                boDinhTuyenCheDo
                                    .layCheDoHienTai()

                            // Mở giữ hoạt động ở mọi chế độ
                            nhanDienMoMieng.capNhat(
                                doMoMieng =
                                    duLieu.doMoMieng,
                                thoiGianMs =
                                    hienTai
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
                                        hienTai
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

                            nhanDienNhamHaiMat.capNhat(
                                doNhamMatTrai =
                                    duLieu.doNhamMatTrai,
                                doNhamMatPhai =
                                    duLieu.doNhamMatPhai,
                                thoiGianMs =
                                    hienTai,
                                thoiGianXacNhanMs =
                                    thoiGianXacNhanNhamMat
                            )


                            if (
                                hienTai -
                                thoiGianLogGanNhat >=
                                KHOANG_LOG_CAMERA_NEN_MS
                            ) {

                                thoiGianLogGanNhat =
                                    hienTai

                                Log.d(
                                    TAG_CAMERA_NEN,
                                    "Camera nen dang phat hien khuon mat"
                                )
                            }
                        }

                        override fun khiKhongThayKhuonMat() {

                            if (!cameraNenDangBat) {
                                return
                            }

                            val hienTai =
                                SystemClock.uptimeMillis()

                            nhanDienNghiengDau.datLai()

                            datLaiNhanDienMieng()

                            // Cho detector YAW/PITCH tự xử lý mất mặt ngắn
                            // thay vì reset ngay chỉ vì một frame rỗng.
                            nhanDienHuongDau.capNhat(
                                roll = null,
                                yaw = null,
                                pitch = null,
                                thoiGianMs = hienTai
                            )

                            datLaiNhanDienMat()

                            capNhatTrangThaiKhuonMatOverlayNen(
                                false
                            )

                            if (
                                hienTai -
                                thoiGianLogGanNhat >=
                                KHOANG_LOG_CAMERA_NEN_MS
                            ) {

                                thoiGianLogGanNhat =
                                    hienTai

                                Log.d(
                                    TAG_CAMERA_NEN,
                                    "Camera nen khong thay khuon mat"
                                )
                            }
                        }

                        override fun khiCoLoi(
                            thongBao: String
                        ) {

                            // Lỗi MediaPipe làm gián đoạn chuỗi frame.
                            // Hủy mọi cử chỉ đang giữ để không tính tiếp thời gian cũ.
                            if (::nhanDienNghiengDau.isInitialized) {
                                nhanDienNghiengDau.datLai()
                            }

                            if (::nhanDienHuongDau.isInitialized) {
                                nhanDienHuongDau.datLai()
                            }

                            datLaiNhanDienMieng()
                            datLaiNhanDienMat()

                            capNhatTrangThaiKhuonMatOverlayNen(
                                false
                            )

                            Log.e(
                                TAG_CAMERA_NEN,
                                "Loi MediaPipe nen: $thongBao"
                            )
                        }
                    }
            )

        phanTichKhungHinhKhuonMat =
            PhanTichKhungHinhKhuonMat(
                xuLyKhuonMat =
                    xuLyKhuonMat,

                laCameraTruoc =
                    true
            )
    }

    private fun capNhatTrangThaiKhuonMatOverlayNen(
        coKhuonMat: Boolean
    ) {
        if (dangThayKhuonMatNen == coKhuonMat) {
            return
        }

        dangThayKhuonMatNen =
            coKhuonMat

        DichVuTruyCapFaceAccess
            .capNhatKhuonMatTrangThaiOverlay(
                coKhuonMat
            )
    }


    private fun khoiTaoCameraNen() {

        quanLyCamera =
            QuanLyCamera(
                context = this,
                lifecycleOwner = this,
                previewView = null,
                boPhanTichKhungHinh =
                    phanTichKhungHinhKhuonMat
            )
    }


    private fun batCameraNen() {

        if (cameraNenDangBat) {

            Log.d(
                TAG_CAMERA_NEN,
                "Camera nen da bat san - bo qua"
            )

            return
        }

        if (cameraNenDangKhoiDong) {

            Log.d(
                TAG_CAMERA_NEN,
                "Camera nen dang khoi dong - bo qua lenh BAT lap"
            )

            return
        }

        cameraNenDangKhoiDong =
            true

        thoiGianLogGanNhat =
            0L

        dangThayKhuonMatNen =
            null

        quanLyCamera.batCamera(

            khiThanhCong = {

                cameraNenDangKhoiDong =
                    false

                cameraNenDangBat =
                    true

                nhanDienNghiengDau.datLai()
                nhanDienHuongDau.datLai()
                datLaiNhanDienMieng()
                datLaiNhanDienMat()

                Log.d(
                    TAG_CAMERA_NEN,
                    "Camera nen da bat thanh cong"
                )

                guiBroadcastCameraNenDaBat()
            },

            khiLoi = { exception ->

                cameraNenDangKhoiDong =
                    false

                cameraNenDangBat =
                    false

                capNhatTrangThaiKhuonMatOverlayNen(
                    false
                )

                Log.e(
                    TAG_CAMERA_NEN,
                    "Khong the bat Camera nen",
                    exception
                )

                if (dangChoXacNhanCameraNenDaTat) {
                    guiBroadcastCameraNenDaTatMotLan()
                }
            }
        )
    }


    private fun tatCameraNen() {

        val dangKhoiDong =
            cameraNenDangKhoiDong

        val dangBat =
            cameraNenDangBat

        cameraNenDangKhoiDong =
            false

        cameraNenDangBat =
            false

        nhanDienNghiengDau.datLai()
        nhanDienHuongDau.datLai()
        datLaiNhanDienMieng()
        datLaiNhanDienMat()

        // Hủy cả yêu cầu bind CameraX đang chờ nếu có.
        quanLyCamera.tatCamera()

        capNhatTrangThaiKhuonMatOverlayNen(
            false
        )

        Log.d(
            TAG_CAMERA_NEN,
            when {
                dangKhoiDong ->
                    "Da huy Camera nen dang khoi dong"

                dangBat ->
                    "Camera nen da tat"

                else ->
                    "Camera nen dang tat san"
            }
        )

        guiBroadcastCameraNenDaTatMotLan()
    }


    private fun guiBroadcastCameraNenDaBat() {

        val intent =
            Intent(
                HANH_DONG_CAMERA_NEN_DA_BAT
            ).apply {

                setPackage(
                    packageName
                )
            }

        sendBroadcast(
            intent
        )

        Log.d(
            TAG_BAN_GIAO_CAMERA,
            "Service xac nhan Camera nen DA BAT"
        )
    }

    private fun guiBroadcastCameraNenDaTatMotLan() {

        if (!dangChoXacNhanCameraNenDaTat) {

            Log.d(
                TAG_BAN_GIAO_CAMERA,
                "Bo qua ACK Camera nen DA TAT trung lap"
            )

            return
        }

        dangChoXacNhanCameraNenDaTat =
            false

        val intent =
            Intent(
                HANH_DONG_CAMERA_NEN_DA_TAT
            ).apply {

                setPackage(
                    packageName
                )
            }

        sendBroadcast(
            intent
        )

        Log.d(
            TAG_BAN_GIAO_CAMERA,
            "Service xac nhan Camera nen DA TAT"
        )
    }


    override fun onDestroy() {

        mainHandler.removeCallbacks(
            tacVuTaiLaiCauHinh
        )

        if (
            ::khoCauHinhNhanDienCuChi
                .isInitialized
        ) {
            khoCauHinhNhanDienCuChi
                .huyDangKyBoLangNgheThayDoi(
                    boLangNgheCauHinh
                )
        }

        DichVuTruyCapFaceAccess
            .tatConTro()

        DichVuTruyCapFaceAccess
            .tatTrangThaiOverlay()

        if (::nhanDienNghiengDau.isInitialized) {
            nhanDienNghiengDau.datLai()
        }

        datLaiNhanDienMieng()

        if (::nhanDienHuongDau.isInitialized) {
            nhanDienHuongDau.datLai()
        }

        if (::nhanDienNhamHaiMat.isInitialized) {
            datLaiNhanDienMat()
        }

        cameraNenDangBat =
            false

        cameraNenDangKhoiDong =
            false

        dangChoXacNhanCameraNenDaTat =
            false

        if (::quanLyCamera.isInitialized) {

            quanLyCamera.dong()
        }

        if (::xuLyKhuonMat.isInitialized) {

            xuLyKhuonMat.dong()
        }

        dichVuDangHoatDong =
            false

        Log.d(
            TAG,
            "Dich vu theo doi da dung"
        )

        lifecycleRegistry.currentState =
            Lifecycle.State.DESTROYED

        super.onDestroy()
    }


    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }


    private fun batForeground() {

        val thongBao =
            NotificationCompat.Builder(
                this,
                ID_KENH_THONG_BAO
            )
                .setSmallIcon(
                    R.mipmap.ic_launcher
                )
                .setContentTitle(
                    "FaceAccess đang hoạt động"
                )
                .setContentText(
                    "Đang theo dõi cử chỉ khuôn mặt"
                )
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .setOngoing(true)
                .build()

        ServiceCompat.startForeground(
            this,
            ID_THONG_BAO,
            thongBao,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
        )
    }


    private fun taoKenhThongBao() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val kenh =
                NotificationChannel(
                    ID_KENH_THONG_BAO,
                    TEN_KENH_THONG_BAO,
                    NotificationManager.IMPORTANCE_LOW
                )

            kenh.description =
                "Thông báo khi FaceAccess đang theo dõi cử chỉ"

            val notificationManager =
                getSystemService(
                    NotificationManager::class.java
                )

            notificationManager
                .createNotificationChannel(
                    kenh
                )
        }
    }


    companion object {

        @Volatile
        private var dichVuDangHoatDong =
            false

        fun dangTheoDoiHoatDong(): Boolean =
            dichVuDangHoatDong

        private const val TAG =
            "DichVuTheoDoi"

        private const val TAG_CAMERA_NEN =
            "CameraNenFaceAccess"

        private const val TAG_BAN_GIAO_CAMERA =
            "BanGiaoCamera"

        private const val TAG_CU_CHI_NEN =
            "CuChiNen"

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

        private const val TAG_CON_TRO =
            "FaceAccessCursor"

        private const val TAG_CAU_HINH =
            "CauHinhNen"

        const val HANH_DONG_BAT_CAMERA_NEN =
            "com.example.faceaccess.v2.BAT_CAMERA_NEN"

        const val HANH_DONG_TAT_CAMERA_NEN =
            "com.example.faceaccess.v2.TAT_CAMERA_NEN"

        const val HANH_DONG_CAMERA_NEN_DA_BAT =
            "com.example.faceaccess.v2.CAMERA_NEN_DA_BAT"

        const val HANH_DONG_CAMERA_NEN_DA_TAT =
            "com.example.faceaccess.v2.CAMERA_NEN_DA_TAT"

        private const val ID_KENH_THONG_BAO =
            "faceaccess_tracking"

        private const val TEN_KENH_THONG_BAO =
            "Theo dõi FaceAccess"

        private const val ID_THONG_BAO =
            1001

        private const val KHOANG_LOG_CAMERA_NEN_MS =
            1000L
    }
}
