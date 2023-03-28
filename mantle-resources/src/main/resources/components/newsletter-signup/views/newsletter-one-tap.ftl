<#-- Modify the model by encoding the href attr and pass it down as the model for the button location.
     Note that we are doing it this (slightly hacky) way to preserve the original interface to avoid a breaking change. -->
<#assign
	buttonClass = "${manifest.component.id} ${extractClasses(model.class)}"
	buttonHref = utils.base64encode(model.attrs.href)
	buttonAttrs = model.attrs + {
		"href" : buttonHref,
		"data-encoded" : "true"
	}
	buttonModel = model + {
		"class" : buttonClass,
		"attrs" : buttonAttrs
	}
/>
<@location name="button" tag="" models=buttonModel />
