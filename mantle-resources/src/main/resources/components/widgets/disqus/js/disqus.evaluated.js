Mntl.Disqus.init({
    forumName: '${model.forumName}',
    url: '${model.url}',
    identifier: '${model.identifier?c!''}',
    title: '${model.title?replace("'", "\\'")}'
});
