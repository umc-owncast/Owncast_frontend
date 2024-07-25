/*package kr.dori.android.own_cast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.databinding.FragmentPlaylistBinding

class PlaylistFragment : Fragment(), AddCategoryListener, EditCategoryListener {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var categoryAdapter: PlaylistCategoryAdapter

    // 나중에 서버 배포로 대체
    private var data = mutableListOf(
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug"),
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("category_name", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        // RecyclerView 어댑터 설정
        categoryAdapter = PlaylistCategoryAdapter(this)
        categoryAdapter.dataList = data
        binding.category.adapter = categoryAdapter
        binding.category.layoutManager = LinearLayoutManager(context)

        // 다이얼로그 구현
        binding.fragmentPlaylistAddIv.setOnClickListener {
            val dialog = AddCategoryDialog(requireContext(), this)
            dialog.show()
        }

        // 저장한 캐스트, 담아온 캐스트 설정
        val savedData = data.filter { it.isSave }
        val unsavedData = data.filter { !it.isSave }
        val castFragment = CastFragment()

        binding.fragmentPlaylistSaveIv.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList("isSave", ArrayList(savedData))
            }
            castFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, castFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.fragmentPlaylistNotsaveIv.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList("isNotSave", ArrayList(unsavedData))
            }
            castFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, castFragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    // notifyDataSetChanged로 데이터 업데이트
    override fun onCategoryAdded(categoryName: String) {
        data.add(SongData(categoryName, R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug"))
        categoryAdapter.notifyDataSetChanged()
    }

    override fun onCategoryEdit(position: Int, newItem: SongData) {
        data[position] = newItem
        categoryAdapter.notifyDataSetChanged()
    }
}*/
