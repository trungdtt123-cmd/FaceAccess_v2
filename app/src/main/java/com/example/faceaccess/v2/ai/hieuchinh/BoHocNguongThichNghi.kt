
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.ai.hieuchinh

import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhNhanDienCuChi

class BoHocNguongThichNghi {

    // Tạo cấu hình cá nhân từ dữ liệu hiệu chỉnh
    fun hoc(
        boThuThap: BoThuThapMauHieuChinh
    ): CauHinhNhanDienCuChi? {

        // Chỉ học khi đã đủ dữ liệu
        if (!boThuThap.daDuTatCaMau()) {
            return null
        }

        val macDinh =
            CauHinhNhanDienCuChi.macDinh()


        val mauTrungTinh =
            boThuThap.layMau(
                BuocHieuChinh.TRUNG_TINH
            )

        val mauQuayTrai =
            boThuThap.layMau(
                BuocHieuChinh.QUAY_TRAI
            )

        val mauQuayPhai =
            boThuThap.layMau(
                BuocHieuChinh.QUAY_PHAI
            )

        val mauNhinLen =
            boThuThap.layMau(
                BuocHieuChinh.NHIN_LEN
            )

        val mauNhinXuong =
            boThuThap.layMau(
                BuocHieuChinh.NHIN_XUONG
            )

        val mauNghiengTrai =
            boThuThap.layMau(
                BuocHieuChinh.NGHIENG_TRAI
            )

        val mauNghiengPhai =
            boThuThap.layMau(
                BuocHieuChinh.NGHIENG_PHAI
            )

        val mauNhamMat =
            boThuThap.layMau(
                BuocHieuChinh.NHAM_HAI_MAT
            )

        val mauMoMieng =
            boThuThap.layMau(
                BuocHieuChinh.MO_MIENG
            )


        // Tính tư thế trung tính
        val rollTrungTinh =
            trungVi(
                mauTrungTinh.mapNotNull {
                    it.roll
                }
            ) ?: return null

        val yawTrungTinh =
            trungVi(
                mauTrungTinh.mapNotNull {
                    it.yaw
                }
            ) ?: return null

        val pitchTrungTinh =
            trungVi(
                mauTrungTinh.mapNotNull {
                    it.pitch
                }
            ) ?: return null


        // Tính giá trị đại diện của từng cử chỉ
        val yawTrai =
            trungVi(
                mauQuayTrai.mapNotNull {
                    it.yaw
                }
            )

        val yawPhai =
            trungVi(
                mauQuayPhai.mapNotNull {
                    it.yaw
                }
            )

        val pitchLen =
            trungVi(
                mauNhinLen.mapNotNull {
                    it.pitch
                }
            )

        val pitchXuong =
            trungVi(
                mauNhinXuong.mapNotNull {
                    it.pitch
                }
            )

        val rollTrai =
            trungVi(
                mauNghiengTrai.mapNotNull {
                    it.roll
                }
            )

        val rollPhai =
            trungVi(
                mauNghiengPhai.mapNotNull {
                    it.roll
                }
            )


        // Biên độ từng hướng
        val bienDoQuayTrai =
            yawTrai?.minus(
                yawTrungTinh
            )

        val bienDoQuayPhai =
            yawPhai?.let {
                yawTrungTinh - it
            }

        val bienDoNhinLen =
            pitchLen?.minus(
                pitchTrungTinh
            )

        val bienDoNhinXuong =
            pitchXuong?.let {
                pitchTrungTinh - it
            }


        // Ngưỡng chung để tương thích
        val nguongYaw =
            hocNguongHaiPhia(
                bienDoPhiaDuong =
                    bienDoQuayTrai,

                bienDoPhiaAm =
                    bienDoQuayPhai,

                macDinh =
                    macDinh.huongDau.nguongYaw,

                toiThieu = 8f,
                toiDa = 18f
            )

        val nguongPitch =
            hocNguongHaiPhia(
                bienDoPhiaDuong =
                    bienDoNhinLen,

                bienDoPhiaAm =
                    bienDoNhinXuong,

                macDinh =
                    macDinh.huongDau.nguongPitch,

                toiThieu = 6f,
                toiDa = 14f
            )


        // Ngưỡng riêng từng hướng
        val nguongQuayTrai =
            hocNguongMotPhia(
                bienDo =
                    bienDoQuayTrai,

                macDinh =
                    macDinh.huongDau.nguongYaw,

                toiThieu = 8f,
                toiDa = 18f
            )

        val nguongQuayPhai =
            hocNguongMotPhia(
                bienDo =
                    bienDoQuayPhai,

                macDinh =
                    macDinh.huongDau.nguongYaw,

                toiThieu = 8f,
                toiDa = 18f
            )

        val nguongNhinLen =
            hocNguongMotPhia(
                bienDo =
                    bienDoNhinLen,

                macDinh =
                    macDinh.huongDau.nguongPitch,

                toiThieu = 6f,
                toiDa = 14f
            )

        val nguongNhinXuong =
            hocNguongMotPhia(
                bienDo =
                    bienDoNhinXuong,

                macDinh =
                    macDinh.huongDau.nguongPitch,

                toiThieu = 6f,
                toiDa = 14f
            )


        // Học ngưỡng nghiêng trái
        val bienDoNghiengTrai =
            rollTrai?.let {
                rollTrungTinh - it
            }

        val nguongNghiengTrai =
            hocNguongMotPhia(
                bienDo =
                    bienDoNghiengTrai,

                macDinh =
                    -macDinh.nghiengDau.nguongTrai,

                toiThieu = 9f,
                toiDa = 18f
            ) * -1f


        // Học ngưỡng nghiêng phải
        val bienDoNghiengPhai =
            rollPhai?.let {
                it - rollTrungTinh
            }

        val nguongNghiengPhai =
            hocNguongMotPhia(
                bienDo =
                    bienDoNghiengPhai,

                macDinh =
                    macDinh.nghiengDau.nguongPhai,

                toiThieu = 9f,
                toiDa = 18f
            )


        val nguongMat =
            hocNguongMat(
                mauTrungTinh =
                    mauTrungTinh.flatMap {
                        listOfNotNull(
                            it.doNhamMatTrai,
                            it.doNhamMatPhai
                        )
                    },

                mauNham =
                    mauNhamMat.flatMap {
                        listOfNotNull(
                            it.doNhamMatTrai,
                            it.doNhamMatPhai
                        )
                    },

                nguongMoMacDinh =
                    macDinh.nhamHaiMat.nguongMo,

                nguongDongMacDinh =
                    macDinh.nhamHaiMat.nguongDong
            )


        val nguongMieng =
            hocNguongMieng(
                mauDong =
                    mauTrungTinh.mapNotNull {
                        it.doMoMieng
                    },

                mauMo =
                    mauMoMieng.mapNotNull {
                        it.doMoMieng
                    },

                nguongMoMacDinh =
                    macDinh.moMieng.nguongMo,

                nguongDongMacDinh =
                    macDinh.moMieng.nguongDong
            )


        val nguongMiengHaiLan =
            hocNguongMiengHaiLan(
                mauDong =
                    mauTrungTinh.mapNotNull {
                        it.doMoMieng
                    },

                mauMo =
                    mauMoMieng.mapNotNull {
                        it.doMoMieng
                    },

                nguongMoMacDinh =
                    macDinh.moMiengHaiLan.nguongMo,

                nguongDongMacDinh =
                    macDinh.moMiengHaiLan.nguongDong
            )


        // Giữ nguyên các tham số thời gian
        return macDinh.copy(

            chuanHoa =
                macDinh.chuanHoa.copy(

                    lechRollTrungTinh =
                        rollTrungTinh,

                    lechYawTrungTinh =
                        yawTrungTinh,

                    lechPitchTrungTinh =
                        pitchTrungTinh
                ),


            huongDau =
                macDinh.huongDau.copy(

                    nguongYaw =
                        nguongYaw,

                    nguongPitch =
                        nguongPitch,

                    nguongQuayTrai =
                        nguongQuayTrai,

                    nguongQuayPhai =
                        nguongQuayPhai,

                    nguongNhinLen =
                        nguongNhinLen,

                    nguongNhinXuong =
                        nguongNhinXuong
                ),


            nghiengDau =
                macDinh.nghiengDau.copy(

                    nguongTrai =
                        nguongNghiengTrai,

                    nguongPhai =
                        nguongNghiengPhai
                ),


            nhamHaiMat =
                macDinh.nhamHaiMat.copy(

                    nguongMo =
                        nguongMat.first,

                    nguongDong =
                        nguongMat.second
                ),


            moMieng =
                macDinh.moMieng.copy(

                    nguongMo =
                        nguongMieng.first,

                    nguongDong =
                        nguongMieng.second
                ),


            moMiengHaiLan =
                macDinh.moMiengHaiLan.copy(

                    nguongMo =
                        nguongMiengHaiLan.first,

                    nguongDong =
                        nguongMiengHaiLan.second
                )
        )
    }


