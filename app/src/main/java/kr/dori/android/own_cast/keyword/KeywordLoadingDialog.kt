package kr.dori.android.own_cast.keyword

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentAddCategoryDialogBinding
import kr.dori.android.own_cast.databinding.KeywordLoadingDialogBinding


class KeywordLoadingDialog(context: Context, val loadingText : String):Dialog(context) {
    private lateinit var binding: KeywordLoadingDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KeywordLoadingDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        binding.loadingText.text = loadingText

        /*binding.loadingCircle.pivotX = (binding.loadingCircle.width / 2).toFloat()
        binding.loadingCircle.pivotY = (binding.loadingCircle.height / 2).toFloat()

        // 회전 애니메이션 생성
        val rotateAnimator = ObjectAnimator.ofFloat(binding.loadingCircle, "rotation", 0f, 360f)
        rotateAnimator.duration = 1000 // 회전 한 바퀴에 걸리는 시간 (밀리초)
        rotateAnimator.repeatCount = ObjectAnimator.INFINITE // 무한 반복
        rotateAnimator.repeatMode = ObjectAnimator.RESTART // 애니메이션이 끝나면 다시 시작*/

        val turnAround : Animation = AnimationUtils.loadAnimation(context, R.animator.rotation_animator)
        context.dialogResize(this, 0.56f,0.185f)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 애니메이션 시작
        binding.loadingCircle.startAnimation(turnAround)
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