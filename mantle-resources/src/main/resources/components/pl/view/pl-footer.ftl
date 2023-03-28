<div class="mntl-pl-footer">
	<div class="mntl-pl-footer__coverage-list is-hidden js-coverage-list-open">
		<div class="mntl-pl-footer__coverage-header">
			<div class="mntl-pl-footer__coverage-list-title">
				Needs Work 🔨
			</div>

			<div class="mntl-pl-footer__close-coverage js-coverage-list-close">
				X
			</div>
		</div>

		<div class="mntl-pl-footer__coverage-container">
			<div class="mntl-pl-footer__coverage-description">
				The following ${model.patternLibraryCoverage.unsupportedComponents?size} components are missing entries for the pattern library.
			</div>

			<div class="mntl-pl-footer__coverage-scroll-container">
				<ul class="mntl-pl-footer__coverage-entries">
					<#list model.patternLibraryCoverage.unsupportedComponents as unsupportedComponent>
						<li class="mntl-pl-footer__unsupported-entry">
							${unsupportedComponent}
						</li>
					</#list>
				</ul>
			</div>
		</div>

		<div class="mntl-pl-footer__coverage-footer">
			<div class="mntl-pl-footer__coverage-footer-text">
				${model.patternLibraryCoverage.unsupportedComponents?size} / ${model.patternLibraryCoverage.infoComponents + model.patternLibraryCoverage.unsupportedComponents?size} Components Undocumented
			</div>
		</div>
	</div>

	<div class="mntl-pl-footer__coverage js-coverage-trigger">
		<div class="mntl-pl-footer__coverage-count">
			<div class="mntl-pl-footer__coverage-count-container">
				<span class="mntl-pl-footer__coverage-count-documented">${model.patternLibraryCoverage.infoComponents}</span>
				<span class="mntl-pl-footer__coverage-count-divider">/</span>
				<span class="mntl-pl-footer__coverage-count-total">${model.patternLibraryCoverage.infoComponents + model.patternLibraryCoverage.unsupportedComponents?size}</span>
			</div>
		</div>
		<div class="mntl-pl-footer__coverage-label">
			<div class="mntl-pl-footer__coverage-label-text">
				Components Documented
			</div>
		</div>
	</div>

	<div class="mntl-pl-footer__copyright">&copy; ${.now?string("yyyy")} About, Inc. (Dotdash) &mdash; All rights reserved.</div>
</div>