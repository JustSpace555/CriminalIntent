package com.bignerdranch.criminalintent

import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File

class FullPhotoDialog(private val imageFile: File) : DialogFragment() {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

		val dialog = Dialog(context!!)
		dialog.setContentView(R.layout.full_photo_dialog)
		val imageView = dialog.findViewById(R.id.full_photo_view) as ImageView

		if (imageFile.exists()) {
			imageView.setImageBitmap(
				getScaledBitmap(imageFile.absolutePath, requireActivity())
			)
		}
		return dialog
	}

}