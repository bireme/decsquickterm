/*
 * Term.java
 *
 * Created on July 21, 2006, 11:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.bireme.decsterm.model;

import java.util.ArrayList;

/**
 *
 * @author vinicius.andrade
 */
public class DecsTerm {
    private String descriptor;
    private ArrayList<String> synonym = new ArrayList();
    private String treeId;
    
    /** Creates a new instance of Term */
    public DecsTerm() {
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public ArrayList<String> getSynonym() {
        return synonym;
    }

    public void setSynonym(ArrayList synonym) {
        this.synonym = synonym;
    }

    public void addSynonym(String synonym) {
        this.synonym.add(synonym);
    }

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }
    
}
