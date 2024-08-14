package kr.dori.android.own_cast.keyword

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kr.dori.android.own_cast.AddCategoryDialog
import kr.dori.android.own_cast.AddCategoryListener
import kr.dori.android.own_cast.EditAudio
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SharedViewModel
import kr.dori.android.own_cast.SongData
import kr.dori.android.own_cast.databinding.FragmentKeyvpAudiosaveBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.SaveInfo
import kr.dori.android.own_cast.forApiData.getRetrofit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.Objects


//AddCategoryDialog에 toast기능을 넣으면서 EditAudio를 추가로 전달해주는 부분이 playlistFragment에 필요해서 인터페이스 상속을 추가했습니다.
class KeyvpAudioSaveFragment : Fragment(),KeywordAudioFinishListener,AddCategoryListener, EditAudio {
    lateinit var binding: FragmentKeyvpAudiosaveBinding
    private val list: MutableList<String> = mutableListOf<String>("카테고리1","카테고리2","카테고리3","카테고리4","카테고리5","추가할 카테고리 이름 입력")
    var currentPos:Int = 0//카테고리 새로 추가할때, dismiss되면 그대로 유지해야되서 만듦
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    lateinit var _list:MutableList<String>

    private val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var adapter:KeyAudSaveDropdownAdapter

    private var isText = false

    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>

    /*postCast에 쓰일 정보들*/
    private var body : MultipartBody.Part? = null//이미지 파일을 이에 담아서 요청때 보내야함
    private lateinit var castTitle: String
    private var isPublic : Boolean = false

    //OnVIewCreated에서 선언해줌으로써, 이미지를 받아올 수 있는 imageResultLauncher를 초기화 한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let {
                    // 이미지를 ImageView에 맞게 로드
                    Glide.with(this)
                        .load(it)
                        .centerCrop() // ImageView에 맞게 이미지 크기를 조정
                        .into(binding.keyAudSaveThumbIv)
                    //아래의 코드로 이제 서버쪽으로 이미지를 보낼 수 있게 해줌.
                    body = createMultipartBodyFromUri(it, requireContext())
                }


                binding.keyAudSaveGalIc.visibility = View.GONE

            }
        }
    }
    fun createMultipartBodyFromUri(uri: Uri, context: Context): MultipartBody.Part? {
        // Open InputStream from the Uri
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        // Create a temp file in the cache directory to store the image
        val tempFile = File(context.cacheDir, "tempImageFile.png")
        tempFile.outputStream().use { outputStream ->
            inputStream.use { inputStream.copyTo(outputStream) }
        }

        // Convert the temp file to RequestBody
        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())

        // Create MultipartBody.Part from RequestBody
        return MultipartBody.Part.createFormData("photo", tempFile.name, requestFile)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyvpAudiosaveBinding.inflate(inflater, container, false)
        initSpinnerAdapter()
        initSaveBtn()//여기서 아마 저장하기 버튼 나옴
        initEditText()
        binding.keyAudSaveThumbIv.setOnClickListener {
            selectGallery()
        }
        return binding.root
    }
