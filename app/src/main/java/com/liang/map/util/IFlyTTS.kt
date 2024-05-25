package com.liang.map.util

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Bundle
import android.util.Log
import com.iflytek.cloud.*
import com.liang.map.ui.callback.ICallBack
import com.liang.map.ui.callback.TTS

class IFlyTTS private constructor(private val context: Context) : TTS, SynthesizerListener,
    OnAudioFocusChangeListener {
    private var isPlaying = false
    private var audioManager: AudioManager
    private var mTts: SpeechSynthesizer? = null
    private var callBack: ICallBack? = null

    /*** 请务必替换为您自己申请的ID。*/
    private val appId = "5350db8d"

    companion object : SingletonHolderParameterOne<IFlyTTS, Context>(::IFlyTTS)

    init {
        SpeechUtility.createUtility(context, "${SpeechConstant.APPID}=${appId}")
        createSynthesizer()
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun init() {
        mTts?.apply {
            setParameter(SpeechConstant.VOICE_NAME, "xiaoyan")
            //设置语速,值范围：[0, 100],默认值：50
            setParameter(SpeechConstant.SPEED, "55")
            //设置音量
            setParameter(SpeechConstant.VOLUME, "tts_volume")
            //设置语调
            setParameter(SpeechConstant.PITCH, "tts_pitch")
            //设置与其他音频软件冲突的时候是否暂停其他音频
            setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false")
            //女生仅vixy支持多音字播报
            setParameter(SpeechConstant.VOICE_NAME, "vixy")
        }
    }

    private fun createSynthesizer() {
        mTts = SpeechSynthesizer.createSynthesizer(context) { errCode ->
            if (ErrorCode.SUCCESS == errCode) {
                Log.v("IFlyTTS", "初始化成功")
            }
        }
    }

    override fun playText(playText: String) {
        //多音字处理举例
        val multiText = if (playText.contains("京藏")) {
            playText.replace("京藏", "京藏[=zang4]")
        } else playText
        if (multiText.isNotEmpty()) {
            val result = audioManager.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                val code = mTts?.startSpeaking(multiText, this)
                Log.v("IFlyTTS", "code:${code}")
                isPlaying = true
            }
        }
    }

    override fun stopSpeak() {
        mTts?.stopSpeaking()
        isPlaying = false
    }

    override fun destroy() {
        stopSpeak()
        mTts?.destroy()
    }

    override fun onBufferProgress(arg0: Int, arg1: Int, arg2: Int, arg3: String) {}

    //播报是否成功以及错误码
    //在音频播放完成，或会话出现错误时，将回调此函数。若为null，则没有出现错误。
    override fun onCompleted(arg0: SpeechError?) {
        isPlaying = false
        audioManager.abandonAudioFocus(this)
        if (callBack != null) {
            if (arg0 == null) {
                callBack?.onCompleted(0)
            }
        }
    }

    override fun onEvent(arg0: Int, arg1: Int, arg2: Int, arg3: Bundle) {}

    override fun onSpeakBegin() {
        isPlaying = true
    }

    override fun onSpeakPaused() {
        isPlaying = false
    }

    override fun onSpeakProgress(arg0: Int, arg1: Int, arg2: Int) {}

    override fun onSpeakResumed() {}

    override fun isPlaying(): Boolean {
        return isPlaying
    }

    override fun setCallback(callback: ICallBack) {
        callBack = callback
    }

    override fun onAudioFocusChange(focusChange: Int) {

    }
}