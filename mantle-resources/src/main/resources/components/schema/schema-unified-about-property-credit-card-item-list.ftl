<@itemList name=model.name description=model.description numberOfItems=model.items?size itemListOrder=itemListOrder(model.listOptions!{})>
	
	<#list model.items as item>
		<#assign creditCard = (item.getContentsListOfType('PRODUCTSUMMARY')?size > 0)?then(item.getContentsListOfType('PRODUCTSUMMARY')[0].data.products.list[0],'') />
		<#assign reviewBody = item.getContentsListOfType('HTML') />
		<#assign position = item_index + 1 />

		<#if creditCard?has_content>

			<@itemListScElement type="ListItem" position=position name='' description='' url=''>
				<#escape x as x?json_string>
					,"item" : {
						"@type":"CreditCard"
						<#if (creditCard.name)?has_content>
							,"name": "${creditCard.name}"
						</#if>
						<#if (creditCard.issuer)?has_content>
							,"brand": "${creditCard.issuer}"
						</#if>
						<#if (creditCard.getAttribute('fullReviewUrl').value)?has_content>
							,"url": "${creditCard.getAttribute('fullReviewUrl').value}"
						</#if>
						<#if (creditCard.getAttribute('cardArtId').value)?has_content>
						    ,"image": "<@thumborUrl img=creditCard.getAttribute('cardArtId').value maxWidth=(creditCard.getAttribute('cardArtId').value.width)!10000 ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />"
						</#if>
						<#if (creditCard.getAttribute('highestRewardsEarningRateDisplay').value)?has_content>
							,"cashback": "${creditCard.getAttribute('highestRewardsEarningRateDisplay').value}"
						</#if>
						<#if (creditCard.getAttribute('RegularAPRDisplay').value)?has_content>
							,"annualPercentageRate": "${creditCard.getAttribute('RegularAPRDisplay').value}"
						</#if>
						<#if (creditCard.getAttribute('annualFeeDisplay').value)?has_content>
							,"feesAndCommissionsSpecification": "${creditCard.getAttribute('annualFeeDisplay').value}"
						</#if>
						<#if (creditCard.getAttribute('intro_apr_display_purchases').value)?has_content>
							,"interestRate": "${creditCard.getAttribute('intro_apr_display_purchases').value}"
						</#if>
						<#if (model.authorAttributions)?has_content || (creditCard.getCategoryScore('best_of_' + model.docId?c))?has_content || (reviewBody?size > 0) >
							,"Review": [
								{
									"@type": "review"
                                    <#if (model.document.guestAuthor.link.text)?has_content>
                                        ,"author": [<@rawAuthor guestAuthorData=(model.document.guestAuthor) />]
                                    <#elseif model.authorAttributions?has_content>
                                        ,<@listAuthors attributionModels=(model.authorAttributions)!{} role="author" />
                                    </#if>
									<#assign ratingValue = creditCard.getCategoryScore('best_of_' + model.docId?c) />
									<#if ratingValue?has_content && !isZero(ratingValue.score)>
										,"reviewRating": {
											"@type": "Rating",
											"ratingValue": "${ratingValue.score}",
											"worstRating": 0,
											"bestRating": 5
										}
									</#if>
									
									<#if (reviewBody?size > 0)>
										,"reviewBody":
											"	
												<#list reviewBody as content>
													${content.data.html}
												</#list>
											"
									</#if>
								}
							]
						</#if>
					}
				</#escape>	
			</@itemListScElement>

			<#sep>,</#sep><#lt>
			
		</#if>

	</#list>
</@itemList>