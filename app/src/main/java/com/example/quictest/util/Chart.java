package com.example.quictest.util;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class Chart {
    private final LineChart chart;

    public enum DataSetIndex{
        H2, H3
    }

    public Chart(LineChart chart){
        this.chart = chart;
    }

    // 绘制图标样式
    public void buildChart() {

        chart.setDescription(new Description());
        chart.getDescription().setText("");
        chart.setNoDataText("请点击上方按钮开始下载测试");

        // x轴
        chart.setVisibleXRangeMaximum(100);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        // y轴
        chart.getAxisLeft().setEnabled(false); // no left axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);

        chart.setData(new LineData());
        LineData data = chart.getData();
        data.addDataSet(createLineDataSet(Chart.DataSetIndex.H2));
        data.addDataSet(createLineDataSet(Chart.DataSetIndex.H3));
    }

    // 为图标添加data
    public void addChartData(Chart.DataSetIndex index, long yData){
        LineData data = chart.getData();
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(index.ordinal());
        if(set == null){
            set = createLineDataSet(index);
            data.addDataSet(set);
        }
        Entry entry = new Entry(set.getEntryCount(),yData);
        data.addEntry(entry,index.ordinal());
    }

    public void update(){
        LineData data = chart.getData();
        if(data != null) {
            // update
            chart.notifyDataSetChanged();
            chart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createLineDataSet(Chart.DataSetIndex index) {
        LineDataSet set;
        if(index == Chart.DataSetIndex.H2) {
            set = new LineDataSet(null, "Tcp延迟(ns)");
            set.setColor(Color.rgb(255, 51, 51));
        } else{
            set = new LineDataSet(null, "Quic延迟(ns)");
            set.setColor(Color.rgb(51, 255, 51));
        }
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set.setDrawCircles(true);
        set.setCircleRadius(3f);
        set.setValueTextSize(0f);
        set.setLineWidth(2f);
        return set;
    }
}
