// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.dieuphoi.hotro

import android.content.Context
import android.net.Uri
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

/**
 * Kho lưu liên hệ hỗ trợ cục bộ.
 *
 * Dữ liệu cũ chưa có "mo_ta" vẫn đọc bình thường vì
 * trường này dùng optString(..., "").
 */
class KhoLienHeHoTro(
    context: Context
) {

    private val appContext =
        context.applicationContext

    private val preferences =
        appContext.getSharedPreferences(
            TEN_PREFERENCES,
            Context.MODE_PRIVATE
        )


    fun layTatCa(): List<NguoiHoTro> {

        val json =
            preferences.getString(
                KHOA_DANH_SACH,
                null
            ) ?: return emptyList()


        return try {

            val array =
                JSONArray(json)


            buildList {

                for (index in 0 until array.length()) {

                    val item =
                        array.getJSONObject(index)


                    add(
                        NguoiHoTro(
                            id =
                                item.getLong(
                                    KHOA_ID
                                ),
                            ten =
                                item.getString(
                                    KHOA_TEN
                                ),
                            soDienThoai =
                                item.getString(
                                    KHOA_SO_DIEN_THOAI
                                ),
                            moTa =
                                item.optString(
                                    KHOA_MO_TA,
                                    ""
                                ),
                            anhUri =
                                item.optString(
                                    KHOA_ANH_URI,
                                    ""
                                ).takeIf {
                                    it.isNotBlank()
                                }
                        )
                    )
                }
            }

        } catch (_: Exception) {

            emptyList()
        }
    }


    fun layTheoId(
        id: Long
    ): NguoiHoTro? {

        return layTatCa()
            .firstOrNull {
                it.id == id
            }
    }


    fun them(
        ten: String,
        soDienThoai: String,
        moTa: String = "",
        anhUri: String? = null
    ): NguoiHoTro {

        val danhSach =
            layTatCa()
                .toMutableList()


        val nguoiMoi =
            NguoiHoTro(
                id =
                    taoIdMoi(
                        danhSach
                    ),
                ten =
                    ten.trim(),
                soDienThoai =
                    soDienThoai.trim(),
                moTa =
                    moTa.trim(),
                anhUri =
                    anhUri
            )


        danhSach.add(
            nguoiMoi
        )


        luu(
            danhSach
        )


        return nguoiMoi
    }


    fun capNhat(
        nguoiHoTro: NguoiHoTro
    ): Boolean {

        val danhSach =
            layTatCa()
                .toMutableList()


        val viTri =
            danhSach.indexOfFirst {
                it.id == nguoiHoTro.id
            }


        if (viTri < 0) {
            return false
        }


        danhSach[viTri] =
            nguoiHoTro.copy(
                ten =
                    nguoiHoTro.ten.trim(),
                soDienThoai =
                    nguoiHoTro.soDienThoai.trim(),
                moTa =
                    nguoiHoTro.moTa.trim()
            )


        luu(
            danhSach
        )


        return true
    }


    fun xoaTheoIds(
        ids: Set<Long>
    ) {

        if (ids.isEmpty()) {
            return
        }


        val danhSachHienTai =
            layTatCa()


        val lienHeBiXoa =
            danhSachHienTai
                .filter {
                    it.id in ids
                }


        val danhSachMoi =
            danhSachHienTai
                .filterNot {
                    it.id in ids
                }


        luu(
            danhSachMoi
        )


        // Chỉ xóa avatar nội bộ không còn được liên hệ nào sử dụng.
        val avatarConDuocSuDung =
            danhSachMoi
                .mapNotNull {
                    it.anhUri
                }
                .toSet()


        lienHeBiXoa
            .mapNotNull {
                it.anhUri
            }
            .filterNot {
                it in avatarConDuocSuDung
            }
            .distinct()
            .forEach { anhUri ->
                xoaAvatarNoiBoNeuCan(
                    anhUri
                )
            }
    }


    private fun xoaAvatarNoiBoNeuCan(
        anhUri: String
    ) {

        try {

            val uri =
                Uri.parse(
                    anhUri
                )


            if (uri.scheme != "file") {
                return
            }


            val duongDan =
                uri.path
                    ?: return


            val thuMucAvatar =
                File(
                    appContext.filesDir,
                    THU_MUC_AVATAR
                ).canonicalFile


            val fileAvatar =
                File(
                    duongDan
                ).canonicalFile


            val tienToThuMucAvatar =
                thuMucAvatar.path +
                        File.separator


            if (
                !fileAvatar.path.startsWith(
                    tienToThuMucAvatar
                )
            ) {
                return
            }


            if (fileAvatar.isFile) {
                fileAvatar.delete()
            }

        } catch (_: Exception) {

            // Dọn file là best-effort, không để lỗi I/O phá thao tác xóa liên hệ.
        }
    }


    private fun taoIdMoi(
        danhSach: List<NguoiHoTro>
    ): Long {

        val idLonNhat =
            danhSach
                .maxOfOrNull {
                    it.id
                } ?: 0L


        return idLonNhat + 1L
    }


    private fun luu(
        danhSach: List<NguoiHoTro>
    ) {

        val array =
            JSONArray()


        danhSach.forEach { nguoi ->

            val item =
                JSONObject().apply {

                    put(
                        KHOA_ID,
                        nguoi.id
                    )

                    put(
                        KHOA_TEN,
                        nguoi.ten
                    )

                    put(
                        KHOA_SO_DIEN_THOAI,
                        nguoi.soDienThoai
                    )

                    put(
                        KHOA_MO_TA,
                        nguoi.moTa
                    )

                    put(
                        KHOA_ANH_URI,
                        nguoi.anhUri ?: ""
                    )
                }


            array.put(
                item
            )
        }


        preferences
            .edit()
            .putString(
                KHOA_DANH_SACH,
                array.toString()
            )
            .apply()
    }


    companion object {

        private const val TEN_PREFERENCES =
            "faceaccess_lien_he_ho_tro"

        private const val KHOA_DANH_SACH =
            "danh_sach"

        private const val THU_MUC_AVATAR =
            "support_avatars"

        private const val KHOA_ID =
            "id"

        private const val KHOA_TEN =
            "ten"

        private const val KHOA_SO_DIEN_THOAI =
            "so_dien_thoai"

        private const val KHOA_MO_TA =
            "mo_ta"

        private const val KHOA_ANH_URI =
            "anh_uri"
    }
}