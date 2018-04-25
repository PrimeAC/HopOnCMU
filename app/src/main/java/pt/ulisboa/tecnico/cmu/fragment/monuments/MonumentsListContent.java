package pt.ulisboa.tecnico.cmu.fragment.monuments;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class with the content of the Monument list on MainActivity
 */
public class MonumentsListContent {
    /**
     * An array of monuments items.
     */
    public static List<MonumentItem> ITEMS = new ArrayList<>();

    /**
     * A map of monuments items, by ID.
     */
    public static final Map<String, MonumentItem> ITEM_MAP = new HashMap<>();


    public static void addMonuments(List<String> monumentsNames){
        // Add some sample items.
        for (int i = 1; i <= monumentsNames.size(); i++) {
            String[] monumentName = monumentsNames.get(i-1).split("\\|");
            if (monumentName[monumentName.length-1].equals("T")){
                addItem(createDummyItem(i,monumentName[0], true));
            }
            else {
                addItem(createDummyItem(i,monumentName[0], false));
            }
        }
    }

    public static void deleteMonuments() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    private static void addItem(MonumentItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static MonumentItem createDummyItem(int position, String content, boolean isAnswered) {
        return new MonumentItem(String.valueOf(position), content, isAnswered);
    }

    public static List<MonumentItem> getITEMS(){
        return ITEMS;
    }

    /**
     * A monument item representing a monument
     */
    public static class MonumentItem {
        public final String id;
        public final String content;
        public boolean answered;

        public MonumentItem(String id, String content, boolean answered) {
            this.id = id;
            this.content = content;
            this.answered = answered;
        }

        public void setAnswered(){
            answered = true;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
