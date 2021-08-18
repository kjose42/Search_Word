package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. 
 * Each keyword maps to a set of documents in which it occurs, 
 * with frequency of occurrence in each document.
 */
public class LittleSearchEngine {
	/**
	 * Hash map data structure:
	 * The key is the string and the corresponding value is an array list of frequencies of the 
	 * keyword in each document.
	 * The array list is in descending order of frequency.
	 * 
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans the document.
	 * Then loads all keywords found into a hash table of keyword occurrences in the document.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		 Scanner sc = new Scanner(new File(docFile));
	     HashMap <String, Occurrence> newHash = new HashMap <String, Occurrence>();
	     while (sc.hasNext()) {
	    	 String word = sc.next();
	         String output = getKeyword(word);
	         if(output != null) {
	        	 boolean foundDup = false;
	             for(String e: newHash.keySet()) {
	            	 if(output.equals(e) == true) {
	            		 Occurrence occ = newHash.get(e);
	                     occ.frequency = occ.frequency + 1;
	                     foundDup = true;
	                     break;}}
	             if(foundDup == false) {
	            	 newHash.put(output, new Occurrence(docFile, 1));}}}
	        sc.close();
	        return newHash;
	}
	
	/**
	 * Inserts the keywords into the keywordsIndex hash Map. 
	 * For each keyword, its Occurrence is placed in the correct position 
	 * (according to descending order of frequency).
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		boolean foundBool = false;
        for(String e: kws.keySet()) {
            ArrayList <Occurrence> tempArr = new ArrayList <Occurrence>();
            for(String e2: keywordsIndex.keySet()) {
                if(e2.equals(e) == true) {
                    tempArr = keywordsIndex.get(e2);
                    tempArr.add(kws.get(e));
                    insertLastOccurrence(tempArr);
                    foundBool = true;
                    break;}}
                if(foundBool == false) {
                    tempArr.add(kws.get(e));
                    keywordsIndex.put(e, tempArr);}
                foundBool = false;}
	}
	
	/**
	 * A keyword is a word that, after removing trailing punctuation, consists only of alphabetic 
	 * letters, and is not a noise word. 
	 * Given a word, returns it as a keyword if it matches the above definition.
	 * If it doesn't match, return null.
	 * 
	 * Characters considered as punctuation: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword
	 */
	public String getKeyword(String word) {
		boolean foundPunct = false;
        String returnStr = "";
        for(int inc = 0; inc < word.length(); inc++) {
            char testChar = word.charAt(inc);
            if(Character.isLetter(testChar) == true) {
                if(foundPunct == true) {return null;}
                else{returnStr = returnStr + Character.toString(testChar).toLowerCase();}}
            else {foundPunct = true;}}
        if(noiseWords.contains(returnStr) == true) {
            return null;}
        if(returnStr == "") {
            return null;}
        return returnStr;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the list
	 * (based on descending order of frequencies).
	 * Binary search is used to find the correct spot to insert the occurrence.
	 * 
	 * @param occs List of Occurrences
	 * @return	Sequence of mid point indexes in the input list checked by the binary search process.
	 * 			This sequence is null if the size of the input list is 1.
	 *         	NOTE: This returned array list is only used to test the code - it is not used 
	 *         	elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs.size() == 1) {
            return null;}
        ArrayList <Integer> midArr = new ArrayList <Integer> ();
        int lastIndex = occs.size()-1;
        int low = 0;
        int high = lastIndex-1;
        int mid = 0;
        boolean lessBool = false;
        while (low <= high) {//once this condition is false, the correct index is found
            mid = (low + high)/2;
            midArr.add(mid);
            if(occs.get(mid).frequency == occs.get(lastIndex).frequency) {
                break;}
            if(occs.get(mid).frequency < occs.get(lastIndex).frequency) {
                lessBool = false;
                high = mid - 1;}
            if(occs.get(mid).frequency > occs.get(lastIndex).frequency) {
                lessBool = true;
                low = mid + 1;}}
        int insertIndex = mid;
        if(lessBool == true) {
            insertIndex = mid + 1;}
        occs.add(insertIndex, occs.get(lastIndex));
        occs.remove(occs.size()-1);
        if(midArr.size() == 0) {
            return null;}
        else {return midArr;}
	}
	
	/**
	 * This method fills the hash map with keywords found in all the documents.
	 * Each keyword will be associated with an array list of occurrence objects.
	 * The array list is in decreasing order of frequencies.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);}
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);}
		sc.close();
	}
	
	/**
	 * Search result for kw1 or kw2. 
	 * A document is in the result set if kw1 or kw2 occurs in that document. 
	 * Result set is arranged in descending order of document frequencies. 
	 * The result set is limited to 5 entries. 
	 * If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. 
	 *         The result size is limited to 5 documents. 
	 *         If there are no matches, returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		kw1 = kw1.toLowerCase();
        kw2 = kw2.toLowerCase();
        ArrayList <String> outArr = new ArrayList <String> ();
        ArrayList <Occurrence> kw1Arr = null;
        ArrayList <Occurrence> kw2Arr = null;
        for(String e: keywordsIndex.keySet()) {
            if(e.equals(kw1) == true) {
                kw1Arr = keywordsIndex.get(e);}
            if(e.equals(kw2) == true) {
                kw2Arr = keywordsIndex.get(e);}}
            int kw1Point = 0;
            int kw2Point = 0;
            for(int inc = 0; inc < 5; inc++) {
                int kw1Value = 0;
                Occurrence kw1Occ = null;
                int kw2Value = 0;
                Occurrence kw2Occ = null;
                if(kw1Arr != null && kw1Point < kw1Arr.size()) {
                    kw1Occ = kw1Arr.get(kw1Point);
                    kw1Value = kw1Occ.frequency;}
                if(kw2Arr != null && kw2Point < kw2Arr.size()) {
                    kw2Occ = kw2Arr.get(kw2Point);
                    kw2Value = kw2Occ.frequency;}
                if(kw1Value == 0 && kw2Value == 0) {
                	//will only occur if the if-statements for 212 and 215 are false
                	//which means that the array doesn't exist or the pointer has iterated through the whole array
                    break;}
                if(kw1Value >= kw2Value) {
                    outArr.add(kw1Occ.document);
                    kw1Point = kw1Point + 1;}
                else {outArr.add(kw2Occ.document);
                    kw2Point = kw2Point + 1;}}
        if(outArr.size() == 0) {//no found matches
            return null;}
        else {return outArr;}//found matches
	}
}
