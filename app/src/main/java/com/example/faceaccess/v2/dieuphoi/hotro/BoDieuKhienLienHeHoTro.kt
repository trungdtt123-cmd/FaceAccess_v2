package com.example.faceaccess.v2.dieuphoi.hotro

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.example.faceaccess.v2.truycap.DichVuTruyCapFaceAccess

/**
 * Bộ điều khiển nghiệp vụ của chế độ HỖ TRỢ.
 *
 * Quy tắc UX:
 *
 * NGOÀI ứng dụng Điện thoại:
 * - YAW trái/phải: chỉ chọn liên hệ.
 * - PITCH lên: mở Dialer với liên hệ đã chọn.
 * - PITCH xuống: hủy lựa chọn.
 *
 * ĐANG ở ứng dụng Điện thoại:
 * - YAW trái/phải: đổi liên hệ và cập nhật số NGAY, không cần PITCH lên lại.
 * - PITCH lên: không mở lặp; chỉ thông báo cuộc gọi hiện tại đã sẵn sàng.
 * - PITCH xuống: hủy liên hệ, xóa số khỏi Dialer nhưng giữ ứng dụng Điện thoại mở.
 *
 * Sau khi PITCH xuống:
 * - FaceAccess xóa selected contact ngay lập tức.
 * - PITCH lên khi chưa YAW lại tuyệt đối không được mở số cũ.
 * - Nếu vẫn đang ở Dialer trống, YAW sẽ chọn người mới và mở số ngay.
 *
 * State phiên không persist qua process death để tránh phục hồi contact cũ.
 */
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


    // =========================================================
    // YAW
    // =========================================================

    /**
     * YAW luôn đổi selected contact.
     *
     * Nếu đang ở Dialer:
     * -> cập nhật số ngay.
     *
     * Nếu không ở Dialer:
     * -> chỉ chọn contact, chờ PITCH lên.
     */
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


        /*
         * Đang ở ứng dụng Điện thoại:
         * YAW phải cập nhật số ngay, không yêu cầu PITCH lên lần nữa.
         */
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


        /*
         * Contact vẫn được chọn, nhưng Dialer update thất bại.
         * PITCH lên có thể thử lại.
         */
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


        /*
         * PITCH lên lặp lại khi số đã nằm trên Dialer:
         * KHÔNG startActivity lại, KHÔNG đổi số.
         */
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


        /*
         * Nếu chỉ đang chọn contact ngoài Dialer:
         * hủy selection, không mở app Điện thoại.
         */
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


        /*
         * SAFETY:
         *
         * Xóa state FaceAccess TRƯỚC khi can thiệp Dialer.
         * Kể từ dòng này, PITCH lên tiếp theo không thể lấy số cũ.
         *
         * Giữ package Dialer để YAW tiếp theo có thể mở số mới ngay.
         */
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


        /*
         * Fallback cho ROM/Dialer không expose field số:
         *
         * 1. BACK khỏi màn hình số hiện tại.
         * 2. Sau một khoảng rất ngắn, mở lại Dialer KHÔNG kèm số.
         *
         * Kết quả cuối vẫn là app Điện thoại đang mở với vùng số trống.
         */
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


        val intentUuTien =
            taoIntent(
                packageUuTien
                    ?: packageDialerGanNhat
            )


        val componentUuTien =
            intentUuTien.resolveActivity(
                appContext.packageManager
            )


        val intentThucThi =
            if (
                componentUuTien !=
                null
            ) {

                intentUuTien

            } else {

                taoIntent(
                    packageName = null
                )
            }


        val component =
            intentThucThi.resolveActivity(
                appContext.packageManager
            )
                ?: return KetQuaMoDialer(
                    thanhCong = false
                )


        return try {

            appContext.startActivity(
                intentThucThi
            )


            KetQuaMoDialer(
                thanhCong = true,
                packageName =
                    component.packageName
            )

        } catch (_: Exception) {

            KetQuaMoDialer(
                thanhCong = false
            )
        }
    }


    /**
     * Trả về:
     * - first  = có đang ở app Điện thoại hay không.
     * - second = package Dialer đang active.
     */
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

        val intent =
            Intent(
                Intent.ACTION_DIAL
            )


        return intent.resolveActivity(
            appContext.packageManager
        )
            ?.packageName
    }


    /**
     * Fallback cuối:
     * đóng màn hình số hiện tại rồi mở lại Dialer không truyền số.
     */
    private fun dongVaMoLaiDialerTrong(
        packageDialer: String?
    ): Boolean {

        val backDaNhan =
            DichVuTruyCapFaceAccess
                .thucThiBack()


        if (
            !backDaNhan
        ) {

            /*
             * Vẫn thử mở Dialer trống trực tiếp.
             */
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


        val intentUuTien =
            taoIntent(
                packageDialer
            )


        val intentThucThi =
            if (
                intentUuTien.resolveActivity(
                    appContext.packageManager
                ) !=
                null
            ) {

                intentUuTien

            } else {

                taoIntent(
                    packageName = null
                )
            }


        if (
            intentThucThi.resolveActivity(
                appContext.packageManager
            ) ==
            null
        ) {

            return false
        }


        return try {

            appContext.startActivity(
                intentThucThi
            )

            true

        } catch (_: Exception) {

            false
        }
    }


    // =========================================================
    // CONTACT STATE
    // =========================================================

    private fun dongBoLienHeDangChonNeuCan() {

        /*
         * Khi số đang hiển thị trên Dialer, snapshot contact phải ổn định.
         */
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

        /**
         * Package Dialer gần nhất FaceAccess đã xác nhận.
         * Giữ lại sau thao tác HỦY khi người dùng vẫn đang ở app Điện thoại.
         */
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
