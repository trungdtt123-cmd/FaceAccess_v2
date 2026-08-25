package com.example.faceaccess.v2

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.faceaccess.v2.camera.QuanLyCamera
import com.example.faceaccess.v2.cuchi.nghiengdau.HuongNghiengDau
import com.example.faceaccess.v2.cuchi.nghiengdau.NhanDienNghiengDau
import com.example.faceaccess.v2.dieuphoi.DieuPhoiCuChi
import com.example.faceaccess.v2.dieuphoi.LenhToanCuc
import com.example.faceaccess.v2.dieuphoi.SuKienCuChi
import com.example.faceaccess.v2.khuonmat.DuLieuKhuonMat
import com.example.faceaccess.v2.khuonmat.PhanTichKhungHinhKhuonMat
import com.example.faceaccess.v2.khuonmat.TrichXuatDuLieuKhuonMat
import com.example.faceaccess.v2.khuonmat.XuLyKhuonMat
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import java.util.Locale

class ManHinhChinhActivity : AppCompatActivity() {

    // =========================================================
    // CAMERA
    // =========================================================

    private lateinit var quanLyCamera: QuanLyCamera

    private lateinit var khungCamera: PreviewView

    private lateinit var txtTrangThaiCamera: TextView

    @Volatile
    private var cameraDangBat = false


    // =========================================================
    // MEDIAPIPE
    // =========================================================

    private lateinit var xuLyKhuonMat: XuLyKhuonMat

    private lateinit var phanTichKhungHinhKhuonMat:
            PhanTichKhungHinhKhuonMat

    private lateinit var trichXuatDuLieuKhuonMat:
            TrichXuatDuLieuKhuonMat


    // =========================================================
    // NHẬN DIỆN CỬ CHỈ
    // =========================================================

    private lateinit var nhanDienNghiengDau:
            NhanDienNghiengDau


    // =========================================================
    // ĐIỀU PHỐI
    // =========================================================

    private lateinit var dieuPhoiCuChi:
            DieuPhoiCuChi


    // =========================================================
    // GIAO DIỆN
    // =========================================================

    private lateinit var btnBatDauTheoDoi: Button

    private lateinit var txtTrangThaiHeThong: TextView

    private lateinit var txtRoll: TextView

    private lateinit var txtYaw: TextView

    private lateinit var txtPitch: TextView

    private lateinit var txtTrangThaiMat: TextView

    private lateinit var txtTrangThaiMieng: TextView


    // =========================================================
    // TRẠNG THÁI
    // =========================================================

    private var dangThayKhuonMat: Boolean? = null

    private var thoiGianCapNhatUiGanNhat = 0L


    // =========================================================
    // QUYỀN CAMERA
    // =========================================================

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


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        anhXaGiaoDien()

        khoiTaoTrichXuatDuLieu()

        /*
         * Điều phối phải được tạo trước detector.
         *
         * Vì detector sẽ gửi sự kiện vào DieuPhoiCuChi.
         */
        khoiTaoDieuPhoiCuChi()

        khoiTaoNhanDienNghiengDau()

        khoiTaoXuLyKhuonMat()

        khoiTaoCamera()

        ganSuKien()

        hienThiCameraDaDung(
            "CAMERA\nChưa khởi động"
        )

