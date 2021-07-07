/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.api.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.openmrs.Order;
import org.openmrs.module.fhir.api.db.FHIRDAO;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * It is a default implementation of  {@link FHIRDAO}.
 */
public class HibernateFHIRDAO implements FHIRDAO {

	protected final Log log = LogFactory.getLog(this.getClass());

	private SessionFactory sessionFactory;

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	@Override
	public <Ord extends Order> List<Ord> getOrdersByAccessionNumber(String accessionNumber) {
		return getCurrentSession().createQuery("from Order o where o.accessionNumber = :accessionNumber").setString("accessionNumber", accessionNumber).list();
	}


	@Override
	public Integer getEncounterIdForObsOrder(final int orderId) {
		final int[] encounterIds = new int[1];
		getCurrentSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				String query = "SELECT distinct encounter_id FROM obs where order_id = ?";
				PreparedStatement stmt = connection.prepareStatement(query);
				stmt.setInt(1, orderId);
				ResultSet resultSet = stmt.executeQuery();
				int encounterId = -1;
				while (resultSet.next()) {
					encounterId = resultSet.getInt("encounter_id");
				}
				resultSet.close();
				encounterIds[0] = encounterId;
			}
		});
		if (encounterIds[0] > 0) {
			return encounterIds[0];
		}
		return null;
	}

	private org.hibernate.Session getCurrentSession() {
		try {
			return sessionFactory.getCurrentSession();
		}  catch (NoSuchMethodError ex) {
			try {
				Method method = sessionFactory.getClass().getMethod("getCurrentSession", null);
				return (org.hibernate.Session)method.invoke(sessionFactory, null);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to get the current hibernate session", e);
			}
		}
	}
}
