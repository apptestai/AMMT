package(default_visibility = ["//visibility:public"])

java_import(
    name =  "kxml2",
    jars = [
        "libs/kxml2-2.3.0.jar"
    ]
)

java_import(
    name =  "guava",
    jars = [
        "libs/guava-27.0.1-jre.jar"
    ]
)

java_import(
    name =  "google-options",
    jars = [
        "libs/google-options-1.0.0.jar"
    ]
)

java_import(
    name =  "axmleditor",
    jars = [
        "libs/AXMLEditor.jar"
    ]
)

java_binary(
    name = "ammt",
    srcs = glob(["src/main/java/ai/apptest/ammt/*.java"]),
    main_class = "ai.apptest.ammt.AXML",
    deps = [
        "//:kxml2",
        "//:guava",
        "//:google-options",
        "//:axmleditor"
    ]
)