
var bundles = {
    "core" : {
        "resource" : "/assets/foobar.23232",
        "modules" : [ "a", "b", "c"]
    },
    "settings" : {
        "resource" : "/assets/foobar",
        "modules" : [ "d", "e", "f"]
    }
};

if (typeof require === "undefined" || require === null) {
    require = {
        paths : []
    }
}

if (typeof require.paths === "undefined" || require.paths === null) {
    require.paths = [];
}

for (var bundle in bundles) {
    var bundleInfo = bundles[bundle];
    for (var i in bundleInfo.modules) {
        var moduleName = bundleInfo.modules[i];
        console.log("Adding module " + moduleName + " to resource " + bundleInfo.resource);
        require.paths[moduleName] = bundleInfo.resource;
    }
}