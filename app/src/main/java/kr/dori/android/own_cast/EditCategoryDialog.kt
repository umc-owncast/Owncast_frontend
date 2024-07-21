package kr.dori.android.own_cast

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import kr.dori.android.own_cast.databinding.FragmentAddCategoryDialogBinding

class EditCategoryDialog(context: Context, private val listener: EditCategoryListener, private val position: Int) : Dialog(context) {

    private lateinit var binding: FragmentAddCategoryDialogBinding
    private var isText = false
    private lateinit var addtext: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddCategoryDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)


        // 뒤로 가기
        binding.categoryDialogCircleIv.setOnClickListener {
            dismiss()
        }

        binding.categoryDialogXIv.setOnClickListener {
            dismiss()
        }

        // 플레인 텍스트에 텍스트를 입력하면 확인 버튼이 on 되고 클릭 리스너 활성화
        binding.fragmentAddCategoryPlainTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    addtext = binding.fragmentAddCategoryPlainTv.text.toString()
                }
                isText = s?.isNotEmpty() == true
                setButton()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.fragmentAddCategoryDialogOn.setOnClickListener {
            if (isText) {
                // 해당 position의 기존 데이터를 로드
                val existingData = listener.getCategoryData(position)

                // creator 속성만 변경하여 새로운 데이터 생성
                val updatedData = existingData.copy(title = addtext)

                // 업데이트된 데이터를 listener로 전달
                listener.onCategoryEdit(position, updatedData)


                dismiss()
            }
        }
    }

    private fun setButton() {
        if (isText) {
            binding.fragmentAddCategoryDialogOff.visibility = View.GONE
            binding.fragmentAddCategoryDialogOn.visibility = View.VISIBLE
        } else {
            binding.fragmentAddCategoryDialogOff.visibility = View.VISIBLE
            binding.fragmentAddCategoryDialogOn.visibility = View.GONE
        }
    }
}
