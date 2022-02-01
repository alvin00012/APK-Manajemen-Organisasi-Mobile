package com.app.manajemenorganisasi.utils;

public class StringUtil {
    public static String convertToSentenceCase(String text){
        if(text == null || text.isEmpty()){
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;

        for(char c: text.toCharArray()){
            if(Character.isSpaceChar(c)){
                convertNext = true;
            }else if(convertNext){
                c = Character.toTitleCase(c);
                convertNext = false;
            }else{
                c = Character.toLowerCase(c);
            }
            converted.append(c);
        }
        return converted.toString();
    }
}
