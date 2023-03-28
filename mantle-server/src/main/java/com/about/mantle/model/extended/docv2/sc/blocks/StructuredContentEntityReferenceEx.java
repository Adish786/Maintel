package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.about.mantle.model.extended.docv2.sc.EntityReferenceInfo;

/**
 * This block is a placeholder for data coming from an external source.
 * The {@link EntityReferenceInfo} refers to a specific entity in an external data source.
 * The propertyName is a type-specific property of the entity.
 * e.g. the entity reference may refer to a drug and the propertyName might be "precautions"
 */
public class StructuredContentEntityReferenceEx extends AbstractStructuredContentContentEx<StructuredContentEntityReferenceEx.StructuredContentEntityReferenceDataEx> {

	public static class StructuredContentEntityReferenceDataEx extends AbstractStructuredContentDataEx {

		private EntityReferenceInfo entityReferenceInfo;
		private String propertyName;

		public EntityReferenceInfo getEntityReferenceInfo() {
			return entityReferenceInfo;
		}

		public void setEntityReferenceInfo(EntityReferenceInfo entityReferenceInfo) {
			this.entityReferenceInfo = entityReferenceInfo;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public String toString() {
			return "StructuredContentEntityReferenceDataEx [entityReferenceInfo=" + entityReferenceInfo
					+ ", propertyName=" + propertyName + "]";
		}

	}

}
