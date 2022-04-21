package com.h2a.fitbook.views.fragments.healthmanagement

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.healthmanagement.HealthManagementFoodListAdapter
import com.h2a.fitbook.databinding.FragmentHealthManagementBinding
import com.h2a.fitbook.models.GeneralInfoModel
import com.h2a.fitbook.viewmodels.healthmanagement.HealthManagementViewModel
import com.h2a.fitbook.views.activities.addfood.AddFoodActivity
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class HealthManagementFragment : Fragment() {
    private var _binding: FragmentHealthManagementBinding? = null
    private var editMode = false
    private lateinit var adapter: HealthManagementFoodListAdapter
    private var toast: Toast? = null
    private lateinit var healthManagementVM: HealthManagementViewModel

    private fun setupAndShowDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resources.getString(R.string.date_picker_default_title))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat(resources.getString(R.string.date_format))
            _binding!!.healthManagementEtFormDate.setText(dateFormatter.format(it))
            updateInfo()
        }

        datePicker.show(parentFragmentManager, "birthdaySelection")
    }

    private fun setInputState(widget: TextInputEditText, state: Boolean) {
        widget.isFocusable = !state
        widget.isCursorVisible = !state
        widget.isFocusableInTouchMode = !state
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthManagementBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        val rcvList = _binding!!.healthManagementRcvFoodList

        healthManagementVM = ViewModelProvider(this)[HealthManagementViewModel::class.java]

        _binding!!.healthManagementEtFormDate.setText(SimpleDateFormat(resources.getString(R.string.date_format)).format(Date()))

        adapter = HealthManagementFoodListAdapter(healthManagementVM.foodList)

        rcvList.adapter = adapter

        rcvList.layoutManager = LinearLayoutManager(activity)

        val menuBtn = _binding!!.healthManagementFabMenu

        menuBtn.setOnClickListener {
            val curState = _binding!!.healthManagementFabAdd.visibility == View.INVISIBLE
            _binding!!.healthManagementFabAdd.visibility = if (curState) View.VISIBLE else View.INVISIBLE
            _binding!!.healthManagementFabEdit.visibility = if (curState) View.VISIBLE else View.INVISIBLE
            _binding!!.healthManagementFabUndo.visibility = if (curState && editMode) View.VISIBLE else View.INVISIBLE

            if (curState) {
                menuBtn.backgroundTintList = ContextCompat.getColorStateList(this.requireContext(), R.color.fab_close_color)
                menuBtn.setImageResource(R.drawable.ic_round_close_24)
            } else {
                menuBtn.backgroundTintList = ContextCompat.getColorStateList(this.requireContext(), R.color.primary)
                menuBtn.setImageResource(R.drawable.ic_round_menu_24)
            }
        }

        _binding!!.healthManagementFabAdd.setOnClickListener {
            val intent = Intent(this.requireContext(), AddFoodActivity::class.java)
            intent.putExtra("Date", _binding!!.healthManagementEtFormDate.text.toString())

            startActivity(intent)
        }

        _binding!!.healthManagementFabEdit.setOnClickListener {
            fun setEditMode() {
                adapter.setSwipeState(!editMode, rcvList)
                setInputState(_binding!!.healthManagementEtFormHeight, editMode)
                setInputState(_binding!!.healthManagementEtFormWeight, editMode)
                _binding!!.healthManagementFabUndo.visibility = if (editMode) View.INVISIBLE else View.VISIBLE
                _binding!!.healthManagementFabEdit.setImageResource(if (editMode) R.drawable.ic_round_edit_24 else R.drawable.ic_round_done_24)
                editMode = !editMode
            }
            if (editMode) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn lưu thay đổi này không?")
                    .setPositiveButton("Có") { _, _ ->
                        val dateString = _binding!!.healthManagementEtFormDate.text!!.split("/")
                        val height: Double = try {
                            _binding!!.healthManagementEtFormHeight.text.toString().toDouble()
                        } catch (e: NumberFormatException) {
                            0.0
                        }
                        val weight: Double = try {
                            _binding!!.healthManagementEtFormWeight.text.toString().toDouble()
                        } catch (e: NumberFormatException) {
                            0.0
                        }
                        val age: Int = try {
                            _binding!!.healthManagementEtFormAge.text.toString().toInt()
                        } catch (e: NumberFormatException) {
                            0
                        }

                        healthManagementVM.save(GeneralInfoModel(height, weight, age), adapter.getCurrentListChanges(), requireContext(), LocalDate.of(dateString[2].toInt(), dateString[1].toInt(), dateString[0].toInt()), toastCallback)
                        setEditMode()
                    }
                    .setNegativeButton("Không") { _,_ ->
                    }
                    .setCancelable(false)
                    .show()
            } else
                setEditMode()
        }

        _binding!!.healthManagementFabUndo.setOnClickListener {
            healthManagementVM.undo()
            adapter.cancelEdit()
        }

        _binding!!.healthManagementTilFormDate.setEndIconOnClickListener {
            setupAndShowDatePicker()
        }

        return root
    }

    private val toastCallback = { message: String ->
        toast?.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    private val updateForm = { info: GeneralInfoModel? ->
        if (info != null) {
            _binding!!.healthManagementEtFormHeight.setText(info._height.toString())
            _binding!!.healthManagementEtFormWeight.setText(info._weight.toString())
            _binding!!.healthManagementEtFormAge.setText(info._age.toString())
        } else {
            _binding!!.healthManagementEtFormHeight.setText("")
            _binding!!.healthManagementEtFormWeight.setText("")
            _binding!!.healthManagementEtFormAge.setText("")
        }
    }

    private val notifyInsert = { position: Int ->
        if (position == -1) {
            adapter.notifyChangeAll()
            if (SimpleDateFormat(resources.getString(R.string.date_format)).format(Date()).equals(_binding!!.healthManagementEtFormDate.text.toString()))
                _binding!!.healthManagementTvAlert.visibility = View.VISIBLE
        } else {
            adapter.notifyChangeAt(position)
            _binding!!.healthManagementTvAlert.visibility = View.GONE
        }
    }

    private fun updateInfo() {
        val dateString = _binding!!.healthManagementEtFormDate.text!!.split("/")

        healthManagementVM.loadData(requireContext(), LocalDate.of(dateString[2].toInt(), dateString[1].toInt(), dateString[0].toInt()), updateForm, toastCallback, notifyInsert)
    }

    override fun onStart() {
        super.onStart()
        updateInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
