package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.databinding.UserLayoutBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnUserListener
import qnopy.com.qnopyandroid.requestmodel.SUser

class UsersAdapter(
    private val userList: ArrayList<SUser>,
    val userClickListener: OnUserListener,
    private val userId: Int
) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: UserLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: SUser) = with(binding) {
            tvUserName.text = user.userName

            ivUser.setOnClickListener {
                userClickListener.onUserBatchClicked(
                    user,
                    absoluteAdapterPosition
                )
            }
            tvUserName.setOnClickListener {
                userClickListener.onUserNameClicked(
                    user,
                    absoluteAdapterPosition
                )
            }

            if (user.userId == userId)
                btnDelete.visibility = View.INVISIBLE
            btnDelete.setOnClickListener {
                userClickListener.onUserDeleteClicked(
                    user,
                    absoluteAdapterPosition
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = userList.size
    fun removeItem(userId: String, posToRemove: Int) {
        var userToRemove: SUser? = null
        for (user in userList) {
            if (user.userId == userId.toInt())
                userToRemove = user
        }

        userToRemove?.let {
            userList.remove(userToRemove)
        }

        notifyItemRemoved(posToRemove)
    }
}