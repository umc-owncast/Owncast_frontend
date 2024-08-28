package kr.dori.android.own_cast.keyword

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager


import android.content.res.ColorStateList


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.ActivityMover
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.playlist.AddCategoryListener
import kr.dori.android.own_cast.editAudio.EditAudio
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.FragmentKeyvpAudiosaveBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.ErrorResponse
import kr.dori.android.own_cast.forApiData.PlayListInterface
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.PostPlaylist

import kr.dori.android.own_cast.forApiData.SaveInfo
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.player.CastWithPlaylistId
import kr.dori.android.own_cast.player.PlayCastActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody

import okhttp3.RequestBody

import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

import java.io.FileOutputStream
import java.io.IOException




//AddCategoryDialog에 toast기능을 넣으면서 EditAudio를 추가로 전달해주는 부분이 playlistFragment에 필요해서 인터페이스 상속을 추가했습니다.
class KeyvpAudioSaveFragment : Fragment(),KeywordAudioFinishListener, AddCategoryListener, ActivityMover, EditAudio {
    lateinit var binding: FragmentKeyvpAudiosaveBinding

    var currentPos:Int = 0//카테고리 새로 추가할때, dismiss되면 그대로 유지해야되서 만듦
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    lateinit var _list:MutableList<String>

    private lateinit var sharedViewModel: KeywordViewModel
    private lateinit var finDialog : KeywordAudioFinishDialog
    lateinit var adapter:KeyAudSaveDropdownAdapter
    private var playlistName : MutableList<String> = mutableListOf<String>()

    private var isText = false
    private var id : Long? = null
    private var playlistId: Long = 0



    private lateinit var dialog:AddCategoryDialog



    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>

    /*postCast에 쓰일 정보들*/
    private var body : MultipartBody.Part? = null//이미지 파일을 이에 담아서 요청때 보내야함
    private lateinit var castTitle: String
    private var isPublic : Boolean = false


    //finish dialog
    private var uri: Uri? = null

    private var currentPage = 0
    override fun onAttach(context: Context) {
        super.onAttach(context)


    }
    override fun onDetach() {
        super.onDetach()

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyvpAudiosaveBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(KeywordViewModel::class.java)
        initSpinnerAdapter()

        //postCastSave()를 같이 호출함
        initSaveBtn()//여기서 저장하기 버튼, 저장 api 호출

        //기존에 있던 사진을 우선적으로 저장함, 나중에 init으로 변경하기ㅕ




        initEditText()
        binding.keyAudSaveThumbIv.setOnClickListener {
            selectGallery()
        }
        return binding.root
    }


//------------------------------------------------------------갤러리 참조용 함수
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

                uri = it//finish dialog로 사진 정보 넘겨줘야함

