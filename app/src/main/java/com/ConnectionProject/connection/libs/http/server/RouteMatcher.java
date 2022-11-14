package com.ConnectionProject.connection.libs.http.server;

public interface RouteMatcher {
    AsyncHttpServerRouter.RouteMatch route(String method, String path);
}