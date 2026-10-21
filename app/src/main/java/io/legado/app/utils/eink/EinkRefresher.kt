package io.legado.app.utils.eink

import android.util.Log
import splitties.init.appCtx

/**
 * å¢¨æ°´å±å¨å·æ§å¶å¨
 *
 * éå¯¹å°ç±³éè¯»å¨(RK3566)ç­ Rockchip å¹³å°è®¾å¤:
 *   ç³»ç»éèæå¡ android.os.EinkManager.sendOneFullFrame() è§¦åå¨å±å·æ°
 *
 * Onyx Boox(é«é)ç­è®¾å¤æ æ­¤æå¡, éé»éçº§ä¸åä»»ä½äºã
 * åå°å¥æåªå¨ç¬¬ä¸æ¬¡æååç¼å­, å¤±è´¥ä¹ä¸éè¯(é¿åæ¯é¡µé½æå¼å¸¸)ã
 */
object EinkRefresher {

    private const val TAG = "EinkRefresher"

    private var pageCount = 0

    private var sendOneFullFrame: java.lang.reflect.Method? = null
    private var einkManager: Any? = null
    private var resolved = false

    /**
     * ç¿»é¡µæ¶è°ç¨, æ¯ interval é¡µå¨å·ä¸æ¬¡
     * @param interval é´éé¡µæ°, <=0 ç¦ç¨
     */
    fun onPageTurned(interval: Int) {
        if (interval <= 0) {
            pageCount = 0
            return
        }
        pageCount++
        if (pageCount >= interval) {
            pageCount = 0
            refreshNow()
        }
    }

    /**
     * ç«å³å¨å±å·æ°
     */
    fun refreshNow() {
        if (!resolved) {
            resolve()
        }
        try {
            val method = sendOneFullFrame
            if (method != null && einkManager != null) {
                method.invoke(einkManager)
                Log.d(TAG, "full refresh sent")
            }
        } catch (e: Exception) {
            Log.w(TAG, "full refresh failed: ${e.message}")
        }
    }

    private fun resolve() {
        resolved = true
        try {
            val cls = Class.forName("android.os.EinkManager")
            einkManager = appCtx.getSystemService("eink")
            sendOneFullFrame = cls.getDeclaredMethod("sendOneFullFrame")
            Log.i(TAG, "EinkManager resolved")
        } catch (e: Exception) {
            Log.i(TAG, "EinkManager not available, full refresh disabled")
        }
    }
}
