package kr.dori.android.own_cast.playlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.ActivityMover
import kr.dori.android.own_cast.FragmentMover
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.FragmentPlaylistBinding
import kr.dori.android.own_cast.editAudio.EditAudio
import kr.dori.android.own_cast.player.PlayCastActivity

class PlaylistFragment : Fragment(), AddCategoryListener, EditCategoryListener, ActivityMover,
    FragmentMover,
    EditAudio {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var categoryAdapter: PlaylistCategoryAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    val dummyData = mutableListOf(
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

        categoryAdapter = PlaylistCategoryAdapter(this, this, this)
        binding.category.adapter = categoryAdapter
        binding.category.layoutManager = LinearLayoutManager(context)

        sharedViewModel.data.observe(viewLifecycleOwner, Observer { newData ->
            categoryAdapter.dataList = newData
            categoryAdapter.notifyDataSetChanged()
        })

        if (sharedViewModel.data.value.isNullOrEmpty()) {
            sharedViewModel.setData(dummyData)
        }

        binding.fragmentPlaylistAddIv.setOnClickListener {
            val dialog = AddCategoryDialog(requireContext(), this,this)
            dialog.show()
        }

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

        // Initialize ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ifsuccess", "success")
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
            }
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

    override fun ToPlayCast() {
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    override fun playlistToCategory() {
        val categoryFragment = CategoryFragment()
        val bundle = Bundle().apply {
            putParcelableArrayList("ape", ArrayList(sharedViewModel.data.value ?: listOf()))
        }
        categoryFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, categoryFragment)
            .addToBackStack(null)
            .commit()
    }

    fun showCustomToast(message: String) {
        // Inflate the custom layout
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast, binding.root.findViewById(R.id.custom_toast_container))

        // Set custom message
        val textView: TextView = layout.findViewById(R.id.toast_message_tv)
        textView.text = message

        // Create and show the Toast
        with (Toast(requireContext())) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    override fun dialogToEditAudio() {
        showCustomToast("카테고리가 추가되었어요")

    }

    override fun ToEditAudio() {
        TODO("Not yet implemented")

    }
}
