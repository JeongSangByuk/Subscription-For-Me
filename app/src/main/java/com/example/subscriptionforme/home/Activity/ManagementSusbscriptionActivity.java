package com.example.subscriptionforme.home.Activity;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.subscriptionforme.AppUsedTimeData;
import com.example.subscriptionforme.R;
import com.example.subscriptionforme.home.Data.AllAccountDatabase;
import com.example.subscriptionforme.home.Data.UserDatabase;
import com.example.subscriptionforme.home.Data.UserSubscriptionData;
import com.example.subscriptionforme.home.Dialog.AlarmSettingDialog;
import com.example.subscriptionforme.home.Dialog.CalendarDialog;
import com.example.subscriptionforme.home.Listener.DeleteUserSubscriptionOnClickListener;
import com.example.subscriptionforme.home.ManagementChartDataVO;
import com.example.subscriptionforme.main.AppTimeCheckDialog;

import com.example.subscriptionforme.main.MainActivity;
import com.example.subscriptionforme.recommendation.detail_recommendation.DayAxisValueLineChartFormatter;
import com.example.subscriptionforme.recommendation.detail_recommendation.MyValueFormatter;
import com.example.subscriptionforme.setting.card.AccountVO;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManagementSusbscriptionActivity extends AppCompatActivity {

    private Context context;
    private UserSubscriptionData userSubscriptionData;
    private ImageView logoImage, warnningImage;
    private TextView name, paymentSystem, beginningDate, payDate, updatePayDate, updateAlarmSetting, deleteSubscriptionTextView, review, recommendation, useStatus;

    private EditText priceEditText, deleteUrlEditText, descriptionEditText;
    private View payDateView, alarmSettingView;
    private LinearLayout useStatusLinearLayout;
    private Button updateButton;
    private ArrayList<AccountVO> accountList;
    private SQLiteDatabase allAccountDatabase;
    private int dataCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subscription);

        context = getApplicationContext();

        accountList = new ArrayList<>();
        allAccountDatabase = AllAccountDatabase.getInstance(context).getReadableDatabase();
        dataCount = AllAccountDatabase.getInstance(context).getDataCount(allAccountDatabase);

        Intent intent = getIntent();
        userSubscriptionData = (UserSubscriptionData) intent.getSerializableExtra("userSubscriptionData");

        logoImage = findViewById(R.id.logo_ativity_management_subscription);
        beginningDate = findViewById(R.id.beginnig_date_ativity_management_subscription);
        payDate = findViewById(R.id.pay_date_ativity_management_subscription);
        updatePayDate = findViewById(R.id.update_pay_date_ativity_management_subscription);
        updateAlarmSetting = findViewById(R.id.alarm_setting_ativity_management_subscription);
        priceEditText = findViewById(R.id.price_ativity_management_subscription);
        deleteUrlEditText = findViewById(R.id.delete_url_ativity_management_subscription);
        descriptionEditText = findViewById(R.id.description_ativity_management_subscription);
        payDateView = findViewById(R.id.pay_date_touch_view_ativity_management_subscription);
        alarmSettingView = findViewById(R.id.alarm_setting_touch_view_ativity_management_subscription);
        updateButton = findViewById(R.id.service_update_button_ativity_management_subscription);
        name = findViewById(R.id.name_ativity_management_subscription);
        paymentSystem = findViewById(R.id.payment_system_ativity_management_subscription);
        deleteSubscriptionTextView = findViewById(R.id.delete_subscription_ativity_management_subscription);
        useStatus = findViewById(R.id.use_status_ativity_management_subscription);
        review = findViewById(R.id.review_ativity_management_subscription);
        recommendation = findViewById(R.id.recommendation_ativity_management_subscription);
        warnningImage = findViewById(R.id.warnnig_ativity_management_subscription);
        useStatusLinearLayout = findViewById(R.id.use_status_layout_ativity_management_subscription);


        //?????? ?????? ???
        setUserUseStatusData();

        logoImage.setImageResource(userSubscriptionData.getSubscriptionImageID());
        beginningDate.setText(userSubscriptionData.getBeginningPayDate());
        payDate.setText(userSubscriptionData.getSubscriptionPayDate());
        updatePayDate.setText(userSubscriptionData.getBeginningPayDate());
        updateAlarmSetting.setText(userSubscriptionData.getAlarmSetting());
        priceEditText.setText(userSubscriptionData.getSubscriptionPrice());
        name.setText(userSubscriptionData.getSubscriptionName());
        paymentSystem.setText(userSubscriptionData.getSubscriptionPaymentSystem());

        //deleteURL??? ?????? ????????????
        if (!userSubscriptionData.getSubscriptionDeleteURL().equals("") && userSubscriptionData.getSubscriptionDeleteURL() != null && !userSubscriptionData.getSubscriptionDeleteURL().equals("null"))
            deleteUrlEditText.setText(userSubscriptionData.getSubscriptionDeleteURL());

        //description??? ?????? ????????????
        if (!userSubscriptionData.getSubscriptionDescription().equals("") && userSubscriptionData.getSubscriptionDescription() != null
                && !userSubscriptionData.getSubscriptionDescription().equals("?????? ???????????? ?????? & ????????? ????????????."))
            descriptionEditText.setText(userSubscriptionData.getSubscriptionDescription());

        payDateView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                CalendarDialog calendarDialog = new CalendarDialog(ManagementSusbscriptionActivity.this, updatePayDate);
            }
        });

        alarmSettingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmSettingDialog alarmSettingDialog = new AlarmSettingDialog(ManagementSusbscriptionActivity.this, updateAlarmSetting);
            }
        });

        deleteSubscriptionTextView.setOnClickListener(new DeleteUserSubscriptionOnClickListener(ManagementSusbscriptionActivity.this, userSubscriptionData, this));

    }

    public void updateSubscriptionData(View view) {

        String isAlarmOn, description, deleteURL;

        if (priceEditText.getText().toString().equals("")) {
            Toast.makeText(context, "????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (updateAlarmSetting.getText().toString()) {

            case "1??? ???":
            case "3??? ???":
            case "????????? ???":
                isAlarmOn = "true";
                break;

            case "?????? ??????":
                isAlarmOn = "false";
                break;

            default:
                isAlarmOn = "false";
                break;
        }


        //?????? ??????&?????? ?????? ??????.
        if (descriptionEditText.getText().toString() == null || descriptionEditText.getText().toString().equals("")) {
            description = "?????? ???????????? ?????? & ????????? ????????????.";
        } else
            description = descriptionEditText.getText().toString();

        //url ??????
        if (deleteUrlEditText.getText().toString() == null || deleteUrlEditText.getText().toString().equals("")) {
            deleteURL = "null";
        } else
            deleteURL = deleteUrlEditText.getText().toString();

        CalendarDialog calendarDialog = new CalendarDialog(context);
        String paymentDay = calendarDialog.getPayDate(updatePayDate.getText().toString());
        String price = moneyFormat(Integer.parseInt(priceEditText.getText().toString().replaceAll(",", "")));

        UserDatabase.getInstance(context).updateSubscruption(UserDatabase.getInstance(context).getWritableDatabase(), userSubscriptionData.getRegisterNumber(),
                price, updatePayDate.getText().toString(), paymentDay, updateAlarmSetting.getText().toString(), isAlarmOn, description, deleteURL);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    //??????????????? ???????????? ?????? ?????? set
    public void setUserUseStatusData() {

        String targetDES = "null", targetDES2 = "null";
        recommendation.setClickable(false);

        //???????????? 0??? ??????, ??? ?????? ????????? ????????? ??????.
        if (dataCount == 0) {

            useStatus.setText("-");
            recommendation.setText("-");
            recommendation.setTextColor(Color.BLACK);
            review.setText("????????? ???????????? ???????????????. ?????? ?????? ?????? ???, ?????? ???????????? ?????? ?????? For Me ?????? ????????? ??????????????????!");
            warnningImage.setVisibility(View.INVISIBLE);

            //?????? ?????????
            LinearLayout chartLayout = findViewById(R.id.layout_chart_acrivity_management_subscription);
            chartLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));

            return;
        }

        switch (userSubscriptionData.getSubscriptionNumberID()) {

            case "0":
                targetDES = "?????????????????????";
                judgeRecommendWithPrice(setArrayData(targetDES, targetDES2), 100000);

                break;

            case "1":
                targetDES = "G??????";
                targetDES2 = "??????";
                judgeRecommendWithPrice(setArrayData(targetDES, targetDES2), 50000);
                break;

            case "2":
                targetDES = "??????";
                judgeRecommendWithPrice(setArrayData(targetDES, targetDES2), 50000);

                break;

            case "3":
                targetDES = "?????????";
                judgeRecommendWithPrice(setArrayData(targetDES, targetDES2), 50000);
                break;

            case "4":
                noInformation();
                break;

            case "5":
            case "6":
                noInformation();
                break;

            case "7":
            case "8":
            case "9":
                targetDES = "??????";
                judgeRecommendWithPrice(setArrayData(targetDES, targetDES2), 10000);
                break;

            case "10":
            case "11":
            case "12":
                noInformation();
                break;

            case "13":
            case "14":
            case "15":
                noInformation();
                break;

            case "16":
            case "17":
            case "18":
                judceRecommnedWithTime("netflix");
                break;

            case "19":
                judceRecommnedWithTime("youtube");
                break;

            case "null":
                noInformation();

                break;
        }

    }

    public void judceRecommnedWithTime(String appName) {

        //?????? ?????????
        LinearLayout chartLayout = findViewById(R.id.layout_chart_acrivity_management_subscription);
        chartLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));

        //?????? ??????
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        //?????? ????????? false
        if (!granted) {
            useStatus.setText("-");
            recommendation.setText(">>>");
            recommendation.setClickable(true);
            recommendation.setTextColor(Color.BLUE);
            review.setText("?????? ???????????? ????????? ?????? ???????????? ?????? ?????? ????????? ???????????????. ?????? ???????????? ???????????? ?????? ?????? ????????? ?????????????????????.");
            warnningImage.setVisibility(View.INVISIBLE);

            recommendation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppTimeCheckDialog appTimeCheckDialog = new AppTimeCheckDialog(ManagementSusbscriptionActivity.this,true);
                }
            });

        } else {
            //?????? ?????? ??? ??????.

            AppUsedTimeData appUsedTimeData = new AppUsedTimeData();

            Long youtubeUseTime = Long.valueOf(0);
            switch (appName){

                case "netflix":
                    youtubeUseTime = appUsedTimeData.getAppUsedTime(context, "netflix") / (30 * 1000);
                    break;

                case "youtube":
                    youtubeUseTime = appUsedTimeData.getAppUsedTime(context, "youtube") / (30 * 1000);
                    break;

            }


            //300????????? ?????????
            if (youtubeUseTime > 300) {
                useStatus.setText(youtubeUseTime + " ???");
                String recommendationMaintainString = "?????? ???????????? ????????? ??????????????? ???????????? ?????????! ????????? ???????????? ?????? ??????????????????.";
                recommendation.setText("?????? ??????");
                recommendation.setTextColor(Color.GREEN);
                review.setText(recommendationMaintainString);
                warnningImage.setVisibility(View.INVISIBLE);
            } else {
                useStatus.setText(youtubeUseTime + " ???");
                String recommendationCancelString = "?????? 2??? ????????? ?????? ????????? ??????, ????????? ?????? ????????????. ????????? ??????????????????.";
                recommendation.setText("?????? ??????");
                recommendation.setTextColor(Color.RED);
                review.setText(recommendationCancelString);
                warnningImage.setVisibility(View.VISIBLE);
            }
        }
    }

    public int setArrayData(String targetDES, String targetDES2) {
        int useStatusData = 0;
        ArrayList<ManagementChartDataVO> managementChartDataList = new ArrayList<>();

        //?????? ????????? ????????????.
        for (int index = 0; index < dataCount; index++) {
            accountList.add(AllAccountDatabase.getInstance(context).getAccountdata(allAccountDatabase, index));
        }

        //?????? ?????? ?????????.
        for (int index = 0; index < dataCount; index++) {
            accountList.add(AllAccountDatabase.getInstance(context).getAccountdata(allAccountDatabase, index));

            if (accountList.get(index).getResAccountDesc3().contains(targetDES) || accountList.get(index).getResAccountDesc3().contains(targetDES2)) {
                useStatusData += Integer.parseInt(accountList.get(index).getResAccountOut());
                managementChartDataList.add(new ManagementChartDataVO(accountList.get(index).getResAccountTrDate(),accountList.get(index).getResAccountOut()));
            }
        }
        drawLineChart(managementChartDataList);

        if(useStatusData != 0){
            TextView noDataTextView = findViewById(R.id.no_data_textview_activity_manage_subscription);
            noDataTextView.setVisibility(View.INVISIBLE);
        }

        return useStatusData;
    }

    public void noInformation() {
        useStatus.setText("-");
        recommendation.setText("-");
        recommendation.setTextColor(Color.BLACK);
        review.setText("?????? ????????? ????????????.");
        warnningImage.setVisibility(View.INVISIBLE);

        LinearLayout chartLayout = findViewById(R.id.layout_chart_acrivity_management_subscription);
        chartLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));

    }

    public void judgeRecommendWithPrice(int useStatusData, int price) {
        if (useStatusData > price)
            recommendMaintaination(useStatusData);
        else
            recommendCancel(useStatusData);
    }

    public void recommendMaintaination(int useStatusData) {
        useStatus.setText(moneyFormat(useStatusData) + " ???");
        String recommendationMaintainString = "?????? ???????????? ????????? ??????????????? ???????????? ?????????! ????????? ???????????? ?????? ??????????????????.";
        recommendation.setText("?????? ??????");
        recommendation.setTextColor(Color.GREEN);
        review.setText(recommendationMaintainString);
        warnningImage.setVisibility(View.INVISIBLE);
    }

    public void recommendCancel(int useStatusData) {
        useStatus.setText(moneyFormat(useStatusData) + " ???");
        String recommendationCancelString = "?????? ??? ???????????? ???????????????, ?????? ?????? ????????? ??? ?????? ??? ?????? ???????????????. ????????? ??????????????????.";
        recommendation.setText("?????? ??????");
        recommendation.setTextColor(Color.RED);
        review.setText(recommendationCancelString);
        warnningImage.setVisibility(View.VISIBLE);
    }

    public String moneyFormat(int intputMoney) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        return decimalFormat.format(intputMoney);
    }


    @SuppressLint("ResourceType")
    public void drawLineChart(ArrayList<ManagementChartDataVO> managementChartDataList){
        LineChart lineChart = (LineChart) findViewById(R.id.linechart_activity_manage_subscription);

        //?????? ?????????
        lineChart.setPinchZoom(true);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);

        List<Entry> entry_chart = new ArrayList<>();

        //ValueFormatter xAxisFormatter = new DayAxisValueLineChartFormatter(lineChart);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextColor(Color.parseColor("#000000"));
        xAxis.setGranularity(1f);

        //remove horizontal lines
        AxisBase axisBase = lineChart.getAxisLeft();
        axisBase.setDrawGridLines(false);

        YAxis yAxisL = lineChart.getAxisLeft();
        YAxis yAxisR = lineChart.getAxisRight();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(31);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(true);
        yAxisL.setDrawAxisLine(false);
        yAxisL.setDrawLabels(false);
        yAxisL.setDrawZeroLine(true);
        yAxisR.setDrawGridLines(false);
        yAxisR.setDrawAxisLine(false);
        yAxisR.setDrawLabels(false);

        //????????? ?????? ??????!
        for(int i=0;i<managementChartDataList.size();i++){
            entry_chart.add(new Entry(Integer.parseInt(managementChartDataList.get(i).getPayDate().substring(6)),
                    Integer.parseInt(managementChartDataList.get(i).getPrice().replace(",",""))));
        }

        LineData lineData = new LineData();

        LineDataSet lineDataSet = new LineDataSet(entry_chart, "????????? ?????? ?????? ??????");
        lineDataSet.setColors(new int[] {Color.parseColor(getString(R.color.mainColor))});
        lineData.addDataSet(lineDataSet);
        lineDataSet.setValueFormatter(new MyValueFormatter());
        lineDataSet.setValueTextColor(Color.parseColor("#000000"));
        lineDataSet.setValueTextSize(10);
        lineDataSet.setCircleColor(Color.parseColor(getString(R.color.mainColor)));
        lineDataSet.setLineWidth(1.5f);
        lineChart.setData(lineData);
        lineChart.setDescription(null);
        lineChart.setTouchEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        lineChart.setBackgroundColor(Color.parseColor("#00000000"));
        lineChart.invalidate();
    }

    public void backButton(View view) {
        onBackPressed();
    }
}
