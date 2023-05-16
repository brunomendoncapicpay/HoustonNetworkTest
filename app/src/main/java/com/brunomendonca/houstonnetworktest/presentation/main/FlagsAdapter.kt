package com.brunomendonca.houstonnetworktest.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.brunomendonca.houstonnetworktest.databinding.FlagItemBinding

class FlagsAdapter(
    private var flagList: List<String> = emptyList()
) : Adapter<FlagsAdapter.FlagItemViewHolder>() {

    fun updateFlags(flagList: List<String>) {
        this.flagList = flagList
        notifyDataSetChanged()
    }

    inner class FlagItemViewHolder(private val flagItemBinding: FlagItemBinding) : ViewHolder(flagItemBinding.root) {
        fun bind(flagName: String) {
            flagItemBinding.tvFlagName.text = flagName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlagItemViewHolder {
        val itemBinding = FlagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlagItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FlagItemViewHolder, position: Int) {
        holder.bind(flagList[position])
    }

    override fun getItemCount(): Int = flagList.size
}