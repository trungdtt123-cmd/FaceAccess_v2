package com.example.faceaccess.v2.dieuphoi

import com.example.faceaccess.v2.cuchi.huongdau.HuongDau

sealed class SuKienCuChi {
    data object NghiengTrai : SuKienCuChi()
    data object NghiengPhai : SuKienCuChi()
    data object MoMieng : SuKienCuChi()
    data object NhamHaiMat : SuKienCuChi()
    data object MoMiengHaiLan : SuKienCuChi()

    data class DieuHuongDau(
        val huong: HuongDau
    ) : SuKienCuChi()
}
