package com.example.faceaccess.v2.dieuphoi

import com.example.faceaccess.v2.cuchi.huongdau.HuongDau

sealed class SuKienCuChi {

    /**
     * ROLL trái là gesture toàn cục.
     */
    data object NghiengTrai :
        SuKienCuChi()

    /**
     * ROLL phải là gesture toàn cục.
     */
    data object NghiengPhai :
        SuKienCuChi()

    /**
     * Mở miệng là gesture toàn cục.
     */
    data object MoMieng :
        SuKienCuChi()

    /**
     * YAW / PITCH chỉ mô tả hướng đầu.
     *
     * Event này chưa quyết định hành động.
     * Hành động thật sẽ phụ thuộc mode hiện tại.
     */
    data class DieuHuongDau(
        val huong: HuongDau
    ) : SuKienCuChi()
}