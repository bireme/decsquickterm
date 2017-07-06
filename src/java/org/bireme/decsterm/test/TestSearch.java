/*
 * TestSearch.java
 *
 * Created on July 21, 2006, 12:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.bireme.decsterm.test;

import java.io.PrintWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bireme.dia.analysis.StandardLatinAnalyzer;

/**
 *
 * @author vinicius.andrade
 */
public class TestSearch {
    
    private String INDEX_DIR = "web/WEB-INF/resources/index/pt/";
    private IndexSearcher searcher;
    
    /** Creates a new instance of TestSearch */
    public TestSearch(String userQuery) throws Exception {
        
        Directory dir = FSDirectory.getDirectory(INDEX_DIR, false);
        
        searcher = new IndexSearcher(dir);
        
        Query query = null;
        QueryParser qParser = null;
        Hits hits = null;
        Document doc = null;
        int count = 100;                 //default count
        
        qParser = new QueryParser("term_words", new StandardLatinAnalyzer());
        qParser.setDefaultOperator(QueryParser.AND_OPERATOR);
         
        
        try {
            query = qParser.parse(userQuery);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);        
        /*
        SortField termField = new SortField("term", SortField.STRING, false);
        SortField scoreField = SortField.FIELD_SCORE;
               
        SortField[] sortFieldList = new SortField[2];
        sortFieldList[0] = scoreField;
        sortFieldList[1] = termField;

        Sort sort = new Sort(sortFieldList);        
        hits = searcher.search(query, sort);
        
        */

        SortField termField = new SortField("term", SortField.STRING, false);
        SortField scoreField = SortField.FIELD_SCORE;
               
        SortField[] sortFieldList = new SortField[2];
        sortFieldList[0] = scoreField;
        sortFieldList[1] = termField;

        //Sort sort = new Sort(sortFieldList);
        Sort sort = new Sort("term");
        
        Integer numHits = 2;
        TopDocs topDocs = searcher.search(query, numHits);
        System.out.println(topDocs.totalHits);
        
        ScoreDoc[] topHits = topDocs.scoreDocs;
        for (int i = 0; i < topHits.length; i++) {
            int docId = topHits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println(d.get("term"));
            // do something with current hit
        }
        
        
        
        hits = searcher.search(query, sort);

        
        if (hits != null) {
            int len = hits.length();
            int to = (len > count ? count : len);
            
            System.out.println("<Result total=\"" + len + "\" count=\"" + count + "\">");
            for (int i = 0; i < to; i++) {
                doc = hits.doc(i);
                System.out.println("<item id=\"" + doc.get("tree_id") + "\" term=\"" + doc.get("term") + "\"/>");
            }
            System.out.println("</Result>");
        }else{
            System.out.println("<Result total=\"0\"/>");
        }
        
    }
    
    public static void main(String[] args) throws Exception{
        
        //TestSearch test = new TestSearch("familia OR familia*");
        TestSearch test = new TestSearch("infeccões OR infecções*");
        
        
    }
    
}
