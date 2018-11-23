package org.openmrs.module.fhir.api.merge;

import org.openmrs.BaseOpenmrsData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TestMergeBehaviour implements MergeBehaviour<BaseOpenmrsData> {

	@Override
	public MergeResult<BaseOpenmrsData> resolveDiff(Class<? extends BaseOpenmrsData> clazz,
													BaseOpenmrsData local, BaseOpenmrsData foreign) {
		MergeResult<BaseOpenmrsData> result;
		BaseOpenmrsData merged;

		if (local.getDateChanged().after(foreign.getDateChanged())) {
			merged = deepClone(foreign);
			result = new MergeSuccess<>(clazz, local, foreign, merged, true, false);
		} else {
			result = new MergeConflict<>(clazz, local, foreign);
		}

		return result;
	}

	private BaseOpenmrsData deepClone(BaseOpenmrsData org) {
		BaseOpenmrsData clone = null;
		try {
			ByteArrayOutputStream baOUT = new ByteArrayOutputStream();
			ObjectOutputStream oOUT = new ObjectOutputStream(baOUT);
			oOUT.writeObject(org);

			ByteArrayInputStream baIN = new ByteArrayInputStream(baOUT.toByteArray());
			ObjectInputStream oIN = new ObjectInputStream(baIN);
			clone = (BaseOpenmrsData) oIN.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return clone;
	}
}

