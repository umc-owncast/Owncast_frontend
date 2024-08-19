package kr.dori.android.own_cast.editAudio

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.ActivityEditAudioBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.PlayListInterface
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.PostPlaylist
import kr.dori.android.own_cast.forApiData.UpdateInfo
import kr.dori.android.own_cast.getRetrofit
import kr.dori.android.own_cast.keyword.AddCategoryDialog
import kr.dori.android.own_cast.keyword.KeywordAppData
import kr.dori.android.own_cast.keyword.KeywordLoadingDialog
import kr.dori.android.own_cast.keyword.PlaylistText
import kr.dori.android.own_cast.playlist.AddCategoryListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditAudioActivity : AppCompatActivity(), EditAudio, AddCategoryListener {

    private lateinit var binding: ActivityEditAudioBinding
    var isLock: Boolean = false
    private var playlistText : ArrayList<PlaylistText> = arrayListOf()
    private var selectedIndex : Int = -1
    private var id:Long = -1
    private var playlistId :Long = -1
    private lateinit var editAudioSpinnerAdapter: EditAudioSpinnerAdapter
    private lateinit var context: Context
    private lateinit var loadingDialog : KeywordLoadingDialog
    private var mode : Boolean = true //모드 true = 삭제, false는 추가

    private lateinit var dialog: AddCategoryDialog

    //캐스트 정보 받아올 때 사용
    private lateinit var imageUrl : String
    private lateinit var title : String
    private var isText = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        .into(binding.imageView17)
                    //아래의 코드로 이제 서버쪽으로 이미지를 보낼 수 있게 해줌.
                    body = createMultipartBodyFromUri(it, this)
                }


            }
        }


        binding = ActivityEditAudioBinding.inflate(layoutInflater)
        context = this
        setContentView(binding.root)

        initCategoryData()//카테고리를 받아오고, 성공시 spinner를 초기화
        initCastData()//캐스트도 받아오게
        initEditText()//비어있으면은 수정 못하게
        binding.activityEditAudioOk.setOnClickListener {

        }

        binding.activityEditAudioExitIv.setOnClickListener {
            finish()
        }

        binding.activityEditAudioSwitchOff.setOnClickListener {
            lock(isLock)
            isLock = false
        }

        binding.activityEditAudioSwitchOn.setOnClickListener {
            lock(isLock)
            isLock = true
        }

        binding.activityEditAudioOk.setOnClickListener {// 수정 완료버튼
            editCast()
        }

        binding.activityEditAudioDelete.setOnClickListener {
            mode = true
            deleteCast()

        }
    }

    fun lock(isLock: Boolean) {
        if (isLock) {
            binding.activityEditAudioSwitchOff.visibility = View.VISIBLE
            binding.activityEditAudioSwitchOn.visibility = View.GONE
        } else {
            binding.activityEditAudioSwitchOff.visibility = View.GONE
            binding.activityEditAudioSwitchOn.visibility = View.VISIBLE
        }
    }

    fun showCustomToast(message: String) {
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container))

        val textView: TextView = layout.findViewById(R.id.toast_message_tv)
        textView.text = message

        with (Toast(applicationContext)) {
            duration = Toast.LENGTH_LONG
            view = layout
           // setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
            show()
        }
    }

    override fun dialogToEditAudio() {
        if(mode){
            showCustomToast("캐스트가 삭제되었어요")
            finish()
        }else{
            showCustomToast("캐스트가 추가되었어요")
        }

    }

    fun initCastData(){
        id = intent.getLongExtra("id",-1)
        playlistId = intent.getLongExtra("playlistId",-1)
        val getPlaylistInfo = getRetrofit().create(Playlist::class.java)
        if(id!= (-1L)){
            CoroutineScope(Dispatchers.IO).launch() {
                val response = getPlaylistInfo.getCast(id)
                launch {
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                response.body()?.result?.let{
                                    imageUrl =  it.imagePath
                                    title = it.title
                                    binding.editTextText.setText(title)
                                    if (imageUrl.startsWith("http")) {
                                        // URL로부터 이미지 로드 (Glide 사용)
                                        Glide.with(context)
                                            .load(imageUrl)
                                            .into(binding.imageView17)
                                    } else {
                                        // 로컬 파일에서 이미지 로드
                                        val bitmap = BitmapFactory.decodeFile(imageUrl)
                                        binding.imageView17.setImageBitmap(bitmap)
                                    }
                                    createMultipartBodyFromUrl(imageUrl, context){ part->
                                        if (part != null) {

                                        } else {
                                            Toast.makeText(context,"파일 변환 실패,\n 오류코드 : $${response.code()}",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context,"캐스트 정보 불러오기 실패,\n 오류코드 : $${response.code()}",Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }


                }
            }
        }else{
            Toast.makeText(this,"아이디 정보 받아오기 실패",Toast.LENGTH_SHORT).show()
        }
    }

    fun initCategoryData(){
        val getCategory = getRetrofit().create(PlayListInterface::class.java)
        loadingDialog = KeywordLoadingDialog(this,"데이터를 불러오고 있어요")
        loadingDialog.setCancelable(false)
        loadingDialog.setCanceledOnTouchOutside(false)
        loadingDialog.show()
        CoroutineScope(Dispatchers.IO).launch() {

            val response = getCategory.getPlayListCorutine()
            launch {

                withContext(Dispatchers.Main) {
                    try {
                        loadingDialog.dismiss()
                        if (response.isSuccessful) {
                            response.body()?.result?.let{
                                for(i :Int in 0..it.size-1){
                                    if(playlistId==it[i].playlistId)selectedIndex = i
                                    playlistText.add(PlaylistText(it[i].playlistId,it[i].name))
                                }
                                playlistText.add(PlaylistText(-1,"추가할 카테고리 이름 입력"))
                                initSpinner()
                            }
                        } else {
                            Toast.makeText(context,"재생목록 불러오기 실패,\n 오류코드 : $${response.code()}",Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            }
        }
    }

    private fun initSpinner(){

        dialog = AddCategoryDialog(this, this, this)
        editAudioSpinnerAdapter= EditAudioSpinnerAdapter(this,R.layout.item_aud_set_spinner,playlistText)
        binding.spinner.adapter = editAudioSpinnerAdapter
        binding.spinner.setSelection(selectedIndex)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, p3: Long) {
                when(pos){
                    playlistText.size-1 ->{//이 부분이 카테고리 생성하는 부분, api 추가해주기
                        mode = false
                        binding.spinner.setSelection(selectedIndex)
                        dialog.show()
                    }
                    else ->{
                        selectedIndex = pos
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun editCast(){
        val editCast = getRetrofit().create(CastInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch() {
            val response = editCast.patchCast(id, UpdateInfo(title,imageUrl,isLock),body!!)
            launch {

                withContext(Dispatchers.Main) {
                    try {

                        if (response.isSuccessful) {
                            response.body()?.result?.let{

                                Toast.makeText(context,"수정되었습니다.",Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context,"수정 실패,\n 오류코드 : $${response.code()}",Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    private fun deleteCast(){
        val deleteCast = getRetrofit().create(CastInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch() {
            val response = deleteCast.deleteCast(id)
            launch {

                withContext(Dispatchers.Main) {
                    try {

                        if (response.isSuccessful) {
                            response.body()?.result?.let{

                                Toast.makeText(context,"삭제되었습니다.",Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context,"삭제 실패,\n 오류코드 : $${response.code()}",Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onCategoryAdded(categoryName: String) {
        addPlaylist(categoryName)
        editAudioSpinnerAdapter.notifyDataSetChanged()
    }
    fun addPlaylist(categoryName: String) {//playList추가 버튼
        val apiService = kr.dori.android.own_cast.forApiData.getRetrofit().create(PlayListInterface::class.java)
        apiService.postPlayList(categoryName).enqueue(object: Callback<AuthResponse<PostPlaylist>> {
            override fun onResponse(call: Call<AuthResponse<PostPlaylist>>, response: Response<AuthResponse<PostPlaylist>>) {
                Log.d("apiTest1", response.toString())
                if(response.isSuccessful){
                    val resp : AuthResponse<PostPlaylist> = response.body()!!
                    when(resp.code) {
                        "COMMON200" -> {
                            resp.result?.let {
                                playlistText.add(PlaylistText(it.playlistId,categoryName))
                                binding.spinner.setSelection(playlistText.size-2)
                            }
                            dialog.dismiss()
                            Toast.makeText(context,"추가되었습니다.",Toast.LENGTH_SHORT).show()
                        }
                        else ->{
                        }
                    }
                }else{
                    val resp = response.errorBody().toString()
                    Toast.makeText(context,"잠시 후 다시 시도해주세요.\n실패코드 : ${response.code()}",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<AuthResponse<PostPlaylist>>, t: Throwable) {
                Log.d("apiTest-playlistAdd", t.message.toString())
            }
        })
    }



    private fun initEditText(){

        binding.editTextText.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    title = binding.editTextText.text.toString()
                }
                isText = s?.isNotEmpty() == true
                if (isText) {
                    binding.activityEditAudioOk.visibility = View.GONE
                    binding.activityEditAudioOk.visibility = View.VISIBLE
                } else{
                    binding.activityEditAudioOk.visibility = View.VISIBLE
                    binding.activityEditAudioOk.visibility = View.GONE
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }








    private var body : MultipartBody.Part? = null//이미지 파일을 이에 담아서 요청때 보내야함
    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    //------------------------------------------------------------갤러리 참조용 함수
//OnVIewCreated에서 선언해줌으로써, 이미지를 받아올 수 있는 imageResultLauncher를 초기화 한다.

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

    fun createMultipartBodyFromUrl(url: String, context: Context, callback: (MultipartBody.Part?) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // 요청 실패 처리
                callback(null)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val _body = response.body?.byteStream()
                    if (_body != null) {
                        // 임시 파일 생성
                        val tempFile = File(context.cacheDir, "tempImageFile.png")
                        tempFile.outputStream().use { outputStream ->
                            _body.copyTo(outputStream)
                        }

                        // RequestBody 생성
                        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())

                        // MultipartBody.Part 생성
                        val part = MultipartBody.Part.createFormData("photo", tempFile.name, requestFile)
                        body = part
                        callback(part)

                        // 성공적으로 파일을 다룬 후에는 임시 파일을 삭제할 수 있음
                         tempFile.delete()
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
        })
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
        val permissionStatus = ContextCompat.checkSelfPermission(this, permission)

        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            // 권한 요청
            ActivityCompat.requestPermissions(
                this,
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


}
