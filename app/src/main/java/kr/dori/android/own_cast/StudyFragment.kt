package kr.dori.android.own_cast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.databinding.FragmentStudyBinding

class StudyFragment : Fragment() {

    private var cardData = mutableListOf(
        cardData("안녕하세요", "hi"),
        cardData("반갑습니다", "hi"),
        cardData("반갑습니다", "hi"),
        cardData("반갑습니다", "hi"),
        cardData("반갑습니다", "hi"),
        cardData("반갑습니다", "hi"),
        cardData("반갑습니다", "hi"),
        cardData("반갑습니다", "hi"),
        cardData("반갑습니다", "hi"),
        cardData("반갑습니다", "hi"),
        cardData("반갑습니다", "hi")
    )

    private lateinit var binding: FragmentStudyBinding
    private val categoryAdapter = StudyCustomAdapter(cardData)
    private val studyAdapter = StudyAdapter()

    private var dummyData = mutableListOf(
        SongData("category_name1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("category_name2", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("category_name3", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("category_name4", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("category_name5", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug"),
        SongData("category_name6", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("category_name7", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("category_name8", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("category_name9", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("category_name10", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudyBinding.inflate(inflater, container, false)

        studyAdapter.dataList = dummyData
        binding.studyCategoryRv.adapter = studyAdapter
        binding.studyCategoryRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.fragmentStudyLoofOffIv.setOnClickListener {
            binding.fragmentStudyLoofOnIv.visibility = View.VISIBLE
            binding.fragmentStudyLoofOffIv.visibility = View.GONE
        }

        binding.fragmentStudyLoofOnIv.setOnClickListener {
            binding.fragmentStudyLoofOffIv.visibility = View.VISIBLE
            binding.fragmentStudyLoofOnIv.visibility = View.GONE
        }

        binding.fragmentStudySoundOffIv.setOnClickListener {
            binding.fragmentStudySoundOnIv.visibility = View.VISIBLE
            binding.fragmentStudySoundOffIv.visibility = View.GONE
        }

        binding.fragmentStudySoundOnIv.setOnClickListener {
            binding.fragmentStudySoundOffIv.visibility = View.VISIBLE
            binding.fragmentStudySoundOnIv.visibility = View.GONE
        }

        // study_custom_adapter_rv 설정
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.studyCustomAdapterRv.layoutManager = layoutManager
        binding.studyCustomAdapterRv.adapter = categoryAdapter

        // SnapHelper 추가 (아이템 간 스냅 효과)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.studyCustomAdapterRv)

        // ScrollListener 추가
        binding.studyCustomAdapterRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                categoryAdapter.adjustItemSize(recyclerView)
            }
        })

        // 초기 크기 조정
        binding.studyCustomAdapterRv.post {
            categoryAdapter.adjustItemSize(binding.studyCustomAdapterRv)
        }

        // 마진 및 패딩 추가 (아이템 간 간격 및 끝 부분 여백)
        val margin = resources.getDimensionPixelSize(R.dimen.study_item_margin)
        binding.studyCustomAdapterRv.addItemDecoration(HorizontalMarginItemDecoration(margin))

        return binding.root
    }
}







