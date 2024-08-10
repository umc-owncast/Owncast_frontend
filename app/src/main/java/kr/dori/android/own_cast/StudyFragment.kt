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
        cardData("오늘 날씨는 정말 맑고 아름다워요.", "The weather today is really clear and beautiful."),
        cardData("이번 주말에는 친구들과 캠핑을 갈 거예요.", "This weekend, I am going camping with my friends."),
        cardData("새로운 취미로 그림을 배우고 있어요.", "I am learning painting as a new hobby."),
        cardData("책을 읽으면서 마음의 평화를 찾았어요.", "I found peace of mind while reading a book."),
        cardData("저녁 식사로 파스타와 샐러드를 만들었어요.", "I made pasta and salad for dinner."),
        cardData("다음 휴가 계획은 아직 세우지 못했어요.", "I haven't made plans for the next vacation yet."),
        cardData("조용한 카페에서 커피를 마시고 싶어요.", "I want to drink coffee at a quiet cafe."),
        cardData("주말마다 공원에서 조깅을 해요.", "I jog in the park every weekend."),
        cardData("하루 종일 새로운 프로젝트에 몰두했어요.", "I was immersed in a new project all day long."),
        cardData("여행을 통해 많은 것을 배웠어요.", "I learned a lot through traveling."),
        cardData("이번 주는 정말 바쁘고 피곤했어요.", "This week has been really busy and tiring.")
    )

    private lateinit var binding: FragmentStudyBinding
    private val customAdapter = StudyCustomAdapter()
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



        //custom
        customAdapter.itemList = cardData

        // study_custom_adapter_rv 설정
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.studyCustomAdapterRv.layoutManager = layoutManager
        binding.studyCustomAdapterRv.adapter = customAdapter

        // SnapHelper 추가 (아이템 간 스냅 효과) -> 탄력 효과라고 생각하면 됨
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.studyCustomAdapterRv)

        // ScrollListener 추가
        binding.studyCustomAdapterRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                customAdapter.adjustItemSize(recyclerView)
            }
        })

        binding.studyCustomAdapterRv.setPadding(0, 0, 0, 0) // 패딩을 제거하여 아이템 간의 간격 줄이기
        binding.studyCustomAdapterRv.clipToPadding = false  // 패딩이 잘리도록 설정


        // 초기 크기 조정
        binding.studyCustomAdapterRv.post {
            customAdapter.adjustItemSize(binding.studyCustomAdapterRv)
        }

        // 마진 및 패딩 추가 (아이템 간 간격 및 끝 부분 여백)
        val margin = resources.getDimensionPixelSize(R.dimen.study_item_margin)
        binding.studyCustomAdapterRv.addItemDecoration(HorizontalMarginItemDecoration(margin))

        binding.fragmentStudyShuffleIv.setOnClickListener {
            customAdapter.itemList.shuffle()
            customAdapter.notifyDataSetChanged()
        }
        return binding.root
    }
}







