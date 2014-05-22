package code.bdif.image.featuerextractor;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.imageanalysis.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.SimpleColorHistogram;
import net.semanticmetadata.lire.imageanalysis.bovw.LocalFeatureHistogramBuilder;
import net.semanticmetadata.lire.imageanalysis.bovw.SurfFeatureHistogramBuilder;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.SurfDocumentBuilder;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;

import code.bdif.data.avro.Features;

public class ExtractBoVW {

	static String dataset = "/home/hadoop/Avro-Projects/test/";
	static String indexPath = "./bow-training-dataset-index";
	static final String CEDD = "CEDD";
	static final String SCH = "SimpleColorHistogram";
	static final String EH = "EdgeHistogram";
	public static void main(String[] args) {
		//dumpBOVW();
		//extractBoVWOthers(SCH);
		//createAvroRecord(null);  // To Generate only Bag of Visual Words features
		createAvroRecord(SCH);  // To Generate Bag of Visual Words + (CEDD/SCH/EH) features  
	}
	
	static void dumpBOVW() {
		String bovw = null;
		
		try {
			ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
			builder.addBuilder(new SurfDocumentBuilder());
			ArrayList<String> images = FileUtils.getAllImages(new File(dataset),true);
			IndexWriter iw = LuceneUtils.createIndexWriter(indexPath, true);
			for (String identifier : images) {
				Document doc = builder.createDocument(new FileInputStream(identifier), identifier);
				iw.addDocument(doc);
			}
			iw.close();
			IndexReader ir = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
			LocalFeatureHistogramBuilder sh = new SurfFeatureHistogramBuilder(ir,images.size(), 100);
			BufferedWriter bw = new BufferedWriter(new FileWriter("bovw-training-data.csv"));
			sh.index();
			for (String identifier : images) {
				System.out.println("processing.. "+identifier);
				Document doc = builder.createDocument(new FileInputStream(identifier), identifier);
				bovw = sh.getVisualWords(doc).toString();
				bw.write(identifier.replaceAll(dataset, "")+" "+bovw.substring(bovw.indexOf("stored<featureSURFHistogram")+28, bovw.indexOf(">>")));
				bw.write("\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static void extractBoVWOthers(String method) {
		String bovw = null;
		
		try {
			ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
			builder.addBuilder(new SurfDocumentBuilder());
			ArrayList<String> images = FileUtils.getAllImages(new File(dataset),true);
			IndexWriter iw = LuceneUtils.createIndexWriter(indexPath, true);
			for (String identifier : images) {
				Document doc = builder.createDocument(new FileInputStream(identifier), identifier);
				iw.addDocument(doc);
			}
			iw.close();
			IndexReader ir = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
			LocalFeatureHistogramBuilder sh = new SurfFeatureHistogramBuilder(ir,images.size(), 100);
			BufferedWriter bw = new BufferedWriter(new FileWriter("bovw-"+method+"-data.csv"));
			sh.index();
			for (String identifier : images) {
				System.out.println("processing.. "+identifier);
				BufferedImage image = ImageIO.read(new FileInputStream(identifier));
				Document doc = builder.createDocument(new FileInputStream(identifier), identifier);
				bovw = sh.getVisualWords(doc).toString();
				if (method.equals("CEDD")) {
					CEDD cedd = new CEDD();
					cedd.extract(image);
					bw.write(identifier
							+ " "
							+ bovw.substring(
									bovw.indexOf("stored<featureSURFHistogram") + 28,
									bovw.indexOf(">>"))
							+ " "
							+ cedd.getStringRepresentation().replace(
									"cedd 144 ", ""));
					bw.write("\n");
				}

				if (method.equals("SimpleColorHistogram")) {
					SimpleColorHistogram sch = new SimpleColorHistogram();
					sch.extract(image);
					bw.write(identifier
							+ " "
							+ bovw.substring(
									bovw.indexOf("stored<featureSURFHistogram") + 28,
									bovw.indexOf(">>"))
							+ " "
							+ sch.getStringRepresentation().replace("RGB 512 ",
									""));
					bw.write("\n");
				}

				if (method.equals("EdgeHistogram")) {
					EdgeHistogram eh = new EdgeHistogram();
					eh.extract(image);
					bw.write(identifier
							+ " "
							+ bovw.substring(
									bovw.indexOf("stored<featureSURFHistogram") + 28,
									bovw.indexOf(">>"))
							+ " "
							+ eh.getStringRepresentation().replace(
									"edgehistogram;", ""));
					bw.write("\n");

				}
				
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static void createAvroRecord(String withOtherFeatures) {

		String bovw = null;
		System.out.println("Starting lire indexing....");
		try {
			ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
			builder.addBuilder(new SurfDocumentBuilder());
			IndexWriter iw = LuceneUtils.createIndexWriter(indexPath, true);
			ArrayList<String> images = FileUtils.getAllImages(new File(dataset),true);
			System.out.println("Writing index file....");
			for (String identifier : images) {
				Document doc = builder.createDocument(new FileInputStream(identifier), identifier);
				iw.addDocument(doc);
			}
			iw.close();
			System.out.println("Index file generation completed");
			IndexReader ir = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
			SurfFeatureHistogramBuilder sh = new SurfFeatureHistogramBuilder(ir,images.size(), 100);
			System.out.println("Starting feature extraction....");
			sh.index();
			File file = new File("features.avro");
			DatumWriter<Features> ImageDatumWriter = new SpecificDatumWriter<Features>(Features.class);
			DataFileWriter<Features> dataFileWriter = new DataFileWriter<Features>(ImageDatumWriter);
			dataFileWriter.create(new Features().getSchema(), file);
			for (String identifier : images) {
				BufferedImage image = ImageIO.read(new FileInputStream(identifier));
				System.out.println("Processing file "+identifier);
				Document doc = builder.createDocument(new FileInputStream(identifier), identifier);
				bovw = sh.getVisualWords(doc).toString();
				Features features = new Features();
				if(withOtherFeatures!=null && withOtherFeatures.length() >0) {
					if (withOtherFeatures.equals("CEDD")) {
						CEDD cedd = new CEDD();
						cedd.extract(image);
						features.setFeaturedata(bovw.substring(bovw.indexOf("stored<featureSURFHistogram")+28, bovw.indexOf(">>"))+ " "
								+ cedd.getStringRepresentation().replace(
										"cedd 144 ", ""));
					}
					if (withOtherFeatures.equals("SimpleColorHistogram")) {
						SimpleColorHistogram sch = new SimpleColorHistogram();
						sch.extract(image);
						features.setFeaturedata(bovw.substring(bovw.indexOf("stored<featureSURFHistogram")+28, bovw.indexOf(">>"))+ " "
								+ sch.getStringRepresentation().replace(
										"RGB 512 ", ""));
					}
					if (withOtherFeatures.equals("EdgeHistogram")) {
						EdgeHistogram eh = new EdgeHistogram();
						eh.extract(image);
						features.setFeaturedata(bovw.substring(bovw.indexOf("stored<featureSURFHistogram")+28, bovw.indexOf(">>"))+ " "
								+ eh.getStringRepresentation().replace(
										"edgehistogram;", ""));
					}
				}
				else {
					features.setFeaturedata(bovw.substring(bovw.indexOf("stored<featureSURFHistogram")+28, bovw.indexOf(">>")));
				}
				
				features.setFilename(identifier);
				features.setSource("lire");
				features.setLabeldesc("");
				features.setLabelpos("");
				dataFileWriter.append(features);
			}
			dataFileWriter.close();
			System.out.println("Feature extraction completed");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}