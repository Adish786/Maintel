package com.about.mantle.model.gtm;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.defaultString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableSet;

import freemarker.template.utility.StringUtil;

@JsonDeserialize(builder=MantleGTM.Builder.class)
public class MantleGTM {
	private static final Set<String> EXCLUDED_METHODS = ImmutableSet.of("getAbTests", "getEnvironment",
			"getPageViewDataAsJSON", "getClass");
	private final List<ABTest> abTests;
	private final EnvironmentData environment;
	private final String fullUrl;
	private final String title;
	private final String contentGroup;
	private final Long documentId;
	private final String authorId;
	private final String lastEditingAuthorId;
	private final String lastEditingUserId;
	private final String templateId;
	private final String experienceType;
	private final String entryType;
	private final Integer numOfArticleWords;
	private final Boolean excludeFromComscore;
	private final String description;
	private final Integer numOfMapLabels;
	private final String internalSessionId;
	private final String internalRequestId;
	private final String muid;
	private final String hid;
	private final String revenueGroup;
	private final String experienceTypeName;
	private final String recircDocIdsFooter;
	private final Boolean euTrafficFlag;
	private final Boolean isGoogleBot;
	private final String country;
	private final String mantleVersion; 
	private final String commerceVersion;
	private final String templateName;
	private final String viewType;
	private final String primaryTaxonomyIds;
	private final String primaryTaxonomyNames;

	@SuppressWarnings("unchecked")
	public MantleGTM(@SuppressWarnings("rawtypes") Builder builder) {
		this.abTests = builder.abTests;
		this.environment = builder.environment;
		this.fullUrl = builder.fullUrl;
		this.title = defaultString(builder.title, "");
		this.contentGroup = defaultString(builder.contentGroup, "");
		this.documentId = builder.documentId;
		this.authorId = defaultString(builder.authorId, "");
		this.lastEditingAuthorId = defaultString(builder.lastEditingAuthorId, "");
		this.lastEditingUserId = defaultString(builder.lastEditingUserId, "");
		this.templateId = defaultString(builder.templateId, "");
		this.experienceType = defaultString(builder.experienceType, "single page");
		this.entryType = defaultString(builder.entryType, "direct");
		this.numOfArticleWords = builder.numOfArticleWords;
		this.excludeFromComscore = defaultIfNull(builder.excludeFromComscore, false);
		this.description = defaultString(builder.description, "");
		this.numOfMapLabels = builder.numOfMapLabels;
		this.internalSessionId = defaultString(builder.internalSessionId, "");
		this.internalRequestId = defaultString(builder.internalRequestId, "");
		this.muid = defaultString(builder.muid, "");
		this.hid = defaultString(builder.hid, "");
		this.revenueGroup = defaultIfNull(builder.revenueGroup, "");
		this.experienceTypeName = defaultString(builder.experienceTypeName, "");
		this.recircDocIdsFooter = defaultString(builder.recircDocIdsFooter, "");
		this.euTrafficFlag = defaultIfNull(builder.euTrafficFlag, false);
		this.isGoogleBot = defaultIfNull(builder.isGoogleBot, false);
		this.country = defaultString(builder.country, "");
		this.mantleVersion = defaultString(builder.mantleVersion, "");
		this.commerceVersion = defaultString(builder.commerceVersion, "");
		this.templateName = defaultString(builder.templateName, "");
		this.viewType = defaultString(builder.viewType, "");
		this.primaryTaxonomyIds = defaultString(builder.primaryTaxonomyIds, "");
		this.primaryTaxonomyNames = defaultString(builder.primaryTaxonomyNames, "");

	}

	public List<ABTest> getAbTests() {
		return abTests;
	}

