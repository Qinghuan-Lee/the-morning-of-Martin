package com.martin.morning.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.martin.morning.R

class AlarmListFragment : Fragment() {

    private val viewModel: AlarmViewModel by viewModels {
        AlarmViewModel.Factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_alarm_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_alarms)
        val adapter = AlarmAdapter(
            onToggle = { alarm, enabled -> viewModel.toggleAlarm(alarm, enabled) },
            onClick = { alarm -> /* TODO: 打开编辑页 */ }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.alarms.observe(viewLifecycleOwner) { alarms ->
            adapter.submitList(alarms)
        }

        view.findViewById<ExtendedFloatingActionButton>(R.id.fab_add).setOnClickListener {
            // TODO: 打开新建闹钟页
        }
    }
}
