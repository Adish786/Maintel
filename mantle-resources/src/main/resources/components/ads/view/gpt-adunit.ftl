<#assign dynamicClass="${model.gptAd.isDynamic?string('dynamic', '')}"/>
<#assign updatedId=model.gptAd.id />
<#assign updatedSizes=model.gptAd.sizes />
<#if model.useOxygen>
	<#if requestContext.userAgent.deviceCategory == 'mobile'>
		<#-- mobile: only need to map billboards and adhesive
			no need to map the leaderboards -->
		<#if model.gptAd.id == 'billboard'>
			<#assign updatedId = 'mob-square-flex-1' />
			<#assign updatedSizes="[[300, 250],[299, 251],[340,450],[1,3],'fluid']" />
		<#elseif model.gptAd.id == 'billboardfooter'>
			<#assign updatedId='mob-squarefooter-fixed-1' />
			<#assign updatedSizes="[[300, 250],[299, 251],'fluid']" />
		<#elseif model.gptAd.id == 'billboardfooter2'>
			<#assign updatedId='mob-squarefooter-fixed-2' />
			<#assign updatedSizes="[[300, 250],[299, 251],'fluid']" />
		<#elseif model.gptAd.id?contains('billboard') || model.gptAd.id?contains('dynamic-inline')>
			<#assign updatedId='mob-square-fixed' />
			<#assign updatedSizes="[[300, 250],[299, 251],'fluid']" />
		<#elseif model.gptAd.id == 'adhesive'>
			<#-- adhesives are only included on mobile -->
			<#assign updatedId='mob-adhesive-banner-fixed' />
			<#assign updatedSizes="[[320, 50], [319, 51]]" />
		</#if>
	<#else>
		<#-- tablet,pc: only need to map billboards and leaderboards
			no need to map the adhesive 
			recent update from Alana, keeping leaderboardac -->
		<#if model.gptAd.id == 'billboard'>
			<#assign updatedId = 'square-flex-1' />
			<#assign updatedSizes='[[300, 250],[299, 251],[300, 600],[300, 1050],[160, 600]]' />
		<#elseif model.gptAd.id == 'billboard2'>
			<#assign updatedId = 'square-flex-2' />
			<#assign updatedSizes="[[300, 250], [300, 251],[300, 600],[300, 601],[160, 600],[2, 1], 'fluid']" />
		<#elseif model.gptAd.id?contains('billboard')>
			<#assign updatedId='square-fixed' />
			<#assign updatedSizes="[[300, 250],[299, 251],'fluid']" />
		<#elseif model.gptAd.id?contains('dynamic-inline')>
			<#assign updatedId='leaderboard-fixed' />
			<#assign updatedSizes="[[728, 90]]" />
		<#elseif model.gptAd.id == 'leaderboard'>
			<#assign updatedId = 'leaderboard-flex-1' />
			<#assign updatedSizes='[[728, 90], [970,90], [970, 250]]' />
		<#elseif model.gptAd.id == 'leaderboard2'>
			<#assign updatedId = 'leaderboard-flex-2' />
			<#assign updatedSizes='[[728, 90], [970,90], [970, 251]]' />
		<#elseif model.gptAd.id == 'leaderboardfooter'>
			<#assign updatedId = 'leaderboardfooter-flex-1' />
			<#assign updatedSizes="[[728, 90], [728, 98], [970, 250], [970, 258], [1, 9], 'fluid']" />
		<#elseif model.gptAd.id == 'leaderboardfooter2'>
			<#assign updatedId = 'leaderboardfooter-flex-2' />
			<#assign updatedSizes="[[728, 90], [728, 98], [970, 250], [970, 258], [1, 9], 'fluid']" />
		<#elseif model.gptAd.id?contains('leaderboard') 
			&& !model.gptAd.id?contains('leaderboard-flex')
			&& !model.gptAd.id?contains('leaderboardfooter-flex')>
			<#if model.gptAd.id == 'leaderboardac'>
				<#--  keeping the id "leaderboardac", but updating its sizes  -->
				<#assign updatedSizes="[[728, 90], 'fluid', [970, 250], [1, 11], [1200, 450]]" />
			<#elseif model.gptAd.id != 'leaderboard-fixed-0'>
				<#--  any remaining leaderboards will be leaderboard-fixed  -->
				<#assign updatedId='leaderboard-fixed' />
				<#assign updatedSizes='[[728, 90]]' />
			</#if>
		</#if>
	</#if>
</#if>
<@component class="gpt ${model.gptAd.type} ${dynamicClass}">
	<@location name="preAd" tag="" />
	<div id="${updatedId}" class="wrapper" 
		<#if (model.gptAd.timedRefresh!0) gt 0>
			data-timed-refresh="${model.gptAd.timedRefresh?c}"
		</#if>

		<#if model.gptAd.timeoutRefreshOnceOnly>
			data-timeout-refresh-once-only="true"
		</#if>

		<#if model.gptAd.refreshAfterSlotRenderedElement?has_content>
			<#if model.useOxygen>
				<#if model.gptAd.refreshAfterSlotRenderedElement == 'billboard2'>
					data-refresh-after-slot-rendered="mob-square-fixed-1"
				<#else>
					data-refresh-after-slot-rendered="mob-square-fixed-2"
				</#if>
			<#else>
				data-refresh-after-slot-rendered="${model.gptAd.refreshAfterSlotRenderedElement}"
			</#if>
		</#if>
		<#if model.gptAd.isDynamic> <@compress single_line=true>
		<#-- these attributes are only need for dynamic ads because their Slot object is
		     constructed from the wrapper element; non-dynamic ads (e.g. leaderboard) are
		     constructed from the initialSlots passed to Mntl.GPT.init in gpt-definition.evaluated.js -->
		data-type="${model.gptAd.type}"
		data-pos="${model.gptAd.pos}"
		data-priority="${model.gptAd.priority}"
		data-sizes="${updatedSizes?replace("'", "&quot;")}"
		data-rtb="${model.gptAd.rtb?c}"
		data-wait-for-third-party="${model.gptAd.waitForThirdParty?c}"
		data-targeting="${utils.toJSONString(model.gptAd.targeting)?html}"
		<#if (model.gptAd.auctionFloorInfo.id)?has_content>
			data-auction-floor-id="${model.gptAd.auctionFloorInfo.id}"
			data-auction-floor-value="${model.gptAd.auctionFloorInfo.floor?c}"
		</#if>
	</@compress></#if>></div>
	<@location name="postAd" tag="" />
</@component>