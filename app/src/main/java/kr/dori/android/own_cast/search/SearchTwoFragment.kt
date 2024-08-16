package kr.dori.android.own_cast.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.FragmentSearchTwoBinding
import kr.dori.android.own_cast.player.PlayCastActivity

class SearchTwoFragment:Fragment(), SearchMover {
    private lateinit var binding: FragmentSearchTwoBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val searchTwoAdapter = SearchTwoAdapter(this)
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchTwoBinding.inflate(inflater, container, false)


        searchTwoAdapter.dataList = dummyData

        binding.fragmentTwoRv.adapter = searchTwoAdapter
        binding.fragmentTwoRv.layoutManager = LinearLayoutManager(context)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("ifsuccess", "success")
                    val data: Intent? = result.data
                    val isSuccess = data?.getBooleanExtra("result", false) ?: false
                    (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
                }
            }
        binding.searchFrTwoBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


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
        TODO("Not yet implemented")
    }
}
