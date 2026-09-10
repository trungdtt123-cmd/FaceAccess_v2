// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.huongdan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.faceaccess.v2.R

class HuongDanCuChiAdapter(
    private var danhSach: List<MucHuongDanCuChi>
) : RecyclerView.Adapter<HuongDanCuChiAdapter.HuongDanViewHolder>() {

    private var viTriDangHoatDong = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HuongDanViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_huong_dan_cu_chi,
                    parent,
                    false
                )

        return HuongDanViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HuongDanViewHolder,
        position: Int
    ) {

        val muc =
            danhSach[position]

        holder.ganDuLieu(
            muc = muc,
            dangHoatDong =
                position == viTriDangHoatDong
        )
    }

    override fun getItemCount(): Int {
        return danhSach.size
    }

    fun capNhatDanhSach(
        danhSachMoi: List<MucHuongDanCuChi>
    ) {

        danhSach =
            danhSachMoi

        viTriDangHoatDong =
            0

        notifyDataSetChanged()
    }

    fun datTrangDangXem(
        viTriMoi: Int
    ) {

        if (
            viTriMoi < 0 ||
            viTriMoi >= danhSach.size
        ) {
            return
        }

        if (
            viTriMoi ==
            viTriDangHoatDong
        ) {
            return
        }

        val viTriCu =
            viTriDangHoatDong

        viTriDangHoatDong =
            viTriMoi

        notifyItemChanged(
            viTriCu
        )

        notifyItemChanged(
            viTriMoi
        )
    }

    inner class HuongDanViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val txtSoThuTuCuChi:
                TextView =
            itemView.findViewById(
                R.id.txtSoThuTuCuChi
            )

        private val txtTenCuChi:
                TextView =
            itemView.findViewById(
                R.id.txtTenCuChi
            )

        private val txtMoTaCuChi:
                TextView =
            itemView.findViewById(
                R.id.txtMoTaCuChi
            )

        private val txtNhanCheDo:
                TextView =
            itemView.findViewById(
                R.id.txtNhanCheDo
            )

        private val txtHanhDongCuChi:
                TextView =
            itemView.findViewById(
                R.id.txtHanhDongCuChi
            )

        private val txtGoiYCuChi:
                TextView =
            itemView.findViewById(
                R.id.txtGoiYCuChi
            )

        private val animationHuongDan:
                LottieAnimationView =
            itemView.findViewById(
                R.id.animationHuongDan
            )

        fun ganDuLieu(
            muc: MucHuongDanCuChi,
            dangHoatDong: Boolean
        ) {

            txtSoThuTuCuChi.text =
                muc.soThuTu.toString()

            txtTenCuChi.text =
                muc.tenCuChi

            txtMoTaCuChi.text =
                muc.moTaCuChi

            txtNhanCheDo.text =
                muc.nhanCheDo

            txtHanhDongCuChi.text =
                muc.hanhDongCuChi

            txtGoiYCuChi.text =
                muc.goiYCuChi

            animationHuongDan.cancelAnimation()

            animationHuongDan.setAnimation(
                muc.animationResId
            )

            animationHuongDan.repeatCount =
                if (muc.lapLaiAnimation) {
                    -1
                } else {
                    0
                }

            if (dangHoatDong) {

                animationHuongDan.progress =
                    0f

                animationHuongDan.playAnimation()

            } else {

                animationHuongDan.pauseAnimation()

                animationHuongDan.progress =
                    0f
            }
        }
    }
}