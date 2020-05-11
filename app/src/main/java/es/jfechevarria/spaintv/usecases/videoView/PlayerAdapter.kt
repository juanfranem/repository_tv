package es.jfechevarria.spaintv.usecases.videoView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.jfechevarria.domain.Channel
import es.jfechevarria.spaintv.databinding.ItemChannelPlayBinding

data class PlayerAdapter(
    var list: List<Channel>,
    val playerViewModel: PlayerViewModel
): RecyclerView.Adapter<PlayerAdapter.Holder>() {

    inner class Holder(val view: ItemChannelPlayBinding): RecyclerView.ViewHolder(view.root) {

        fun bind(any: Channel?) {
            view.title.text = any?.name
            view.favourite.visibility = if (playerViewModel.current == any) {
                View.VISIBLE
            } else {
                View.GONE
            }
            view.root.setOnClickListener {
                notifyDataSetChanged()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemChannelPlayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

}