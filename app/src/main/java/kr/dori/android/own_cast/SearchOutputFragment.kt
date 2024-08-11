package kr.dori.android.own_cast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.databinding.FragmentSearchOutputBinding

class SearchOutputFragment : Fragment(), SearchMover {

    private lateinit var binding: FragmentSearchOutputBinding
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val searchAdapter = SearchAdapter(this)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val dummyData = mutableListOf(
        SongData("Cast1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("Cast2", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("Cast3", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("Cast4", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("Cast5", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug"),
        SongData("Cast1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("Cast2", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("Cast3", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("Cast4", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("Cast5", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchOutputBinding.inflate(inflater, container, false)

        searchAdapter.dataList = dummyData

        binding.fragmentSearchOutputRv.adapter = searchAdapter
        binding.fragmentSearchOutputRv.layoutManager = GridLayoutManager(context,2)

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ifsuccess", "success")
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
            }
        }

        //직접 이동말고 스택을 연속으로 두개 지우는게 더 깔끔하니까 수정하자.
        binding.outputBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm,SearchFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.outputSearchIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.outputExitIv.setOnClickListener {
            searchViewModel.addtext("")
            requireActivity().supportFragmentManager.popBackStack()
        }

        // ViewModel 관찰하여 텍스트 뷰 업데이트
        searchViewModel.text.observe(viewLifecycleOwner, Observer { newText ->
            binding.fragmentSearchOutputTitleTv.text = newText
        })

        return binding.root
    }

    override fun goPlayCast() {
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    override fun goAddCast() {
        val intent = Intent(requireContext(), SearchAddCategoryActivity::class.java)
        startActivity(intent)
    }

    override fun backSearch() {
        // 구현 필요 없음
    }
}
