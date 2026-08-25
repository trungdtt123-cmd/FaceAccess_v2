package com.example.faceaccess.v2.dieuphoi

import android.util.Log

class DieuPhoiCuChi(
    private val khiCoLenhToanCuc:
        (LenhToanCuc) -> Unit
) {

    /**
     * Điểm duy nhất nhận sự kiện từ các detector.
     *
     * Detector không được tự gọi HOME,
     * đổi mode hoặc AccessibilityService.
     */
    fun xuLy(
        suKien: SuKienCuChi
    ) {

        when (suKien) {

            SuKienCuChi.NghiengTrai -> {

                Log.d(
                    TAG,
                    "NghiengTrai -> HOME"
                )

                phatLenh(
                    LenhToanCuc.HOME
                )
            }

            SuKienCuChi.NghiengPhai -> {

                Log.d(
                    TAG,
                    "NghiengPhai -> DOI_CHE_DO"
                )

                phatLenh(
                    LenhToanCuc.DOI_CHE_DO
                )
            }
        }
    }

    private fun phatLenh(
        lenh: LenhToanCuc
    ) {

        khiCoLenhToanCuc(
            lenh
        )
    }

    companion object {

        private const val TAG =
            "DieuPhoiCuChi"
    }
}