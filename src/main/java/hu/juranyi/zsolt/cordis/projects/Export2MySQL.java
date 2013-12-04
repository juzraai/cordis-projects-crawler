package hu.juranyi.zsolt.cordis.projects;

import hu.juranyi.zsolt.common.MD5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO JAVADOC
public class Export2MySQL {

	private static final Logger LOG = LoggerFactory
			.getLogger(Export2MySQL.class);

	// TODO private boolean dropExisting = false;
	private String host;
	private String name;
	private String user;
	private String pass;

	public Export2MySQL(String host, String name, String user, String pass) {
		this.host = host;
		this.name = name;
		this.user = user;
		this.pass = pass;
	}

	public void export(List<Project> projects) {
		LOG.info("Exporting {} projects to MySQL...", projects.size());

		try {
			// connection
			LOG.debug("Connecting...");
			Connection connection = DriverManager.getConnection("jdbc:mysql://"
					+ host, user, pass);
			LOG.debug("Selecting database...");
			Statement s = connection.createStatement();
			s.execute("USE " + name);
			s.close();
			LOG.debug("Connected.");

			// do the work
			for (Project project : projects) {
				insertProject(project, connection);

				Participant coordinator = project.getCoordinator();
				insertParticipant(coordinator, connection);
				insertParticipation(coordinator, project, connection);

				for (Participant participant : project.getParticipants()) {
					insertParticipant(participant, connection);
					insertParticipation(participant, project, connection);
				}
				// for participants ...
				// for publications, inside: for authors...
				// do connections
			}

		} catch (SQLException ex) {
			LOG.error("Error: {}", ex.getMessage());
		}

		// Project id: (int) rcn
		// Publication id: (string) md5(title+url+authors)
		// Participant id: (string) md5(all data)
		// Author id: (int) id, auto_increment
	}

	private void insertParticipation(Participant participant, Project project,
			Connection conn) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("REPLACE Participation"
					+ "(project_rcn, participant_id) VALUES (?,?)");
			ps.setInt(1, project.getRcn());
			ps.setString(2, calcParticipantId(participant));
			ps.execute();
			ps.close();
		} catch (Exception e) {
			LOG.error("Could not insert participation for project {}: {}",
					project.getRcn(), e.getMessage());
		}
	}

	private String calcParticipantId(Participant participant) throws Exception {
		return MD5.getMD5FromString(participant.getAddress()
				+ participant.getAdministrativeContact()
				+ participant.getCountry() + participant.getFax()
				+ participant.getName() + participant.getTel()
				+ participant.getWebsite());
	}

	private void insertParticipant(Participant participant, Connection conn) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("REPLACE Participant"
					+ "(id, address, administrative_contact, country, fax,"
					+ "name, tel, website) VALUES (?,?,?,?,?,?,?,?)");
			ps.setString(1, calcParticipantId(participant));
			ps.setString(2, participant.getAddress());
			ps.setString(3, participant.getAdministrativeContact());
			ps.setString(4, participant.getCountry());
			ps.setString(5, participant.getFax());
			ps.setString(6, participant.getName());
			ps.setString(7, participant.getTel());
			ps.setString(8, participant.getWebsite());
			ps.execute();
			ps.close();
		} catch (Exception e) {
			LOG.error("Could not insert participant: {}", e.getMessage());
		}
	}

	private void insertProject(Project project, Connection conn) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("REPLACE Project"
					+ "(rcn, contract_type, cost, cost_currency,"
					+ "eu_contribution, eu_contribution_currency, dates_from,"
					+ "dates_to, general_information, last_updated, name,"
					+ "objective, programme_acronym, reference, status,"
					+ "subprogramme_area, title, website)"
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setInt(1, project.getRcn());
			ps.setString(2, project.getContractType());
			ps.setInt(3, project.getCost());
			ps.setString(4, project.getCostCurrency());
			ps.setInt(5, project.getEuContribution());
			ps.setString(6, project.getEuContributionCurrency());
			ps.setDate(7, new java.sql.Date(project.getDatesFrom().getTime()));
			ps.setDate(8, new java.sql.Date(project.getDatesTo().getTime()));
			ps.setString(9, project.getGeneralInformation());
			ps.setDate(10, new java.sql.Date(project.getLastUpdatedOn()
					.getTime()));
			ps.setString(11, project.getName());
			ps.setString(12, project.getObjective());
			ps.setString(13, project.getProgrammeAcronym());
			ps.setString(14, project.getReference());
			ps.setString(15, project.getStatus());
			ps.setString(16, project.getSubprogrammeArea());
			ps.setString(17, project.getTitle());
			ps.setString(18, project.getWebsite());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			LOG.error("Could not insert project {}: {}", project.getRcn(),
					e.getMessage());
		}
	}
}
