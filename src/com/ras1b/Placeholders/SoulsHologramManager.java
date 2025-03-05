package com.ras1b.Placeholders;

import java.util.*;
import org.bukkit.entity.Player;

public class SoulsHologramManager {

    public static List<String> getTopSoulsLines(Map<String, Integer> topSouls, Player player) {
        List<String> hologramLines = new ArrayList<>();
        hologramLines.add(" &#FDE49E👻 &#FEB941&lᴛᴏᴘ &f| &#FEB941&lꜱᴏᴜʟꜱ &#FDE49E👻");
        
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(topSouls.entrySet());
        sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        for (int position = 1; position <= 10; position++) {
            String playerName = "---";
            int souls = 0;
            
            if (position <= sortedList.size()) {
                playerName = sortedList.get(position - 1).getKey();
                souls = sortedList.get(position - 1).getValue();
            }
            
            hologramLines.add("&#FEB941#" + position + " &f" + playerName + " &a→ &#FDE49E" + souls + " 👻");
        }

        hologramLines.add("&f");
        hologramLines.add("&#FEB941• ʏᴏᴜʀ ᴘᴏꜱɪᴛɪᴏɴ:");
        
        if (player != null) {
            int playerSouls = topSouls.getOrDefault(player.getName(), 0);
            int playerPosition = sortedList.stream().map(Map.Entry::getKey).toList().indexOf(player.getName()) + 1;
            hologramLines.add("&#FDE49E#" + (playerPosition > 0 ? playerPosition : "--") + " &f" + player.getName() + " &a→ &#FDE49E" + playerSouls + " 👻");
        }
        
        return hologramLines;
    }
}