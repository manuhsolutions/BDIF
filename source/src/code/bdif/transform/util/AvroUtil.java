package code.bdif.transform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.lucene.document.Document;

import code.bdif.data.avro.Datacore;
import code.bdif.data.avro.Features;

public class AvroUtil {
	public static void main(String[] args) {
		readAvro("e:\\datacore.avro",  new Datacore()) ;
	}

	/*public static void writeToAvro(String filename, String datatowrite, Object datatype,ArrayList<String> images) {
		if (datatype instanceof Features) {
		File file = new File(filename);
		DatumWriter<Features> ImageDatumWriter = new SpecificDatumWriter<Features>(Features.class);
		DataFileWriter<Features> dataFileWriter = new DataFileWriter<Features>(ImageDatumWriter);
		dataFileWriter.create(new Features().getSchema(), file);
		Features img = new Features();
		img.setFilename(identifier);
		img.setSource("lire");
		img.setFeaturedata();
		dataFileWriter.append(img);
		dataFileWriter.close();
		}
		if (datatype instanceof Datacore) {
			
		}
	}*/

	public static void readAvro(String filename, Object datatype) {
		File file = new File(filename);
		DatumReader<GenericRecord> datumReader = null;
		if (datatype instanceof Features) {
			datumReader = new GenericDatumReader<GenericRecord>(new Features().getSchema());
		}
		if (datatype instanceof Datacore) {
			datumReader = new GenericDatumReader<GenericRecord>(new Datacore().getSchema());
		}

		try {
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord image = null;
			while (dataFileReader.hasNext()) {
				image = dataFileReader.next(image);
				System.out.println(image.get(0)+" "+image.get(1));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
