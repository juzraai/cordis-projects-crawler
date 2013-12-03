package hu.juranyi.zsolt.cordis.projects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO JAVADOC
// TODO settings!
public class Export2MySQL {

	private static final Logger LOG = LoggerFactory
			.getLogger(Export2MySQL.class);

	// TODO private boolean dropExisting = false;
	private String host = "localhost";
	private String name = "cordis";
	private String user = "root";
	private String pass = "root";

	// TODO settings should be passed to constructor

	public static void export(List<Project> projects) {
		LOG.info("Exporting {} projects to MySQL...", projects.size());
		// TODO export...

		// Project id: (int) rcn
		// Publication id: (string) md5(title+url+authors)
		// Participant id: (string) md5(all data)
		// Author id: (int) id, auto_increment
	}

}