                body = createMultipartBodyFromUri(it, requireContext())
            }
            Log.d("이미지 변환","${body}")

            binding.keyAudSaveGalIc.visibility = View.GONE

        }
    }
}
    fun createMultipartBodyFromUri(uri: Uri, context: Context): MultipartBody.Part? {
        // Uri에서 MIME 타입을 가져옵니다.
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg" // 기본값을 설정할 수 있습니다.

        // Uri에서 InputStream을 가져옵니다.
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        // 임시 파일을 생성합니다.
        val tempFile = File(context.cacheDir, "tempImageFile${System.currentTimeMillis()}.${mimeType.substringAfter("/")}")

        // InputStream의 데이터를 임시 파일로 복사합니다.
        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        // RequestBody를 생성합니다.
        val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())

        // MultipartBody.Part를 생성합니다.
        val multipartBody = MultipartBody.Part.createFormData("photo", tempFile.name, requestFile)

        // 임시 파일 삭제 (선택 사항: 파일 사용 후 삭제)
        tempFile.deleteOnExit()

        return multipartBody
    }


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


    //기존 파일을 비트맵으로 바꿔서 서버 api에 보낼 수 있게 하는 방법
    fun prepareFilePartFromDrawable(context: Context, resourceId: Int, partName: String): MultipartBody.Part {
        // 리소스에서 Bitmap 가져오기
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)

        // Bitmap을 임시 파일로 변환
        val file = createTempFile(context, bitmap)

        // 파일을 RequestBody로 변환
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)

        // MultipartBody.Part로 변환
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun createTempFile(context: Context, bitmap: Bitmap): File {
        // 임시 파일 생성
        val file = File(context.cacheDir, "temp_image.jpg")

        try {
            // Bitmap을 JPEG 파일로 저장
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

//------------------------------------------------------------갤러리 참조용 함수 종료


    fun initSpinnerAdapter() {
        //마지막 칸이 category를 생성하는 기능이기 때문에 다이얼로그를 받아온다.
        dialog = AddCategoryDialog(requireContext(), this, this)


        // 뷰모델의 MutableLiveData로부터 playlistName들을 추출하여 List로 만들기
        for(i :Int in 0..sharedViewModel.getPlayList.value!!.size-1){
            playlistName.add(sharedViewModel.getPlayList.value!![i].playlistName)
        }
        playlistName.add("추가할 카테고리 이름 입력")
        adapter = KeyAudSaveDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner,playlistName)
        //adapter.dataList = sharedViewModel.getPlayList.value!!
        adapter.notifyDataSetChanged()
        binding.keyAudSaveCategorySp.adapter = adapter

        Log.d("apitest-getPlaylist",sharedViewModel.getPlayList.value!!.toString())

        binding.keyAudSaveCategorySp.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (binding.keyAudSaveCategorySp.count == 1) {

                    dialog.show()
                    return@setOnTouchListener true
                }
            }
            false  // 이벤트가 처리되지 않았음을 나타냅니다.
        }

        binding.keyAudSaveCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, p3: Long) {
                val value = binding.keyAudSaveCategorySp.getItemAtPosition(pos).toString()
                if((parent?.count == 1)){

                }else{
                    when(pos){

                        playlistName.size-1 ->{//이 부분이 카테고리 생성하는 부분, api 추가해주기
                            binding.keyAudSaveCategorySp.setSelection(currentPos)
                            dialog.show()
                        }
                        else ->{
                            currentPos = pos

                        }
                    }
                }



                //다시 포커스 안된거처럼 색깔을 바꿔줘야 함
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 선택되지 않은 경우

            }

        }
        // Initialize ActivityResultLauncher
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("ifsuccess", "success")
                    val data: Intent? = result.data
                    val isSuccess = data?.getBooleanExtra("result", false) ?: false
                    (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
                }
            }
    }






    //goHome
    //finish dialog listener 구현
    override fun goHomeFragment() {
        super.goHomeFragment()
        //activity?.supportFragmentManager?.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        activity?.finish()
    }
        //캐스트 이동
    override fun goPlayCast() {
        super.goPlayCast()
        getCastInfo(playlistId)
    }

    //addCategory같이 카테고리 추가하는 기능
    override fun onCategoryAdded(categoryName: String) {

        addPlaylist(categoryName)
        adapter.notifyDataSetChanged()
        dialog.dismiss()

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


    //dialog버튼 좀 길어져서 init으로 함수 바꿈.
    fun initSaveBtn(){
        // 화면 크기 가져오기
        val displayMetrics = DisplayMetrics()

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        binding.keyAudSaveBtnOnIv.setOnClickListener{
            //finishDialog띄우는 버튼

            postCastSave()//이게 api

            /*
            val window = dialog.window
            if (window != null) {
                val params: WindowManager.LayoutParams = window.attributes
                params.gravity = Gravity.TOP //or Gravity.CENTER_HORIZONTAL

                params.y = (screenHeight * 0.5).toInt()//y위치 임의 조정함
                window.attributes = params
            }*/
            //dialog위치 조정

        }

    }


    //캐스트 저장
    fun postCastSave(){//api저장하는 버튼
        val apiService = getRetrofit().create(CastInterface::class.java)
        //1. apiService후, 자신이 만들어놓은 인터페이스(함수 지정해주기)
        //2. AuthResponse에 응답으로 넘어오는 result 값의 제네릭 넣어주기 AuthResponse<List<CastHomeDTO>>
        //3. COMMON200이 성공 코드이고, resp에서 필요한 값 받기
        Log.d("캐스트 저장","${playlistId}랑 ${sharedViewModel.getPlayList.value!![binding.keyAudSaveCategorySp.selectedItemPosition].id}")
        playlistId = sharedViewModel.getPlayList.value!![binding.keyAudSaveCategorySp.selectedItemPosition].id

        finDialog = KeywordAudioFinishDialog(requireContext(), this, castTitle,
            sharedViewModel.getPlayList.value!![binding.keyAudSaveCategorySp.selectedItemPosition].playlistName, uri, sharedViewModel.songDuration)
            //저장 타이틀, 카테고리, 길이, 사진

        //캐스트 저장 api호출

        CoroutineScope(Dispatchers.IO).launch() {
            Log.d("캐스트 저장","저장할 때 타이틀 ${castTitle}, 사진 파일 ${body}")
            val response = apiService.postCast(sharedViewModel.castId.value!!, SaveInfo(castTitle,  !binding.keyAudPublicBtnIv.isChecked,playlistId) , body)
            launch {
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            Log.d("캐스트 저장", "${response.body()?.result}")
                            finDialog.setCancelable(false)//dialog는 여기서
                            finDialog.setCanceledOnTouchOutside(false)
                            finDialog.show()

                        } else {
                            Toast.makeText(this@KeyvpAudioSaveFragment.requireContext(), "저장 실패 코드 ${response.code()}", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this@KeyvpAudioSaveFragment.requireContext(), "응답 실패 ${e.message}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }


            }
        }
    }

    private fun addPlaylist(categoryName: String){
        dialog.dismiss()
        val apiService = getRetrofit().create(Playlist::class.java)
        val loadingdialog = KeywordLoadingDialog(requireContext(),"플리를 생성중이에요")
        loadingdialog.setCancelable(false)
        loadingdialog.setCanceledOnTouchOutside(false)
        loadingdialog.show()
        CoroutineScope(Dispatchers.IO).launch() {
            val response = apiService.postPlaylist(categoryName)
            launch {

                withContext(Dispatchers.Main) {
                    try {

                        if (response.isSuccessful) {
                            response.body()?.result?.let{
                                sharedViewModel.addGetPlayList(PlaylistText(it.playlistId,categoryName))
                                playlistName.add(playlistName.size-1,categoryName)
                                adapter.notifyDataSetChanged()
                                playlistId = it.playlistId
                            }
                            binding.keyAudSaveCategorySp.setSelection(playlistName.size-2)

                        } else {
                            response.errorBody()?.let { errorBody ->
                                val gson = Gson()
                                val errorResponse: ErrorResponse = gson.fromJson(errorBody.charStream(), ErrorResponse::class.java)
                            }
                            Log.d("카테고리 추가", "카테고리 추가 실패 : ${response.code()}, ${response.message()}")
                            Toast.makeText(this@KeyvpAudioSaveFragment.requireContext(),"카테고리 추가 실패,\n 오류코드 : ${response.code()}", Toast.LENGTH_SHORT).show()
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    loadingdialog.dismiss()
                }
            }
        }
    }



    private fun initEditText(){

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

    //바로 들으러 가기
    fun getCastInfo(playlistId: Long) {
        val getAllPlaylist = getRetrofit().create(Playlist::class.java)
        val dialog = KeywordLoadingDialog(requireContext(),"이동 중입니다.")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = getAllPlaylist.getPlaylistInfo(playlistId, 0, 20)
                withContext(Dispatchers.Main) { dialog.dismiss() }
                if (response.isSuccessful) {
                    val playlistInfo = response.body()?.result
                    withContext(Dispatchers.Main) {

                        playlistInfo?.let {
                            val castList = it.castList.toMutableList()

                            val castListWithPlaylistId = castList.map{
                                    cast ->
                                CastWithPlaylistId(
                                    castId = cast.castId,
                                    playlistId = playlistId,
                                    castTitle = cast.castTitle?:"제목못받음",//서버에서 바로 저장이 안돼서 그런거같음;
                                    isPublic = cast.isPublic,
                                    castCreator = cast.castCreator,
                                    castCategory = cast.castCategory,
                                    audioLength = cast.audioLength,
                                    imagePath = cast.imagePath
                                )
                            }
                            Log.d("캐스트 저장", castListWithPlaylistId.toString())
                            CastPlayerData.setCast(castListWithPlaylistId, 0)
                            CastPlayerData.setCurrentIndex(castListWithPlaylistId.size-1)

                            ToPlayCast()


                            activity?.finish()
                            finDialog.dismiss()
                        }
                    }
                } else {
                    Log.e("PlaylistCategoryAdapter", "${response.code()}, ${response.errorBody()?.string()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "서버 오류 코드 : ${response.code()}", Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            } catch (e: Exception) {
                Log.e("PlaylistCategoryAdapter", "Exception during API call", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "응답 실패  : ${e.message}", Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
    override fun ToPlayCast() {

        // 현재 서비스가 재생 중인지 확인하고 중지
        //val currentService = getCurrentServiceInstance()
        //  service?.stopAudio()

        // 캐스트 설정 및 새 액티비티로 이동
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    override fun ToEditAudio(id: Long, playlistId: Long) {
        TODO("Not yet implemented")
    }

}


