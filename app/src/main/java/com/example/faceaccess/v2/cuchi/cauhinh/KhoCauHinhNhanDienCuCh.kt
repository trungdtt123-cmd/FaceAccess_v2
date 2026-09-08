package com.example.faceaccess.v2.cuchi.cauhinh

import android.content.Context

class KhoCauHinhNhanDienCuChi(
    context: Context
) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            TEN_PREFERENCES,
            Context.MODE_PRIVATE
        )


    // Kiểm tra người dùng đã hiệu chỉnh chưa
    fun daHieuChinh(): Boolean {

        return preferences.getBoolean(
            KHOA_DA_HIEU_CHINH,
            false
        )
    }


    // Lấy cấu hình đang sử dụng
    fun layCauHinh(): CauHinhNhanDienCuChi {

        val macDinh =
            CauHinhNhanDienCuChi.macDinh()


        // Chưa hiệu chỉnh thì dùng cấu hình gốc
        if (!daHieuChinh()) {
            return macDinh
        }


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

                    tyLeChiPhoiYaw =
                        macDinh.huongDau.tyLeChiPhoiYaw,

                    tyLeChiPhoiPitch =
                        macDinh.huongDau.tyLeChiPhoiPitch,

                    nguongRollTrungTinh =
                        macDinh.huongDau.nguongRollTrungTinh,

                    nguongYawTrungTinh =
                        macDinh.huongDau.nguongYawTrungTinh,

                    nguongPitchTrungTinh =
                        macDinh.huongDau.nguongPitchTrungTinh,

                    thoiGianGiuYawMs =
                        macDinh.huongDau.thoiGianGiuYawMs,

                    thoiGianGiuPitchMs =
                        macDinh.huongDau.thoiGianGiuPitchMs,

                    thoiGianGraceMs =
                        macDinh.huongDau.thoiGianGraceMs,

                    thoiGianTrungTinhMs =
                        macDinh.huongDau.thoiGianTrungTinhMs
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
                        macDinh.nghiengDau.nguongTrungTinh,

                    thoiGianGiuMs =
                        macDinh.nghiengDau.thoiGianGiuMs,

                    tyLeChiPhoi =
                        macDinh.nghiengDau.tyLeChiPhoi
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
                        macDinh.nhamHaiMat.thoiGianNhamXacNhanMs,

                    thoiGianMoDeRearmMs =
                        macDinh.nhamHaiMat.thoiGianMoDeRearmMs,

                    thoiGianNhieuChoPhepMs =
                        macDinh.nhamHaiMat.thoiGianNhieuChoPhepMs
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
                        macDinh.moMieng.thoiGianGiuBackMs,

                    thoiGianDongDeRearmMs =
                        macDinh.moMieng.thoiGianDongDeRearmMs
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
                        macDinh.moMiengHaiLan.thoiGianGiuBackMs,

                    thoiGianMoNganToiThieuMs =
                        macDinh.moMiengHaiLan.thoiGianMoNganToiThieuMs,

                    khoangChoLanHaiMs =
                        macDinh.moMiengHaiLan.khoangChoLanHaiMs,

                    thoiGianDongDeRearmMs =
                        macDinh.moMiengHaiLan.thoiGianDongDeRearmMs,

                    thoiGianNhieuChoPhepMs =
                        macDinh.moMiengHaiLan.thoiGianNhieuChoPhepMs
                )
        )
    }


    // Lưu cấu hình sau khi hiệu chỉnh
    fun luuCauHinh(
        cauHinh: CauHinhNhanDienCuChi
    ) {

        preferences
            .edit()

            .putBoolean(
                KHOA_DA_HIEU_CHINH,
                true
            )

            .putFloat(
                KHOA_LECH_ROLL_TRUNG_TINH,
                cauHinh.chuanHoa.lechRollTrungTinh
            )

            .putFloat(
                KHOA_LECH_YAW_TRUNG_TINH,
                cauHinh.chuanHoa.lechYawTrungTinh
            )

            .putFloat(
                KHOA_LECH_PITCH_TRUNG_TINH,
                cauHinh.chuanHoa.lechPitchTrungTinh
            )

            .putFloat(
                KHOA_NGUONG_YAW,
                cauHinh.huongDau.nguongYaw
            )

            .putFloat(
                KHOA_NGUONG_PITCH,
                cauHinh.huongDau.nguongPitch
            )

            .putFloat(
                KHOA_NGUONG_NGHIENG_TRAI,
                cauHinh.nghiengDau.nguongTrai
            )

            .putFloat(
                KHOA_NGUONG_NGHIENG_PHAI,
                cauHinh.nghiengDau.nguongPhai
            )

            .putFloat(
                KHOA_NGUONG_MAT_DONG,
                cauHinh.nhamHaiMat.nguongDong
            )

            .putFloat(
                KHOA_NGUONG_MAT_MO,
                cauHinh.nhamHaiMat.nguongMo
            )

            .putFloat(
                KHOA_NGUONG_MIENG_MO,
                cauHinh.moMieng.nguongMo
            )

            .putFloat(
                KHOA_NGUONG_MIENG_DONG,
                cauHinh.moMieng.nguongDong
            )

            .putFloat(
                KHOA_NGUONG_MIENG_HAI_LAN_MO,
                cauHinh.moMiengHaiLan.nguongMo
            )

            .putFloat(
                KHOA_NGUONG_MIENG_HAI_LAN_DONG,
                cauHinh.moMiengHaiLan.nguongDong
            )

            .apply()
    }


    // Xóa profile và quay về cấu hình mặc định
    fun xoaCauHinhCaNhan() {

        preferences
            .edit()
            .clear()
            .apply()
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


        private const val KHOA_NGUONG_NGHIENG_TRAI =
            "nguong_nghieng_trai"

        private const val KHOA_NGUONG_NGHIENG_PHAI =
            "nguong_nghieng_phai"


        private const val KHOA_NGUONG_MAT_DONG =
            "nguong_mat_dong"

        private const val KHOA_NGUONG_MAT_MO =
            "nguong_mat_mo"


        private const val KHOA_NGUONG_MIENG_MO =
            "nguong_mieng_mo"

        private const val KHOA_NGUONG_MIENG_DONG =
            "nguong_mieng_dong"


        private const val KHOA_NGUONG_MIENG_HAI_LAN_MO =
            "nguong_mieng_hai_lan_mo"

        private const val KHOA_NGUONG_MIENG_HAI_LAN_DONG =
            "nguong_mieng_hai_lan_dong"
    }
}