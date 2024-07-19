package kr.dori.android.own_cast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.databinding.FragmentCastBinding

class CastFragment : Fragment() {
    private lateinit var binding: FragmentCastBinding
    private lateinit var castAdapter: CastAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCastBinding.inflate(inflater,container,false)

        // CastAdapter 초기화
        castAdapter = CastAdapter()

        // Bundle에서 데이터를 읽어오기
        val savedData: ArrayList<SongData>? = arguments?.getParcelableArrayList("isSave")
        val unsavedData: ArrayList<SongData>? = arguments?.getParcelableArrayList("isNotSave")

        // 데이터가 없을 경우 빈 리스트를 사용
        val data = savedData ?: unsavedData ?: arrayListOf()

        // CastAdapter에 데이터 설정
        castAdapter.dataList = data

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
