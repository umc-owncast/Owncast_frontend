package kr.dori.android.own_cast.keyword

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.browse.MediaBrowser.MediaItem
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeyvpAuioscriptBinding


class KeyvpAudioScriptFragment:Fragment() {
    lateinit var binding: FragmentKeyvpAuioscriptBinding
    private var listener: KeywordBtnClickListener? = null
    private var speedList = ArrayList<TextView>()
    private lateinit var sharedViewModel: KeywordViewModel
    private var curSpeed:Int = 2
    private lateinit var adapter:KeyvpAudioScriptRVAdapter


    /*-------exoPlayer용 변수--------------------*/
    lateinit var player: ExoPlayer
    private lateinit var steamingUrl : String
    private val handler = Handler(Looper.getMainLooper())
    private var isSeeking = false
    private var mills: Float =0f

    /*---------------------------*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? KeywordBtnClickListener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyvpAuioscriptBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(KeywordViewModel::class.java)


        binding.keyAudScrNextIv.setOnClickListener {
            listener?.onButtonClick()
        }
        binding.keyAudScrRemakeIv.setOnClickListener{
            if(sharedViewModel.postCastScript.value!= null){
                listener?.createCastByScript(sharedViewModel.postCastScript.value!!)
            }else{
                listener?.createCastByKeyword(sharedViewModel.postCastKeyword.value!!)
            }
        }


        player = ExoPlayer.Builder(requireContext()).build()
        //이 작업을 전체 관리하는 listener에서 해주고 있음.
        //화면을 나갔을때의 pause와 resume과 재생성했을때의 pause를 재 관리 해줘야할거같은데..
        initPlayer()
        initRecyclerView()//sentnences를 받아와 출력
        //스크립트 재생성했을때 이 두개를 다시 실행시켜야함

        initSpeedUi()
        return binding.root




    }

    //다음 페이지 넘겨주는 listener 해제
    override fun onDetach() {
        super.onDetach()
        listener = null
        player?.let {
            it.release()
        } // 플레이어 리소스 해제
        stopSeekBarUpdate() // SeekBar 업데이트 중지
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.let {
            it.release()
        } // 플레이어 리소스 해제// 플레이어 리소스 해제
        stopSeekBarUpdate() // SeekBar 업데이트 중지
    }

    override fun onPause() {
        super.onPause()
        player.pause()
        stopSeekBarUpdate()
    }

    override fun onResume() {
        super.onResume()
        player.play()
        if(player.playWhenReady)startSeekBarUpdate()
    }



    fun initPlayer(){//새로 만들어올떄만 필요
        binding.keyAudScriptSb.progress=0

        steamingUrl = sharedViewModel.streamingUrl
        //deprecated 됐다길래;
        val mediaItem = androidx.media3.common.MediaItem.fromUri(steamingUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.addListener(object : Player.Listener {
            override fun onIsLoadingChanged(isLoading: Boolean) {

            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {//재생상태가 준비되었음
                    //binding.keyAudScriptSb.max = (player.duration / 1000).toInt() // SeekBar 최대값 설정
                    binding.keyAudSetMediaTimeTv.text = formatTime(player.duration)
                    //사용자가 화면을 나갔을때 다시 progressbar 코루틴을 다시 실행시키기 위해 만듦
                    startSeekBarUpdate()
                    player.play()
                }
            }


            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {//재생상태가 변경되었음.
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)

            }
            //Player.DISCONTINUITY_REASON_AUTO_TRANSITION 미디어간 자동 변환
        })
        binding.keyAudScriptSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isSeeking) {
                    // 사용자가 SeekBar를 조작하면 플레이어의 재생 위치를 변경
                    player.seekTo(progress*player.duration/100000L)
                    binding.keyAudSetMediaTimeTv.text = formatTime(player.currentPosition) // 현재 재생 위치를 업데이트

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true // 사용자가 SeekBar를 조작 중
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false // 사용자가 SeekBar 조작을 마침
                val progress = seekBar?.progress?.toLong() ?: 0L
                player.seekTo(progress*player.duration/100000L)
                binding.keyAudSetMediaTimeTv.text = formatTime(player.currentPosition) // 현재 재생 위치 업데이트

            }
        })


        binding.keyAudScrAddSecondIv.setOnClickListener{
            val currentPosition = player.currentPosition
            val newPosition = currentPosition + 10_000L // 10초 앞으로
            player.seekTo(newPosition)
        }
        binding.keyAudScrMinSecondIv.setOnClickListener {
            val currentPosition = player.currentPosition
            val newPosition = currentPosition - 10_000L // 10초 앞으로
            player.seekTo(newPosition)
        }
        binding.keyAudScriptPlaybtnIv.setOnClickListener{
            player.play()
            binding.keyAudScriptPlaybtnIv.visibility = View.GONE
            binding.keyAudScriptStopBtnIv.visibility = View.VISIBLE


        }
        binding.keyAudScriptStopBtnIv.setOnClickListener{
            player.pause()
            binding.keyAudScriptPlaybtnIv.visibility = View.VISIBLE
            binding.keyAudScriptStopBtnIv.visibility = View.GONE
        }
    }

    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (player.isPlaying) {
                val currentPosition = player.currentPosition
                binding.keyAudScriptSb.progress = ((currentPosition*100000 /player.duration )).toInt() // SeekBar 현재 위치 업데이트
                binding.keyAudSetProgTimeTv.text = formatTime(currentPosition) // 현재 재생 시간 업데이트
                updateLyricsHighlight()

            }
            handler.postDelayed(this, 100) // 1초마다 업데이트
        }
    }

    // SeekBar 업데이트 시작
    private fun startSeekBarUpdate() {
        handler.postDelayed(updateSeekBar, 100)
    }

    // SeekBar 업데이트 중지
    private fun stopSeekBarUpdate() {
        handler.removeCallbacks(updateSeekBar)
    }
    private fun formatTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    /*---------------------------------------------------------------------------------------------*/
    //음성 속도 지정하는 tool에 대한 listener 지정
    fun initSpeedUi(){
        binding.keyAudScrCurSpeedTv.setOnClickListener {
            binding.keyAudScrSpeedToolCl.visibility = View.VISIBLE
            binding.keyAudScrCurSpeedTv.setTextColor(Color.parseColor("#8050F2"))//메인컬러
        }
        //x버튼 누르면 꺼지는거
        binding.keywordSpeedBackIv.setOnClickListener {
            binding.keyAudScrSpeedToolCl.visibility = View.GONE
            binding.keyAudScrCurSpeedTv.setTextColor(Color.parseColor("#00051F"))

        }

        speedList.add(binding.keywordSpeed05)
        speedList.add(binding.keywordSpeed075)
        speedList.add(binding.keywordSpeed10)
        speedList.add(binding.keywordSpeed125)
        speedList.add(binding.keywordSpeed15)
        speedList.add(binding.keywordSpeed20)
        for(i:Int in 0..5){
            val speed: Float = if (i < 5) {
                0.5f + i * 0.25f
            } else {
                2.0f // 2.0만 있으므로
            }
            speedList[i].setOnClickListener {
                binding.keyAudScrCurSpeedTv.text = "${speed}X"


                player.setPlaybackSpeed(speed)//미디어 플레이어 재생속도 설정
                player.playWhenReady = true//자동 재생해줌


                //#8050F2는 MainColro
                //이전거 버튼 비활성화
                speedList[curSpeed].setTextColor(Color.parseColor("#00051F"))
                speedList[curSpeed].backgroundTintList = ColorStateList.
                valueOf(ContextCompat.getColor(this.requireContext(), R.color.button_unclick))
                //현재 버튼 활성화
                speedList[i].backgroundTintList = ColorStateList.
                valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))
                speedList[i].setTextColor(Color.parseColor("#FFFFFF"))
                //다시 현재 버튼 위치 기억
                curSpeed = i
                binding.keyAudScrSpeedToolCl.visibility = View.GONE


                binding.keyAudScrCurSpeedTv.setTextColor(Color.parseColor("#00051F"))
                //버튼 ui도 색깔 바꾸기

            }
        }

        binding.keyAudScrSpeedToolCl.visibility = View.GONE

    }

    fun initRecyclerView(){

        sharedViewModel.sentences.value?.let{
            adapter = KeyvpAudioScriptRVAdapter(it)

            binding.keyAudScriptRv.adapter = adapter
        }

    }

    fun updateLyricsHighlight(){

    }
}