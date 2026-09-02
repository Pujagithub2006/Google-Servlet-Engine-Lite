package org.gse_lite.controller;

import org.gse_lite.annotation.Controller;
import org.gse_lite.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.ServletException;
import org.gse_lite.service.PostService;

import java.io.IOException;

@Controller
public class FeedController {

    @GetMapping("/feed")
    public String showFeed(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PostService service = new PostService();

        request.setAttribute(
                "posts",
                service.getPaginated(1,10)
        );

        return "feed"; // ViewResolver config - /WEB-INF/views/feed.jsp
    }

}
