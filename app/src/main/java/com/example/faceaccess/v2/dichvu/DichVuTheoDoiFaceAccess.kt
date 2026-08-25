package com.example.faceaccess.v2.dichvu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.faceaccess.v2.R

class DichVuTheoDoiFaceAccess : Service() {

    override fun onCreate() {
        super.onCreate()

        Log.d(
            TAG,
            "Dich vu theo doi da duoc tao"
        )

        taoKenhThongBao()

        batForeground()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        Log.d(
            TAG,
            "Dich vu theo doi dang chay"
        )

        return START_STICKY
    }

    override fun onDestroy() {

        Log.d(
            TAG,
            "Dich vu theo doi da dung"
        )

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
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
                    NotificationManager
                        .IMPORTANCE_LOW
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


    companion object {

        private const val TAG =
            "DichVuTheoDoi"

        private const val ID_KENH_THONG_BAO =
            "faceaccess_tracking"

        private const val TEN_KENH_THONG_BAO =
            "Theo dõi FaceAccess"

        private const val ID_THONG_BAO =
            1001
    }
}