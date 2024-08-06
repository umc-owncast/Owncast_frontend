package kr.dori.android.own_cast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.dori.android.own_cast.databinding.ActivityPlayCastBinding

class PlayCastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayCastBinding
    private val playCastViewModel: PlayCastViewModel by viewModels { PlayCastViewModelFactory(application) }
    private lateinit var speedTableViewModel: SpeedTableViewModel

    var matchData : Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speedTableViewModel = ViewModelProvider(this).get(SpeedTableViewModel::class.java)

        val clickImageView = listOf(
            binding.activityCastSpeed05Iv,
            binding.activityCastSpeed075Iv,
            binding.activityCastSpeed10Iv,
            binding.activityCastSpeed125Iv,
            binding.activityCastSpeed15Iv,
            binding.activityCastSpeed175Iv,
            binding.activityCastSpeed20Iv
        )

        val targetView = listOf(
            binding.activityCastSpeedOn05Iv,
            binding.activityCastSpeedOn075Iv,
            binding.activityCastSpeedOn10Iv,
            binding.activityCastSpeedOn125Iv,
            binding.activityCastSpeedOn15Iv,
            binding.activityCastSpeedOn175Iv,
            binding.activityCastSpeedOn20Iv,
            binding.activityCastSpeedOn05Tv,
            binding.activityCastSpeedOn075Tv,
            binding.activityCastSpeedOn10Tv,
            binding.activityCastSpeedOn125Tv,
            binding.activityCastSpeedOn15Tv,
            binding.activityCastSpeedOn175Tv,
            binding.activityCastSpeedOn20Tv
        )


        // Fragment 초기화
        supportFragmentManager.beginTransaction()
            .add(R.id.play_cast_frm, CastAudioFragment())
            .commit()

        // speedTable 클릭 리스너 -> 클릭한 iv의 id값을 float로 변환 후 viewModel data 초기화 -> UI 업데이트
        clickImageView.forEach { listener ->
            listener.setOnClickListener {
                val imageId = resources.getResourceEntryName(listener.id)
                val regex = "_(\\d+\\.\\d+|\\d+_\\d+)_".toRegex()
                val matchResult = regex.find(imageId)

                if (matchResult != null) {
                    val speed = matchResult.groupValues[1].replace("_", ".")
                    val realspeed = speed.toFloat()
                    speedTableViewModel.setData(realspeed)
                    Log.d("speed", "Image ID: $imageId, Speed: $speed")
                } else {
                    Log.e("speed", "No match found for $imageId")
                }
            }
        }

        // LiveData 관찰
        speedTableViewModel.data.observe(this, Observer { value ->
            binding.realSpeedTv.text = formatSpeed(value.toString())
            Log.d("speed", "$value")
            targetView.forEach { listener ->
                val viewId = resources.getResourceEntryName(listener.id)
                val regex = "_(\\d+\\.\\d+|\\d+_\\d+)_".toRegex()
                val matchResult = regex.find(viewId)

                if (matchResult != null) {
                    val speed = matchResult.groupValues[1].replace("_", ".")
                    matchData = speed.toFloat()

                    if (matchData == value) {
                        listener.visibility = View.VISIBLE
                    } else {
                        listener.visibility = View.GONE
                    }
                }
            }
        })

        // 데이터 초기 설정
        if (speedTableViewModel.data.value == null) {
            speedTableViewModel.setData(1.0f)
        }

        // speed_table visible 설정
        binding.speedTableOffIv.setOnClickListener {
            binding.speedTable.visibility = View.VISIBLE
            binding.speedTable.bringToFront()
            binding.speedTable.invalidate()
            binding.speedTable.requestLayout()
            binding.activityPlayCastSpeedOn.visibility = View.VISIBLE
        }

        binding.speedTableExitIv.setOnClickListener {
            binding.speedTable.visibility = View.GONE
            binding.activityPlayCastSpeedOn.visibility = View.GONE
        }

        // fragment 이동 부분
        binding.activityPlayCastScriptOffIv.setOnClickListener {
            audioToScript()
        }

        binding.activityPlayCastPlaylistOffIv.setOnClickListener {
            audioToPlaylist()
        }

        binding.activityPlayCastPlaylistOnIv.setOnClickListener {
            playlistToAudio()
        }

        binding.activityPlayCastScriptOnIv.setOnClickListener {
            scriptToAudio()
        }

//        binding.playcastActivitySaveBackIv.setOnClickListener {
//            playCastViewModel.showPlay()
//            finish()
//        }
        binding.playcastActivitySaveBackIv.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("result", true)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

//        binding.activityPlayCastAudioExitIv.setOnClickListener {
//            playCastViewModel.closePlay()
//            finish()
//        }

        binding.activityPlayCastAudioExitIv.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("result", false)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.activityPlayCastNotAudioExit.setOnClickListener {
            scriptToAudio()
        }
    }

    fun audioToScript() {
        binding.activityPlayCastPlaylistOnIv.visibility = View.GONE
        binding.activityPlayCastPlaylistOffIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOnIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOffIv.visibility = View.GONE
        binding.activityPlayCastAudioExitIv.visibility = View.GONE
        binding.activityPlayCastNotAudioExit.visibility = View.VISIBLE
        binding.playcastActivitySaveBackIv.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, CastScriptFragment())
            .commitAllowingStateLoss()
    }

    fun scriptToAudio() {
        binding.activityPlayCastPlaylistOnIv.visibility = View.GONE
        binding.activityPlayCastPlaylistOffIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOnIv.visibility = View.GONE
        binding.activityPlayCastScriptOffIv.visibility = View.VISIBLE
        binding.activityPlayCastAudioExitIv.visibility = View.VISIBLE
        binding.activityPlayCastNotAudioExit.visibility = View.GONE
        binding.playcastActivitySaveBackIv.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, CastAudioFragment())
            .commitAllowingStateLoss()
    }

    fun audioToPlaylist() {
        binding.activityPlayCastPlaylistOnIv.visibility = View.VISIBLE
        binding.activityPlayCastPlaylistOffIv.visibility = View.GONE
        binding.activityPlayCastScriptOnIv.visibility = View.GONE
        binding.activityPlayCastScriptOffIv.visibility = View.VISIBLE
        binding.activityPlayCastAudioExitIv.visibility = View.GONE
        binding.activityPlayCastNotAudioExit.visibility = View.VISIBLE
        binding.playcastActivitySaveBackIv.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, CastPlaylistFragment())
            .commitAllowingStateLoss()
    }

    fun playlistToAudio() {
        binding.activityPlayCastPlaylistOnIv.visibility = View.GONE
        binding.activityPlayCastPlaylistOffIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOnIv.visibility = View.GONE
        binding.activityPlayCastScriptOffIv.visibility = View.VISIBLE
        binding.activityPlayCastAudioExitIv.visibility = View.VISIBLE
        binding.activityPlayCastNotAudioExit.visibility = View.GONE
        binding.playcastActivitySaveBackIv.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, CastAudioFragment())
            .commitAllowingStateLoss()
    }

    fun formatSpeed(speed: String): String {
        return if (speed.endsWith(".0")) {
            speed.replace(".0", "") + "x"
        } else {
            speed + "x"
        }
    }
}
