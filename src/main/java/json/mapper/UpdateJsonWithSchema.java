package json.mapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UpdateJsonWithSchema {
    public void validate(JSONObject dataJsonObject,String name){
        // Read the schema File
        InputStream schemaStream = UpdateJsonWithSchema.class.getResourceAsStream("/schema.json");
        JSONTokener schemaTokenizer = new JSONTokener(new InputStreamReader(schemaStream));
        JSONObject schemaJsonObject = new JSONObject(schemaTokenizer);

        // validate the data file against the schema
        insertMissingFields(schemaJsonObject, dataJsonObject);

        // prepare a folder under resources to save the output file
        String directoryPath = "src/main/resources";
        String fullPath = directoryPath + File.separator + name;
        File directory = new File(fullPath);
        directory.mkdirs();
        String fileName = String.valueOf(name)+".json";
        Path outputPath = Paths.get(fullPath, fileName);
        if (Files.exists(outputPath)) {
            // If the file already exists, append content to it
            try (FileWriter fileWriter = new FileWriter(outputPath.toString(), true)) {
                fileWriter.write(",");
                fileWriter.write(dataJsonObject.toString(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // If the file doesn't exist, create a new one and write content to it
            try (FileWriter fileWriter = new FileWriter(outputPath.toString())) {
                fileWriter.write(dataJsonObject.toString(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void insertMissingFields(JSONObject schema, JSONObject data) {
        for (String key : schema.keySet()) {
            // Check if the key is present in the data
            if (!data.has(key)) {
                // Key is missing, insert with its default value from the schema
                Object defaultValue = schema.get(key);
                data.put(key, defaultValue);

                // If the default value is an object, recursively insert missing fields in it
                if (defaultValue instanceof JSONObject) {
                    insertMissingFields((JSONObject) defaultValue, data.getJSONObject(key));
                }
            } else {
                // If the schema value is an object and the data value is also an object, recurse
                if (schema.get(key) instanceof JSONObject && data.get(key) instanceof JSONObject) {
                    insertMissingFields((JSONObject) schema.get(key), data.getJSONObject(key));
                }
                // If the schema value is an array and the data value is also an array, recurse for each item
                else if (schema.get(key) instanceof JSONArray && data.get(key) instanceof JSONArray) {
                    JSONArray schemaArray = (JSONArray) schema.get(key);
                    JSONArray dataArray = (JSONArray) data.get(key);
                    for (int i = 0; i < dataArray.length(); i++) {
                        if (schemaArray.length() > 0) {
                            // Assuming that the schema array contains only one schema object
                            insertMissingFields(schemaArray.getJSONObject(0), dataArray.getJSONObject(i));
                        }
                    }
                }
            }
        }
    }
}