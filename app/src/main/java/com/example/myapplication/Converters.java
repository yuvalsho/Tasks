package com.example.myapplication;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;

public class Converters {
    /**
     * converter from task list to string, required to save the list in the database
      * @param list is the list of tasks
     * @return the string that is used to save the list in the database
     */
    @TypeConverter
    public String listToString(ArrayList<String> list){
        StringBuilder converted= new StringBuilder();
        for (String s : list) converted.append(s).append("\n");
        return converted.toString();
    }

    /**
     * converter from a string that is saved in the database to list that is used in the app
     * @param string is the string that is saved in the database
     * @return the list to use in the app
     */
    @TypeConverter
    public ArrayList<String> stringToList(String string){
        ArrayList<String> converted=new ArrayList<>();
        while (!string.isEmpty()){
            converted.add(string.substring(0,string.indexOf('\n')));
            string=string.substring(string.indexOf('\n')+1);
        }
        return converted;
    }
}
