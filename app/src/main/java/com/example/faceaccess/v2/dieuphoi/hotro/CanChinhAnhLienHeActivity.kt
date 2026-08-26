package com.example.faceaccess.v2.dieuphoi.hotro

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.faceaccess.v2.R
import com.yalantis.ucrop.UCropActivity

/**
 * Màn hình căn chỉnh ảnh đại diện riêng của FaceAccess.
 *
 * Dùng engine crop của uCrop nhưng thay phần tương tác chính bằng UI
 * đồng bộ với khu vực Liên hệ hỗ trợ:
 *
 * - nền tối;
 * - khung crop tròn;
 * - hướng dẫn rõ ràng;
 * - nút "XÁC NHẬN ẢNH" lớn ở phía dưới;
 * - vẫn kéo ảnh và pinch-to-zoom trực tiếp trên ảnh;
 * - nút X trên toolbar vẫn dùng để hủy/quay lại.
 *
 * Không sửa source của thư viện uCrop.
 */
class CanChinhAnhLienHeActivity :
    UCropActivity() {

    private var btnXacNhanAnh:
            Button? =
        null


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        themPanelXacNhan()
    }


    /**
     * uCrop mặc định có icon dấu tick trên toolbar.
     *
     * FaceAccess dùng nút lớn phía dưới nên ẩn menu crop mặc định.
     * Loader của uCrop vẫn được giữ để phản hồi khi đang xử lý ảnh.
     */
    override fun onCreateOptionsMenu(
        menu: Menu
    ): Boolean {

        val ketQua =
            super.onCreateOptionsMenu(
                menu
            )


        menu.findItem(
            com.yalantis.ucrop.R.id.menu_crop
        )
            ?.isVisible =
            false


        return ketQua
    }


    /**
     * Khi ảnh load xong, uCrop làm menu_crop visible.
     * Ta dùng trạng thái đó để bật nút XÁC NHẬN ẢNH, rồi tiếp tục
     * ẩn menu crop mặc định.
     */
    override fun onPrepareOptionsMenu(
        menu: Menu
    ): Boolean {

        val ketQua =
            super.onPrepareOptionsMenu(
                menu
            )


        val menuCrop =
            menu.findItem(
                com.yalantis.ucrop.R.id.menu_crop
            )


        val daSanSang =
            menuCrop?.isVisible ==
                    true


        btnXacNhanAnh?.apply {

            isEnabled =
                daSanSang

            alpha =
                if (daSanSang) {
                    1f
                } else {
                    0.55f
                }
        }


        menuCrop?.isVisible =
            false


        return ketQua
    }


    private fun themPanelXacNhan() {

        val content =
            findViewById<FrameLayout>(
                android.R.id.content
            )


        val panel =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                gravity =
                    Gravity.CENTER

                setPadding(
                    dp(16),
                    dp(14),
                    dp(16),
                    dp(16)
                )

                background =
                    taoNenPanel()
            }


        val huongDan =
            TextView(this).apply {

                text =
                    "Kéo ảnh để căn giữa • Chụm 2 ngón để phóng to hoặc thu nhỏ"

                gravity =
                    Gravity.CENTER

                textSize =
                    14f

                setTextColor(
                    ContextCompat.getColor(
                        this@CanChinhAnhLienHeActivity,
                        R.color.chu_chinh
                    )
                )

                setLineSpacing(
                    0f,
                    1.08f
                )
            }


        btnXacNhanAnh =
            Button(this).apply {

                text =
                    "XÁC NHẬN ẢNH"

                textSize =
                    15f

                setTypeface(
                    typeface,
                    Typeface.BOLD
                )

                setTextColor(
                    ContextCompat.getColor(
                        this@CanChinhAnhLienHeActivity,
                        android.R.color.white
                    )
                )

                backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@CanChinhAnhLienHeActivity,
                            R.color.xanh_chinh
                        )
                    )

                isEnabled =
                    false

                alpha =
                    0.55f

                setOnClickListener {

                    /**
                     * cropAndSaveImage() là protected trong UCropActivity.
                     * Nó crop ảnh hiện tại, ghi ra URI đích rồi trả RESULT_OK.
                     */
                    cropAndSaveImage()
                }
            }


        panel.addView(
            huongDan,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )


        val paramsNut =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(54)
            ).apply {

                topMargin =
                    dp(12)
            }


        panel.addView(
            btnXacNhanAnh,
            paramsNut
        )


        val paramsPanel =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            ).apply {

                marginStart =
                    dp(18)

                marginEnd =
                    dp(18)

                bottomMargin =
                    dp(22)
            }


        content.addView(
            panel,
            paramsPanel
        )
    }


    private fun taoNenPanel():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            cornerRadius =
                dp(18).toFloat()

            setColor(
                ContextCompat.getColor(
                    this@CanChinhAnhLienHeActivity,
                    R.color.nen_man_hinh
                )
            )

            setStroke(
                dp(1),
                ContextCompat.getColor(
                    this@CanChinhAnhLienHeActivity,
                    R.color.xanh_chinh
                )
            )
        }
    }


    private fun dp(
        giaTri: Int
    ): Int {

        return (
                giaTri *
                        resources.displayMetrics.density
                )
            .toInt()
    }
}
