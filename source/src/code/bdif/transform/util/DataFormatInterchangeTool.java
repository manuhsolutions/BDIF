package code.bdif.transform.util;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;

import code.bdif.data.avro.io.BDASRecord;
import code.bdif.data.avro.io.GraphlabRecord;
import code.bdif.data.avro.io.JavaCVRecord;
import code.bdif.data.avro.io.LireRecord;
import code.bdif.data.avro.io.MahoutRecord;

public class DataFormatInterchangeTool {
	static JFrame jWindow;
	static String path = null;
	static String classifier_path = null;
	static String concept_path = null;

	public static void main(String[] args) {
		Options options = getOptions();
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			String mode = cmd.getOptionValue("mode");
			if(cmd.getOptions().length < 1 || mode == null){
				System.err.println("Usage : DataformatInterChangeTool.jar [options]");
				System.err.println("options");
				System.err.println("------");
				System.err.println("-mode GUI");
				System.err.println("-mode CMD [params]");
				System.err.println("params");
				System.err.println("------");
				System.err.println("-sourceApp <<source tool>>");
				System.err.println("-destApp <<destination tool>>");
				System.err.println("-operType <<operation type>>");
				System.err.println("-source <<source filename>>");
				System.err.println("-resultPath <<result path>>");	
				System.err.println("-classifierdata <<classifier file path>>");
				System.err.println("-conceptdata <<concept file path>>");
				System.exit(1);
			}			
			if (mode.equals("GUI")) {
				String[] sources = { "lire", "javacv", "bdas", "graphlab","mahout" };
				String params[] = showFrame(sources);
				prepareGeneric(params[0], params[1], params[2], params[3], path,classifier_path,concept_path);
			} else if (mode.equals("CMD")) {
				if (cmd.getOptions().length < 7) {
					System.err.println("Usage : DataformatInterChangeTool.jar -mode CMD [params]");					
					System.err.println("params");
					System.err.println("------");
					System.err.println("-sourceApp <<source tool>>");
					System.err.println("-destApp <<destination tool>>");
					System.err.println("-operType <<operation type>>");
					System.err.println("-source <<source filename>>");
					System.err.println("-resultPath <<result path>>");
					System.err.println("-classifierdata <<classifier file path>>");
					System.err.println("-conceptdata <<concept file path>>");
					System.exit(1);
				}
				prepareGeneric(cmd.getOptionValue("sourceApp"),
						cmd.getOptionValue("destApp"),
						cmd.getOptionValue("operType"),
						cmd.getOptionValue("resultPath"),
						cmd.getOptionValue("source"),
						cmd.getOptionValue("classifierdata"),
						cmd.getOptionValue("conceptdata"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void prepareGeneric(String src, String dest, String operType,
			String resultPath, String source, String classifierdata,String conceptdata) {
		if (src != null && dest != null && operType != null
				&& resultPath != null && source != null) {
			if (src.equals("lire") && dest.equals("mahout")) {
				LireRecord lireRecord = new LireRecord(source);
				MahoutRecord mahoutRecord = Mapper.fromMahout(lireRecord,
						operType, resultPath,classifierdata,conceptdata);
			}
			if (src.equals("javacv") && dest.equals("mahout")) {
				JavaCVRecord javaCVRecord = new JavaCVRecord(source);
				MahoutRecord mahoutRecord = Mapper.fromMahout(javaCVRecord,
						operType, resultPath,classifierdata,conceptdata);
			}
			if (src.equals("graphlab") && dest.equals("mahout")) {
				GraphlabRecord graphlabRecord = new GraphlabRecord(source);
				MahoutRecord mahoutRecord = Mapper.fromMahout(graphlabRecord,
						operType, resultPath,classifierdata,conceptdata);
			}
			if (src.equals("lire") && dest.equals("bdas")) {
				LireRecord lireRecord = new LireRecord(source);
				BDASRecord bDASRecord = Mapper.fromBdas(lireRecord, operType,
						resultPath,classifierdata,conceptdata);
			}
			if (src.equals("javacv") && dest.equals("bdas")) {
				JavaCVRecord javaCVRecord = new JavaCVRecord(source);
				BDASRecord bDASRecord = Mapper.fromBdas(javaCVRecord, operType,
						resultPath,classifierdata,conceptdata);
			}
			if (src.equals("graphlab") && dest.equals("bdas")) {
				GraphlabRecord graphlabRecord = new GraphlabRecord(source);
				BDASRecord bDASRecord = Mapper.fromBdas(graphlabRecord,
						operType, resultPath,classifierdata,conceptdata);
			}
			if (src.equals("mahout") && dest.equals("lire")) {
				MahoutRecord mahoutRecord = new MahoutRecord(source);
				LireRecord lireRecord = Mapper.fromLire(mahoutRecord, operType,
						resultPath);
			}
			if (src.equals("bdas") && dest.equals("lire")) {
				BDASRecord bDASRecord = new BDASRecord(source);
				LireRecord lireRecord = Mapper.fromLire(bDASRecord, operType,
						resultPath);
			}
			if (src.equals("mahout") && dest.equals("javacv")) {
				MahoutRecord mahoutRecord = new MahoutRecord(source);
				JavaCVRecord javaCVRecord = Mapper.fromJavacv(mahoutRecord, operType,
						resultPath);
			}
			if (src.equals("bdas") && dest.equals("javacv")) {
				BDASRecord bDASRecord = new BDASRecord(source);
				JavaCVRecord javaCVRecord = Mapper.fromJavacv(bDASRecord, operType,
						resultPath);
			}
			if (src.equals("mahout") && dest.equals("graphlab")) {
				MahoutRecord mahoutRecord = new MahoutRecord(source);
				GraphlabRecord graphlabRecord = Mapper.fromGraphLab(mahoutRecord, operType,
						resultPath);
			}
			if (src.equals("bdas") && dest.equals("graphlab")) {
				BDASRecord bDASRecord = new BDASRecord(source);
				GraphlabRecord graphlabRecord = Mapper.fromGraphLab(bDASRecord, operType,
						resultPath);
			}
		} else {
			System.out.println("Provide valid details");
		}
	}

	public static String[] showFrame(String[] sources) {
		final DefaultComboBoxModel<String> dataCoredestnModel = new DefaultComboBoxModel<String>(
				new String[] { "mahout", "bdas" });
		final DefaultComboBoxModel<String> imagedestnModel = new DefaultComboBoxModel<String>(
				new String[] { "lire", "javacv", "graphlab" });
		final JComboBox<String> destnationComboBox = new JComboBox<String>(
				dataCoredestnModel);
		final DefaultComboBoxModel<String> sourceModel = new DefaultComboBoxModel<String>(
				sources);
		final JComboBox<String> sourceComboBox = new JComboBox<String>(
				sourceModel);
		DefaultComboBoxModel<String> oprModel = new DefaultComboBoxModel<String>(
				new String[] { "Classification", "Clustring" });
		JComboBox<String> oprComboBox = new JComboBox<String>(oprModel);
		String params[] = new String[4];
		if (EventQueue.isDispatchThread()) {
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(7, 2));
			panel.add(new JLabel("Select Source"));
			panel.add(sourceComboBox);
			panel.add(new JLabel("Select Destination"));
			panel.add(destnationComboBox);
			panel.add(new JLabel("Select Operation"));
			panel.add(oprComboBox);
			JTextField resultPath = new JTextField("/home/hadoop/", 20);
			panel.add(new JLabel("Enter Result Path"));
			panel.add(resultPath);
			JButton browse = new JButton("Browse");
			final JLabel avroFile = new JLabel("Select Avro File");
			panel.add(avroFile);
			panel.add(browse);
			browse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					int res = fc.showOpenDialog(jWindow);
					if (res == JFileChooser.APPROVE_OPTION) {
						path = fc.getSelectedFile().getAbsolutePath();
						avroFile.setText(path.substring(path.lastIndexOf("/")+1, path.length()));
					} else {
						JOptionPane.showMessageDialog(null,
								"You must select a file.", "Aborting...",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			});
			JButton classifierdata = new JButton("Browse");
			final JLabel clsdata = new JLabel("Select Classifier File");
			panel.add(clsdata);
			panel.add(classifierdata);
			classifierdata.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					int res = fc.showOpenDialog(jWindow);
					if (res == JFileChooser.APPROVE_OPTION) {
						classifier_path = fc.getSelectedFile().getAbsolutePath();
						clsdata.setText(classifier_path.substring(classifier_path.lastIndexOf("/")+1, classifier_path.length()));
					} else {
						JOptionPane.showMessageDialog(null,
								"You must select a file.", "Aborting...",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			});
			JButton conceptdata = new JButton("Browse");
			final JLabel cdata = new JLabel("Select Concept File");
			panel.add(cdata);
			panel.add(conceptdata);
			conceptdata.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					int res = fc.showOpenDialog(jWindow);
					if (res == JFileChooser.APPROVE_OPTION) {
						concept_path = fc.getSelectedFile().getAbsolutePath();
						cdata.setText(concept_path.substring(concept_path.lastIndexOf("/")+1, concept_path.length()));
					} else {
						JOptionPane.showMessageDialog(null,
								"You must select a file.", "Aborting...",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			});
			sourceComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if ("mahout".equals(sourceComboBox.getSelectedItem())
							|| "bdas".equals(sourceComboBox.getSelectedItem())) {
						destnationComboBox.setModel(imagedestnModel);
					} else {
						destnationComboBox.setModel(dataCoredestnModel);
					}
				}
			});
			int iResult = JOptionPane.showConfirmDialog(null, panel,
					"Avro Main", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			switch (iResult) {
			case JOptionPane.OK_OPTION:
				params[0] = (String) sourceComboBox.getSelectedItem();
				params[1] = (String) destnationComboBox.getSelectedItem();
				params[2] = (String) oprComboBox.getSelectedItem();
				params[3] = resultPath.getText();
				break;
			}
		} else {
			Response response = new Response(sources);
			try {
				SwingUtilities.invokeAndWait(response);
				params = response.getResponse();
			} catch (InterruptedException | InvocationTargetException ex) {
				ex.printStackTrace();
			}

		}
		return params;
	}

	public static class Response implements Runnable {

		private String[] sources;
		private String[] response;

		public Response(String[] sources) {
			this.sources = sources;
		}

		@Override
		public void run() {
			response = showFrame(sources);
		}

		public String[] getResponse() {
			return response;
		}
	}

	private static Options getOptions() {
		Options options = new Options();
		options.addOption("mode", true, " Mode type GUI or CMD");
		options.addOption("sourceApp", true, " Name of source application");
		options.addOption("destApp", true, " Name of destination application");
		options.addOption("operType", true, " Name of operation type");
		options.addOption("source", true, " Path of source file");
		options.addOption("resultPath", true, " Path of destination file");
		options.addOption("classifierdata", true, " Path of classifier file");
		options.addOption("conceptdata", true, " Path of concept file");
		return options;
	}
}
