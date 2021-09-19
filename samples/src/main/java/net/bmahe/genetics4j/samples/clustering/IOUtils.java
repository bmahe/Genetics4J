package net.bmahe.genetics4j.samples.clustering;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOUtils {
	final static public Logger logger = LogManager.getLogger(IOUtils.class);

	public static double[][] loadClusters(final String filename) {
		logger.info("Loading clusters from {}", filename);

		Reader in;
		try {
			in = new FileReader(filename);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		Iterable<CSVRecord> records;
		try {
			records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final List<double[]> entries = new ArrayList<>();
		for (final CSVRecord record : records) {
			final double x = Double.parseDouble(record.get(1));
			final double y = Double.parseDouble(record.get(2));

			entries.add(new double[] { x, y });
		}

		final double[][] clusters = new double[entries.size()][2];
		for (int i = 0; i < entries.size(); i++) {
			clusters[i][0] = entries.get(i)[0];
			clusters[i][1] = entries.get(i)[1];
		}
		return clusters;
	}

	public static double[][] loadDataPoints(final String filename) throws IOException {
		final Reader in = new FileReader(filename);
		final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader()
				.withSkipHeaderRecord(true)
				.parse(in);
		final List<double[]> entries = new ArrayList<>();
		for (final CSVRecord record : records) {
			final double cluster = Double.parseDouble(record.get(0));
			final double x = Double.parseDouble(record.get(1));
			final double y = Double.parseDouble(record.get(2));

			entries.add(new double[] { cluster, x, y });
		}

		final double[][] clusters = new double[entries.size()][3];
		for (int i = 0; i < entries.size(); i++) {
			clusters[i][0] = entries.get(i)[1];
			clusters[i][1] = entries.get(i)[2];
			clusters[i][2] = entries.get(i)[0];
		}
		return clusters;
	}

	public static void persistClusters(final double[][] clusters, final String clustersFilename) throws IOException {
		logger.info("Saving clusters to CSV: {}", clustersFilename);

		final CSVPrinter csvPrinter;
		try {
			csvPrinter = CSVFormat.DEFAULT.withAutoFlush(true)
					.withHeader(new String[] { "cluster", "x", "y" })
					.print(Path.of(clustersFilename), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Could not open {}", clustersFilename, e);
			throw new RuntimeException("Could not open file " + clustersFilename, e);
		}

		for (int i = 0; i < clusters.length; i++) {
			try {
				csvPrinter.printRecord(i, clusters[i][0], clusters[i][1]);
			} catch (IOException e) {
				throw new RuntimeException("Could not write data", e);
			}
		}
		csvPrinter.close(true);
	}

	public static void persistDataPoints(final double[][] data, final String filename) throws IOException {
		Validate.notBlank(filename);

		logger.info("Saving data to CSV: {}", filename);

		final int numDataPoints = data.length;

		final CSVPrinter csvPrinter;
		try {
			csvPrinter = CSVFormat.DEFAULT.withAutoFlush(true)
					.withHeader(new String[] { "cluster", "x", "y" })
					.print(Path.of(filename), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Could not open {}", filename, e);
			throw new RuntimeException("Could not open file " + filename, e);
		}

		for (int i = 0; i < numDataPoints; i++) {
			try {
				csvPrinter.printRecord((int) data[i][2], data[i][0], data[i][1]);
			} catch (IOException e) {
				throw new RuntimeException("Could not write data", e);
			}
		}
		csvPrinter.close(true);
	}

	public static void persistDataPoints(final double[][] data, final int[] closestClusterIndex, final String filename)
			throws IOException {
		Validate.notBlank(filename);

		logger.info("Saving data to CSV: {}", filename);

		final int numDataPoints = data.length;

		final CSVPrinter csvPrinter;
		try {
			csvPrinter = CSVFormat.DEFAULT.withAutoFlush(true)
					.withHeader(new String[] { "cluster", "x", "y" })
					.print(Path.of(filename), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Could not open {}", filename, e);
			throw new RuntimeException("Could not open file " + filename, e);
		}

		for (int i = 0; i < numDataPoints; i++) {
			try {
				csvPrinter.printRecord(closestClusterIndex[i], data[i][0], data[i][1]);
			} catch (IOException e) {
				throw new RuntimeException("Could not write data", e);
			}
		}
		csvPrinter.close(true);
	}
}