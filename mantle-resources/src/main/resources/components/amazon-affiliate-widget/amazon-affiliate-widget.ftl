<@component data_image_size="${model.imageSize}">
	<div class="mntl-amazon-affiliate-widget__photo js-photo is-hidden" data-default-image="${model.imageUrl!''}">
		<img src="" class="mntl-amazon-affiliate-widget__img" alt="${model.alt!''}" />
	</div>

    <div class="mntl-amazon-affiliate-widget__container">
		<div class="mntl-amazon-affiliate-widget__product-info js-product-info is-hidden">
		    <span class="mntl-amazon-affiliate-widget__regular-price js-regular-price"></span>
		    <span class="mntl-amazon-affiliate-widget__price js-price"></span>
		    <div class="mntl-amazon-affiliate-widget__prime js-prime is-hidden">
			    <img class="mntl-amazon-affiliate-widget__prime-image" src="${model.primeImagePath}" alt="Prime" />
		    </div>
		    <span class="mntl-amazon-affiliate-widget__savings js-savings is-hidden">
		        You save:
		        <span class="mntl-amazon-affiliate-widget__amount-saved js-amount-saved"></span>
		        <span class="mntl-amazon-affiliate-widget__amount-saved-pct js-amount-saved-pct"></span>
		    </span>
		</div>
		<button class="mntl-amazon-affiliate-widget__button">${model.label}</button>
	</div>
</@component>
