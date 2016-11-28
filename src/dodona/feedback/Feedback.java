package dodona.feedback;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Feedback {

    private List<Message> messages;
    private List<Tab>     groups;

    public Feedback() {
        messages = new ArrayList<>();
        groups = new ArrayList<>();
    }

    public static void main(String[] args) {
        Gson gson = new Gson();
        Feedback target = new Feedback();
        System.out.println(gson.toJson(target));
    }

}
