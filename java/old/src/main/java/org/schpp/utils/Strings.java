/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.schpp.utils;

/**
 *
 * @author gorka
 */
public class Strings {
    public static String merge( String oldString, String newString ) {
        if( newString == null || newString.isEmpty() )
            return oldString;
        if( oldString == null || oldString.isEmpty() )
            return newString;
        String tmp = oldString + ";";
        if( tmp.contains(newString+";") )
            return oldString;
        return tmp+newString;
    }
}
