package org.gse_lite.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerMethod {

    private final Object controller;
    private final Method method;

    public HandlerMethod(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Object invokeReflection(
            HttpServletRequest request,
            HttpServletResponse response
    )
            throws InvocationTargetException, IllegalAccessException {

        return method.invoke(
                controller,
                request,
                response
        );
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }


}
