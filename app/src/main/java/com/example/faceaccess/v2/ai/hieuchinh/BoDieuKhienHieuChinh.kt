// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.ai.hieuchinh

import com.example.faceaccess.v2.khuonmat.DuLieuKhuonMat
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max

enum class TrangThaiHieuChinh {
    CHUAN_BI,
    CHO_DUNG_TU_THE,
    DANG_GIU,
    HOAN_THANH
}

data class KetQuaDieuKhienHieuChinh(
    val trangThai: TrangThaiHieuChinh,
    val tienDoPhanTram: Int,
    val thongDiep: String,
    val soGiayChuanBiConLai: Int = 0
)

class BoDieuKhienHieuChinh(
    private val boThuThap: BoThuThapMauHieuChinh
) {

    private var buocHienTai: BuocHieuChinh? = null

    private var thoiDiemBatDauBuocMs = 0L
    private var thoiDiemLayMauGanNhatMs = 0L
    private var thoiDiemBatDauSaiTuTheMs = 0L

    private var duLieuTruoc: DuLieuKhuonMat? = null

    private var rollTrungTinh: Float? = null
    private var yawTrungTinh: Float? = null
    private var pitchTrungTinh: Float? = null

    private var matMoTrungTinh: Float? = null
    private var miengDongTrungTinh: Float? = null

    private enum class TrucHieuChinh {
        ROLL,
        YAW,
        PITCH
    }

    // Bắt đầu một bước hiệu chỉnh mới
    fun batDauBuoc(
        buoc: BuocHieuChinh,
        thoiGianMs: Long
    ) {
        buocHienTai = buoc

        thoiDiemBatDauBuocMs = thoiGianMs
        thoiDiemLayMauGanNhatMs = 0L
        thoiDiemBatDauSaiTuTheMs = 0L

        duLieuTruoc = null

        boThuThap.batDauBuoc(buoc)

        if (buoc != BuocHieuChinh.TRUNG_TINH) {
            capNhatMocTrungTinh()
        }
    }

    // Xử lý một frame trong lúc hiệu chỉnh
    fun xuLy(
        duLieu: DuLieuKhuonMat,
        thoiGianMs: Long
    ): KetQuaDieuKhienHieuChinh {

        val buoc =
            buocHienTai
                ?: return KetQuaDieuKhienHieuChinh(
                    trangThai = TrangThaiHieuChinh.CHO_DUNG_TU_THE,
                    tienDoPhanTram = 0,
                    thongDiep = "Chưa có bước hiệu chỉnh"
                )

        val daQua =
            thoiGianMs - thoiDiemBatDauBuocMs

        if (daQua < THOI_GIAN_CHUAN_BI_MS) {

            duLieuTruoc = duLieu

            val conLaiMs =
                THOI_GIAN_CHUAN_BI_MS - daQua

            val conLaiGiay =
                ceil(
                    conLaiMs / 1000.0
                ).toInt()

            return KetQuaDieuKhienHieuChinh(
                trangThai = TrangThaiHieuChinh.CHUAN_BI,
                tienDoPhanTram = 0,
                thongDiep = "Chuẩn bị...",
                soGiayChuanBiConLai = conLaiGiay
            )
        }

        val dungTuThe =
            kiemTraDungTuThe(
                buoc = buoc,
                duLieu = duLieu
            )

        if (!dungTuThe) {

            xuLySaiTuThe(
                buoc = buoc,
                thoiGianMs = thoiGianMs
            )

            duLieuTruoc = duLieu

            return KetQuaDieuKhienHieuChinh(
                trangThai = TrangThaiHieuChinh.CHO_DUNG_TU_THE,
                tienDoPhanTram = tienDoPhanTram(),
                thongDiep = thongDiepChoDungTuThe(buoc)
            )
        }

        thoiDiemBatDauSaiTuTheMs = 0L

        val duKhoangCachLayMau =
            thoiDiemLayMauGanNhatMs == 0L ||
                    thoiGianMs - thoiDiemLayMauGanNhatMs >=
                    KHOANG_CACH_LAY_MAU_MS

        if (duKhoangCachLayMau) {

            val daThem =
                boThuThap.themMau(duLieu)

            if (daThem) {
                thoiDiemLayMauGanNhatMs = thoiGianMs
            }
        }

        duLieuTruoc = duLieu

        if (boThuThap.daDuMauBuocHienTai()) {

            if (buoc == BuocHieuChinh.TRUNG_TINH) {
                capNhatMocTrungTinh()
            }

            return KetQuaDieuKhienHieuChinh(
                trangThai = TrangThaiHieuChinh.HOAN_THANH,
                tienDoPhanTram = 100,
                thongDiep = "Hoàn thành"
            )
        }

        return KetQuaDieuKhienHieuChinh(
            trangThai = TrangThaiHieuChinh.DANG_GIU,
            tienDoPhanTram = tienDoPhanTram(),
            thongDiep = "Đúng rồi, giữ nguyên"
        )
    }

    // Kết thúc bước hiện tại
    fun ketThucBuoc() {
        boThuThap.ketThucBuoc()

        buocHienTai = null
        thoiDiemBatDauBuocMs = 0L
        thoiDiemLayMauGanNhatMs = 0L
        thoiDiemBatDauSaiTuTheMs = 0L
        duLieuTruoc = null
    }

    // Đặt lại toàn bộ trạng thái điều khiển
    fun datLai() {
        buocHienTai = null

        thoiDiemBatDauBuocMs = 0L
        thoiDiemLayMauGanNhatMs = 0L
        thoiDiemBatDauSaiTuTheMs = 0L

        duLieuTruoc = null

        rollTrungTinh = null
        yawTrungTinh = null
        pitchTrungTinh = null

        matMoTrungTinh = null
        miengDongTrungTinh = null
    }

    // Tên lớn hiển thị trên giao diện
    fun layTenDongTac(
        buoc: BuocHieuChinh
    ): String {
        return when (buoc) {
            BuocHieuChinh.TRUNG_TINH -> "NHÌN THẲNG"
            BuocHieuChinh.QUAY_TRAI -> "QUAY ĐẦU SANG TRÁI"
            BuocHieuChinh.QUAY_PHAI -> "QUAY ĐẦU SANG PHẢI"
            BuocHieuChinh.NHIN_LEN -> "NGẨNG ĐẦU LÊN"
            BuocHieuChinh.NHIN_XUONG -> "CÚI ĐẦU XUỐNG"
            BuocHieuChinh.NGHIENG_TRAI -> "NGHIÊNG ĐẦU SANG TRÁI"
            BuocHieuChinh.NGHIENG_PHAI -> "NGHIÊNG ĐẦU SANG PHẢI"
            BuocHieuChinh.NHAM_HAI_MAT -> "NHẮM HAI MẮT"
            BuocHieuChinh.MO_MIENG -> "MỞ MIỆNG"
        }
    }

    // Hướng dẫn ngắn cho người dùng
    fun layHuongDan(
        buoc: BuocHieuChinh
    ): String {
        return when (buoc) {
            BuocHieuChinh.TRUNG_TINH ->
                "Nhìn thẳng vào camera, mở mắt và giữ đầu tự nhiên"

            BuocHieuChinh.QUAY_TRAI ->
                "Quay đầu sang trái và giữ nguyên khi hệ thống báo đúng"

            BuocHieuChinh.QUAY_PHAI ->
                "Quay đầu sang phải và giữ nguyên khi hệ thống báo đúng"

            BuocHieuChinh.NHIN_LEN ->
                "Ngẩng đầu lên và giữ nguyên khi hệ thống báo đúng"

            BuocHieuChinh.NHIN_XUONG ->
                "Cúi đầu xuống và giữ nguyên khi hệ thống báo đúng"

            BuocHieuChinh.NGHIENG_TRAI ->
                "Nghiêng đầu sang trái và giữ nguyên khi hệ thống báo đúng"

            BuocHieuChinh.NGHIENG_PHAI ->
                "Nghiêng đầu sang phải và giữ nguyên khi hệ thống báo đúng"

            BuocHieuChinh.NHAM_HAI_MAT ->
                "Nhắm cả hai mắt và giữ nguyên khi hệ thống báo đúng"

            BuocHieuChinh.MO_MIENG ->
                "Mở miệng rõ ràng và giữ nguyên khi hệ thống báo đúng"
        }
    }

    // Kiểm tra người dùng đã thực hiện đúng động tác chưa
    private fun kiemTraDungTuThe(
        buoc: BuocHieuChinh,
        duLieu: DuLieuKhuonMat
    ): Boolean {

        return when (buoc) {

            BuocHieuChinh.TRUNG_TINH ->
                kiemTraTrungTinh(duLieu)

            BuocHieuChinh.QUAY_TRAI ->
                kiemTraTrucChiPhoi(
                    duLieu = duLieu,
                    truc = TrucHieuChinh.YAW,
                    huongDuong = true,
                    nguong = NGUONG_XAC_NHAN_YAW,
                    tyLeChiPhoi = TY_LE_CHI_PHOI_HUONG_DAU
                )

            BuocHieuChinh.QUAY_PHAI ->
                kiemTraTrucChiPhoi(
                    duLieu = duLieu,
                    truc = TrucHieuChinh.YAW,
                    huongDuong = false,
                    nguong = NGUONG_XAC_NHAN_YAW,
                    tyLeChiPhoi = TY_LE_CHI_PHOI_HUONG_DAU
                )

            BuocHieuChinh.NHIN_LEN ->
                kiemTraTrucChiPhoi(
                    duLieu = duLieu,
                    truc = TrucHieuChinh.PITCH,
                    huongDuong = true,
                    nguong = NGUONG_XAC_NHAN_PITCH,
                    tyLeChiPhoi = TY_LE_CHI_PHOI_HUONG_DAU
                )

            BuocHieuChinh.NHIN_XUONG ->
                kiemTraTrucChiPhoi(
                    duLieu = duLieu,
                    truc = TrucHieuChinh.PITCH,
                    huongDuong = false,
                    nguong = NGUONG_XAC_NHAN_PITCH,
                    tyLeChiPhoi = TY_LE_CHI_PHOI_HUONG_DAU
                )

            BuocHieuChinh.NGHIENG_TRAI ->
                kiemTraTrucChiPhoi(
                    duLieu = duLieu,
                    truc = TrucHieuChinh.ROLL,
                    huongDuong = false,
                    nguong = NGUONG_XAC_NHAN_ROLL,
                    tyLeChiPhoi = TY_LE_CHI_PHOI_NGHIENG_DAU
                )

            BuocHieuChinh.NGHIENG_PHAI ->
                kiemTraTrucChiPhoi(
                    duLieu = duLieu,
                    truc = TrucHieuChinh.ROLL,
                    huongDuong = true,
                    nguong = NGUONG_XAC_NHAN_ROLL,
                    tyLeChiPhoi = TY_LE_CHI_PHOI_NGHIENG_DAU
                )

            BuocHieuChinh.NHAM_HAI_MAT ->
                kiemTraNhamHaiMat(duLieu)

            BuocHieuChinh.MO_MIENG ->
                kiemTraMoMieng(duLieu)
        }
    }

    // Cử chỉ hiệu chỉnh phải đủ biên độ và đúng trục chi phối.
    private fun kiemTraTrucChiPhoi(
        duLieu: DuLieuKhuonMat,
        truc: TrucHieuChinh,
        huongDuong: Boolean,
        nguong: Float,
        tyLeChiPhoi: Float
    ): Boolean {

        val roll = duLieu.roll ?: return false
        val yaw = duLieu.yaw ?: return false
        val pitch = duLieu.pitch ?: return false

        val mocRoll = rollTrungTinh ?: return false
        val mocYaw = yawTrungTinh ?: return false
        val mocPitch = pitchTrungTinh ?: return false

        val deltaRoll = roll - mocRoll
        val deltaYaw = yaw - mocYaw
        val deltaPitch = pitch - mocPitch

        val giaTriChinh =
            when (truc) {
                TrucHieuChinh.ROLL -> deltaRoll
                TrucHieuChinh.YAW -> deltaYaw
                TrucHieuChinh.PITCH -> deltaPitch
            }

        val dungHuong =
            if (huongDuong) {
                giaTriChinh >= nguong
            } else {
                giaTriChinh <= -nguong
            }

        if (!dungHuong) {
            return false
        }

        val giaTriChinhAbs = abs(giaTriChinh)

        val (phuThuNhat, phuThuHai) =
            when (truc) {
                TrucHieuChinh.ROLL -> abs(deltaYaw) to abs(deltaPitch)
                TrucHieuChinh.YAW -> abs(deltaRoll) to abs(deltaPitch)
                TrucHieuChinh.PITCH -> abs(deltaRoll) to abs(deltaYaw)
            }

        return giaTriChinhAbs >= phuThuNhat * tyLeChiPhoi &&
                giaTriChinhAbs >= phuThuHai * tyLeChiPhoi
    }

    // Trung tính phải ổn định, mở mắt và khép miệng
    private fun kiemTraTrungTinh(
        duLieu: DuLieuKhuonMat
    ): Boolean {

        val truoc =
            duLieuTruoc
                ?: return false

        val roll = duLieu.roll ?: return false
        val yaw = duLieu.yaw ?: return false
        val pitch = duLieu.pitch ?: return false

        val rollTruoc = truoc.roll ?: return false
        val yawTruoc = truoc.yaw ?: return false
        val pitchTruoc = truoc.pitch ?: return false

        val matTrai =
            duLieu.doNhamMatTrai
                ?: return false

        val matPhai =
            duLieu.doNhamMatPhai
                ?: return false

        val mieng =
            duLieu.doMoMieng
                ?: return false

        val gocOnDinh =
            abs(roll - rollTruoc) <= NGUONG_ON_DINH_GOC &&
                    abs(yaw - yawTruoc) <= NGUONG_ON_DINH_GOC &&
                    abs(pitch - pitchTruoc) <= NGUONG_ON_DINH_GOC

        val matDangMo =
            matTrai < NGUONG_MAT_DONG_KHI_TRUNG_TINH &&
                    matPhai < NGUONG_MAT_DONG_KHI_TRUNG_TINH

        val miengDangDong =
            mieng < NGUONG_MIENG_MO_KHI_TRUNG_TINH

        return gocOnDinh &&
                matDangMo &&
                miengDangDong
    }

    // Hai mắt phải đóng rõ ràng so với trạng thái trung tính
    private fun kiemTraNhamHaiMat(
        duLieu: DuLieuKhuonMat
    ): Boolean {

        val trai =
            duLieu.doNhamMatTrai
                ?: return false

        val phai =
            duLieu.doNhamMatPhai
                ?: return false

        val trungBinh =
            (trai + phai) / 2f

        val moc =
            matMoTrungTinh
                ?: 0f

        val nguong =
            max(
                NGUONG_XAC_NHAN_NHAM_MAT,
                moc + KHOANG_CACH_NHAM_MAT_TOI_THIEU
            )

        return trai >= nguong &&
                phai >= nguong &&
                trungBinh >= nguong
    }

    // Miệng phải mở rõ ràng so với trạng thái trung tính
    private fun kiemTraMoMieng(
        duLieu: DuLieuKhuonMat
    ): Boolean {

        val mieng =
            duLieu.doMoMieng
                ?: return false

        val moc =
            miengDongTrungTinh
                ?: 0f

        val nguong =
            max(
                NGUONG_XAC_NHAN_MO_MIENG,
                moc + KHOANG_CACH_MO_MIENG_TOI_THIEU
            )

        return mieng >= nguong
    }

    // Nếu sai tư thế lâu thì thu lại bước từ đầu
    private fun xuLySaiTuThe(
        buoc: BuocHieuChinh,
        thoiGianMs: Long
    ) {

        if (
            boThuThap.soMauDaThu(buoc) == 0
        ) {
            thoiDiemBatDauSaiTuTheMs = 0L
            return
        }

        if (thoiDiemBatDauSaiTuTheMs == 0L) {
            thoiDiemBatDauSaiTuTheMs = thoiGianMs
            return
        }

        if (
            thoiGianMs - thoiDiemBatDauSaiTuTheMs >=
            THOI_GIAN_SAI_TU_THE_CHO_PHEP_MS
        ) {

            boThuThap.batDauBuoc(buoc)

            thoiDiemLayMauGanNhatMs = 0L
            thoiDiemBatDauSaiTuTheMs = 0L
        }
    }

    // Cập nhật tư thế trung tính sau bước đầu tiên
    private fun capNhatMocTrungTinh() {

        val mau =
            boThuThap.layMau(
                BuocHieuChinh.TRUNG_TINH
            )

        if (mau.isEmpty()) {
            return
        }

        rollTrungTinh =
            trungVi(
                mau.mapNotNull {
                    it.roll
                }
            )

        yawTrungTinh =
            trungVi(
                mau.mapNotNull {
                    it.yaw
                }
            )

        pitchTrungTinh =
            trungVi(
                mau.mapNotNull {
                    it.pitch
                }
            )

        matMoTrungTinh =
            trungVi(
                mau.flatMap {
                    listOfNotNull(
                        it.doNhamMatTrai,
                        it.doNhamMatPhai
                    )
                }
            )

        miengDongTrungTinh =
            trungVi(
                mau.mapNotNull {
                    it.doMoMieng
                }
            )
    }

    private fun tienDoPhanTram(): Int {
        return (
                boThuThap
                    .tienDoBuocHienTai() *
                        100f
                )
            .toInt()
            .coerceIn(
                0,
                100
            )
    }

    private fun thongDiepChoDungTuThe(
        buoc: BuocHieuChinh
    ): String {
        return when (buoc) {
            BuocHieuChinh.TRUNG_TINH ->
                "Nhìn thẳng, mở mắt và giữ đầu ổn định"

            BuocHieuChinh.QUAY_TRAI ->
                "Hãy quay đầu sang trái"

            BuocHieuChinh.QUAY_PHAI ->
                "Hãy quay đầu sang phải"

            BuocHieuChinh.NHIN_LEN ->
                "Hãy ngẩng đầu lên"

            BuocHieuChinh.NHIN_XUONG ->
                "Hãy cúi đầu xuống"

            BuocHieuChinh.NGHIENG_TRAI ->
                "Hãy nghiêng đầu sang trái"

            BuocHieuChinh.NGHIENG_PHAI ->
                "Hãy nghiêng đầu sang phải"

            BuocHieuChinh.NHAM_HAI_MAT ->
                "Hãy nhắm cả hai mắt"

            BuocHieuChinh.MO_MIENG ->
                "Hãy mở miệng rõ hơn"
        }
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

        // Cho người dùng thời gian đọc hướng dẫn
        private const val THOI_GIAN_CHUAN_BI_MS =
            1500L

        // Chỉ lấy tối đa khoảng 10 mẫu mỗi giây
        private const val KHOANG_CACH_LAY_MAU_MS =
            100L

        // Sai tư thế quá lâu thì thu lại bước đó
        private const val THOI_GIAN_SAI_TU_THE_CHO_PHEP_MS =
            500L

        // Ngưỡng nhẹ để xác nhận người dùng đã bắt đầu cử chỉ
        private const val NGUONG_XAC_NHAN_YAW =
            6f

        private const val NGUONG_XAC_NHAN_PITCH =
            5f

        private const val NGUONG_XAC_NHAN_ROLL =
            6f

        // Đồng bộ với quy tắc trục chi phối của detector khi chạy thật
        private const val TY_LE_CHI_PHOI_HUONG_DAU =
            1.05f

        private const val TY_LE_CHI_PHOI_NGHIENG_DAU =
            1f

        // Độ dao động cho phép khi nhìn thẳng
        private const val NGUONG_ON_DINH_GOC =
            2.5f

        private const val NGUONG_MAT_DONG_KHI_TRUNG_TINH =
            0.45f

        private const val NGUONG_MIENG_MO_KHI_TRUNG_TINH =
            0.20f

        private const val NGUONG_XAC_NHAN_NHAM_MAT =
            0.55f

        private const val KHOANG_CACH_NHAM_MAT_TOI_THIEU =
            0.25f

        private const val NGUONG_XAC_NHAN_MO_MIENG =
            0.25f

        private const val KHOANG_CACH_MO_MIENG_TOI_THIEU =
            0.15f
    }
}
