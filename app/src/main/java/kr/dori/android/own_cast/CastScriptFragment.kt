package kr.dori.android.own_cast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.databinding.FragmentCastScriptBinding

class CastScriptFragment : Fragment() {
    lateinit var binding: FragmentCastScriptBinding
    private val playCastViewModel: PlayCastViewModel2 by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCastScriptBinding.inflate(inflater, container, false)

        val rawLyrics = """
            [00:00:000]당신은 사랑받기 위해 태어난 사람,\n i love kotlin, i lover android
            [00:05:000]당신의 삶속에서 그 사랑받고 있지요. \n i love kotlin, i lover android
            [00:10:000]당신은 사랑받기 위해 태어난 사람,\n i love kotlin, i lover android
            [00:15:000]당신의 삶속에서 그 사랑받고 있지요.\n i love kotlin, i lover android
            [00:20:000]태초부터 시작된 하나님의 사랑은,\n i love kotlin, i lover android
            [00:25:000]우리의 만남을 통해 열매를 맺고,\n i love kotlin, i lover android
            [00:30:000]당신이 이세상에 존재함으로 인해,\n i love kotlin, i lover android
            [00:35:000]우리에게 얼마나 큰 기쁨이 되는지.\n i love kotlin, i lover android
            [00:40:000]당신은 사랑받기 위해 태어난 사람,\n i love kotlin, i lover android
            [00:45:000]지금도 그 사랑 받고있지요.\n i love kotlin, i lover android
            [00:50:000]당신은 사랑받기 위해 태어난 사람,\n i love kotlin, i lover android
            [00:55:000]지금도 그 사랑 받고있지요.\n i love kotlin, i lover android
            [01:00:000]당신은 사랑받기 위해 태어난 사람,\n i love kotlin, i lover android
            [01:05:000]당신의 삶속에서 그 사랑 받고있지요.\n i love kotlin, i lover android
            [01:10:000]당신은 사랑받기위해 태어난 사람,\n i love kotlin, i lover android
            [01:15:000]당신의 삶속에서 그 사랑받고 있지요.\n i love kotlin, i lover android
        """

        val lyricsList = parseLyrics(rawLyrics)

        val adapter = ScriptAdapter()
        adapter.dataList = lyricsList
        binding.scriptRv.adapter = adapter
        binding.scriptRv.layoutManager = LinearLayoutManager(context)

        playCastViewModel.currentTime.observe(viewLifecycleOwner, Observer { currentTime ->
            adapter.updateCurrentTime(currentTime)
        })

        return binding.root
    }

    private fun parseLyrics(rawLyrics: String): List<Lyrics> {
        val regex = "\\[(\\d{2}):(\\d{2}):(\\d{3})]".toRegex()
        val matches = regex.findAll(rawLyrics)

        val lyricsList = mutableListOf<Lyrics>()
        var lastIndex = 0
        matches.forEach { matchResult ->
            val (minutes, seconds, millis) = matchResult.destructured
            val timeInMillis = minutes.toLong() * 60000 + seconds.toLong() * 1000 + millis.toLong()

            val startIndex = matchResult.range.last + 1
            val endIndex = rawLyrics.indexOf("[", startIndex)
            val text = if (endIndex != -1) rawLyrics.substring(startIndex, endIndex).trim() else rawLyrics.substring(startIndex).trim()

            lyricsList.add(Lyrics(timeInMillis, text))
            lastIndex = endIndex
        }

        return lyricsList
    }
}
