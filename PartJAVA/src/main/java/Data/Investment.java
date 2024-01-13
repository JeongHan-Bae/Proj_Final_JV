package Data;

import java.util.HashMap;
import java.util.Map;

public class Investment {

    public float coins;
    public Map<String, Float> investMap;

    // constructor
    public Investment(String investInfos) {
        investMap = new HashMap<>();
        coins = 0.0F;
        if (investInfos.isEmpty()){
            return;
        }
        String[] investments = investInfos.split(",");

        for (String investment : investments) {
            String[] parts = investment.trim().split(" ");
            String key = parts[0];
            float value = Float.parseFloat(parts[1]);
            if (key.equals("coin")){
                coins = value;
            } else {
                investMap.put(key, value);
            }

        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("\n\t\tcoins: " + coins + "\n\t\t" +
                "Financial Products: ");
        for (Map.Entry<String, Float> entry : investMap.entrySet()) {
            String key = entry.getKey();
            Float value = entry.getValue();

            res.append("\n\t\t\t").append(key).append(": ").append(value);
        }
        return res.toString();
    }
}
