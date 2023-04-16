package com.akvamarin.clientappfriends.vk.requests;

import androidx.annotation.NonNull;

import com.akvamarin.clientappfriends.vk.models.VKUser;
import com.vk.api.sdk.VKApiJSONResponseParser;
import com.vk.api.sdk.VKApiManager;
import com.vk.api.sdk.VKMethodCall;
import com.vk.api.sdk.exceptions.VKApiException;
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException;
import com.vk.api.sdk.internal.ApiCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VKUsersCommand extends ApiCommand<List<VKUser>> {
    public static final int CHUNK_LIMIT = 900;
    private final int[] uids;

    public VKUsersCommand(int[] uids) {
        this.uids = uids != null ? uids : new int[0];
    }

    // Execute the VKUsersCommand by sending a VK API request to retrieve data for the specified user IDs
    @Override
    public List<VKUser> onExecute(@NonNull VKApiManager manager) throws VKApiException, IOException, InterruptedException {
        if (uids.length == 0) {
            // if no uids, send user's data
            VKMethodCall call = new VKMethodCall.Builder()
                    .method("users.get")
                    .args("fields", "photo_200, bdate, city, country, sex, about")
                    .version(manager.getConfig().getVersion())
                    .args("lang", "ru")
                    .build();
            return manager.execute(call, new ResponseApiParser());
        } else {
            List<VKUser> result = new ArrayList<>();
            List<List<Integer>> chunks = chunkArray(uids, CHUNK_LIMIT);
            for (List<Integer> chunk : chunks) {
                String ids = chunk.stream().map(Object::toString).collect(Collectors.joining(","));
                VKMethodCall call = new VKMethodCall.Builder()
                        .method("users.get")
                        .args("user_ids", ids)
                        .args("fields", "photo_200, bdate, city, country, sex, about")
                        .args("lang", "ru")
                        .version(manager.getConfig().getVersion())
                        .build();
                result.addAll(manager.execute(call, new ResponseApiParser()));
            }
            return result;
        }
    }

    // Implementation of VKApiJSONResponseParser that parses the response into a List<VKUser>
    private static class ResponseApiParser implements VKApiJSONResponseParser<List<VKUser>> {
        @Override
        public List<VKUser> parse(JSONObject responseJson) throws VKApiIllegalResponseException {
            try {
                JSONArray jsonArray = responseJson.getJSONArray("response");
                ArrayList<VKUser> r = new ArrayList<>(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    VKUser user = VKUser.parse(jsonArray.getJSONObject(i));
                    r.add(user);
                }
                return r;
            } catch (JSONException ex) {
                throw new VKApiIllegalResponseException(ex);
            }
        }
    }



    // Helper function to split an array into chunks of the given size
    public static List<List<Integer>> chunkArray(int[] array, int chunkSize) {
        List<List<Integer>> chunks = new ArrayList<>();
        for (int i = 0; i < array.length; i += chunkSize) {
            List<Integer> chunk = Arrays.stream(Arrays.copyOfRange(array, i, Math.min(i + chunkSize, array.length)))
                    .boxed()
                    .collect(Collectors.toList());
            chunks.add(chunk);
        }
        return chunks;
    }





}
