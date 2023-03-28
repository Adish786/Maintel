<@component tag="figure" class="figure-" + (model.imageOrientation?lower_case) + " figure-" + (model.imageResolution?lower_case?replace("_", "-"))>
    <div class="figure-media">
		<@location name="overlay" tag="" />
        <@location name="image" tag="" />
    </div>
    <@location name="bottom" tag="" />
</@component>