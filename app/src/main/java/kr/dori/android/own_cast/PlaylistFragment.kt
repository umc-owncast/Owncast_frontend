package kr.dori.android.own_cast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.databinding.FragmentPlaylistBinding

class PlaylistFragment : Fragment(), AddCategoryListener, EditCategoryListener {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var categoryAdapter: PlaylistCategoryAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()

    //나중에 서버 데이터로 변경
    private val dummyData = mutableListOf(
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
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        // RecyclerView 어댑터 설정
        categoryAdapter = PlaylistCategoryAdapter(this)
        binding.category.adapter = categoryAdapter
        binding.category.layoutManager = LinearLayoutManager(context)

        // ViewModel 데이터 관찰
        sharedViewModel.data.observe(viewLifecycleOwner, Observer { newData ->
            categoryAdapter.dataList = newData
            categoryAdapter.notifyDataSetChanged()
        })

        // ViewModel에 초기 데이터 설정
        if (sharedViewModel.data.value.isNullOrEmpty()) {
            sharedViewModel.setData(dummyData)
        }

        // 다이얼로그 구현
        binding.fragmentPlaylistAddIv.setOnClickListener {
            val dialog = AddCategoryDialog(requireContext(), this)
            dialog.show()
        }

        // 저장한 캐스트, 담아온 캐스트 설정
        val castFragment = CastFragment()

        binding.fragmentPlaylistSaveIv.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList("isSave", ArrayList(sharedViewModel.data.value?.filter { it.isSave } ?: listOf()))
            }
            castFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, castFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.fragmentPlaylistNotsaveIv.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList("isNotSave", ArrayList(sharedViewModel.data.value?.filter { !it.isSave } ?: listOf()))
            }
            castFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, castFragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    override fun onCategoryAdded(categoryName: String) {
        val newItem = SongData(categoryName, R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug")
        sharedViewModel.addData(newItem)
    }

    override fun onCategoryEdit(position: Int, newItem: SongData) {
        sharedViewModel.updateDataAt(position, newItem)
    }

    override fun getCategoryData(position: Int): SongData {
        return sharedViewModel.data.value?.get(position) ?: dummyData[position]
    }
}
