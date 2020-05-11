package es.jfechevarria.spaintv.usecases.main.ui.allChannels.pagination

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import es.jfechevarria.data.Repository
import es.jfechevarria.domain.Channel

class ChannelDataSourceFactory(
    private val repository: Repository,
    var search: String?
): DataSource.Factory<Int, Channel>() {

    override fun create(): DataSource<Int, Channel> {
        return ChannelDataSourceClass(repository, search)
    }

}