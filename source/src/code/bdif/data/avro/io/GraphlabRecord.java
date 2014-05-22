package code.bdif.data.avro.io;

import java.io.File;

import code.bdif.data.avro.Features;

public class GraphlabRecord extends GenericFeatureRecord {
	GraphlabRecord instance;
	public GraphlabRecord() {

	}

	public GraphlabRecord(String avrofilename) {
		this.avrofilename = avrofilename;
		avroFile = new File(this.avrofilename);
	}
	public GraphlabRecord getInstance() {
		instance = new GraphlabRecord();
		
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
