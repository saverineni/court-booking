package org.movoto.selenium.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainHandler implements RequestHandler<String, String> {
    static Logger logger = LoggerFactory.getLogger(MainHandler.class);

    public MainHandler() {
    }

    public String handleRequest(String s, Context context) {
        return "Hello world";
    }
}