//------------------------------------------------------------갤러리 참조용 함수
    private fun selectGallery() {
        // Android 버전에 따른 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API 33) 이상
            requestPermission(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            // Android 12 (API 32) 이하
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun requestPermission(permission: String) {
        val permissionStatus = ContextCompat.checkSelfPermission(requireContext(), permission)

        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            // 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                REQ_GALLERY
            )
        } else {
            // 권한이 있는 경우 갤러리 실행
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imageResultLauncher.launch(intent)
    }



    companion object {
        private const val REQ_GALLERY = 1
    }
//------------------------------------------------------------갤러리 참조용 함수 종료


    fun initSpinnerAdapter() {
        //마지막 칸이 category를 생성하는 기능이기 때문에 다이얼로그를 받아온다.
        val dialog = AddCategoryDialog(requireContext(), this, this)
        /*if(_list.isNullOrEmpty()){
            adapter = KeyAudSaveDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner, list)
        }else{

        }*/
        adapter = KeyAudSaveDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner, list)
        binding.keyAudSaveCategorySp.adapter = adapter
        binding.keyAudSaveCategorySp.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                // Spinner가 클릭되었을 때 실행할 코드
                binding.keyAudSaveCategorySp.setBackgroundResource(R.drawable.key_audset_dropdown_bg)
            }
            false
        }
        binding.keyAudSaveCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, p3: Long) {
                when(pos){
                    list.size-1 ->{//이 부분이 카테고리 생성하는 부분, api 추가해주기
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

                binding.keyAudSaveCategorySp.setBackgroundResource(R.drawable.key_audset_dropdown_off_bg)
                //다시 포커스 안된거처럼 색깔을 바꿔줘야 함
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 선택되지 않은 경우
                binding.keyAudSaveCategorySp.setBackgroundResource(R.drawable.key_audset_dropdown_off_bg)
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
        binding.keyAudSaveBtnOnIv.setOnClickListener{
            //finishDialog띄우는 버튼
            val dialog = KeywordAudioFinishDialog(requireContext(), this)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            val window = dialog.window
            /*if (window != null) {
                val params: WindowManager.LayoutParams = window.attributes
                params.gravity = Gravity.TOP //or Gravity.CENTER_HORIZONTAL

                params.y = (screenHeight * 0.5).toInt()//y위치 임의 조정함
                window.attributes = params
            }*/
            postCastSave()
            dialog.show()
            //dialog위치 조정

        }

    }


    private fun initEditText(){
        /*binding.keyAudSaveTitleEt.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // 포커스가 잡혔을 때 실행할 코드
                binding.keyAudSaveTitleEt.backgroundTintList = ColorStateList.
                valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))

            } else {
                // 포커스가 해제되었을 때 실행할 코드
                binding.keyAudSaveTitleEt.backgroundTintList = ColorStateList.
                valueOf(ContextCompat.getColor(this.requireContext(), R.color.hint_color))
            }
        }*/
        binding.keyAudSaveTitleEt.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    castTitle = binding.keyAudSaveTitleEt.text.toString()
                }
                isText = s?.isNotEmpty() == true
                if (isText) {
                    binding.keyAudSaveBtnOffIv.visibility = View.GONE
                    binding.keyAudSaveBtnOnIv.visibility = View.VISIBLE
                } else{
                    binding.keyAudSaveBtnOffIv.visibility = View.VISIBLE
                    binding.keyAudSaveBtnOnIv.visibility = View.GONE
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
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

    fun postCastSave(){//api저장하는 버튼
        val apiService = getRetrofit().create(CastInterface::class.java)
        //1. apiService후, 자신이 만들어놓은 인터페이스(함수 지정해주기)
        //2. AuthResponse에 응답으로 넘어오는 result 값의 제네릭 넣어주기 AuthResponse<List<CastHomeDTO>>
        //3. COMMON200이 성공 코드이고, resp에서 필요한 값 받기
        //ㅔplayList가 재생목록
        apiService.postCast(0, SaveInfo(castTitle,0,binding.keyAudPublicBtnIv.isChecked), body!!).enqueue(object: Callback<AuthResponse<Objects>> {
            override fun onResponse(call: Call<AuthResponse<Objects>>, response: Response<AuthResponse<Objects>>) {
                Log.d("apiTest1", response.toString())
                val resp = response.body()!!
                when(resp.code) {
                    "COMMON200" -> {
                        Log.d("apiTest-castPost","저장성공")
                        Log.d("apiTest-castPost",resp.result.toString())

                    }
                    else ->{
                        Log.d("apiTest-castPost","연결실패 코드 : ${resp.code}")

                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse<Objects>>, t: Throwable) {
                Log.d("apiTest-castPost", t.message.toString())
            }
        })
    }
}

