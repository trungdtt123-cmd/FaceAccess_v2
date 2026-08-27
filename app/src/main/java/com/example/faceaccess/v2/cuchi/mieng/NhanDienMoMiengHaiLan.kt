package com.example.faceaccess.v2.cuchi.mieng

class NhanDienMoMiengHaiLan(
    private val khiMoMotLan: () -> Unit,
    private val khiMoHaiLan: () -> Unit
) {

    private enum class TrangThai {
        SAN_SANG,
        MO_LAN_1,
        CHO_DONG_LAN_1,
        CHO_LAN_2,
        MO_LAN_2,
        CHO_DONG_SAU_KICH_HOAT
    }

    private var trangThai = TrangThai.SAN_SANG
    private var batDauMoMs: Long? = null
    private var batDauDongMs: Long? = null
    private var batDauChoLanHaiMs: Long? = null
    private var batDauNhieuMs: Long? = null

    fun capNhat(
        doMoMieng: Float?,
        thoiGianMs: Long
    ) {
        if (doMoMieng == null) {
            datLai()
            return
        }

        val dangMo = doMoMieng >= NGUONG_MO
        val dangDong = doMoMieng <= NGUONG_DONG

        when (trangThai) {
            TrangThai.SAN_SANG ->
                xuLySanSang(dangMo, dangDong, thoiGianMs)

            TrangThai.MO_LAN_1 ->
                xuLyMoLanMot(dangMo, dangDong, thoiGianMs)

            TrangThai.CHO_DONG_LAN_1 ->
                xuLyChoDongLanMot(dangDong, thoiGianMs)

            TrangThai.CHO_LAN_2 ->
                xuLyChoLanHai(dangMo, thoiGianMs)

            TrangThai.MO_LAN_2 ->
                xuLyMoLanHai(dangMo, dangDong, thoiGianMs)

            TrangThai.CHO_DONG_SAU_KICH_HOAT ->
                xuLyChoDongSauKichHoat(dangDong, thoiGianMs)
        }
    }

    fun datLai() {
        trangThai = TrangThai.SAN_SANG
        batDauMoMs = null
        batDauDongMs = null
        batDauChoLanHaiMs = null
        batDauNhieuMs = null
    }

    private fun xuLySanSang(
        dangMo: Boolean,
        dangDong: Boolean,
        thoiGianMs: Long
    ) {
        if (dangMo) {
            trangThai = TrangThai.MO_LAN_1
            batDauMoMs = thoiGianMs
            batDauNhieuMs = null
            return
        }

        if (dangDong) {
            batDauMoMs = null
            batDauNhieuMs = null
        }
    }

    private fun xuLyMoLanMot(
        dangMo: Boolean,
        dangDong: Boolean,
        thoiGianMs: Long
    ) {
        if (dangMo) {
            batDauNhieuMs = null

            val batDau = batDauMoMs ?: thoiGianMs.also {
                batDauMoMs = it
            }

            if (thoiGianMs - batDau >= THOI_GIAN_MO_XAC_NHAN_MS) {
                trangThai = TrangThai.CHO_DONG_LAN_1
                batDauDongMs = null
                batDauNhieuMs = null
            }
            return
        }

        if (dangDong) {
            datLai()
            return
        }

        if (quaThoiGianNhieu(thoiGianMs)) {
            datLai()
        }
    }

    private fun xuLyChoDongLanMot(
        dangDong: Boolean,
        thoiGianMs: Long
    ) {
        if (!dangDong) {
            batDauDongMs = null
            return
        }

        val batDau = batDauDongMs ?: thoiGianMs.also {
            batDauDongMs = it
        }

        if (thoiGianMs - batDau >= THOI_GIAN_DONG_GIUA_HAI_LAN_MS) {
            trangThai = TrangThai.CHO_LAN_2
            batDauChoLanHaiMs = thoiGianMs
            batDauMoMs = null
            batDauNhieuMs = null
        }
    }

    private fun xuLyChoLanHai(
        dangMo: Boolean,
        thoiGianMs: Long
    ) {
        val batDauCho = batDauChoLanHaiMs ?: thoiGianMs.also {
            batDauChoLanHaiMs = it
        }

        if (thoiGianMs - batDauCho > KHOANG_CHO_LAN_2_MS) {
            khiMoMotLan()
            datLai()
            return
        }

        if (dangMo) {
            trangThai = TrangThai.MO_LAN_2
            batDauMoMs = thoiGianMs
            batDauNhieuMs = null
        }
    }

    private fun xuLyMoLanHai(
        dangMo: Boolean,
        dangDong: Boolean,
        thoiGianMs: Long
    ) {
        if (dangMo) {
            batDauNhieuMs = null

            val batDau = batDauMoMs ?: thoiGianMs.also {
                batDauMoMs = it
            }

            if (thoiGianMs - batDau >= THOI_GIAN_MO_XAC_NHAN_MS) {
                khiMoHaiLan()
                trangThai = TrangThai.CHO_DONG_SAU_KICH_HOAT
                batDauDongMs = null
                batDauNhieuMs = null
            }
            return
        }

        if (dangDong) {
            trangThai = TrangThai.CHO_LAN_2
            batDauMoMs = null
            batDauNhieuMs = null
            return
        }

        if (quaThoiGianNhieu(thoiGianMs)) {
            trangThai = TrangThai.CHO_LAN_2
            batDauMoMs = null
            batDauNhieuMs = null
        }
    }

    private fun xuLyChoDongSauKichHoat(
        dangDong: Boolean,
        thoiGianMs: Long
    ) {
        if (!dangDong) {
            batDauDongMs = null
            return
        }

        val batDau = batDauDongMs ?: thoiGianMs.also {
            batDauDongMs = it
        }

        if (thoiGianMs - batDau >= THOI_GIAN_DONG_DE_REARM_MS) {
            datLai()
        }
    }

    private fun quaThoiGianNhieu(thoiGianMs: Long): Boolean {
        val batDau = batDauNhieuMs

        if (batDau == null) {
            batDauNhieuMs = thoiGianMs
            return false
        }

        return thoiGianMs - batDau > THOI_GIAN_NHIEU_CHO_PHEP_MS
    }

    companion object {
        private const val NGUONG_MO = 0.30f
        private const val NGUONG_DONG = 0.18f
        private const val THOI_GIAN_MO_XAC_NHAN_MS = 60L
        private const val THOI_GIAN_DONG_GIUA_HAI_LAN_MS = 40L
        private const val KHOANG_CHO_LAN_2_MS = 850L
        private const val THOI_GIAN_DONG_DE_REARM_MS = 90L
        private const val THOI_GIAN_NHIEU_CHO_PHEP_MS = 140L
    }
}