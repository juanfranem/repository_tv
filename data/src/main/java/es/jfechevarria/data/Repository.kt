package es.jfechevarria.data

import android.content.Context
import es.jfechevarria.data.cloud.ConnectionManager
import es.jfechevarria.data.cloud.Services
import es.jfechevarria.data.room.RoomDB

class Repository(val context: Context) {

    val roomDB = RoomDB.getInstance(context)
    val services: Services = ConnectionManager(context).getServices()

}