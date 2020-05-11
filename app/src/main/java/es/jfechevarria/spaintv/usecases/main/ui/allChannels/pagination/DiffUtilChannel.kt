package es.jfechevarria.spaintv.usecases.main.ui.allChannels.pagination

import androidx.recyclerview.widget.DiffUtil
import es.jfechevarria.domain.Channel

class DiffUtilChannel : DiffUtil.ItemCallback<Channel>() {
    override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
        return oldItem.name == newItem.name
                && oldItem.id == newItem.id
                && oldItem.url == newItem.url
                && oldItem.checked == newItem.checked
    }

}