package hu.juranyi.zsolt.cordis.projects;

public class CordisProjectsApp {
	public static void main(String[] args) {
		// TODO args management: commands, options
		new ProjectDownloader().outputDir(args[0]).all();
	}
}
