package com.ConnectionProject.connection.libs.http;

import com.ConnectionProject.connection.libs.future.Cancellable;

public class SimpleMiddleware implements AsyncHttpClientMiddleware {
    @Override
    public void onRequest(OnRequestData data) {
    }

    @Override
    public Cancellable getSocket(GetSocketData data) {
        return null;
    }

    @Override
    public boolean exchangeHeaders(OnExchangeHeaderData data) {
        return false;
    }

    @Override
    public void onRequestSent(OnRequestSentData data) {
    }

    @Override
    public void onHeadersReceived(OnHeadersReceivedData data) {
    }

    @Override
    public void onBodyDecoder(OnBodyDecoderData data) {
    }

    @Override
    public AsyncHttpRequest onResponseReady(OnResponseReadyData data) {
        return null;
    }

    @Override
    public void onResponseComplete(OnResponseCompleteData data) {
    }
}
