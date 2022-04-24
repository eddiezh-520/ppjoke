package com.example.ppjoke.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.ppjoke.model.BottomBar;
import com.example.ppjoke.model.Destination;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AppConfig {

    private static HashMap<String, Destination> sDestConfig;
    private static BottomBar sBottomBar;

    public static HashMap<String, Destination> getDestConfig() {
        if (sDestConfig == null) {
            String content = parseFile("destination.json");
            sDestConfig = JSON.parseObject(content,new TypeReference<HashMap<String, Destination>>(){}.getType());
        }
        return sDestConfig;
    }

    public static BottomBar getBottomBar() {
        if (sBottomBar == null) {
            String content = parseFile("main_tabs_config.json");
            sBottomBar = JSON.parseObject(content,BottomBar.class);
        }
        return sBottomBar;
    }


    private static String parseFile(String fileName) {
        //解析assets下面的文件，需要配合AssetsManager来实现   这个AssetsManager需要context对象来得到
        AssetManager assets = AppGlobals.getApplication().getAssets();

        InputStream stream = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            stream = assets.open(fileName);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }catch (Exception e){ }
        }

        return builder.toString();
    }
}
