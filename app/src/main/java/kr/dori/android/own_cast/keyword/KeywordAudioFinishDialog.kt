package kr.dori.android.own_cast.keyword

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.databinding.FragmentKeywordOutDialogBinding
import kr.dori.android.own_cast.databinding.KeywordFinishDialogBinding

class KeywordAudioFinishDialog(context: Context, private val listener: KeywordAudioFinishListener
,val title : String, val category : String, val uri: Uri?
) : Dialog(context) {
    lateinit var binding : KeywordFinishDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KeywordFinishDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)


        binding.keyFinHomeTv.setOnClickListener {
            listener.goHomeFragment()
        }
        initData()

        context.dialogResize(this, 0.9f,0.335f)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    private fun initData(){
        binding.keyFinCastTitleTv.text = title
        binding.keyFinScrpit1stTv.text = SignupData.name
        binding.keyFinCastCategoryTv.text = category
        if(uri!=null){
            Glide.with(context)
                .load(uri)
                .centerCrop() // ImageView에 맞게 이미지 크기를 조정
                .into(binding.keyFinCastImgIv)
        }else{

        }
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