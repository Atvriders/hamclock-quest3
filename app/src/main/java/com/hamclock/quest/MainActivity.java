package com.hamclock.quest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.GeolocationPermissions;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String URL = "https://hamclock-reborn.org/";
    private WebView webView;

    // Cyberpunk spatial splash screen with animated starfield + grid
    private static final String SPLASH_HTML =
        "<html><head><style>" +
        "* { margin:0; padding:0; box-sizing:border-box; }" +
        "body { background:#000; overflow:hidden; height:100vh; width:100vw; font-family:'Courier New',monospace; }" +
        "canvas { position:fixed; top:0; left:0; z-index:0; }" +
        ".overlay { position:fixed; top:0; left:0; width:100%; height:100%; z-index:1; " +
        "  display:flex; flex-direction:column; align-items:center; justify-content:center; " +
        "  background: radial-gradient(ellipse at center, rgba(0,20,40,0.3) 0%, rgba(0,0,0,0.8) 70%); }" +
        ".grid { position:fixed; top:0; left:0; width:100%; height:100%; z-index:0; " +
        "  background-image: linear-gradient(rgba(0,255,255,0.03) 1px, transparent 1px), " +
        "    linear-gradient(90deg, rgba(0,255,255,0.03) 1px, transparent 1px); " +
        "  background-size: 40px 40px; " +
        "  animation: gridScroll 20s linear infinite; }" +
        "@keyframes gridScroll { 0%{transform:perspective(500px) rotateX(60deg) translateY(0)} " +
        "  100%{transform:perspective(500px) rotateX(60deg) translateY(40px)} }" +
        ".title { font-size:48px; font-weight:900; letter-spacing:12px; " +
        "  background: linear-gradient(90deg, #00ffff, #ff00ff, #00ffff); " +
        "  -webkit-background-clip:text; -webkit-text-fill-color:transparent; " +
        "  background-size:200% auto; animation: shimmer 3s linear infinite; " +
        "  text-shadow: 0 0 40px rgba(0,255,255,0.3); filter: drop-shadow(0 0 20px rgba(0,255,255,0.5)); }" +
        "@keyframes shimmer { 0%{background-position:0% center} 100%{background-position:200% center} }" +
        ".subtitle { font-size:14px; letter-spacing:8px; color:#00ffff; margin-top:12px; " +
        "  opacity:0.7; text-transform:uppercase; text-shadow: 0 0 10px rgba(0,255,255,0.5); }" +
        ".loader { margin-top:40px; display:flex; gap:8px; }" +
        ".loader span { width:8px; height:8px; border-radius:50%; background:#00ffff; " +
        "  animation: pulse 1.4s ease-in-out infinite; box-shadow: 0 0 10px #00ffff; }" +
        ".loader span:nth-child(2) { animation-delay:0.2s; }" +
        ".loader span:nth-child(3) { animation-delay:0.4s; }" +
        ".loader span:nth-child(4) { animation-delay:0.6s; }" +
        ".loader span:nth-child(5) { animation-delay:0.8s; }" +
        "@keyframes pulse { 0%,80%,100%{transform:scale(0.4);opacity:0.3} 40%{transform:scale(1);opacity:1} }" +
        ".status { margin-top:20px; font-size:11px; letter-spacing:3px; color:#ff00ff; opacity:0.6; " +
        "  animation: blink 2s ease-in-out infinite; }" +
        "@keyframes blink { 0%,100%{opacity:0.6} 50%{opacity:0.2} }" +
        ".hud-corner { position:fixed; font-size:9px; color:rgba(0,255,255,0.15); letter-spacing:2px; z-index:2; }" +
        ".hud-tl { top:20px; left:20px; } .hud-tr { top:20px; right:20px; }" +
        ".hud-bl { bottom:20px; left:20px; } .hud-br { bottom:20px; right:20px; }" +
        ".scanline { position:fixed; top:0; left:0; width:100%; height:100%; z-index:3; pointer-events:none; " +
        "  background: repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,0,0,0.05) 2px, rgba(0,0,0,0.05) 4px); }" +
        "</style></head><body>" +
        "<canvas id='stars'></canvas>" +
        "<div class='grid'></div>" +
        "<div class='scanline'></div>" +
        "<div class='hud-corner hud-tl'>SYS://HAMCLOCK_VR</div>" +
        "<div class='hud-corner hud-tr'>QUEST_3 SPATIAL</div>" +
        "<div class='hud-corner hud-bl'>RF_SUBSYSTEM: INIT</div>" +
        "<div class='hud-corner hud-br'>v1.0 // CYBERDECK</div>" +
        "<div class='overlay'>" +
        "  <div class='title'>HAMCLOCK</div>" +
        "  <div class='subtitle'>R E B O R N &nbsp; // &nbsp; V R</div>" +
        "  <div class='loader'><span></span><span></span><span></span><span></span><span></span></div>" +
        "  <div class='status'>ESTABLISHING UPLINK...</div>" +
        "</div>" +
        "<script>" +
        "const c=document.getElementById('stars'),x=c.getContext('2d');" +
        "c.width=window.innerWidth;c.height=window.innerHeight;" +
        "const stars=[];for(let i=0;i<300;i++)stars.push({x:Math.random()*c.width," +
        "y:Math.random()*c.height,r:Math.random()*1.5+0.5,s:Math.random()*0.5+0.1," +
        "a:Math.random()});" +
        "function draw(){x.fillStyle='rgba(0,0,10,0.15)';x.fillRect(0,0,c.width,c.height);" +
        "stars.forEach(s=>{s.a+=0.02;const b=0.3+Math.sin(s.a)*0.7;" +
        "x.beginPath();x.arc(s.x,s.y,s.r,0,Math.PI*2);" +
        "x.fillStyle=`rgba(0,${150+Math.floor(b*105)},${200+Math.floor(b*55)},${b})`;" +
        "x.fill();x.y-=s.s;if(s.y<0){s.y=c.height;s.x=Math.random()*c.width;}});" +
        "requestAnimationFrame(draw);}draw();" +
        "</script></body></html>";

    // Cyberpunk offline error page
    private static final String OFFLINE_HTML =
        "<html><head><style>" +
        "* { margin:0; padding:0; box-sizing:border-box; }" +
        "body { background:#000; height:100vh; display:flex; align-items:center; justify-content:center; " +
        "  font-family:'Courier New',monospace; overflow:hidden; " +
        "  background-image: linear-gradient(rgba(255,0,80,0.03) 1px, transparent 1px), " +
        "    linear-gradient(90deg, rgba(255,0,80,0.03) 1px, transparent 1px); " +
        "  background-size: 40px 40px; }" +
        ".box { text-align:center; padding:60px; border:1px solid rgba(255,0,80,0.3); " +
        "  background:rgba(10,0,20,0.8); box-shadow: 0 0 40px rgba(255,0,80,0.1), inset 0 0 40px rgba(255,0,80,0.05); }" +
        "h1 { font-size:36px; letter-spacing:8px; " +
        "  background: linear-gradient(90deg, #ff0050, #ff00ff); " +
        "  -webkit-background-clip:text; -webkit-text-fill-color:transparent; }" +
        ".err { color:#ff0050; font-size:14px; letter-spacing:4px; margin-top:16px; " +
        "  text-shadow: 0 0 10px rgba(255,0,80,0.5); }" +
        ".hint { color:#666; font-size:12px; letter-spacing:2px; margin-top:12px; }" +
        ".glitch { animation: glitch 3s infinite; }" +
        "@keyframes glitch { 0%,95%{opacity:1} 96%{opacity:0.8;transform:translateX(-2px)} " +
        "  97%{opacity:0.6;transform:translateX(3px)} 98%{opacity:0.9;transform:translateX(-1px)} 100%{opacity:1;transform:none} }" +
        ".scanline { position:fixed; top:0; left:0; width:100%; height:100%; pointer-events:none; " +
        "  background: repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,0,0,0.05) 2px, rgba(0,0,0,0.05) 4px); }" +
        "</style></head><body>" +
        "<div class='scanline'></div>" +
        "<div class='box glitch'>" +
        "  <h1>HAMCLOCK</h1>" +
        "  <div class='err'>// UPLINK FAILED</div>" +
        "  <div class='hint'>NO NETWORK CONNECTION DETECTED</div>" +
        "  <div class='hint' style='margin-top:8px;color:#444'>CONNECT TO WIFI AND RESTART</div>" +
        "</div></body></html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen immersive mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Hide system UI
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        // Create WebView
        webView = new WebView(this);
        webView.setBackgroundColor(0xFF000000);
        setContentView(webView);

        // Configure WebView
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setUserAgentString(settings.getUserAgentString() + " HamClockQuest/1.0");

        // Show cyberpunk splash while loading
        webView.loadDataWithBaseURL(null, SPLASH_HTML, "text/html", "UTF-8", null);

        // Handle navigation inside WebView
        webView.setWebViewClient(new WebViewClient() {
            private boolean splashShown = true;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false; // Load everything in WebView
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Once the real site loads, splash is gone
                if (url.contains("hamclock-reborn.org") || url.contains("hamclock.waterburp.com")) {
                    splashShown = false;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (failingUrl.contains("hamclock-reborn.org") || failingUrl.contains("hamclock.waterburp.com")) {
                    view.loadDataWithBaseURL(null, OFFLINE_HTML, "text/html", "UTF-8", null);
                }
            }
        });

        // Chrome client for fullscreen support
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        // Delay loading the real URL to show splash briefly
        webView.postDelayed(() -> {
            webView.loadUrl(URL);
        }, 2500);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
        // Re-hide system UI
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    protected void onPause() {
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
