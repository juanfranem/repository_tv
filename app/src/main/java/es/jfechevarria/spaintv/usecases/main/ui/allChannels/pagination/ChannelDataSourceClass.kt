package es.jfechevarria.spaintv.usecases.main.ui.allChannels.pagination

import androidx.paging.PageKeyedDataSource
import es.jfechevarria.data.Repository
import es.jfechevarria.domain.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ChannelDataSourceClass(
    private val repository: Repository,
    var query: String? = null
): PageKeyedDataSource<Int, Channel>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Channel>
    ) {
        GlobalScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    if (query.isNullOrEmpty()) {
                        repository.services.all()
                    } else {
                        repository.services.all(search = query)
                    }
                }
                callback.onResult(response?.data.orEmpty(), if (response?.meta?.current_page ?: 1 == 1)
                    null
                else
                    response?.meta?.current_page, response?.meta?.current_page ?: 1 + 1)
            } catch (e: Exception) {}

        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Channel>) {
        GlobalScope.launch {
            try {
            val response =  withContext(Dispatchers.IO) {
                if (query.isNullOrEmpty()){
                    repository.services.all(params.key)
                } else {
                    repository.services.all(params.key, search = query)
                }
            }

            callback.onResult(response?.data.orEmpty(), params.key + 1)
            } catch (e: Exception) {}

        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Channel>) {
        GlobalScope.launch {
            try {
            val response =  withContext(Dispatchers.IO) {
                if (query.isNullOrEmpty()){
                    repository.services.all(params.key)
                } else {
                    repository.services.all(params.key, search = query)
                }
            }

            callback.onResult(response?.data.orEmpty(), params.key - 1)
            } catch (e: Exception) {}
        }
    }

}