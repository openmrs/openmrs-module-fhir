package org.openmrs.module.fhir.api.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.FHIRHelperService;

import java.lang.reflect.Method;

public class FHIRHelperServiceImpl extends BaseOpenmrsService implements FHIRHelperService {

	DbSessionFactory sessionFactory;

	Method method;

	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.module.fhir.api.FHIRHelperService#getObjectByUuid(Class, String)
	 */
	@Override
	public <T> T getObjectByUuid(Class<? extends T> type, String uuid) {
		return type.cast(getSession().createCriteria(type).add(Restrictions.eq("uuid", uuid)).uniqueResult());
	}

	private DbSession getSession() {
		if (method == null) {
			try {
				return sessionFactory.getCurrentSession();
			}
			catch (NoSuchMethodError error) {
				//Supports Hibernate 3 by casting org.hibernate.classic.Session to org.hibernate.Session
				try {
					method = sessionFactory.getClass().getMethod("getCurrentSession");
					return (DbSession) method.invoke(sessionFactory);
				}
				catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		} else {
			try {
				return (DbSession) method.invoke(sessionFactory);
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
