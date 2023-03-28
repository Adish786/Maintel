<#assign document = model.document />
<#assign skillLevels = ["Kid-friendly", "Beginner", "Intermediate", "Advanced"] />

<#assign workingTime = getTimeText(document.workingTimeRange, document.workingTime) />
<#assign totalTime = getTimeText(document.totalTimeRange, document.totalTime) />
<#assign yield = document.yield!'' />
<#assign skillLevel = (document.skillLevel)?has_content?then(skillLevels[document.skillLevel - 1], '') />
<#assign estimatedCost = (document.estimatedCost??)?then(document.estimatedCost.price!'', '') />

<#if (totalTime?has_content || workingTime?has_content || yield?has_content || skillLevel?has_content || estimatedCost?has_content)>
    <@component class="overview-block">
        <div class="overview-block__header ${model.overviewHeadingClass!''}">
            <span class="overview-block__header-inner">Project Overview</span>
        </div>

        <div class="overview-block__stats">
            <ul class="overview-block__stats-list">
                <#if workingTime?has_content>
                    <li class="overview-block__stats-list-item">
                        <span class="overview-block__stats-label ${model.labelClass!''}">
                            Working Time:
                        </span>
                        <span class="overview-block__stats-content ${model.valueClass!''}">
                            ${workingTime}
                        </span>
                    </li>
                </#if>

                <#if totalTime?has_content>
                    <li class="overview-block__stats-list-item">
                        <span class="overview-block__stats-label ${model.labelClass!''}">
                            Total Time:
                        </span>
                        <span class="overview-block__stats-content ${model.valueClass!''}">
                            ${totalTime}
                        </span>
                    </li>
                </#if>
       
                <#if skillLevel?has_content>
                    <li class="overview-block__stats-list-item">
                        <span class="overview-block__stats-label ${model.labelClass!''}">
                            Skill Level:
                        </span>
                        <span class="overview-block__stats-content ${model.valueClass!''}">
                            ${skillLevel}
                        </span>
                    </li>
                </#if>

                <#if estimatedCost?has_content>
                    <li class="overview-block__stats-list-item">
                        <span class="overview-block__stats-label ${model.labelClass!''}">
                            Estimated Cost:
                        </span>
                        <span class="overview-block__stats-content ${model.valueClass!''}">
                            $${estimatedCost}
                        </span>
                    </li>
                </#if>

                <#if yield?has_content>
                    <li class="overview-block__stats-list-item">
                        <span class="overview-block__stats-label ${model.labelClass!''}">
                            Yield:
                        </span>
                        <span class="overview-block__stats-content ${model.valueClass!''}">
                            ${yield}
                        </span>
                    </li>
                </#if>
            </ul>
        </div>
    </@component>
</#if>
