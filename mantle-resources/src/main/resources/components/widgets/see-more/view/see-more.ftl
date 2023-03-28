<@component class="see-more see-more-wrapper collapsed"  data_height="${model.height}">
	<@location name="content" class="see-more-content" style="max-height: ${model.height}px" />
	<div class="see-more-btn-container">		
		<div class="see-more-btn see-more-wrapper-btn link-arrowed" data-more="${model.moreText}" data-less="${model.lessText}">
			<span class="text ${model.arrowDirection!''}">${model.moreText}</span>
		</div>
	</div>
</@component>