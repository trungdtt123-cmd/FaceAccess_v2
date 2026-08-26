package com.example.faceaccess.v2.dieuphoi.hotro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


        khoLienHe =
            KhoLienHeHoTro(
                applicationContext
            )


        anhXa()

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