    // Học ngưỡng từ hai hướng đối lập
    private fun hocNguongHaiPhia(
        bienDoPhiaDuong: Float?,
        bienDoPhiaAm: Float?,
        macDinh: Float,
        toiThieu: Float,
        toiDa: Float
    ): Float {

        if (
            bienDoPhiaDuong == null ||
            bienDoPhiaAm == null ||
            bienDoPhiaDuong <= 0f ||
            bienDoPhiaAm <= 0f
        ) {
            return macDinh
        }

        val bienDo =
            minOf(
                bienDoPhiaDuong,
                bienDoPhiaAm
            )

        return (
                bienDo *
                        TY_LE_NGUONG_CHUYEN_DONG
                )
            .coerceIn(
                toiThieu,
                minOf(toiDa, macDinh)
            )
    }


    // Học ngưỡng từ một hướng
    private fun hocNguongMotPhia(
        bienDo: Float?,
        macDinh: Float,
        toiThieu: Float,
        toiDa: Float
    ): Float {

        if (
            bienDo == null ||
            bienDo <= 0f
        ) {
            return macDinh
        }

        return (
                bienDo *
                        TY_LE_NGUONG_CHUYEN_DONG
                )
            .coerceIn(
                toiThieu,
                minOf(toiDa, macDinh)
            )
    }


