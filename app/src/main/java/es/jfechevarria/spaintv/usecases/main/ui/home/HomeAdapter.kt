package es.jfechevarria.spaintv.usecases.main.ui.home

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.jfechevarria.data.Repository
import es.jfechevarria.domain.Channel
import es.jfechevarria.spaintv.Actions
import es.jfechevarria.spaintv.databinding.ItemChannelBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeAdapter(
    var list: List<Channel>,
    val repository: Repository,
    val actions: Actions,
    val updateSize: (count: Int) -> Unit
): RecyclerView.Adapter<HomeAdapter.Holder>() {

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
                if (!isChecked) {
                    view.favourite.visibility = View.GONE
                    view.delete.visibility = View.VISIBLE
                    Handler().postDelayed(Runnable {
                        view.favourite.isChecked = true
                        view.favourite.visibility = View.VISIBLE
                        view.delete.visibility = View.GONE
                    }, 4000)
                }
            }
            view.delete.setOnClickListener {
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        repository.roomDB?.dataDao()?.delete(any!!)
                    }
                }
                val newList = list.toMutableList()
                newList.remove(any)
                submitList(newList)
            }
            view.root.setOnClickListener { actions.onClick(any!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[holder.adapterPosition])
    }

    fun submitList(newList: List<Channel>?) {
        newList?.let {
            list = it
            notifyDataSetChanged()
        }
        updateSize(list.size)
    }

}