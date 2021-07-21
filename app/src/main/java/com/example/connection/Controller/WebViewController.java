package com.example.connection.Controller;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebViewAssetLoader;

public class WebViewController {
   // WebView webView = (WebView) findViewById(R.id.webview);
    WebView webView;
    WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this.webView.getContext()))
            .build();
    public void SetWebView(String id) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            @RequiresApi(21)
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest
                    request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        WebSettings webViewSettings = webView.getSettings();
        webViewSettings.setAllowFileAccessFromFileURLs(false);
        webViewSettings.setAllowUniversalAccessFromFileURLs(false);
        webViewSettings.setAllowFileAccess(false);
        webViewSettings.setAllowContentAccess(false);
        // Assets are hosted under http(s)://appassets.androidplatform.net/assets/... .
        // If the application's assets are in the "main/assets" folder this will read the file
        // from "main/assets/www/index.html" and load it as if it were hosted on:
        // https://appassets.androidplatform.net/assets/www/index.html
        webView.loadUrl("https://appassets.androidplatform.net/assets/www/"+id+".html");
    }
}

