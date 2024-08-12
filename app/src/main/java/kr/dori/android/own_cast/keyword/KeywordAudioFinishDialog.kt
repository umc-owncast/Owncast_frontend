package kr.dori.android.own_cast.keyword

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import kr.dori.android.own_cast.databinding.FragmentKeywordOutDialogBinding
import kr.dori.android.own_cast.databinding.KeywordFinishDialogBinding

class KeywordAudioFinishDialog(context: Context, private val listener: KeywordAudioFinishListener) : Dialog(context) {
    lateinit var binding : KeywordFinishDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KeywordFinishDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)


        binding.keyFinHomeTv.setOnClickListener {
            listener.goHomeFragment()
        }

        //context.dialogResize(this, 0.945f,0.335f)
        //context.dialogResize(this, 1f,0.7f)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun Context.dialogResize(dialog: Dialog, width: Float, height: Float){
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT < 30){//우리는 api 31이라서 deprecated되었음
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialog.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()

            window?.setLayout(x, y)

        }else{
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }
}