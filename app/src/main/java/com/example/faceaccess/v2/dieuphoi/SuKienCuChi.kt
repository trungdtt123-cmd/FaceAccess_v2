package com.example.faceaccess.v2.dieuphoi

sealed class SuKienCuChi {

    /**
     * Nghiêng đầu vật lý sang trái.
     *
     * Sau này được ánh xạ thành HOME.
     */
    data object NghiengTrai : SuKienCuChi()

    /**
     * Nghiêng đầu vật lý sang phải.
     *
     * Sau này được ánh xạ thành đổi chế độ.
     */
    data object NghiengPhai : SuKienCuChi()
}