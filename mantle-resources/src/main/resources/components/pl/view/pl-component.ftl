<#if manifest.locations['component']?has_content>
	<#assign m = manifest.locations['component'][0] />
	<#assign c = m.component />

	<div class="mntl-pl-component">
		<#assign href = "/pattern-library" + c.categoryUri + "/" + (c.displayName!c.id)?replace('\\s+', '-', 'rm')?lower_case />

		<section class="mntl-pl-tags">
			<div class="mntl-pl-tags__tag"><#if c.module??>${c.module.definition.id}</#if></div>

			<#if c.tags?has_content>
				<#list c.tags?split(",") as tag><#assign tagClass = tag?lower_case /><#assign tagClass = tagClass?replace('\\s+', '-', 'rm') /><div class="mntl-pl-tags__tag ${tagClass}">${tag}</div></#list>
			</#if>
		</section>

		<section class="mntl-pl-markdown">
			<#if c.description?has_content>
				<h2>Description</h2>
				<div class="markdown mntl-pl-markdown__content">${(c.description)!""}</div>
			</#if>

			<#if c.documentation?has_content>
				<h2>Documentation</h2>
				<div class="markdown mntl-pl-markdown__content mntl-pl-markdown__content--documentation">${(c.documentation)?trim?html}</div>
			</#if>
		</section>

		<#if c.previewType != "none">

			<#assign iframeurl = "https://" + requestContext.serverName />

			<#if ![0,80,443]?seq_contains(requestContext.serverPort)>
				<#assign iframeurl = iframeurl + ":" + requestContext.serverPort?c />
			</#if>

			<#assign iframeurl = iframeurl + "/pattern-library-component?id=" + c.id + "&categoryUri=" + c.categoryUri />

			<#if requestContext.queryString?has_content>
				<#assign iframeurl = iframeurl + "&" + requestContext.queryString />
			</#if>

			<section>
				<h2>Preview</h2>
				<div class="preview-panel<#if c.resizable && c.previewType=="default"> resize</#if>${(c.previewType=="markup")?string(" markup","")}"<#if c.bgColor?has_content> style="background-color: ${c.bgColor}"</#if>>
					<div class="preview-panel-header">
						<div class="snap-links breakpoint-pointers"></div>
					</div>
					<div class="preview-panel-content">
						<iframe id="iframe-${c.id}" src="${iframeurl}" width="100%" height="100%" frameborder="0" name="${c.id}" allowfullscreen="true" webkitallowfullscreen="true" mozallowfullscreen="true"></iframe>
					</div>
				</div>
				<#if c.resizable && c.previewType=="default">
					<div class="preview-panel-disclaimer">
						Drag right side of container above to view example at different widths
					</div>
				</#if>
			</section>
		</#if>

		<@location name="devPanel" tag="" />
	</div>

	<script>
		window.PatternLibrary.EditForm.init({
			id: '${c.id}',
			xml: "<components>${c.toXmlString()?replace('<info>.*?</info>', (c.previewType=='markup')?string('<info><previewType>markup</previewType></info>', ''), 'rms')?replace("'", "\\'")?replace('"', '\\"')?replace("</", "<_/")}</components>".replace(new RegExp('<_/', 'g'), '</')
		});
	</script>
</#if>