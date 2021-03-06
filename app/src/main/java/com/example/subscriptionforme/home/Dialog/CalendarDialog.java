package com.example.subscriptionforme.home.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.subscriptionforme.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;

public class CalendarDialog extends Dialog {

    private Context context;
    private MaterialCalendarView materialCalendarView;
    private String payDate;
    private Button okaybutton;
    private TextView payDateTextView;

    public CalendarDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CalendarDialog(@NonNull final Context context, TextView textView) {
        super(context);

        this.context = context;
        this.payDateTextView = textView;
        payDate = "";

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setGravity(Gravity.BOTTOM);

        setContentView(R.layout.dialog_calendar);

        //??????????????? ????????????
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480, context.getResources().getDisplayMetrics());
        layoutParams.windowAnimations = R.style.CalendarDialogAnimation;
        getWindow().setAttributes(layoutParams);

        materialCalendarView = findViewById(R.id.calender_dialog);
        materialCalendarView.setWeekDayTextAppearance(R.color.mainColor);
        setCalender();

        okaybutton = findViewById(R.id.button_ok_calender_dialog);
        okaybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!payDate.isEmpty()) {
                    payDateTextView.setText(payDate);
                    dismiss();
                }
                else
                    Toast.makeText(context,"?????? ???????????? ???????????????.",Toast.LENGTH_SHORT).show();
            }
        });

        show();

    }

    //?????? ?????? ??????
    public void setCalender() {

        materialCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(getContext(), R.color.red)));
            }
        });

        materialCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(getContext(), R.color.blue)));
            }
        });

        materialCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                CalendarDay date = CalendarDay.today();
                return date != null && day.equals(date);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new StyleSpan(Typeface.BOLD));
                view.addSpan(new RelativeSizeSpan(1.5f));
            }
        });

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                payDate = date.getYear() + "??? " + (date.getMonth()+1) + "??? " + date.getDay() + "???";
            }
        });
    }

    /**????????? ????????? ????????? get?????? ?????????
     * ?????? ????????? 2020??? 8??? 11??? -> ?????? ??? + ?????????????????? ???
     * ?????? ???????????? ?????? ?????? ?????? ?????? ????????? ???????????????.
     */
    public String getPayDate(String subscriptionPayDateString) {

        //?????? ????????? ??????
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy??? M??? d???");

        SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy???");
        SimpleDateFormat monthDateFormat = new SimpleDateFormat("M???");
        SimpleDateFormat dayDateFormat = new SimpleDateFormat("d???");

        try {
            Date nowDate = dateFormat.parse(dateFormat.format(date));
            Date subscriptionPayDate = dateFormat.parse(subscriptionPayDateString);

            //?????? ???????????? ???????????? ?????? ???????????? ?????????????????? ?????? ???
            if (nowDate.after(subscriptionPayDate)) {

                String paymentYearString = yearDateFormat.format(date);
                String paymentMonthString = monthDateFormat.format(date);
                String paymentDayString = dayDateFormat.format(subscriptionPayDate);

                if (paymentDayString.equals("31???"))
                    paymentDayString = "30???";

                return paymentYearString + " " + paymentMonthString + " " + paymentDayString;
            }

            String paymentYearString = yearDateFormat.format(subscriptionPayDate);
            String paymentMonthString = monthDateFormat.format(subscriptionPayDate);
            String paymentDayString = dayDateFormat.format(subscriptionPayDate);
            return paymentYearString + " " + paymentMonthString + " " + paymentDayString;

        } catch (Exception e) {
        }
        return "";
    }
}
