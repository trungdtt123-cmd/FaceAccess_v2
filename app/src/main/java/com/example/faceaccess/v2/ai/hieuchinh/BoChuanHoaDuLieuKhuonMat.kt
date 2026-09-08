package com.example.faceaccess.v2.ai.hieuchinh

import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhChuanHoa
import com.example.faceaccess.v2.khuonmat.DuLieuKhuonMat

class BoChuanHoaDuLieuKhuonMat(
    private val cauHinh: CauHinhChuanHoa
) {

    // Bù sai lệch tư thế trung tính
    fun chuanHoa(
        duLieu: DuLieuKhuonMat
    ): DuLieuKhuonMat {

        return duLieu.copy(

            roll =
                duLieu.roll?.minus(
                    cauHinh.lechRollTrungTinh
                ),

            yaw =
                duLieu.yaw?.minus(
                    cauHinh.lechYawTrungTinh
                ),

            pitch =
                duLieu.pitch?.minus(
                    cauHinh.lechPitchTrungTinh
                )
        )
    }
}