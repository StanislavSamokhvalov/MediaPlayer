package ru.netology.mediaplayer

import android.media.AudioAttributes
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import ru.netology.mediaplayer.databinding.ActivityMainBinding
import ru.netology.mediaplayer.viewmodel.MediaViewModel
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import kotlinx.android.synthetic.main.activity_main.*
import ru.netology.mediaplayer.adapter.MediaAdapter
import ru.netology.mediaplayer.adapter.TrackCallback
import ru.netology.mediaplayer.dto.Track

class MainActivity : AppCompatActivity() {

    private val viewModel: MediaViewModel by viewModels()
    private val mediaObserver = MediaLifecycleObserver()
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val adapter = MediaAdapter(object : TrackCallback {
            override fun onPlay(track: Track) {
                play(track)
            }
        })

        setContentView(binding.root)
        binding.recycler.adapter = adapter

        viewModel.data.observe(this) { album ->
            adapter.submitList(album.tracks)

            with(binding) {
                nameAlbum.text = album.title
                artistName.text = album.artist
                years.text = album.published
                genre.text = album.genre

                playStart.setImageResource(
                    if (viewModel.isPlaying()) {
                        R.drawable.ic_play_24dp
                    } else {
                        R.drawable.ic_pause_24dp
                    }
                )

                playNext.setOnClickListener { playNextTrack() }
                playPrev.setOnClickListener { playPrevTrack() }

                seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) mediaObserver.player?.seekTo(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            }

        }
        lifecycle.addObserver(mediaObserver)
    }

    fun play(track: Track) {
        mediaObserver.player?.setOnCompletionListener {
            playNextTrack()
        }

        if (track.id != viewModel.playId.value) {
            mediaObserver.onStateChanged(this@MainActivity, Lifecycle.Event.ON_STOP)
        }
        if (mediaObserver.player?.isPlaying == true) {
            mediaObserver.onStateChanged(this@MainActivity, Lifecycle.Event.ON_PAUSE)
        } else {
            if (track.id != viewModel.playId.value) {
                mediaObserver.apply {
                    player?.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    player?.setDataSource("${BuildConfig.BASE_URL}${track.file}")
                }.play()

                initialiseSeekBar()
            } else {
                mediaObserver.player?.start()
            }
        }
        viewModel.play(track.id)
    }

    private fun playNextTrack() {
        viewModel.data.value?.let {
            track = if (viewModel.playId.value == it.tracks.size) {
                it.tracks[0]
            } else {
                it.tracks[viewModel.playId.value!!.toInt()]
            }
        }
        play(track)
    }

    private fun playPrevTrack() {
        viewModel.data.value?.let {
            track = if (viewModel.playId.value == 1) {
                it.tracks[it.tracks.size - 1]
            } else {
                it.tracks[(viewModel.playId.value!! - 2)]
            }
        }
        play(track)
    }

    private fun initialiseSeekBar() {
        seekbar.max = mediaObserver.player!!.duration

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    seekbar.progress = mediaObserver.player?.currentPosition!!
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    seekbar.progress = 0
                }
            }
        }, 0)

    }


    override fun onStop() {
        if (mediaObserver.player?.isPlaying == true) {
            mediaObserver.onStateChanged(this@MainActivity, Lifecycle.Event.ON_PAUSE)
        }
        super.onStop()
    }
}