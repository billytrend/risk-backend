package GeneralUtils;

import com.google.gson.Gson;

public class Jsonify {

    // the idea is that this maybe overridden to handle certain objects
    public static String getObjectAsJsonString(Object o) {
        return new Gson().toJson(o);
    }
    public static Object getJsonStringAsObject(String s, Class c) {
        return new Gson().fromJson(s, c);
    }

}
