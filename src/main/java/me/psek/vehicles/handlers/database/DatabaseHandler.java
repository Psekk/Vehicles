package me.psek.vehicles.handlers.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.Arrays;

public class DatabaseHandler {
    public static MongoClient connect(String connectionString, @Nullable MongoClientSettings settings) {
        return settings == null ? MongoClients.create(connectionString) : MongoClients.create(settings);
    }

    public static boolean insert(MongoClient client, String databaseName, String collectionName, Document... documents) {
        MongoDatabase database = getDatabase(client, databaseName);
        MongoCollection<Document> collection = getCollection(database, collectionName);
        collection.insertMany(Arrays.asList(documents));
        return true;
    }

    public static boolean modify() {
        return false;
    }

    public static boolean remove() {
        return false;
    }

    public static BsonDocument get() {
        return null;
    }

    //todo add proper checks
    private static MongoDatabase getDatabase(MongoClient client, String databaseName) {
        return client.getDatabase(databaseName);
    }

    private static MongoCollection<Document> getCollection(MongoDatabase database, String collectionName) {
        return database.getCollection(collectionName);
    }
}
