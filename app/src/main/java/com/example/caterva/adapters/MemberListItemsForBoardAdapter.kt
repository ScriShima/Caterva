package com.example.caterva.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.content.Intent
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caterva.R
import com.example.caterva.activities.MembersActivity
import com.example.caterva.firebase.FirestoreClass
import com.example.caterva.models.Board
import com.example.caterva.models.User
import com.example.caterva.utils.Constants
import kotlinx.android.synthetic.main.item_member_delete.view.*

class MemberListItemsForBoardAdapter (
    private val context: Context,
    private var list: ArrayList<User>,
    private var mBoardDetails: Board

): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member_delete,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_member_image_board)

            holder.itemView.tv_member_name_board.text = model.name
            holder.itemView.tv_member_email_board.text = model.email
            holder.itemView.iv_deleted_member.visibility = View.VISIBLE

            holder.itemView.iv_deleted_member.setOnClickListener {
                if (model.name == mBoardDetails.createdBy) {
                    Toast.makeText(context, "Вы не можете удалить создателя доски", Toast.LENGTH_SHORT).show()
                }
                else {
                    FirestoreClass().deleteAssignedMembers(MembersActivity(),mBoardDetails.documentId, model.id)
                    Toast.makeText(context, "Пользователь ${model.name}, успешно удалён", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    interface OnClickListener {
        fun onClick()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}