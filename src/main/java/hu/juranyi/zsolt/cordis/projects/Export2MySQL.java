package hu.juranyi.zsolt.cordis.projects;

import hu.juranyi.zsolt.common.MD5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO JAVADOC
public class Export2MySQL {
	// TODO on-the-fly exporting while parsing!!!
	// TODO option to build database (run schema.sql)

	private static final Logger LOG = LoggerFactory
			.getLogger(Export2MySQL.class);

	// TODO private boolean dropExisting = false;
	private String host;
	private String name;
	private String pass;
	private String user;

	public Export2MySQL(String host, String name, String user, String pass) {
		this.host = host;
		this.name = name;
		this.user = user;
		this.pass = pass;
	}

	private String calcParticipantId(Participant participant) throws Exception {
		return MD5.getMD5FromString(participant.getAddress()
				+ participant.getAdministrativeContact()
				+ participant.getCountry() + participant.getFax()
				+ participant.getName() + participant.getTel()
				+ participant.getWebsite());
	}

	private String calcPublicationId(Publication publication) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(publication.getTitle());
		sb.append(publication.getUrl());
		for (String author : publication.getAuthors()) {
			sb.append(author);
		}
		return MD5.getMD5FromString(sb.toString());
	}

	public void export(List<Project> projects) {
		LOG.info("Exporting {} projects to MySQL...", projects.size());

		try {
			// connection
			LOG.debug("Connecting...");
			Connection conn = DriverManager.getConnection("jdbc:mysql://"
					+ host, user, pass);
			LOG.debug("Selecting database...");
			Statement s = conn.createStatement();
			s.execute("USE " + name);
			s.close();
			LOG.debug("Connected. Now inserting records...");

			// do the work
			for (int i = 0; i < projects.size(); i++) {
				Project project = projects.get(i);
				LOG.info("Inserting project {}/{}, RCN: {}", i + 1,
						projects.size(), project.getRcn());

				insertProject(project, conn);

				if (null != project.getCoordinator()) {
					Participant coordinator = project.getCoordinator();
					insertParticipant(coordinator, conn);
					insertParticipation(coordinator, project, conn);
				}

				if (null != project.getParticipants()) {
					for (Participant participant : project.getParticipants()) {
						insertParticipant(participant, conn);
						insertParticipation(participant, project, conn);
					}
				}

				if (null != project.getPublications()) {
					for (Publication pub : project.getPublications()) {
						// if (null != pub) {
						insertPublication(pub, conn);
						insertProjectPublication(pub, project, conn);
						if (null != pub.getAuthors()) {
							for (String author : pub.getAuthors()) {
								// if (null != author) {
								author = author.trim();
								insertAuthor(author, conn);
								insertAuthoring(author, pub, conn);
								// }
							}
						}
						// }
					}
				}

				// TODO DELETE THIS IS DEBUG!!!
				// <DEBUG CODE>
				/*
				 * PreparedStatement ps = conn.prepareStatement("" +
				 * "SELECT count(publication_id)" + " FROM Project_Publication"
				 * + " WHERE project_rcn=90433;"); ResultSet rs =
				 * ps.executeQuery(); int c = -1; if (rs.next()) c =
				 * rs.getInt(1); rs.close(); ps.close();
				 * LOG.error("*** DYNANETS PUBLICATION COUNT = {}", c);
				 */
				// </DEBUG CODE>

			} // projects
		} catch (SQLException ex) {
			LOG.error("Error: {}", ex.getMessage());
		}
	}

	private void insertAuthor(String author, Connection connection) {
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement("INSERT IGNORE INTO Author "
					+ "(name) VALUES (?)");
			ps.setString(1, author);
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			LOG.error("Could not insert author {}: {}", author, e.getMessage());
		}
	}

	private void insertAuthoring(String author, Publication publication,
			Connection connection) {
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement("SELECT id FROM Author "
					+ "WHERE name=?");
			ps.setString(1, author);
			ResultSet results = ps.executeQuery();
			Integer authorId = null;
			while (results.next()) {
				authorId = results.getInt(1);
			}
			results.close();
			ps.close();

			if (null == authorId) {
				throw new Exception("Could not find author.");
			}

			ps = connection.prepareStatement("REPLACE Authoring "
					+ "(author_id, publication_id) VALUES (?,?)");
			ps.setInt(1, authorId);
			ps.setString(2, calcPublicationId(publication));
			ps.execute();
			ps.close();

		} catch (Exception e) {
			LOG.error("Could not insert authoring for author {}: {}", author,
					e.getMessage());
		}
	}

	private void insertParticipant(Participant participant,
			Connection connection) {
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement("REPLACE Participant "
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

	private void insertParticipation(Participant participant, Project project,
			Connection connection) {
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement("REPLACE Participation "
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

	private void insertProject(Project project, Connection connection) {
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement("REPLACE Project "
					+ "(rcn, contract_type, cost, cost_currency,"
					+ "eu_contribution, eu_contribution_currency, dates_from,"
					+ "dates_to, general_information, last_updated, name,"
					+ "objective, programme_acronym, reference, status,"
					+ "subprogramme_area, title, website) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setInt(1, project.getRcn());
			ps.setString(2, project.getContractType());
			ps.setInt(3, project.getCost());
			ps.setString(4, project.getCostCurrency());
			ps.setInt(5, project.getEuContribution());
			ps.setString(6, project.getEuContributionCurrency());
			ps.setDate(7, toSqlDate(project.getDatesFrom()));
			ps.setDate(8, toSqlDate(project.getDatesTo()));
			ps.setString(9, project.getGeneralInformation());
			ps.setDate(10, toSqlDate(project.getLastUpdatedOn()));
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

	private void insertProjectPublication(Publication publication,
			Project project, Connection connection) {
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement("REPLACE Project_Publication "
					+ "(project_rcn, publication_id) VALUES (?,?)");
			ps.setInt(1, project.getRcn());
			ps.setString(2, calcPublicationId(publication));
			ps.execute();
			ps.close();
		} catch (Exception e) {
			LOG.error(
					"Could not insert publication relation for project {}: {}",
					project.getRcn(), e.getMessage());
		}
	}

	private void insertPublication(Publication publication,
			Connection connection) {
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement("REPLACE Publication "
					+ "(id, title, url) VALUES (?, ?,?);");
			ps.setString(1, calcPublicationId(publication));
			ps.setString(2, publication.getTitle());
			ps.setString(3, publication.getUrl());
			ps.execute();
			ps.close();
		} catch (Exception e) {
			LOG.error("Could not insert publication: {}", e.getMessage());
		}
	}

	private java.sql.Date toSqlDate(java.util.Date utilDate) {
		return (null == utilDate) ? null
				: new java.sql.Date(utilDate.getTime());
	}
}
