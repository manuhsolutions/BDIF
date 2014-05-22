package code.bdif.data.avro.io;

import java.io.File;

import code.bdif.data.avro.Features;

public class JavaCVRecord extends GenericFeatureRecord {
	JavaCVRecord instance;
	public JavaCVRecord() {

	}

	public JavaCVRecord(String avrofilename) {
		this.avrofilename = avrofilename;
		avroFile = new File(this.avrofilename);
	}
	public JavaCVRecord getInstance() {
		instance = new JavaCVRecord();
		
		return instance;
	}
	
	public String getAvroFileName() {
		return this.avrofilename;
	}
	
	public Features getFeature() {
		return this.feature;
	}
	
	public void setAvroFileName(String avrofilename) {
		 this.avrofilename = avrofilename;
	}
	
	public void setFeature(Features feature) {
		this.feature = feature;
	}
}
