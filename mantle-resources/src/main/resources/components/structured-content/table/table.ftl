<#assign tableRowIndex=-1 />

<@component tag="figure">
    <div class="mntl-sc-block-table__table-wrapper">
        <table class="mntl-sc-block-table__table">
            <#list model.columnAttributes.list as attrs>
                <colgroup style="<#if attrs.width??>width: ${attrs.width.value?string + attrs.width.unit};</#if>" span="1"></colgroup>
            </#list>

            <#if model.title?has_content || model.rowAttributes.list?first.isHeading>
                <thead>
                    <#if model.title?has_content>
                        <tr>
                            <th colspan="${model.columnAttributes.list?size}" class="mntl-sc-block-table__title">${model.title}</th>
                        </tr>
                    </#if>

                <#--
                    Only rows marked isHeader at the beginning of the data list will
                    be printed in thead. Later rows will be printed in tbody.
                -->
                <#if model.rowAttributes.list?first.isHeading>
                    <#list model.rowAttributes.list as rowAttrs>
                        <#if rowAttrs.isHeading>
                            <@tableRow
                                cells=model.tableData.list[rowAttrs?index].list
                                tag="th"
                                columnAttributes=model.columnAttributes
                            />
                            <#assign tableRowIndex=rowAttrs?index />
                        <#else>
                            <#break>
                        </#if>
                    </#list>
                </#if>
            </thead>
        </#if>

        <tbody data-check="${tableRowIndex}">
            <#list model.tableData.list as row>
                <#--
                    Continues displaying table rows in the tbody starting with the
                    first row not marked isHeader. If later rows are marked isHeading
                    they'll be output here with all cells marked up as <th>
                -->
                <#if row?index gt tableRowIndex>
                    <@tableRow
                        cells=row.list
                        tag=((model.rowAttributes.list[row?index].isHeading)!false)?then("th", "td")
                        columnAttributes=model.columnAttributes
                    />
                </#if>
            </#list>
        </tbody>
    </table>

    <#if model.caption?has_content>
        <figcaption>${model.caption}</figcaption>
    </#if>
</@component>
