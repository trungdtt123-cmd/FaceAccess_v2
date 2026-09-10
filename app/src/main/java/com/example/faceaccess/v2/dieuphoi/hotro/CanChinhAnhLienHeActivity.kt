// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.dieuphoi.hotro

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.faceaccess.v2.R
import com.yalantis.ucrop.UCropActivity

// Giao diện căn ảnh theo phong cách FaceAccess.
class CanChinhAnhLienHeActivity :
    UCropActivity() {

    private var btnDungAnh:
            Button? =
        null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        cauHinhThanhHeThong()
        themBangDieuKhien()
    }

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

        btnDungAnh?.apply {

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

    private fun themBangDieuKhien() {

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
                    dp(14),
                    dp(10),
                    dp(14),
                    dp(12)
                )

                background =
                    getDrawable(
                        R.drawable.fa_support_card
                    )

                elevation =
                    dp(5).toFloat()
            }

        val huongDan =
            TextView(this).apply {

                text =
                    "Kéo hoặc chụm để căn ảnh"

                gravity =
                    Gravity.CENTER

                textSize =
                    13f

                setTextColor(
                    Color.parseColor(
                        "#5F756D"
                    )
                )
            }

        val hangNut =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER
            }

        val btnHuy =
            Button(this).apply {

                text =
                    "Hủy"

                textSize =
                    14f

                isAllCaps =
                    false

                setTypeface(
                    typeface,
                    Typeface.BOLD
                )

                setTextColor(
                    Color.parseColor(
                        "#315248"
                    )
                )

                backgroundTintList =
                    null

                background =
                    getDrawable(
                        R.drawable.fa_support_button_secondary
                    )

                ganHieuUngNhan(
                    this
                )

                setOnClickListener {

                    setResult(
                        RESULT_CANCELED
                    )

                    finish()
                }
            }

        btnDungAnh =
            Button(this).apply {

                text =
                    "Dùng ảnh"

                textSize =
                    14f

                isAllCaps =
                    false

                setTypeface(
                    typeface,
                    Typeface.BOLD
                )

                setTextColor(
                    Color.WHITE
                )

                backgroundTintList =
                    null

                background =
                    getDrawable(
                        R.drawable.fa_support_button_primary
                    )

                isEnabled =
                    false

                alpha =
                    0.55f

                ganHieuUngNhan(
                    this
                )

                setOnClickListener {

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

        val paramsHangNut =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52)
            ).apply {

                topMargin =
                    dp(8)
            }

        panel.addView(
            hangNut,
            paramsHangNut
        )

        val paramsNutHuy =
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
            ).apply {

                marginEnd =
                    dp(5)
            }

        val paramsNutDungAnh =
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
            ).apply {

                marginStart =
                    dp(5)
            }

        hangNut.addView(
            btnHuy,
            paramsNutHuy
        )

        hangNut.addView(
            btnDungAnh,
            paramsNutDungAnh
        )

        val paramsPanel =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            ).apply {

                marginStart =
                    dp(16)

                marginEnd =
                    dp(16)

                // Khoảng cách dự phòng trước khi nhận system inset
                bottomMargin =
                    dp(16)
            }

        content.addView(
            panel,
            paramsPanel
        )

        // Đẩy panel lên trên thanh điều hướng của điện thoại
        ViewCompat.setOnApplyWindowInsetsListener(
            content
        ) {
                _,
                insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            val layoutParams =
                panel.layoutParams
                        as FrameLayout.LayoutParams

            layoutParams.bottomMargin =
                systemBars.bottom +
                        dp(14)

            panel.layoutParams =
                layoutParams

            insets
        }

        ViewCompat.requestApplyInsets(
            content
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

    private fun dp(
        giaTri: Int
    ): Int {

        return (
                giaTri *
                        resources
                            .displayMetrics
                            .density
                )
            .toInt()
    }
}
