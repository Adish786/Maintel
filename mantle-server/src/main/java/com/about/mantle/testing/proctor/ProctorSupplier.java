package com.about.mantle.testing.proctor;

import com.about.globe.core.refresh.exception.ProctorSupplierException;
import com.indeed.proctor.common.Proctor;

@FunctionalInterface
public interface ProctorSupplier {

	Proctor supply() throws ProctorSupplierException;

}
