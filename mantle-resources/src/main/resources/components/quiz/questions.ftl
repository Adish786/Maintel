<@component tag="section">

	<#macro answers answers hasImages>

	    <#local num = [2,3,2,3,3]/>
		<#local total = answers.totalSize />

	    <#if hasImages>
	        <#if answers.list?size lt 7>
	            <#local col_class = "answer-${num[answers.list?size-2]}" />
	        <#else>
	            <#local col_class = "answer-4" />
	        </#if>
	    <#else>
	    	<#-- change width of answer based on length of text -->
	        <#local count = 0/>
	        <#list answers.list as answer>
	            <#if answer.text?length gt count>
	                <#local count = answer.text?length/>
	            </#if>
	        </#list>

	        <#if count lt 10>
	            <#local col_class = "answer-2" />
	        <#elseif count gt 9 && count lt 30>
	            <#local col_class = "answer-2" />
	        <#else>
	            <#local col_class = "answer-1" />
	        </#if>
	    </#if>

		<ul class="answers">
			<#list answers.list as answer>
				<#local answerString = "" />
				<#list answer.values.list as value>
					<#local answerString = answerString + value.key + ":" + value.score />

					<#if value_index < answer.values.list?size - 1>
						<#local answerString = answerString + "," />
					</#if>
				</#list>

	            <li class="answer ${col_class} has-hover<#if hasImages> image-answer</#if>" data-answer-value="${answerString}" data-index="${answer_index + 1}">
	                <a>
	                    <#if hasImages>
	                    	<img src="<@thumborUrl img=answer.image maxWidth=model.answerImageWidth maxHeight=model.answerImageHeight />" />
	                    </#if>
	                    <div class="answer-text">
	                        <div class="checkbox">
								<@location name="checkbox" tag="" />
	                        </div>
	                        ${answer.text!''}
	                    </div>
	                </a>
	            </li>

			</#list>
		</ul>
	</#macro>

	<div class="questions-block">
		<#list model.document.questions.list as question>
			<div class="question" data-index="${question_index + 1}">
				<${model.questionHeadingTag!"span"} class="question-text"><span>${question_index+1}.</span> ${question.text}</${model.questionHeadingTag!"span"}>
				<#if question.image.url?has_content>
					<figure class="question-image">
						<img src="<@thumborUrl img=question.image maxWidth=model.questionImageWidth maxHeight=model.questionImageHeight />" />
						<figcaption>
					        <#if question.image.caption?has_content>${question.image.caption}.</#if>
					        <#if question.image.owner?has_content>${question.image.owner}</#if>
					    </figcaption>
				    </figure>
                </#if>
				<@answers answers=question.answers hasImages=question.answers.list[0].image.url?has_content />
				<#if question.reveal?has_content>
					<div class="reveal collapsed">
						<div class="reveal-text">
	                        <#if question.reveal.image.url?has_content>
	                        	<img src="<@thumborUrl img=question.reveal.image maxWidth=model.answerImageWidth maxHeight=model.answerImageHeight />" class="reveal-image"/>
	                        </#if>
	                        <${model.revealHeadingTag!"span"} class="reveal-title">
		                        <span class="correct inactive">${model.correctText!'Correct'}</span>
		                        <span class="wrong inactive">${model.incorrectText!'Wrong'}</span>
		                    </${model.revealHeadingTag!"span"}>
	                        ${question.reveal.text}
	                    </div>
	                </div>
	           </#if>
			</div>
			<@location name="postQuestion${question_index+1}" tag="" />
		</#list>
		<@location name="questionsBottom" tag="" />
	</div>

</@component>