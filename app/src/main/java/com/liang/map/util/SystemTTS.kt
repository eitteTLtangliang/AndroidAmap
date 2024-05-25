package com.liang.map.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener
import android.speech.tts.UtteranceProgressListener
import com.liang.map.ui.callback.ICallBack
import com.liang.map.ui.callback.TTS
import java.util.*

class SystemTTS private constructor(context: Context) : UtteranceProgressListener(), TTS, OnUtteranceCompletedListener {
    private var mContext: Context? = null
    private var singleton: SystemTTS? = null
    private var textToSpeech // 系统语音播报类
            : TextToSpeech? = null
    private var isSuccess = true

    companion object : SingletonHolderParameterOne<SystemTTS, Context>(::SystemTTS)

    init {
        mContext = context.applicationContext
        textToSpeech = TextToSpeech(mContext) { i ->
            //系统语音初始化成功
            if (i == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(Locale.CHINA)
                textToSpeech!!.setPitch(1.0f) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                textToSpeech!!.setSpeechRate(1.0f)
                textToSpeech!!.setOnUtteranceProgressListener(this@SystemTTS)
                textToSpeech!!.setOnUtteranceCompletedListener(this@SystemTTS)
                if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    //系统不支持中文播报
                    isSuccess = false
                }
            }
        }
    }


    override fun destroy() {
        stopSpeak()
        if (textToSpeech != null) {
            textToSpeech!!.shutdown()
        }
        singleton = null
    }

    override fun init() {}

    override fun playText(playText: String) {
        if (!isSuccess) {
            return
        }
        if (textToSpeech != null) {
            textToSpeech!!.speak(
                playText,
                TextToSpeech.QUEUE_ADD, null, null
            )
        }
    }

    override fun stopSpeak() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
        }
    }

    override fun isPlaying(): Boolean {
        return textToSpeech!!.isSpeaking
    }

    var callBack: ICallBack? = null

    override fun setCallback(callback: ICallBack) {
        callBack = callback
    }


    //播报完成回调
    override fun onUtteranceCompleted(utteranceId: String?) {}

    override fun onStart(utteranceId: String?) {}

    override fun onDone(utteranceId: String?) {}

    override fun onError(utteranceId: String?) {}
}