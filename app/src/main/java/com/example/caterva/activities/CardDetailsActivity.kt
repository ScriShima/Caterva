package com.example.caterva.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caterva.R
import com.example.caterva.adapters.CardMemberListItemsAdapter
import com.example.caterva.dialogs.LabelColorListDialog
import com.example.caterva.dialogs.MembersListDialog
import com.example.caterva.firebase.FirestoreClass
import com.example.caterva.models.*
import com.example.caterva.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList: ArrayList<User>
    private var mSelectedDueDateMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        getIntentData()
        setupActionBar()

        et_name_card_details.setText(
            mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()) {
            setColor()
        }

        btn_update_card_details.setOnClickListener {
            if(et_name_card_details.text.toString().isNotEmpty()) {
                updateCardDetails()
            }else {
                Toast.makeText(
                    this,
                    "Введите имя карточки",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        tv_select_label_color.setOnClickListener {
            labelColorsListDialog()
        }

        tv_select_members.setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()

        mSelectedDueDateMilliSeconds =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].dueDate

        if(mSelectedDueDateMilliSeconds > 0) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            tv_select_due_date.text = selectedDate
        }

        tv_select_due_date.setOnClickListener {
            showDataPicker()
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_card_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = mBoardDetails
                .taskList[mTaskListPosition]
                .cards[mCardPosition].name
        }

        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun colorList(): ArrayList<String> {
        val colorList: ArrayList<String> = ArrayList()
        colorList.add("#FF0000")
        colorList.add("#FF1493")
        colorList.add("#FFA500")
        colorList.add("#800080")
        colorList.add("#00FF00")
        colorList.add("#008000")
        colorList.add("#00FFFF")
        colorList.add("#0000FF")

        return colorList
    }

    private fun setColor() {
        tv_select_label_color.text = ""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(
                    mBoardDetails.taskList[mTaskListPosition]
                        .cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    private fun membersListDialog() {
        var cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition]
                .cards[mCardPosition].assignedTo

        if(cardAssignedMembersList.size > 0) {
            for(i in mMembersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if(mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        }else {
            for(i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ) {
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT) {
                    if(!mBoardDetails.taskList[mCardPosition]
                            .cards[mCardPosition]
                            .assignedTo.contains(user.id)) {

                        mBoardDetails.taskList[mCardPosition]
                            .cards[mCardPosition]
                            .assignedTo.add(user.id)
                    }
                } else {
                    mBoardDetails.taskList[mCardPosition]
                        .cards[mCardPosition]
                        .assignedTo.remove(user.id)

                    for(i in mMembersDetailList.indices) {
                        if(mMembersDetailList[i].id == user.id) {
                            mMembersDetailList[i].selected = false
                        }
                    }
                }

                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }

    private fun updateCardDetails() {
        val card = Card(
            et_name_card_details.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )



        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard() {
        val cardList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards

        cardList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cards = cardList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)

        builder.setTitle(resources.getString(R.string.alert))

        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)


        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss()

            deleteCard()

        }

        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun labelColorsListDialog() {
        val colorsList: ArrayList<String> = colorList()

        val listDialog = object : LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor

        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }

        listDialog.show()
    }

    private fun setupSelectedMembersList() {
        val cardAssignedMemberList =
            mBoardDetails.taskList[mTaskListPosition]
                .cards[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for(i in mMembersDetailList.indices) {
            for (j in cardAssignedMemberList) {
                if(mMembersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            tv_select_members.visibility = View.GONE
            rv_selected_members_list.visibility = View.VISIBLE

            rv_selected_members_list.layoutManager = GridLayoutManager(
                this,
                6
            )

            val adapter = CardMemberListItemsAdapter(this, selectedMembersList, true)
            rv_selected_members_list.adapter = adapter
            adapter.setOnClickListener(
                object : CardMemberListItemsAdapter.OnClickListener {
                    override fun onClick() {
                        membersListDialog()
                    }
                }
            )

        }else {
            tv_select_members.visibility = View.VISIBLE
            rv_selected_members_list.visibility = View.GONE
        }
    }

    private fun showDataPicker() {

        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"

                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"

                tv_select_due_date.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                val theDate = sdf.parse(selectedDate)

                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show()
    }

}