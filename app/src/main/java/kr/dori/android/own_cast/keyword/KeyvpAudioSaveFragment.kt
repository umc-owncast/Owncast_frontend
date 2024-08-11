package kr.dori.android.own_cast.keyword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.AddCategoryDialog
import kr.dori.android.own_cast.AddCategoryListener
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeyvpAudiosaveBinding


class KeyvpAudioSaveFragment : Fragment(),KeywordAudioFinishListener,AddCategoryListener {
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


        val dialog = AddCategoryDialog(requireContext(), this)
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

        binding.keyAudSaveBtnIv.setOnClickListener{
            val dialog = KeywordAudioFinishDialog(requireContext(), this)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }

        return binding.root



    }

    override fun goHomeFragment() {
        super.goHomeFragment()
        activity?.finish()
    }
    //addCategory와 같은 기능이니, listener 사용해서 구현
    override fun onCategoryAdded(categoryName: String) {
        list.add(list.size-1,categoryName)
        adapter.notifyDataSetChanged()
        //setSelection 써야 우리가 작성한 카테고리가 선택됨
        //selection 효과가 기본적으로 구현돼있는듯
        binding.keyAudSaveCategorySp.setSelection(list.size-2)

    }

}