    // Học ngưỡng đóng và mở mắt
    private fun hocNguongMat(
        mauTrungTinh: List<Float>,
        mauNham: List<Float>,
        nguongMoMacDinh: Float,
        nguongDongMacDinh: Float
    ): Pair<Float, Float> {

        val matMo =
            trungVi(
                mauTrungTinh
            )

        val matDong =
            trungVi(
                mauNham
            )

        if (
            matMo == null ||
            matDong == null
        ) {
            return Pair(
                nguongMoMacDinh,
                nguongDongMacDinh
            )
        }

        val khoangCach =
            matDong - matMo

        // Dữ liệu không đủ tách biệt thì dùng mặc định
        if (
            khoangCach <
            KHOANG_CACH_MAT_TOI_THIEU
        ) {
            return Pair(
                nguongMoMacDinh,
                nguongDongMacDinh
            )
        }

        val nguongMo =
            (
                    matMo +
                            khoangCach * 0.30f
                    )
                .coerceIn(
                    0.05f,
                    0.60f
                )

        val nguongDong =
            (
                    matMo +
                            khoangCach * 0.65f
                    )
                .coerceIn(
                    0.30f,
                    0.90f
                )

        if (
            nguongDong <=
            nguongMo
        ) {
            return Pair(
                nguongMoMacDinh,
                nguongDongMacDinh
            )
        }

        return Pair(
            nguongMo,
            nguongDong
        )
    }


