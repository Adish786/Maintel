<#if model.appId??>
    <#assign appId = model.appId + utils.parseInstanceId(manifest.instanceId) />
    <#assign appName = model.appId />
<#else>
    <#assign appId = utils.camelCaseInstanceId(manifest.instanceId) />
    <#assign appName = appId />
</#if>

(function() {
    const vueAppName = Mntl.VueInGlobe.apps.${appName};
    vueAppName.${appId} = cloneDeep(vueAppName.baseApp);

    const vueApp = vueAppName.${appId};

    if (vueApp) {
        <#if model.data?has_content>
            const currData = vueApp.data();
            const newData = ${utils.toJSONString(model.data)};
            const newKeys = Object.keys(newData);

            newKeys.forEach(function (key) {
                currData[key] = newData[key];
            });

            vueApp.data = function() {
                return currData;
            };
        </#if>

        let props = {}
        <#if model.props?has_content>
            props = ${utils.toJSONString(model.props)};
        </#if>

        let vueAppConfig = {
            name: '${appId}',
            el: '#${appId}',
            render: function (h) {
                return h(vueApp, { props });
            }
        }

        if (Mntl.VueInGlobe.roots && Mntl.VueInGlobe.roots.${appName}) {
            const root = cloneDeep(Mntl.VueInGlobe.roots.${appName}.baseRoot)
            vueAppConfig = Object.assign(vueAppConfig, root)
        }

        if (vueAppName.datastore) {
            vueAppConfig.store = vueAppName.datastore;
        }

        Mntl.VueInGlobe.comps.${appId} = new Vue(vueAppConfig);

    } else {
        console.error('Cannot load: ${appId}');
    }
})();
