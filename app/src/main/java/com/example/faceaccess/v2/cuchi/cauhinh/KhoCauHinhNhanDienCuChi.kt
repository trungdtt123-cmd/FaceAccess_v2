// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.cuchi.cauhinh

import android.content.Context
import android.content.SharedPreferences

class KhoCauHinhNhanDienCuChi(
    context: Context
) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            TEN_PREFERENCES,
            Context.MODE_PRIVATE
        )

    fun daHieuChinh(): Boolean =
        preferences.getBoolean(
            KHOA_DA_HIEU_CHINH,
            false
        )

    fun dangKyBoLangNgheThayDoi(
        listener:
        SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        preferences
            .registerOnSharedPreferenceChangeListener(
                listener
            )
    }

    fun huyDangKyBoLangNgheThayDoi(
        listener:
        SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        preferences
            .unregisterOnSharedPreferenceChangeListener(
                listener
            )
    }

    fun layCauHinh(): CauHinhNhanDienCuChi {

        val macDinh =
            CauHinhNhanDienCuChi.macDinh()

        return CauHinhNhanDienCuChi(

            chuanHoa =
                CauHinhChuanHoa(
                    lechRollTrungTinh =
                        preferences.getFloat(
                            KHOA_LECH_ROLL_TRUNG_TINH,
                            macDinh.chuanHoa.lechRollTrungTinh
                        ),
                    lechYawTrungTinh =
                        preferences.getFloat(
                            KHOA_LECH_YAW_TRUNG_TINH,
                            macDinh.chuanHoa.lechYawTrungTinh
                        ),
                    lechPitchTrungTinh =
                        preferences.getFloat(
                            KHOA_LECH_PITCH_TRUNG_TINH,
                            macDinh.chuanHoa.lechPitchTrungTinh
                        )
                ),

            huongDau =
                CauHinhHuongDau(
                    nguongYaw =
                        preferences.getFloat(
                            KHOA_NGUONG_YAW,
                            macDinh.huongDau.nguongYaw
                        ),
                    nguongPitch =
                        preferences.getFloat(
                            KHOA_NGUONG_PITCH,
                            macDinh.huongDau.nguongPitch
                        ),
                    nguongQuayTrai =
                        layFloatNullable(
                            KHOA_NGUONG_QUAY_TRAI
                        ),
                    nguongQuayPhai =
                        layFloatNullable(
                            KHOA_NGUONG_QUAY_PHAI
                        ),
                    nguongNhinLen =
                        layFloatNullable(
                            KHOA_NGUONG_NHIN_LEN
                        ),
                    nguongNhinXuong =
                        layFloatNullable(
                            KHOA_NGUONG_NHIN_XUONG
                        ),
                    tyLeChiPhoiYaw =
                        preferences.getFloat(
                            KHOA_TY_LE_CHI_PHOI_YAW,
                            macDinh.huongDau.tyLeChiPhoiYaw
                        ),
                    tyLeChiPhoiPitch =
                        preferences.getFloat(
                            KHOA_TY_LE_CHI_PHOI_PITCH,
                            macDinh.huongDau.tyLeChiPhoiPitch
                        ),
                    nguongRollTrungTinh =
                        preferences.getFloat(
                            KHOA_NGUONG_ROLL_TRUNG_TINH,
                            macDinh.huongDau.nguongRollTrungTinh
                        ),
                    nguongYawTrungTinh =
                        preferences.getFloat(
                            KHOA_NGUONG_YAW_TRUNG_TINH,
                            macDinh.huongDau.nguongYawTrungTinh
                        ),
                    nguongPitchTrungTinh =
                        preferences.getFloat(
                            KHOA_NGUONG_PITCH_TRUNG_TINH,
                            macDinh.huongDau.nguongPitchTrungTinh
                        ),
                    thoiGianGiuYawMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_GIU_YAW,
                            macDinh.huongDau.thoiGianGiuYawMs
                        ),
                    thoiGianGiuPitchMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_GIU_PITCH,
                            macDinh.huongDau.thoiGianGiuPitchMs
                        ),
                    thoiGianGraceMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_GRACE_HUONG_DAU,
                            macDinh.huongDau.thoiGianGraceMs
                        ),
                    thoiGianTrungTinhMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_TRUNG_TINH_HUONG_DAU,
                            macDinh.huongDau.thoiGianTrungTinhMs
                        )
                ),

            nghiengDau =
                CauHinhNghiengDau(
                    nguongTrai =
                        preferences.getFloat(
                            KHOA_NGUONG_NGHIENG_TRAI,
                            macDinh.nghiengDau.nguongTrai
                        ),
                    nguongPhai =
                        preferences.getFloat(
                            KHOA_NGUONG_NGHIENG_PHAI,
                            macDinh.nghiengDau.nguongPhai
                        ),
                    nguongTrungTinh =
                        preferences.getFloat(
                            KHOA_NGUONG_NGHIENG_TRUNG_TINH,
                            macDinh.nghiengDau.nguongTrungTinh
                        ),
                    thoiGianGiuMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_GIU_NGHIENG,
                            macDinh.nghiengDau.thoiGianGiuMs
                        ),
                    tyLeChiPhoi =
                        preferences.getFloat(
                            KHOA_TY_LE_CHI_PHOI_NGHIENG,
                            macDinh.nghiengDau.tyLeChiPhoi
                        )
                ),

            nhamHaiMat =
                CauHinhNhamHaiMat(
                    nguongDong =
                        preferences.getFloat(
                            KHOA_NGUONG_MAT_DONG,
                            macDinh.nhamHaiMat.nguongDong
                        ),
                    nguongMo =
                        preferences.getFloat(
                            KHOA_NGUONG_MAT_MO,
                            macDinh.nhamHaiMat.nguongMo
                        ),
                    thoiGianNhamXacNhanMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_NHAM_XAC_NHAN,
                            macDinh.nhamHaiMat.thoiGianNhamXacNhanMs
                        ),
                    thoiGianMoDeRearmMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_MO_MAT_REARM,
                            macDinh.nhamHaiMat.thoiGianMoDeRearmMs
                        ),
                    thoiGianNhieuChoPhepMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_NHIEU_MAT,
                            macDinh.nhamHaiMat.thoiGianNhieuChoPhepMs
                        )
                ),

            moMieng =
                CauHinhMoMieng(
                    nguongMo =
                        preferences.getFloat(
                            KHOA_NGUONG_MIENG_MO,
                            macDinh.moMieng.nguongMo
                        ),
                    nguongDong =
                        preferences.getFloat(
                            KHOA_NGUONG_MIENG_DONG,
                            macDinh.moMieng.nguongDong
                        ),
                    thoiGianGiuBackMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_GIU_MIENG,
                            macDinh.moMieng.thoiGianGiuBackMs
                        ),
                    thoiGianDongDeRearmMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_DONG_MIENG_REARM,
                            macDinh.moMieng.thoiGianDongDeRearmMs
                        )
                ),

            moMiengHaiLan =
                CauHinhMoMiengHaiLan(
                    nguongMo =
                        preferences.getFloat(
                            KHOA_NGUONG_MIENG_HAI_LAN_MO,
                            macDinh.moMiengHaiLan.nguongMo
                        ),
                    nguongDong =
                        preferences.getFloat(
                            KHOA_NGUONG_MIENG_HAI_LAN_DONG,
                            macDinh.moMiengHaiLan.nguongDong
                        ),
                    thoiGianGiuBackMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_GIU_MIENG_HAI_LAN,
                            macDinh.moMiengHaiLan.thoiGianGiuBackMs
                        ),
                    thoiGianMoNganToiThieuMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_MO_NGAN_TOI_THIEU,
                            macDinh.moMiengHaiLan.thoiGianMoNganToiThieuMs
                        ),
                    khoangChoLanHaiMs =
                        preferences.getLong(
                            KHOA_KHOANG_CHO_LAN_HAI,
                            macDinh.moMiengHaiLan.khoangChoLanHaiMs
                        ),
                    thoiGianDongDeRearmMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_DONG_MIENG_HAI_LAN_REARM,
                            macDinh.moMiengHaiLan.thoiGianDongDeRearmMs
                        ),
                    thoiGianNhieuChoPhepMs =
                        preferences.getLong(
                            KHOA_THOI_GIAN_NHIEU_MIENG_HAI_LAN,
                            macDinh.moMiengHaiLan.thoiGianNhieuChoPhepMs
                        )
                ),

            hanhDong =
                CauHinhHanhDongCuChi(
                    quayTrai =
                        layHanhDong(
                            KHOA_HANH_DONG_QUAY_TRAI,
                            macDinh.hanhDong.quayTrai
                        ),
                    quayPhai =
                        layHanhDong(
                            KHOA_HANH_DONG_QUAY_PHAI,
                            macDinh.hanhDong.quayPhai
                        ),
                    nhinLen =
                        layHanhDong(
                            KHOA_HANH_DONG_NHIN_LEN,
                            macDinh.hanhDong.nhinLen
                        ),
                    nhinXuong =
                        layHanhDong(
                            KHOA_HANH_DONG_NHIN_XUONG,
                            macDinh.hanhDong.nhinXuong
                        ),
                    nghiengTrai =
                        layHanhDong(
                            KHOA_HANH_DONG_NGHIENG_TRAI,
                            macDinh.hanhDong.nghiengTrai
                        ),
                    nghiengPhai =
                        layHanhDong(
                            KHOA_HANH_DONG_NGHIENG_PHAI,
                            macDinh.hanhDong.nghiengPhai
                        ),
                    nhamHaiMat =
                        layHanhDong(
                            KHOA_HANH_DONG_NHAM_HAI_MAT,
                            macDinh.hanhDong.nhamHaiMat
                        ),
                    moMieng =
                        layHanhDong(
                            KHOA_HANH_DONG_MO_MIENG,
                            macDinh.hanhDong.moMieng
                        ),
                    moMiengHaiLan =
                        layHanhDong(
                            KHOA_HANH_DONG_MO_MIENG_HAI_LAN,
                            macDinh.hanhDong.moMiengHaiLan
                        )
                ).chuanHoaChoCuChiToanCuc()
        )
    }

    fun luuCauHinh(
        cauHinh: CauHinhNhanDienCuChi
    ) {
        ghiCauHinh(
            cauHinh = cauHinh,
            danhDauDaHieuChinh = true
        )
    }

    fun luuCauHinhTuyChinh(
        cauHinh: CauHinhNhanDienCuChi
    ) {
        ghiCauHinh(
            cauHinh = cauHinh,
            danhDauDaHieuChinh = false
        )
    }

    fun huyHieuChinhCaNhan() {

        val hienTai =
            layCauHinh()

        val macDinh =
            CauHinhNhanDienCuChi.macDinh()

        // Chỉ xóa phần dữ liệu được học từ hiệu chỉnh.
        // Giữ nguyên mode/action mapping và các tham số thời gian người dùng đã chỉnh.
        val sauKhiHuy =
            hienTai.copy(
                chuanHoa =
                    macDinh.chuanHoa,

                huongDau =
                    hienTai.huongDau.copy(
                        nguongYaw =
                            macDinh.huongDau.nguongYaw,
                        nguongPitch =
                            macDinh.huongDau.nguongPitch,
                        nguongQuayTrai =
                            null,
                        nguongQuayPhai =
                            null,
                        nguongNhinLen =
                            null,
                        nguongNhinXuong =
                            null
                    ),

                nghiengDau =
                    hienTai.nghiengDau.copy(
                        nguongTrai =
                            macDinh.nghiengDau.nguongTrai,
                        nguongPhai =
                            macDinh.nghiengDau.nguongPhai
                    ),

                nhamHaiMat =
                    hienTai.nhamHaiMat.copy(
                        nguongDong =
                            macDinh.nhamHaiMat.nguongDong,
                        nguongMo =
                            macDinh.nhamHaiMat.nguongMo
                    ),

                moMieng =
                    hienTai.moMieng.copy(
                        nguongMo =
                            macDinh.moMieng.nguongMo,
                        nguongDong =
                            macDinh.moMieng.nguongDong
                    ),

                moMiengHaiLan =
                    hienTai.moMiengHaiLan.copy(
                        nguongMo =
                            macDinh.moMiengHaiLan.nguongMo,
                        nguongDong =
                            macDinh.moMiengHaiLan.nguongDong
                    )
            )

        ghiCauHinh(
            cauHinh = sauKhiHuy,
            danhDauDaHieuChinh = false,
            xoaDanhDauHieuChinh = true
        )
    }

    private fun ghiCauHinh(
        cauHinh: CauHinhNhanDienCuChi,
        danhDauDaHieuChinh: Boolean,
        xoaDanhDauHieuChinh: Boolean = false
    ) {

        val hanhDong =
            cauHinh.hanhDong
                .chuanHoaChoCuChiToanCuc()

        preferences.edit().apply {

            when {
                xoaDanhDauHieuChinh ->
                    remove(
                        KHOA_DA_HIEU_CHINH
                    )

                danhDauDaHieuChinh ->
                    putBoolean(
                        KHOA_DA_HIEU_CHINH,
                        true
                    )
            }

            putFloat(
                KHOA_LECH_ROLL_TRUNG_TINH,
                cauHinh.chuanHoa.lechRollTrungTinh
            )
            putFloat(
                KHOA_LECH_YAW_TRUNG_TINH,
                cauHinh.chuanHoa.lechYawTrungTinh
            )
            putFloat(
                KHOA_LECH_PITCH_TRUNG_TINH,
                cauHinh.chuanHoa.lechPitchTrungTinh
            )

            putFloat(
                KHOA_NGUONG_YAW,
                cauHinh.huongDau.nguongYaw
            )
            putFloat(
                KHOA_NGUONG_PITCH,
                cauHinh.huongDau.nguongPitch
            )

            ghiFloatNullable(
                this,
                KHOA_NGUONG_QUAY_TRAI,
                cauHinh.huongDau.nguongQuayTrai
            )
            ghiFloatNullable(
                this,
                KHOA_NGUONG_QUAY_PHAI,
                cauHinh.huongDau.nguongQuayPhai
            )
            ghiFloatNullable(
                this,
                KHOA_NGUONG_NHIN_LEN,
                cauHinh.huongDau.nguongNhinLen
            )
            ghiFloatNullable(
                this,
                KHOA_NGUONG_NHIN_XUONG,
                cauHinh.huongDau.nguongNhinXuong
            )

            putFloat(
                KHOA_TY_LE_CHI_PHOI_YAW,
                cauHinh.huongDau.tyLeChiPhoiYaw
            )
            putFloat(
                KHOA_TY_LE_CHI_PHOI_PITCH,
                cauHinh.huongDau.tyLeChiPhoiPitch
            )
            putFloat(
                KHOA_NGUONG_ROLL_TRUNG_TINH,
                cauHinh.huongDau.nguongRollTrungTinh
            )
            putFloat(
                KHOA_NGUONG_YAW_TRUNG_TINH,
                cauHinh.huongDau.nguongYawTrungTinh
            )
            putFloat(
                KHOA_NGUONG_PITCH_TRUNG_TINH,
                cauHinh.huongDau.nguongPitchTrungTinh
            )
            putLong(
                KHOA_THOI_GIAN_GIU_YAW,
                cauHinh.huongDau.thoiGianGiuYawMs
            )
            putLong(
                KHOA_THOI_GIAN_GIU_PITCH,
                cauHinh.huongDau.thoiGianGiuPitchMs
            )
            putLong(
                KHOA_THOI_GIAN_GRACE_HUONG_DAU,
                cauHinh.huongDau.thoiGianGraceMs
            )
            putLong(
                KHOA_THOI_GIAN_TRUNG_TINH_HUONG_DAU,
                cauHinh.huongDau.thoiGianTrungTinhMs
            )

            putFloat(
                KHOA_NGUONG_NGHIENG_TRAI,
                cauHinh.nghiengDau.nguongTrai
            )
            putFloat(
                KHOA_NGUONG_NGHIENG_PHAI,
                cauHinh.nghiengDau.nguongPhai
            )
            putFloat(
                KHOA_NGUONG_NGHIENG_TRUNG_TINH,
                cauHinh.nghiengDau.nguongTrungTinh
            )
            putLong(
                KHOA_THOI_GIAN_GIU_NGHIENG,
                cauHinh.nghiengDau.thoiGianGiuMs
            )
            putFloat(
                KHOA_TY_LE_CHI_PHOI_NGHIENG,
                cauHinh.nghiengDau.tyLeChiPhoi
            )

            putFloat(
                KHOA_NGUONG_MAT_DONG,
                cauHinh.nhamHaiMat.nguongDong
            )
            putFloat(
                KHOA_NGUONG_MAT_MO,
                cauHinh.nhamHaiMat.nguongMo
            )
            putLong(
                KHOA_THOI_GIAN_NHAM_XAC_NHAN,
                cauHinh.nhamHaiMat.thoiGianNhamXacNhanMs
            )
            putLong(
                KHOA_THOI_GIAN_MO_MAT_REARM,
                cauHinh.nhamHaiMat.thoiGianMoDeRearmMs
            )
            putLong(
                KHOA_THOI_GIAN_NHIEU_MAT,
                cauHinh.nhamHaiMat.thoiGianNhieuChoPhepMs
            )

            putFloat(
                KHOA_NGUONG_MIENG_MO,
                cauHinh.moMieng.nguongMo
            )
            putFloat(
                KHOA_NGUONG_MIENG_DONG,
                cauHinh.moMieng.nguongDong
            )
            putLong(
                KHOA_THOI_GIAN_GIU_MIENG,
                cauHinh.moMieng.thoiGianGiuBackMs
            )
            putLong(
                KHOA_THOI_GIAN_DONG_MIENG_REARM,
                cauHinh.moMieng.thoiGianDongDeRearmMs
            )

            putFloat(
                KHOA_NGUONG_MIENG_HAI_LAN_MO,
                cauHinh.moMiengHaiLan.nguongMo
            )
            putFloat(
                KHOA_NGUONG_MIENG_HAI_LAN_DONG,
                cauHinh.moMiengHaiLan.nguongDong
            )
            putLong(
                KHOA_THOI_GIAN_GIU_MIENG_HAI_LAN,
                cauHinh.moMiengHaiLan.thoiGianGiuBackMs
            )
            putLong(
                KHOA_THOI_GIAN_MO_NGAN_TOI_THIEU,
                cauHinh.moMiengHaiLan.thoiGianMoNganToiThieuMs
            )
            putLong(
                KHOA_KHOANG_CHO_LAN_HAI,
                cauHinh.moMiengHaiLan.khoangChoLanHaiMs
            )
            putLong(
                KHOA_THOI_GIAN_DONG_MIENG_HAI_LAN_REARM,
                cauHinh.moMiengHaiLan.thoiGianDongDeRearmMs
            )
            putLong(
                KHOA_THOI_GIAN_NHIEU_MIENG_HAI_LAN,
                cauHinh.moMiengHaiLan.thoiGianNhieuChoPhepMs
            )

            putString(
                KHOA_HANH_DONG_QUAY_TRAI,
                hanhDong.quayTrai.name
            )
            putString(
                KHOA_HANH_DONG_QUAY_PHAI,
                hanhDong.quayPhai.name
            )
            putString(
                KHOA_HANH_DONG_NHIN_LEN,
                hanhDong.nhinLen.name
            )
            putString(
                KHOA_HANH_DONG_NHIN_XUONG,
                hanhDong.nhinXuong.name
            )
            putString(
                KHOA_HANH_DONG_NGHIENG_TRAI,
                hanhDong.nghiengTrai.name
            )
            putString(
                KHOA_HANH_DONG_NGHIENG_PHAI,
                hanhDong.nghiengPhai.name
            )
            putString(
                KHOA_HANH_DONG_NHAM_HAI_MAT,
                hanhDong.nhamHaiMat.name
            )
            putString(
                KHOA_HANH_DONG_MO_MIENG,
                hanhDong.moMieng.name
            )
            putString(
                KHOA_HANH_DONG_MO_MIENG_HAI_LAN,
                hanhDong.moMiengHaiLan.name
            )

            apply()
        }
    }

    fun saoLuuCauHinhCaNhan() {

        val duLieuHienTai =
            preferences.all.toMap()

        preferences.edit().apply {

            duLieuHienTai.keys
                .filter { it.startsWith(TIEN_TO_BAN_SAO) }
                .forEach { remove(it) }

            duLieuHienTai.forEach { (khoa, giaTri) ->

                if (
                    khoa == KHOA_CO_BAN_SAO ||
                    khoa == KHOA_THOI_GIAN_BAN_SAO ||
                    khoa.startsWith(TIEN_TO_BAN_SAO)
                ) {
                    return@forEach
                }

                ghiGiaTri(
                    this,
                    TIEN_TO_BAN_SAO + khoa,
                    giaTri
                )
            }

            putBoolean(
                KHOA_CO_BAN_SAO,
                true
            )

            putLong(
                KHOA_THOI_GIAN_BAN_SAO,
                System.currentTimeMillis()
            )

            apply()
        }
    }

    fun coBanSaoCauHinh(): Boolean =
        preferences.getBoolean(
            KHOA_CO_BAN_SAO,
            false
        )

    fun layThoiGianBanSao(): Long? {

        if (!coBanSaoCauHinh()) {
            return null
        }

        val thoiGian =
            preferences.getLong(
                KHOA_THOI_GIAN_BAN_SAO,
                0L
            )

        return thoiGian.takeIf {
            it > 0L
        }
    }

    fun khoiPhucCauHinhTuBanSao(): Boolean {

        if (!coBanSaoCauHinh()) {
            return false
        }

        val duLieuBanSao =
            preferences.all
                .filterKeys {
                    it.startsWith(TIEN_TO_BAN_SAO)
                }

        if (duLieuBanSao.isEmpty()) {
            return false
        }

        val editor =
            preferences.edit()

        xoaCauHinhDangDung(
            editor
        )

        duLieuBanSao.forEach { (khoaBanSao, giaTri) ->

            val khoaGoc =
                khoaBanSao.removePrefix(
                    TIEN_TO_BAN_SAO
                )

            ghiGiaTri(
                editor,
                khoaGoc,
                giaTri
            )
        }

        editor.apply()

        return true
    }

    fun datLaiCauHinhMacDinh() {
        preferences.edit().apply {
            xoaCauHinhDangDung(this)
            apply()
        }
    }

    fun xoaCauHinhCaNhan() {
        datLaiCauHinhMacDinh()
    }

    fun xoaBanSaoCauHinh() {

        preferences.edit().apply {

            preferences.all.keys
                .filter {
                    it.startsWith(TIEN_TO_BAN_SAO)
                }
                .forEach {
                    remove(it)
                }

            remove(
                KHOA_CO_BAN_SAO
            )

            remove(
                KHOA_THOI_GIAN_BAN_SAO
            )

            apply()
        }
    }

    private fun xoaCauHinhDangDung(
        editor: SharedPreferences.Editor
    ) {

        preferences.all.keys
            .filterNot {
                laKhoaBanSao(it)
            }
            .forEach {
                editor.remove(it)
            }
    }

    private fun laKhoaBanSao(
        khoa: String
    ): Boolean =
        khoa.startsWith(
            TIEN_TO_BAN_SAO
        ) ||
                khoa == KHOA_CO_BAN_SAO ||
                khoa == KHOA_THOI_GIAN_BAN_SAO

    private fun layFloatNullable(
        khoa: String
    ): Float? =
        if (preferences.contains(khoa)) {
            preferences.getFloat(
                khoa,
                0f
            )
        } else {
            null
        }

    private fun layHanhDong(
        khoa: String,
        macDinh: HanhDongTuyChinhCuChi
    ): HanhDongTuyChinhCuChi {

        val ten =
            preferences.getString(
                khoa,
                null
            ) ?: return macDinh

        return runCatching {
            HanhDongTuyChinhCuChi.valueOf(
                ten
            )
        }.getOrDefault(
            macDinh
        )
    }

    private fun ghiFloatNullable(
        editor: SharedPreferences.Editor,
        khoa: String,
        giaTri: Float?
    ) {

        if (giaTri == null) {
            editor.remove(khoa)
        } else {
            editor.putFloat(
                khoa,
                giaTri
            )
        }
    }

    private fun ghiGiaTri(
        editor: SharedPreferences.Editor,
        khoa: String,
        giaTri: Any?
    ) {

        when (giaTri) {

            is String ->
                editor.putString(
                    khoa,
                    giaTri
                )

            is Int ->
                editor.putInt(
                    khoa,
                    giaTri
                )

            is Long ->
                editor.putLong(
                    khoa,
                    giaTri
                )

            is Float ->
                editor.putFloat(
                    khoa,
                    giaTri
                )

            is Boolean ->
                editor.putBoolean(
                    khoa,
                    giaTri
                )

            is Set<*> ->
                editor.putStringSet(
                    khoa,
                    giaTri.filterIsInstance<String>()
                        .toSet()
                )
        }
    }

    companion object {

        private const val TEN_PREFERENCES =
            "faceaccess_cau_hinh_nhan_dien"

        private const val KHOA_DA_HIEU_CHINH =
            "da_hieu_chinh"

        private const val KHOA_LECH_ROLL_TRUNG_TINH =
            "lech_roll_trung_tinh"

        private const val KHOA_LECH_YAW_TRUNG_TINH =
            "lech_yaw_trung_tinh"

        private const val KHOA_LECH_PITCH_TRUNG_TINH =
            "lech_pitch_trung_tinh"

        private const val KHOA_NGUONG_YAW =
            "nguong_yaw"

        private const val KHOA_NGUONG_PITCH =
            "nguong_pitch"

        private const val KHOA_NGUONG_QUAY_TRAI =
            "nguong_quay_trai"

        private const val KHOA_NGUONG_QUAY_PHAI =
            "nguong_quay_phai"

        private const val KHOA_NGUONG_NHIN_LEN =
            "nguong_nhin_len"

        private const val KHOA_NGUONG_NHIN_XUONG =
            "nguong_nhin_xuong"

        private const val KHOA_TY_LE_CHI_PHOI_YAW =
            "ty_le_chi_phoi_yaw"

        private const val KHOA_TY_LE_CHI_PHOI_PITCH =
            "ty_le_chi_phoi_pitch"

        private const val KHOA_NGUONG_ROLL_TRUNG_TINH =
            "nguong_roll_trung_tinh"

        private const val KHOA_NGUONG_YAW_TRUNG_TINH =
            "nguong_yaw_trung_tinh"

        private const val KHOA_NGUONG_PITCH_TRUNG_TINH =
            "nguong_pitch_trung_tinh"

        private const val KHOA_THOI_GIAN_GIU_YAW =
            "thoi_gian_giu_yaw"

        private const val KHOA_THOI_GIAN_GIU_PITCH =
            "thoi_gian_giu_pitch"

        private const val KHOA_THOI_GIAN_GRACE_HUONG_DAU =
            "thoi_gian_grace_huong_dau"

        private const val KHOA_THOI_GIAN_TRUNG_TINH_HUONG_DAU =
            "thoi_gian_trung_tinh_huong_dau"

        private const val KHOA_NGUONG_NGHIENG_TRAI =
            "nguong_nghieng_trai"

        private const val KHOA_NGUONG_NGHIENG_PHAI =
            "nguong_nghieng_phai"

        private const val KHOA_NGUONG_NGHIENG_TRUNG_TINH =
            "nguong_nghieng_trung_tinh"

        private const val KHOA_THOI_GIAN_GIU_NGHIENG =
            "thoi_gian_giu_nghieng"

        private const val KHOA_TY_LE_CHI_PHOI_NGHIENG =
            "ty_le_chi_phoi_nghieng"

        private const val KHOA_NGUONG_MAT_DONG =
            "nguong_mat_dong"

        private const val KHOA_NGUONG_MAT_MO =
            "nguong_mat_mo"

        private const val KHOA_THOI_GIAN_NHAM_XAC_NHAN =
            "thoi_gian_nham_xac_nhan"

        private const val KHOA_THOI_GIAN_MO_MAT_REARM =
            "thoi_gian_mo_mat_rearm"

        private const val KHOA_THOI_GIAN_NHIEU_MAT =
            "thoi_gian_nhieu_mat"

        private const val KHOA_NGUONG_MIENG_MO =
            "nguong_mieng_mo"

        private const val KHOA_NGUONG_MIENG_DONG =
            "nguong_mieng_dong"

        private const val KHOA_THOI_GIAN_GIU_MIENG =
            "thoi_gian_giu_mieng"

        private const val KHOA_THOI_GIAN_DONG_MIENG_REARM =
            "thoi_gian_dong_mieng_rearm"

        private const val KHOA_NGUONG_MIENG_HAI_LAN_MO =
            "nguong_mieng_hai_lan_mo"

        private const val KHOA_NGUONG_MIENG_HAI_LAN_DONG =
            "nguong_mieng_hai_lan_dong"

        private const val KHOA_THOI_GIAN_GIU_MIENG_HAI_LAN =
            "thoi_gian_giu_mieng_hai_lan"

        private const val KHOA_THOI_GIAN_MO_NGAN_TOI_THIEU =
            "thoi_gian_mo_ngan_toi_thieu"

        private const val KHOA_KHOANG_CHO_LAN_HAI =
            "khoang_cho_lan_hai"

        private const val KHOA_THOI_GIAN_DONG_MIENG_HAI_LAN_REARM =
            "thoi_gian_dong_mieng_hai_lan_rearm"

        private const val KHOA_THOI_GIAN_NHIEU_MIENG_HAI_LAN =
            "thoi_gian_nhieu_mieng_hai_lan"

        private const val KHOA_HANH_DONG_QUAY_TRAI =
            "hanh_dong_quay_trai"

        private const val KHOA_HANH_DONG_QUAY_PHAI =
            "hanh_dong_quay_phai"

        private const val KHOA_HANH_DONG_NHIN_LEN =
            "hanh_dong_nhin_len"

        private const val KHOA_HANH_DONG_NHIN_XUONG =
            "hanh_dong_nhin_xuong"

        private const val KHOA_HANH_DONG_NGHIENG_TRAI =
            "hanh_dong_nghieng_trai"

        private const val KHOA_HANH_DONG_NGHIENG_PHAI =
            "hanh_dong_nghieng_phai"

        private const val KHOA_HANH_DONG_NHAM_HAI_MAT =
            "hanh_dong_nham_hai_mat"

        private const val KHOA_HANH_DONG_MO_MIENG =
            "hanh_dong_mo_mieng"

        private const val KHOA_HANH_DONG_MO_MIENG_HAI_LAN =
            "hanh_dong_mo_mieng_hai_lan"

        private const val TIEN_TO_BAN_SAO =
            "backup_"

        private const val KHOA_CO_BAN_SAO =
            "co_ban_sao_cau_hinh"

        private const val KHOA_THOI_GIAN_BAN_SAO =
            "thoi_gian_ban_sao_cau_hinh"
    }
}
