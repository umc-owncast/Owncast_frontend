package kr.dori.android.own_cast

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kr.dori.android.own_cast.databinding.FragmentAddCategoryDialogBinding
import kr.dori.android.own_cast.databinding.FragmentEditCategoryDialogBinding

class EditCategoryDialog(context: Context, private val listener: EditCategoryListener, private val position: Int) : Dialog(context) {

    private lateinit var binding: FragmentEditCategoryDialogBinding
    private var isText = false
    private lateinit var addtext: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentEditCategoryDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        //외부 터치 이벤트 제거하기
        setCanceledOnTouchOutside(false)

/*
edit category dialog에서 밑줄의 색을 변경하는 방법
-> plain text에서 밑줄의 색 변경은 기본적으로 지원되지 않는다.
1. button을 커스텀해서 plain text 기능을 대체한다. -> 할게 많음
2. 밑줄 이미지 뷰의 visible설정을 바탕으로 plaintext 밑줄 위에 덮어씌운다. -> 세밀한 dp 조정이 필요한데, dp를 소수점 단위로 조정해야 됨
일단 다른거 하고 돌아오자.

        val constraintLayout: ConstraintLayout = binding.editCategory
        val imageView: ImageView = binding.fragmentEditDialogOnLIne
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintSet.connect(imageView.id,ConstraintSet.TOP, R.id.fragment_edit_category_plain_tv, ConstraintSet.TOP, 38)
 */

            // 뒤로 가기
        binding.categoryDialogCircleIv.setOnClickListener {
            dismiss()
        }

        binding.categoryDialogXIv.setOnClickListener {
            dismiss()
        }

        // 플레인 텍스트에 텍스트를 입력하면 확인 버튼이 on 되고 클릭 리스너 활성화
        binding.fragmentEditCategoryPlainTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    addtext = binding.fragmentEditCategoryPlainTv.text.toString()
                }
                isText = s?.isNotEmpty() == true
                setButton()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.fragmentEditCategoryOn.setOnClickListener {
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
            binding.fragmentEditCategoryOff.visibility = View.GONE
            binding.fragmentEditCategoryOn.visibility = View.VISIBLE
            binding.fragmentEditDialogOnLIne.visibility = View.VISIBLE
            binding.fragmentEditDialogOnEdit.visibility = View.VISIBLE
            binding.fragmentEditDialogOff.visibility = View.GONE
        } else {
            binding.fragmentEditCategoryOff.visibility = View.VISIBLE
            binding.fragmentEditCategoryOn.visibility = View.GONE
            binding.fragmentEditDialogOnLIne.visibility = View.GONE
            binding.fragmentEditDialogOnEdit.visibility = View.GONE
            binding.fragmentEditDialogOff.visibility = View.VISIBLE
        }
    }
}
