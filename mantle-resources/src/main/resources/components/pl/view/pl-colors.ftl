<link href="https://fonts.googleapis.com/css?family=RobotoDraft:400,500,700,400italic" rel="stylesheet" type="text/css">
<@component>
	<ul class="colors">
		<#list model.colors?keys?sort as hsbString>
			<#assign variable = model.colors[hsbString][0] />
			<#assign color = model.colors[hsbString][1] />

			<li>
				<div class="swatch" style="background-color: ${color};"></div>
				<div class="swatch-hex">${color}</div>
				<div class="swatch-name">${variable}</div>
			</li>
		</#list>
	</ul>
</@component>