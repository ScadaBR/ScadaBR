/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Matthew Lohbihler
 */
public class DBConvert {
	private static final Log LOG = LogFactory.getLog(DBConvert.class);

	private DatabaseAccess source;
	private DatabaseAccess target;

	public void setSource(DatabaseAccess source) {
		this.source = source;
	}

	public void setTarget(DatabaseAccess target) {
		this.target = target;
	}

	public void execute() throws SQLException {
		LOG.warn("Running database conversion from " + source.getType().name() + " to " + target.getType().name());

		// Create the connections
		Connection sourceConn = source.getDataSource().getConnection();
		sourceConn.setAutoCommit(true);
		Connection targetConn = target.getDataSource().getConnection();
		targetConn.setAutoCommit(false);

		for (String tableName : getTableNames())
			copyTable(sourceConn, targetConn, tableName);

		for (String tableName : getHistoricTableNames())
			copyHistoricTable(sourceConn, targetConn, tableName);

		for (String tableName : getHistoricDepTableNames())
			copyTable(sourceConn, targetConn, tableName);

		sourceConn.close();
		targetConn.close();

		LOG.warn("Completed database conversion");
	}

	private String[] getTableNames() {
		return new String[] { "systemSettings", //
				"users", //
				"mailingLists", //
				"mailingListInactive", //
				"mailingListMembers", //
				"dataSources", //
				"dataSourceUsers", //
				"dataPoints", //
				"dataPointUsers", //
				"mangoViews", //
				"mangoViewUsers", //
				"watchLists", //
				"watchListPoints", //
				"watchListUsers", //
				"pointEventDetectors", //
				"eventHandlers", //
				"scheduledEvents", //
				"pointHierarchy", //
				"compoundEventDetectors", //
				"reports", //
				"reportInstances", //
				"reportInstancePoints", //
				"reportInstanceData", //
				"reportInstanceDataAnnotations", //
				"reportInstanceEvents", //
				"reportInstanceUserComments", //
				"publishers", //
				"maintenanceEvents", //
				"usersProfiles", //
				"dataSourceUsersProfiles", //
				"dataPointUsersProfiles", //
				"usersUsersProfiles", //
				"watchListUsersProfiles", //
				"viewUsersProfiles", //
				"pointLinks", //
		};
	}

	private String[] getHistoricTableNames() {
		return new String[] { "pointValues", //
		};
	}

	private String[] getHistoricDepTableNames() {
		return new String[] { "events", //
				"userEvents", //
				"userComments", //
				"pointValueAnnotations", //
		};
	}

	private void copyTable(Connection sourceConn, Connection targetConn, String tableName) throws SQLException {
		LOG.warn(" --> Converting table " + tableName + "...");

		// Get the source data
		Statement sourceStmt = sourceConn.createStatement();
		ResultSet rs = sourceStmt.executeQuery("select * from " + tableName);

		// Create the insert statement from the meta data of the source.
		StringBuilder sb = new StringBuilder();
		ResultSetMetaData meta = rs.getMetaData();
		int columns = meta.getColumnCount();
		sb.append("insert into ").append(tableName).append(" (");
		for (int i = 1; i <= columns; i++) {
			if (i > 1)
				sb.append(",");
			sb.append(meta.getColumnName(i));
		}
		sb.append(") values (");
		for (int i = 1; i <= columns; i++) {
			if (i > 1)
				sb.append(",");
			sb.append("?");
		}
		sb.append(")");
		String insert = sb.toString();

		// Do the inserts. Commit every now and then so that transaction logs
		// don't get huge.
		int cnt = 0;
		int total = 0;
		int maxCnt = 1000;
		while (rs.next()) {
			PreparedStatement targetStmt = targetConn.prepareStatement(insert);
			for (int i = 1; i <= columns; i++)
				targetStmt.setObject(i, rs.getObject(i), meta.getColumnType(i));

			try {
				targetStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOG.error("Error while converting: " + e.getMessage());
			}

			cnt++;
			total++;
			if (cnt >= maxCnt) {
				targetConn.commit();
				cnt = 0;
			}

			targetStmt.close();
		}
		targetConn.commit();

		rs.close();
		sourceStmt.close();

		LOG.warn(" --> Finished converting table " + tableName + ". " + total + " records copied.");
	}

	private void copyHistoricTable(Connection sourceConn, Connection targetConn, String tableName) throws SQLException {
		LOG.warn(" --> Converting Historic table " + tableName + "...");

		// Get the source data
		Statement sourceStmt = sourceConn.createStatement();
		int total = 0;
		int cnt = 0;
		int maxCnt = 1000;
		long first, last;
		long now = System.currentTimeMillis();

		// First, let´s get the initial ID
		ResultSet rs = sourceStmt.executeQuery("select min(ts) from " + tableName);
		rs.first();
		first = rs.getLong(1);
		LOG.warn(" --> Initial TS " + first);
		rs.close();

		for (int j = 0;; j++) {
			// Check if there is still values on the table to insert
			if (now < first)
				break;

			last = first + 86400000;
			String query = "select * from " + tableName + " where ts >= " + first + " and ts < " + last;
			if (tableName.equals("pointValues"))
				query += " and pointValue > -1E200 and pointValue < 1E200";
			LOG.warn(" --> Query: " + query);
			rs = sourceStmt.executeQuery(query);

			LOG.warn(" --> Converting points of " + new Date(first).toGMTString());

			// Create the insert statement from the meta data of the source.
			StringBuilder sb = new StringBuilder();
			ResultSetMetaData meta = rs.getMetaData();
			int columns = meta.getColumnCount();
			sb.append("insert into ").append(tableName).append(" (");
			for (int i = 1; i <= columns; i++) {
				if (i > 1)
					sb.append(",");
				sb.append(meta.getColumnName(i));
			}
			sb.append(") values (");
			for (int i = 1; i <= columns; i++) {
				if (i > 1)
					sb.append(",");
				sb.append("?");
			}
			sb.append(")");
			LOG.warn(" --> InsertString: " + sb.toString());
			String insert = sb.toString();

			// Do the inserts. Commit every now and then so that transaction
			// logs
			// don't get huge.
			while (rs.next()) {

				PreparedStatement targetStmt = targetConn.prepareStatement(insert);
				for (int i = 1; i <= columns; i++)
					targetStmt.setObject(i, rs.getObject(i), meta.getColumnType(i));
				try {
					targetStmt.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					LOG.error("Error while converting: " + e.getMessage());
				}

				cnt++;
				total++;
				if (cnt >= maxCnt) {
					targetConn.commit();
					cnt = 0;
				}

				targetStmt.close();
			}

			first = last;
			targetConn.commit();
			rs.close();
		}

		sourceStmt.close();
		LOG.warn(" --> Finished converting table " + tableName + ". " + total + " records copied.");
	}
}
