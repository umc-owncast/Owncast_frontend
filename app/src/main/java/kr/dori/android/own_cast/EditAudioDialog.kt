package kr.dori.android.own_cast

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import kr.dori.android.own_cast.databinding.EditAudioDialogBinding

class EditAudioDialog(context: Context, private val mover: EditAudio) : Dialog(context) {
    private lateinit var binding: EditAudioDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditAudioDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)

        binding.editAudioDialogDismissIv.setOnClickListener {
            dismiss()
        }

        binding.editAudioDialogDelete.setOnClickListener {
            dismiss()
            mover.dialogToEditAudio()

        }
    }
}