        datLaiThongTinNhanDien()
    }


    // =========================================================
    // ÁNH XẠ UI
    // =========================================================

    private fun anhXaGiaoDien() {

        khungCamera =
            findViewById(R.id.khungCamera)

        txtTrangThaiCamera =
            findViewById(R.id.txtTrangThaiCamera)

        btnBatDauTheoDoi =
            findViewById(R.id.btnBatDauTheoDoi)

        txtTrangThaiHeThong =
            findViewById(R.id.txtTrangThaiHeThong)

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


    // =========================================================
    // TRÍCH XUẤT DỮ LIỆU
    // =========================================================

    private fun khoiTaoTrichXuatDuLieu() {

        trichXuatDuLieuKhuonMat =
            TrichXuatDuLieuKhuonMat()
    }


    // =========================================================
    // ĐIỀU PHỐI CỬ CHỈ
    // =========================================================

    private fun khoiTaoDieuPhoiCuChi() {

        dieuPhoiCuChi =
            DieuPhoiCuChi { lenh ->

                when (lenh) {

                    LenhToanCuc.HOME -> {

                        /*
                         * CHƯA thực hiện HOME thật.
                         *
                         * Chỉ log để test kiến trúc.
                         */
                        Log.d(
                            TAG_LENH,
                            "LENH HOME"
                        )
                    }

                    LenhToanCuc.DOI_CHE_DO -> {

                        /*
                         * CHƯA đổi mode thật.
                         */
                        Log.d(
                            TAG_LENH,
                            "LENH DOI_CHE_DO"
                        )
                    }
                }
            }
    }


    // =========================================================
    // DETECTOR ROLL
    // =========================================================

    private fun khoiTaoNhanDienNghiengDau() {

        nhanDienNghiengDau =
            NhanDienNghiengDau { huong ->

                /*
                 * Detector chỉ báo người dùng
                 * vừa nghiêng về hướng nào.
                 *
                 * Detector không biết HOME,
                 * không biết mode.
                 */
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


    // =========================================================
    // MEDIAPIPE
    // =========================================================

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

                            capNhatTrangThaiKhuonMat(
                                coKhuonMat = true
                            )

                            capNhatDuLieuKhuonMat(
                                duLieu
                            )

                            /*
                             * MediaPipe
                             * ↓
                             * dữ liệu thô
                             * ↓
                             * detector ROLL
                             */
                            nhanDienNghiengDau.capNhat(
                                roll = duLieu.roll,
                                yaw = duLieu.yaw,
                                pitch = duLieu.pitch,
                                thoiGianMs =
                                    SystemClock.uptimeMillis()
                            )
                        }


                        override fun khiKhongThayKhuonMat() {

                            if (!cameraDangBat) {
                                return
                            }

                            /*
                             * Tracking mất thì detector
                             * phải trở về trạng thái sạch.
                             */
                            nhanDienNghiengDau.datLai()

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


    // =========================================================
    // CAMERA
    // =========================================================

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


    // =========================================================
    // UI EVENT
    // =========================================================

    private fun ganSuKien() {

        btnBatDauTheoDoi.setOnClickListener {

            if (cameraDangBat) {

                tatCamera()

            } else {

                kiemTraVaBatCamera()
            }
        }
    }


    // =========================================================
    // QUYỀN CAMERA
    // =========================================================

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


    // =========================================================
    // BẬT CAMERA
    // =========================================================

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

        quanLyCamera.batCamera(

            khiThanhCong = {

                cameraDangBat =
                    true

                dangThayKhuonMat =
                    null

                thoiGianCapNhatUiGanNhat =
                    0L

                nhanDienNghiengDau.datLai()

                txtTrangThaiHeThong.text =
                    "● Camera đang hoạt động - đang tìm khuôn mặt"

                btnBatDauTheoDoi.text =
                    "DỪNG THEO DÕI"

                btnBatDauTheoDoi.isEnabled =
                    true
            },


            khiLoi = { exception ->

                cameraDangBat =
                    false

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


    // =========================================================
    // DỪNG CAMERA
    // =========================================================

    private fun tatCamera() {

        /*
         * Khóa xử lý callback đến muộn trước.
         */
        cameraDangBat =
            false

        nhanDienNghiengDau.datLai()

        quanLyCamera.tatCamera()

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


    // =========================================================
    // TRẠNG THÁI KHUÔN MẶT
    // =========================================================

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


    // =========================================================
    // DỮ LIỆU REALTIME
    // =========================================================

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


    // =========================================================
    // FORMAT
    // =========================================================

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


    // =========================================================
    // RESET UI
    // =========================================================

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


    // =========================================================
    // CAMERA PLACEHOLDER
    // =========================================================

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


    // =========================================================
    // DESTROY
    // =========================================================

    override fun onDestroy() {

        if (
            ::nhanDienNghiengDau.isInitialized
        ) {
            nhanDienNghiengDau.datLai()
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

        super.onDestroy()
    }


    companion object {

        private const val KHOANG_CAP_NHAT_UI_MS =
            100L

        private const val TAG_LENH =
            "LenhToanCuc"

        private const val TAG_MEDIAPIPE =
            "FaceAccessMediaPipe"
    }
}