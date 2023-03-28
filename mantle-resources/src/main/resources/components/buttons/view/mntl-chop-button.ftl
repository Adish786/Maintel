<@button class="chop-btn btn btn-chop ${extractClasses(model.class)}">
	<div class="btn-chop-top">
		<span class="btn-title">${model.title}</span>
		<@location name="svg" tag="" /> 
	</div>
	<span class="btn-text">${model.text!''}</span>
</@button>
