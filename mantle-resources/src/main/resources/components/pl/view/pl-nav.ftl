<@component>
	<#if (model.pagination.prev)?has_content>
		<@a class="mntl-pl-nav__button mntl-pl-nav__button--previous" href="/pattern-library"+model.pagination.prev.uri>
			<span class="mntl-pl-nav__arrow">&larr;</span>
			<span class="mntl-pl-nav__content">
				<span class="mntl-pl-nav__content__title">${model.pagination.prev.displayName!model.pagination.prev.id}</span>
			</span>
		</@a>
	</#if>

	<#if (model.pagination.next)?has_content>
		<@a class="mntl-pl-nav__button mntl-pl-nav__button--next" href="/pattern-library"+model.pagination.next.uri>
			<span class="mntl-pl-nav__content">
				<span class="mntl-pl-nav__content__title">${model.pagination.next.displayName!model.pagination.next.id}</span>
			</span>
			<span class="mntl-pl-nav__arrow">&rarr;</span>
		</@a>
	</#if>
</@component>