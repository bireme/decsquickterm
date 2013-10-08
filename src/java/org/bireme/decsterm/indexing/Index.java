/*
 * Index.java
 *
 * Created on June 30, 2006, 11:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.bireme.decsterm.indexing;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.bireme.decsterm.model.DecsTerm;
import org.bireme.dia.analysis.StandardLatinAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author vinicius.andrade
 */
public class Index extends DefaultHandler {
    /** A buffer for each XML element */
    private StringBuilder elementBuffer;
    private StringBuilder path;
    private HashMap attributeMap;
    private Document doc;
    private IndexWriter writer;
    private DecsTerm decsTerm;
    
    public Index(String xml, String dir, Boolean createIndex) throws Exception {
        
        File indexDir = new File("web/WEB-INF/resources/index/" + dir);
                
        try {
            writer = new IndexWriter(indexDir, new StandardLatinAnalyzer(new String[]{}), createIndex);
            System.out.println("Indexing to directory '" + indexDir.getAbsolutePath() + "'...");
            
            Date start = new Date();
            indexTerms(writer, xml);
            System.out.println("Optimizing index...");
            writer.optimize();
            writer.close();
            Date end = new Date();
            
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");
            
        } catch (IOException e) {
            System.out.println("caught a " + e.getClass() + "\n with message: " +
                    e.getMessage());
        }
        
    }
    
    private void indexTerms(IndexWriter writer, String xml){
        SAXParser sax = null;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        
        InputSource xmlInput = new InputSource(xml);
        xmlInput.setEncoding("ISO-8859-1");
        
        try {
            sax = factory.newSAXParser();
            XMLReader reader = sax.getXMLReader();
            reader.setEntityResolver(null);
            reader.setContentHandler(this);
            reader.parse(xmlInput);
            
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void startDocument() {
        this.path = new StringBuilder("/");
        attributeMap = new HashMap();
        elementBuffer = new StringBuilder();
    }
    
    public void startElement(String uri, String localName, String qName, Attributes atts)
    throws SAXException {
        
        this.path.append(qName + "/");
        
        if ( qName.equals("term") ) {
            decsTerm = new DecsTerm();            
        }
        
        elementBuffer.setLength(0);
        attributeMap.clear();
        if (atts.getLength() > 0) {
            attributeMap = new HashMap();
            for (int i = 0; i < atts.getLength(); i++) {
                attributeMap.put(atts.getQName(i), atts.getValue(i));
            }
        }
    }
    
    public void characters(char[] text, int start, int length) {
        elementBuffer.append(text, start, length);
    }
    
    public void ignorableWhitespace(char[] ch, int start, int length)
    throws SAXException {
        
    }
    
    public void endElement(String uri, String localName, String qName)
    throws SAXException {
        
        String text = elementBuffer.toString();
        
        if ( qName.equals("descriptor") ){
            decsTerm.setDescriptor(text);
            
        }else if (qName.equals("synonym")){
            decsTerm.addSynonym(text);          
            
        }else if( qName.equals("tree_id") ) {
            decsTerm.setTreeId(text);
        }
        
        if ( qName.equals("term") ){
            try {

                // index descriptor                 
                index(decsTerm.getDescriptor(), decsTerm.getTreeId());                
                               
                // index each synonym as separate lucene document
                for (String synonym : decsTerm.getSynonym() ) {                    
                    index(synonym, decsTerm.getTreeId());                                                         
                }                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        
    }

    private void index(String term, String id) throws IOException {
        doc = new Document();
        doc.add( new Field("term", term, Field.Store.YES, Field.Index.UN_TOKENIZED) );                
        doc.add( new Field("term_words", term, Field.Store.NO, Field.Index.TOKENIZED) );
        doc.add( new Field("tree_id", id, Field.Store.YES, Field.Index.NO) );
        writer.addDocument(doc);
    }
    
    public static void main(String args[]) throws Exception {
       
        Index idxPt = new Index("/home/projects/decsquickterm/xml/decsterm_pt.xml", "pt", true);
        Index idxEs = new Index("/home/projects/decsquickterm/xml/decsterm_es.xml", "es", true);
        Index idxEn = new Index("/home/projects/decsquickterm/xml/decsterm_en.xml", "en", true);
       
        Index idxAllPt = new Index("/home/projects/decsquickterm/xml/decsterm_pt.xml", "all", true);
        Index idxAllEs = new Index("/home/projects/decsquickterm/xml/decsterm_es.xml", "all", false);
        Index idxAllEn = new Index("/home/projects/decsquickterm/xml/decsterm_en.xml", "all", false);

    }
}
