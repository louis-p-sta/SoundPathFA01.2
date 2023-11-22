package com.example.soundpathempty
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.soundpathempty.Entities.Route_Data
import com.example.soundpathempty.Relations.RouteWithMarkers
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerDao {
    @Upsert
    suspend fun upsertMarker(marker:Marker_Data)
    @Delete
    suspend fun deleteMarker(marker: Marker_Data)
    @Query("SELECT * FROM markers ORDER BY name ASC")
    fun getAll(): Flow<List<Marker_Data>>
    @Query("SELECT * FROM markers ORDER BY name ASC")
    fun getMarkers(): List<Marker_Data>
    @Upsert
    suspend fun upsertRoute(route: Route_Data)
    @Query("SELECT * FROM routes ORDER BY routeName ASC")
    fun getAllRoutes():Flow<List<RouteWithMarkers>>
    @Query("SELECT * FROM routes ORDER BY routeName ASC")
    fun getRoutes():List<RouteWithMarkers>
    @Transaction
    @Query("SELECT * FROM routes WHERE routeName = :routeName ")
    suspend fun getRouteWithMarkers(routeName: String): List<RouteWithMarkers>

    //Fonction pour updater les marqueurs
   // @Update(entity= Marker_Data::class)
   // fun update(obj: MarkerUpdate)

}