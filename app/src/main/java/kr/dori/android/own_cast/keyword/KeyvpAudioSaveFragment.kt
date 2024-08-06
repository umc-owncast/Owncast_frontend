package kr.dori.android.own_cast.keyword

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.AddCategoryDialog
import kr.dori.android.own_cast.AddCategoryListener
import kr.dori.android.own_cast.EditAudio
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeyvpAudiosaveBinding


//AddCategoryDialog에 toast기능을 넣으면서 EditAudio를 추가로 전달해주는 부분이 playlistFragment에 필요해서 인터페이스 상속을 추가했습니다.
class KeyvpAudioSaveFragment : Fragment(),KeywordAudioFinishListener,AddCategoryListener, EditAudio {
    lateinit var binding: FragmentKeyvpAudiosaveBinding
    val list: MutableList<String> = mutableListOf<String>("카테고리1","카테고리2","카테고리3","카테고리4","카테고리5","추가할 카테고리 이름 입력")
    var currentPos:Int = 0//카테고리 새로 추가할때, dismiss되면 그대로 유지해야되서 만듦

    lateinit var adapter:KeyAudSaveDropdownAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyvpAudiosaveBinding.inflate(inflater, container, false)


        val dialog = AddCategoryDialog(requireContext(), this,this)
        adapter = KeyAudSaveDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner,list)
        binding.keyAudSaveCategorySp.adapter = adapter

        binding.keyAudSaveCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, p3: Long) {
                when(pos){
                    list.size-1 ->{
                        binding.keyAudSaveCategorySp.setSelection(currentPos)
                        dialog.show()
                    }
                    else ->{
                        val value = binding.keyAudSaveCategorySp.getItemAtPosition(pos).toString()
                        binding.keyAudSaveCategorySp.getItemAtPosition(pos)
                        currentPos = pos
                        Toast.makeText(requireContext(), value, Toast.LENGTH_SHORT).show()
                    }
                }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 선택되지 않은 경우
            }
        }

        initSaveBtn()

        return binding.root



    }

    //dialog버튼 좀 길어져서 init으로 함수 바꿈.
    fun initSaveBtn(){
        // 화면 크기 가져오기
        val displayMetrics = DisplayMetrics()

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        binding.keyAudSaveBtnIv.setOnClickListener{
            val dialog = KeywordAudioFinishDialog(requireContext(), this)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

            //dialog위치 조정
            val window = dialog.window
            if (window != null) {
                val params: WindowManager.LayoutParams = window.attributes
                params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

                params.y = (screenHeight * 0.55).toInt()
                window.attributes = params
            }
        }
    }


    //finish dialog listener 구현
    override fun goHomeFragment() {
        super.goHomeFragment()
        activity?.finish()
    }
    //addCategory같이 카테고리 추가하는 기능
    override fun onCategoryAdded(categoryName: String) {
        list.add(list.size-1,categoryName)
        adapter.notifyDataSetChanged()
        //setSelection 써야 우리가 작성한 카테고리가 선택됨
        //selection 효과가 기본적으로 구현돼있는듯
        binding.keyAudSaveCategorySp.setSelection(list.size-2)

    }

    override fun dialogToEditAudio() {
        TODO("Not yet implemented")
    }
}