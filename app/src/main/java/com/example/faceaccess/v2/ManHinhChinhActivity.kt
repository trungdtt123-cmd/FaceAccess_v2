package com.example.faceaccess.v2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.faceaccess.v2.camera.QuanLyCamera

class ManHinhChinhActivity : AppCompatActivity() {

    private lateinit var quanLyCamera: QuanLyCamera

    private lateinit var khungCamera: PreviewView
    private lateinit var txtTrangThaiCamera: TextView

    private lateinit var btnBatDauTheoDoi: Button
    private lateinit var txtTrangThaiHeThong: TextView

    private var cameraDangBat = false

    /**
     * Nhận kết quả khi Android hỏi quyền Camera.
     */
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        anhXaGiaoDien()
        khoiTaoCamera()
        ganSuKien()

        hienThiCameraDaDung(
            "CAMERA\nChưa khởi động"
        )
    }

    /**
     * Ánh xạ View XML sang Kotlin.
     */
    private fun anhXaGiaoDien() {

        khungCamera =
            findViewById(R.id.khungCamera)

        txtTrangThaiCamera =
            findViewById(R.id.txtTrangThaiCamera)

        btnBatDauTheoDoi =
            findViewById(R.id.btnBatDauTheoDoi)

        txtTrangThaiHeThong =
            findViewById(R.id.txtTrangThaiHeThong)
    }

    /**
     * Khởi tạo module Camera.
     *
     * Activity chỉ điều khiển UI.
     * Logic CameraX nằm trong QuanLyCamera.
     */
    private fun khoiTaoCamera() {

        quanLyCamera =
            QuanLyCamera(
                context = this,
                lifecycleOwner = this,
                previewView = khungCamera
            )
    }

    /**
     * Gắn sự kiện cho nút bắt đầu/dừng theo dõi.
     */
    private fun ganSuKien() {

        btnBatDauTheoDoi.setOnClickListener {

            if (cameraDangBat) {

                tatCamera()

            } else {

                kiemTraVaBatCamera()
            }
        }
    }

    /**
     * Kiểm tra quyền Camera trước khi bật.
     */
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

    /**
     * Bật camera và hiện PreviewView.
     */
    private fun batCamera() {

        // Ẩn placeholder.
        txtTrangThaiCamera.visibility = View.GONE

        // Hiện vùng camera.
        khungCamera.visibility = View.VISIBLE

        // Giao CameraX cho QuanLyCamera xử lý.
        quanLyCamera.batCamera()

        cameraDangBat = true

        txtTrangThaiHeThong.text =
            "● Camera đang hoạt động"

        btnBatDauTheoDoi.text =
            "DỪNG THEO DÕI"
    }

    /**
     * Dừng camera và trả giao diện về placeholder.
     */
    private fun tatCamera() {

        // Dừng CameraX.
        quanLyCamera.tatCamera()

        cameraDangBat = false

        // Không cho PreviewView giữ frame cuối.
        hienThiCameraDaDung(
            "CAMERA\nĐã dừng"
        )

        txtTrangThaiHeThong.text =
            "● Đã dừng theo dõi"

        btnBatDauTheoDoi.text =
            "BẮT ĐẦU THEO DÕI"
    }

    /**
     * Ẩn PreviewView và hiện placeholder.
     */
    private fun hienThiCameraDaDung(
        noiDung: String
    ) {

        khungCamera.visibility = View.GONE

        txtTrangThaiCamera.text = noiDung
        txtTrangThaiCamera.visibility = View.VISIBLE
    }
}