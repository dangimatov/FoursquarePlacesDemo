package com.dgimatov.foursqplacesdemo.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import com.dgimatov.foursqplacesdemo.R
import kotlinx.android.synthetic.main.detail_panel.*
import retrofit2.HttpException

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
                detailPanelName.setTextOrHide(state.venueDetails.name)
                detailPanelDescription.setTextOrHide(state.venueDetails.description)
                var address = ""
                state.venueDetails.location.formattedAddress?.forEach {
                    address += it
                    address += "\n"
                }
                detailPanelAddress.setTextOrHide(address)
                detailPanelOpenHours.setTextOrHide(state.venueDetails.hours?.status)
                detailPanelPhone.setTextOrHide(state.venueDetails.contact?.phone)
                detailPanelWebsite.setTextOrHide(state.venueDetails.url)
                detailsContainer.visibility = View.VISIBLE
                detailsProgress.visibility = View.GONE
            }

            is DetailViewContentState.Error -> {
                if (state.exception is HttpException && state.exception.code() == 429) {
                    detailPanelName.setTextOrHide(state.venue.name)
                    var address = ""
                    state.venue.location.formattedAddress?.forEach {
                        address += it
                        address += "\n"
                    }
                    detailPanelAddress.setTextOrHide(address)
                    detailPanelDescription.setTextOrHide("Unfortunately API's daily quota was exceeded. Only basic information is available")
                    detailPanelOpenHours.setTextOrHide(null)
                    detailPanelPhone.setTextOrHide(null)
                    detailPanelWebsite.setTextOrHide(null)
                    detailsContainer.visibility = View.VISIBLE
                    detailsProgress.visibility = View.GONE
                } else {
                    showErrorDialog(state.exception)
                    dismiss()
                }

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

    private fun TextView.setTextOrHide(text: String?) {
        text?.let {
            if (text != "") {
                this.text = it
                this.visibility = View.VISIBLE
            } else {
                this.visibility = View.GONE
            }

        } ?: run {
            this.visibility = View.GONE
        }
    }
}