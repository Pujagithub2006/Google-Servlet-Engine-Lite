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

        int page = 1;
        String pageParam = request.getParameter("page");

        if(pageParam != null) {
            try{
                page = Integer.parseInt(pageParam);
            }
            catch (NumberFormatException ignored){
                page = 1;
            }
        }

        request.setAttribute(
                "posts",
                service.getPaginated(page, 10)
        );

        boolean ajaxRequest =
                "XMLHttpRequest".equals(
                    request.getHeader("X-Requested-With")
                );

        if(ajaxRequest) return "partials/post_item";
        return "feed";

    }

}
