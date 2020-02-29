package edu.duke.ece651.classbuilder;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.io.IOException;
import java.io.*;
import java.util.*;
public class ClassBuilderTest {
  @Test
  public void test_ClassBuilder() throws Exception{
    String content1 = Files.readString(Paths.get("../ece651-hwk1-tester/inputs/empty.json")); 
    ClassBuilder A = new ClassBuilder(content1);
    String bathpath = "../ece651-hwk1-tester/src/main/java/";
    A.createAllClasses(bathpath);

    String content2 = Files.readString(Paths.get("../ece651-hwk1-tester/inputs/arr.json")); 
    ClassBuilder b = new ClassBuilder(content2);
    
    b.createAllClasses(bathpath);

    String content3 = Files.readString(Paths.get("../ece651-hwk1-tester/inputs/nameRef.json")); 
    ClassBuilder c = new ClassBuilder(content3);
    
    c.createAllClasses(bathpath);

    /*String content4 = Files.readString(Paths.get("../ece651-hwk1-tester/inputs/crazyarray.json")); 
    ClassBuilder d = new ClassBuilder(content4);
    
    d.createAllClasses(bathpath);

    String content5 = Files.readString(Paths.get("../ece651-hwk1-tester/inputs/mdarr.json"));
    ClassBuilder e = new ClassBuilder(content5);
    
    e.createAllClasses(bathpath);*/

    /*String no_pkg = "{'classes':[{'name':'Test','fields':[{'name':'x','type':'int'}]}]}";

     ClassBuilder no_p = new ClassBuilder(no_pkg);
     no_p.createAllClasses("../ece651-hwk1-tester/src/main/java");*/
      
    InputStream input = new FileInputStream("../ece651-hwk1-tester/inputs/simple.json");
    ClassBuilder A_stream = new ClassBuilder(input);
    ArrayList<String> classNames = A_stream.getClassNames();
    A_stream.createAllClasses(bathpath);

    InputStream input_2 = new FileInputStream("../ece651-hwk1-tester/inputs/prims.json");
    ClassBuilder B_stream = new  ClassBuilder(input_2);
    B_stream.createAllClasses(bathpath);

    /* InputStream input_3 = new FileInputStream("../ece651-hwk1-tester/inputs/zoo.json");
    ClassBuilder C_stream =new  ClassBuilder(input_3);
    C_stream.createAllClasses(bathpath);

    InputStream input_4 = new FileInputStream("../ece651-hwk1-tester/inputs/simplearray.json");
    ClassBuilder D_stream = new ClassBuilder(input_4);
    D_stream.createAllClasses(bathpath);
    */
    
  }
}
