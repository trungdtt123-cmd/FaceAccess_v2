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
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.example.faceaccess.v2.ai.hieuchinh.BoChuanHoaDuLieuKhuonMat
import com.example.faceaccess.v2.ai.hieuchinh.BoDieuKhienHieuChinh
import com.example.faceaccess.v2.ai.hieuchinh.BoHocNguongThichNghi
import com.example.faceaccess.v2.ai.hieuchinh.BoThuThapMauHieuChinh
import com.example.faceaccess.v2.ai.hieuchinh.BuocHieuChinh
import com.example.faceaccess.v2.ai.hieuchinh.TrangThaiHieuChinh
import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhNhanDienCuChi
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
    private var theoDoiDangHoatDong = false

    @Volatile
    private var dangChoCameraNenNhaQuyen = false

    @Volatile
    private var dangChoBatDichVuTruyCap = false

    private var daDangKyBoNhanBanGiaoCamera = false

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

                    Log.d(
                        TAG_CU_CHI_MIENG,
                        "APP: MO MIENG MOT LAN - BACK"
                    )

                    dieuPhoiCuChi.xuLy(
                        SuKienCuChi.MoMieng
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

    private lateinit var btnHieuChinh: Button

    private lateinit var txtTrangThaiHeThong: TextView

    private lateinit var khungTrangThaiHeThong: View

    private lateinit var noiDungChinh: View

    // GIAO DIỆN HIỆU CHỈNH

    private lateinit var khungHieuChinh: View

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

        startService(intent)

        Log.d(
            TAG_BAN_GIAO_CAMERA,
            "Activity da nha Camera -> yeu cau Service BAT Camera nen"
        )
    }

    private fun yeuCauTatCameraNenDeNhanLaiCamera() {

        if (dangChoCameraNenNhaQuyen) {
            return
        }

        dangChoCameraNenNhaQuyen =
            true

        val intent =
            Intent(
                this,
                DichVuTheoDoiFaceAccess::class.java
            ).apply {

                action =
                    DichVuTheoDoiFaceAccess
                        .HANH_DONG_TAT_CAMERA_NEN
            }

        startService(intent)

        Log.d(
            TAG_BAN_GIAO_CAMERA,
            "Activity yeu cau Service TAT Camera nen"
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

    // QUYỀN CAMERA

    private val yeuCauQuyenCamera =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { duocCapQuyen ->

            if (duocCapQuyen) {

                batCamera()

            } else {

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

    // ON CREATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        anhXaGiaoDien()

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

        btnHieuChinh =
            findViewById(R.id.btnHieuChinh)

        txtTrangThaiHeThong =
            findViewById(R.id.txtTrangThaiHeThong)

        khungTrangThaiHeThong =
            findViewById(R.id.khungTrangThaiHeThong)

        noiDungChinh =
            findViewById(R.id.noiDungChinh)

        // Hiệu chỉnh
        khungHieuChinh =
            findViewById(R.id.khungHieuChinh)

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

                            if (
                                cheDoHienTai ==
                                CheDoDieuKhien.CON_TRO
                            ) {
                                nhanDienMoMiengHaiLan.capNhat(
                                    doMoMieng =
                                        duLieu.doMoMieng,
                                    thoiGianMs =
                                        thoiGianHienTai
                                )
                            } else {
                                nhanDienMoMieng.capNhat(
                                    doMoMieng =
                                        duLieu.doMoMieng,
                                    thoiGianMs =
                                        thoiGianHienTai
                                )
                            }

                            val thoiGianXacNhanNhamMat =
                                when (cheDoHienTai) {
                                    CheDoDieuKhien.HO_TRO ->
                                        BoDieuKhienLienHeHoTro
                                            .THOI_GIAN_NHAM_XAC_NHAN_MS

                                    CheDoDieuKhien.DIEU_HUONG,
                                    CheDoDieuKhien.MEDIA,
                                    CheDoDieuKhien.CON_TRO ->
                                        NhanDienNhamHaiMat
                                            .THOI_GIAN_NHAM_XAC_NHAN_MS
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

            if (cameraDangBat) {

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

        btnHieuChinh.setOnClickListener {

            batDauHieuChinh()
        }

        btnHuyHieuChinh.setOnClickListener {

            huyHieuChinh()
        }
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

        khoCauHinhNhanDienCuChi
            .luuCauHinh(
                cauHinhMoi
            )

        cauHinhNhanDienCuChi =
            cauHinhMoi

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

            khungTrangThaiHeThong.visibility =
                View.GONE

            noiDungChinh.visibility =
                View.GONE

            khungHieuChinh.visibility =
                View.VISIBLE
        }
    }

    private fun anGiaoDienHieuChinh() {

        khungHieuChinh.visibility =
            View.GONE

        khungTrangThaiHeThong.visibility =
            View.VISIBLE

        noiDungChinh.visibility =
            View.VISIBLE
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

                btnBatDauTheoDoi.text =
                    "DỪNG THEO DÕI"

                btnBatDauTheoDoi.isEnabled =
                    true
            },

            khiLoi = { exception ->

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

                btnBatDauTheoDoi.text =
                    "BẮT ĐẦU THEO DÕI"

                btnBatDauTheoDoi.isEnabled =
                    true
            }
        )
    }

    // NHẬN LẠI CAMERA TỪ SERVICE

    private fun batLaiCameraActivitySauBanGiao() {

        if (
            !theoDoiDangHoatDong ||
            cameraDangBat
        ) {
            return
        }

        Log.d(
            TAG_BAN_GIAO_CAMERA,
            "Bat lai Camera tren Activity sau ban giao"
        )

        nhanDienNghiengDau.datLai()

        quanLyCamera.batCamera(

            khiThanhCong = {

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

                    btnBatDauTheoDoi.text =
                        "DỪNG THEO DÕI"

                    btnBatDauTheoDoi.isEnabled =
                        true
                }

                Log.d(
                    TAG_BAN_GIAO_CAMERA,
                    "Activity da nhan lai Camera thanh cong"
                )
            },

            khiLoi = { exception ->

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

        btnBatDauTheoDoi.text =
            "BẮT ĐẦU THEO DÕI"
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
        }
    }

    private fun datTatCaCardVeTrangThaiThuong() {

        val mauChuThuong =
            ContextCompat.getColor(
                this,
                R.color.chu_chinh
            )

        val danhSachCard =
            listOf(
                cardDieuHuong,
                cardMedia,
                cardHoTro,
                cardConTro
            )

        danhSachCard.forEach { card ->

            card.setBackgroundResource(
                R.drawable.nen_che_do_thuong
            )

            card.setTextColor(
                mauChuThuong
            )
        }
    }

    private fun danhDauCardDangChon(
        card: TextView
    ) {

        card.setBackgroundResource(
            R.drawable.nen_che_do_dang_chon
        )

        card.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.xanh_chinh
            )
        )
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
            cameraDangBat &&
            ::quanLyCamera.isInitialized
        ) {

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

        private const val ACTION_ACCESSIBILITY_DETAILS_SETTINGS =
            "android.settings.ACCESSIBILITY_DETAILS_SETTINGS"

        private const val SO_LAN_CHO_DICH_VU_TRUY_CAP =
            5

        private const val THOI_GIAN_CHO_DICH_VU_TRUY_CAP_MS =
            300L

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