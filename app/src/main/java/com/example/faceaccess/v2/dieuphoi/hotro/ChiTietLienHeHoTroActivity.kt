package com.example.faceaccess.v2.dieuphoi.hotro

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.faceaccess.v2.R
import com.yalantis.ucrop.UCrop
import java.io.File

/**
 * Màn hình chi tiết một liên hệ hỗ trợ.
 *
 * Ảnh đại diện:
 * - chưa có ảnh -> dùng chữ cái đầu;
 * - bấm CHỌN ẢNH -> hiện giải thích quyền truy cập;
 * - Android mở trình chọn ảnh hệ thống;
 * - sau khi chọn -> uCrop cho phép kéo ảnh, phóng to/thu nhỏ
 *   trong khung tròn;
 * - chỉ khi bấm CẬP NHẬT thì URI ảnh mới được lưu vào liên hệ.
 *
 * Nút GỌI mở trình quay số Android bằng ACTION_DIAL.
 */
class ChiTietLienHeHoTroActivity :
    AppCompatActivity() {

    private lateinit var imgAvatar:
            ImageView

    private lateinit var txtAvatar:
            TextView

    private lateinit var edtTen:
            EditText

    private lateinit var edtSoDienThoai:
            EditText

    private lateinit var edtMoTa:
            EditText

    private lateinit var btnChonAnh:
            Button

    private lateinit var btnGoi:
            Button

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


    /**
     * URI ảnh đang hiển thị/chờ lưu.
     */
    private var anhUriDangChon:
            String? =
        null


    /**
     * Ảnh mới được uCrop tạo trong filesDir.
     * Nếu người dùng rời màn hình mà chưa bấm CẬP NHẬT,
     * file tạm mới này sẽ được xóa để tránh rác.
     */
    private var fileAnhMoiChuaLuu:
            File? =
        null


    private var daLuuThayDoi:
            Boolean =
        false


    /**
     * Android system document picker.
     *
     * Ta không xin quyền đọc toàn bộ thư viện.
     * Người dùng chủ động chọn đúng ảnh mà FaceAccess được phép đọc.
     */
    private val boChonAnh =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uriNguon ->

            if (uriNguon == null) {
                return@registerForActivityResult
            }


            try {

                contentResolver.takePersistableUriPermission(
                    uriNguon,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

            } catch (_: SecurityException) {

                // Không phải provider nào cũng cấp persistable permission.
            }


            moManHinhCanChinhAnh(
                uriNguon
            )
        }


    /**
     * Nhận kết quả từ uCrop.
     */
    private val boCanChinhAnh =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { ketQua ->

            if (
                ketQua.resultCode ==
                RESULT_OK
            ) {

                val uriKetQua =
                    ketQua.data
                        ?.let {
                            UCrop.getOutput(it)
                        }


                if (uriKetQua == null) {

                    Toast.makeText(
                        this,
                        "Không nhận được ảnh sau khi căn chỉnh",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@registerForActivityResult
                }


                anhUriDangChon =
                    uriKetQua.toString()


                hienThiAvatar(
                    ten =
                        edtTen.text
                            ?.toString()
                            ?.trim()
                            .orEmpty(),
                    anhUri =
                        anhUriDangChon
                )

                return@registerForActivityResult
            }


            if (
                ketQua.resultCode ==
                UCrop.RESULT_ERROR
            ) {

                val loi =
                    ketQua.data
                        ?.let {
                            UCrop.getError(it)
                        }


                Toast.makeText(
                    this,
                    loi?.message
                        ?: "Không thể căn chỉnh ảnh",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


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


    override fun onDestroy() {

        /**
         * Nếu crop ảnh mới nhưng chưa bấm CẬP NHẬT thì không giữ
         * file ảnh mới trong bộ nhớ ứng dụng.
         */
        if (!daLuuThayDoi) {

            fileAnhMoiChuaLuu
                ?.takeIf {
                    it.exists()
                }
                ?.delete()
        }


        super.onDestroy()
    }


    private fun anhXa() {

        imgAvatar =
            findViewById(
                R.id.imgAvatarChiTietLienHeHoTro
            )

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

        btnChonAnh =
            findViewById(
                R.id.btnChonAnhLienHeHoTro
            )

        btnGoi =
            findViewById(
                R.id.btnGoiLienHeHoTro
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


        btnChonAnh.setOnClickListener {

            hienThiXacNhanTruyCapAnh()
        }


        btnGoi.setOnClickListener {

            moManHinhGoiDien()
        }


        btnCapNhat.setOnClickListener {

            capNhatLienHe()
        }


        btnXoa.setOnClickListener {

            xacNhanXoaLienHe()
        }
    }


    /**
     * Đây là bước giải thích trước khi mở trình chọn ảnh.
     *
     * FaceAccess KHÔNG cần quyền đọc toàn bộ thư viện.
     * Sau khi người dùng bấm "Cho phép chọn ảnh", Android sẽ
     * mở trình chọn ảnh hệ thống và người dùng tự quyết định ảnh nào
     * được chia sẻ cho ứng dụng.
     */
    private fun hienThiXacNhanTruyCapAnh() {

        AlertDialog.Builder(this)
            .setTitle(
                "Cho phép chọn ảnh đại diện"
            )
            .setMessage(
                "FaceAccess cần truy cập ảnh bạn chọn để làm ảnh đại diện. " +
                        "Ứng dụng chỉ sử dụng ảnh bạn tự chọn từ thư viện."
            )
            .setNegativeButton(
                "Hủy",
                null
            )
            .setPositiveButton(
                "Cho phép"
            ) {
                    _,
                    _ ->

                boChonAnh.launch(
                    arrayOf(
                        "image/*"
                    )
                )
            }
            .show()
    }


    /**
     * uCrop hiển thị khung tròn.
     *
     * Người dùng có thể:
     * - kéo ảnh sang trái/phải/lên/xuống;
     * - dùng hai ngón tay phóng to/thu nhỏ;
     * - đặt phần muốn giữ vào giữa vòng tròn.
     */
    private fun moManHinhCanChinhAnh(
        uriNguon: Uri
    ) {

        val thuMucAvatar =
            File(
                filesDir,
                THU_MUC_AVATAR
            )


        if (!thuMucAvatar.exists()) {

            thuMucAvatar.mkdirs()
        }


        val fileDich =
            File(
                thuMucAvatar,
                "avatar_${lienHeId}_${System.currentTimeMillis()}.jpg"
            )


        fileAnhMoiChuaLuu =
            fileDich


        val uriDich =
            Uri.fromFile(
                fileDich
            )


        val tuyChon =
            UCrop.Options().apply {

                setCircleDimmedLayer(
                    true
                )

                setShowCropFrame(
                    true
                )

                setShowCropGrid(
                    false
                )

                setFreeStyleCropEnabled(
                    false
                )

                setCompressionQuality(
                    92
                )

                setMaxScaleMultiplier(
                    10f
                )

                setToolbarTitle(
                    "Căn chỉnh ảnh đại diện"
                )

                setToolbarColor(
                    ContextCompat.getColor(
                        this@ChiTietLienHeHoTroActivity,
                        R.color.nen_man_hinh
                    )
                )

                setActiveControlsWidgetColor(
                    ContextCompat.getColor(
                        this@ChiTietLienHeHoTroActivity,
                        R.color.xanh_chinh
                    )
                )

                setToolbarWidgetColor(
                    ContextCompat.getColor(
                        this@ChiTietLienHeHoTroActivity,
                        android.R.color.white
                    )
                )
            }


        val intentCrop =
            UCrop.of(
                uriNguon,
                uriDich
            )
                .withAspectRatio(
                    1f,
                    1f
                )
                .withMaxResultSize(
                    1024,
                    1024
                )
                .withOptions(
                    tuyChon
                )
                .getIntent(
                    this
                )
                .apply {

                    /*
                     * Không dùng thanh điều khiển mặc định của uCrop.
                     * FaceAccess cung cấp panel hướng dẫn + nút xác nhận riêng
                     * trong CanChinhAnhLienHeActivity để đồng bộ giao diện.
                     */
                    putExtra(
                        UCrop.Options.EXTRA_HIDE_BOTTOM_CONTROLS,
                        true
                    )

                    putExtra(
                        UCrop.Options.EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR,
                        ContextCompat.getColor(
                            this@ChiTietLienHeHoTroActivity,
                            R.color.nen_man_hinh
                        )
                    )

                    putExtra(
                        UCrop.Options.EXTRA_DIMMED_LAYER_COLOR,
                        Color.argb(
                            190,
                            0,
                            0,
                            0
                        )
                    )

                    putExtra(
                        UCrop.Options.EXTRA_CROP_FRAME_COLOR,
                        Color.WHITE
                    )

                    putExtra(
                        UCrop.Options.EXTRA_STATUS_BAR_LIGHT,
                        false
                    )

                    putExtra(
                        UCrop.Options.EXTRA_NAVIGATION_BAR_LIGHT,
                        false
                    )

                    /*
                     * uCrop.getIntent() mặc định trỏ vào UCropActivity.
                     * Chuyển sang Activity kế thừa của FaceAccess để có
                     * giao diện và nút xác nhận riêng.
                     */
                    setClass(
                        this@ChiTietLienHeHoTroActivity,
                        CanChinhAnhLienHeActivity::class.java
                    )
                }


        boCanChinhAnh.launch(
            intentCrop
        )
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

        anhUriDangChon =
            lienHe.anhUri


        edtTen.setText(
            lienHe.ten
        )

        edtSoDienThoai.setText(
            lienHe.soDienThoai
        )

        edtMoTa.setText(
            lienHe.moTa
        )


        hienThiAvatar(
            ten =
                lienHe.ten,
            anhUri =
                lienHe.anhUri
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
                    moTa,
                anhUri =
                    anhUriDangChon
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


        /**
         * Nếu ảnh cũ cũng là file nội bộ do FaceAccess tạo và người dùng
         * đã chọn ảnh mới, xóa file cũ để tránh tích rác.
         */
        xoaAnhCuNeuCan(
            uriCu =
                hienTai.anhUri,
            uriMoi =
                anhUriDangChon
        )


        daLuuThayDoi =
            true

        fileAnhMoiChuaLuu =
            null


        Toast.makeText(
            this,
            "Đã cập nhật liên hệ",
            Toast.LENGTH_SHORT
        ).show()


        /**
         * DanhSachLienHeHoTroActivity reload trong onResume().
         */
        finish()
    }


    private fun xoaAnhCuNeuCan(
        uriCu: String?,
        uriMoi: String?
    ) {

        if (
            uriCu.isNullOrBlank() ||
            uriCu == uriMoi
        ) {
            return
        }


        try {

            val parsed =
                Uri.parse(
                    uriCu
                )


            if (
                parsed.scheme ==
                "file"
            ) {

                val duongDan =
                    parsed.path
                        ?: return


                val file =
                    File(
                        duongDan
                    )


                if (
                    file.absolutePath.startsWith(
                        filesDir.absolutePath
                    )
                ) {

                    file.delete()
                }
            }

        } catch (_: Exception) {

            // Không để lỗi dọn ảnh cũ làm hỏng cập nhật liên hệ.
        }
    }


    private fun moManHinhGoiDien() {

        val soDienThoai =
            edtSoDienThoai.text
                ?.toString()
                ?.trim()
                .orEmpty()


        if (!soDienThoaiHopLe(soDienThoai)) {

            edtSoDienThoai.error =
                "Số điện thoại chưa hợp lệ"

            edtSoDienThoai.requestFocus()

            return
        }


        val soDaChuanHoa =
            chuanHoaSoDienThoai(
                soDienThoai
            )


        val intentGoi =
            Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts(
                    "tel",
                    soDaChuanHoa,
                    null
                )
            )


        try {

            startActivity(
                intentGoi
            )

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Không tìm thấy ứng dụng gọi điện",
                Toast.LENGTH_SHORT
            ).show()
        }
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


    /**
     * Hiển thị ảnh thật nếu URI đọc được.
     * Nếu không thì fallback về chữ cái đầu.
     */
    private fun hienThiAvatar(
        ten: String,
        anhUri: String?
    ) {

        if (!anhUri.isNullOrBlank()) {

            try {

                imgAvatar.setImageURI(
                    null
                )

                imgAvatar.setImageURI(
                    Uri.parse(
                        anhUri
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

                    return
                }

            } catch (_: Exception) {

                // Fallback xuống avatar chữ.
            }
        }


        imgAvatar.setImageDrawable(
            null
        )

        imgAvatar.visibility =
            View.GONE

        txtAvatar.visibility =
            View.VISIBLE


        txtAvatar.text =
            chuDauCuaTen(
                ten
            )
    }


    private fun chuDauCuaTen(
        ten: String
    ): String {

        return ten
            .trim()
            .firstOrNull()
            ?.uppercaseChar()
            ?.toString()
            ?: "?"
    }


    private fun chuanHoaSoDienThoai(
        giaTri: String
    ): String {

        return giaTri
            .replace(
                " ",
                ""
            )
            .replace(
                "-",
                ""
            )
    }


    private fun soDienThoaiHopLe(
        giaTri: String
    ): Boolean {

        val daChuanHoa =
            chuanHoaSoDienThoai(
                giaTri
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

        private const val THU_MUC_AVATAR =
            "support_avatars"
    }
}
