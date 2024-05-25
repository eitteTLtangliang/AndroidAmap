package com.liang.map.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.model.*
import com.liang.map.ui.callback.ICallBack
import com.liang.map.ui.callback.TTS
import java.util.*

class TTSController(private val context: Context) : AMapNaviListener, ICallBack {
    companion object {
        private const val TTS_PLAY = 1
        private const val CHECK_TTS_PLAY = 2
    }

    private var ttsManager: TTSController? = null
    private var tts: TTS? = null
    private var systemTTS = SystemTTS.getInstance(context)
    private var iflyTTS = IFlyTTS.getInstance(context).also { tts = it }
    private val wordList = LinkedList<String>()

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                TTS_PLAY -> if (tts != null && wordList.size > 0) {
                    tts?.playText(wordList.removeFirst())
                }
                CHECK_TTS_PLAY -> if (tts?.isPlaying() == false) {
                    this.obtainMessage(1).sendToTarget()
                }
                else -> {}
            }
        }
    }

    override fun onCompleted(code: Int) {
        handler.obtainMessage(1).sendToTarget()
    }

    enum class TTSType {
        /*** 讯飞语音 **/
        IFLYTTS,

        /*** 系统语音 **/
        SYSTEMTTS
    }

    fun setTTSType(type: TTSType) {
        tts = if (type == TTSType.SYSTEMTTS) {
            systemTTS
        } else {
            iflyTTS
        }
        tts?.setCallback(this)
    }

    fun init() {
        systemTTS.init()
        iflyTTS.init()
        tts?.setCallback(this)
    }

    fun getInstance(context: Context): TTSController? {
        if (ttsManager == null) {
            ttsManager = TTSController(context)
        }
        return ttsManager
    }

    fun stopSpeaking() {
        systemTTS.stopSpeak()
        iflyTTS.stopSpeak()
        wordList.clear()
    }

    fun destroy() {
        systemTTS.destroy()
        iflyTTS.destroy()
        ttsManager = null
    }

    /****************************************************************************
     * 以下都是导航相关接口
     */
    override fun onArriveDestination() {}

    override fun onArrivedWayPoint(arg0: Int) {}

    override fun onCalculateRouteFailure(arg0: Int) {}

    override fun onEndEmulatorNavi() {}

    override fun onGetNavigationText(arg0: Int, arg1: String?) {}


    override fun onInitNaviFailure() {}

    override fun onInitNaviSuccess() {}

    override fun onLocationChange(arg0: AMapNaviLocation?) {}

    override fun onReCalculateRouteForTrafficJam() {
        wordList.addLast("前方路线拥堵，路线重新规划")
    }

    override fun onReCalculateRouteForYaw() {
        wordList.addLast("路线重新规划")
    }

    override fun onStartNavi(arg0: Int) {}

    override fun onTrafficStatusUpdate() {}

    override fun onGpsOpenStatus(enabled: Boolean) {}

    override fun onNaviInfoUpdate(naviinfo: NaviInfo?) {}

    override fun updateCameraInfo(infoArray: Array<AMapNaviCameraInfo?>?) {}

    override fun onServiceAreaUpdate(infoArray: Array<AMapServiceAreaInfo?>?) {}

    override fun showCross(aMapNaviCross: AMapNaviCross?) {}

    override fun hideCross() {}

    override fun showLaneInfo(
        laneInfos: Array<AMapLaneInfo>,
        laneBackgroundInfo: ByteArray,
        laneRecommendedInfo: ByteArray
    ) {
    }


    override fun hideLaneInfo() {}

    override fun onCalculateRouteSuccess(routeIds: IntArray?) {}

    override fun notifyParallelRoad(parallelRoadType: Int) {}

    override fun OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfo: AMapNaviTrafficFacilityInfo?) {}

    override fun OnUpdateTrafficFacility(infos: Array<AMapNaviTrafficFacilityInfo?>?) {}


    override fun updateAimlessModeStatistics(aimLessModeStat: AimLessModeStat?) {}

    override fun updateAimlessModeCongestionInfo(aimLessModeCongestionInfo: AimLessModeCongestionInfo?) {}

    override fun onPlayRing(type: Int) {}

    override fun onGetNavigationText(playText: String) {
        wordList.addLast(playText)
        handler.obtainMessage(CHECK_TTS_PLAY).sendToTarget()
    }

    override fun showModeCross(aMapModelCross: AMapModelCross?) {}

    override fun hideModeCross() {}

    override fun updateIntervalCameraInfo(
        aMapNaviCameraInfo: AMapNaviCameraInfo?,
        aMapNaviCameraInfo1: AMapNaviCameraInfo?,
        i: Int
    ) {
    }

    override fun showLaneInfo(aMapLaneInfo: AMapLaneInfo?) {}

    override fun onCalculateRouteSuccess(aMapCalcRouteResult: AMapCalcRouteResult?) {}

    override fun onCalculateRouteFailure(aMapCalcRouteResult: AMapCalcRouteResult?) {
        wordList.addLast("路线规划失败")
    }

    override fun onNaviRouteNotify(aMapNaviRouteNotifyData: AMapNaviRouteNotifyData?) {}

    override fun onGpsSignalWeak(b: Boolean) {}
}