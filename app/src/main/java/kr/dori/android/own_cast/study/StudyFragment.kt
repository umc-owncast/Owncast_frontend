package kr.dori.android.own_cast.study

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.PlaylistInfo
import kr.dori.android.own_cast.data.cardData
import kr.dori.android.own_cast.databinding.FragmentStudyBinding
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit

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

    private var dataCount = cardData.size

    private val snapHelper = LinearSnapHelper()
    private lateinit var binding: FragmentStudyBinding
    private val customAdapter = StudyCustomAdapter()
    private lateinit var studyAdapter: StudyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding 초기화
        binding = FragmentStudyBinding.inflate(inflater, container, false)

        // 어댑터 초기화
        studyAdapter = StudyAdapter()

        // RecyclerView 설정
        setupRecyclerView()

        // 데이터 로드 및 설정
        loadPlaylistData()

        // UI 초기화
        setupUI()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.studyCategoryRv.adapter = studyAdapter
        binding.studyCategoryRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        studyAdapter.selectedPosition = 0

        // 커스텀 어댑터 초기화
        customAdapter.itemList = cardData

        // 리사이클러뷰 설정
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.studyCustomAdapterRv.layoutManager = layoutManager
        binding.studyCustomAdapterRv.adapter = customAdapter

        // SnapHelper 추가
        snapHelper.attachToRecyclerView(binding.studyCustomAdapterRv)

        // ScrollListener 추가
        binding.studyCustomAdapterRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                customAdapter.adjustItemSize(recyclerView)

                // 중앙에 위치한 아이템의 포지션 계산 및 UI 업데이트
                val centerView = snapHelper.findSnapView(layoutManager)
                centerView?.let {
                    val position = layoutManager.getPosition(it)
                    val actualPosition = position % dataCount
                    binding.fragmentStudyStateTv.text = "${setText(actualPosition)}/$dataCount"
                    Log.d("text", "$actualPosition")
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                    if (firstPosition <= dataCount) {
                        recyclerView.scrollToPosition(firstPosition + dataCount)
                    } else if (lastPosition >= layoutManager.itemCount - dataCount) {
                        recyclerView.scrollToPosition(lastPosition - dataCount)
                    }
                }
            }
        })

        // 초기 크기 조정
        binding.studyCustomAdapterRv.post {
            customAdapter.adjustItemSize(binding.studyCustomAdapterRv)
        }

        // 마진 및 패딩 추가
        val margin = resources.getDimensionPixelSize(R.dimen.study_item_margin)
        binding.studyCustomAdapterRv.addItemDecoration(HorizontalMarginItemDecoration(margin))
    }

    private fun loadPlaylistData() {
        CoroutineScope(Dispatchers.IO).launch {
            val getAllPlaylist = getRetrofit().create(Playlist::class.java)

            try {
                val response = getAllPlaylist.getAllPlaylist()
                if (response.isSuccessful) {
                    val allPlaylist = response.body()?.result

                    // 필터링 및 정렬 로직
                    val filteredList = mutableListOf<PlaylistInfo>()

                    allPlaylist?.let { playlists ->
                        // 이름이 "내가 만든 캐스트"인 아이템을 1번째에 추가
                        playlists.find { it.name == "내가 만든 캐스트" }?.let {
                            filteredList.add(
                                PlaylistInfo(it.playlistId, it.name, it.imagePath)
                            )
                        }

                        // 이름이 "담아온 캐스트"인 아이템을 2번째에 추가
                        playlists.find { it.name == "담아온 캐스트" }?.let {
                            filteredList.add(
                                PlaylistInfo(it.playlistId, it.name, it.imagePath)
                            )
                        }

                        // playlistId가 0이 아닌 것들을 나머지로 추가
                        playlists.filter { it.playlistId != 0L }.forEach {
                            filteredList.add(
                                PlaylistInfo(it.playlistId, it.name, it.imagePath)
                            )
                        }
                    }

                    withContext(Dispatchers.Main) {
                        // 어댑터에 데이터 설정 및 UI 업데이트
                        studyAdapter.dataList = filteredList
                        studyAdapter.notifyDataSetChanged()
                    }

                    Log.d("allPlaylist", "$filteredList")

                } else {
                    // 처리 실패 시 로직
                    Log.e("StudyFragment", "Failed to load playlists")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("StudyFragment", "Error loading playlists", e)
            }
        }
    }

    private fun setupUI() {
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

        // Next 버튼 클릭 시 동작
        binding.fragmentStudyNextIv.setOnClickListener {
            scrollToNextItem()
        }

        binding.fragmentStudyBackIv.setOnClickListener {
            scrollToBackItem()
        }

        binding.fragmentStudyShuffleIv.setOnClickListener {
            customAdapter.itemList.shuffle()
            customAdapter.notifyDataSetChanged()
        }

        binding.studyCustomAdapterRv.scrollToPosition(5)
        binding.studyCustomAdapterRv.post {
            val layoutManager = binding.studyCustomAdapterRv.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(
                5,
                (binding.studyCustomAdapterRv.width / 2) - (binding.studyCustomAdapterRv.getChildAt(0).width / 2)
            )

            binding.studyCustomAdapterRv.post {
                customAdapter.adjustItemSize(binding.studyCustomAdapterRv)
                val centerView = snapHelper.findSnapView(layoutManager)
                centerView?.let {
                    val position = layoutManager.getPosition(it)
                    val actualPosition = position % dataCount
                    binding.fragmentStudyStateTv.text = "${setText(actualPosition)}/$dataCount"
                    Log.d("text", "$actualPosition")
                }
            }
        }
    }

    private fun scrollToNextItem() {
        val layoutManager = binding.studyCustomAdapterRv.layoutManager as LinearLayoutManager
        val centerView = snapHelper.findSnapView(layoutManager)
        centerView?.let {
            val position = layoutManager.getPosition(it)
            binding.studyCustomAdapterRv.smoothScrollToPosition(position + 1)
        }
    }

    private fun scrollToBackItem() {
        val layoutManager = binding.studyCustomAdapterRv.layoutManager as LinearLayoutManager
        val centerView = snapHelper.findSnapView(layoutManager)
        centerView?.let {
            val position = layoutManager.getPosition(it)
            binding.studyCustomAdapterRv.smoothScrollToPosition(position - 1)
        }
    }

    private fun setText(actualPosition: Int): Int {
        return if (actualPosition >= 5) {
            actualPosition - 4
        } else {
            actualPosition + dataCount - 4
        }
    }
}
