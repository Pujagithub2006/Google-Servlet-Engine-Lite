// mini MVC
package org.gse_lite.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gse_lite.annotation.Controller;
import org.gse_lite.annotation.GetMapping;
import org.gse_lite.scanner.ClassScanner;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DispatcherServlet extends HttpServlet {
    private final Map<String, HandlerMethod> routeMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        // finding Controllers and mapping it against the urls
        System.out.println("DispatcherServlet INIT");

        Set<Class<?>> controllerClasses;
        try {
            controllerClasses = ClassScanner.scan("org.gse_lite.controller");
        } catch (Exception e) {
            throw new ServletException(
                    "Failed to scan controller classes", e
            );
        }

        for (Class<?> controllerClass : controllerClasses) {
            if(!controllerClass.isAnnotationPresent(Controller.class)) continue;

            try {
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

                for (Method method : controllerClass.getDeclaredMethods()) {
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);

                    if (getMapping == null) continue;

                    String urlPath = getMapping.value();

                    HandlerMethod handlerMethod = new HandlerMethod(controllerInstance, method);

                    routeMap.put(urlPath, handlerMethod);
                }
            }
            catch (ReflectiveOperationException e) {
                throw new ServletException(
                        "Failed to initialize controller: " + controllerClass.getName(), e
                );

            }
        }

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DispatcherServlet DOGET");

        String urlPath = request.getRequestURI()
                .substring(request.getContextPath().length());

        HandlerMethod handlerMethod = routeMap.get(urlPath);

        if (handlerMethod == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            Object result = handlerMethod.invokeReflection(request, response);

            if (result != null) {

                String viewName = result.toString();

                request.getRequestDispatcher(
                        "/WEB-INF/views/" + viewName + ".jsp"
                ).forward(request, response);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new ServletException(
                    "Failed to invoke handler for the path: " + urlPath, e
            );

        }
    }
}
