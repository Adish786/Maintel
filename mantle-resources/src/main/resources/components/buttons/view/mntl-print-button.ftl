<@component tag="form" action=requestContext.urlData.path+"?print" method="POST" target="_blank">
	<button class="mntl-print-button__btn" aria-label="Print this article.">
	    ${model.text!'Print'}
	    <@svg name="icon-print" classes="mntl-print-button__icon" />
    </button>
	<input type="hidden" value="true" name="print" />
	<input type="hidden" value="${requestContext.csrfToken}" name="CSRFToken" />
</@component>