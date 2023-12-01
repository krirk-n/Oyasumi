package project.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class DetailActivity extends AppCompatActivity {
    private long[] sleepTimeS = new long[5];
    private long[] sleepTimeM = new long[5];
    private long[] sleepTimeH = new long[5];
    private String[] date = new String[5];;
    private String[] startTime = new String[5];
    private String[] endTime = new String[5];
    private String FILENAME = "sleepData.txt";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*Intent intent = getIntent();
        date = intent.getStringExtra("date");
        if(date == null) {
            date = "11/30/2023";
        }
        sleepTimeH = intent.getLongExtra("sleepTimeH", 0);
        sleepTimeM = intent.getLongExtra("sleepTimeM", 0);
        sleepTimeS = intent.getLongExtra("sleepTimeS", 0);
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");*/

        for(int i = 0; i < 5; i++) {
            date[i] = "00/00/0000";
        }

        FileInputStream fis = null;
        int i = 0;
        try {
            fis = openFileInput(FILENAME);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);

            System.out.println("Sleep " + new String(buffer));
            StringTokenizer st = new StringTokenizer(new String(buffer), "\0");
            int maxCount = st.countTokens();
            int count = 0;

            while(st.hasMoreTokens()) {
                count = st.countTokens();
                if(count != maxCount && count % 6 == 0) {
                    i++;
                }

                date[i] = st.nextToken();
                sleepTimeH[i] = Long.parseLong(st.nextToken());
                sleepTimeM[i] = Long.parseLong(st.nextToken());
                sleepTimeS[i] = Long.parseLong(st.nextToken());
                startTime[i] = st.nextToken();
                endTime[i] = st.nextToken();
            }

            fis.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BarChart bc = (BarChart) findViewById(R.id.chart);
        this.initBarChart(bc);
        this.setChartData(bc);

        TextView tvSleepTime = (TextView) findViewById(R.id.tvSleepTime2);
        TextView tvSleepQuality = (TextView) findViewById(R.id.tvSleepQuality2);
        TextView tvTimeInBed  = (TextView) findViewById(R.id.tvTimeInBed2);

        tvSleepTime.setText("Sleep time = " + startTime[i] + " - " + endTime[i]);
        tvSleepQuality.setText("Sleep quality = " + (sleepTimeH[i] / 8 * 100) + "%");

        if(sleepTimeH[i] > 0) {
            tvTimeInBed.setText("Time in bed : " + sleepTimeH[i] + "hours " + sleepTimeM[i] + "mins " + sleepTimeS[i] + "secs");
        } else if(sleepTimeM[i] > 0) {
            tvTimeInBed.setText("Time in bed : " + sleepTimeM[i] + "mins " + sleepTimeS[i] + "secs");
        } else {
            tvTimeInBed.setText("Time in bed : " + sleepTimeS[i] + " secs");
        }

        Button btBack = (Button) findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(DetailActivity.this, MainActivity.class);
                mainIntent.addFlags(mainIntent.FLAG_ACTIVITY_CLEAR_TOP | mainIntent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(mainIntent);
            }
        });
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

        List<String> xAxisValues = new ArrayList<>(Arrays.asList(
                date[0].substring(0,5), date[1].substring(0,5), date[2].substring(0,5), date[3].substring(0,5), date[4].substring(0,5)));
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        YAxis leftAxis = barChart.getAxisLeft();
        // 좌측 선 설정 (default = true)
        leftAxis.setDrawAxisLine(false);
        // 좌측 텍스트 컬러 설정
        leftAxis.setTextColor(Color.BLUE);
        leftAxis.setGranularity(3f);

        YAxis rightAxis = barChart.getAxisRight();
        // 우측 선 설정 (default = true)
        rightAxis.setDrawAxisLine(false);
        // 우측 텍스트 컬러 설정
        rightAxis.setTextColor(Color.GREEN);
        rightAxis.setGranularity(3f);

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
        String title = "Date";

        // 임의 데이터
        for (int i = 0; i < 5; i++) {
            //valueList.add(new BarEntry((float)i, i * 100f));
            //valueList.add(new BarEntry((float)i, i * 3f));
            valueList.add(new BarEntry((float)i, sleepTimeH[i]));
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
}