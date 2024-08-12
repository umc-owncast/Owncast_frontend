package kr.dori.android.own_cast

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.dori.android.own_cast.databinding.ActivityEditAudioBinding

class EditAudioActivity : AppCompatActivity(), EditAudio {

    private lateinit var binding: ActivityEditAudioBinding
    var isLock: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityEditAudioExitIv.setOnClickListener {
            finish()
        }

        binding.activityEditAudioSwitchOff.setOnClickListener {
            lock(isLock)
            isLock = false
        }

        binding.activityEditAudioSwitchOn.setOnClickListener {
            lock(isLock)
            isLock = true
        }

        binding.activityEditAudioOk.setOnClickListener {
            finish()
        }

        binding.activityEditAudioDelete.setOnClickListener {
            val dialog = EditAudioDialog(this, this)
            dialog.show()
        }
    }

    fun lock(isLock: Boolean) {
        if (isLock) {
            binding.activityEditAudioSwitchOff.visibility = View.VISIBLE
            binding.activityEditAudioSwitchOn.visibility = View.GONE
        } else {
            binding.activityEditAudioSwitchOff.visibility = View.GONE
            binding.activityEditAudioSwitchOn.visibility = View.VISIBLE
        }
    }

    fun showCustomToast(message: String) {
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container))

        val textView: TextView = layout.findViewById(R.id.toast_message_tv)
        textView.text = message

        with (Toast(applicationContext)) {
            duration = Toast.LENGTH_LONG
            view = layout
           // setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
            show()
        }
    }

    override fun dialogToEditAudio() {
        showCustomToast("캐스트가 삭제되었어요")
        finish()
    }
}
