<#assign manifest_bak=manifest />
<#assign manifest = model.manifest!manifest.root />
<svg class="mntl-svg-resource is-hidden">
    <defs>
		<#list manifest.allSvgs as svg>
			<symbol id="${svg.resourceHandle.id}">
				<#include "/${svg.resource.path}" parse=false />
			</symbol>
		</#list>
	</defs>
</svg>
<#assign manifest = manifest_bak />