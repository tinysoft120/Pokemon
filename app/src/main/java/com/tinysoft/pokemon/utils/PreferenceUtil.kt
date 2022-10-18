package com.tinysoft.pokemon.utils

import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.tinysoft.pokemon.App
import com.tinysoft.pokemon.LIST_GRID_SIZE
import com.tinysoft.pokemon.LIST_SORT_ORDER
import com.tinysoft.pokemon.R
import com.tinysoft.pokemon.helper.SortOrder

object PreferenceUtil {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext())

    var listSortOrder: SortOrder
        get() {
            val id: Int = sharedPreferences.getInt(LIST_SORT_ORDER, 0)
            for (order in SortOrder.values()) {
                if (order.id == id) {
                    return order
                }
            }
            return SortOrder.BY_NUMBER
        }
        set(value) = sharedPreferences.edit { putInt(LIST_SORT_ORDER, value.id) }

    var listGridSize: Int
        get() = sharedPreferences.getInt(
            LIST_GRID_SIZE,
            App.getContext().resources.getInteger(R.integer.default_grid_columns)
        )
        set(value) = sharedPreferences.edit {
            putInt(LIST_GRID_SIZE, value)
        }
}