package com.skytek.live.wallpapers.Prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefConfig3d {

    private static final String LIST_KEY ="list_key3d" ;

    public static void writeListInPref3d(Context context, ArrayList<String>list){
        Gson gson=new Gson();
        String jsonString=gson.toJson(list);
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor= pref.edit();
        editor.putString(LIST_KEY,jsonString);
        editor.apply();
    }

    public static ArrayList<String> readListFromPref3d(Context context){

        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(LIST_KEY,"");
        Gson gson= new Gson();
        Type type=new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> list=gson.fromJson(jsonString, type);
        return list;
    }
    }

