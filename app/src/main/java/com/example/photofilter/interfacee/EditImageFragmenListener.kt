package com.example.photofilter.interfacee

interface EditImageFragmenListener {
    fun onBrightnessChanged(brightnesInt:Int)
    fun onSaturationChanged(saturationInt:Int)
    fun onConstrantChanged(constrant:Int)
    fun onEditStarted()
    fun onEditCompleted()

}