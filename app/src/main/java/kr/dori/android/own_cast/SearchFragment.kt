package kr.dori.android.own_cast

import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.databinding.FragmentPlaylistBinding
import kr.dori.android.own_cast.databinding.FragmentSearchBinding

class SearchFragment : Fragment(),SearchMover {

    lateinit var binding: FragmentSearchBinding
    private val searchAdapter = SearchAdapter(this)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var gridLayoutManager: GridLayoutManager

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
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        searchAdapter.dataList = dummyData


        /*binding.recyclerView.adapter = searchAdapter
        gridLayoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL, false)

        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.setHasFixedSize(true)*/

        for (i in 0 until 4) {
            // item_layout.xml을 inflate하여 GridLayout에 추가
            val itemView = inflater.inflate(R.layout.item_search_fr, binding.gridLayout, false)
            // 필요시 itemView의 내부 요소를 수정
            /*var params:GridLayout.LayoutParams
            if(i==1){
                params = GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(0))
                *//*params.width = GridLayout.LayoutParams.WRAP_CONTENT/4.toInt()
                params.height = GridLayout.LayoutParams.WRAP_CONTENT/4.toInt()*//*
            }else if(i==2){
                params = GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(1))
                *//*params.width = GridLayout.LayoutParams.WRAP_CONTENT/4*3.toInt()
                params.height = GridLayout.LayoutParams.WRAP_CONTENT/4.toInt()*//*
            }else if(i==2){
                params = GridLayout.LayoutParams(GridLayout.spec(1), GridLayout.spec(0))
                *//*params.width = GridLayout.LayoutParams.WRAP_CONTENT/4.toInt()
                params.height = GridLayout.LayoutParams.WRAP_CONTENT/4*3.toInt()*//*
            }else{
                params = GridLayout.LayoutParams(GridLayout.spec(1), GridLayout.spec(1))
                *//*params.width = GridLayout.LayoutParams.WRAP_CONTENT/4*3.toInt()
                params.height = GridLayout.LayoutParams.WRAP_CONTENT/4*3.toInt()*//*
            }
            params.setGravity(Gravity.CENTER)
            itemView.layoutParams = params*/
            binding.gridLayout.addView(itemView)
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ifsuccess", "success")
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
            }
        }


        binding.fragmentSearchArrowIv.setOnClickListener {
            val input = SearchTwoFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, input)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
        binding.fragmentSearchPlaintextIv.setOnClickListener {
            val input = SearchInputFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, input)
                .addToBackStack(null)
                .commitAllowingStateLoss()
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
