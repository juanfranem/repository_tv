package es.jfechevarria.data.cloud

import android.content.Context

class ConnectionManager(var context: Context) {

    private val services = RetrofitClient.getClient(
        context
    )
        .create(Services::class.java)

    fun getServices(): Services {
        return services
    }
}