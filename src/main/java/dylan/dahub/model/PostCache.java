package dylan.dahub.model;

import java.util.ArrayList;
import java.util.HashMap;

public class PostCache {
    private static HashMap<Integer, Post> INSTANCE;

    public static HashMap<Integer, Post>  getInstance() {
        return INSTANCE;
    }

    public static HashMap<Integer, Post>  createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HashMap<>();
        }
        return INSTANCE;
    }

    public static void getFromCache(int ID) {
        INSTANCE.get(ID);
    }

    public static void addToCache(Post post) {
        INSTANCE.put(post.getID(), post);
    }

    public static void removeFromCache(Post post) {
        INSTANCE.remove(post.getID());
    }
}
