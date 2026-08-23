package org.gse_lite.controller;

import org.gse_lite.annotation.Controller;
import org.gse_lite.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.ServletException;
import java.io.IOException;

import java.io.PrintWriter;

@Controller
public class FeedController {

    @GetMapping("/feed")
    public void showFeed(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter respWriter = response.getWriter();
        respWriter.println("Dispatching feed successful");
    }

}
