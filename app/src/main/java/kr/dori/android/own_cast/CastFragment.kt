package kr.dori.android.own_cast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.databinding.FragmentCastBinding

class CastFragment : Fragment() {
    private lateinit var binding: FragmentCastBinding
    private lateinit var castAdapter: CastAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCastBinding.inflate(inflater, container, false)

        // CastAdapter 초기화
        castAdapter = CastAdapter()

        // ViewModel 데이터 관찰
        sharedViewModel.data.observe(viewLifecycleOwner, Observer { newData ->
            val savedData = arguments?.getParcelableArrayList<SongData>("isSave")
            val unsavedData = arguments?.getParcelableArrayList<SongData>("isNotSave")
            val data = savedData ?: unsavedData ?: arrayListOf()
            castAdapter.dataList = data
            castAdapter.notifyDataSetChanged()
        })

        // Bundle에서 데이터를 읽어오기
        val savedData: ArrayList<SongData>? = arguments?.getParcelableArrayList("isSave")
        val unsavedData: ArrayList<SongData>? = arguments?.getParcelableArrayList("isNotSave")

        // 데이터가 없을 경우 빈 리스트를 사용
        val data = savedData ?: unsavedData ?: arrayListOf()

        // CastAdapter에 데이터 설정
        castAdapter.dataList = data

        if (savedData != null) {
            binding.fragmentCastMaintitleTv.text = "저장한 캐스트"
            binding.fragmentCastTitleTv.text = "${castAdapter.itemCount},"
        } else {
            binding.fragmentCastMaintitleTv.text = "담아온 캐스트"
            binding.fragmentCastTitleTv.text = "${castAdapter.itemCount}"
        }

        // RecyclerView 설정
        binding.fragmentCastRv.adapter = castAdapter
        binding.fragmentCastRv.layoutManager = LinearLayoutManager(context)

        // Back 버튼 클릭 이벤트 처리
        binding.fragmentCastBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }
}







