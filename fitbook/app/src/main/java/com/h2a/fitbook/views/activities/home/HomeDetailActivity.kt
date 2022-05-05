package com.h2a.fitbook.views.activities.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.h2a.fitbook.R
import com.h2a.fitbook.viewmodels.home.ExerciseDetailViewModel

class HomeDetailActivity : AppCompatActivity() {
    private lateinit var createScheduleFab: FloatingActionButton
    private val viewModel: ExerciseDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_detail)

        // get exercise by id from API
        val exerciseId = intent.getStringExtra("EXERCISE_ID")
        viewModel.setLoading(true)
        viewModel.getExerciseDetail(exerciseId!!) { isOk, data ->
            viewModel.setLoading(false)
            if (!isOk) {
                Toast.makeText(this, R.string.activity_home_detail_load_error, Toast.LENGTH_SHORT)
                    .show()
            } else {
                createScheduleFab = findViewById(R.id.home_detail_fab_create_schedule)
                createScheduleFab.setOnClickListener {
                    val intent = Intent(
                        this, HomeRescheduleActivity::class.java
                    )
                    intent.putExtra("EXERCISE_THUMBNAIL", data.image)
                    intent.putExtra("EXERCISE_ID", data.id)
                    intent.putExtra("EXERCISE_TITLE", data.name)
                    intent.putExtra("EXERCISE_DURATION", data.measureDuration)
                    intent.putExtra("EXERCISE_CALORIES", data.measureCalories.toString())
                    startActivity(intent)
                }

                // check login
                if (viewModel.auth.currentUser == null) {
                    createScheduleFab.visibility = View.GONE
                }
            }
        }
    }
}
