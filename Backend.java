import java.util.List;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class Backend implements BackendInterface {

    private IterableSortedCollection<GameRecord> tree;
    private Integer lowRange = null;
    private Integer highRange = null;
    private GameRecord.Continent continentFilter = null;

    public Backend(IterableSortedCollection<GameRecord> tree) {
     this.tree = tree;

    }
    // Your constructor must have the signature above. All methods below must
    // use the provided tree to store, sort, and iterate through records. This
    // will enable you to create some tests that use the placeholder tree, and
    // others that make use of a working tree, depending on what is passed
    // into this constructor.



    /** Add and stores the specified record to the tree. Don't forget that the GameRe>
     *  must have the Comparator set. This will be used to store these records in ord>
     *  tree, and to retrieve them by collectables range in the getRange method.
     * @param record the game record to add
     */
    public void addRecord(GameRecord record){
       if(record == null) throw new NullPointerException("Cannot add empty record in tree."); 
 
       this.tree.insert(record);
    }



     /**
     * Loads data from the .csv file referenced by filename.  You can rely
     * on the exact headers found in the provided records.csv, but you should
     * not rely on them always being presented in this order or on there
     * not being additional columns describing other record qualities.
     * After reading records from the file, the records are inserted into
     * the tree passed to this backend's constructor. This will be used to store thes>
     * tree, and to retrieve them by score range in the getRange method.
     * @param filename is the name of the csv file to load data from
     * @throws IOException when there is trouble finding/reading file
     * @throws IllegalArgumentException if required columns are missing.
     */
    public void readData(String filename) throws IOException {

    if (filename == null) throw new IOException("Filename cannot be null.");

    Scanner scanner = null;

    try {
        File file = new File(filename);
        scanner = new Scanner(file);

        // empty file: nothing to load
        if (!scanner.hasNextLine()) {
            return;
        }

        // read header line
        String headerLine = scanner.nextLine();
        String[] headers = headerLine.split(",");

        // mapping them, so that it would be easier to track later. 
        Map<String, Integer> col = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            col.put(headers[i].trim().toLowerCase(), i);
        }

        // helps to understand where exactly our headers are stored
        int nameIdx = reqColumnHelper(col, "name");
        int continentIdx = reqColumnHelper(col, "continent");
        int scoreIdx = reqColumnHelper(col, "score");
        int maxHealthIdx = reqColumnHelper(col, "max_health");
        int collectablesIdx = reqColumnHelper(col, "collectables");
        int completionTimeIdx = reqColumnHelper(col, "completion_time");

        // read each data row
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            
            // Skip rows that do not contain all expected columns
            if (parts.length < headers.length) continue;

            try {
               String name = parts[nameIdx].trim();
               GameRecord.Continent location =
                     GameRecord.Continent.valueOf(parts[continentIdx].trim().toUpperCase());
                    
               int score = Integer.parseInt(parts[scoreIdx].trim());
               int maxHealth = Integer.parseInt(parts[maxHealthIdx].trim());
               int collectables = Integer.parseInt(parts[collectablesIdx].trim());
               String completionTime = parts[completionTimeIdx].trim();

               GameRecord record =
                    new GameRecord(name, location, score, maxHealth, collectables, completionTime);

               tree.insert(record);
            } catch (IllegalArgumentException e) {
                // Skip malformed rows instead of stopping the whole file read
                continue;
            }

         }

       } catch (FileNotFoundException e) {
        throw new IOException("Could not find file: " + filename, e);
       } finally {
          if (scanner != null) scanner.close();
       }
     }

   



     // Helper that throws exception if specific header is not found.
     private int reqColumnHelper(Map<String, Integer> col, String name) {
      if(!col.containsKey(name)) throw new IllegalArgumentException("This required column" + name + " is missing");

       return col.get(name);

     }



     /**
     * Retrieves a list of names from the tree passed to the constructor.
     * The records should be ordered by the record's collectables, and fall within
     * the specified range of collectable values.  This collectables range will
     * also be used by future calls to filterRecords and getTopTen.
     *
     * If a continent filter has been set using the filterRecords method
     * below, then only records that pass that filter should be included in the
     * list of names returned by this method.
     *
     * When null is passed as either the low or high argument to this method,
     * that end of the range is understood to be unbounded.  For example, a 
     * null argument for the high parameter means that there is no maximum 
     * collectables to include in the returned list.
     *
     * @param low is the minimum collectables of records in the returned list
     * @param high is the maximum collectables of records in the returned list
     * @return List of names for all records from low to high that pass any
     *     set filter, or an empty list when no such records can be found
     */

     public List<String> getAndSetRange(Integer low, Integer high) {

       this.lowRange = low;
       this.highRange = high;

       if (low == null) {
         tree.setIteratorMin(null);
       } else {
          GameRecord min = new GameRecord("", GameRecord.Continent.AFRICA, 0, 0, low, "");
          tree.setIteratorMin(min);
       }

        if (high == null) {
          tree.setIteratorMax(null);
        } else {
           GameRecord max = new GameRecord("", GameRecord.Continent.AFRICA, 0, 0, high, "");
           tree.setIteratorMax(max);
        }

        List<String> listOfNames = new ArrayList<>();
        for (GameRecord record : tree) {
           if (continentFilter == null || record.getContinent() == continentFilter) {
              listOfNames.add(record.getName());
           }
        }

       return listOfNames;
     }





     /**
     * Retrieves a list of record names that have a continent that match the specified
     * continent.  
     * Similar to the getRange method: this list of record names should be ordered by>
     * collectables, and should only include records that fall within the specified
     * range of collectable values that was established by the most recent call
     * to getRange.  If getRange has not previously been called, then no low
     * or high collectable bound should be used.  The filter set by this method
     * will be used by future calls to the getRange and getTopTen methods.
     *
     * When null is passed as the continent to this method, then no 
     * continent filter should be used.  This clears the filter.
     *
     * @param continent filters returned record names to only include records that
     *     have a continent that match the specified value.
     * @return List of names for records that meet this filter requirement and
     *     are within any previously set collectables range, or an empty list
     *     when no such records can be found
     */
     public List<String> applyAndSetFilter(GameRecord.Continent continent) {

       this.continentFilter = continent;

       if (this.lowRange == null) {
        tree.setIteratorMin(null);
       } else {
         GameRecord min = new GameRecord("", GameRecord.Continent.AFRICA, 0, 0, this.lowRange, "");
         tree.setIteratorMin(min);
       }

       if (this.highRange == null) {
        tree.setIteratorMax(null);
       } else {
          GameRecord max = new GameRecord("", GameRecord.Continent.AFRICA, 0, 0, this.highRange, "");
          tree.setIteratorMax(max);
       }

      List<String> names = new ArrayList<>();
      for (GameRecord record : tree) {
          if (this.continentFilter == null || record.getContinent() == this.continentFilter) {
            names.add(record.getName());
          }
      }

        return names;
     }









     /**
     * This method returns a list of record names representing the top
     * ten fastest (lowest completion time) records that both fall within any attribu>
     * by the most recent call to getRange, and conform to any filter set by
     * the most recent call to filteredRecords.  The order of the record names 
     * in this returned list is up to you.
     *
     * If fewer than ten such records exist, return all of them.  And return an
     * empty list when there are no such records.
     *
     * @return List of ten fastest record names
     */
     public List<String> getTopTen() {

       if (lowRange == null) {
          tree.setIteratorMin(null);
       } else {
          GameRecord min = new GameRecord("", GameRecord.Continent.AFRICA, 0, 0, lowRange, "000:00:00");
          tree.setIteratorMin(min);
       }

       if (highRange == null) {
          tree.setIteratorMax(null);
       } else {
          GameRecord max = new GameRecord("", GameRecord.Continent.AFRICA, 0, 0, highRange, "999:59:59");
          tree.setIteratorMax(max);
        }

      List<GameRecord> matches = new ArrayList<>();
      for (GameRecord r : tree) {
         if (continentFilter == null || r.getContinent() == continentFilter) {
            matches.add(r);
         }
       }

      Collections.sort(matches, new Comparator<GameRecord>() {

        public int compare(GameRecord a, GameRecord b) {
            return parseTimeToSeconds(a.getCompletionTime()) - parseTimeToSeconds(b.getCompletionTime());
        }
      });

      List<String> result = new ArrayList<>();
      for (int i = 0; i < matches.size() && i < 10; i++) {
         result.add(matches.get(i).getName());
      }
   

       return result;
    }







    // Helper for formating completion time.
    // Accepts formats: "hh:mm:ss", "mm:ss", or "ss".
    private int parseTimeToSeconds(String time) {
     
       // If given time is empty, will throw exception.
       if (time == null || time.trim().isEmpty()) {
         throw new IllegalArgumentException("Time value cannot be empty.");
       }
     
       // Using ":" to seperate hours/minutes/seconds from one another.
       String[] parts = time.trim().split(":");

       try {

         if (parts.length == 3) {
         
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            int s = Integer.parseInt(parts[2]);
            return h * 3600 + m * 60 + s;

         } else if (parts.length == 2) {

            int m = Integer.parseInt(parts[0]);
            int s = Integer.parseInt(parts[1]);
            return m * 60 + s;

         } else if (parts.length == 1) {

            int s = Integer.parseInt(parts[0]);
            return s;

         } else {
            throw new IllegalArgumentException("Invalid time format: " + time);
         }

       } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value in time: " + time);
       }
       
       
    }




}

