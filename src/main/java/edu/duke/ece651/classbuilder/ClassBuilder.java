package edu.duke.ece651.classbuilder;

import java.io.*;
import java.util.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
//classbuilder
class ClassBuilder {
	private ArrayList<String> classes;
	private Map<String, List<List<String>>> classfields; 
	private String pkg = "test";
	 
	
	public ClassBuilder(String str) {
		classes = new ArrayList<String>();
		classfields = new HashMap<>();
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(str); 
		}
		catch(JSONException e) {
			e.printStackTrace();
		}
		String pkg_name = jobj.optString("package");
		if(pkg_name != "") {
			pkg = jobj.getString("package");
			//System.out.println("pkg"+pkg);
		}
		JSONArray jsonarr = jobj.optJSONArray("classes");
		if(jsonarr != null) {
			
			for(int i =0; i <jsonarr.length(); i++) {
				List<List<String>> L = new ArrayList<List<String>>();
				JSONObject obj_i = (JSONObject)jsonarr.get(i);
				//System.out.println(obj_i.getString("name"));
				classes.add(obj_i.getString("name")); 
				JSONArray arr = obj_i.getJSONArray("fields");
				//System.out.println("i:"+i);
				for(int j = 0; j<arr.length(); j++) {
					//System.out.println("j:"+j);
					JSONObject obj_j = arr.getJSONObject(j);
					List<String> l = new ArrayList<String>();
					l.add(obj_j.getString("name"));
					//System.out.println("here "+obj_j.getString("name"));
					String type;
					if(obj_j.optJSONObject("type")!=null) {
						type = convert(obj_j.getJSONObject("type"));
					}
					else {
						type = obj_j.getString("type");
					}
					//l.add(obj_j.getString("type"));
					l.add(type);
					//System.out.println(obj_j.getString("name")+":"+type);
					L.add(l);
				}
				classfields.put(obj_i.getString("name"), L);
			}	
		}
	}
	//classbuilder
	public ClassBuilder(InputStream is) {
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(is));
		StringBuilder stringBuilder = new StringBuilder();
		String currentLine;
		try {
			while((currentLine = streamReader.readLine()) != null) {
				stringBuilder.append(currentLine);
			}
			//call classbuilder(string)
			classes = new ArrayList<String>();
			classfields = new HashMap<>();
			JSONObject jobj = null;
			try {
				jobj = new JSONObject(stringBuilder.toString()); 
			}
			catch(JSONException e) {
				e.printStackTrace();
			}
			String pkg_name = jobj.optString("package");
			if(pkg_name != "") {
				pkg = jobj.getString("package");
				//System.out.println("pkg"+pkg);
			}
			JSONArray jsonarr = jobj.optJSONArray("classes");
			if(jsonarr != null) {
				
				for(int i =0; i <jsonarr.length(); i++) {
					List<List<String>> L = new ArrayList<List<String>>();
					JSONObject obj_i = (JSONObject)jsonarr.get(i);
					//System.out.println(obj_i.getString("name"));
					classes.add(obj_i.getString("name")); 
					JSONArray arr = obj_i.getJSONArray("fields");
					//System.out.println("i:"+i);
					for(int j = 0; j<arr.length(); j++) {
						//System.out.println("j:"+j);
						JSONObject obj_j = arr.getJSONObject(j);
						List<String> l = new ArrayList<String>();
						l.add(obj_j.getString("name"));
						//System.out.println("here "+obj_j.getString("name"));
						String type;
						if(obj_j.optJSONObject("type")!=null) {
							type = convert(obj_j.getJSONObject("type"));
						}
						else {
							type = obj_j.getString("type");
						}
						//l.add(obj_j.getString("type"));
						l.add(type);
						//System.out.println(obj_j.getString("name")+":"+type);
						L.add(l);
					}
					classfields.put(obj_i.getString("name"), L);
				}	
			}
			//
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	//return classes
	ArrayList<String> getClassNames(){
		return this.classes;
	}
	//getsourcecode
	String getSourceCode(String className) {
		if(!classfields.containsKey(className)) {
			System.out.println("no such classname:"+className);
			System.exit(1);
			
		}
		List<List<String>> fields = classfields.get(className);
		String sourcecode = "";
		if(pkg != null) {
			sourcecode += "package "+pkg + ";\n\n";
		}
		sourcecode += "import java.io.*;\n";
		sourcecode += "import java.util.*;\n";
		sourcecode += "import org.json.JSONException;\n";
		sourcecode += "import org.json.JSONObject;\n";
		sourcecode += "import org.json.JSONArray;\n";
	
		sourcecode += "public class "+className +" {\n\n";
		//System.out.println(sourcecode);
		for(int i = 0; i< fields.size(); i++) {
			String type = fields.get(i).get(1);
			String name = fields.get(i).get(0);
			
			
			if(type.length() > 8  && ((String)type.substring(0, 9)).equals("ArrayList")) {
				sourcecode += "\tprivate "+ type +  " " + name + " = new " + type +"();\n";
			}
			else {
				sourcecode += "\tprivate "+ type
						+ " "+ name + ";\n";
			}
		}
		for(int i = 0; i< fields.size(); i++) {
			String type = fields.get(i).get(1);
			String name = fields.get(i).get(0);
			String name_cap = name.substring(0,1).toUpperCase() + name.substring(1);
			
			
			
			if(type.length() > 8  && ((String)type.substring(0, 9)).equals("ArrayList")) {
				//public void setNames(int index, String n){
						//this.Names.set(index,n);
				//}
				sourcecode += "\tpublic void set"+name_cap+"(int index, "+(type.substring(10,type.length()-1))+" "+"n){\n";
				sourcecode += "\t\t" +"this."+ name + ".set(index,n);\n";
				sourcecode += "\t}\n\n";
				//public String getNames(int index){
				//		return this.name.get(index);
				//}
				sourcecode += "\tpublic "+ (type.substring(10,type.length()-1)) +" get"+name_cap+"(int index){\n";
				sourcecode += "\t\treturn this."+name+".get(index);\n";
				sourcecode += "\t}\n\n";
				//public void addNames(String n){
				//		this.Names.add(n);
				//
				sourcecode += "\tpublic void add"+name_cap+"("+(type.substring(10,type.length()-1))+" n){\n";
				sourcecode += "\t\tthis."+name+".add(n);\n";
				sourcecode += "\t}\n\n";
				//public int numNames(){
				//	return this.Names.size();
				//}
				sourcecode += "\tpublic int num"+name_cap+"(){\n";
				sourcecode += "\t\treturn this."+name+".size();\n";
				sourcecode += "\t}\n\n";
			}
			else {
				//public void setNames(int index, String n){
					//this.Names.set(index,n);
				//}
				sourcecode += "\tpublic void set"+name_cap+"("+type+" "+"n){\n";
				sourcecode += "\t\t" +"this."+ name + "=n;\n";
				sourcecode += "\t}\n\n";
				//public String getNames(int index){
				//		return this.name.get(index);
				//}
				sourcecode += "\tpublic "+ type +" get"+name_cap+"(){\n";
				sourcecode += "\t\treturn this."+name+";\n";
				sourcecode += "\t}\n\n";
				//public 
			}
		}
		sourcecode += tojson(classfields.get(className), className);
		sourcecode += "}";
		return sourcecode;
	}
	//tojson hllerp
	String tojson(List<List<String>> fields, String className) {
//		ArrayList<String>fieldNames = new ArrayList<String>();
//		for(int i = 0; i < fields.size();i++) {
//			fieldNames.add(fields.get(i).get(0));
//		}
		StringBuilder tojsonCode = new StringBuilder();
		
		 tojsonCode.append("\tpublic JSONObject toJSON() throws JSONException { \n");
		 tojsonCode.append("\t\tMap<Object,Integer> mp = new HashMap<Object,Integer> ();\n");
		 tojsonCode.append("\t\treturn this.helper(mp);\n");
		 tojsonCode.append("\t}\n");
		 tojsonCode.append("\tpublic JSONObject helper(Map<Object,Integer> mp) {\n");
		 tojsonCode.append("\t\tMap<Object,Integer> visited = mp;\n");
		 tojsonCode.append("\t\tif(visited.containsKey(this)){\n");
		 tojsonCode.append("\t\t\tJSONObject ref= new JSONObject();\n");
		 tojsonCode.append("\t\t\tref.put(\"ref\", visited.get(this));\n");
		 tojsonCode.append("\t\t\treturn ref;\n");
		 tojsonCode.append("\t\t}\n");
		 tojsonCode.append("\t\telse{\n");
		 tojsonCode.append("\t\t\tJSONObject jsonObject = new JSONObject();\n");
		 tojsonCode.append("\t\t\tint id = mp.size()+1;\n");
		 tojsonCode.append("\t\t\tvisited.put(this,id);\n");
		 tojsonCode.append("\t\t\tjsonObject.put(\"id\",id);\n");
		 tojsonCode.append("\t\t\tjsonObject.put(\"type\",\"").append(className).append("\");\n");
		 tojsonCode.append("\t\t\tJSONArray values = new JSONArray(); \n");
        for (List<String> field : fields) {
        	String fieldName = field.get(0);
        	String type = field.get(1);
        	if(type.length() > 8  && ((String)type.substring(0, 9)).equals("ArrayList")) {
        		if(!is_prim_cap(type.substring(10,type.length()-1))){
        			tojsonCode.append("\t\t\tJSONArray json_arr = new JSONArray();\n");
        			tojsonCode.append("\t\t\tfor(").append(type.substring(10,type.length()-1)).append(" item: ").append(fieldName).append("){\n");
        			tojsonCode.append("\t\t\t\tjson_arr.put(item.helper(visited));\n");
        			tojsonCode.append("\t\t\t}\n");
        			tojsonCode.append("\t\t\tJSONObject put_into_values = new JSONObject();\n");
        			tojsonCode.append("\t\t\tput_into_values.put(\"").append(fieldName).append("\", json_arr);\n");
        			tojsonCode.append("\t\t\tvalues.put(put_into_values);\n");
        		}
        		else {
        			tojsonCode.append("\t\t\tJSONArray arr").append(fieldName).append(" = new JSONArray();\n");
        			tojsonCode.append("\t\t\tJSONObject json").append(fieldName).append("= new JSONObject();\n");
        			tojsonCode.append("\t\t\tfor(").append(type.substring(10,type.length()-1)).append(" item: ").append(fieldName).append("){\n");
        			tojsonCode.append("\t\t\t\tarr").append(fieldName).append(".add(String.valueOf(item));\n");
        			tojsonCode.append("\t\t\t}\n");
        			tojsonCode.append("\t\t\tjson").append(fieldName).append(".put(\"").append(fieldName).append("\", arr").append(fieldName).append(");\n");
        			tojsonCode.append("\t\t\tvalues.put(json").append(fieldName).append(");\n");
        		}
        	}
        	else {
        		if(!is_prim(type)) {
        			tojsonCode.append("\t\t\tJSONObject json = this.").append(fieldName).append(".helper(visited);\n");
				tojsonCode.append("\t\t\tJSONObject json_").append(fieldName).append(" = new JSONObject();\n");
				tojsonCode.append("\t\t\tjson_").append(fieldName).append(".put(\"").append(fieldName).append("\",json);\n");
        			tojsonCode.append("\t\t\tvalues.put(json_").append(fieldName).append(");\n");
    
        		}
        		else {
        			tojsonCode.append("\t\t\tJSONObject new_json").append(fieldName).append("= new JSONObject();\n");
        			tojsonCode.append("\t\t\tnew_json").append(fieldName).append(".put(\"").append(fieldName).append("\", String.valueOf(this.").append(fieldName).append("));\n");
        			tojsonCode.append("\t\t\tvalues.put(new_json").append(fieldName).append(");\n");	
        		} 
        	}	
        }
        tojsonCode.append("\t\t\tjsonObject.put(\"values\",values);\n");
    	tojsonCode.append("\t\t\treturn jsonObject;\n");
    	tojsonCode.append("\t\t}\n");
    	tojsonCode.append("\t}\n");
		return tojsonCode.toString();
	}
	//create all class
	void createAllClasses(String basePath) {
		if(classes.isEmpty() || classfields.isEmpty()) {
			System.out.println("Invalid Opertation");
			System.exit(1);
		}
		if(pkg == null) {
			String path = "";
			String ds_path =basePath +"/"+"Deserializer.java";;
			for(int i = 0 ; i < classes.size(); i++) {
				String classname = classes.get(i);
				//List<List<String>> fields = classfields.get(classname);
				path =  basePath + "/" + classname + ".java";
				//ds_path = basePath + "/"+classname+"Deserializer.java";
				//System.out.println(path);
				
				File f = new File(path);
				
				try {
					f.createNewFile();
					try {
						FileOutputStream out = new FileOutputStream(f);
						String txt = getSourceCode(classname);
						byte buy[] = txt.getBytes();
						out.write(buy);
						out.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
			//create deserialization java
			System.out.println(ds_path+":::ds_path");
			File ds_f = new File(ds_path);
			
			try {
				ds_f.createNewFile();
				try {
					FileOutputStream out = new FileOutputStream(ds_f);
					String txt = Deserializer();
					byte buy[] = txt.getBytes();
					out.write(buy);
					out.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		
		}
		else {
			String path = basePath + "/";
			String ds_path = basePath + "/";
			String[] path_word = pkg.split("\\.");
			for(int k =0; k<path_word.length; k++) {
				path += path_word[k]+"/";
				ds_path += path_word[k] + "/";
			}
			for(int i = 0 ; i < classes.size(); i++) {
				String classname = classes.get(i);
				
				String real_path = path  + classname + ".java";
				//String real_ds_path  = ds_path +classname+ "Deserializer.java";
				System.out.println(real_path);
				File f = new File(real_path);
				try {
					f.createNewFile();
					try {
						FileOutputStream out = new FileOutputStream(f);
						String txt = getSourceCode(classname);
						byte buy[] = txt.getBytes();
						out.write(buy);
						out.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			ds_path += "/Deserializer.java";
			System.out.println(ds_path+":::ds_path");
			File ds_f = new File(ds_path);
			try {
				ds_f.createNewFile();
				try {
					FileOutputStream out = new FileOutputStream(ds_f);
					String txt = Deserializer();
					byte buy[] = txt.getBytes();
					out.write(buy);
					out.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		
		}
			
	}
	//conver e:something
	String convert(JSONObject jobj) {
		String type;
		if(jobj.optJSONObject("e") != null) {
			JSONObject obj = jobj;
			String left = "ArrayList<";
			String right = ">";
			String no_prim_type = obj.getString("e");
			String prim_type;
			if(no_prim_type.equals("byte") || no_prim_type.equals("int") || no_prim_type.equals("long") ||no_prim_type.equals("short")) {
				prim_type = "Integer";
			}
			else if(no_prim_type.equals("boolean")) {
				prim_type = "Boolean"; 
			}
			else if(no_prim_type.equals("char")) {
				prim_type = "Character";
			}
			else if(no_prim_type.equals("float") || no_prim_type.equals("double")) {
				prim_type = "Float";
			}
			else {
				prim_type = no_prim_type;
			}
			while(obj.optJSONObject("e")!= null) {
				left = left+"Collection<" ;
				right = right + ">"; 
				obj = obj.getJSONObject("e");
			}
			
			type = left + prim_type+ right;
		}
		else {
			JSONObject obj = jobj;
			String no_prim_type = obj.getString("e");
			String prim_type = no_prim_type;
			if(no_prim_type.equals("byte") || no_prim_type.equals("int") || no_prim_type.equals("long") ||no_prim_type.equals("short")) {
				prim_type = "Integer";
			}
			if(no_prim_type.equals("boolean")) {
				prim_type = "Boolean"; 
			}
			if(no_prim_type.equals("char")) {
				prim_type = "Character";
			}
			if(no_prim_type.equals("float") || no_prim_type.equals("double")) {
				prim_type = "Float";
			}
			type = "ArrayList<"+prim_type+">";
		}
		return type;
		
	}
	//check prim
	boolean is_prim_cap(String value){
		if((value.equals("String")) || ( value.equals("Integer")) || (value.equals("Float")) || (value .equals("Boolean"))|| (value.equals("Character"))){
			return true;
		}
		else{
			return false;
		}
	}
	
	boolean is_prim(String value){
		if((value.equals("String")) || ( value.equals("int")) || ( value.equals("byte")) ||( value.equals("long")) ||( value.equals("short")) ||(value.equals("char")) || (value .equals("double"))||(value .equals("boolean"))|| (value.equals("float"))){
			return true;
		}
		else{
			return false;
		}
	}
	//deserailzaer class
	public String Deserializer() {
		StringBuilder ds = new StringBuilder();
		if(pkg!=null) {
			ds.append("package "+pkg).append(";\n\n");
		}
		ds.append("import java.io.*;\n");
		ds.append("import java.util.*;\n");
		ds.append("import org.json.JSONObject;\n");
		ds.append("import org.json.JSONArray;");
		ds.append("import org.json.JSONException;\n\n");
		ds.append("public class Deserializer{\n");
		for(int i = 0; i < classes.size(); i++) {
			String classname = classes.get(i);
			List<List<String>> fields = classfields.get(classname);
			ds.append(Deserializer_helper(classname,fields));
		}
		ds.append("\n");
		ds.append("}\n");
		return ds.toString();
	}
	public String Deserializer_helper(String classname, List<List<String>> fields) {
		StringBuilder ds = new StringBuilder(); 
		
		ds.append("\tpublic static ").append(classname).append(" read").append(classname).append("(JSONObject js) throws JSONException{\n");
		ds.append("\t\tMap<Integer, Object> mp = new HashMap<Integer,Object>();\n");
		ds.append("\t\treturn ").append(classname).append("helper(mp,js);\n");
		ds.append("\t}\n");
		ds.append("\tpublic static ").append(classname).append(" ").append(classname).append("helper(Map<Integer,Object> visited, JSONObject js){\n");
		ds.append("\t\t").append(classname).append(" ").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(" = new ").append(classname).append("();\n");
		ds.append("\t\tInteger id;\n");
		ds.append("\t\tif(js.has(\"ref\")){\n");
		ds.append("\t\t\tid = js.getInt(\"ref\");\n");
		ds.append("\t\t\treturn (").append(classname).append(") visited.get(id);\n");
		ds.append("\t\t}\n");
		ds.append("\t\telse{\n");
		ds.append("\t\t\tid = js.getInt(\"id\");\n");
		ds.append("\t\t}\n");
		ds.append("\t\tif(visited.containsKey(id)){\n");
		ds.append("\t\t\treturn (").append(classname).append(") visited.get(id);\n"); 
		ds.append("\t\t}\n");
		ds.append("\t\telse{\n");
		ds.append("\t\t\tvisited.put(id,").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(");\n");
		ds.append("\t\t\tJSONArray values = (JSONArray) js.get(\"values\");\n");
		for(int i = 0; i < fields.size(); i++) {
			List<String> field = fields.get(i);
        	String fieldName = field.get(0);
        	String type = field.get(1);
        	if(type.length() > 8  && ((String)type.substring(0, 9)).equals("ArrayList")) {
        		if(!is_prim_cap(type.substring(10,type.length()-1))) {
        			ds.append("\t\t\tJSONArray ").append(fieldName).append("_arr= values.getJSONObject(").append(i).append(")").append(".getJSONArray(\"").append(fieldName).append("\");\n");// first get jsonobject then get jsonarray
        			ds.append("\t\t\tfor(int i = 0; i< ").append(fieldName).append("_arr.length(); i++){\n");
        			ds.append("\t\t\t\t").append(type.substring(10,type.length()-1)).append(" item = ").append(type.substring(10,type.length()-1)).append("helper(visited,").append(fieldName).append("_arr.getJSONObject(i));\n");
        			ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("((").append(type.substring(10,type.length()-1)).append(") item);\n");
        			ds.append("\t\t\t}\n"); 
        		}  
        		else {  
        			ds.append("\t\t\tJSONArray ").append(fieldName).append("_list = values.getJSONObject(").append(i).append(")").append(",getJSONArray(\"").append(fieldName).append("\");\n");
        			ds.append("\t\t\tfor(int i = 0; i < ").append(fieldName).append("_list.length(); i++){\n");
        			if(type.substring(10,type.length()-1).equals("int")) {
        				ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append(type.substring(10,type.length()-1).substring(0,1).toUpperCase() + type.substring(10,type.length()-1).substring(1)).append("Integer.parseInt((String)").append(fieldName).append("_list.get(i))").append("));\n");
        			}
        			else if(type.substring(10,type.length()-1).equals("float")){
        				ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append(type.substring(10,type.length()-1).substring(0,1).toUpperCase() + type.substring(10,type.length()-1).substring(1)).append("Float.parseFloat((String)").append(fieldName).append("_list.get(i))").append("));\n");
        			}
        			else if(type.substring(10,type.length()-1).equals("double")){
        				ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append(type.substring(10,type.length()-1).substring(0,1).toUpperCase() + type.substring(10,type.length()-1).substring(1)).append("Double.parseDouble((String)").append(fieldName).append("_list.get(i))").append("));\n");
        			}
        			else if(type.substring(10,type.length()-1).equals("long")) {
        				ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append(type.substring(10,type.length()-1).substring(0,1).toUpperCase() + type.substring(10,type.length()-1).substring(1)).append("Long.parseLong((String)").append(fieldName).append("_list.get(i))").append("));\n");
        			}
        			else if(type.substring(10,type.length()-1).equals("short")) {
        				ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append(type.substring(10,type.length()-1).substring(0,1).toUpperCase() + type.substring(10,type.length()-1).substring(1)).append("Short.parseShort((String)").append(fieldName).append("_list.get(i))").append("));\n");
        			}
        			else if(type.substring(10,type.length()-1).equals("byte")){
        				ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append(type.substring(10,type.length()-1).substring(0,1).toUpperCase() + type.substring(10,type.length()-1).substring(1)).append("Byte.parseByte((String)").append(fieldName).append("_list.get(i))").append("));\n");
        			}
        			else if(type.substring(10,type.length()-1).equals("Boolean")) {
        				ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append(type.substring(10,type.length()-1).substring(0,1).toUpperCase() + type.substring(10,type.length()-1).substring(1)).append("Boolean.parseBoolean((String)").append(fieldName).append("_list.get(i))").append("));\n");
        			}
        			else if(type.substring(10,type.length()-1).equals("char")) {
        				ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append(type.substring(10,type.length()-1).substring(0,1).toUpperCase() + type.substring(10,type.length()-1).substring(1)).append("(((String)").append(fieldName).append("_list.get(i)).charAt(0))").append("));\n");
        			}
        			else {
        				ds.append("\t\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".add").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append(type.substring(10,type.length()-1).substring(0,1).toUpperCase() + type.substring(10,type.length()-1).substring(1)).append("(String)").append(fieldName).append("_list.get(i))").append("));\n");
        			}
        			ds.append("\t\t\t}\n");
        		}
        	} 
        	else {
        		if(!is_prim(type)) {
        			ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = ").append(type).append("helper(visited,values.getJSONObject(").append(i).append(").getJSONObject(\"").append(fieldName).append("\"));\n");
        			ds.append("\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".set").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append("new_").append(fieldName).append(");\n");
        		}
        		else {
        			if(type.equals("int")) {
        				ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = Integer.parseInt((String)(values.getJSONObject(").append(i).append(")").append(".get(\"").append(fieldName).append("\")));\n");
        			}
        			else if(type.equals("long")){
        				ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = Long.parseLong((String)(values.getJSONObject(").append(i).append(")").append(".get(\"").append(fieldName).append("\")));\n");
        			}
        			else if(type.equals("short")) {
        				ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = Short.parseShort((String)(values.getJSONObject(").append(i).append(")").append(".get(\"").append(fieldName).append("\")));\n");
        			}
        			else if(type.equals("double")) {
        				ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = Double.parseDouble((String)(values.getJSONObject(").append(i).append(")").append(".get(\"").append(fieldName).append("\")));\n");
        			}
        			else if(type.equals("boolean")) {
        				ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = Boolean.parseBoolean((String)(values.getJSONObject(").append(i).append(")").append(".get(\"").append(fieldName).append("\")));\n");
        			}
        			else if(type.equals("float")) {
        				ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = Float.parseFloat((String)(values.getJSONObject(").append(i).append(")").append(".get(\"").append(fieldName).append("\")));\n");
        			}
        			else if(type.equals("byte")) {
        				ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = Byte.parseByte((String)(values.getJSONObject(").append(i).append(")").append(".get(\"").append(fieldName).append("\")));\n");
        			}
        			else if(type.equals("char")) {
        				ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = (((String)(values.getJSONObject(").append(i).append(")").append(".get(\"").append(fieldName).append("\"))).charAt(0));\n");
        			}
        			else {
        				ds.append("\t\t\t").append(type).append(" new_").append(fieldName).append(" = ((String)(values.getJSONObject(").append(i).append(")").append(".get(\"").append(fieldName).append("\")));\n");
        			}
        			ds.append("\t\t\t").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(".set").append(fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)).append("(").append("new_").append(fieldName).append(");\n");
        		}
        	}
		} 
		ds.append("\t\t}\n");
		
		ds.append("\t\treturn ").append(classname.substring(0,1).toLowerCase() + classname.substring(1)).append(";\n");
		ds.append("\t}\n\n");
		
		return ds.toString();
	}
	
	
	public static void main(String args[]) throws FileNotFoundException{
		InputStream input = new FileInputStream("/Users/shulinxiang/Desktop/ece651-hwk1/array.json");
		ClassBuilder cb = new ClassBuilder(input);
		//System.out.println(cb.getSourceCode("Faculty"));
		cb.createAllClasses("/Users/shulinxiang/Desktop/ece651-hwk1");
		//System.out.println(cb.Deserializer());
	}
}
