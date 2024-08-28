package kr.dori.android.own_cast.playlist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import kr.dori.android.own_cast.databinding.FragmentEditCategoryDialogBinding
import kr.dori.android.own_cast.forApiData.GetAllPlaylist

class EditCategoryDialog(context: Context, private val listener: EditCategoryListener, private val position: Long, val playlistId: Long) : Dialog(context) {

    private lateinit var binding: FragmentEditCategoryDialogBinding
    private var isText = false
    private lateinit var addtext: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentEditCategoryDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        //외부 터치 이벤트 제거하기
        setCanceledOnTouchOutside(false)

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
                val existingData: GetAllPlaylist = listener.getCategoryData(position)
                val name: String = existingData.copy(name = addtext).name
                val playlistId = playlistId
                // creator 속성만 변경하여 새로운 데이터 생성


                // 업데이트된 데이터를 listener로 전달
                listener.onCategoryEdit(position, name, playlistId)

                dismiss()
            }
        }
    }

    private fun setButton() {
        if (isText) {
            binding.fragmentEditCategoryOff.visibility = View.GONE
            binding.fragmentEditCategoryOn.visibility = View.VISIBLE
            binding.fragmentEditDialogOnEdit.visibility = View.VISIBLE
            binding.fragmentEditDialogOff.visibility = View.GONE
        } else {
            binding.fragmentEditCategoryOff.visibility = View.VISIBLE
            binding.fragmentEditCategoryOn.visibility = View.GONE
            binding.fragmentEditDialogOnEdit.visibility = View.GONE
            binding.fragmentEditDialogOff.visibility = View.VISIBLE
        }
    }
}
