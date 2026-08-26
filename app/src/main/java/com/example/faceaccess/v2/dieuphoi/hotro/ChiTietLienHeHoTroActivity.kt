package com.example.faceaccess.v2.dieuphoi.hotro

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.faceaccess.v2.R

/**
 * Màn hình chi tiết một liên hệ hỗ trợ.
 *
 * Checkpoint hiện tại:
 * - avatar chữ cái đầu ở chính giữa phía trên;
 * - xem/sửa tên;
 * - xem/sửa số điện thoại;
 * - xem/sửa mô tả;
 * - cập nhật;
 * - xóa riêng liên hệ.
 *
 * Ảnh đại diện thật sẽ được nối sau mà không cần đổi model.
 */
class ChiTietLienHeHoTroActivity :
    AppCompatActivity() {

    private lateinit var txtAvatar:
            TextView

    private lateinit var edtTen:
            EditText

    private lateinit var edtSoDienThoai:
            EditText

    private lateinit var edtMoTa:
            EditText

    private lateinit var btnCapNhat:
            Button

    private lateinit var btnXoa:
            Button

    private lateinit var btnQuayLai:
            Button

    private lateinit var khoLienHe:
            KhoLienHeHoTro


    private var lienHeId:
            Long =
        ID_KHONG_HOP_LE


    private var lienHeHienTai:
            NguoiHoTro? =
        null


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_chi_tiet_lien_he_ho_tro
        )


        lienHeId =
            intent.getLongExtra(
                EXTRA_NGUOI_HO_TRO_ID,
                ID_KHONG_HOP_LE
            )


        if (
            lienHeId ==
            ID_KHONG_HOP_LE
        ) {

            finish()

            return
        }


        khoLienHe =
            KhoLienHeHoTro(
                applicationContext
            )


        anhXa()

        ganSuKien()

        taiLienHe()
    }


    private fun anhXa() {

        txtAvatar =
            findViewById(
                R.id.txtAvatarChiTietLienHeHoTro
            )

        edtTen =
            findViewById(
                R.id.edtTenChiTietLienHeHoTro
            )

        edtSoDienThoai =
            findViewById(
                R.id.edtSoDienThoaiChiTietLienHeHoTro
            )

        edtMoTa =
            findViewById(
                R.id.edtMoTaChiTietLienHeHoTro
            )

        btnCapNhat =
            findViewById(
                R.id.btnCapNhatLienHeHoTro
            )

        btnXoa =
            findViewById(
                R.id.btnXoaMotLienHeHoTro
            )

        btnQuayLai =
            findViewById(
                R.id.btnQuayLaiChiTietLienHe
            )
    }


    private fun ganSuKien() {

        btnQuayLai.setOnClickListener {

            finish()
        }


        btnCapNhat.setOnClickListener {

            capNhatLienHe()
        }


        btnXoa.setOnClickListener {

            xacNhanXoaLienHe()
        }
    }


    private fun taiLienHe() {

        val lienHe =
            khoLienHe.layTheoId(
                lienHeId
            )


        if (lienHe == null) {

            Toast.makeText(
                this,
                "Liên hệ không còn tồn tại",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        lienHeHienTai =
            lienHe


        edtTen.setText(
            lienHe.ten
        )

        edtSoDienThoai.setText(
            lienHe.soDienThoai
        )

        edtMoTa.setText(
            lienHe.moTa
        )


        capNhatAvatar(
            lienHe.ten
        )
    }


    private fun capNhatLienHe() {

        val hienTai =
            lienHeHienTai
                ?: return


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


        val lienHeMoi =
            hienTai.copy(
                ten =
                    ten,
                soDienThoai =
                    soDienThoai,
                moTa =
                    moTa
            )


        val thanhCong =
            khoLienHe.capNhat(
                lienHeMoi
            )


        if (!thanhCong) {

            Toast.makeText(
                this,
                "Không thể cập nhật liên hệ",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        lienHeHienTai =
            lienHeMoi


        capNhatAvatar(
            ten
        )


        Toast.makeText(
            this,
            "Đã cập nhật liên hệ",
            Toast.LENGTH_SHORT
        ).show()


        /*
         * Quay lại DanhSachLienHeHoTroActivity.
         *
         * DanhSachLienHeHoTroActivity đã reload dữ liệu trong onResume(),
         * nên khi màn hình chi tiết finish(), danh sách sẽ hiển thị dữ liệu mới.
         */
        finish()
    }


    private fun xacNhanXoaLienHe() {

        val lienHe =
            lienHeHienTai
                ?: return


        AlertDialog.Builder(this)
            .setTitle(
                "Xóa liên hệ hỗ trợ"
            )
            .setMessage(
                "Bạn có chắc muốn xóa ${lienHe.ten}?"
            )
            .setNegativeButton(
                "Hủy",
                null
            )
            .setPositiveButton(
                "Xóa"
            ) {
                    _,
                    _ ->

                khoLienHe.xoaTheoIds(
                    setOf(
                        lienHe.id
                    )
                )


                Toast.makeText(
                    this,
                    "Đã xóa ${lienHe.ten}",
                    Toast.LENGTH_SHORT
                ).show()


                finish()
            }
            .show()
    }


    private fun capNhatAvatar(
        ten: String
    ) {

        val chuDau =
            ten
                .trim()
                .firstOrNull()
                ?.uppercaseChar()
                ?.toString()
                ?: "?"


        txtAvatar.text =
            chuDau
    }


    private fun soDienThoaiHopLe(
        giaTri: String
    ): Boolean {

        val daChuanHoa =
            giaTri
                .replace(
                    " ",
                    ""
                )
                .replace(
                    "-",
                    ""
                )


        return daChuanHoa.matches(
            Regex(
                """^\+?\d{9,15}$"""
            )
        )
    }


    companion object {

        const val EXTRA_NGUOI_HO_TRO_ID =
            "extra_nguoi_ho_tro_id"

        private const val ID_KHONG_HOP_LE =
            -1L
    }
}
