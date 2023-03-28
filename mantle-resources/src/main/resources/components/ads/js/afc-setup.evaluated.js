<#assign title = (model.title?js_string)!"">

var google_ad_channel_window = google_ad_channel = Mntl.AFC.extendGoogleAdChannel('${(model.channel?js_string)!""}'),
	google_ad_client_window = google_ad_client = (docCookies.getItem('mpt') == 'A1') ? 'ca-aj-about-sem' : "${(model.clientId?js_string)!''}";

var google_hints = '${(model.hints?js_string)!""}',
	google_page_url = <#if model.url?has_content>'${model.url?js_string}'<#else>window.location.href</#if>,
	google_ad_output = 'js',
	google_max_num_ads = Mntl.AFC.getTotalAdsenseAfcs();
	google_safe = '${(model.googleSafeLevel?js_string)!"medium"}',
	google_encoding = 'latin1';

function google_ad_request_done(google_ads) {
	var containerSelector = 'article';

	debug.log('building the cpc links: afc/radlinks');
	Mntl.AFC.buildAdsenseAfc('<a href="#" class="muted" onclick="javascript: var w=450, h=425, top=(screen.height / 2) - (h / 2), left=(screen.width / 2) - (w / 2); window.open(\'${model.adTitleUrl!""}\', \'\', \'width=\' + w + \', height=\' + h + \', top=\' + top + \', left=\' + left + \', toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, copyhistory=no\'); return false;">Ads</a>', containerSelector);

}