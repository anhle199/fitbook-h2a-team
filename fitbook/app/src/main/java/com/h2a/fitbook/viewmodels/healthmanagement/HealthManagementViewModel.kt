package com.h2a.fitbook.viewmodels.healthmanagement

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h2a.fitbook.R
import com.h2a.fitbook.models.GeneralInfoModel
import com.h2a.fitbook.models.HealthManagementFood
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

class HealthManagementViewModel : ViewModel() {
    private var db = FirebaseFirestore.getInstance()
    private var generalInfo: GeneralInfoModel? = null
    private var tempGeneralInfo: GeneralInfoModel? = null
    var foodList: MutableList<HealthManagementFood> = arrayListOf()
    private var auth = FirebaseAuth.getInstance()

    private fun getWeekRange(
        context: Context, date: LocalDate
    ): String { // determine country (Locale) specific first day of current week
        val firstDayOfWeek: DayOfWeek = WeekFields.of(Locale("vi")).firstDayOfWeek
        val startOfCurrentWeek: LocalDate =
            date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))

        // determine last day of current week
        val lastDayOfWeek: DayOfWeek = firstDayOfWeek.plus(6) // or minus(1)

        val endOfWeek: LocalDate = date.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))

        return startOfCurrentWeek.format(
            DateTimeFormatter.ofPattern(
                context.getString(
                    R.string.date_format_db
                )
            )
        ) + "-" + endOfWeek.format(
            DateTimeFormatter.ofPattern(
                context.getString(
                    R.string.date_format_db
                )
            )
        )
    }

    fun undo() {
        if (tempGeneralInfo != null) generalInfo = tempGeneralInfo!!.clone()
    }

    fun save(
        info: GeneralInfoModel,
        curList: List<HealthManagementFood>,
        context: Context,
        date: LocalDate,
        toast: ((String) -> Unit)
    ) {
        toast("Đang lưu ...")
        var size = curList.size + 1
        for (item in curList) {
            db.collection("user_health").document(auth.uid!!)
                .collection(getWeekRange(context, date))
                .document(date.dayOfWeek.toString().lowercase()).collection("foodList")
                .document(item._id).delete().addOnSuccessListener {
                    --size
                    if (size == 0) toast("Lưu thành công!")
                }.addOnFailureListener {
                    Log.i("Debug", it.toString())
                    toast("Có lỗi xảy ra khi lưu ${item._name}!")
                }
        }
        val infoHashMap = info.toHashMap()
        if (infoHashMap != null) {
            db.collection("user_health").document(auth.uid!!)
                .collection(getWeekRange(context, date))
                .document(date.dayOfWeek.toString().lowercase()).set(infoHashMap)
                .addOnSuccessListener {
                    --size
                    if (size == 0) toast("Lưu thành công!")
                }.addOnFailureListener {
                    Log.i("Debug", it.toString())
                    toast("Có lỗi xảy ra khi lưu thông tin sức khỏe!")
                }
        }
    }

    fun loadData(
        context: Context,
        date: LocalDate,
        updateForm: ((GeneralInfoModel?) -> Unit),
        toast: ((String) -> Unit),
        notifyInsert: ((Int) -> Unit)
    ) {
        toast("Đang tải dữ liệu ...")
        val calendar = Calendar.getInstance()
        db.collection("user_health").document(auth.uid!!).collection(getWeekRange(context, date))
            .document(date.dayOfWeek.toString().lowercase()).get().addOnSuccessListener {
                db.collection("users").document(auth.uid!!).get().addOnSuccessListener { user ->
                    calendar.time = (user.data?.get("dateOfBirth") as Timestamp).toDate()
                    val age: Int = date.year - calendar.get(Calendar.YEAR)
                    var height = 0.0
                    var weight = 0.0
                    if (it.data != null) {
                        try {
                            height = it.data?.get("height").toString().toDouble()
                        } catch (e: NumberFormatException) {
                        }
                        try {
                            weight = it.data?.get("weight").toString().toDouble()
                        } catch (e: NumberFormatException) {
                        }
                    }
                    generalInfo = GeneralInfoModel(height, weight, age)
                    updateForm(generalInfo)
                    if (generalInfo != null) tempGeneralInfo = generalInfo!!.clone()
                }.addOnFailureListener { ex ->
                    Log.i("Debug", ex.toString())
                    toast("Có lỗi xảy ra!")
                }
            }.addOnFailureListener {
                Log.i("Debug", it.toString())
                toast("Có lỗi xảy ra!")
            }
        db.collection("user_health").document(auth.uid!!).collection(getWeekRange(context, date))
            .document(date.dayOfWeek.toString().lowercase()).collection("foodList").get()
            .addOnSuccessListener {
                foodList.clear()
                notifyInsert(foodList.size - 1)
                for (document in it) {
                    foodList.add(
                        HealthManagementFood(
                            document.id,
                            document.data["name"].toString(),
                            document.data["caloriesPerUnit"].toString().toFloat(),
                            document.data["image"].toString(),
                            document.data["unit"].toString(),
                            document.data["quantity"].toString().toInt()
                        )
                    )
                    notifyInsert(foodList.size - 1)
                }
                if (foodList.size == 0) {
                    toast("Không có dữ liệu!")
                }
            }.addOnFailureListener {
                Log.i("Debug", it.toString())
                toast("Có lỗi xảy ra!")
            }
    }
}