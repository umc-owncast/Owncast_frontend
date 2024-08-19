import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.databinding.ScriptItemBinding
import kr.dori.android.own_cast.forApiData.Bookmark
import kr.dori.android.own_cast.forApiData.NewSentences
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.player.CastWithPlaylistId
import retrofit2.create

class ScriptAdapter(val currentCast: CastWithPlaylistId) : RecyclerView.Adapter<ScriptAdapter.Holder>() {
    var dataList: List<NewSentences> = emptyList() //여기에 센텐스 아이디도 담겨있음
    var bookmarkList: List<String> = emptyList()

    var onRepeatToggleListener: ((position: Int, isRepeatOn: Boolean) -> Unit)? = null

    private var currentHighlightedPosition: Int = -1
    private var repeatPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScriptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    fun updateCurrentTime(currentTime: Long) {
        val newHighlightedPosition = findCurrentPosition(currentTime)
        if (newHighlightedPosition != currentHighlightedPosition) {
            currentHighlightedPosition = newHighlightedPosition
            notifyDataSetChanged()
        }
    }

    private fun findCurrentPosition(currentTime: Long): Int {
        for (i in dataList.indices) {
            val sentence = dataList[i]
            val sentenceTimePoint = (sentence.timePoint * 1000).toLong()
            val nextSentenceTime = if (i + 1 < dataList.size) {
                (dataList[i + 1].timePoint * 1000).toLong()
            } else {
                Long.MAX_VALUE
            }

            if (currentTime >= sentenceTimePoint && currentTime < nextSentenceTime) {
                return i
            }
        }
        return -1
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.bind(data, position == currentHighlightedPosition, position == repeatPosition)
    }

    override fun getItemCount(): Int = dataList.size

    inner class Holder(val binding: ScriptItemBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(data: NewSentences, isHighlighted: Boolean, isRepeatOn: Boolean) {
            binding.originalSentenceTv.text = data.originalSentence
            binding.translationSentenceTv.text = data.translatedSentence

/*
            // 북마크 리스트에 현재 문장이 있는지 확인
            if (bookmarkList.contains(data.originalSentence)) {
                binding.onFocusOn.visibility = View.VISIBLE
                binding.notFocusOff.visibility = View.GONE
            } else {
                binding.onFocusOn.visibility = View.GONE
                binding.notFocusOff.visibility = View.VISIBLE
            }

            binding.onFocusOn.setOnClickListener {
                binding.onFocusOn.visibility = View.GONE
                binding.notFocusOff.visibility = View.VISIBLE
                deleteBookmark(data.id.toInt())
            }

            binding.notFocusOff.setOnClickListener {
                binding.onFocusOn.visibility = View.VISIBLE
                binding.notFocusOff.visibility = View.GONE
                postBookmark(data.id.toInt())
            }
 */

            if (isHighlighted) {
                binding.translationSentenceTv.setTextColor(Color.parseColor("#000000"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#000000"))

                if (isRepeatOn) {
                    binding.loofOff.visibility = View.GONE
                    binding.loofOn.visibility = View.VISIBLE
                } else {
                    binding.loofOff.visibility = View.VISIBLE
                    binding.loofOn.visibility = View.GONE
                }

                binding.loofOff.setOnClickListener {
                    binding.loofOff.visibility = View.GONE
                    binding.loofOn.visibility = View.VISIBLE
                    repeatPosition = adapterPosition
                    onRepeatToggleListener?.invoke(adapterPosition, true)
                    notifyDataSetChanged()
                }

                binding.loofOn.setOnClickListener {
                    binding.loofOff.visibility = View.VISIBLE
                    binding.loofOn.visibility = View.GONE
                    repeatPosition = null
                    onRepeatToggleListener?.invoke(adapterPosition, false)
                    notifyDataSetChanged()
                }
            } else {
                binding.translationSentenceTv.setTextColor(Color.parseColor("#808080"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#808080"))
                binding.loofOff.visibility = View.VISIBLE
                binding.loofOn.visibility = View.GONE
            }
        }
    }

    // Suspend function to get bookmark sentences
    suspend fun getBookmarkSentence(currentCast: CastWithPlaylistId): List<String>? {
        val getBookmark = getRetrofit().create(Bookmark::class.java)
        return try {
            val response = getBookmark.getBookmark(currentCast.playlistId)

            Log.d("sex","${response.body()?.result?.filter { bookmark ->
                bookmark.castId == currentCast.castId
            }?.map { it.originalSentence } }")

            if (response.isSuccessful) {
                response.body()?.result?.filter { bookmark ->
                    bookmark.castId == currentCast.castId
                }?.map { it.originalSentence }

            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun postBookmark(sentenceId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val postBookmark = getRetrofit().create(Bookmark::class.java)
            try{
                val response = postBookmark.postBookmark(sentenceId)
                //데이터 업데이트
                loadBookmarkData()
                notifyDataSetChanged()
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun deleteBookmark(sentenceId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val postBookmark = getRetrofit().create(Bookmark::class.java)
            try{
                val response = postBookmark.deleteBookmark(sentenceId)
                //데이터 업데이트
                loadBookmarkData()
                notifyDataSetChanged()
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun loadBookmarkData() {
        CoroutineScope(Dispatchers.Main).launch {
            val bookmarks = withContext(Dispatchers.IO) {
                getBookmarkSentence(currentCast)
            }
            if (bookmarks != null) {
                bookmarkList = bookmarks
                Log.d("sex1","$bookmarkList")
                notifyDataSetChanged() // Update the adapter's data after loading the bookmarks
            }
        }
    }
}
