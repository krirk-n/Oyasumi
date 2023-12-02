package project.sleep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private EditText etDate;
    private TextView tvSleepTime;
    private TextView tvSleepQuality;
    private TextView tvTimeInBed;
    private Button btStart;
    private boolean startFlag = false;
    private long startTime = 0;
    private long endTime = 0;
    private long sleepTimeS = 0;
    private long sleepTimeM = 0;
    private long sleepTimeH = 0;
    private Date startDate;
    private Date endDate;
    private SimpleDateFormat dateFormat;
    private String FILENAME = "sleepData.txt";
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormat = new SimpleDateFormat("hh:mm a", new Locale("en", "US"));

        Button btNext = (Button) findViewById(R.id.buttonNext);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(MainActivity.this, DetailActivity.class);

                date = etDate.getText().toString();
                if(date.equals("")) {
                    new AlertDialog.Builder(MainActivity.this).
                            setTitle("Error").
                            setMessage("Please input the date!").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   dialogInterface.dismiss();
                                }
                            }).
                            create().
                            show();
                    return;
                }

                if(startFlag) {
                    btStart.performClick();

                    nextIntent.putExtra("date", date);
                    nextIntent.putExtra("sleepTimeH", sleepTimeH);
                    nextIntent.putExtra("sleepTimeM", sleepTimeM);
                    nextIntent.putExtra("sleepTimeS", sleepTimeS);
                    nextIntent.putExtra("startTime", dateFormat.format(startDate));
                    nextIntent.putExtra("endTime", dateFormat.format(endDate));
                } else if(startDate != null && endDate !=null) {
                    nextIntent.putExtra("date", date);
                    nextIntent.putExtra("sleepTimeH", sleepTimeH);
                    nextIntent.putExtra("sleepTimeM", sleepTimeM);
                    nextIntent.putExtra("sleepTimeS", sleepTimeS);
                    nextIntent.putExtra("startTime", dateFormat.format(startDate));
                    nextIntent.putExtra("endTime", dateFormat.format(endDate));
                }

                FileOutputStream fos = null;
                try {
                    if(startDate != null && endDate !=null) {
                        fos = openFileOutput(FILENAME, Context.MODE_PRIVATE | Context.MODE_APPEND);
                        //fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                        String writeData = date + "\0" + sleepTimeH + "\0" + sleepTimeM + "\0" + sleepTimeS + "\0" +
                                dateFormat.format(startDate) + "\0" + dateFormat.format(endDate) + "\0";
                        fos.write(writeData.getBytes());
                        fos.close();
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                FileInputStream fis = null;
                try {
                    fis = openFileInput(FILENAME);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);

                    StringTokenizer st = new StringTokenizer(new String(buffer), "\0");
                    int count = st.countTokens();

                    if(count > 30) {
                        for(int i = 0; i < 6; i++) {
                            st.nextToken();
                        }
                    }

                    try {
                        if(startDate != null && endDate !=null) {
                            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);

                            String writeData = "";
                            while(st.hasMoreTokens()) {
                                writeData += st.nextToken() + "\0";
                            }
                            fos.write(writeData.getBytes());
                            fos.close();
                        }
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    fis.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                startActivity(nextIntent);
            }
        });

        etDate = (EditText) findViewById(R.id.editTextDate);
        etDate.addTextChangedListener(new TextWatcher() {
            private String current = "";
            //private String ddmmyyyy = "DDMMYYYY";
            private String mmddyyyy = "MMDDYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!charSequence.toString().equals(current)) {
                    String clean = charSequence.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + mmddyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        //int day  = Integer.parseInt(clean.substring(0,2));
                        int day  = Integer.parseInt(clean.substring(2,4));
                        //int mon  = Integer.parseInt(clean.substring(2,4));
                        int mon  = Integer.parseInt(clean.substring(0,2));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        //clean = String.format("%02d%02d%02d",day, mon, year);
                        clean = String.format("%02d%02d%02d",mon, day, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    etDate.setText(current);
                    etDate.setSelection(sel < current.length() ? sel : current.length());
                    etDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        tvSleepTime = (TextView) findViewById(R.id.tvSleepTime);
        tvSleepQuality = (TextView) findViewById(R.id.tvSleepQuality);
        tvTimeInBed = (TextView) findViewById(R.id.tvTimeInBed);

        btStart = (Button) findViewById(R.id.startButton);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!startFlag) {
                    startFlag = true;
                    btStart.setText("Stop Sleeping...");

                    startTime = System.currentTimeMillis();
                    startDate = new Date(startTime);
                } else if(startFlag) {
                    startFlag = false;
                    btStart.setText("Start Sleeping...");

                    endTime = System.currentTimeMillis();
                    endDate = new Date(endTime);

                    sleepTimeS = (endTime - startTime) / 1000;

                    if(sleepTimeS < 60) {
                        //tvSleepTime.setText("Sleep time : " + sleepTimeS + " secs");
                    } else if(sleepTimeS < 60 * 60) {
                        sleepTimeM = sleepTimeS / 60;
                        sleepTimeS = sleepTimeS % 60;
                        //tvSleepTime.setText("Sleep time : " + sleepTimeM + "mins " + sleepTimeS + "secs");
                    }  else {
                        sleepTimeH = sleepTimeS / (60 * 60);
                        sleepTimeM = sleepTimeS % (60 * 60) / 60;
                        sleepTimeS = (sleepTimeS % (60 * 60)) % 60;
                       // tvSleepTime.setText("Sleep time : " + sleepTimeH + "hours " + sleepTimeM + "mins " + sleepTimeS + "secs");
                    }

                    tvSleepTime.setText("Sleep time = " + dateFormat.format(startDate) + " - " + dateFormat.format(endDate));

                    if(sleepTimeH > 0) {
                        tvTimeInBed.setText("Time in bed : " + sleepTimeH + "hours " + sleepTimeM + "mins " + sleepTimeS + "secs");
                    } else if(sleepTimeM > 0) {
                        tvTimeInBed.setText("Time in bed : " + sleepTimeM + "mins " + sleepTimeS + "secs");
                    } else {
                        tvTimeInBed.setText("Time in bed : " + sleepTimeS + " secs");
                    }

                    long sleepTime = sleepTimeS + (sleepTimeM * 60) + (sleepTimeH * 60 * 60);

                    tvSleepQuality.setText("Sleep quality = " + String.format("%3.3f", (sleepTime * 100.000f / (8.000f * 60.000f * 60.000f))) + "%");
                }
            }
        });
    }
}