package com.tinysoft.pokemon.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.tinysoft.pokemon.R

class CustomProgressDialog {

    private var dialog: CustomDialog? = null
    val isShowing get() = (dialog!=null && dialog!!.isShowing)

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog_view, null)
        val cpTitle: TextView = view.findViewById(R.id.cp_title)
        val cpCardview: CardView = view.findViewById(R.id.cp_cardview)
        val cpProcessBar: ProgressBar = view.findViewById(R.id.cp_pbar)
        if (title != null) {
            cpTitle.text = title
        }

        // Card Color
        cpCardview.setCardBackgroundColor(Color.parseColor("#60424242"))

        // Progress Bar Color
        setColorFilter(cpProcessBar.indeterminateDrawable, ResourcesCompat.getColor(context.resources, R.color.md_white_1000, null))

        // Text Color
        cpTitle.setTextColor(Color.WHITE)

        dialog = CustomDialog(context)
        dialog!!.setContentView(view)
        dialog!!.show()
        return dialog!!
    }

    fun dismiss() = dialog?.dismiss()

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.progress_background)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }
}