package com.example.faceaccess.v2.dieuphoi.hotro

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.faceaccess.v2.R
import java.util.Locale

/**
 * Màn hình quản lý liên hệ hỗ trợ.
 *
 * Checkpoint 1 đã có:
 * - danh sách không giới hạn cứng;
 * - thêm liên hệ;
 * - tìm theo tên / số điện thoại;
 * - xóa nhiều liên hệ cùng lúc;
 * - avatar chữ cái đầu khi chưa có ảnh.
 *
 * Đã nối:
 * - nhấn liên hệ -> mở màn hình chi tiết;
 * - cập nhật tên/số điện thoại/mô tả ở màn hình chi tiết.
 *
 * Đã nối thêm:
 * - ảnh đại diện thật được hiển thị ngay trong danh sách;
 * - nếu không có ảnh hoặc URI lỗi thì fallback về chữ cái đầu.
 *
 * Checkpoint sau:
 * - kết nối danh sách này với gesture HO_TRO.
 */
class DanhSachLienHeHoTroActivity :
    AppCompatActivity() {

    private lateinit var edtTimKiem:
            EditText

    private lateinit var btnThem:
            Button

    private lateinit var btnXoa:
            Button

    private lateinit var btnHuyXoa:
            Button

    private lateinit var btnQuayLai:
            Button

    private lateinit var khungDanhSach:
            LinearLayout

    private lateinit var txtTrangThaiRong:
            TextView

    private lateinit var txtTongLienHe:
            TextView

    private lateinit var khoLienHe:
            KhoLienHeHoTro


    private var danhSachGoc:
            List<NguoiHoTro> =
        emptyList()


    private var cheDoXoa =
        false


    private val idsDaChon =
        linkedSetOf<Long>()


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_danh_sach_lien_he_ho_tro
        )


        khoLienHe =
            KhoLienHeHoTro(
                applicationContext
            )


        anhXa()

        ganSuKien()
    }


    override fun onResume() {

        super.onResume()

        taiLaiDuLieu()
    }


    private fun anhXa() {

        edtTimKiem =
            findViewById(
                R.id.edtTimKiemLienHeHoTro
            )

        btnThem =
            findViewById(
                R.id.btnThemLienHeHoTro
            )

        btnXoa =
            findViewById(
                R.id.btnXoaLienHeHoTro
            )

        btnHuyXoa =
            findViewById(
                R.id.btnHuyCheDoXoaLienHe
            )

        btnQuayLai =
            findViewById(
                R.id.btnQuayLaiDanhSachHoTro
            )

        khungDanhSach =
            findViewById(
                R.id.khungDanhSachLienHeHoTro
            )

        txtTrangThaiRong =
            findViewById(
                R.id.txtDanhSachLienHeHoTroRong
            )

        txtTongLienHe =
            findViewById(
                R.id.txtTongLienHeHoTro
            )
    }


    private fun ganSuKien() {

        btnQuayLai.setOnClickListener {

            finish()
        }


        btnThem.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ThemLienHeHoTroActivity::class.java
                )
            )
        }


        btnXoa.setOnClickListener {

            if (!cheDoXoa) {

                batCheDoXoa()

            } else {

                xoaNhungLienHeDaChon()
            }
        }


        btnHuyXoa.setOnClickListener {

            tatCheDoXoa()
        }


        edtTimKiem.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit


                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    hienThiTheoTimKiem()
                }


                override fun afterTextChanged(
                    s: Editable?
                ) = Unit
            }
        )
    }


    private fun taiLaiDuLieu() {

        danhSachGoc =
            khoLienHe.layTatCa()


        idsDaChon.retainAll(
            danhSachGoc
                .map {
                    it.id
                }
                .toSet()
        )


        hienThiTheoTimKiem()
    }


    private fun hienThiTheoTimKiem() {

        val tuKhoa =
            edtTimKiem.text
                ?.toString()
                ?.trim()
                ?.lowercase(
                    Locale.getDefault()
                )
                .orEmpty()


        val ketQua =
            if (tuKhoa.isBlank()) {

                danhSachGoc

            } else {

                danhSachGoc.filter { nguoi ->

                    nguoi.ten
                        .lowercase(
                            Locale.getDefault()
                        )
                        .contains(tuKhoa) ||
                            nguoi.soDienThoai
                                .contains(tuKhoa)
                }
            }


        veDanhSach(
            ketQua
        )


        txtTongLienHe.text =
            if (tuKhoa.isBlank()) {

                "Tổng: ${danhSachGoc.size} liên hệ"

            } else {

                "Tìm thấy: ${ketQua.size}/${danhSachGoc.size} liên hệ"
            }
    }


    private fun veDanhSach(
        danhSach: List<NguoiHoTro>
    ) {

        khungDanhSach.removeAllViews()


        txtTrangThaiRong.visibility =
            if (danhSach.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }


        danhSach.forEach { nguoi ->

            khungDanhSach.addView(
                taoDongLienHe(
                    nguoi
                )
            )
        }
    }


    private fun taoDongLienHe(
        nguoi: NguoiHoTro
    ): View {

        val scale =
            resources.displayMetrics.density


        val row =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    (14 * scale).toInt(),
                    (12 * scale).toInt(),
                    (14 * scale).toInt(),
                    (12 * scale).toInt()
                )

                background =
                    ContextCompat.getDrawable(
                        this@DanhSachLienHeHoTroActivity,
                        R.drawable.nen_the
                    )

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {

                        bottomMargin =
                            (10 * scale).toInt()
                    }
            }


        val kichThuocAvatar =
            (52 * scale).toInt()


        val khungAvatar =
            FrameLayout(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        kichThuocAvatar,
                        kichThuocAvatar
                    )
            }


        val imgAvatar =
            ImageView(this).apply {

                layoutParams =
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                background =
                    ContextCompat.getDrawable(
                        this@DanhSachLienHeHoTroActivity,
                        R.drawable.nen_avatar_lien_he
                    )

                scaleType =
                    ImageView.ScaleType.CENTER_CROP

                clipToOutline =
                    true

                visibility =
                    View.GONE
            }


        val txtAvatar =
            TextView(this).apply {

                val chuDau =
                    nguoi.ten
                        .trim()
                        .firstOrNull()
                        ?.uppercaseChar()
                        ?.toString()
                        ?: "?"


                text =
                    chuDau

                gravity =
                    Gravity.CENTER

                textSize =
                    20f

                setTextColor(
                    ContextCompat.getColor(
                        this@DanhSachLienHeHoTroActivity,
                        android.R.color.white
                    )
                )

                background =
                    ContextCompat.getDrawable(
                        this@DanhSachLienHeHoTroActivity,
                        R.drawable.nen_avatar_lien_he
                    )

                layoutParams =
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
            }


        /**
         * Nếu liên hệ có ảnh thật thì ưu tiên ảnh.
         * Nếu URI hỏng/mất thì giữ avatar chữ cái đầu.
         */
        if (!nguoi.anhUri.isNullOrBlank()) {

            try {

                imgAvatar.setImageURI(
                    Uri.parse(
                        nguoi.anhUri
                    )
                )


                if (
                    imgAvatar.drawable !=
                    null
                ) {

                    imgAvatar.visibility =
                        View.VISIBLE

                    txtAvatar.visibility =
                        View.GONE
                }

            } catch (_: Exception) {

                imgAvatar.visibility =
                    View.GONE

                txtAvatar.visibility =
                    View.VISIBLE
            }
        }


        khungAvatar.addView(
            imgAvatar
        )

        khungAvatar.addView(
            txtAvatar
        )


        val thongTin =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    ).apply {

                        marginStart =
                            (14 * scale).toInt()
                    }
            }


        val txtTen =
            TextView(this).apply {

                text =
                    nguoi.ten

                textSize =
                    17f

                setTextColor(
                    ContextCompat.getColor(
                        this@DanhSachLienHeHoTroActivity,
                        R.color.chu_chinh
                    )
                )
            }


        val txtSo =
            TextView(this).apply {

                text =
                    nguoi.soDienThoai

                textSize =
                    14f

                setTextColor(
                    ContextCompat.getColor(
                        this@DanhSachLienHeHoTroActivity,
                        R.color.chu_phu
                    )
                )
            }


        thongTin.addView(
            txtTen
        )

        thongTin.addView(
            txtSo
        )


        val checkBox =
            CheckBox(this).apply {

                isChecked =
                    nguoi.id in idsDaChon

                visibility =
                    if (cheDoXoa) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                setOnCheckedChangeListener {
                        _,
                        duocChon ->

                    if (duocChon) {

                        idsDaChon.add(
                            nguoi.id
                        )

                    } else {

                        idsDaChon.remove(
                            nguoi.id
                        )
                    }


                    capNhatNutXoa()
                }
            }


        row.addView(
            khungAvatar
        )

        row.addView(
            thongTin
        )

        row.addView(
            checkBox
        )


        row.setOnClickListener {

            if (cheDoXoa) {

                checkBox.isChecked =
                    !checkBox.isChecked

            } else {

                startActivity(
                    Intent(
                        this,
                        ChiTietLienHeHoTroActivity::class.java
                    ).putExtra(
                        ChiTietLienHeHoTroActivity.EXTRA_NGUOI_HO_TRO_ID,
                        nguoi.id
                    )
                )
            }
        }


        return row
    }


    private fun batCheDoXoa() {

        cheDoXoa =
            true

        idsDaChon.clear()

        btnHuyXoa.visibility =
            View.VISIBLE

        btnThem.isEnabled =
            false

        capNhatNutXoa()

        hienThiTheoTimKiem()
    }


    private fun tatCheDoXoa() {

        cheDoXoa =
            false

        idsDaChon.clear()

        btnHuyXoa.visibility =
            View.GONE

        btnThem.isEnabled =
            true

        btnXoa.text =
            "XÓA LIÊN HỆ"

        hienThiTheoTimKiem()
    }


    private fun capNhatNutXoa() {

        btnXoa.text =
            "XÓA ĐÃ CHỌN (${idsDaChon.size})"
    }


    private fun xoaNhungLienHeDaChon() {

        if (idsDaChon.isEmpty()) {

            Toast.makeText(
                this,
                "Hãy chọn ít nhất một liên hệ",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        AlertDialog.Builder(this)
            .setTitle("Xóa liên hệ hỗ trợ")
            .setMessage(
                "Bạn có chắc muốn xóa ${idsDaChon.size} liên hệ đã chọn?"
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
                    idsDaChon
                )


                tatCheDoXoa()

                taiLaiDuLieu()
            }
            .show()
    }
}