<@component tag="nav" role="navigation">
	<div class="mntl-dotdash-universal-nav__content">
        <@svg name="mntl-dotdash-universal-nav__logo" />
		<span class="mntl-dotdash-universal-nav__text">
			<#-- nbsp; needed between publishing and family so those words wrap together -->
		    ${model.verticalName!'Website'} is part of the <@a href="https://www.dotdashmeredith.com" class="mntl-dotdash-universal-nav__text--link" target="_blank">Dotdash Meredith</@a> publishing&nbsp;family.
		</span>
	</div>
</@component>