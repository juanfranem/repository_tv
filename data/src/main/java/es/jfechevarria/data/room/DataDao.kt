package es.jfechevarria.data.room

import androidx.room.*
import es.jfechevarria.domain.Channel

@Dao
interface DataDao {

    @Query("select count(*) from channel where id = :id limit 1")
    suspend fun exists(id: Long): Int

    @Query("Select * from channel order by id desc")
    suspend fun all(): List<Channel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: Channel): Long

    @Delete
    suspend fun delete(channel: Channel)
}