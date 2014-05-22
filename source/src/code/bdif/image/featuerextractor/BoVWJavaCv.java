package code.bdif.image.featuerextractor;

import static com.googlecode.javacv.cpp.opencv_core.CV_KMEANS_USE_INITIAL_LABELS;
import static com.googlecode.javacv.cpp.opencv_core.CV_TERMCRIT_ITER;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM;

import java.io.File;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvTermCriteria;
import com.googlecode.javacv.cpp.opencv_core.StringVector;
import com.googlecode.javacv.cpp.opencv_features2d.BFMatcher;
import com.googlecode.javacv.cpp.opencv_features2d.BOWImgDescriptorExtractor;
import com.googlecode.javacv.cpp.opencv_features2d.BOWKMeansTrainer;
import com.googlecode.javacv.cpp.opencv_features2d.DescriptorExtractor;
import com.googlecode.javacv.cpp.opencv_features2d.FeatureDetector;
import com.googlecode.javacv.cpp.opencv_features2d.KeyPoint;
import com.googlecode.javacv.cpp.opencv_nonfree.SIFT;

public class BoVWJavaCv {

	final String trainingData = "";
	final String testData = "";
	int dictionarySize = 100;
	int retries = 1;
	int flags = CV_KMEANS_USE_INITIAL_LABELS;
	BOWKMeansTrainer bMeansTrainer;
	BOWImgDescriptorExtractor bDescriptorExtractor;
	FeatureDetector featureDetector;
	DescriptorExtractor descriptorExtractor;
	BFMatcher descriptorMatcher;
	CvTermCriteria termcrit = new CvTermCriteria(CV_TERMCRIT_ITER, 10, 0.001);

	public BoVWJavaCv() {
		super();
		System.out.println("begin");
		SIFT sift = new SIFT();
		featureDetector = sift.getFeatureDetector();
		descriptorExtractor = sift.getDescriptorExtractor();
		StringVector sv = new StringVector();
		descriptorExtractor.getList(sv);
		System.out.println("String Vector :" +sv.toString());
		descriptorMatcher = new BFMatcher(0); 
		System.out.println("end");
		bMeansTrainer = new BOWKMeansTrainer(dictionarySize, termcrit, retries,
				flags);
		bDescriptorExtractor = new BOWImgDescriptorExtractor(
				descriptorExtractor, descriptorMatcher);
	}
	public BoVWJavaCv(String baseurl) {
		this.extractTrainingVocabulary(baseurl);
	}

	public void extractTrainingVocabulary(String baseurl) {
		File dir = new File(baseurl);
		System.out.println(baseurl);
		for (File child : dir.listFiles()) {
			if (child.isDirectory()) {
				System.out.println("Processing directory" + child.getName());
				extractTrainingVocabulary(child.getAbsolutePath());
			}
			else {
				if (child.getName().contains(".jpg")) {
					System.out.println("Processing file" + "" + child.getName());
					CvMat image = cvLoadImageM(child.getAbsolutePath());
					System.out.println(image.isNull());
					if (!image.empty()) {

						KeyPoint keypoints = new KeyPoint(); // a verifier
						featureDetector.detect(image, keypoints, null);

						if (keypoints.isNull()) {
							System.out.println("Warning: Could not find key points in image: "+child.getName());
						}
						else {
							CvMat featurs = new CvMat(); //
							descriptorExtractor.compute(image, keypoints,featurs);
							bMeansTrainer.add(featurs);
							System.out.println("featurs.toString() :"+featurs.toString());
						}
					}

					else {
						System.out.println("Warning: Could not read image: "
								+ child.getName());
					}

				}
			}
			System.out.println(bMeansTrainer.getDescriptors().toString());
		}
	}
	public void extractBOWDescriptor(String baseurl, CvMat descriptors)
	{
		File dir = new File(baseurl);
		for (File child : dir.listFiles()) {
			if (child.isDirectory()) {
				System.out.println("Processing directory"
						+ child.getAbsolutePath());
				extractBOWDescriptor(child.getName(), descriptors);
			}
			else {
				if (child.getName().contains(".jpg")) {
					System.out.println("Processing file" + child.getName());
					final CvMat image = cvLoadImageM(child.getName());
					if (!image.empty()) {
						KeyPoint keypoints = new KeyPoint(); // a verifier
						featureDetector.detect(image, keypoints, null);
						if (keypoints.isNull()) {
							System.out
									.println("Warning: Could not find key points in image: "
											+
											child.getName());
						}
						else {
							CvMat BoWdescriptors = new CvMat(); //
							bDescriptorExtractor.compute(image, keypoints,
									BoWdescriptors, null, null);
							descriptors.put(descriptors);
							// float label=Float.parseFloat(child.getName());
							// labels.put(label);
						}
					}
					else {
						System.out.println("Warning: Could not read image: "
								+ child.getName());
					}

				}
			}
		}
	}
	public BOWKMeansTrainer getbMeansTrainer() {
		return bMeansTrainer;
	}

	public void setbMeansTrainer(BOWKMeansTrainer bMeansTrainer) {
		this.bMeansTrainer = bMeansTrainer;
	}

	public BOWImgDescriptorExtractor getbDescriptorExtractor() {
		return bDescriptorExtractor;
	}

	public void setbDescriptorExtractor(
			BOWImgDescriptorExtractor bDescriptorExtractor) {
		this.bDescriptorExtractor = bDescriptorExtractor;
	}

	public FeatureDetector getFeatureDetector() {
		return featureDetector;
	}

	public void setFeatureDetector(FeatureDetector featureDetector) {
		this.featureDetector = featureDetector;
	}

	public DescriptorExtractor getDescriptorExtractor() {
		return descriptorExtractor;
	}

	public void setDescriptorExtractor(DescriptorExtractor descriptorExtractor) {
		this.descriptorExtractor = descriptorExtractor;
	}

	public BFMatcher getDescriptorMatcher() {
		return descriptorMatcher;
	}

	public void setDescriptorMatcher(BFMatcher descriptorMatcher) {
		this.descriptorMatcher = descriptorMatcher;
	}

	public static void main(String[] args) {
		new BoVWJavaCv("d:/photos/sample/");
	}
}