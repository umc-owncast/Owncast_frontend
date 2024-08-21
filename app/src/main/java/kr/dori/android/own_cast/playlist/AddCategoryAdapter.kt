package kr.dori.android.own_cast.playlist

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.search.SearchMover
import kr.dori.android.own_cast.databinding.PlaylistCategoryItemBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.ErrorResponse
import kr.dori.android.own_cast.forApiData.GetAllPlaylist
import kr.dori.android.own_cast.forApiData.PostOtherPlaylist
import kr.dori.android.own_cast.forApiData.PostOtherPlaylistCast
import kr.dori.android.own_cast.forApiData.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCategoryAdapter(private val mover: SearchMover) : RecyclerView.Adapter<AddCategoryAdapter.Holder>() {
    var dataList: ArrayList<GetAllPlaylist> = arrayListOf()
    var id:Long = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = PlaylistCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]

        holder.setText(data, holder)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: PlaylistCategoryItemBinding):RecyclerView.ViewHolder(binding.root){

        init{

        }

        fun setText(data: GetAllPlaylist, holder: Holder){
            binding.playlistCategoryEditIv.visibility = View.GONE
            binding.playlistCategoryPlayIv.visibility = View.GONE
            binding.playlistCategoryTitleTv.text = data.name
            binding.playlistCategoryNumTv.text = data.totalCast.toString()
            Log.d("apitest-caregory","사진경로${data.imagePath}")
            if (data.imagePath.startsWith("http")) {
                // URL로부터 이미지 로드 (Glide 사용)
                Glide.with(holder.itemView.context)
                    .load(data.imagePath)
                    .into(binding.categoryImg)
            } else {
                // 로컬 파일에서 이미지 로드
                val bitmap = BitmapFactory.decodeFile(data.imagePath)
                binding.categoryImg.setImageBitmap(bitmap)
            }
            binding.root.setOnClickListener {
                if(id!=(-1L)){
                    saveOtherCast(id,data.playlistId)
                }else{

                }


            }
        }
    }

    fun saveOtherCast(castId:Long, playlistId:Long){
        val apiService = getRetrofit().create(CastInterface::class.java)
        apiService.postOtherPlaylistCast(PostOtherPlaylistCast(castId, playlistId)).enqueue(object: Callback<AuthResponse<PostOtherPlaylist>> {
            override fun onResponse(call: Call<AuthResponse<PostOtherPlaylist>>, response: Response<AuthResponse<PostOtherPlaylist>>) {
                Log.d("apiTest-저장","${castId},${playlistId},${response.toString()}")
                if(response.code().equals("COMMON200")){
                    val resp: AuthResponse<PostOtherPlaylist> = response.body()!!
                    when(resp.code) {
                        "COMMON200" -> {
                            Log.d("apiTest-저장","저장성공, resp값: ${resp.result.toString()}}")
                            mover.backSearch()
                        }
                        else ->{
                            Log.d("apiTest-저장","연결실패 코드 : ${resp.code}, ${resp.message}")

                        }
                    }
                }
                else{
                    val resp= response.errorBody()?.string()
                    resp?.let {
                        try {
                            // Gson을 사용해 에러 응답을 파싱
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ErrorResponse::class.java)
                            Log.d("apiTest-저장", "오류 발생: ${errorResponse.code}, ${errorResponse.message}")
                        } catch (e: Exception) {
                            Log.d("apiTest-저장", "에러 응답 파싱 실패: ${e.message}")
                        }
                    } ?: run {
                        Log.d("apiTest-저장", "에러 바디가 없음: ${response.code()}")
                    }
                }
                mover.backSearch()
            }
            override fun onFailure(call: Call<AuthResponse<PostOtherPlaylist>>, t: Throwable) {
                Log.d("apiTest-searchHome","연결실패 ${t.message}")
                mover.backSearch()
            }
        })
    }
}
