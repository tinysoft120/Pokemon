package com.tinysoft.pokemon.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "pokemon_table")
@Parcelize
data class Pokemon(
    @PrimaryKey
    @ColumnInfo(name = "pokemon_id")
    var id: Int,

    @ColumnInfo(name = "pokemon_name")
    var name: String,

    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    @ColumnInfo(name = "pokemon_image")
    var image: String,

    @ColumnInfo(name = "pokemon_height")
    var height: Double,

    @ColumnInfo(name = "pokemon_weight")
    var weight: Double,

    @ColumnInfo(name = "pokemon_type1")
    var type1: String,

    @ColumnInfo(name = "pokemon_type2")
    var type2: String,

    @ColumnInfo(name = "pokemon_attack")
    var attack: Int,

    @ColumnInfo(name = "pokemon_defense")
    var defense: Int,

    @ColumnInfo(name = "pokemon_spAttack")
    var spAttack: Int,

    @ColumnInfo(name = "pokemon_spDefense")
    var spDefense: Int,

    @ColumnInfo(name = "pokemon_speed")
    var speed: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is Pokemon) {
            if (!Objects.equals(this.id, other.id)) return false
            if (!Objects.equals(this.name, other.name)) return false
            // ignore updatedAt when check equal objects
            //if (!Objects.equals(this.updatedAt, other.updatedAt)) return false
            if (!Objects.equals(this.image, other.image)) return false
            if (!Objects.equals(this.height, other.height)) return false
            if (!Objects.equals(this.weight, other.weight)) return false
            if (!Objects.equals(this.type1, other.type1)) return false
            if (!Objects.equals(this.type2, other.type2)) return false
            if (!Objects.equals(this.attack, other.attack)) return false
            if (!Objects.equals(this.defense, other.defense)) return false
            if (!Objects.equals(this.spAttack, other.spAttack)) return false
            if (!Objects.equals(this.spDefense, other.spDefense)) return false
            if (!Objects.equals(this.speed, other.speed)) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        //result = 31 * result + updatedAt.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + weight.hashCode()
        result = 31 * result + type1.hashCode()
        result = 31 * result + type2.hashCode()
        result = 31 * result + attack
        result = 31 * result + defense
        result = 31 * result + spAttack
        result = 31 * result + spDefense
        result = 31 * result + speed
        return result
    }
}