    // Học ngưỡng mở và đóng miệng
    private fun hocNguongMieng(
        mauDong: List<Float>,
        mauMo: List<Float>,
        nguongMoMacDinh: Float,
        nguongDongMacDinh: Float
    ): Pair<Float, Float> {

        val miengDong =
            trungVi(
                mauDong
            )

        val miengMo =
            trungVi(
                mauMo
            )

        if (
            miengDong == null ||
            miengMo == null
        ) {
            return Pair(
                nguongMoMacDinh,
                nguongDongMacDinh
            )
        }

        val khoangCach =
            miengMo - miengDong

        if (
            khoangCach <
            KHOANG_CACH_MIENG_TOI_THIEU
        ) {
            return Pair(
                nguongMoMacDinh,
                nguongDongMacDinh
            )
        }

        val nguongDong =
            (
                    miengDong +
                            khoangCach * 0.20f
                    )
                .coerceIn(
                    0.05f,
                    0.35f
                )

        val nguongMo =
            (
                    miengDong +
                            khoangCach * 0.55f
                    )
                .coerceIn(
                    0.15f,
                    0.70f
                )

        if (
            nguongMo <=
            nguongDong
        ) {
            return Pair(
                nguongMoMacDinh,
                nguongDongMacDinh
            )
        }

        return Pair(
            nguongMo,
            nguongDong
        )
    }


    // Học ngưỡng mở miệng hai lần
    private fun hocNguongMiengHaiLan(
        mauDong: List<Float>,
        mauMo: List<Float>,
        nguongMoMacDinh: Float,
        nguongDongMacDinh: Float
    ): Pair<Float, Float> {

        val miengDong =
            trungVi(
                mauDong
            )

        val miengMo =
            trungVi(
                mauMo
            )

        if (
            miengDong == null ||
            miengMo == null
        ) {
            return Pair(
                nguongMoMacDinh,
                nguongDongMacDinh
            )
        }

        val khoangCach =
            miengMo - miengDong

        if (
            khoangCach <
            KHOANG_CACH_MIENG_TOI_THIEU
        ) {
            return Pair(
                nguongMoMacDinh,
                nguongDongMacDinh
            )
        }

        val nguongDong =
            (
                    miengDong +
                            khoangCach * 0.28f
                    )
                .coerceIn(
                    0.05f,
                    0.40f
                )

        val nguongMo =
            (
                    miengDong +
                            khoangCach * 0.45f
                    )
                .coerceIn(
                    0.12f,
                    0.65f
                )

        if (
            nguongMo <=
            nguongDong
        ) {
            return Pair(
                nguongMoMacDinh,
                nguongDongMacDinh
            )
        }

        return Pair(
            nguongMo,
            nguongDong
        )
    }


    // Trung vị giúp giảm ảnh hưởng của frame nhiễu
    private fun trungVi(
        giaTri: List<Float>
    ): Float? {

        if (giaTri.isEmpty()) {
            return null
        }

        val daSapXep =
            giaTri.sorted()

        val giua =
            daSapXep.size / 2

        return if (
            daSapXep.size % 2 == 1
        ) {

            daSapXep[giua]

        } else {

            (
                    daSapXep[giua - 1] +
                            daSapXep[giua]
                    ) / 2f
        }
    }


    companion object {

        // Dùng khoảng 72% biên độ cử chỉ đã học
        private const val TY_LE_NGUONG_CHUYEN_DONG =
            0.72f

        // Tránh học từ dữ liệu nhắm mắt quá giống mắt mở
        private const val KHOANG_CACH_MAT_TOI_THIEU =
            0.15f

        // Tránh học từ dữ liệu mở miệng không rõ
        private const val KHOANG_CACH_MIENG_TOI_THIEU =
            0.10f
    }
}