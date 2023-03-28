<figure class="op-tracker">
	<iframe>
		<script type="text/javascript">
			<#assign gtmAccountId = model.accountId!"GTM-W9QMDMG" />
			(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src='//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);})(window,document,'script','dataLayer','${gtmAccountId}');
	
			var dataLayer = window.dataLayer || [],
				pageViewData = ${model.gtmPageView.pageViewDataAsJSON};
			
			pageViewData['event'] = 'unifiedPageview';
			pageViewData['pageviewType'] = 'facebookInstant';
			pageViewData['excludefromComscore'] = false;
			pageViewData['nativeAdEnabled'] = false;
			
			dataLayer.push(pageViewData);
		</script>	
	</iframe>
</figure>
