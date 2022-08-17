package com.gscapin.blogger.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gscapin.blogger.R
import com.gscapin.blogger.core.BaseViewHolder
import com.gscapin.blogger.core.TimeAgo
import com.gscapin.blogger.data.model.Post
import com.gscapin.blogger.databinding.PostItemBinding

class HomeAdapter(
    private val postList: List<Post>,
    private val onPostClickListener: OnPostClickListener,
    private val onNameClickListener: OnNameClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var postClickListener: OnPostClickListener? = null
    private var nameClickListener: OnNameClickListener? = null

    init {
        postClickListener = onPostClickListener
        nameClickListener = onNameClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding =
            PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(itemBinding, parent.context)
    }

    private inner class HomeViewHolder(val itemBinding: PostItemBinding, val context: Context) :
        BaseViewHolder<Post>(itemBinding.root) {
        override fun bind(item: Post) {
            setPostImage(item)
            setUserInfo(item)
            setTimestamp(item)
            isLikePost(item)
            setCounterLikes(item)
            setDescription(item)
            likeClickAction(item)
            nameClickAction(item)
        }

        private fun setTimestamp(item: Post) {
            val time = item.createdAt?.time?.div(1000L)?.toInt()
            itemBinding.timePost.text = time?.let { TimeAgo.getTime(it) }
        }

        private fun nameClickAction(item: Post){
            itemBinding.headerPost.setOnClickListener {
                nameClickListener?.onNameButtonClick(item)
            }
        }

        private fun likeClickAction(item: Post) {
            itemBinding.isLikePost.setOnClickListener {
                if(item.liked) item.apply {
                    liked = false
                }else item.apply{
                    liked = true
                }
                isLikePost(item)
                postClickListener?.onLikeButtonClick(item, item.liked)
            }
        }

        private fun setDescription(item: Post) {
            itemBinding.descriptionPost.text = item.postDescription
        }

        private fun setCounterLikes(item: Post) {
            itemBinding.numberLikes.text = if(item.likes>0) "${item.likes} likes" else "Me gusta"
        }

        private fun isLikePost(item: Post) {
            if (item.liked){
                itemBinding.isLikePost.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_favorite_24
                    )
                )
                itemBinding.isLikePost.setColorFilter(ContextCompat.getColor(context, R.color.teal_700))
            }else{
                itemBinding.isLikePost.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_favorite_border_24
                    )
                )
            }
        }

        private fun setUserInfo(item: Post) {
            Glide.with(context).load(item.poster?.profilePicture).centerCrop().into(itemBinding.imageUserPost)
            itemBinding.nameUserPost.text = item.poster?.username.toString()
        }


        private fun setPostImage(item: Post) {
            Glide.with(context).load(item.postImage).centerCrop().into(itemBinding.imagePost)
        }



    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is HomeViewHolder -> holder.bind(postList[position])
        }
    }

    override fun getItemCount(): Int = postList.size

}

interface OnPostClickListener {
    fun onLikeButtonClick(post: Post, liked: Boolean)
}

interface OnNameClickListener{
    fun onNameButtonClick(post: Post)
}
