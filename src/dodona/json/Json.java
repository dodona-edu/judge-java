package dodona.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json {

    private Gson gson;

    public Json() {
        this.gson = new GsonBuilder()
            .registerTypeAdapterFactory(new PrettyEnumTypeAdapterFactory())
            .setPrettyPrinting()
            .create();
    }

    public String asString(Object src) {
        return gson.toJson(src);
    }

}
