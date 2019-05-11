/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treeBank;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author confo
 */
public class Reader {

  public static Map<String, Map<String, Integer>> analyzeTreeBank(String path){
    Map<String, Map<String, Integer>> data = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
    String line, token, pos;
    String[] splits;
    Map<String, Integer> tmp;
    while ((line = br.readLine()) != null) {
       if(!(line.isEmpty() || line.trim().equals("") || line.trim().equals("\n")) && line.charAt(0) != '#'){ // ignore comments and empty lines
         splits = line.split("\t"); // splits[1] is token and spltis[3] is PoS
         token = splits[1];
          pos = splits[3];
          if (data.containsKey(token)) { // the token has already been found
            tmp = data.get(token);
            if (tmp.containsKey(pos)) {  // the token was found with the same PoS
              data.get(token).put(pos, tmp.get(pos) + 1);
            } else {  // the token is found with this PoS for the first time
              data.get(token).put(pos, 1);
            }
          }
          else{ // the token is found for the first time
            tmp = new HashMap<>();
            tmp.put(pos, 1);
            data.put(token, tmp);
          }
        }
      }
}   catch (FileNotFoundException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    }
    return data;
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    String path = "/home/confo/UNI/magistrale/TLN/esercizi_parte_1/traduttoreDirect/universal_dependency/ud-treebanks-v2.3/UD_English-GUM/en_gum-ud-dev.conllu";
    Map<String, Map<String, Integer>> map = Reader.analyzeTreeBank(path);
    System.out.println(map);
  }
  
}
