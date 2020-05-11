package es.jfechevarria.spaintv.usecases.main.ui.allChannels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.jfechevarria.data.Repository
import es.jfechevarria.domain.Channel
import es.jfechevarria.spaintv.Actions
import es.jfechevarria.spaintv.databinding.ItemChannelBinding
import es.jfechevarria.spaintv.usecases.main.ui.allChannels.pagination.DiffUtilChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChannelsAdapter(
    val repository: Repository,
    val actions: Actions
): PagedListAdapter<Channel, ChannelsAdapter.Holder>(DiffUtilChannel()) {

    inner class Holder(val view: ItemChannelBinding): RecyclerView.ViewHolder(view.root) {

        fun bind(any: Channel?) {
            view.title.text = any?.name
            view.date.text = any?.checked
            GlobalScope.launch {
                val exist = withContext(Dispatchers.IO) {
                    repository.roomDB?.dataDao()?.exists(any?.id ?: 0) == 1 ?: false
                }
                view.favourite.isChecked = exist
            }
            view.favourite.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    GlobalScope.launch(Dispatchers.IO) {
                        repository.roomDB?.dataDao()?.insert(any!!)
                    }
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        repository.roomDB?.dataDao()?.delete(any!!)
                    }
                }
            }
            view.root.setOnClickListener { actions.onClick(any!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return currentList?.size ?: 0
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }
}