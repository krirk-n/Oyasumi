package project.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private TextView tvSleepTime;
    private TextView tvTimeInBed;
    private long sleepTimeS = 0;
    private long sleepTimeM = 0;
    private long sleepTimeH = 0;
    private String date;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        sleepTimeH = intent.getLongExtra("sleepTimeH", 0);
        sleepTimeM = intent.getLongExtra("sleepTimeM", 0);
        sleepTimeS = intent.getLongExtra("sleepTimeS", 0);
        String startTime = intent.getStringExtra("startTime");
        String endTime = intent.getStringExtra("endTime");

        BarChart bc = (BarChart) findViewById(R.id.chart);
        this.initBarChart(bc);
        this.setChartData(bc);

        tvSleepTime = (TextView) findViewById(R.id.tvSleepTime2);
        tvTimeInBed = (TextView) findViewById(R.id.tvTimeInBed);

        tvSleepTime.setText("Sleep time = " + startTime + " - " + endTime);

        if(sleepTimeH > 0) {
            tvTimeInBed.setText("Sleep time : " + sleepTimeH + "hours " + sleepTimeM + "mins " + sleepTimeS + "secs");
        } else if(sleepTimeM > 0) {
            tvTimeInBed.setText("Sleep time : " + sleepTimeM + "mins " + sleepTimeS + "secs");
        } else {
            tvTimeInBed.setText("Sleep time : " + sleepTimeS + " secs");
        }
    }

    private void initBarChart(BarChart barChart) {
        // 차트 회색 배경 설정 (default = false)
        barChart.setDrawGridBackground(false);
        // 막대 그림자 설정 (default = false)
        barChart.setDrawBarShadow(false);
        // 차트 테두리 설정 (default = false)
        barChart.setDrawBorders(false);

        Description description = new Description();
        // 오른쪽 하단 모서리 설명 레이블 텍스트 표시 (default = false)
        description.setEnabled(false);
        barChart.setDescription(description);

        // X, Y 바의 애니메이션 효과
        barChart.animateY(1000);
        barChart.animateX(1000);

        // 바텀 좌표 값
        XAxis xAxis = barChart.getXAxis();
        // x축 위치 설정
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 그리드 선 수평 거리 설정
        xAxis.setGranularity(1f);
        // x축 텍스트 컬러 설정
        xAxis.setTextColor(Color.RED);
        // x축 선 설정 (default = true)
        xAxis.setDrawAxisLine(false);
        // 격자선 설정 (default = true)
        xAxis.setDrawGridLines(false);
        //xAxis.setValueFormatter();

        YAxis leftAxis = barChart.getAxisLeft();
        // 좌측 선 설정 (default = true)
        leftAxis.setDrawAxisLine(false);
        // 좌측 텍스트 컬러 설정
        leftAxis.setTextColor(Color.BLUE);

        YAxis rightAxis = barChart.getAxisRight();
        // 우측 선 설정 (default = true)
        rightAxis.setDrawAxisLine(false);
        // 우측 텍스트 컬러 설정
        rightAxis.setTextColor(Color.GREEN);

        // 바차트의 타이틀
        Legend legend = barChart.getLegend();
        // 범례 모양 설정 (default = 정사각형)
        legend.setForm(Legend.LegendForm.LINE);
        // 타이틀 텍스트 사이즈 설정
        legend.setTextSize(20f);
        // 타이틀 텍스트 컬러 설정
        legend.setTextColor(Color.BLACK);
        // 범례 위치 설정
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        // 범례 방향 설정
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        // 차트 내부 범례 위치하게 함 (default = false)
        legend.setDrawInside(false);
    }

    private void setChartData(BarChart barChart) {
        // Zoom In / Out 가능 여부 설정
        barChart.setScaleEnabled(false);

        ArrayList<BarEntry> valueList = new ArrayList<BarEntry>();
        String title = "날짜";

        // 임의 데이터
        for (int i = 0; i < 5; i++) {
            valueList.add(new BarEntry((float)i, i * 100f));
        }

        BarDataSet barDataSet = new BarDataSet(valueList, title);
        // 바 색상 설정 (ColorTemplate.LIBERTY_COLORS)
        barDataSet.setColors(
                Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), Color.rgb(136, 180, 187),
                Color.rgb(118, 174, 175), Color.rgb(42, 109, 130));

        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.invalidate();
    }

    private class XAxisCustomFormatter extends ValueFormatter {
        public XAxisCustomFormatter() {
        }

        @Override
        public String getFormattedValue(float value) {
            return super.getFormattedValue(value);
        }
    }
}