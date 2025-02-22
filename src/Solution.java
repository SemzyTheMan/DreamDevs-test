import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) {
        List<Map<String, Object>> allData = readTextFiles("test-case-1");

        Integer highestSalesVolume = Collections.max(getHighestSalesVolumeInADay(allData));
        System.out.println(highestSalesVolume);


    }

    public static List<Integer> getHighestSalesVolumeInADay(List<Map<String, Object>> allRecords) {
        List<Integer>  totalSalesVolumePerDay = new ArrayList<>();
        allRecords.forEach(record -> {
            Object products = record.get("products");
            List<Map<String, Integer>>  productsList = convertStringToList(products.toString());
            Integer totalQuantity=0;
            for(Map<String, Integer> product : productsList){
                totalQuantity+= product.get("quantity");
            }
            totalSalesVolumePerDay.add(totalQuantity);

        });

        return totalSalesVolumePerDay;

    }

    public static List<Map<String, Integer>> convertStringToList(String input) {
        List<Map<String, Integer>> resultList = new ArrayList<>();

        input = input.replaceAll("[\\[\\]]", "");

        String[] mapStrings = input.split("\\},\\s*\\{");

        for (String mapStr : mapStrings) {

            mapStr = mapStr.replaceAll("[{}]", "");

            Map<String, Integer> map = new HashMap<>();

            String[] pairs = mapStr.split(",\\s*");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = keyValue[0].trim();
                Integer value = Integer.parseInt(keyValue[1].trim());
                map.put(key, value);
            }

            resultList.add(map);
        }

        return resultList;
    }


    public static List<Map<String, Object>> readTextFiles(String folderPath) {
        List<Map<String, Object>> fileContents = new ArrayList<>();

        try {
            List<Path> textFiles = Files.walk(Paths.get(folderPath))
                    .filter(path -> path.toString().endsWith(".txt"))
                    .collect(Collectors.toList());

            textFiles.forEach(file -> {

                fileContents.addAll(convertTextToMap(file.toString()));
            });


        } catch (IOException e) {
            System.err.println("Error accessing directory: " + folderPath);
            e.printStackTrace();
        }
        return fileContents;

    }

    public static List<Map<String, Object>> convertTextToMap(String filePath) {
        List<Map<String, Object>> data = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(filePath))) {


            bufferedReader.lines().forEach(lines -> {

                Map<String, Object> innerMap = new HashMap<>();
                String[] split = lines.split(",");
                innerMap.put("salesStaffId", split[0]);
                innerMap.put("transactionTime", split[1]);
                String product = split[2].substring(1, split[2].length() - 1);
                List<Map<String, String>> dayProducts = new ArrayList<>();

                String[] tempProducts = product.split("\\|");
                for (String productStr : tempProducts) {
                    Map<String, String> products = new HashMap<>();
                    String[] productSplit = productStr.split(":");
                    products.put("productId", productSplit[0]);
                    products.put("quantity", productSplit[1]);
                    dayProducts.add(products);
                }
                innerMap.put("products", dayProducts.toString());
                innerMap.put("totalAmount", split[3]);
                data.add(innerMap);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}