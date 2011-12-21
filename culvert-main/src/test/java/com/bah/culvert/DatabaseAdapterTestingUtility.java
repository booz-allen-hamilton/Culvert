package com.bah.culvert;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.bah.culvert.adapter.DatabaseAdapter;
import com.bah.culvert.adapter.TableAdapter;
import com.bah.culvert.data.CColumn;

/**
 * Utility class for testing the new database classes
 */
public class DatabaseAdapterTestingUtility {

  private static String TABLE_NAME = "TestTable";
  private static byte[][] TABLE_SPLIT_KEYS = new byte[0][];
  
  /**
   * Run the full testing suite on the database adapter
   * @param db
   * @throws Throwable
   */
  public static void testDatabaseAdapter(DatabaseAdapter db) throws Throwable {
    testDatabaseConnection(db);
    testCreateTable(db);
    testDeleteTable(db);
  }

  /**
   * Test that the connection can be verified.
   * 
   * @throws Throwable
   */
  private static void testDatabaseConnection(DatabaseAdapter db)
      throws Throwable {
    db.verify();
  }

  /**
   * Test that the adapter can create tables.
   * 
   * @throws Throwable
   */
  private static void testCreateTable(DatabaseAdapter db) throws Throwable {
    List<CColumn> columns = new ArrayList<CColumn>();
    CColumn col1 = new CColumn("col1".getBytes());
    columns.add(col1);
    db.create(TABLE_NAME, TABLE_SPLIT_KEYS, columns);
    TableAdapter table = db.getTableAdapter(TABLE_NAME);
    assertTrue(table != null);
    byte[][] t1Splits = new byte[1][];
    t1Splits[0] = new byte[] { 0, 1, 2, 3 };
    db.create(TABLE_NAME + "1", t1Splits, columns);
    table = db.getTableAdapter(TABLE_NAME + "1");
    assertTrue(table != null);

    byte[][] t2Splits = new byte[2][];
    t2Splits[0] = new byte[] { 0, 1, 2, 3 };
    t2Splits[1] = new byte[] { 3, 2, 12 };

    db.create(TABLE_NAME + "2", t2Splits, columns);
    table = db.getTableAdapter(TABLE_NAME + "2");
    assertTrue(table != null);
  }

  /**
   * Test that the adapter can delete tables.
   * 
   * @throws Throwable
   */
  private static void testDeleteTable(DatabaseAdapter db) throws Throwable {
	   if(db.tableExists(TABLE_NAME))
          db.delete(TABLE_NAME);
	   
	   if(db.tableExists(TABLE_NAME + "1"))
		   db.delete(TABLE_NAME + "1");
	   
	   if(db.tableExists(TABLE_NAME + "2"))
		   db.delete(TABLE_NAME + "2");
  }

}
