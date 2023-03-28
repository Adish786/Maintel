package com.about.mantle.web.filter;

import com.about.mantle.model.services.Mantle410Service;
import com.about.mantle.spring.interceptor.PageGoneHandler;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Mantle410Handler implements PageGoneHandler {

    private static final String PAGE_GONE_TEMPLATE = "/410pagegone";
    private Mantle410Service mantle410Service;

    public Mantle410Handler(Mantle410Service mantle410Service){
        this.mantle410Service = mantle410Service;
    }

    @Override
    public boolean is410(HttpServletRequest request) {
        return mantle410Service.is410(request.getRequestURI());
    }

    @Override
    public void handle410(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpStatus.GONE_410);
        request.getRequestDispatcher(PAGE_GONE_TEMPLATE).forward(request, response);
    }
}
