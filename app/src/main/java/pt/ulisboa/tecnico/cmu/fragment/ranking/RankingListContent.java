package pt.ulisboa.tecnico.cmu.fragment.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class RankingListContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<RankingItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, RankingItem> ITEM_MAP = new HashMap<>();

    public static void updateRanking(Map<String, Integer> ranking){
        // Add some sample items.
        int i = 1;
        for (Map.Entry<String, Integer> entry: ranking.entrySet()) {
            addItem(createDummyItem(i, entry.getKey(), entry.getValue()));
            i++;
        }
    }

    public static void deleteRanking(){
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    private static void addItem(RankingItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static RankingItem createDummyItem(int position, String userID, int userScore) {
        return new RankingItem(String.valueOf(position), userID + ":" + userScore, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class RankingItem {
        public final String id;
        public final String content;
        public final String details;

        public RankingItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
