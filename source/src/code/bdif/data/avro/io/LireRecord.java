package code.bdif.data.avro.io;

import code.bdif.data.avro.Features;

public class LireRecord extends GenericFeatureRecord {
	public LireRecord() {
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

	public LireRecord(String avrofilename) {
		this.avrofilename = avrofilename;
	}
	
}