	public EnvironmentData getEnvironment() {
		return environment;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public String getTitle() {
		return title;
	}

	public String getContentGroup() {
		return contentGroup;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public String getAuthorId() {
		return authorId;
	}
	
	public String getLastEditingAuthorId() {
		return lastEditingAuthorId;
	}
	
	public String getLastEditingUserId() {
		return lastEditingUserId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public String getExperienceType() {
		return experienceType;
	}

	public String getEntryType() {
		return entryType;
	}

	public Integer getNumOfArticleWords() {
		return numOfArticleWords;
	}

	public Boolean getExcludeFromComscore() {
		return excludeFromComscore;
	}

	@Deprecated
	public String getSocialImage() {
		return null;
	}

	public String getDescription() {
		return description;
	}

	public Integer getNumOfMapLabels() {
		return numOfMapLabels;
	}
	
	public String getInternalSessionId() {
		return internalSessionId;
	}
	
	public String getInternalRequestId() {
		return internalRequestId;
	}

	public String getMuid() {
		return muid;
	}

	public String getHid() {
		return hid;
	}

    public String getRevenueGroup() {
    	return this.revenueGroup;
    }
	
    public String getexperienceTypeName() {
        return this.experienceTypeName;
    }

    public String getRecircDocIdsFooter() {
		return recircDocIdsFooter;
	}

    public Boolean getEuTrafficFlag() {
		return euTrafficFlag;
	}

	public Boolean getIsGoogleBot() { return isGoogleBot; }

	public String getCountry() {
		return country;
	}

	protected Set<String> excludedMethods() {
		return EXCLUDED_METHODS;
	}

	private String toPageViewField(Method method) {
		char chars[] = method.getName().substring(3).toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	private boolean isPageViewMethod(Method method) {
		return method.getName().startsWith("get") && !excludedMethods().contains(method.getName());
	}

	public String getPageViewDataAsJSON() throws ClassNotFoundException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		StringBuilder sb = new StringBuilder(1200);
		Class<?> c = getClass();
		Method[] methods = c.getMethods();
		boolean first = true;
		sb.append('{');
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (!isPageViewMethod(method)) continue;
			String fieldName = toPageViewField(method);
			Object object = method.invoke(this);
			
			if(object == null) continue;
			
			if (first) first = false;
			else sb.append(',');
			sb.append('"').append(fieldName).append('"').append(':');
			if (object == null) {
				sb.append('"').append('"');
			} else if (object instanceof String) {
				sb.append('"').append(StringUtil.javaScriptStringEnc((String) object)).append('"');
				if (fieldName.equals("fullUrl")) {
					sb.append(" + location.hash");
				} else if (fieldName.equals("title")) {
					sb.append(" || document.title || ''");
				}
			} else {
				sb.append(object);
			}
		}
		sb.append('}');
		return sb.toString();
	}

	public String getMantleVersion() {
		return mantleVersion;
	}

	public String getCommerceVersion() {
		return commerceVersion;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getViewType() {
		return viewType;
	}

	public String getPrimaryTaxonomyIds() {
		return primaryTaxonomyIds;
	}

	public String getPrimaryTaxonomyNames() {
		return primaryTaxonomyNames;
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder<B extends Builder<B>> {
		private List<ABTest> abTests;
		private EnvironmentData environment;
		private String fullUrl;
		private String title;
		private String contentGroup;
		private Long documentId;
		private String authorId;
		private String lastEditingAuthorId;
		private String lastEditingUserId;
		private String templateId;
		private String experienceType;
		private String entryType;
		private Integer numOfArticleWords;
		private Boolean excludeFromComscore;
		private String description;
		private Integer numOfMapLabels;
		private String internalSessionId;
		private String internalRequestId;
		private String muid;
		private String hid;
		private String revenueGroup;
		private String experienceTypeName;
		private String recircDocIdsFooter;
		private Boolean euTrafficFlag;
		private Boolean isGoogleBot;
		private String country;
		private String mantleVersion; 
		private String commerceVersion;
		private String templateName;
		private String viewType;
		private String primaryTaxonomyIds;
		private String primaryTaxonomyNames;
		private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd");

		@SuppressWarnings("unchecked")
		public B abTests(List<ABTest> abTests) {
			this.abTests = abTests;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B environment(EnvironmentData environment) {
			this.environment = environment;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B fullUrl(String fullUrl) {
			this.fullUrl = fullUrl;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B title(String title) {
			this.title = title;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B contentGroup(String contentGroup) {
			this.contentGroup = contentGroup;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B documentId(Long documentId) {
			this.documentId = documentId;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B authorId(String authorId) {
			this.authorId = authorId;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B lastEditingAuthorId(String lastEditingAuthorId) {
			this.lastEditingAuthorId = lastEditingAuthorId;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B lastEditingUserId(String lastEditingUserId) {
			this.lastEditingUserId = lastEditingUserId;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B templateId(String templateId) {
			this.templateId = templateId;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B experienceType(String experienceType) {
			this.experienceType = experienceType;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B entryType(String pageviewType) {
			this.entryType = pageviewType;
			return (B) this;
		}
		
		private String formatDateTime(DateTime dt) {
            return dt != null ? dt.toString(Builder.timeFormatter) : "";
        }

		@SuppressWarnings("unchecked")
		public B numOfArticleWords(Integer numOfArticleWords) {
			this.numOfArticleWords = numOfArticleWords;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B excludeFromComscore(Boolean excludeFromComscore) {
			this.excludeFromComscore = excludeFromComscore;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		@Deprecated
		public B socialImage(String socialImage) {
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B description(String description) {
			this.description = description;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B numOfMapLabels(Integer numOfMapLabels) {
			this.numOfMapLabels = numOfMapLabels;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B internalSessionId(String internalSessionId) {
			this.internalSessionId = internalSessionId;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B internalRequestId(String internalRequestId) {
			this.internalRequestId = internalRequestId;
			return (B) this;
		}

		public B muid(String muid) {
			this.muid = muid;
			return (B) this;
		}

		public B hid(String hid) {
			this.hid = hid;
			return (B) this;
		}
	
        @SuppressWarnings("unchecked")
        public B revenueGroup(String revenueGroup) {
            this.revenueGroup = revenueGroup;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B experienceTypeName(String experienceTypeName) {
            this.experienceTypeName = experienceTypeName;
            return (B) this;
        }
        
        @SuppressWarnings("unchecked")
        public B recircDocIdsFooter(String recircDocIdsFooter) {
            this.recircDocIdsFooter = recircDocIdsFooter;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B euTrafficFlag(Boolean euTrafficFlag) {
            this.euTrafficFlag = euTrafficFlag;
            return (B) this;
        }

		@SuppressWarnings("unchecked")
		public B isGoogleBot(Boolean isGoogleBot) {
			this.isGoogleBot = isGoogleBot;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B country(String country) {
			this.country = country;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B mantleVersion(String mantleVersion) {
			this.mantleVersion = mantleVersion;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B commerceVersion(String commerceVersion) {
			this.commerceVersion = commerceVersion;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B templateName(String templateName) {
			this.templateName = templateName;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B viewType(String viewType) {
			this.viewType = viewType;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B primaryTaxonomyIds(String primaryTaxonomyIds) {
			this.primaryTaxonomyIds = primaryTaxonomyIds;
			return (B) this;
		}
		
		@SuppressWarnings("unchecked")
		public B primaryTaxonomyNames(String primaryTaxonomyNames) {
			this.primaryTaxonomyNames = primaryTaxonomyNames;
			return (B) this;
		}

		public MantleGTM build() {
			return new MantleGTM(this);
		}
	}

}
