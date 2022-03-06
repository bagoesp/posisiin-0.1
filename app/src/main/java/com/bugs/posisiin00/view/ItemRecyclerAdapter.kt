package com.bugs.posisiin00.view

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bugs.posisiin00.databinding.SampelItemBinding
import com.bugs.posisiin00.model.DataSampel

class ItemRecyclerAdapter(
    private val activity: KelolaDataActivity,
    private val listSampel : List<DataSampel>
) : RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SampelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listSampel[position])
    }

    override fun getItemCount(): Int = listSampel.size

    inner class ViewHolder(private val binding: SampelItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataSampel) {
            binding.tvLabelSampel.text = item.lokasi.toString()
            binding.tvAp1Level.text = item.ap1.toString()
            binding.tvAp2Level.text = item.ap2.toString()
            binding.tvAp3Level.text = item.ap3.toString()
            binding.tvCreationTime.text = DateUtils.getRelativeTimeSpanString(item.waktu_input!!)

            binding.btnDeleteSampel.setOnClickListener {
                activity.dialogHapus(item)
            }
        }
    }
}