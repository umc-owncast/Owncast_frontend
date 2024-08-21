package kr.dori.android.own_cast.playlist

import android.content.Context
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

class AddCategoryAdapter(val context: Context, private val mover: SearchMover) : RecyclerView.Adapter<AddCategoryAdapter.Holder>() {
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
                    Toast.makeText(context," 캐스트아이디 오류",Toast.LENGTH_SHORT).show()
                }


            }
        }
    }

    fun saveOtherCast(castId:Long, playlistId:Long){
        val apiService = getRetrofit().create(CastInterface::class.java)
        apiService.postOtherPlaylistCast(PostOtherPlaylistCast(castId, playlistId)).enqueue(object: Callback<AuthResponse<PostOtherPlaylist>> {
            override fun onResponse(call: Call<AuthResponse<PostOtherPlaylist>>, response: Response<AuthResponse<PostOtherPlaylist>>) {
                Log.d("apiTest-저장","${castId},${playlistId},${response.toString()}")
                if(response.isSuccessful){
                    Toast.makeText(context,"저장 성공",Toast.LENGTH_SHORT).show()
                }
                else{
                    Log.d("apiTest-searchHome","연결실패 ${response.code()}")
                    Log.d("apiTest-searchHome","연결실패 ${response.body()?.result}")
                    Log.d("apiTest-searchHome","연결실패 ${response.body()?.message}")
                    response.errorBody()?.let{
                        Toast.makeText(context,"이미 추가된 캐스트 입니다.",Toast.LENGTH_SHORT).show()
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
