/*
 * Search.java
 *
 * Created on July 12, 2006, 9:38 AM
 */

package org.bireme.decsterm.servlet;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.search.Sort;

import org.bireme.dia.analysis.StandardLatinAnalyzer;

/**
 *
 * @author vinicius.andrade
 * @version
 */
public class Search extends HttpServlet {
    private IndexSearcher[] searcher = new IndexSearcher[4];
    private Logger log;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        RAMDirectory[] directory = new RAMDirectory[4];
        log = Logger.getLogger(this.getClass());        
        
        String INDEX_DIR = getServletContext().getRealPath("/WEB-INF/resources/index");
        System.out.println(INDEX_DIR);
        try {
            directory[0] = new RAMDirectory(INDEX_DIR + "/all/");
            searcher[0] = new IndexSearcher(directory[0]);
            
            /*
            directory[0] = new RAMDirectory(INDEX_DIR + "/pt/");
            directory[1] = new RAMDirectory(INDEX_DIR + "/es/");
            directory[2] = new RAMDirectory(INDEX_DIR + "/en/");
            */
            
            /*
            searcher[0] = new IndexSearcher(directory[1]);
            searcher[1] = new IndexSearcher(directory[2]);
            searcher[2] = new IndexSearcher(directory[3]);
            */
            
        } catch (IOException ex) {
            log.fatal("falha no carregamento dos indices em memoria", ex);            
            throw new ServletException(ex);
        }
        log.info("servlet inicializado");
        
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        Query query = null;
        QueryParser qParser = null;
        Hits hits = null;
        Document doc = null;
        int index = 0;                  //default index "ALL"
        int count = 100;                //default COUNT
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
        
        String paramQuery = request.getParameter("query");
        String paramCount = request.getParameter("count");
        String lang = request.getParameter("lang");
        
        if (paramCount != null && paramCount.equals("") == false) {
            count = Integer.parseInt(paramCount);
        }
        
        /* always perform a search on all terms (all languages)       
        lang = (lang == null ? "all" : lang);        
        if (lang.equals("pt"))
            index = 1;
        else if (lang.equals("es"))
            index = 2;
        else if (lang.equals("en"))        
            index = 3;
        */
        
        qParser = new QueryParser("term_words", new StandardLatinAnalyzer());
        qParser.setDefaultOperator(QueryParser.AND_OPERATOR);
        
        try {
            query = qParser.parse(paramQuery);
        } catch (ParseException ex) {
            log.fatal("falha no parser da query", ex);
            throw new ServletException("falha no parser do query",ex);
        }
        
        log.debug(query.toString());
        
        hits = searcher[index].search(query, new Sort("term"));
        
        PrintWriter out = response.getWriter();
        response.setContentType("text/xml;charset=ISO-8859-1");
        response.setHeader("Cache-Control", "no-cache");
        out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        out.println("<DeCSTermService version=\"0.1.0\">");
        
        if (hits != null) {
            int len = hits.length();
            int to = (len > count ? count : len);
            String term, treeId = null;
            
            out.println("<Result total=\"" + len + "\" count=\"" + count + "\" >");
            for (int i = 0; i < to; i++) {
                doc = hits.doc(i);
                treeId  = doc.get("tree_id");
                term = doc.get("term");
                term = term.replaceAll("&","&amp;");
                out.println("<item id=\"" + treeId + "\" term=\"" + term + "\"/>");
            }
            out.println("</Result>");
        }else{
            out.println("<Result total=\"0\"/>");
        }
        
        out.println("</DeCSTermService>");
        out.close();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
