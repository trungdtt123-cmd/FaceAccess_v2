package com.example.faceaccess.v2

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.example.faceaccess.v2.camera.QuanLyCamera
import com.example.faceaccess.v2.chedo.BoDinhTuyenCheDo
import com.example.faceaccess.v2.chedo.CheDoDieuKhien
import com.example.faceaccess.v2.cuchi.nghiengdau.HuongNghiengDau
import com.example.faceaccess.v2.cuchi.nghiengdau.NhanDienNghiengDau
import com.example.faceaccess.v2.cuchi.huongdau.HuongDau
import com.example.faceaccess.v2.cuchi.huongdau.NhanDienHuongDau
import com.example.faceaccess.v2.cuchi.mieng.NhanDienMoMieng
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

    private var daDangKyBoNhanBanGiaoCamera = false

    // DETECTOR MỞ MIỆNG

    private fun khoiTaoNhanDienMoMieng() {

        nhanDienMoMieng =
            NhanDienMoMieng {

                Log.d(
                    TAG_CU_CHI_MIENG,
                    "APP: MO MIENG"
                )

                dieuPhoiCuChi.xuLy(
                    SuKienCuChi.MoMieng
                )
            }
    }

    // DETECTOR HƯỚNG ĐẦU YAW / PITCH

    private fun khoiTaoNhanDienHuongDau() {

        nhanDienHuongDau =
            NhanDienHuongDau { huong ->

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

    // MEDIAPIPE

    private lateinit var xuLyKhuonMat: XuLyKhuonMat

    private lateinit var phanTichKhungHinhKhuonMat:
            PhanTichKhungHinhKhuonMat

    private lateinit var trichXuatDuLieuKhuonMat:
            TrichXuatDuLieuKhuonMat

    // NHẬN DIỆN CỬ CHỈ

    private lateinit var nhanDienNghiengDau:
            NhanDienNghiengDau

    private lateinit var nhanDienMoMieng:
            NhanDienMoMieng

    private lateinit var nhanDienHuongDau:
            NhanDienHuongDau

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

    private lateinit var txtTrangThaiHeThong: TextView

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

                txtTrangThaiHeThong.text =
                    "● Chưa được cấp quyền Camera"
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

        khoiTaoBoDinhTuyenCheDo()

        khoiTaoBoDieuKhienMedia()

        khoiTaoBoDieuKhienLienHeHoTro()

        khoiTaoDieuPhoiCuChi()

        khoiTaoNhanDienNghiengDau()

        khoiTaoNhanDienMoMieng()

        khoiTaoNhanDienHuongDau()

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

        txtTrangThaiHeThong =
            findViewById(R.id.txtTrangThaiHeThong)

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

    // HỆ THỐNG CHẾ ĐỘ

    private fun khoiTaoBoDinhTuyenCheDo() {

        boDinhTuyenCheDo =
            BoDinhTuyenCheDo { cheDoMoi ->

                Log.d(
                    TAG_CHE_DO,
                    "Che do moi: $cheDoMoi"
                )

                if (
                    cheDoMoi !=
                    CheDoDieuKhien.HO_TRO &&
                    ::boDieuKhienLienHeHoTro.isInitialized
                ) {

                    boDieuKhienLienHeHoTro
                        .datLaiPhien()
                }

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

                                    txtTrangThaiHeThong.text =
                                        "● Hãy bật dịch vụ trợ năng FaceAccess"
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

                                    txtTrangThaiHeThong.text =
                                        "● Hãy bật dịch vụ trợ năng FaceAccess"
                                }
                            }
                        }
                    }
                }
            )
    }

    // DETECTOR ROLL

    private fun khoiTaoNhanDienNghiengDau() {

        nhanDienNghiengDau =
            NhanDienNghiengDau { huong ->

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

                            val duLieu =
                                trichXuatDuLieuKhuonMat
                                    .trichXuat(result)

                            // KHUÔN MẶT

                            capNhatTrangThaiKhuonMat(
                                coKhuonMat = true
                            )

                            // DEBUG REALTIME

                            capNhatDuLieuKhuonMat(
                                duLieu
                            )

                            // DETECTOR ROLL

                            val thoiGianHienTai =
                                SystemClock.uptimeMillis()

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

                            nhanDienMoMieng.capNhat(
                                doMoMieng =
                                    duLieu.doMoMieng,
                                thoiGianMs =
                                    thoiGianHienTai
                            )
                        }

                        override fun khiKhongThayKhuonMat() {

                            if (!cameraDangBat) {
                                return
                            }

                            nhanDienNghiengDau.datLai()

                            nhanDienMoMieng.datLai()

                            nhanDienHuongDau.datLai()

                            capNhatTrangThaiKhuonMat(
                                coKhuonMat = false
                            )

                            datLaiThongTinNhanDien()
                        }

                        override fun khiCoLoi(
                            thongBao: String
                        ) {

                            Log.e(
                                TAG_MEDIAPIPE,
                                thongBao
                            )

                            runOnUiThread {

                                if (cameraDangBat) {

                                    txtTrangThaiHeThong.text =
                                        "● Lỗi MediaPipe: $thongBao"
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

    // SỰ KIỆN UI

    private fun ganSuKien() {

        btnBatDauTheoDoi.setOnClickListener {

            if (cameraDangBat) {

                tatCamera()

            } else {

                kiemTraVaBatCamera()
            }
        }

        cardHoTro.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    DanhSachLienHeHoTroActivity::class.java
                )
            )
        }
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

        txtTrangThaiHeThong.text =
            "● Đang khởi động Camera..."

        btnBatDauTheoDoi.isEnabled =
            false

        nhanDienNghiengDau.datLai()

        nhanDienMoMieng.datLai()

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

                nhanDienMoMieng.datLai()

                txtTrangThaiHeThong.text =
                    "● Camera đang hoạt động - đang tìm khuôn mặt"

                btnBatDauTheoDoi.text =
                    "DỪNG THEO DÕI"

                btnBatDauTheoDoi.isEnabled =
                    true
            },

            khiLoi = { exception ->

                tatDichVuTheoDoi()

                cameraDangBat =
                    false

                theoDoiDangHoatDong =
                    false

                DichVuTruyCapFaceAccess
                    .tatConTro()

                dangThayKhuonMat =
                    null

                nhanDienNghiengDau.datLai()

                hienThiCameraDaDung(
                    "CAMERA\nKhông thể khởi động"
                )

                datLaiThongTinNhanDien()

                txtTrangThaiHeThong.text =
                    "● Lỗi Camera: ${
                        exception.message
                            ?: "Không xác định"
                    }"

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

                runOnUiThread {

                    khungCamera.visibility =
                        View.VISIBLE

                    txtTrangThaiCamera.visibility =
                        View.GONE

                    txtTrangThaiHeThong.text =
                        "● Camera đang hoạt động - đang tìm khuôn mặt"

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

                    txtTrangThaiHeThong.text =
                        "● Không thể nhận lại Camera: ${
                            exception.message
                                ?: "Không xác định"
                        }"
                }
            }
        )
    }

    // DỪNG CAMERA

    private fun tatCamera() {

        theoDoiDangHoatDong =
            false

        DichVuTruyCapFaceAccess
            .tatConTro()

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

        nhanDienMoMieng.datLai()

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

        txtTrangThaiHeThong.text =
            "● Đã dừng theo dõi"

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

        runOnUiThread {

            if (!cameraDangBat) {
                return@runOnUiThread
            }

            if (coKhuonMat) {

                txtTrangThaiHeThong.text =
                    "● Đã phát hiện khuôn mặt"

            } else {

                txtTrangThaiHeThong.text =
                    "● Không thấy khuôn mặt"
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

        if (
            theoDoiDangHoatDong &&
            cameraDangBat &&
            ::quanLyCamera.isInitialized
        ) {

            cameraDangBat =
                false

            nhanDienNghiengDau.datLai()

            nhanDienMoMieng.datLai()

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

            nhanDienMoMieng.datLai()
        }

        if (
            ::nhanDienHuongDau.isInitialized
        ) {

            nhanDienHuongDau.datLai()
        }

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
