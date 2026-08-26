package com.example.faceaccess.v2.dieuphoi.media

import android.content.Context
import android.media.AudioManager
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent

/**
 * Thực thi các lệnh MEDIA lên Android.
 *
 * Lớp này không nhận diện cử chỉ và không biết mode.
 * Nó chỉ nhận LenhMedia semantic rồi gọi Android framework.
 */
class BoDieuKhienMedia(
    context: Context
) {

    private val audioManager =
        context.applicationContext
            .getSystemService(
                Context.AUDIO_SERVICE
            ) as AudioManager


    /**
     * Thực thi đúng một lệnh media.
     *
     * true  = lệnh đã được gửi tới Android thành công.
     * false = có exception khi gọi Android framework.
     */
    fun thucThi(
        lenh: LenhMedia
    ): Boolean {

        return when (lenh) {

            LenhMedia.TRUOC ->
                guiPhimMedia(
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS
                )

            LenhMedia.TIEP_THEO ->
                guiPhimMedia(
                    KeyEvent.KEYCODE_MEDIA_NEXT
                )

            LenhMedia.TANG_AM_LUONG ->
                dieuChinhAmLuong(
                    AudioManager.ADJUST_RAISE
                )

            LenhMedia.GIAM_AM_LUONG ->
                dieuChinhAmLuong(
                    AudioManager.ADJUST_LOWER
                )
        }
    }


    /**
     * Gửi đầy đủ ACTION_DOWN + ACTION_UP.
     *
     * Android sẽ route media key tới media session đang hoạt động
     * (Spotify, YouTube Music, trình phát nhạc... nếu app đó hỗ trợ).
     */
    private fun guiPhimMedia(
        keyCode: Int
    ): Boolean {

        return try {

            val downTime =
                SystemClock.uptimeMillis()


            val suKienNhan =
                KeyEvent(
                    downTime,
                    downTime,
                    KeyEvent.ACTION_DOWN,
                    keyCode,
                    0
                )


            audioManager.dispatchMediaKeyEvent(
                suKienNhan
            )


            val upTime =
                SystemClock.uptimeMillis()


            val suKienTha =
                KeyEvent(
                    downTime,
                    upTime,
                    KeyEvent.ACTION_UP,
                    keyCode,
                    0
                )


            audioManager.dispatchMediaKeyEvent(
                suKienTha
            )


            Log.d(
                TAG,
                "MEDIA_KEY DA_GUI | keyCode=$keyCode"
            )


            true

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "MEDIA_KEY THAT_BAI | keyCode=$keyCode",
                exception
            )


            false
        }
    }


    /**
     * Mỗi cử chỉ PITCH chỉ tăng/giảm đúng một nấc volume media.
     *
     * FLAG_SHOW_UI giúp người dùng nhận phản hồi trực quan từ
     * thanh âm lượng hệ thống.
     */
    private fun dieuChinhAmLuong(
        huongDieuChinh: Int
    ): Boolean {

        return try {

            val truoc =
                audioManager.getStreamVolume(
                    AudioManager.STREAM_MUSIC
                )


            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                huongDieuChinh,
                AudioManager.FLAG_SHOW_UI
            )


            val sau =
                audioManager.getStreamVolume(
                    AudioManager.STREAM_MUSIC
                )


            Log.d(
                TAG,
                "VOLUME DA_GUI | " +
                        "huong=$huongDieuChinh | " +
                        "truoc=$truoc | sau=$sau"
            )


            true

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "VOLUME THAT_BAI | huong=$huongDieuChinh",
                exception
            )


            false
        }
    }


    companion object {

        private const val TAG =
            "MediaAction"
    }
}