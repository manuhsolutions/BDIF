package code.bdif.transform.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.util.Utf8;

import code.bdif.data.avro.Datacore;
import code.bdif.data.avro.Features;
import code.bdif.data.avro.io.BDASRecord;
import code.bdif.data.avro.io.GraphlabRecord;
import code.bdif.data.avro.io.JavaCVRecord;
import code.bdif.data.avro.io.LireRecord;
import code.bdif.data.avro.io.MahoutRecord;

public class Mapper {
	static final String CLASS_LABEL ="class_label";
	public static MahoutRecord fromMahout(LireRecord lr,String oprType,String resultPath, String clasiferdata,String conceptdata) {
		MahoutRecord mahoutRecord = new MahoutRecord();
		Datacore datacore = new Datacore();		
		File file = new File(lr.getAvroFileName());		
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Features().getSchema());
		File avroFile = new File(resultPath+"datacore.avro");
		DatumWriter<Datacore> ImageDatumWriter = new SpecificDatumWriter<Datacore>(Datacore.class);
		DataFileWriter<Datacore> dataFileWriter = new DataFileWriter<Datacore>(ImageDatumWriter);		
		try {
			BufferedWriter temp = new BufferedWriter(new FileWriter("temp.csv"));
			dataFileWriter.create(new Datacore().getSchema(), avroFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord feature = null;
			while (dataFileReader.hasNext()) {
				feature = dataFileReader.next(feature);							
				datacore.setVector(feature.get("featuredata"));
				if(feature.get("labeldesc")!=null && feature.get("labeldesc").toString().length()>0)
					datacore.setLabelname(feature.get("labeldesc").toString());	
				else
					datacore.setLabelname("");
				if(oprType.equalsIgnoreCase("Classification"))
					datacore.setOperationtype(Constants.CLASSIFICATION);
				else
					datacore.setOperationtype(Constants.CLUSTERING);
				datacore.setFiledesc("");
				datacore.setFulldesc("");
				datacore.setLabeldesc("");				
				datacore.setLookupfieldpos(-1);				
				dataFileWriter.append(datacore);	
				temp.write(feature.get("filename")+","+feature.get("featuredata").toString().trim().replaceAll("\\s+",",")+"\n");
			}
			dataFileWriter.close();
			temp.close();
			BufferedReader temp_read = new BufferedReader(new FileReader("temp.csv"));
			String firstLine = temp_read.readLine();			
			int count = (firstLine.split(",").length)-1;
			StringBuffer header = new StringBuffer(CLASS_LABEL);
			StringBuffer predictors = new StringBuffer();
			temp_read.close();
			for(int i=0 ; i<count;i++){
				header.append(",field"+(i+1));
				predictors.append("field"+(i+1)+" ");
			}
			
			header.append("\n");
			LabelandPredictorsMerge.merge(clasiferdata,conceptdata);
			String fileContent;
			File currentFile;
			FileWriter fooWriter;
			File dir = new File("result");			
			String[]files = dir.list();
			for(String file1: files){
			    currentFile = new File(dir.getPath(),file1);
			    fileContent = new String(Files.readAllBytes(currentFile.toPath()), StandardCharsets.UTF_8);
			    fooWriter = new FileWriter(currentFile, false);
			    fooWriter.write(header.toString()+fileContent);
			    fooWriter.close();
			    fileContent = "";
			    generateShellScript(predictors.toString(),file1);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		mahoutRecord.setAvroFileName(resultPath+"datacore.avro");
		return mahoutRecord;
	}
	public static MahoutRecord fromMahout(JavaCVRecord lr,String oprType,String resultPath, String clasiferdata,String conceptdata) {
		MahoutRecord mahoutRecord = new MahoutRecord();
		Datacore datacore = new Datacore();		
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Features().getSchema());
		File avroFile = new File(resultPath+"datacore.avro");
		DatumWriter<Datacore> ImageDatumWriter = new SpecificDatumWriter<Datacore>(Datacore.class);
		DataFileWriter<Datacore> dataFileWriter = new DataFileWriter<Datacore>(ImageDatumWriter);		
		try {
			BufferedWriter temp = new BufferedWriter(new FileWriter("temp.csv"));
			dataFileWriter.create(new Datacore().getSchema(), avroFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord feature = null;
			while (dataFileReader.hasNext()) {
				feature = dataFileReader.next(feature);							
				datacore.setVector(feature.get("featuredata"));
				if(feature.get("labeldesc")!=null && feature.get("labeldesc").toString().length()>0)
					datacore.setLabelname(feature.get("labeldesc").toString());	
				else
					datacore.setLabelname("");
				if(oprType.equalsIgnoreCase("Classification"))
					datacore.setOperationtype(Constants.CLASSIFICATION);
				else
					datacore.setOperationtype(Constants.CLUSTERING);
				datacore.setFiledesc("");
				datacore.setFulldesc("");
				datacore.setLabeldesc("");				
				datacore.setLookupfieldpos(-1);
				dataFileWriter.append(datacore);
				temp.write(feature.get("filename")+","+feature.get("featuredata").toString().trim().replaceAll("\\s+",",")+"\n");
			}
			dataFileWriter.close();
			temp.close();
			BufferedReader temp_read = new BufferedReader(new FileReader("temp.csv"));
			String firstLine = temp_read.readLine();			
			int count = (firstLine.split(",").length)-1;
			StringBuffer header = new StringBuffer(CLASS_LABEL);
			temp_read.close();
			for(int i=0 ; i<count;i++){
				header.append(",feild"+(i+1));
			}
			header.append("\n");
			LabelandPredictorsMerge.merge(clasiferdata,conceptdata);
			String fileContent;
			File currentFile;
			FileWriter fooWriter;
			File dir = new File("result");			
			String[]files = dir.list();
			for(String file1: files){
			    currentFile = new File(dir.getPath(),file1);
			    fileContent = new String(Files.readAllBytes(currentFile.toPath()), StandardCharsets.UTF_8);
			    fooWriter = new FileWriter(currentFile, false);
			    fooWriter.write(header.toString()+fileContent);
			    fooWriter.close();
			    fileContent = "";		       
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mahoutRecord.setAvroFileName(resultPath+"datacore.avro");
		return mahoutRecord;
	}	
	public static MahoutRecord fromMahout(GraphlabRecord lr,String oprType,String resultPath, String clasiferdata,String conceptdata) {
		MahoutRecord mahoutRecord = new MahoutRecord();
		Datacore datacore = new Datacore();		
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Features().getSchema());
		File avroFile = new File(resultPath+"datacore.avro");
		DatumWriter<Datacore> ImageDatumWriter = new SpecificDatumWriter<Datacore>(Datacore.class);
		DataFileWriter<Datacore> dataFileWriter = new DataFileWriter<Datacore>(ImageDatumWriter);		
		try {
			BufferedWriter temp = new BufferedWriter(new FileWriter("temp.csv"));
			dataFileWriter.create(new Datacore().getSchema(), avroFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord feature = null;
			while (dataFileReader.hasNext()) {
				feature = dataFileReader.next(feature);							
				datacore.setVector(feature.get("featuredata"));
				if(feature.get("labeldesc")!=null && feature.get("labeldesc").toString().length()>0)
					datacore.setLabelname(feature.get("labeldesc").toString());	
				else
					datacore.setLabelname("");	
				if(oprType.equalsIgnoreCase("Classification"))
					datacore.setOperationtype(Constants.CLASSIFICATION);
				else
					datacore.setOperationtype(Constants.CLUSTERING);
				datacore.setFiledesc("");
				datacore.setFulldesc("");
				datacore.setLabeldesc("");				
				datacore.setLookupfieldpos(-1);
				dataFileWriter.append(datacore);	
				temp.write(feature.get("filename")+","+feature.get("featuredata").toString().trim().replaceAll("\\s+",",")+"\n");
			}
			dataFileWriter.close();
			temp.close();
			BufferedReader temp_read = new BufferedReader(new FileReader("temp.csv"));
			String firstLine = temp_read.readLine();			
			int count = (firstLine.split(",").length)-1;
			StringBuffer header = new StringBuffer(CLASS_LABEL);
			temp_read.close();
			for(int i=0 ; i<count;i++){
				header.append(",feild"+(i+1));
			}
			header.append("\n");
			LabelandPredictorsMerge.merge(clasiferdata,conceptdata);
			String fileContent;
			File currentFile;
			FileWriter fooWriter;
			File dir = new File("result");			
			String[]files = dir.list();
			for(String file1: files){
			    currentFile = new File(dir.getPath(),file1);
			    fileContent = new String(Files.readAllBytes(currentFile.toPath()), StandardCharsets.UTF_8);
			    fooWriter = new FileWriter(currentFile, false);
			    fooWriter.write(header.toString()+fileContent);
			    fooWriter.close();
			    fileContent = "";		       
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mahoutRecord.setAvroFileName(resultPath+"datacore.avro");
		return mahoutRecord;
	}
	
	public static BDASRecord fromBdas(LireRecord lr,String oprType,String resultPath, String clasiferdata,String conceptdata) {
		BDASRecord bDASRecord = new BDASRecord();
		Datacore datacore = new Datacore();		
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Features().getSchema());
		File avroFile = new File(resultPath+"datacore.avro");
		DatumWriter<Datacore> ImageDatumWriter = new SpecificDatumWriter<Datacore>(Datacore.class);
		DataFileWriter<Datacore> dataFileWriter = new DataFileWriter<Datacore>(ImageDatumWriter);		
		try {
			BufferedWriter temp = new BufferedWriter(new FileWriter("temp.csv"));
			dataFileWriter.create(new Datacore().getSchema(), avroFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord feature = null;
			while (dataFileReader.hasNext()) {
				feature = dataFileReader.next(feature);
				datacore.setVector(feature.get("featuredata"));
				if(feature.get("labeldesc")!=null && feature.get("labeldesc").toString().length()>0)
					datacore.setLabelname(feature.get("labeldesc").toString());	
				else
					datacore.setLabelname("");
				if(oprType.equalsIgnoreCase("Classification"))
					datacore.setOperationtype(Constants.CLASSIFICATION);
				else
					datacore.setOperationtype(Constants.CLUSTERING);
				datacore.setFiledesc("");
				datacore.setFulldesc("");
				datacore.setLabeldesc("");				
				datacore.setLookupfieldpos(-1);
				dataFileWriter.append(datacore);	
				temp.write(feature.get("filename")+","+feature.get("featuredata").toString().trim().replaceAll("\\s+",",")+"\n");
			}
			dataFileWriter.close();
			temp.close();
			LabelandPredictorsMerge.merge(clasiferdata,conceptdata);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bDASRecord.setAvroFileName(resultPath+"datacore.avro");
		return bDASRecord;		
	}
	
	public static BDASRecord fromBdas(JavaCVRecord lr,String oprType,String resultPath, String clasiferdata,String conceptdata) {
		BDASRecord bDASRecord = new BDASRecord();
		Datacore datacore = new Datacore();		
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Features().getSchema());
		File avroFile = new File(resultPath+"datacore.avro");
		DatumWriter<Datacore> ImageDatumWriter = new SpecificDatumWriter<Datacore>(Datacore.class);
		DataFileWriter<Datacore> dataFileWriter = new DataFileWriter<Datacore>(ImageDatumWriter);		
		try {
			BufferedWriter temp = new BufferedWriter(new FileWriter("temp.csv"));
			dataFileWriter.create(new Datacore().getSchema(), avroFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord feature = null;
			while (dataFileReader.hasNext()) {
				feature = dataFileReader.next(feature);							
				datacore.setVector(feature.get("featuredata"));
				if(feature.get("labeldesc")!=null && feature.get("labeldesc").toString().length()>0)
					datacore.setLabelname(feature.get("labeldesc").toString());	
				else
					datacore.setLabelname("");	
				if(oprType.equalsIgnoreCase("Classification"))
					datacore.setOperationtype(Constants.CLASSIFICATION);
				else
					datacore.setOperationtype(Constants.CLUSTERING);
				datacore.setFiledesc("");
				datacore.setFulldesc("");
				datacore.setLabeldesc("");				
				datacore.setLookupfieldpos(-1);
				dataFileWriter.append(datacore);	
				temp.write(feature.get("filename")+","+feature.get("featuredata").toString().trim().replaceAll("\\s+",",")+"\n");
			}
			dataFileWriter.close();
			temp.close();
			LabelandPredictorsMerge.merge(clasiferdata,conceptdata);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bDASRecord.setAvroFileName(resultPath+"datacore.avro");
		return bDASRecord;
	}
	
	public static BDASRecord fromBdas(GraphlabRecord lr,String oprType,String resultPath, String clasiferdata,String conceptdata) {
		BDASRecord bDASRecord = new BDASRecord();
		Datacore datacore = new Datacore();		
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Features().getSchema());
		File avroFile = new File(resultPath+"datacore.avro");
		DatumWriter<Datacore> ImageDatumWriter = new SpecificDatumWriter<Datacore>(Datacore.class);
		DataFileWriter<Datacore> dataFileWriter = new DataFileWriter<Datacore>(ImageDatumWriter);		
		try {
			BufferedWriter temp = new BufferedWriter(new FileWriter("temp.csv"));
			dataFileWriter.create(new Datacore().getSchema(), avroFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord feature = null;
			while (dataFileReader.hasNext()) {
				feature = dataFileReader.next(feature);							
				datacore.setVector(feature.get("featuredata"));
				if(feature.get("labeldesc")!=null && feature.get("labeldesc").toString().length()>0)
					datacore.setLabelname(feature.get("labeldesc").toString());	
				else
					datacore.setLabelname("");	
				if(oprType.equalsIgnoreCase("Classification"))
					datacore.setOperationtype(Constants.CLASSIFICATION);
				else
					datacore.setOperationtype(Constants.CLUSTERING);
				datacore.setFiledesc("");
				datacore.setFulldesc("");
				datacore.setLabeldesc("");				
				datacore.setLookupfieldpos(-1);
				dataFileWriter.append(datacore);	
				temp.write(feature.get("filename")+","+feature.get("featuredata").toString().trim().replaceAll("\\s+",",")+"\n");
			}
			dataFileWriter.close();
			temp.close();
			LabelandPredictorsMerge.merge(clasiferdata,conceptdata);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bDASRecord.setAvroFileName(resultPath+"datacore.avro");
		return bDASRecord;
	}
	public static LireRecord fromLire(MahoutRecord lr,String oprType,String resultPath) {
		LireRecord lireRecord = new LireRecord();
		Features feature = new Features();
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Datacore().getSchema());
		File lireFile = new File(resultPath+"feature.avro");
		DatumWriter<Features> ImageDatumWriter = new SpecificDatumWriter<Features>(Features.class);
		DataFileWriter<Features> dataFileWriter = new DataFileWriter<Features>(ImageDatumWriter);
		try {
			dataFileWriter.create(new Features().getSchema(), lireFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord datacore = null;
			while (dataFileReader.hasNext()) {
				datacore = dataFileReader.next(datacore);	
				feature.setLabeldesc("");
				feature.setLabelpos("");
				feature.setFeaturedata(((Utf8) datacore.get("vector")).toString());
				feature.setFilename("");	
				feature.setSource("mahout");				
				dataFileWriter.append(feature);				
			}
			dataFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lireRecord.setAvroFileName(resultPath+"feature.avro");
		return lireRecord;
	}
	public static LireRecord fromLire(BDASRecord lr,String oprType,String resultPath) {
		LireRecord lireRecord = new LireRecord();
		Features feature = new Features();
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Datacore().getSchema());
		File lireFile = new File(resultPath+"feature.avro");
		DatumWriter<Features> ImageDatumWriter = new SpecificDatumWriter<Features>(Features.class);
		DataFileWriter<Features> dataFileWriter = new DataFileWriter<Features>(ImageDatumWriter);
		try {
			dataFileWriter.create(new Features().getSchema(), lireFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord datacore = null;
			while (dataFileReader.hasNext()) {
				datacore = dataFileReader.next(datacore);
				feature.setLabeldesc("");
				feature.setLabelpos("");
				feature.setFeaturedata(((Utf8) datacore.get("vector")).toString());
				feature.setFilename("");	
				feature.setSource("bdas");				
				dataFileWriter.append(feature);				
			}
			dataFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lireRecord.setAvroFileName(resultPath+"feature.avro");
		return lireRecord;
	}
	public static JavaCVRecord fromJavacv(MahoutRecord lr,String oprType,String resultPath) {
		JavaCVRecord javaCVRecord = new JavaCVRecord();
		Features feature = new Features();
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Datacore().getSchema());
		File lireFile = new File(resultPath+"feature.avro");
		DatumWriter<Features> ImageDatumWriter = new SpecificDatumWriter<Features>(Features.class);
		DataFileWriter<Features> dataFileWriter = new DataFileWriter<Features>(ImageDatumWriter);
		try {
			dataFileWriter.create(new Features().getSchema(), lireFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord datacore = null;
			while (dataFileReader.hasNext()) {
				datacore = dataFileReader.next(datacore);
				feature.setLabeldesc("");
				feature.setLabelpos("");
				feature.setFeaturedata(((Utf8) datacore.get("vector")).toString());
				feature.setFilename("");	
				feature.setSource("mahout");				
				dataFileWriter.append(feature);				
			}
			dataFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		javaCVRecord.setAvroFileName(resultPath+"feature.avro");
		return javaCVRecord;
	}
	public static JavaCVRecord fromJavacv(BDASRecord lr,String oprType,String resultPath) {
		JavaCVRecord javaCVRecord = new JavaCVRecord();
		Features feature = new Features();
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Datacore().getSchema());
		File lireFile = new File(resultPath+"feature.avro");
		DatumWriter<Features> ImageDatumWriter = new SpecificDatumWriter<Features>(Features.class);
		DataFileWriter<Features> dataFileWriter = new DataFileWriter<Features>(ImageDatumWriter);
		try {
			dataFileWriter.create(new Features().getSchema(), lireFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord datacore = null;
			while (dataFileReader.hasNext()) {
				datacore = dataFileReader.next(datacore);
				feature.setLabeldesc("");
				feature.setLabelpos("");
				feature.setFeaturedata(((Utf8) datacore.get("vector")).toString());
				feature.setFilename("");	
				feature.setSource("bdas");				
				dataFileWriter.append(feature);				
			}
			dataFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		javaCVRecord.setAvroFileName(resultPath+"feature.avro");
		return javaCVRecord;
	}
	public static GraphlabRecord fromGraphLab(MahoutRecord lr,String oprType,String resultPath) {
		GraphlabRecord graphlabRecord = new GraphlabRecord();
		Features feature = new Features();
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Datacore().getSchema());
		File lireFile = new File(resultPath+"feature.avro");
		DatumWriter<Features> ImageDatumWriter = new SpecificDatumWriter<Features>(Features.class);
		DataFileWriter<Features> dataFileWriter = new DataFileWriter<Features>(ImageDatumWriter);
		try {
			dataFileWriter.create(new Features().getSchema(), lireFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord datacore = null;
			while (dataFileReader.hasNext()) {
				datacore = dataFileReader.next(datacore);	
				feature.setLabeldesc("");
				feature.setLabelpos("");
				feature.setFeaturedata(((Utf8) datacore.get("vector")).toString());
				feature.setFilename("");	
				feature.setSource("mahout");				
				dataFileWriter.append(feature);				
			}
			dataFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		graphlabRecord.setAvroFileName(resultPath+"feature.avro");
		return graphlabRecord;
	}
	public static GraphlabRecord fromGraphLab(BDASRecord lr,String oprType,String resultPath) {
		GraphlabRecord graphlabRecord = new GraphlabRecord();
		Features feature = new Features();
		File file = new File(lr.getAvroFileName());
		DatumReader<GenericRecord> datumReader = null;
		datumReader = new GenericDatumReader<GenericRecord>(new Datacore().getSchema());
		File lireFile = new File(resultPath+"feature.avro");
		DatumWriter<Features> ImageDatumWriter = new SpecificDatumWriter<Features>(Features.class);
		DataFileWriter<Features> dataFileWriter = new DataFileWriter<Features>(ImageDatumWriter);
		try {
			dataFileWriter.create(new Features().getSchema(), lireFile);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord datacore = null;
			while (dataFileReader.hasNext()) {
				datacore = dataFileReader.next(datacore);	
				feature.setLabeldesc("");
				feature.setLabelpos("");
				feature.setFeaturedata(((Utf8) datacore.get("vector")).toString());
				feature.setFilename("");	
				feature.setSource("bdas");				
				dataFileWriter.append(feature);				
			}
			dataFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		graphlabRecord.setAvroFileName(resultPath+"features.avro");
		return graphlabRecord;
	}
	
	public static void generateShellScript(String predictors, String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("result/"
					+ filename + "_run.sh"));
			bw.write("$MAHOUT_HOME/bin/mahout trainlogistic  --input "
					+ filename + " --output model --target " + CLASS_LABEL
					+ " --predictors " + predictors
					+ " --types numeric --categories 2");
			bw.write("\n");
			bw.write("$MAHOUT_HOME/bin/mahout org.apache.mahout.classifier.sgd.RunLogistic --input "
					+ filename + "  --model model --auc  --confusion");
			bw.write("\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}