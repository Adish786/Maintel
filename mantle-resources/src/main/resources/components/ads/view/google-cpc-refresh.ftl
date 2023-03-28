<!DOCTYPE html>
<html itemtype="http://schema.org/WebPage">

<head></head>

<body>
<script>
	document.domain = "${model.domain?js_string}";
	var google_hints = window.parent.Mntl.AFC.getDynamicHints() || window.parent.google_hints_window,
		google_page_url = window.parent.Mntl.AFC.getDynamicHref() || window.parent.location.href,
		google_ad_output = 'js',
		google_max_num_ads = window.parent.Mntl.AFC.getTotalAdsenseAfcs(),
		google_safe = window.parent.google_safe_window,
		google_encoding = 'latin1',
		google_ad_channel = window.parent.Mntl.AFC.getDynamicChannel() || window.parent.google_ad_channel_window,
		google_ad_client = window.parent.google_ad_client_window;
		
	
	function google_ad_request_done(google_ads) {
	    window.parent.google_ads = google_ads;
	    window.parent.google_ad_request_done(google_ads)
	}
</script>
<script type='text/javascript' src='//pagead2.googlesyndication.com/pagead/show_ads.js'></script>
</body>
</html>