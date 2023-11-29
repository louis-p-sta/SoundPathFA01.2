package com.example.soundpathempty
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//import androidx.room.vo.PrimaryKey
@Entity(tableName = "markers")
data class Marker_Data(
    @PrimaryKey(autoGenerate = false)
    var name: String,
    var description: String,
    var longitude: Double,
    var latitude: Double,
    var routeName:String,//TODO:Check that mutable types are ok here.
)
//Entité pour updater les marqueurs
/*@Entity
class MarkerUpdate{
    @ColumnInfo(name= "id")
    public long id;
    @ColumnInfo(name= "")
}
*/