package kr.dori.android.own_cast.keyword

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kr.dori.android.own_cast.AddCategoryDialog
import kr.dori.android.own_cast.AddCategoryListener
import kr.dori.android.own_cast.EditAudio
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SharedViewModel
import kr.dori.android.own_cast.SongData
import kr.dori.android.own_cast.databinding.FragmentKeyvpAudiosaveBinding


//AddCategoryDialog에 toast기능을 넣으면서 EditAudio를 추가로 전달해주는 부분이 playlistFragment에 필요해서 인터페이스 상속을 추가했습니다.
class KeyvpAudioSaveFragment : Fragment(),KeywordAudioFinishListener,AddCategoryListener, EditAudio {
    lateinit var binding: FragmentKeyvpAudiosaveBinding
    private val list: MutableList<String> = mutableListOf<String>("카테고리1","카테고리2","카테고리3","카테고리4","카테고리5","추가할 카테고리 이름 입력")
    var currentPos:Int = 0//카테고리 새로 추가할때, dismiss되면 그대로 유지해야되서 만듦
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    lateinit var _list:MutableList<String>

    private val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var adapter:KeyAudSaveDropdownAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyvpAudiosaveBinding.inflate(inflater, container, false)

        /*sharedViewModel.data.value?.let {
            for(i:Int in 0..it.size){
                it[i].title?.let {
                    _list.add(it)
                }

            }
        }*/


        initSpinnerAdapter()
        initSaveBtn()

        return binding.root
    }

    fun initSpinnerAdapter() {
        //마지막 칸이 category를 생성하는 기능이기 때문에 다이얼로그를 받아온다.
        val dialog = AddCategoryDialog(requireContext(), this, this)
        /*if(_list.isNullOrEmpty()){
            adapter = KeyAudSaveDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner, list)
        }else{

        }*/
        adapter = KeyAudSaveDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner, _list)
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
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
            }
        }
    }

    //dialog버튼 좀 길어져서 init으로 함수 바꿈.
    fun initSaveBtn(){
        // 화면 크기 가져오기
        val displayMetrics = DisplayMetrics()

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        binding.keyAudSaveBtnIv.setOnClickListener{
            //finishDialog를 생성한다.
            val dialog = KeywordAudioFinishDialog(requireContext(), this)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

            //dialog위치 조정
            val window = dialog.window
            if (window != null) {
                val params: WindowManager.LayoutParams = window.attributes
                params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

                params.y = (screenHeight * 0.5).toInt()
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
        //새로 생성하면 키워드 생성하기 메뉴가 size-1, 새로 생성된 메뉴가 size-2

    }

    fun showCustomToast(message: String) {
        // Inflate the custom layout
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast, binding.root.findViewById(R.id.custom_toast_container))

        // Set custom message
        val textView: TextView = layout.findViewById(R.id.toast_message_tv)
        textView.text = message

        // Create and show the Toast
        with (Toast(requireContext())) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    override fun dialogToEditAudio() {
        showCustomToast("카테고리가 추가되었어요")
    }

    private val REQUEST_PERMISSION = 1001
    private val PICK_IMAGE_REQUEST = 1002



    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_PERMISSION)
            } else {
                openGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
            } else {
                openGallery()
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

}