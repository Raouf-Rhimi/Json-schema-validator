package json.mapper;

import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        UpdateJsonWithSchema updateJsonWithSchema = new UpdateJsonWithSchema();

        // Read the data json file
        InputStream dataStream = UpdateJsonWithSchema.class.getResourceAsStream("/old_data.json");
        JSONTokener dataTokenizer = new JSONTokener(new InputStreamReader(dataStream));
        JSONObject dataObjectSchema = new JSONObject(dataTokenizer);

        // validate the data file against the schema and save it into a folder called "mappedData" (if it exist, append to it)
        updateJsonWithSchema.validate(dataObjectSchema,"mappedData");
    }
}