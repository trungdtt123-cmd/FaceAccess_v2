package com.example.faceaccess.v2.dieuphoi

import android.util.Log
import com.example.faceaccess.v2.chedo.CheDoDieuKhien
import com.example.faceaccess.v2.cuchi.huongdau.HuongDau
import com.example.faceaccess.v2.dieuphoi.dieuhuong.LenhDieuHuong
import com.example.faceaccess.v2.dieuphoi.media.LenhMedia

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
     * Chỉ dùng khi mode hiện tại là DIEU_HUONG.
     *
     * Checkpoint này callback chỉ Logcat.
     * Chưa thực thi Accessibility action thật.
     */
    private val khiCoLenhDieuHuong:
        (LenhDieuHuong) -> Unit =
        { _ -> },

    /**
     * Chỉ dùng khi mode hiện tại là MEDIA.
     *
     * Checkpoint này mới route semantic command.
     * Chưa thực thi Android media action thật.
     */
    private val khiCoLenhMedia:
        (LenhMedia) -> Unit =
        { _ -> },

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
                 * Luôn log semantic route hiện tại để regression-test
                 * được tất cả mode.
                 */
                khiCoHuongTheoCheDo(
                    cheDoHienTai,
                    suKien.huong
                )


                /*
                 * YAW / PITCH được ánh xạ theo mode hiện tại.
                 *
                 * DIEU_HUONG:
                 * TRAI  -> TRUOC
                 * PHAI  -> TIEP_THEO
                 * LEN   -> CUON_LEN
                 * XUONG -> CUON_XUONG
                 *
                 * MEDIA:
                 * TRAI  -> media trước
                 * PHAI  -> media tiếp theo
                 * LEN   -> tăng âm lượng
                 * XUONG -> giảm âm lượng
                 *
                 * HO_TRO / CON_TRO chưa xử lý ở checkpoint này.
                 */
                when (cheDoHienTai) {

                    CheDoDieuKhien.DIEU_HUONG -> {

                        val lenhDieuHuong =
                            when (suKien.huong) {

                                HuongDau.TRAI ->
                                    LenhDieuHuong.TRUOC

                                HuongDau.PHAI ->
                                    LenhDieuHuong.TIEP_THEO

                                HuongDau.LEN ->
                                    LenhDieuHuong.CUON_LEN

                                HuongDau.XUONG ->
                                    LenhDieuHuong.CUON_XUONG
                            }


                        Log.d(
                            TAG,
                            "DIEU_HUONG: ${suKien.huong} -> $lenhDieuHuong"
                        )


                        khiCoLenhDieuHuong(
                            lenhDieuHuong
                        )
                    }


                    CheDoDieuKhien.MEDIA -> {

                        val lenhMedia =
                            when (suKien.huong) {

                                HuongDau.TRAI ->
                                    LenhMedia.TRUOC

                                HuongDau.PHAI ->
                                    LenhMedia.TIEP_THEO

                                HuongDau.LEN ->
                                    LenhMedia.TANG_AM_LUONG

                                HuongDau.XUONG ->
                                    LenhMedia.GIAM_AM_LUONG
                            }


                        Log.d(
                            TAG,
                            "MEDIA: ${suKien.huong} -> $lenhMedia"
                        )


                        khiCoLenhMedia(
                            lenhMedia
                        )
                    }


                    CheDoDieuKhien.HO_TRO,
                    CheDoDieuKhien.CON_TRO -> {
                        /*
                         * Chưa có command riêng ở checkpoint này.
                         */
                    }
                }
            }
        }
    }


    companion object {

        private const val TAG =
            "DieuPhoiCuChi"
    }
}