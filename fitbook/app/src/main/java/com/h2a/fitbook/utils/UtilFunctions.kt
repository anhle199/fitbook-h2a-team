package com.h2a.fitbook.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.models.UserModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList

object UtilFunctions {

    fun makeUnderlineForTextView(textView: TextView, text: String) {
        val textUnderlined = SpannableString(text)
        textUnderlined.setSpan(UnderlineSpan(), 0, textUnderlined.length, 0)
        textView.text = textUnderlined
    }

    // Returns all colors when `size` parameter is null.
    fun generateColorSet(size: Int? = null): List<Int> {
        val colors = ArrayList<Int>()
        colors.addAll(ColorTemplate.MATERIAL_COLORS.toList())
        colors.addAll(ColorTemplate.COLORFUL_COLORS.toList())
        colors.addAll(ColorTemplate.JOYFUL_COLORS.toList())
        colors.addAll(ColorTemplate.LIBERTY_COLORS.toList())
        colors.addAll(ColorTemplate.PASTEL_COLORS.toList())
        colors.addAll(ColorTemplate.VORDIPLOM_COLORS.toList())

        size?.let {
            val safeSize = if (it > 0) it else 0
            return colors.subList(0, safeSize).toList()
        }

        return colors
    }

    fun convertDateStringToTimestamp(dateInString: String, dateFormatPattern: String): Timestamp {
        val vnLocale = Locale("vi", "VN")
        val formatter = SimpleDateFormat(dateFormatPattern, vnLocale)
        val dobInDate = formatter.parse(dateInString)!!

        return Timestamp(dobInDate)
    }

    fun getWeekRangeInString(date: LocalDate): String {
        // determine country (Locale) specific first day of current week
        val firstDayOfWeek: DayOfWeek = WeekFields.of(Locale("vi")).firstDayOfWeek
        val startOfCurrentWeek: LocalDate =
            date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))

        // determine last day of current week
        val lastDayOfWeek: DayOfWeek = firstDayOfWeek.plus(6) // or minus(1)

        val endOfWeek: LocalDate = date.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))

        val formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_IN_USER_HEALTH)
        return startOfCurrentWeek.format(formatter) + "-" + endOfWeek.format(formatter)
    }

    fun convertViewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    fun saveBitmapToGallery(bitmap: Bitmap): Boolean {
        try {
            // Create new file
            val path = Environment.getExternalStorageDirectory().toString() + File.separator
            val filename = "${System.currentTimeMillis()}.png"
            val file = File(path + filename)
            file.createNewFile()

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return true
        } catch (e: Exception) {
            Log.i("StatisticByDate", "saveBitmapToGallery::failed - ${e.message}")
        }

        return false
    }

    fun fetchUserData(userId: String, completion: (Boolean, UserModel?) -> Unit) {
        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION_NAME)
            .document(userId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val snapshot = it.result
                    val loadedObject = snapshot.toObject(UserModel::class.java)
                    completion(true, loadedObject)

                    Log.i("Profile", "fetchUserData:success - get user data")
                } else {
                    Log.i("Profile", "fetchUserData:failed - get user data")
                    completion(false, null)
                }
            }
    }

    fun convertFloatToFormattedString(value: Float): String {
        return String.format("%.2f", value)
    }

}
