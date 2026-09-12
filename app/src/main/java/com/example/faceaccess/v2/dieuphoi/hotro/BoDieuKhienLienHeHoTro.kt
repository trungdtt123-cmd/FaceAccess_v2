// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.dieuphoi.hotro

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.example.faceaccess.v2.truycap.DichVuTruyCapFaceAccess

class BoDieuKhienLienHeHoTro(
    context: Context
) {

    private val appContext =
        context.applicationContext

    private val khoLienHe =
        KhoLienHeHoTro(
            appContext
        )

    private val mainHandler =
        Handler(
            Looper.getMainLooper()
        )


    enum class TrangThaiPhien {
        CHUA_CHON,
        DA_CHON,
        DIALER_CO_SO
    }


    data class KetQua(
        val thanhCong: Boolean,
        val thongBao: String,
        val lienHe: NguoiHoTro? = null
    )


    private data class KetQuaMoDialer(
        val thanhCong: Boolean,
        val packageName: String? = null
    )


    fun thucThi(
        lenh: LenhHoTro
    ): KetQua {

        synchronized(KHOA_DONG_BO) {

            if (
                laLenhTrungLapGanNhat(
                    lenh
                )
            ) {

                return KetQua(
                    thanhCong = false,
                    thongBao = "",
                    lienHe = lienHeDangChon
                )
            }


            dongBoLienHeDangChonNeuCan()

            xuLyNeuDangChonSim(
                lenh
            )
                ?.let {
                    return it
                }

            if (
                lenh !=
                LenhHoTro.GOI_HOAC_KET_THUC &&
                DichVuTruyCapFaceAccess
                    .dangCoCuocGoiDangHienThi()
            ) {
                return KetQua(
                    thanhCong = false,
                    thongBao =
                        "Cuộc gọi đang diễn ra. Hãy nhắm hai mắt để kết thúc.",
                    lienHe = lienHeDangChon
                )
            }

            return when (lenh) {

                LenhHoTro.NGUOI_TRUOC -> {
                    xuLyYaw(
                        buoc = -1
                    )
                }


                LenhHoTro.NGUOI_TIEP_THEO -> {
                    xuLyYaw(
                        buoc = 1
                    )
                }


                LenhHoTro.XAC_NHAN_LIEN_HE -> {
                    xuLyPitchLen()
                }


                LenhHoTro.HUY_LIEN_HE -> {
                    xuLyPitchXuong()
                }

                LenhHoTro.GOI_HOAC_KET_THUC -> {
                    xuLyGoiHoacKetThuc()
                }
            }
        }
    }


    fun datLaiPhien() {

        synchronized(KHOA_DONG_BO) {

            datTrangThaiChuaChon(
                giuPackageDialer = false
            )

            lenhGanNhat =
                null

            thoiDiemLenhGanNhat =
                0L
        }
    }


    fun layLienHeDangChon():
            NguoiHoTro? {

        synchronized(KHOA_DONG_BO) {

            dongBoLienHeDangChonNeuCan()

            return lienHeDangChon
        }
    }


    fun layTrangThaiPhien():
            TrangThaiPhien {

        synchronized(KHOA_DONG_BO) {

            dongBoLienHeDangChonNeuCan()

            return trangThaiPhien
        }
    }


    private fun xuLyNeuDangChonSim(
        lenh: LenhHoTro
    ): KetQua? {
        // Cuộc gọi thật luôn có ưu tiên cao hơn bộ chọn SIM. Nếu màn hình
        // cuộc gọi có nhãn SIM 1/SIM 2, nhắm mắt vẫn phải kết thúc cuộc gọi
        // theo luồng cũ thay vì bị hiểu nhầm là xác nhận SIM.
        if (
            DichVuTruyCapFaceAccess
                .dangCoCuocGoiDangHienThi()
        ) {
            return null
        }

        if (
            trangThaiPhien !=
            TrangThaiPhien.DIALER_CO_SO ||
            !DichVuTruyCapFaceAccess
                .dangHienThiBoChonSim()
        ) {
            return null
        }

        val lienHe =
            lienHeDangChon

        return when (lenh) {
            // Khi hộp thoại chọn SIM đang hiện, YAW được ưu tiên để chuyển
            // qua lại giữa các SIM. Mapping YAW chung không đổi, chỉ đổi ý
            // nghĩa trong đúng ngữ cảnh bộ chọn SIM.
            LenhHoTro.NGUOI_TRUOC -> {
                val thanhCong =
                    DichVuTruyCapFaceAccess
                        .thucThiDiChuyenLuaChonSim(
                            buoc = -1
                        )

                KetQua(
                    thanhCong = thanhCong,
                    thongBao =
                        if (thanhCong) {
                            "Đã chuyển sang SIM trước. Nhắm hai mắt để xác nhận SIM."
                        } else {
                            "Chưa thể chuyển sang SIM trước."
                        },
                    lienHe = lienHe
                )
            }

            LenhHoTro.NGUOI_TIEP_THEO -> {
                val thanhCong =
                    DichVuTruyCapFaceAccess
                        .thucThiDiChuyenLuaChonSim(
                            buoc = 1
                        )

                KetQua(
                    thanhCong = thanhCong,
                    thongBao =
                        if (thanhCong) {
                            "Đã chuyển sang SIM tiếp theo. Nhắm hai mắt để xác nhận SIM."
                        } else {
                            "Chưa thể chuyển sang SIM tiếp theo."
                        },
                    lienHe = lienHe
                )
            }

            LenhHoTro.GOI_HOAC_KET_THUC -> {
                val thanhCong =
                    DichVuTruyCapFaceAccess
                        .thucThiXacNhanLuaChonSim()

                KetQua(
                    thanhCong = thanhCong,
                    thongBao =
                        if (thanhCong) {
                            "Đã xác nhận SIM. Đang thực hiện cuộc gọi."
                        } else {
                            "Hãy YAW trái/phải để chọn SIM trước khi nhắm hai mắt xác nhận."
                        },
                    lienHe = lienHe
                )
            }

            // PITCH giữ nguyên ý nghĩa Hỗ trợ bên ngoài hộp thoại chọn SIM.
            // Riêng lúc hộp thoại đang hiện, không để PITCH vô tình chọn SIM
            // hoặc làm thay đổi luồng gọi trên các máy không cần chọn SIM.
            LenhHoTro.XAC_NHAN_LIEN_HE,
            LenhHoTro.HUY_LIEN_HE -> {
                KetQua(
                    thanhCong = false,
                    thongBao =
                        "Đang chọn SIM. Hãy YAW trái/phải để đổi SIM, " +
                                "sau đó nhắm hai mắt để xác nhận.",
                    lienHe = lienHe
                )
            }
        }
    }


    // =========================================================
    // YAW
    // =========================================================

    private fun xuLyYaw(
        buoc: Int
    ): KetQua {

        val danhSach =
            khoLienHe.layTatCa()


        if (
            danhSach.isEmpty()
        ) {

            datTrangThaiChuaChon(
                giuPackageDialer =
                    dangOTrongDialer()
                        .first
            )


            return KetQua(
                thanhCong = false,
                thongBao =
                    "Danh sách liên hệ hỗ trợ đang trống"
            )
        }


        val viTriHienTai =
            lienHeDangChon
                ?.let { lienHe ->

                    danhSach.indexOfFirst {
                        it.id ==
                                lienHe.id
                    }
                }
                ?: -1


        val viTriMoi =
            if (
                viTriHienTai < 0
            ) {

                if (buoc >= 0) {
                    0
                } else {
                    danhSach.lastIndex
                }

            } else {

                (
                        viTriHienTai +
                                buoc +
                                danhSach.size
                        ) %
                        danhSach.size
            }


        val lienHeMoi =
            danhSach[
                viTriMoi
            ]


        lienHeDangChon =
            lienHeMoi

        trangThaiPhien =
            TrangThaiPhien.DA_CHON


        val (
            dangTrongDialer,
            packageDialerHienTai
        ) =
            dangOTrongDialer()


        if (
            !dangTrongDialer
        ) {

            return KetQua(
                thanhCong = true,
                thongBao =
                    "Đã chọn liên hệ hỗ trợ: ${tenHienThi(lienHeMoi)}.",
                lienHe =
                    lienHeMoi
            )
        }


        val ketQuaMo =
            moSoLienHeTrenDialer(
                lienHe = lienHeMoi,
                packageUuTien =
                    packageDialerHienTai
            )


        if (
            ketQuaMo.thanhCong
        ) {

            trangThaiPhien =
                TrangThaiPhien.DIALER_CO_SO

            packageDialerGanNhat =
                ketQuaMo.packageName
                    ?: packageDialerHienTai
                            ?: packageDialerGanNhat


            return KetQua(
                thanhCong = true,
                thongBao =
                    "Đã chuyển sang số của ${tenHienThi(lienHeMoi)}",
                lienHe =
                    lienHeMoi
            )
        }


        trangThaiPhien =
            TrangThaiPhien.DA_CHON


        return KetQua(
            thanhCong = false,
            thongBao =
                "Đã chọn ${tenHienThi(lienHeMoi)}, " +
                        "nhưng chưa thể cập nhật số trên ứng dụng Điện thoại.",
            lienHe =
                lienHeMoi
        )
    }


    // =========================================================
    // PITCH LÊN
    // =========================================================

    private fun xuLyPitchLen():
            KetQua {

        val lienHe =
            lienHeDangChon


        if (
            lienHe ==
            null ||
            trangThaiPhien ==
            TrangThaiPhien.CHUA_CHON
        ) {

            datTrangThaiChuaChon(
                giuPackageDialer =
                    dangOTrongDialer()
                        .first
            )


            return KetQua(
                thanhCong = false,
                thongBao =
                    "Chưa có liên hệ hỗ trợ được chọn. " +
                            "Hãy xoay đầu trái hoặc phải để chọn liên hệ."
            )
        }


        val (
            dangTrongDialer,
            packageDialerHienTai
        ) =
            dangOTrongDialer()


        if (
            dangTrongDialer &&
            trangThaiPhien ==
            TrangThaiPhien.DIALER_CO_SO
        ) {

            return KetQua(
                thanhCong = true,
                thongBao =
                    "Cuộc gọi tới ${tenHienThi(lienHe)} đã được chuẩn bị. " +
                            "Số điện thoại đang hiển thị trên ứng dụng Điện thoại.",
                lienHe =
                    lienHe
            )
        }


        val ketQuaMo =
            moSoLienHeTrenDialer(
                lienHe = lienHe,
                packageUuTien =
                    if (dangTrongDialer) {
                        packageDialerHienTai
                    } else {
                        null
                    }
            )


        if (
            !ketQuaMo.thanhCong
        ) {

            trangThaiPhien =
                TrangThaiPhien.DA_CHON


            return KetQua(
                thanhCong = false,
                thongBao =
                    "Không thể mở số điện thoại của ${tenHienThi(lienHe)}.",
                lienHe =
                    lienHe
            )
        }


        trangThaiPhien =
            TrangThaiPhien.DIALER_CO_SO

        packageDialerGanNhat =
            ketQuaMo.packageName
                ?: packageDialerHienTai
                        ?: packageDialerGanNhat


        return KetQua(
            thanhCong = true,
            thongBao =
                "Đã chuẩn bị cuộc gọi tới ${tenHienThi(lienHe)}. " +
                        "Số điện thoại đã được mở trên ứng dụng Điện thoại.",
            lienHe =
                lienHe
        )
    }


    // =========================================================
    // PITCH XUỐNG
    // =========================================================

    private fun xuLyPitchXuong():
            KetQua {

        val lienHeCu =
            lienHeDangChon


        if (
            lienHeCu ==
            null ||
            trangThaiPhien ==
            TrangThaiPhien.CHUA_CHON
        ) {

            datTrangThaiChuaChon(
                giuPackageDialer =
                    dangOTrongDialer()
                        .first
            )


            return KetQua(
                thanhCong = false,
                thongBao =
                    "Không có liên hệ hỗ trợ nào đang được chọn."
            )
        }


        val (
            dangTrongDialer,
            packageDialerHienTai
        ) =
            dangOTrongDialer()


        if (
            !dangTrongDialer
        ) {

            datTrangThaiChuaChon(
                giuPackageDialer = false
            )


            return KetQua(
                thanhCong = true,
                thongBao =
                    "Đã hủy lựa chọn ${tenHienThi(lienHeCu)}.",
                lienHe =
                    lienHeCu
            )
        }


        val soDienThoai =
            chuanHoaSoDienThoai(
                lienHeCu.soDienThoai
            )


        packageDialerGanNhat =
            packageDialerHienTai
                ?: packageDialerGanNhat

        datTrangThaiChuaChon(
            giuPackageDialer = true
        )


        val xoaTaiChoThanhCong =
            DichVuTruyCapFaceAccess
                .thucThiXoaSoTrinhQuaySo(
                    packageDialerMongDoi =
                        packageDialerHienTai
                            ?: packageDialerGanNhat,
                    soDienThoai =
                        soDienThoai
                )


        if (
            xoaTaiChoThanhCong
        ) {

            return KetQua(
                thanhCong = true,
                thongBao =
                    "Đã hủy liên hệ ${tenHienThi(lienHeCu)}. " +
                            "Số điện thoại đã được xóa. " +
                            "Hãy xoay đầu trái hoặc phải để chọn liên hệ khác.",
                lienHe =
                    lienHeCu
            )
        }


        val fallbackDaNhan =
            dongVaMoLaiDialerTrong(
                packageDialer =
                    packageDialerHienTai
                        ?: packageDialerGanNhat
            )


        return if (
            fallbackDaNhan
        ) {

            KetQua(
                thanhCong = true,
                thongBao =
                    "Đã hủy liên hệ ${tenHienThi(lienHeCu)}. " +
                            "Đang làm trống số điện thoại. " +
                            "Hãy xoay đầu trái hoặc phải để chọn liên hệ khác.",
                lienHe =
                    lienHeCu
            )

        } else {

            KetQua(
                thanhCong = false,
                thongBao =
                    "Liên hệ ${tenHienThi(lienHeCu)} đã được hủy trong FaceAccess, " +
                            "nhưng hệ thống chưa thể xóa số trên ứng dụng Điện thoại.",
                lienHe =
                    lienHeCu
            )
        }
    }


    // =========================================================
    // DIALER
    // =========================================================

    private fun xuLyGoiHoacKetThuc(): KetQua {
        if (
            DichVuTruyCapFaceAccess
                .thucThiKetThucCuocGoiNeuDangCo()
        ) {
            if (lienHeDangChon != null) {
                trangThaiPhien =
                    TrangThaiPhien.DA_CHON
            } else {
                trangThaiPhien =
                    TrangThaiPhien.CHUA_CHON
            }

            return KetQua(
                thanhCong = true,
                thongBao =
                    "Đã kết thúc cuộc gọi.",
                lienHe = lienHeDangChon
            )
        }

        val lienHe =
            lienHeDangChon

        if (
            lienHe == null ||
            trangThaiPhien !=
            TrangThaiPhien.DIALER_CO_SO
        ) {
            return KetQua(
                thanhCong = false,
                thongBao =
                    "Chưa có cuộc gọi sẵn sàng. " +
                            "Hãy chọn liên hệ và ngẩng đầu để chuẩn bị số."
            )
        }

        val (
            dangTrongDialer,
            packageDialerHienTai
        ) =
            dangOTrongDialer()

        if (!dangTrongDialer) {
            return KetQua(
                thanhCong = false,
                thongBao =
                    "Chưa ở màn hình quay số. " +
                            "Hãy ngẩng đầu để chuẩn bị lại cuộc gọi.",
                lienHe = lienHe
            )
        }

        val thanhCong =
            DichVuTruyCapFaceAccess
                .thucThiBatDauCuocGoiTrenDialer(
                    packageDialerMongDoi =
                        packageDialerHienTai
                            ?: packageDialerGanNhat
                )

        return if (thanhCong) {
            KetQua(
                thanhCong = true,
                thongBao =
                    "Đang gọi ${tenHienThi(lienHe)}.",
                lienHe = lienHe
            )
        } else {
            KetQua(
                thanhCong = false,
                thongBao =
                    "Không thể bắt đầu cuộc gọi tới ${tenHienThi(lienHe)}.",
                lienHe = lienHe
            )
        }
    }

    private fun moSoLienHeTrenDialer(
        lienHe: NguoiHoTro,
        packageUuTien: String?
    ): KetQuaMoDialer {

        val soDienThoai =
            chuanHoaSoDienThoai(
                lienHe.soDienThoai
            )


        if (
            !SO_DIEN_THOAI_HOP_LE
                .matches(
                    soDienThoai
                )
        ) {

            return KetQuaMoDialer(
                thanhCong = false
            )
        }


        fun taoIntent(
            packageName: String?
        ): Intent {

            return Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts(
                    "tel",
                    soDienThoai,
                    null
                )
            ).apply {

                if (
                    !packageName.isNullOrBlank()
                ) {

                    setPackage(
                        packageName
                    )
                }


                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )

                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                )

                addFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }
        }


        val cacPackageThu =
            buildList<String?> {
                val packageUuTienThucTe =
                    packageUuTien
                        ?: packageDialerGanNhat

                if (
                    !packageUuTienThucTe.isNullOrBlank()
                ) {
                    // Giữ đường ưu tiên cũ đang hoạt động tốt trên Samsung.
                    add(
                        packageUuTienThucTe
                    )
                }

                val packageMacDinh =
                    layPackageDialerMacDinh()

                if (
                    !packageMacDinh.isNullOrBlank() &&
                    packageMacDinh !in this
                ) {
                    add(
                        packageMacDinh
                    )
                }

                // Fallback cuối để Android/Flyme tự chọn Dialer phù hợp.
                add(
                    null
                )
            }


        for (packageName in cacPackageThu) {
            val intent =
                taoIntent(
                    packageName
                )

            try {
                // Không chặn bằng resolveActivity trước khi startActivity.
                // Một số ROM OEM giới hạn package visibility nhưng vẫn mở
                // ACTION_DIAL bình thường khi để Android tự định tuyến.
                appContext.startActivity(
                    intent
                )

                val packageDaMo =
                    packageName
                        ?: try {
                            intent.resolveActivity(
                                appContext.packageManager
                            )
                                ?.packageName
                        } catch (_: Exception) {
                            null
                        }
                        ?: layPackageDialerMacDinh()

                return KetQuaMoDialer(
                    thanhCong = true,
                    packageName =
                        packageDaMo
                )

            } catch (_: Exception) {
                // Thử phương án tiếp theo.
            }
        }


        return KetQuaMoDialer(
            thanhCong = false
        )
    }


    private fun dangOTrongDialer():
            Pair<Boolean, String?> {

        val packageDangHoatDong =
            DichVuTruyCapFaceAccess
                .layPackageDangHoatDong()
                ?: return Pair(
                    false,
                    null
                )


        val packageMacDinh =
            layPackageDialerMacDinh()


        val laDialer =
            packageDangHoatDong ==
                    packageDialerGanNhat ||
                    packageDangHoatDong ==
                    packageMacDinh


        if (
            laDialer
        ) {

            packageDialerGanNhat =
                packageDangHoatDong
        }


        return Pair(
            laDialer,
            if (laDialer) {
                packageDangHoatDong
            } else {
                null
            }
        )
    }


    private fun layPackageDialerMacDinh():
            String? {

        val packageTuTelecom =
            try {
                val telecomManager =
                    appContext.getSystemService(
                        Context.TELECOM_SERVICE
                    ) as? android.telecom.TelecomManager

                telecomManager
                    ?.defaultDialerPackage
                    ?.takeIf {
                        it.isNotBlank()
                    }
            } catch (_: Exception) {
                null
            }


        if (packageTuTelecom != null) {
            return packageTuTelecom
        }


        val intent =
            Intent(
                Intent.ACTION_DIAL,
                Uri.parse(
                    "tel:"
                )
            )


        return try {
            intent.resolveActivity(
                appContext.packageManager
            )
                ?.packageName
        } catch (_: Exception) {
            null
        }
    }


    private fun dongVaMoLaiDialerTrong(
        packageDialer: String?
    ): Boolean {

        val backDaNhan =
            DichVuTruyCapFaceAccess
                .thucThiBack()


        if (
            !backDaNhan
        ) {

            return moDialerTrongNgay(
                packageDialer
            )
        }


        mainHandler.postDelayed(
            {
                moDialerTrongNgay(
                    packageDialer
                )
            },
            DO_TRE_MO_LAI_DIALER_MS
        )


        return true
    }


    private fun moDialerTrongNgay(
        packageDialer: String?
    ): Boolean {

        fun taoIntent(
            packageName: String?
        ): Intent {

            return Intent(
                Intent.ACTION_DIAL
            ).apply {

                if (
                    !packageName.isNullOrBlank()
                ) {

                    setPackage(
                        packageName
                    )
                }


                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )

                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                )

                addFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }
        }


        val cacPackageThu =
            buildList<String?> {
                if (
                    !packageDialer.isNullOrBlank()
                ) {
                    add(
                        packageDialer
                    )
                }

                val packageMacDinh =
                    layPackageDialerMacDinh()

                if (
                    !packageMacDinh.isNullOrBlank() &&
                    packageMacDinh !in this
                ) {
                    add(
                        packageMacDinh
                    )
                }

                add(
                    null
                )
            }


        for (packageName in cacPackageThu) {
            try {
                appContext.startActivity(
                    taoIntent(
                        packageName
                    )
                )

                return true

            } catch (_: Exception) {
                // Thử phương án tiếp theo.
            }
        }


        return false
    }


    // =========================================================
    // CONTACT STATE
    // =========================================================

    private fun dongBoLienHeDangChonNeuCan() {

        if (
            trangThaiPhien !=
            TrangThaiPhien.DA_CHON
        ) {
            return
        }


        val id =
            lienHeDangChon
                ?.id
                ?: run {

                    datTrangThaiChuaChon(
                        giuPackageDialer =
                            dangOTrongDialer()
                                .first
                    )

                    return
                }


        val lienHeMoi =
            khoLienHe.layTheoId(
                id
            )


        if (
            lienHeMoi ==
            null
        ) {

            datTrangThaiChuaChon(
                giuPackageDialer =
                    dangOTrongDialer()
                        .first
            )

        } else {

            lienHeDangChon =
                lienHeMoi
        }
    }


    private fun datTrangThaiChuaChon(
        giuPackageDialer: Boolean
    ) {

        trangThaiPhien =
            TrangThaiPhien.CHUA_CHON

        lienHeDangChon =
            null


        if (
            !giuPackageDialer
        ) {

            packageDialerGanNhat =
                null
        }
    }


    private fun tenHienThi(
        lienHe: NguoiHoTro
    ): String {

        return lienHe.ten
            .trim()
            .takeIf {
                it.isNotBlank()
            }
            ?: lienHe.soDienThoai
    }


    private fun chuanHoaSoDienThoai(
        giaTri: String
    ): String {

        return giaTri
            .replace(
                " ",
                ""
            )
            .replace(
                "-",
                ""
            )
            .replace(
                "(",
                ""
            )
            .replace(
                ")",
                ""
            )
            .trim()
    }


    // =========================================================
    // DUPLICATE COMMAND GUARD
    // =========================================================

    private fun laLenhTrungLapGanNhat(
        lenh: LenhHoTro
    ): Boolean {

        val hienTai =
            SystemClock.elapsedRealtime()


        val trungLap =
            lenhGanNhat ==
                    lenh &&
                    hienTai -
                    thoiDiemLenhGanNhat <
                    CUA_SO_CHONG_LENH_TRUNG_MS


        lenhGanNhat =
            lenh

        thoiDiemLenhGanNhat =
            hienTai


        return trungLap
    }


    companion object {

        private val KHOA_DONG_BO =
            Any()

        @Volatile
        private var trangThaiPhien =
            TrangThaiPhien.CHUA_CHON

        @Volatile
        private var lienHeDangChon:
                NguoiHoTro? =
            null

        @Volatile
        private var packageDialerGanNhat:
                String? =
            null

        @Volatile
        private var lenhGanNhat:
                LenhHoTro? =
            null

        @Volatile
        private var thoiDiemLenhGanNhat =
            0L


        const val THOI_GIAN_NHAM_XAC_NHAN_MS =
            600L

        private const val CUA_SO_CHONG_LENH_TRUNG_MS =
            120L

        private const val DO_TRE_MO_LAI_DIALER_MS =
            160L


        private val SO_DIEN_THOAI_HOP_LE =
            Regex(
                "^\\+?\\d{9,15}$"
            )
    }
}
