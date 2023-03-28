<@component>
	<div class="article-feedback__rating-section js-rating-section">
		<div class="article-feedback__heading">${model.ratingSectionHeading!''}</div>
		<@location name="rating-section" tag="" />
	</div>

	<div class="article-feedback__success-section js-success-section is-hidden">
		<div class="article-feedback__heading">${model.successSectionHeading!''}</div>
		<@location name="success-section" />
	</div>

	<div class="article-feedback__feedback-section js-feedback-section is-hidden">
		<div class="article-feedback__heading">${model.feedbackSectionHeading!''}</div>
		<@location name="feedback-section" tag="" />
		<form action="/ugc-feedback" method="post" class="article-feedback__feedback-form js-feedback-form is-hidden">
			<textarea class="article-feedback__feedback-text js-feedback-text" placeholder="${model.placeholderText!''}" required="required" maxlength="1500"></textarea>
			<@location name="submit-button" tag="" />
			<input type="hidden" name="doc-id" value="${model.docId?c}" class="js-doc-id" />
		</form>
	</div>
</@component>