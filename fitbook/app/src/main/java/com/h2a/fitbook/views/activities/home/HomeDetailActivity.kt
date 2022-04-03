package com.h2a.fitbook.views.activities.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.h2a.fitbook.R
import com.h2a.fitbook.models.ExerciseModel
import com.h2a.fitbook.viewmodels.home.ExerciseDetailViewModel

class HomeDetailActivity : AppCompatActivity() {
    private lateinit var createScheduleFab: FloatingActionButton
    private val exerciseDetailViewModel: ExerciseDetailViewModel by viewModels()

    private val getSecondActivityIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_detail)

        // get exercise by id from API
        val exerciseId = intent.getStringExtra("EXERCISE_ID")
        val steps = arrayListOf<String>()
        for (i in 1..5) {
            steps.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Id sed vitae diam morbi ut eget dolor. Suspendisse dictum ipsum risus sodales nisi id.")
        }
        val exercise = ExerciseModel(
            "1",
            "Bài 1",
            2,
            50,
            "https://i.ibb.co/qg4Pjx4/bg-home-list-item-running.png",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Id sed vitae diam morbi ut eget dolor. Suspendisse dictum ipsum risus sodales nisi id.",
            steps
        )

        exerciseDetailViewModel.setThumbnail(exercise.image)
        exerciseDetailViewModel.setTitle(exercise.title)
        exerciseDetailViewModel.setDetail("${exercise.duration} Phút | ${exercise.calories} Calo")
        exerciseDetailViewModel.setDescription(exercise.description)
        exerciseDetailViewModel.setSteps(exercise.steps)

        createScheduleFab = findViewById(R.id.home_detail_fab_create_schedule)
        createScheduleFab.setOnClickListener {
            val intent = Intent(this, HomeRescheduleActivity::class.java)
            intent.putExtra("EXERCISE_THUMBNAIL", exercise.image)
            intent.putExtra("EXERCISE_TITLE", exercise.title)
            intent.putExtra("EXERCISE_DETAIL", "${exercise.duration} Phút | ${exercise.calories} Calo")
            getSecondActivityIntent.launch(intent)
        }
    }
}