package com.hb.qx;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import in.srain.cube.request.FailData;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestData;
import in.srain.cube.request.RequestHandler;
import in.srain.cube.request.RequestJsonHandler;
import in.srain.cube.request.SimpleRequest;

public class RequestUtil {

    public static void reverseget(String url, final RequestJsonHandler handler) {
        RequestHandler<JsonData> requestHandler = new RequestHandler<JsonData>() {

            @Override
            public void onRequestFinish(JsonData data) {
                System.out.println("======================success=======================");
                handler.onRequestFinish(data);
            }

            @Override
            public JsonData processOriginData(JsonData jsonData) {
                return jsonData;
            }

            @Override
            public void onRequestFail(FailData failData) {
                System.out.println("======================Fail=======================");
                handler.onRequestFail(failData);
            }
        };
        SimpleRequest<JsonData> request = new SimpleRequest<JsonData>(requestHandler);
        RequestData requestData = request.getRequestData();
        requestData.addHeader("apikey", "6237fe55c078134d0b55468207b95edc");
        requestData.setRequestUrl(url);
        request.send();
    }
}
