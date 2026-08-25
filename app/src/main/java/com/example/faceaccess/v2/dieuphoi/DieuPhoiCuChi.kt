package com.example.faceaccess.v2.dieuphoi

import android.util.Log

class DieuPhoiCuChi(
    private val khiCoLenh:
        (LenhToanCuc) -> Unit
) {

    fun xuLy(
        suKien: SuKienCuChi
    ) {

        when (suKien) {

            SuKienCuChi.NghiengTrai -> {

                Log.d(
                    TAG,
                    "NghiengTrai -> HOME"
                )

                khiCoLenh(
                    LenhToanCuc.HOME
                )
            }


            SuKienCuChi.NghiengPhai -> {

                Log.d(
                    TAG,
                    "NghiengPhai -> DOI_CHE_DO"
                )

                khiCoLenh(
                    LenhToanCuc.DOI_CHE_DO
                )
            }


            SuKienCuChi.MoMieng -> {

                Log.d(
                    TAG,
                    "MoMieng -> BACK"
                )

                khiCoLenh(
                    LenhToanCuc.BACK
                )
            }
        }
    }


    companion object {

        private const val TAG =
            "DieuPhoiCuChi"
    }
}