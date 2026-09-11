// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.huongdan

import androidx.annotation.RawRes

data class MucHuongDanCuChi(
    val soThuTu: Int,
    val tenCuChi: String,
    val moTaCuChi: String,
    val hanhDongCuChi: String,
    val goiYCuChi: String,
    val nhanCheDo: String,

    @param:RawRes
    val animationResId: Int,

    val lapLaiAnimation: Boolean = true
)