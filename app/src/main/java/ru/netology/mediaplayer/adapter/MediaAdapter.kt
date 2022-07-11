package ru.netology.mediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mediaplayer.R
import ru.netology.mediaplayer.databinding.CardCompositionBinding
import ru.netology.mediaplayer.dto.Track

interface TrackCallback {
    fun onPlay(track: Track)
}

class MediaAdapter(
    private val trackCallback: TrackCallback,
) : ListAdapter<Track, TrackViewHolder>(MediaDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding =
            CardCompositionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, trackCallback)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }
}

class TrackViewHolder(
    private val binding: CardCompositionBinding,
    private val trackCallback: TrackCallback
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        with(binding) {
            trackName.text = track.file
            albumName.text = track.titleAlbum

            playButton.setImageResource(
                if (track.isPlaying) {
                    R.drawable.ic_pause_24dp
                } else {
                    R.drawable.ic_play_24dp
                }
            )

            playButton.setOnClickListener {
                trackCallback.onPlay(track)
            }
        }
    }
}

class MediaDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}
