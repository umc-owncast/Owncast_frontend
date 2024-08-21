package kr.dori.android.own_cast.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.ActivitySearchAddCategoryBinding
import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.GetAllPlaylist
import kr.dori.android.own_cast.playlist.AddCategoryAdapter

class SearchAddCategoryActivity : AppCompatActivity(), SearchMover {


    private lateinit var binding: ActivitySearchAddCategoryBinding
    private var searchadapter = AddCategoryAdapter(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val playlist = intent.getSerializableExtra("categoryList") as? ArrayList<GetAllPlaylist>
        val id = intent.getLongExtra("id",-1)
        playlist?.let{
            searchadapter.dataList = it
        }
        id?.let {
            searchadapter.id = it
        }
        if(playlist == null){
            Toast.makeText(this,"잠시후 다시 시도해주세요",Toast.LENGTH_SHORT)
        }
        binding.activitySearchAddCategoryRv.adapter = searchadapter
        binding.activitySearchAddCategoryRv.layoutManager = LinearLayoutManager(this)
        binding.activitySearchAddCategoryExitIv.setOnClickListener {
            finish()
        }
    }

    override fun goPlayCast(list: List<CastHomeDTO>, id:Long) {
        TODO("Not yet implemented")
    }

    override fun goAddCast(id:Long) {
        TODO("Not yet implemented")
    }

    override fun backSearch() {
        showCustomToast("(카테고리 이름)에 담아드렸어요")
        finish()
    }

    fun showCustomToast(message: String) {
        // Inflate the custom layout
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container))

        // Set custom message
        val textView: TextView = layout.findViewById(R.id.toast_message_tv)
        textView.text = message

        // Create and show the Toast
        with (Toast(applicationContext)) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }
}
