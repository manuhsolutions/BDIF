package code.bdif.transform.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LabelandPredictorsMerge {
	
	private static String CLASIFIER_DELEMETER;
	private static String TRAINING_DELEMETER;
	private static String CONCEPT_DELEMETER;
	private static String CLASIFER_PATH;
	private static String TRAINING_PATH;
	private static String CONCEPT_PATH;
	private static String OUTPUT;
	
	public static void main(String[] args) {
		if(args.length > 1){
			merge(args[0],args[1]);
		}else{
			merge(args[0],"");
		}

	}
	
	public static void merge(String clasifer_path,String concept_path){
		HashMap<String,String> tr_map = new HashMap<String,String>();
		HashMap<String,String[]> cla_map = new HashMap<String,String[]>();
		HashMap<String,String> con_map = new HashMap<String,String>();
		BufferedReader tr_read = null;
		BufferedReader cla_read = null;
		BufferedReader con_read = null;
		try {						
			CLASIFIER_DELEMETER = "\t";
			TRAINING_DELEMETER = ",";
			CONCEPT_DELEMETER = "\t";
			CLASIFER_PATH = clasifer_path;
			TRAINING_PATH = "temp.csv";
			CONCEPT_PATH = concept_path;
			OUTPUT = "result";
			
			System.out.println("Process Started......");
			String trCurrentLine;
			String claCurrentLine;
			String conCurrentLine;
			File dir = new File(OUTPUT);
			dir.mkdirs();
			String[]files = dir.list();
			for(String file: files){
			    File currentFile = new File(dir.getPath(),file);
			    currentFile.delete();
			}
			tr_read = new BufferedReader(new FileReader(TRAINING_PATH));
			cla_read = new BufferedReader(new FileReader(CLASIFER_PATH));			
			while ((trCurrentLine = tr_read.readLine()) != null) {
				int index = trCurrentLine.indexOf(TRAINING_DELEMETER);	
				tr_map.put(trCurrentLine.substring(0,index), trCurrentLine.substring(index,trCurrentLine.length()));				
			}
			while ((claCurrentLine = cla_read.readLine()) != null) {				
				int index = claCurrentLine.indexOf(CLASIFIER_DELEMETER);
				String arr[] = claCurrentLine.substring(index,claCurrentLine.length()).trim().split(CLASIFIER_DELEMETER);				
				cla_map.put(claCurrentLine.substring(0,index),arr);				
			}	
			if(CONCEPT_PATH != null && CONCEPT_PATH.length() > 0){
				con_read = new BufferedReader(new FileReader(CONCEPT_PATH));
				while ((conCurrentLine = con_read.readLine()) != null) {
					int index = conCurrentLine.indexOf(CONCEPT_DELEMETER);	
					con_map.put(conCurrentLine.substring(0,index), conCurrentLine.substring(index,conCurrentLine.length()).trim());				
				}
			}else{
				con_map.put("0", "default");
			}
			for (Map.Entry<String, String> concept : con_map.entrySet()) {			
				createMergeFile(concept.getValue(),cla_map,tr_map,Integer.parseInt(concept.getKey()));
			}
			System.out.println("Process completed Sucessfully");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (tr_read != null)tr_read.close();
				if (cla_read != null)tr_read.close();
				if (con_read != null)tr_read.close();
				File temp = new File(TRAINING_PATH);
				if(temp.exists()) {
					temp.delete();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void createMergeFile(String fileName,HashMap<String,String[]> cla_map,HashMap<String,String> tr_map,int index){		
		try{
			System.out.println("Creating File "+fileName+".csv .......");
			File dir = new File(OUTPUT);			
			File file = new File(dir, fileName+".csv");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (Map.Entry<String,String[]> traning : cla_map.entrySet()) {
				String jpeg_name = traning.getKey();
				String strt_index = traning.getValue()[index];					
				for (Map.Entry<String,String> clasifier : tr_map.entrySet()) {
					if(clasifier.getKey().contains(jpeg_name)){
							bw.write(strt_index+clasifier.getValue());
							bw.write("\n");
							break;
						}						
				}
			}
			System.out.println("File "+fileName+".csv Created Sucessfully");
			bw.close();
		}catch (IOException e) {
			e.printStackTrace();
		} 
	}
}