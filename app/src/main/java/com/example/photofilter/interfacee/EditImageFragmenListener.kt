package com.example.photofilter.interfacee

interface EditImageFragmenListener {
    fun onBrightnessChanged(brightnes:Int)
    fun onSaturationChanged(saturation:Float)
    fun onConstrantChanged(constrant:Float)
    fun onEditStarted()
    fun onEditCompleted()

}