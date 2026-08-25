package com.example.faceaccess.v2.dieuphoi

import android.util.Log
import com.example.faceaccess.v2.chedo.CheDoDieuKhien
import com.example.faceaccess.v2.cuchi.huongdau.HuongDau

class DieuPhoiCuChi(

    /**
     * Chỉ được đọc khi nhận event YAW / PITCH.
     *
     * Gesture toàn cục như HOME, đổi mode, BACK
     * không phụ thuộc mode.
     */
    private val layCheDoHienTai:
        () -> CheDoDieuKhien =
        { CheDoDieuKhien.DIEU_HUONG },

    /**
     * Checkpoint hiện tại:
     * callback này chỉ dùng để Log routing theo mode.
     *
     * Chưa thực thi Navigation / Media / Support / Cursor.
     */
    private val khiCoHuongTheoCheDo:
        (CheDoDieuKhien, HuongDau) -> Unit =
        { _, _ -> },

    /**
     * Các lệnh toàn cục đang hoạt động thật.
     */
    private val khiCoLenh:
        (LenhToanCuc) -> Unit
) {

    fun xuLy(
        suKien: SuKienCuChi
    ) {

        when (suKien) {

            // =================================================
            // GLOBAL: ROLL TRÁI -> HOME
            // =================================================

            SuKienCuChi.NghiengTrai -> {

                Log.d(
                    TAG,
                    "NghiengTrai -> HOME"
                )

                khiCoLenh(
                    LenhToanCuc.HOME
                )
            }


            // =================================================
            // GLOBAL: ROLL PHẢI -> ĐỔI MODE
            // =================================================

            SuKienCuChi.NghiengPhai -> {

                Log.d(
                    TAG,
                    "NghiengPhai -> DOI_CHE_DO"
                )

                khiCoLenh(
                    LenhToanCuc.DOI_CHE_DO
                )
            }


            // =================================================
            // GLOBAL: MỞ MIỆNG -> BACK
            // =================================================

            SuKienCuChi.MoMieng -> {

                Log.d(
                    TAG,
                    "MoMieng -> BACK"
                )

                khiCoLenh(
                    LenhToanCuc.BACK
                )
            }


            // =================================================
            // MODE-DEPENDENT: YAW / PITCH
            // =================================================

            is SuKienCuChi.DieuHuongDau -> {

                val cheDoHienTai =
                    layCheDoHienTai()

                Log.d(
                    TAG,
                    "HuongDau=${suKien.huong} -> MODE=$cheDoHienTai"
                )

                /*
                 * CHƯA thực hiện hành động thật.
                 *
                 * Chỉ chuyển semantic event + mode hiện tại
                 * sang callback để kiểm tra routing.
                 */
                khiCoHuongTheoCheDo(
                    cheDoHienTai,
                    suKien.huong
                )
            }
        }
    }


    companion object {

        private const val TAG =
            "DieuPhoiCuChi"
    }
}