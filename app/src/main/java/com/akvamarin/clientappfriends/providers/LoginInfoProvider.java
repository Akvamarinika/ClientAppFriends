package com.akvamarin.clientappfriends.providers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.requests.VKRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class LoginInfoProvider {
    private static final String TAG = "loginInfoProvider";
    private final PreferenceManager preferenceManager;
    private final Context context;

    public LoginInfoProvider(PreferenceManager preferenceManager, Context context) {
        this.preferenceManager = preferenceManager;
        this.context = context;
    }

    public boolean loadInfoAboutUserInVK() {
        if (VK.isLoggedIn()) {
            //photo, photo_medium, photo_big, photo_rec, connections
            VKRequest<JSONObject> vkRequest = new VKRequest<JSONObject>("users.get", VK.getApiVersion())
                    .addParam("fields", "mobile_phone, city, country, timezone, domain, nickname, photo_max, sex, birthdate")
                    .addParam("lang",0);

            //  VK.execute((new UsersService()).usersGet(Collections.singletonList(String.valueOf(VK.getUserId())),
            //          Collections.singletonList(UsersFields.PHOTO_200),
            //         NameCaseParam.NOMINATIVE), new VKApiCallback<List<UsersUserXtrCounters>>() {

            VK.execute(vkRequest, new VKApiCallback<JSONObject>() {

                @Override
                public void success(JSONObject response) {

                    Log.d(TAG, "VKONTAKTE:  VK.execute " + response);   //{"response":[{"first_name":"Akvamarin","id":461.....
                    JsonObject json = null;
                    try {
                        //приходит старый org.json ====> преобразуем в gson
                        json = (JsonParser.parseString(response.getJSONArray("response").getJSONObject(0).toString())).getAsJsonObject();
                        json.addProperty("network", "vkontakte");
                        //json.addProperty("email", access_token.getEmail());
                        //json.addProperty("phone", access_token.getPhone());
                    } catch (Exception e) {
                        //Log.d(TAG,"Ошибка в авторизации данных");
                    }
                    String name = json.get("first_name").toString().replace("\"","")+" "+json.get("last_name").toString().replace("\"","");
                    String photo = json.get("photo_max").toString().replace("\"","");
                    String city = json.get("city").getAsJsonObject().get("title").toString();

                    String email = preferenceManager.getString(Constants.KEY_EMAIL);
//                    String phone = preferenceManager.getString(Constants.KEY_PHONE);

                    Log.d(TAG,name);
                    Log.d(TAG,photo);
                    Log.d(TAG,email);
//                    Log.d(TAG, "ТЕЛЕФОН " + phone.toString());
                    Log.d(TAG,city);
                }


  /*              @Override
                public void success(List<UsersUserXtrCounters> usersUserXtrCounters) {
                    UsersUserXtrCounters userXtrCounters = usersUserXtrCounters.get(0);
                    String name = userXtrCounters.getFirstName();
                    String birthday = userXtrCounters.getBdate();
                    String email = userXtrCounters.getEmail();
                   // String city = userXtrCounters.getCity().getTitle(); ///////////////error
                    String phone = userXtrCounters.getMobilePhone();
                    String urlPhoto = userXtrCounters.getPhoto200();
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_EMAIL, (email != null) ? email : "");
                    preferenceManager.putString(Constants.KEY_PHONE, (phone != null) ? phone : "");
                    preferenceManager.putString(Constants.KEY_NAME, (name != null) ? name : "");
                    preferenceManager.putString(Constants.KEY_BIRTHDAY, (birthday != null) ? birthday : "");
                   // preferenceManager.putString(Constants.KEY_CITY, (city != null) ? city : "");
                    preferenceManager.putString(Constants.KEY_IMAGE, (urlPhoto != null) ? urlPhoto : "");

                    Log.d(TAG, "last name: " + userXtrCounters.getLastName());
                    Log.d(TAG, "first name: " + userXtrCounters.getFirstName());
                    Log.d(TAG, "bday: " + userXtrCounters.getBdate());
                   // Log.d(TAG, "city: " + userXtrCounters.getCity().component2());
                    Log.d(TAG, "first name: " + userXtrCounters.getFirstName());
                    Log.d(TAG, "url: " + userXtrCounters.getPhoto200());

                    Toast.makeText(getApplicationContext(), "getAuthorizeData", Toast.LENGTH_SHORT).show();

                }       */

                @Override
                public void fail(@NotNull Exception e) {
                    //Log.e(TAG, "VKONTAKTE:  VK.execute " + e);
                    Toast.makeText(context,
                            "Не удалось авторизироваться!", Toast.LENGTH_SHORT).show();
                }
            });


            return  true;
        }

        return  false;
    }
}
