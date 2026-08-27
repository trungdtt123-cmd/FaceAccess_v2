package com.example.faceaccess.v2.dieuphoi.media

import android.content.Context
import android.media.AudioManager
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent

class BoDieuKhienMedia(
    context: Context
) {

    private val audioManager =
        context.applicationContext
            .getSystemService(
                Context.AUDIO_SERVICE
            ) as AudioManager

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

            LenhMedia.PHAT_TAM_DUNG ->
                guiPhimMedia(
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                )
        }
    }

    private fun guiPhimMedia(
        keyCode: Int
    ): Boolean {
        return try {
            val downTime =
                SystemClock.uptimeMillis()

            audioManager.dispatchMediaKeyEvent(
                KeyEvent(
                    downTime,
                    downTime,
                    KeyEvent.ACTION_DOWN,
                    keyCode,
                    0
                )
            )

            audioManager.dispatchMediaKeyEvent(
                KeyEvent(
                    downTime,
                    SystemClock.uptimeMillis(),
                    KeyEvent.ACTION_UP,
                    keyCode,
                    0
                )
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
                "VOLUME DA_GUI | huong=$huongDieuChinh | " +
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
