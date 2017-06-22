package com.rc.nowtv.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.rc.nowtv.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by berg on 26/04/17.
 */

public class LocalStorage {
    public static final String LABEL = "storage_label";

    /**
     * Type: String
     */

    /**
     * Objeto Usuário
     */
    public static final String USER = "user";
    private static final String SIZE_CHAT_MESSAGE_LIST = "size_message_list";
    private static final String CHAT_MESSAGE_LIST = "message";

    /**
     * Objeto Video
     */
    public static final String OBJ_VIDEO = "obj_video";


    /**
     *
     * Type: int
     */

    /**
     * Token do usuário
     */
    public static String REQUEST_TOKEN = "token";

    /**
     * Type: boolean
     */

    /**
     * Type: int
     */
    public static final String PLAYER_MODE = "player_mode";

    private static final String SETTINGS = "settings";


    public static LocalStorage localStorage;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public LocalStorage(Context context) {
        settings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static LocalStorage getInstance(Context context) {
        if (localStorage == null)
            localStorage = new LocalStorage(context);
        return localStorage;
    }

    public void addToStorage(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void addToStorage(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void addToStorage(String key, long value) {
        editor.putString(key, "" + value);
        editor.commit();
    }

    /**
     * Persiste um valor <code>boolean</code> sob uma chave pré-definida.
     *
     * @param key   Chave pré-definida.
     * @param value Valor
     */
    public void addToStorage(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Persiste um valor <code>double</code> sob uma chave pré-definida.
     *
     * @param key   Chave pré-definida.
     * @param value Valor
     */
    public void addToStorage(String key, double value) {
        editor.putString(key, "" + value);
        editor.commit();
    }

    /**
     * Persiste um valor <code>Object</code> sob uma chave pré-definida.
     *
     * @param key   Chave pré-definida.
     * @param value Valor
     */
    public <T> void addToStorage(String key, T value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.commit();
    }

    /**
     * Retorna um Objeto que foi armazenado utilizando uma chave especícia <br/>
     *
     * @param key Chave pré-definida.
     * @return Dado persistido. Caso não haja, retorna <code>null</code>
     */
    public <T> T getObjectFromStorage(String key, Class<T> clazz) {
        String json = settings.getString(key, null);
        Gson gson = new Gson();

        T object = null;
        try {
            object = gson.fromJson(json, clazz);
        }catch (Exception e) {
            object = null;
        }

        return object;
    }

    /**
     * Retorna uma string que foi armazenada utilizando uma chave especícia <br/>
     *
     * @param key Chave pré-definida.
     * @return Dado persistido. Caso não haja, retorna <code>null</code>
     */
    public String getStringFromStorage(String key) {
        return settings.getString(key, null);
    }

    /**
     * Retorna um inteiro que foi armazenado utilizando uma chave especícia <br/>
     *
     * @param key Chave pré-definida
     * @return Dado persistido
     */
    public int getIntFromStorage(String key) {
        return settings.getInt(key, 0);
    }

    /**
     * Retorna um valor booleano foi armazenado utilizando uma chave especícia <br/>
     *
     * @param key Chave pré-definida.
     * @return Dado persistido
     */
    public boolean getBooleanFromStorage(String key) {
        return settings.getBoolean(key, false);
    }

    /**
     * Retorna um valor double foi armazenado utilizando uma chave especícia <br/>
     *
     * @param key Chave pré-definida.
     * @return Dado persistido
     */
    public double getDoubleFromStorage(String key) {
        String str = settings.getString(key, null);
        if (str != null)
            try {
                return Double.parseDouble(str);
            } catch (Exception e) {
                //Falha na conversão String para Double
                Log.e("LocalStorage", "Falha na conversão de String para Double em LocalStorage.getDoubleFromStorage");
                return 0.0;
            }
        else
            return 0.0;
    }

    public long getLongFromStorage(String key) {
        try {
            String str = settings.getString(key, null);
            if (str != null)
                return Long.parseLong(str);
            else
                return 0;
        } catch (Exception e) {
            //Falha na conversão String para Double
            Log.e("LocalStorage", "Falha na conversão de String para Long em LocalStorage.getLongFromStorage");
            return 0;
        }
    }

    /**
     * Persiste a lista de comentários de um vídeo
     * @param value
     */
    public void addMessageListToStorage(List<ChatMessage> value) {
        if(value != null && value.size() < 20) {
            addToStorage(SIZE_CHAT_MESSAGE_LIST, value.size());

            for(int i = 0; i < value.size(); i++) {
                ChatMessage chatMessage = value.get(i);
                addToStorage(CHAT_MESSAGE_LIST + i, chatMessage);
            }
        } else if(value.size() > 20) {
            addToStorage(SIZE_CHAT_MESSAGE_LIST, 0);
        }
    }

    public ArrayList<ChatMessage> getMessagesFromStorage() {
        ArrayList<ChatMessage> arrayList = new ArrayList<>();
        int size = getIntFromStorage(SIZE_CHAT_MESSAGE_LIST);

        for(int i = 0; i < size; i++) {
            ChatMessage comment = getObjectFromStorage(CHAT_MESSAGE_LIST + i, ChatMessage.class);

            arrayList.add(comment);
        }

        return arrayList;
    }

}

