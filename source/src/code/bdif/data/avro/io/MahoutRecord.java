package code.bdif.data.avro.io;

import code.bdif.data.avro.Datacore;

public class MahoutRecord  extends GenericFeatureRecord {
	
	public MahoutRecord() {

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
	
	public MahoutRecord(String avrofilename) {
		this.avrofilename = avrofilename;
	}
	
}
