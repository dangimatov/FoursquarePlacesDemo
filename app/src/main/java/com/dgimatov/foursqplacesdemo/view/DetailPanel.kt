package com.dgimatov.foursqplacesdemo.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import com.dgimatov.foursqplacesdemo.R
import kotlinx.android.synthetic.main.detail_panel.*

/**
 * Panel which shows details of a restaurant
 */
class DetailPanel(context: Context) : AppCompatDialog(context), DetailView {

    init {
        setContentView(R.layout.detail_panel)
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val attributes = window?.attributes
        attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        attributes?.gravity = Gravity.BOTTOM

        window?.apply {
            this.attributes = attributes
            setBackgroundDrawable(context.resources.getDrawable(android.R.color.transparent))
            setWindowAnimations(R.style.DetailPage)
        }
    }

    override fun updateState(state: DetailViewContentState) {
        Log.i("test_", "DetailPanel updateState: ${state.javaClass.simpleName}")
        when (state) {
            DetailViewContentState.Loading -> {
                detailsContainer.visibility = View.GONE
                detailsProgress.visibility = View.VISIBLE
            }

            is DetailViewContentState.ShowDetails -> {
                detailPanelName.text = state.venueDetails.name
                detailPanelDescription.text = state.venueDetails.description
                detailsContainer.visibility = View.VISIBLE
                detailsProgress.visibility = View.GONE
            }

            is DetailViewContentState.Error -> {
                showErrorDialog(state.exception)
                dismiss()
            }
        }
    }

    private fun showErrorDialog(e: Throwable) {
        AlertDialog.Builder(context)
                .setTitle("Error")
                .setCancelable(true)
                .setMessage("Something went wrong: ${e.message}")
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }
}