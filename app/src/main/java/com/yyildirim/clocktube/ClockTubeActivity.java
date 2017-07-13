package com.yyildirim.clocktube;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextClock;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

public class ClockTubeActivity extends AppCompatActivity {

    private View decorView;
    private WebView webView;
    private FloatingActionButton hideYouTubeFab;
    private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_clock);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setImmersiveMode();
        initYouTubeWebView();
        initHideYouTubeButton();
        initCalendarView();
        setupDigitalClock();
    }

    private void setupDigitalClock() {
        TextClock digitalClock = (TextClock) findViewById(R.id.digital_clock_view);
        digitalClock.addTextChangedListener(new DigitalClockTextWatcher());
    }

    private void setImmersiveMode() {
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                goImmersive();
            }
        });
    }

    private void setDateToToday() {
        calendarView.clearSelection();
        calendarView.setDateSelected(CalendarDay.today(), true);
        calendarView.setCurrentDate(CalendarDay.today(), true);
    }

    private void initCalendarView() {
        calendarView = (MaterialCalendarView) findViewById(R.id.calendar_view);
        setDateToToday();
        MaterialCalendarView.StateBuilder stateBuilder = calendarView.state().edit();
        stateBuilder.setFirstDayOfWeek(Calendar.MONDAY);
        stateBuilder.commit();
    }

    private void initHideYouTubeButton() {
        hideYouTubeFab = (FloatingActionButton) findViewById(R.id.hide_youtube_fab);
        hideYouTubeFab.setOnClickListener(new FabClickListener());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initYouTubeWebView() {
        webView = ((WebView) findViewById(R.id.youtube_webview));
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        goImmersive();
        setScreenBrightness(0F);
        if (webView != null) {
            if (webView.getUrl() != null && !webView.getUrl().isEmpty()) {
                webView.onResume();
            } else {
                webView.loadUrl("http://m.youtube.com");
            }
        }
    }

    private void setScreenBrightness(float brightness) {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = brightness;
        getWindow().setAttributes(layout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        setScreenBrightness(-1F);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            goImmersive();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    private void goImmersive() {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private class FabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (View.VISIBLE == webView.getVisibility()) {
                webView.setVisibility(View.GONE);
                hideYouTubeFab.setImageResource(R.drawable.chevron_left);
                webView.onPause();
            } else {
                webView.setVisibility(View.VISIBLE);
                hideYouTubeFab.setImageResource(R.drawable.chevron_right);
                webView.onResume();
            }
        }
    }

    private class DigitalClockTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // nop
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // nop
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equals("00:00")) {
                setDateToToday();
            }
        }
    }
}
