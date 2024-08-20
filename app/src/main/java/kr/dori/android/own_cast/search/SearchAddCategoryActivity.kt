package kr.dori.android.own_cast.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.ActivitySearchAddCategoryBinding
import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.GetAllPlaylist
import kr.dori.android.own_cast.forApiData.GetUserPlaylist
import kr.dori.android.own_cast.forApiData.PlayListInterface
import kr.dori.android.own_cast.forApiData.UpdateInfo
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.KeywordLoadingDialog
import kr.dori.android.own_cast.keyword.PlaylistText
import kr.dori.android.own_cast.player.CastWithPlaylistId
import kr.dori.android.own_cast.playlist.AddCategoryAdapter

class SearchAddCategoryActivity : AppCompatActivity(), SearchMover {


    private lateinit var binding: ActivitySearchAddCategoryBinding
    private var searchadapter = AddCategoryAdapter(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val playlist = intent.getSerializableExtra("categoryList") as? ArrayList<GetAllPlaylist>
        val id = intent.getLongExtra("id",-1)
        playlist?.let{
            it.removeAt(0)
            it.removeAt(0)
            searchadapter.dataList = it
        } ?: run {
            initCategoryData()
        }
        id?.let {
            searchadapter.id = it
        }
        if(playlist == null){
            Toast.makeText(this,"잠시후 다시 시도해주세요",Toast.LENGTH_SHORT)
        }
        binding.activitySearchAddCategoryRv.adapter = searchadapter
        binding.activitySearchAddCategoryRv.layoutManager = LinearLayoutManager(this)
        binding.activitySearchAddCategoryExitIv.setOnClickListener {
            finish()
        }
    }

    override fun goPlayCast(list: List<CastHomeDTO>, id:Long) {
        TODO("Not yet implemented")
    }

    override fun goAddCast(id:Long) {
        TODO("Not yet implemented")
    }

    override fun backSearch() {
        showCustomToast("(카테고리 이름)에 담아드렸어요")
        finish()
    }

    fun showCustomToast(message: String) {
        // Inflate the custom layout
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container))

        // Set custom message
        val textView: TextView = layout.findViewById(R.id.toast_message_tv)
        textView.text = message

        // Create and show the Toast
        with (Toast(applicationContext)) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    fun initCategoryData(){
        val getCategory = getRetrofit().create(PlayListInterface::class.java)
        val loadingDialog = KeywordLoadingDialog(this,"데이터를 불러오고 있어요")
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
                                var data = it.map{
                                    GetAllPlaylist(
                                        name = it.name,
                                        imagePath = it.imagePath,
                                        playlistId = it.playlistId,
                                        totalCast = it.totalCast
                                    )
                                }
                                searchadapter.dataList.addAll(data)
                            }
                        } else {
                            Toast.makeText(this@SearchAddCategoryActivity,"재생목록 불러오기 실패,\n 오류코드 : $${response.code()}",Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            }
        }
    }
}
