package kr.dori.android.own_cast.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentCastPlaylistBinding


class CastPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentCastPlaylistBinding
    private lateinit var castPlaylistAdapter: CastPlaylistAdapter
    private var dummy_data = mutableListOf<SongData>(
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug"),
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("캐스트1", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug")

    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCastPlaylistBinding.inflate(inflater,container,false)

        castPlaylistAdapter = CastPlaylistAdapter()
        castPlaylistAdapter.dataList = dummy_data
        binding.fragmentCastPlaylistRv.adapter = castPlaylistAdapter
        binding.fragmentCastPlaylistRv.layoutManager = LinearLayoutManager(context)

        return binding.root
    }
}