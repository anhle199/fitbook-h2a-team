package com.h2a.fitbook.viewmodels.healthmanagement

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.R
import com.h2a.fitbook.models.Food
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

class AddFoodViewModel : ViewModel() {
    private var foodList: MutableList<Food> = arrayListOf()
    var db = FirebaseFirestore.getInstance()

    private fun getWeekRange(context: Context, date: LocalDate): String {
        // determine country (Locale) specific first day of current week
        val firstDayOfWeek: DayOfWeek = WeekFields.of(Locale("vi")).firstDayOfWeek
        val startOfCurrentWeek: LocalDate =
            date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))

        // determine last day of current week
        val lastDayOfWeek: DayOfWeek = firstDayOfWeek.plus(6) // or minus(1)

        val endOfWeek: LocalDate = date.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))

        return startOfCurrentWeek.format(
            DateTimeFormatter.ofPattern(context.getString(
                R.string.date_format_db))) + "-" + endOfWeek.format(
            DateTimeFormatter.ofPattern(context.getString(
                R.string.date_format_db)))
    }

    fun loadFoodList(notifyAdapter: ((Int) -> Unit), toast: ((String) -> Unit)) {
        toast("Đang tải dữ liệu ...")
        db.collection("foods")
            .get()
            .addOnSuccessListener { documents ->
                foodList.clear()
                notifyAdapter(foodList.size - 1)
                for (document in documents) {
                    foodList.add(Food(document.data["name"].toString(), document.data["caloriesPerUnit"].toString().toFloat(), document.data["image"].toString(), document.data["unit"].toString()))
                    notifyAdapter(foodList.size - 1)
                }
                if (foodList.size == 0)
                    toast("Không có dữ liệu!")
            }
            .addOnFailureListener { exception ->
                Log.i("Debug", "Exception: $exception")
                toast("Có lỗi xảy ra!")
            }
    }

    fun getFoodList() = foodList

    fun saveFood(context: Context, date: String, item: Food, amount: Int, toast: ((String) -> Unit)) {
        toast("Đang lưu ....")
        val splitString = date.split("/")
        val formattedDate = LocalDate.of(splitString[2].toInt(), splitString[1].toInt(), splitString[0].toInt())
        db.collection("user_health")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(getWeekRange(context, formattedDate))
            .document(formattedDate.dayOfWeek.toString().lowercase())
            .collection("foodList")
            .add(item.toHashMap(amount))
            .addOnSuccessListener {
                toast("Lưu thành công!")
            }
            .addOnFailureListener {
                Log.i("Debug", it.toString())
                toast("Có lỗi xảy ra!")
            }
    }
}