// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.dieuphoi.hotro

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.faceaccess.v2.R

/**
 * Thêm liên hệ hỗ trợ:
 * - tên;
 * - số điện thoại;
 * - mô tả;
 * - lưu cục bộ.
 *
 * Ảnh đại diện vẫn tự lấy chữ cái đầu của tên khi chưa có ảnh thật.
 */
class ThemLienHeHoTroActivity :
    AppCompatActivity() {

    private lateinit var edtTen:
            EditText

    private lateinit var edtSoDienThoai:
            EditText

    private lateinit var edtMoTa:
            EditText

    private lateinit var btnLuuLienHe:
            Button

    private lateinit var btnQuayLai:
            Button

    private lateinit var khoLienHe:
            KhoLienHeHoTro


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_them_lien_he_ho_tro
        )

        cauHinhThanhHeThong()


        khoLienHe =
            KhoLienHeHoTro(
                applicationContext
            )


        anhXa()

        apDungPhongCachNut()

        ganSuKien()
    }


    private fun anhXa() {

        edtTen =
            findViewById(
                R.id.edtTenLienHeHoTro
            )

        edtSoDienThoai =
            findViewById(
                R.id.edtSoDienThoaiLienHeHoTro
            )

        edtMoTa =
            findViewById(
                R.id.edtMoTaLienHeHoTro
            )

        btnLuuLienHe =
            findViewById(
                R.id.btnLuuLienHeHoTro
            )

        btnQuayLai =
            findViewById(
                R.id.btnQuayLaiThemLienHe
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


    private fun apDungPhongCachNut() {

        btnLuuLienHe.backgroundTintList =
            null

        btnQuayLai.backgroundTintList =
            null

        btnQuayLai.background =
            null

        btnLuuLienHe.isAllCaps =
            false

        btnQuayLai.isAllCaps =
            false

        ganHieuUngNhan(
            btnLuuLienHe
        )
    }


    private fun ganHieuUngNhan(
        view: View
    ) {

        view.setOnTouchListener {
                v,
                event ->

            when (
                event.actionMasked
            ) {

                MotionEvent.ACTION_DOWN -> {

                    v.animate()
                        .scaleX(0.96f)
                        .scaleY(0.96f)
                        .setDuration(80L)
                        .start()
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {

                    v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(110L)
                        .start()
                }
            }

            false
        }
    }


    private fun ganSuKien() {

        btnQuayLai.setOnClickListener {

            finish()
        }


        btnLuuLienHe.setOnClickListener {

            luuLienHe()
        }
    }


    private fun luuLienHe() {

        val ten =
            edtTen.text
                ?.toString()
                ?.trim()
                .orEmpty()


        val soDienThoai =
            edtSoDienThoai.text
                ?.toString()
                ?.trim()
                .orEmpty()


        val moTa =
            edtMoTa.text
                ?.toString()
                ?.trim()
                .orEmpty()


        if (ten.isBlank()) {

            edtTen.error =
                "Vui lòng nhập tên liên hệ"

            edtTen.requestFocus()

            return
        }


        if (!soDienThoaiHopLe(soDienThoai)) {

            edtSoDienThoai.error =
                "Số điện thoại phải có từ 9 đến 15 chữ số"

            edtSoDienThoai.requestFocus()

            return
        }


        khoLienHe.them(
            ten = ten,
            soDienThoai = soDienThoai,
            moTa = moTa
        )


        Toast.makeText(
            this,
            "Đã thêm $ten",
            Toast.LENGTH_SHORT
        ).show()


        finish()
    }


    private fun soDienThoaiHopLe(
        giaTri: String
    ): Boolean {

        val daChuanHoa =
            giaTri
                .replace(" ", "")
                .replace("-", "")


        return daChuanHoa.matches(
            Regex("""^\+?\d{9,15}$""")
        )
    }
}
