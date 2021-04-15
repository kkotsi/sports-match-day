package com.example.sports_match_day.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sports_match_day.R
import com.example.sports_match_day.databinding.FragmentDashboardBinding
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.ui.base.BaseFragment
import lecho.lib.hellocharts.model.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 14-Apr-21
 */
class Dashboard : BaseFragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.loadSquadWins()
        viewModel.loadMatchesPerSport()
    }

    private fun setupObservers() {

        viewModel.squadWins.observe(viewLifecycleOwner, {
            binding.chartMatches.columnChartData = generateSquadWinsData(it)
            binding.chartMatches.visibility = View.VISIBLE
            binding.textView4.visibility = View.VISIBLE
        })

        viewModel.matchesPerSport.observe(viewLifecycleOwner, {
            binding.chartSports.pieChartData = generateMatchesPerSportData(it)
            binding.chartSports.visibility = View.VISIBLE

            val colorNames = mutableListOf<PieChartColors>()
            colorNames.addAll(pieChartColors(requireContext()))
            it.onEachIndexed { index, item ->
                if (index < colorNames.size) {
                    colorNames[index].name = item.key.name
                }
            }
            binding.recyclerChartColors.visibility = View.VISIBLE
            binding.recyclerChartColors.adapter = PieChartColorsAdapter(colorNames)
            binding.recyclerChartColors.layoutManager = LinearLayoutManager(context)
            binding.textView5.visibility = View.VISIBLE
        })

        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            binding.progressBar.isVisible = it
        })
    }

    private fun generateMatchesPerSportData(map: Map<Sport, Int>): PieChartData {
        val data = PieChartData()
        val values = mutableListOf<SliceValue>()

        map.onEachIndexed { index, it ->
            val slice = SliceValue(it.value.toFloat())
            val colors = pieChartColors(requireContext())
            if (index < colors.size)
                slice.color = colors[index].color

            slice.setLabel(it.value.toString())
            values.add(slice)
        }

        data.setHasLabels(true)
        data.values = values
        return data
    }

    private fun generateSquadWinsData(map: Map<Squad, Int>): ColumnChartData {
        val data = ColumnChartData()
        val columns = mutableListOf<Column>()
        val axesWins = mutableListOf<Float>()
        axesWins.add(0f)
        map.forEach {
            val values = mutableListOf<SubcolumnValue?>()
            val wins = it.value
            axesWins.add(wins.toFloat())
            val subColumn = SubcolumnValue(wins.toFloat())
            subColumn.setLabel(it.key.name)

            subColumn.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

            values.add(subColumn)

            val column = Column(values)
            column.setHasLabels(true)

            columns.add(column)
        }
        data.isValueLabelBackgroundEnabled = true
        data.isValueLabelBackgroundAuto = false

        data.axisYLeft = Axis.generateAxisFromCollection(axesWins)
        data.axisYLeft.name = "Wins"

        data.valueLabelBackgroundColor =
            ContextCompat.getColor(requireContext(), R.color.colorBackgroundCard)
        data.setValueLabelsTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryDark
            )
        )
        data.columns = columns
        return data
    }

    companion object {
        fun pieChartColors(context: Context): MutableList<PieChartColors>{
            return mutableListOf(
                PieChartColors(ContextCompat.getColor(context, R.color.colorPrimary), ""),
                PieChartColors(Color.YELLOW, ""),
                PieChartColors(Color.GREEN, ""),
                PieChartColors(Color.LTGRAY, "")
            )
        }
    }

    class PieChartColors(val color: Int, var name: String = "")
}