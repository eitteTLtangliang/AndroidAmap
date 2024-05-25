package com.liang.map.ui.callback

interface TTS {
    fun init()
    fun playText(playText: String)
    fun stopSpeak()
    fun destroy()
    fun isPlaying(): Boolean
    fun setCallback(callback: ICallBack)
}