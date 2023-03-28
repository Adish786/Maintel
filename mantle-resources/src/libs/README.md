# What is this directory?
In this directory we hard saved a couple of vendor libraries that have a an un-minified and minified format. Since these libraries are a minority of files out of the total set of js files in the repo, we decided to store both minified and unminified versions to make the build times faster.

The overhead of downloading these libraires and minifying them through our mantle-frontend-build system is high for these files. So we plan to just use copy tasks to port them our our /static/ directory

# package.json?
To identify if the libraries need to get updated via Snyk. Otherwise do not run yarn install here

# Changelog?
To track the version of the libraries, we should update the CHANGELOG.md that is also in the libs directory with the version we generated the files from. If there is custom overrides please link the ticket with details like with the mantle CHANGELOG.md in the root directory.