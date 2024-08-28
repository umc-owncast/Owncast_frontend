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

class KeywordAudioOutDialog(context: Context, private val listener: KeywordAudioOutListener) : Dialog(context) {
    private lateinit var binding : FragmentKeywordOutDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentKeywordOutDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        binding.keyAudOutCancelTv.setOnClickListener{
            dismiss()
        }
        //
        binding.keyAudOutConfirmTv.setOnClickListener {
            dismiss()
            listener.getOut()//keywordActivity종료
        }

        //크기 재조정 함수(이거 안쓰면 원하는 대로 안나왔음)
        //https://hanyeop.tistory.com/422
        context.dialogResize(this, 0.675f,0.165f)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    //size 조정하는 함수,
    fun Context.dialogResize(dialog: Dialog, width: Float, height: Float){
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