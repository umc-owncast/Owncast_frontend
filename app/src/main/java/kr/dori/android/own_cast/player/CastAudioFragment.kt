package kr.dori.android.own_cast.player

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.FragmentCastAudioBinding
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.CastInfo
import kr.dori.android.own_cast.forApiData.NewSentences
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.Sentences
import kr.dori.android.own_cast.forApiData.getRetrofit

class CastAudioFragment(val currentCast: CastWithPlaylistId) : Fragment() {

    lateinit var binding: FragmentCastAudioBinding
    lateinit var castInfo: CastInfo
    private var sentences : List<NewSentences> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCastAudioBinding.inflate(inflater,container,false)

        initCastData()

        //이미지 추가


        val getScript = getRetrofit().create(Playlist::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val castInfoDeferred = async {
                val response = getScript.getCast(currentCast.castId)
                if (response.isSuccessful) {
                    response.body()?.result
                } else {
                    null
                }
            }
            castInfo = castInfoDeferred.await() ?: CastInfo(0, "", "", "", "", emptyList())
            sentences = castInfo.sentences
            Log.d("센텐스 체크", sentences.toString())
        }



        return binding.root

    }

    fun setCastTitle(castTitle: String){
        binding.castTitle.text = castTitle
    }

    fun updateCurrentTime(currentTime: Long) {
        Log.d("UpdateTime", "Fragment: $currentTime")
        val newHighlightedPosition = findCurrentPosition(currentTime)
        if(!sentences.isNullOrEmpty()){
            binding.textView11.text = sentences[newHighlightedPosition].originalSentence
            binding.textView25.text = sentences[newHighlightedPosition].translatedSentence
        }

    }

    private fun findCurrentPosition(currentTime: Long): Int {
        for (i:Int in 0 until sentences.size) {
            val previousSentenceTime = if (i > 0){
                (sentences[i-1].timePoint * 1000).toLong()
            } else {
                (sentences[0].timePoint * 1000).toLong()
            }

            val sentenceTimePoint = (sentences[i].timePoint * 1000).toLong()

            Log.d("ScriptAdapter", "Checking sentence $i: previousSentenceTime = $previousSentenceTime, sentenceTimePoint = $sentenceTimePoint")

            if (currentTime >= previousSentenceTime && currentTime < sentenceTimePoint) {
                Log.d("ScriptAdapter", "Current sentence index: $i")
                return i - 1
            }else if(i == sentences.size-1&&currentTime>sentenceTimePoint) return i
        }
        return 0
    }

    fun initCastData(){
        Glide.with(binding.root.context).load(currentCast.imagePath).centerCrop().into(binding.imageView)
        binding.castTitle.text = currentCast.castTitle

        if(currentCast.castCreator != SignupData.nickname){
            binding.creatorCategory.text = "${currentCast.castCreator}-${currentCast.castCategory}"
        }else{
            binding.creatorCategory.visibility = View.GONE
        }
    }

}