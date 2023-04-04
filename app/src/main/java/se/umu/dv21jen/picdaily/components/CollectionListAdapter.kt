package se.umu.dv21jen.picdaily.components

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import se.umu.dv21jen.picdaily.CollectionActivity
import se.umu.dv21jen.picdaily.R
import se.umu.dv21jen.picdaily.models.UserCollection
import se.umu.dv21jen.picdaily.utils.TimeObj

class CollectionListAdapter(
    private val collections: List<UserCollection>,
    private val context: Context,
) : BaseAdapter() {

    override fun getCount(): Int {
        return collections.size
    }

    override fun getItem(p0: Int): Any {
        return collections[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView

        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.collection_item, parent, false)
        }

        val purposeTextView = convertView?.findViewById<TextView>(R.id.collection_purpose_text)
        val statusCardView = convertView?.findViewById<CardView>(R.id.picture_taken_bg)
        val statusTextView = convertView?.findViewById<TextView>(R.id.picture_taken_text)
        val collection = collections[position]

        if (purposeTextView != null) {
            purposeTextView.text = collection.purpose
        }

        val date = TimeObj(collection.lastPictureTaken, "GMT")
        val sameDay = date.isWithinToday()

        if (statusCardView != null) {
            val color = if (sameDay) {
                context.getColor(R.color.status_good)
            } else {
                context.getColor(R.color.status_error)
            }

            statusCardView.setCardBackgroundColor(color)
        }

        if (statusTextView != null) {
            val text = if (sameDay) {
                context.resources.getString(R.string.picture_taken_today)
            } else {
                context.resources.getString(R.string.no_picture_today)
            }

            statusTextView.text = text
        }

        convertView?.findViewById<CardView>(R.id.collection_list_button)?.setOnClickListener {
            val intent = Intent(context, CollectionActivity::class.java)
            intent.putExtra("COLLECTION", collection)
            context.startActivity(intent)
        }

        return convertView
    }
}