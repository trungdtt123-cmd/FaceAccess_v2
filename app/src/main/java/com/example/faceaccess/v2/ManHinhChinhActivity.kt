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

    /**
     * Chỉ cập nhật TextView khoảng 10 lần/giây.
     *
     * MediaPipe vẫn xử lý frame với tốc độ bình thường.
     * Chỉ UI được giảm tần suất cập nhật.
     */
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
    // ÁNH XẠ GIAO DIỆN
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
    // DETECTOR NGHIÊNG ĐẦU
    // =========================================================

    private fun khoiTaoNhanDienNghiengDau() {

        nhanDienNghiengDau =
            NhanDienNghiengDau { huong ->

                when (huong) {

                    HuongNghiengDau.TRAI -> {

                        Log.d(
                            TAG_CU_CHI,
                            "NGHIENG TRAI"
                        )
                    }

                    HuongNghiengDau.PHAI -> {

                        Log.d(
                            TAG_CU_CHI,
                            "NGHIENG PHAI"
                        )
                    }
                }
            }
    }


    // =========================================================
    // MEDIAPIPE FACE LANDMARKER
    // =========================================================

    private fun khoiTaoXuLyKhuonMat() {

        xuLyKhuonMat =
            XuLyKhuonMat(
                context = this,
                langNghe =
                    object :
                        XuLyKhuonMat.LangNgheXuLyKhuonMat {

                        /**
                         * MediaPipe load model thành công.
                         *
                         * Không thay đổi trạng thái Camera tại đây.
                         */
                        override fun khiKhoiTaoThanhCong() {

                            Log.d(
                                TAG_MEDIAPIPE,
                                "Face Landmarker da san sang"
                            )
                        }


                        /**
                         * Có kết quả nhận diện khuôn mặt.
                         */
                        override fun khiCoKetQua(
                            result: FaceLandmarkerResult,
                            chieuRongAnh: Int,
                            chieuCaoAnh: Int
                        ) {

                            /*
                             * Có thể còn một callback cũ
                             * sau khi Camera vừa dừng.
                             */
                            if (!cameraDangBat) {
                                return
                            }

                            val duLieu =
                                trichXuatDuLieuKhuonMat
                                    .trichXuat(result)

                            /*
                             * Cập nhật trạng thái:
                             *
                             * Có khuôn mặt.
                             */
                            capNhatTrangThaiKhuonMat(
                                coKhuonMat = true
                            )

                            /*
                             * Hiển thị dữ liệu thô:
                             *
                             * ROLL
                             * YAW
                             * PITCH
                             * MẮT
                             * MIỆNG
                             */
                            capNhatDuLieuKhuonMat(
                                duLieu
                            )

                            /*
                             * Đưa dữ liệu sang detector ROLL.
                             *
                             * Detector hiện tại CHỈ nhận diện:
                             *
                             * TRAI
                             * PHAI
                             *
                             * Chưa HOME.
                             * Chưa đổi chế độ.
                             */
                            nhanDienNghiengDau.capNhat(
                                roll = duLieu.roll,
                                yaw = duLieu.yaw,
                                pitch = duLieu.pitch,
                                thoiGianMs =
                                    SystemClock.uptimeMillis()
                            )
                        }


                        /**
                         * MediaPipe không thấy khuôn mặt.
                         */
                        override fun khiKhongThayKhuonMat() {

                            if (!cameraDangBat) {
                                return
                            }

                            /*
                             * Mất tracking:
                             *
                             * detector phải reset.
                             *
                             * Không được giữ trạng thái
                             * nghiêng đầu cũ.
                             */
                            nhanDienNghiengDau.datLai()

                            capNhatTrangThaiKhuonMat(
                                coKhuonMat = false
                            )

                            datLaiThongTinNhanDien()
                        }


                        /**
                         * MediaPipe gặp lỗi runtime.
                         */
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


        /*
         * Chuyển frame CameraX
         * thành MPImage cho MediaPipe.
         */
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
    // SỰ KIỆN GIAO DIỆN
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

        /*
         * Mỗi session Camera mới
         * phải bắt đầu detector từ trạng thái sạch.
         */
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
         * Đánh dấu dừng trước để callback MediaPipe
         * đến muộn không tiếp tục xử lý gesture.
         */
        cameraDangBat =
            false

        /*
         * Reset detector.
         */
        nhanDienNghiengDau.datLai()

        /*
         * Dừng CameraX + ImageAnalysis.
         */
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
    // HIỂN THỊ DỮ LIỆU THÔ
    // =========================================================

    private fun capNhatDuLieuKhuonMat(
        duLieu: DuLieuKhuonMat
    ) {

        if (!cameraDangBat) {
            return
        }

        val thoiGianHienTai =
            SystemClock.uptimeMillis()

        /*
         * UI chỉ update tối đa khoảng 10Hz.
         */
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
    // FORMAT GÓC
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


    // =========================================================
    // FORMAT BLENDSHAPE
    // =========================================================

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
    // RESET THÔNG TIN NHẬN DIỆN
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
    // PLACEHOLDER CAMERA
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
    // GIẢI PHÓNG TÀI NGUYÊN
    // =========================================================

    override fun onDestroy() {

        /*
         * Ngăn detector giữ state khi Activity bị hủy.
         */
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


    // =========================================================
    // CONSTANT
    // =========================================================

    companion object {

        /**
         * Cập nhật TextView tối đa khoảng 10 lần/giây.
         */
        private const val KHOANG_CAP_NHAT_UI_MS =
            100L

        /**
         * Logcat cho detector ROLL.
         */
        private const val TAG_CU_CHI =
            "CuChiNghiengDau"

        /**
         * Logcat MediaPipe.
         */
        private const val TAG_MEDIAPIPE =
            "FaceAccessMediaPipe"
    }
}