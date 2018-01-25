package com.hanibey.smartorder.administration.admin;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderbusiness.DateTimeService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.OrderItem;
import com.hanibey.smartordermodel.Report;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentReport extends Fragment {

    private DatabaseReference reportRef;
    DateTimeService dateTimeService;

    private LineChartView chart;
    private LineChartData data;

    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = true;
    private boolean isCubic = true;
    private boolean hasLabelForSelected = true;

    public FragmentReport() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Report);
        dateTimeService = new DateTimeService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        chart = (LineChartView) rootView.findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        generateData();
        chart.setViewportCalculationEnabled(false);
        resetViewport();

        return rootView;
    }


    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        int numberOfPoints = 12;
        v.right = numberOfPoints - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    private void generateData() {

        final String year = dateTimeService.getDate(Constant.DateTypes.Year);
        final String month = dateTimeService.getDate(Constant.DateTypes.Month);
        final String day = dateTimeService.getDate(Constant.DateTypes.Day);

        reportRef.child(year).child(month).child(day).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.exists()){

                    Report report = snapshot.getValue(Report.class);
                    int index = 1;

                    List<Line> lines = new ArrayList<>();
                    List<PointValue> values = new ArrayList<>();

                    for(OrderItem item : report.Items){
                        values.add(new PointValue(index++, Integer.valueOf(item.Quantity)));
                    }

                    Line line = new Line(values);
                    line.setColor(Color.WHITE);
                    line.setShape(shape);
                    line.setCubic(isCubic);
                    line.setFilled(isFilled);
                    line.setHasLabels(hasLabels);
                    line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                    line.setHasLines(hasLines);
                    line.setHasPoints(hasPoints);


                    lines.add(line);

                    data = new LineChartData(lines);

                    if (hasAxes) {
                        Axis axisX = new Axis();
                        Axis axisY = new Axis().setHasLines(true);
                        if (hasAxesNames) {
                            axisX.setName("Günler");
                            axisY.setName("Sipariş Miktarı");
                        }
                        data.setAxisXBottom(axisX);
                        data.setAxisYLeft(axisY);
                    } else {
                        data.setAxisXBottom(null);
                        data.setAxisYLeft(null);
                    }
                    //data.setBaseValue(Float.POSITIVE_INFINITY);
                    //data.setValueLabelTextSize(16);
                    chart.setLineChartData(data);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }


}
