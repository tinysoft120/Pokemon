package com.tinysoft.pokemon.helper

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.tinysoft.pokemon.R

/**
 * Pokemon sort order entries.
 */
enum class SortOrder constructor(
    @param:StringRes @field:StringRes val titleRes: Int,
    @param:IdRes @field:IdRes val resId: Int,
    val id: Int
) {
    BY_NUMBER(R.string.action_order_by_num, R.id.action_sort_order_number, 1),
    BY_NAME(R.string.action_order_by_name, R.id.action_sort_order_name, 2),
    BY_TYPE(R.string.action_order_by_type, R.id.action_sort_order_type, 3),
}