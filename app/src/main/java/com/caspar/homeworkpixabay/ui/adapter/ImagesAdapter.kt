package com.caspar.homeworkpixabay.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.caspar.homeworkpixabay.databinding.CellImageBinding
import com.caspar.homeworkpixabay.databinding.CellImageListBinding
import com.caspar.homeworkpixabay.model.dataClass.Hit
import com.caspar.homeworkpixabay.ui.enumClass.ImageDisplayType

class ImagesAdapter(initDisplayType: ImageDisplayType) : ListAdapter<Hit, RecyclerView.ViewHolder>(DiffImages()) {
    private lateinit var parentContext: Context
    private var clickListener: ((String) -> Unit)? = null
    private var currentDisplayType: ImageDisplayType

    init {
        currentDisplayType = initDisplayType
    }

    class ImageViewHolder(
        private val binding: CellImageBinding,
        private val cellWidth: Int,
        private val cellHeight: Int,
        private val orientation: Int,
        private val viewHolderListener: ((String) -> Unit)?,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(image: Hit) {
            val ratio: Float = image.imageHeight.toFloat() / image.imageWidth.toFloat()

            with(binding.root) {
                layoutParams.apply {
                    height =
                        if (orientation == LinearLayout.VERTICAL) (cellWidth * ratio).toInt() else LinearLayout.LayoutParams.MATCH_PARENT
                    width =
                        if (orientation == LinearLayout.VERTICAL) LinearLayout.LayoutParams.MATCH_PARENT else (cellHeight / ratio).toInt()
                }
            }

            with(binding.imageView) {
                Glide.with(this)
                    .load(image.previewURL)
                    .centerInside()
                    .into(this)

                setOnClickListener {
                    viewHolderListener?.invoke(image.largeImageURL ?: "")
                }
            }
        }
    }

    class ImageListViewHolder(
        private val context: Context,
        private val binding: CellImageListBinding,
        private val viewHolderListener: ((String) -> Unit)?,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(image: Hit) {
            with(binding) {
                Glide.with(imageView)
                    .load(image.previewURL)
                    .centerInside()
                    .into(imageView)

                name.text = "Name: ${image.largeImageURL?.takeLast(20) ?: "Unknown"}"
                tag.text = "Tags: " + image.tags
                resolution.text = "Resolution: ${image.imageWidth} x ${image.imageHeight}"
                size.text = "Size: ${android.text.format.Formatter.formatFileSize(context, image.imageSize.toLong())}"

                root.setOnClickListener {
                    viewHolderListener?.invoke(image.largeImageURL ?: "")
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            "photo" -> 1
            "illustration" -> 2
            "vector" -> 3
            else -> 0  // including "all"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        parentContext = parent.context

        val viewHolder =
            when (currentDisplayType) {
                ImageDisplayType.GRID -> {
                    val lm = (parent as RecyclerView).layoutManager as StaggeredGridLayoutManager
                    val orientation = lm.orientation
                    val spanCount = lm.spanCount
                    val cellWidth = parent.width / spanCount
                    val cellHeight = parent.height / spanCount

                    ImageViewHolder(
                        CellImageBinding.inflate(LayoutInflater.from(parentContext), parent, false),
                        cellWidth,
                        cellHeight,
                        orientation,
                        clickListener,
                    )
                }

                ImageDisplayType.LIST -> {
                    ImageListViewHolder(
                        parentContext,
                        CellImageListBinding.inflate(LayoutInflater.from(parentContext), parent, false),
                        clickListener
                    )
                }
            }

        return viewHolder
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (currentDisplayType) {
            ImageDisplayType.GRID -> (holder as ImageViewHolder).onBind(getItem(position))
            ImageDisplayType.LIST -> (holder as ImageListViewHolder).onBind(getItem(position))
        }
    }

    fun setImageClickListener(l: (String) -> Unit) {
        clickListener = l
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDisplayType(newType: ImageDisplayType) {
        currentDisplayType = newType
        this.notifyDataSetChanged()
    }
}