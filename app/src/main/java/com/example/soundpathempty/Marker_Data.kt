package com.example.soundpathempty
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//import androidx.room.vo.PrimaryKey
@Entity(tableName = "markers")
data class Marker_Data(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val description: String,
    val longitude: Double,
    val latitude: Double
)
//Entité pour updater les marqueurs
/*@Entity
class MarkerUpdate{
    @ColumnInfo(name= "id")
    public long id;
    @ColumnInfo(name= "")
}
*/