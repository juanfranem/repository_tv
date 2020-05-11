package es.jfechevarria.spaintv.usecases.main.ui.allChannels

import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import es.jfechevarria.data.Repository
import es.jfechevarria.domain.Channel
import es.jfechevarria.spaintv.usecases.main.ui.allChannels.pagination.ChannelDataSourceFactory

class AllChannelsViewModel(val app: Application) : AndroidViewModel(app) {

    private val repository = Repository(app)
    var data: LiveData<PagedList<Channel>>? = null
    var queryText = MutableLiveData<String>()

    init {
        data = Transformations.switchMap(queryText) {
            val dataFactory = ChannelDataSourceFactory(repository, it)
            val config = PagedList.Config.Builder()
                .setPageSize(50)
                .setEnablePlaceholders(true)
                .build()
            LivePagedListBuilder(dataFactory, config)
                .setInitialLoadKey(1)
                .build()
        }
    }
}