package com.about.mantle.spring.interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PageGoneHandler {
    public boolean is410(HttpServletRequest request);
    public void handle410(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException;
}
