package com.example.photofilter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.photofilter.interfacee.EditImageFragmenListener
import kotlinx.android.synthetic.main.fragment_edit_image.*


class EditImageFragment : Fragment(), SeekBar.OnSeekBarChangeListener {
    private var listenr: EditImageFragmenListener? = null
    fun resetControles() {

        seekbar_brightness.progress = 100
        seekbar_constrant.progress = 0
        seekbar_saturation.progress = 10
    }

    fun setListener(listener: EditImageFragmenListener) {
        this.listenr = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_edit_image, container, false)
        seekbar_brightness.max = 200
        seekbar_brightness.progress = 100
        seekbar_constrant.max = 20
        seekbar_constrant.progress = 0
        seekbar_saturation.max = 30
        seekbar_saturation.progress = 10


        seekbar_saturation.setOnSeekBarChangeListener(this)
        seekbar_constrant.setOnSeekBarChangeListener(this)
        seekbar_brightness.setOnSeekBarChangeListener(this)
        return view
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        var progress = progress
        if (listenr != null) {
            if (seekBar!!.id == R.id.seekbar_brightness) {
                listenr!!.onBrightnessChanged(progress - 100)
            } else if (seekBar!!.id == R.id.seekbar_constrant) {
                progress += 10
                val floatVal = .10f * progress
                listenr!!.onConstrantChanged(floatVal)
            } else if (seekBar!!.id == R.id.seekbar_saturation) {
                val floatVal = .10f * progress
                listenr!!.onSaturationChanged(floatVal)

            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        if (listenr != null)
            listenr!!.onEditStarted()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (listenr != null)
            listenr!!.onEditCompleted()
    }

}
