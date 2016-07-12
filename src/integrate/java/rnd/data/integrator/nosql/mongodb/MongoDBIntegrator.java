package rnd.data.integrator.nosql.mongodb;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class MongoDBIntegrator {

	public static enum DBHolder {
		
		SINGLETON;

		private static DB db = null;

		// private static MongoCredential credential;

		public DB getDB() {
			if (db == null) {
				try {
					// credential = MongoCredential.createMongoCRCredential(user, dbname, password.toCharArray());
					MongoClient mongo = new MongoClient(new ServerAddress("localhost", 27017)/* , Arrays.asList(credential) */);
					db = mongo.getDB("appydb");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return db;
		}
	}

	public static void main(String[] args) {
		System.out.println(DBHolder.SINGLETON.getDB().getCollectionNames());
	}

}
