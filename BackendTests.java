import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;





public class BackendTests {

   /**
    * First tester:
    * Tests addRecord() and getAndSetRange().
    * Ensures records are inserted and correctly returned within range.
    */
   @Test
   public void roleTest1() {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);

        GameRecord a = new GameRecord("A", GameRecord.Continent.AFRICA, 100, 5, 10, "001:00:00");
        GameRecord b = new GameRecord("B", GameRecord.Continent.ASIA,   200, 5, 20, "002:00:00");

        backend.addRecord(a);
        backend.addRecord(b); // placeholder forgets A, remembers B

        List<String> results = backend.getAndSetRange(5, 25);

        Assertions.assertFalse(results.contains("A")); // placeholder forgets A
        Assertions.assertTrue(results.contains("B"));  // lastAdded is included
        Assertions.assertEquals(1, results.size());    // fixed records are excluded by range
   }



   /**
    * Second Tester:
    * Tests applyAndSetFilter() together with getAndSetRange().
    * Ensures continent filtering works correctly.
    */
   @Test
   public void roleTest2() {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);

        // Make sure lastAdded is AFRICA
        GameRecord africaGame = new GameRecord("AfricaGame", GameRecord.Continent.AFRICA, 100, 5, 15, "001:30:00");
        backend.addRecord(africaGame);

        backend.getAndSetRange(null, null); // unbounded
        List<String> filtered = backend.applyAndSetFilter(GameRecord.Continent.AFRICA);

        // From placeholder fixed records:
        Assertions.assertTrue(filtered.contains("v0idt3mp0"));

        // Our lastAdded AFRICA record should be included
        Assertions.assertTrue(filtered.contains("AfricaGame"));

        Assertions.assertFalse(filtered.contains("speedRoyalty"));   // EUROPE
        Assertions.assertFalse(filtered.contains("xXxgamer47xXx"));  // ASIA
   }



   /**
    * Third Tester:
    * Tests readData() and getTopTen().
    * Loads records.csv and ensures getTopTen() returns at most 10 items.
    */
   @Test
   public void roleTest3() throws IOException {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);

        // Create temp CSV matching the headers you showed:
        File tmp = File.createTempFile("records", ".csv");
        tmp.deleteOnExit();

        try (PrintWriter out = new PrintWriter(tmp)) {
            out.println("name,continent,score,max_health,damage_taken,damage_given,collectables,level,completion_time");
            out.println("SlowOne,EUROPE,100,10,0,0,5,1,900:00:00");
            out.println("FastestEver,AFRICA,100,10,0,0,6,1,000:00:01");
        }

        backend.readData(tmp.getAbsolutePath());
        backend.getAndSetRange(null, null);
        backend.applyAndSetFilter(null);

        List<String> topTen = backend.getTopTen();

        Assertions.assertTrue(topTen.size() <= 10);
        Assertions.assertTrue(topTen.size() >= 1);

        Assertions.assertEquals("FastestEver", topTen.get(0));
   }


   

   /**
    * Tests that Backend.getAndSetRange correctly returns records whose
    * collectables fall within the specified range when using the real
    * RBTreeIterable data structure.
    */
   @Test
   public void testIntegration1() {

      // create real tree and backend
      IterableSortedCollection<GameRecord> tree = new RBTreeIterable<>();
      Backend backend = new Backend(tree);

      // add records
      backend.addRecord(new GameRecord("GameA",GameRecord.Continent.AFRICA,5,10,5,"000:00:10"));
      backend.addRecord(new GameRecord("GameB",GameRecord.Continent.EUROPE,8,5,8,"000:00:9"));
      backend.addRecord(new GameRecord("GameC",GameRecord.Continent.ASIA,12,3,12,"000:00:8"));

      // request collectables range
      List<String> results = backend.getAndSetRange(5,10);

      // verify only records within range appear
      Assertions.assertTrue(results.contains("GameA"));
      Assertions.assertTrue(results.contains("GameB"));
      Assertions.assertFalse(results.contains("GameC"));
   }


   /**
    * Tests that continent filtering works together with the collectables
    * range when Backend is integrated with RBTreeIterable.
    */
   @Test
   public void testIntegration2() {

       IterableSortedCollection<GameRecord> tree = new RBTreeIterable<>();
      Backend backend = new Backend(tree);

      // insert records
      backend.addRecord(new GameRecord("GameA",GameRecord.Continent.AFRICA,5,10,5,"000:10:00"));
      backend.addRecord(new GameRecord("GameB",GameRecord.Continent.AFRICA,8,5,8,"000:09:00"));
      backend.addRecord(new GameRecord("GameC",GameRecord.Continent.EUROPE,7,4,6,"000:11:00"));

      // set range
      backend.getAndSetRange(5,10);

      // apply continent filter
      List<String> filtered = backend.applyAndSetFilter(GameRecord.Continent.AFRICA);

      // verify filtering
      Assertions.assertTrue(filtered.contains("GameA"));
      Assertions.assertTrue(filtered.contains("GameB"));
      Assertions.assertFalse(filtered.contains("GameC"));
   }



   /**
    * Tests that Backend.getTopTen correctly returns the fastest records
    * when integrated with the RBTreeIterable data structure.
    */
   @Test
   public void testIntegration3() {

      IterableSortedCollection<GameRecord> tree = new RBTreeIterable<>();
      Backend backend = new Backend(tree);

      // add records with different completion times
      backend.addRecord(new GameRecord("FastGame",GameRecord.Continent.AFRICA,5,10,100,"000:02:00"));
      backend.addRecord(new GameRecord("MediumGame",GameRecord.Continent.EUROPE,8,5,200,"000:05:00"));
      backend.addRecord(new GameRecord("SlowGame",GameRecord.Continent.ASIA,12,3,150,"000:10:00"));

      // get fastest times
      List<String> fastest = backend.getTopTen();

      // verify fastest game appears
      Assertions.assertTrue(fastest.contains("FastGame"));
   }




   /**
    * Tests that the frontend help command works correctly when integrated
    * with the real backend and RBTreeIterable implementation.
    */
   @Test
   public void testIntegration4() {

      // simulate user input
      TextUITester tester = new TextUITester("help\nquit\n");

      IterableSortedCollection<GameRecord> tree = new RBTreeIterable<>();
      Backend backend = new Backend(tree);

      Scanner input = new Scanner(System.in);
      Frontend frontend = new Frontend(input, backend);

      // run frontend
      frontend.runCommandLoop();

      // capture output
      String output = tester.checkOutput();

      // verify help instructions appear
      Assertions.assertTrue(output.contains("Available Commands"));
   }

   



}
