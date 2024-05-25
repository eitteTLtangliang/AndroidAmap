package com.liang.map.ui.callback

import android.os.Bundle
import com.iflytek.cloud.SpeechError

interface SynthesizerListener {
    fun onSpeakBegin()
    fun onBufferProgress(var1: Int, var2: Int, var3: Int, var4: String)
    fun onSpeakPaused()
    fun onSpeakResumed()
    fun onSpeakProgress(var1: Int, var2: Int, var3: Int)
    fun onCompleted(var1: SpeechError)
    fun onEvent(var1: Int, var2: Int, var3: Int, var4: Bundle)
}
