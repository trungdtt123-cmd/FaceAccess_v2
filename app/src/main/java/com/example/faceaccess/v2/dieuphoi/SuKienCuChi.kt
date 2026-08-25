package com.example.faceaccess.v2.dieuphoi

sealed class SuKienCuChi {

    data object NghiengTrai :
        SuKienCuChi()

    data object NghiengPhai :
        SuKienCuChi()

    data object MoMieng :
        SuKienCuChi()
}