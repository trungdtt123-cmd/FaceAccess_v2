package com.example.faceaccess.v2.dieuphoi

import android.util.Log
import com.example.faceaccess.v2.chedo.CheDoDieuKhien
import com.example.faceaccess.v2.cuchi.huongdau.HuongDau
import com.example.faceaccess.v2.dieuphoi.contro.LenhConTro
import com.example.faceaccess.v2.dieuphoi.dieuhuong.LenhDieuHuong
import com.example.faceaccess.v2.dieuphoi.hotro.LenhHoTro
import com.example.faceaccess.v2.dieuphoi.media.LenhMedia

class DieuPhoiCuChi(
    private val layCheDoHienTai: () -> CheDoDieuKhien =
        { CheDoDieuKhien.DIEU_HUONG },
    private val khiCoHuongTheoCheDo:
        (CheDoDieuKhien, HuongDau) -> Unit =
        { _, _ -> },
    private val khiCoLenhDieuHuong:
        (LenhDieuHuong) -> Unit =
        { _ -> },
    private val khiCoLenhMedia:
        (LenhMedia) -> Unit =
        { _ -> },
    private val khiCoLenhHoTro:
        (LenhHoTro) -> Unit =
        { _ -> },
    private val khiCoLenhConTro:
        (LenhConTro) -> Unit =
        { _ -> },
    private val khiCoXacNhanConTro:
        () -> Unit =
        {},
    private val khiCoLenh:
        (LenhToanCuc) -> Unit
) {

    fun xuLy(suKien: SuKienCuChi) {
        when (suKien) {
            SuKienCuChi.NghiengTrai ->
                khiCoLenh(LenhToanCuc.HOME)

            SuKienCuChi.NghiengPhai ->
                khiCoLenh(LenhToanCuc.DOI_CHE_DO)

            SuKienCuChi.MoMieng ->
                khiCoLenh(LenhToanCuc.BACK)

            SuKienCuChi.NhamHaiMat ->
                xuLyNhamHaiMat()

            SuKienCuChi.MoMiengHaiLan ->
                xuLyMoMiengHaiLan()

            is SuKienCuChi.DieuHuongDau ->
                xuLyHuongDau(suKien.huong)
        }
    }

    private fun xuLyNhamHaiMat() {
        val cheDo =
            layCheDoHienTai()

        when (cheDo) {
            CheDoDieuKhien.MEDIA ->
                khiCoLenhMedia(
                    LenhMedia.PHAT_TAM_DUNG
                )

            CheDoDieuKhien.HO_TRO ->
                khiCoLenhHoTro(
                    LenhHoTro.GOI_HOAC_KET_THUC
                )

            CheDoDieuKhien.CON_TRO ->
                khiCoXacNhanConTro()

            CheDoDieuKhien.DIEU_HUONG ->
                khiCoLenhDieuHuong(
                    LenhDieuHuong.XAC_NHAN
                )
        }

        Log.d(
            TAG,
            "MODE=$cheDo | NHAM_HAI_MAT"
        )
    }

    private fun xuLyMoMiengHaiLan() {
        val cheDo = layCheDoHienTai()

        if (cheDo == CheDoDieuKhien.CON_TRO) {
            khiCoLenh(
                LenhToanCuc.DOI_KHOA_CON_TRO
            )
        }

        Log.d(
            TAG,
            "MODE=$cheDo | MO_MIENG_HAI_LAN"
        )
    }

    private fun xuLyHuongDau(huong: HuongDau) {
        val cheDo = layCheDoHienTai()

        khiCoHuongTheoCheDo(
            cheDo,
            huong
        )

        when (cheDo) {
            CheDoDieuKhien.DIEU_HUONG ->
                khiCoLenhDieuHuong(
                    huong.toLenhDieuHuong()
                )

            CheDoDieuKhien.MEDIA ->
                khiCoLenhMedia(
                    huong.toLenhMedia()
                )

            CheDoDieuKhien.HO_TRO ->
                khiCoLenhHoTro(
                    huong.toLenhHoTro()
                )

            CheDoDieuKhien.CON_TRO ->
                khiCoLenhConTro(
                    huong.toLenhConTro()
                )
        }

        Log.d(
            TAG,
            "MODE=$cheDo | HUONG=$huong"
        )
    }

    private fun HuongDau.toLenhDieuHuong(): LenhDieuHuong {
        return when (this) {
            HuongDau.TRAI -> LenhDieuHuong.TRUOC
            HuongDau.PHAI -> LenhDieuHuong.TIEP_THEO
            HuongDau.LEN -> LenhDieuHuong.CUON_XUONG
            HuongDau.XUONG -> LenhDieuHuong.CUON_LEN
        }
    }

    private fun HuongDau.toLenhMedia(): LenhMedia {
        return when (this) {
            HuongDau.TRAI -> LenhMedia.TRUOC
            HuongDau.PHAI -> LenhMedia.TIEP_THEO
            HuongDau.LEN -> LenhMedia.TANG_AM_LUONG
            HuongDau.XUONG -> LenhMedia.GIAM_AM_LUONG
        }
    }

    private fun HuongDau.toLenhHoTro(): LenhHoTro {
        return when (this) {
            HuongDau.TRAI -> LenhHoTro.NGUOI_TRUOC
            HuongDau.PHAI -> LenhHoTro.NGUOI_TIEP_THEO
            HuongDau.LEN -> LenhHoTro.XAC_NHAN_LIEN_HE
            HuongDau.XUONG -> LenhHoTro.HUY_LIEN_HE
        }
    }

    private fun HuongDau.toLenhConTro(): LenhConTro {
        return when (this) {
            HuongDau.TRAI -> LenhConTro.TRAI
            HuongDau.PHAI -> LenhConTro.PHAI
            HuongDau.LEN -> LenhConTro.LEN
            HuongDau.XUONG -> LenhConTro.XUONG
        }
    }

    companion object {
        private const val TAG = "DieuPhoiCuChi"
    }
}
