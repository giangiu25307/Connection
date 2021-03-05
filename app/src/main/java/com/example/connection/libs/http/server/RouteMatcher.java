package com.example.connection.libs.http.server;

import com.example.connection.libs.http.server.AsyncHttpServerRouter;

public interface RouteMatcher {
    AsyncHttpServerRouter.RouteMatch route(String method, String path);
}