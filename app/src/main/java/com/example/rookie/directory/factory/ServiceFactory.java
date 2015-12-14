package com.example.rookie.directory.factory;

import android.content.ContentResolver;
import android.content.Context;

import com.example.rookie.directory.service.ServiceImpl;

/**
 * Created by Rookie on 2015/12/13.
 */
public class ServiceFactory {
    public static ServiceImpl getService(){
        return new ServiceImpl();
    }
}
