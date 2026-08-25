package com.example.faceaccess.v2.dichvu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.example.faceaccess.v2.R
import com.example.faceaccess.v2.camera.QuanLyCamera
import com.example.faceaccess.v2.cuchi.nghiengdau.HuongNghiengDau
import com.example.faceaccess.v2.cuchi.nghiengdau.NhanDienNghiengDau
import com.example.faceaccess.v2.khuonmat.TrichXuatDuLieuKhuonMat
import com.example.faceaccess.v2.khuonmat.PhanTichKhungHinhKhuonMat
import com.example.faceaccess.v2.khuonmat.XuLyKhuonMat
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class DichVuTheoDoiFaceAccess :
    Service(),
    LifecycleOwner {

    // =========================================================
    // LIFECYCLE
    // =========================================================

    private val lifecycleRegistry =
        LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry


    // =========================================================
    // CAMERA + MEDIAPIPE NỀN
    // =========================================================

    private lateinit var xuLyKhuonMat:
            XuLyKhuonMat

    private lateinit var phanTichKhungHinhKhuonMat:
            PhanTichKhungHinhKhuonMat

    private lateinit var quanLyCamera:
            QuanLyCamera

    private lateinit var trichXuatDuLieuKhuonMat:
            TrichXuatDuLieuKhuonMat

    private lateinit var nhanDienNghiengDau:
            NhanDienNghiengDau

    /**
     * true khi Camera nền đã bind thành công và Service
     * đang thực sự sở hữu Camera.
     */
    @Volatile
    private var cameraNenDangBat =
        false

    /**
     * ProcessCameraProvider.bindToLifecycle() là bất đồng bộ
     * trong QuanLyCamera.batCamera().
     *
     * Cờ này ngăn lệnh BAT/TAT chồng lên nhau trong lúc Camera
     * nền vẫn đang được khởi động.
     */
    @Volatile
    private var cameraNenDangKhoiDong =
        false

    /**
     * Có thể Activity quay lại rất nhanh trong lúc Service
     * vẫn đang chờ Camera nền bind xong.
     *
     * Khi đó Service không được báo "đã tắt" quá sớm.
     * Nó phải chờ bind hoàn tất rồi nhả Camera ngay.
     */
    @Volatile
    private var yeuCauTatSauKhiKhoiDong =
        false

    /**
     * One-shot guard cho ACK CAMERA_NEN_DA_TAT.
     *
     * Mỗi yêu cầu TẮT từ Activity chỉ được phép phát
     * đúng một broadcast xác nhận DA_TAT.
     */
    @Volatile
    private var dangChoXacNhanCameraNenDaTat =
        false

    private var thoiGianLogGanNhat =
        0L


    // =========================================================
    // SERVICE CREATE
    // =========================================================

    override fun onCreate() {
        super.onCreate()

        lifecycleRegistry.currentState =
            Lifecycle.State.CREATED

        Log.d(
            TAG,
            "Dich vu theo doi da duoc tao"
        )

        taoKenhThongBao()

        batForeground()

        /*
         * Chỉ khởi tạo pipeline.
         * Camera nền chỉ bật khi Activity đã nhả Camera
         * và gửi HANH_DONG_BAT_CAMERA_NEN.
         */
        khoiTaoNhanDienCuChiNen()

        khoiTaoXuLyKhuonMatNen()

        khoiTaoCameraNen()
    }


    // =========================================================
    // SERVICE START
    // =========================================================

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        lifecycleRegistry.currentState =
            Lifecycle.State.STARTED

        Log.d(
            TAG,
            "Dich vu theo doi dang chay"
        )

        when (intent?.action) {

            HANH_DONG_BAT_CAMERA_NEN -> {

                Log.d(
                    TAG_CAMERA_NEN,
                    "Nhan yeu cau BAT Camera nen"
                )

                batCameraNen()
            }


            HANH_DONG_TAT_CAMERA_NEN -> {

                if (dangChoXacNhanCameraNenDaTat) {

                    Log.d(
                        TAG_BAN_GIAO_CAMERA,
                        "Dang cho ACK DA_TAT - bo qua lenh TAT trung lap"
                    )

                } else {

                    dangChoXacNhanCameraNenDaTat =
                        true

                    Log.d(
                        TAG_CAMERA_NEN,
                        "Nhan yeu cau TAT Camera nen"
                    )

                    tatCameraNen()
                }
            }
        }

        return START_STICKY
    }


    // =========================================================
    // NHẬN DIỆN CỬ CHỈ NỀN
    // =========================================================

    private fun khoiTaoNhanDienCuChiNen() {

        trichXuatDuLieuKhuonMat =
            TrichXuatDuLieuKhuonMat()

        nhanDienNghiengDau =
            NhanDienNghiengDau { huong ->

                when (huong) {

                    HuongNghiengDau.TRAI -> {

                        Log.d(
                            TAG_CU_CHI_NEN,
                            "NEN: NGHIENG TRAI"
                        )
                    }

                    HuongNghiengDau.PHAI -> {

                        Log.d(
                            TAG_CU_CHI_NEN,
                            "NEN: NGHIENG PHAI"
                        )
                    }
                }
            }
    }


    // =========================================================
    // KHỞI TẠO MEDIAPIPE NỀN
    // =========================================================

    private fun khoiTaoXuLyKhuonMatNen() {

        xuLyKhuonMat =
            XuLyKhuonMat(
                context = this,
                langNghe =
                    object :
                        XuLyKhuonMat.LangNgheXuLyKhuonMat {

                        override fun khiKhoiTaoThanhCong() {

                            Log.d(
                                TAG_CAMERA_NEN,
                                "MediaPipe nen da san sang"
                            )
                        }


                        override fun khiCoKetQua(
                            result: FaceLandmarkerResult,
                            chieuRongAnh: Int,
                            chieuCaoAnh: Int
                        ) {

                            if (!cameraNenDangBat) {
                                return
                            }

                            val hienTai =
                                SystemClock.uptimeMillis()

                            val duLieu =
                                trichXuatDuLieuKhuonMat
                                    .trichXuat(result)

                            nhanDienNghiengDau.capNhat(
                                roll = duLieu.roll,
                                yaw = duLieu.yaw,
                                pitch = duLieu.pitch,
                                thoiGianMs = hienTai
                            )

                            /*
                             * Chỉ log tối đa 1 lần/giây để kiểm tra
                             * pipeline nền mà không spam Logcat.
                             */
                            if (
                                hienTai -
                                thoiGianLogGanNhat >=
                                KHOANG_LOG_CAMERA_NEN_MS
                            ) {

                                thoiGianLogGanNhat =
                                    hienTai

                                Log.d(
                                    TAG_CAMERA_NEN,
                                    "Camera nen dang phat hien khuon mat"
                                )
                            }
                        }


                        override fun khiKhongThayKhuonMat() {

                            if (!cameraNenDangBat) {
                                return
                            }

                            nhanDienNghiengDau.datLai()

                            val hienTai =
                                SystemClock.uptimeMillis()

                            if (
                                hienTai -
                                thoiGianLogGanNhat >=
                                KHOANG_LOG_CAMERA_NEN_MS
                            ) {

                                thoiGianLogGanNhat =
                                    hienTai

                                Log.d(
                                    TAG_CAMERA_NEN,
                                    "Camera nen khong thay khuon mat"
                                )
                            }
                        }


                        override fun khiCoLoi(
                            thongBao: String
                        ) {

                            Log.e(
                                TAG_CAMERA_NEN,
                                "Loi MediaPipe nen: $thongBao"
                            )
                        }
                    }
            )


        phanTichKhungHinhKhuonMat =
            PhanTichKhungHinhKhuonMat(
                xuLyKhuonMat =
                    xuLyKhuonMat,

                laCameraTruoc =
                    true
            )
    }


    // =========================================================
    // KHỞI TẠO CAMERA NỀN
    // =========================================================

    private fun khoiTaoCameraNen() {

        quanLyCamera =
            QuanLyCamera(
                context = this,
                lifecycleOwner = this,
                previewView = null,
                boPhanTichKhungHinh =
                    phanTichKhungHinhKhuonMat
            )
    }


    // =========================================================
    // BẬT CAMERA NỀN
    // =========================================================

    private fun batCameraNen() {

        if (cameraNenDangBat) {

            Log.d(
                TAG_CAMERA_NEN,
                "Camera nen da bat san - bo qua"
            )

            return
        }

        if (cameraNenDangKhoiDong) {

            Log.d(
                TAG_CAMERA_NEN,
                "Camera nen dang khoi dong - bo qua lenh BAT lap"
            )

            return
        }

        cameraNenDangKhoiDong =
            true

        yeuCauTatSauKhiKhoiDong =
            false

        thoiGianLogGanNhat =
            0L

        quanLyCamera.batCamera(

            khiThanhCong = {

                cameraNenDangKhoiDong =
                    false

                cameraNenDangBat =
                    true

                nhanDienNghiengDau.datLai()

                Log.d(
                    TAG_CAMERA_NEN,
                    "Camera nen da bat thanh cong"
                )

                if (yeuCauTatSauKhiKhoiDong) {

                    /*
                     * Activity đã quay lại trong lúc Camera nền
                     * còn đang bind. Nhả Camera ngay và chỉ báo
                     * DA_TAT sau khi unbind đã được gọi.
                     */
                    yeuCauTatSauKhiKhoiDong =
                        false

                    Log.d(
                        TAG_BAN_GIAO_CAMERA,
                        "Co yeu cau TAT trong luc dang BAT -> nha Camera ngay"
                    )

                    tatCameraNenVaBaoDaTat()

                } else {

                    guiBroadcastCameraNenDaBat()
                }
            },


            khiLoi = { exception ->

                cameraNenDangKhoiDong =
                    false

                cameraNenDangBat =
                    false

                val dangChoTat =
                    yeuCauTatSauKhiKhoiDong

                yeuCauTatSauKhiKhoiDong =
                    false

                Log.e(
                    TAG_CAMERA_NEN,
                    "Khong the bat Camera nen",
                    exception
                )

                /*
                 * Nếu Activity đang chờ Service nhả Camera,
                 * Camera nền khởi động thất bại đồng nghĩa
                 * Service hiện không sở hữu Camera.
                 */
                if (dangChoTat) {

                    guiBroadcastCameraNenDaTatMotLan()
                }
            }
        )
    }


    // =========================================================
    // TẮT CAMERA NỀN
    // =========================================================

    private fun tatCameraNen() {

        if (cameraNenDangKhoiDong) {

            /*
             * KHÔNG broadcast DA_TAT ngay ở đây.
             *
             * Nếu báo sớm, Activity có thể bind Camera trong khi
             * callback bật Camera nền vẫn chưa kết thúc, tạo race.
             */
            yeuCauTatSauKhiKhoiDong =
                true

            Log.d(
                TAG_BAN_GIAO_CAMERA,
                "Camera nen dang khoi dong -> cho khoi dong xong roi tat"
            )

            return
        }


        if (!cameraNenDangBat) {

            /*
             * Service không sở hữu Camera.
             * Có thể xác nhận ngay cho Activity.
             */
            Log.d(
                TAG_BAN_GIAO_CAMERA,
                "Camera nen dang tat san -> xac nhan DA_TAT"
            )

            guiBroadcastCameraNenDaTatMotLan()

            return
        }


        tatCameraNenVaBaoDaTat()
    }


    /**
     * Nhả Camera của Service rồi mới gửi ACK cho Activity.
     */
    private fun tatCameraNenVaBaoDaTat() {

        cameraNenDangBat =
            false

        nhanDienNghiengDau.datLai()

        quanLyCamera.tatCamera()

        Log.d(
            TAG_CAMERA_NEN,
            "Camera nen da tat"
        )

        guiBroadcastCameraNenDaTatMotLan()
    }


    // =========================================================
    // BROADCAST BÀN GIAO CAMERA
    // =========================================================

    private fun guiBroadcastCameraNenDaBat() {

        val intent =
            Intent(
                HANH_DONG_CAMERA_NEN_DA_BAT
            ).apply {

                /*
                 * Chỉ cho chính package FaceAccess nhận broadcast.
                 */
                setPackage(
                    packageName
                )
            }

        sendBroadcast(
            intent
        )

        Log.d(
            TAG_BAN_GIAO_CAMERA,
            "Service xac nhan Camera nen DA BAT"
        )
    }


    private fun guiBroadcastCameraNenDaTatMotLan() {

        /*
         * Có thể nhiều nhánh bất đồng bộ cùng đi đến đây
         * (camera đã tắt sẵn, callback khởi động xong rồi tắt,
         * hoặc callback lỗi).
         *
         * Chỉ nhánh đầu tiên của đúng một yêu cầu TẮT được
         * phép gửi ACK. Các lần sau bị bỏ qua.
         */
        if (!dangChoXacNhanCameraNenDaTat) {

            Log.d(
                TAG_BAN_GIAO_CAMERA,
                "Bo qua ACK Camera nen DA TAT trung lap"
            )

            return
        }

        /*
         * Clear guard TRƯỚC khi broadcast để ngay cả khi
         * receiver phản ứng rất nhanh cũng không tạo ACK lặp.
         */
        dangChoXacNhanCameraNenDaTat =
            false

        val intent =
            Intent(
                HANH_DONG_CAMERA_NEN_DA_TAT
            ).apply {

                setPackage(
                    packageName
                )
            }

        sendBroadcast(
            intent
        )

        Log.d(
            TAG_BAN_GIAO_CAMERA,
            "Service xac nhan Camera nen DA TAT"
        )
    }


    // =========================================================
    // SERVICE DESTROY
    // =========================================================

    override fun onDestroy() {

        if (::nhanDienNghiengDau.isInitialized) {
            nhanDienNghiengDau.datLai()
        }

        cameraNenDangBat =
            false

        cameraNenDangKhoiDong =
            false

        yeuCauTatSauKhiKhoiDong =
            false

        dangChoXacNhanCameraNenDaTat =
            false

        if (::quanLyCamera.isInitialized) {

            quanLyCamera.dong()
        }

        if (::xuLyKhuonMat.isInitialized) {

            xuLyKhuonMat.dong()
        }

        Log.d(
            TAG,
            "Dich vu theo doi da dung"
        )

        lifecycleRegistry.currentState =
            Lifecycle.State.DESTROYED

        super.onDestroy()
    }


    // =========================================================
    // BIND
    // =========================================================

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }


    // =========================================================
    // FOREGROUND
    // =========================================================

    private fun batForeground() {

        val thongBao =
            NotificationCompat.Builder(
                this,
                ID_KENH_THONG_BAO
            )
                .setSmallIcon(
                    R.mipmap.ic_launcher
                )
                .setContentTitle(
                    "FaceAccess đang hoạt động"
                )
                .setContentText(
                    "Đang theo dõi cử chỉ khuôn mặt"
                )
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .setOngoing(true)
                .build()

        startForeground(
            ID_THONG_BAO,
            thongBao
        )
    }


    // =========================================================
    // NOTIFICATION CHANNEL
    // =========================================================

    private fun taoKenhThongBao() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val kenh =
                NotificationChannel(
                    ID_KENH_THONG_BAO,
                    TEN_KENH_THONG_BAO,
                    NotificationManager.IMPORTANCE_LOW
                )

            kenh.description =
                "Thông báo khi FaceAccess đang theo dõi cử chỉ"

            val notificationManager =
                getSystemService(
                    NotificationManager::class.java
                )

            notificationManager
                .createNotificationChannel(
                    kenh
                )
        }
    }


    // =========================================================
    // CONSTANT
    // =========================================================

    companion object {

        private const val TAG =
            "DichVuTheoDoi"

        private const val TAG_CAMERA_NEN =
            "CameraNenFaceAccess"

        private const val TAG_BAN_GIAO_CAMERA =
            "BanGiaoCamera"

        private const val TAG_CU_CHI_NEN =
            "CuChiNen"

        const val HANH_DONG_BAT_CAMERA_NEN =
            "com.example.faceaccess.v2.BAT_CAMERA_NEN"

        const val HANH_DONG_TAT_CAMERA_NEN =
            "com.example.faceaccess.v2.TAT_CAMERA_NEN"

        const val HANH_DONG_CAMERA_NEN_DA_BAT =
            "com.example.faceaccess.v2.CAMERA_NEN_DA_BAT"

        const val HANH_DONG_CAMERA_NEN_DA_TAT =
            "com.example.faceaccess.v2.CAMERA_NEN_DA_TAT"

        private const val ID_KENH_THONG_BAO =
            "faceaccess_tracking"

        private const val TEN_KENH_THONG_BAO =
            "Theo dõi FaceAccess"

        private const val ID_THONG_BAO =
            1001

        private const val KHOANG_LOG_CAMERA_NEN_MS =
            1000L
    }
}
