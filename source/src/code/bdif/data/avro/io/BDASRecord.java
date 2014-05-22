package code.bdif.data.avro.io;

import java.io.File;

import code.bdif.data.avro.Datacore;

public class BDASRecord extends GenericFeatureRecord {
	BDASRecord instance;
	public BDASRecord() {

	}
	public String getAvroFileName() {
		return this.avrofilename;
	}
	
	public Datacore getDatacore() {
		return this.datacore;
	}
	public void  setAvroFileName(String avrofilename) {
		this.avrofilename = avrofilename;
	}
	
	public Datacore setDatcore(Datacore datacore) {
		return this.datacore;
	}
	public BDASRecord(String avrofilename) {
		this.avrofilename = avrofilename;
		avroFile = new File(this.avrofilename);
	}
	public BDASRecord getInstance() {
		instance = new BDASRecord();
		
		return instance;
	}
}
