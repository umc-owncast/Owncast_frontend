package kr.dori.android.own_cast.playlist

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
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.ActivityMover
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.FragmentCategoryBinding
import kr.dori.android.own_cast.editAudio.EditAudioActivity
import kr.dori.android.own_cast.player.PlayCastActivity

class CategoryFragment() : Fragment(), ActivityMover {

    private lateinit var binding: FragmentCategoryBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var castAdapter: CastAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentCategoryBinding.inflate(inflater,container,false)

        binding.fragmentCategoryBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        castAdapter = CastAdapter(this)

        sharedViewModel.data.observe(viewLifecycleOwner, Observer{ newData ->
            val Data = arguments?.getParcelableArrayList<SongData>(
                "ape"
            )?: arrayListOf()
            castAdapter.dataList = Data
            castAdapter.notifyDataSetChanged()
        })

        // Initialize ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ifsuccess", "success")
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
            }
        }

        binding.fragmentCategoryRv.adapter = castAdapter
        binding.fragmentCategoryRv.layoutManager = LinearLayoutManager(context)

        binding.fragmentCategoryPlayIv.setOnClickListener {
            ToPlayCast()
        }

        binding.fragmentCategoryShuffleIv.setOnClickListener {
            ToPlayCast()
        }

        return binding.root
    }

    override fun ToPlayCast() {
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    override fun ToEditAudio() {
        val intent = Intent(requireContext(), EditAudioActivity::class.java)
        startActivity(intent)
    }
}