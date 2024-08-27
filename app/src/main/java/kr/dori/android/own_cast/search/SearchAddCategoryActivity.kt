package kr.dori.android.own_cast.search

import android.os.Bundle
import android.util.Log
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
import kr.dori.android.own_cast.data.CastPlayerData
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
    private lateinit var searchadapter : AddCategoryAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //add해야되는 부분이 검색과 플레이 캐스트 액티비티이므로, 차라리 currentCast받아오게 변경
        searchadapter = AddCategoryAdapter(this,this)
        initCategoryData()//재생목록 받아온다




        binding.activitySearchAddCategoryExitIv.setOnClickListener {
            finish()
        }
    }

    override fun goPlayCast(list: List<CastHomeDTO>, id:Long) {
        TODO("Not yet implemented")
    }

    override fun goAddCast(castHomeDTO: CastHomeDTO) {
        TODO("Not yet implemented")
    }

    override fun backSearch() {
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
        val loadingDialog = KeywordLoadingDialog(this,"재생목록을 불러오고 있어요")
        Log.d("캐스트 추가", "함수 진입")
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
                                //추가할때도 검색한 캐스트를 currentCast로 설정해버림

                                searchadapter.id = CastPlayerData.currentCast.castId
                                searchadapter.dataList.addAll(data)
                                searchadapter.dataList.removeAt(0)
                                searchadapter.dataList.removeAt(0)
                                binding.activitySearchAddCategoryRv.adapter = searchadapter
                                binding.activitySearchAddCategoryRv.layoutManager = LinearLayoutManager(this@SearchAddCategoryActivity)
                                Log.d("캐스트 추가","성공 \n ${data.toString()}")
                            }
                        } else {
                            Log.d("캐스트 추가","실패 ${response.code()}")
                            Toast.makeText(this@SearchAddCategoryActivity,"재생목록 불러오기 실패,\n 오류코드 : $${response.code()}",Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Log.d("캐스트 추가","실패 ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
