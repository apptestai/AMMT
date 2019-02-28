### AMMT
Modify binary AndroidManifest.xml(ARSC)  
1) add attribute  
```xml
<application android:debuggable="true" ...>
```
```shell
ammt -t attr -m insert -r application,package,debuggable,true AndroidManifest.xml AndroidManifest.xml_dump
```

2) remove attribute  
```shell
ammt -t attr -m remove -r application,allowBackup AndroidManifest.xml AndroidManifest.xml_dump
```

3) add attribute  
```shell
ammt -t attr -m modify -r application,package,debuggable,false AndroidManifest.xml AndroidManifest.xml_dump
```

4) add tag  
```xml
<recevier android:name="test.ABCReceiver" ...>
```
```shell
ammt -t tag -m insert -r receiver,test.ABCReceiver,exported:true,enabled:true AndroidManifest.xml AndroidManifest.xml_dump
```

5) remove tag  
```shell
ammt -t tag -m remove -r receiver test.ABCReceiver AndroidManifest.xml AndroidManifest.xml_dump
```


### Building & Running
####1.maven  
```shell
mvn install:install-file -Dfile=libs/AXMLEditor-1.0.jar -DgroupId=cn.wjdiankong -DartifactId=AXMLEditor -Dversion=1.0 -Dpackaging=jar

mvn package

java -jar target/ammt-1.0-jar-with-dependencies.jar -h
Usage: ammt OPTIONS
Options category 'misc':
  --[no]help [-h] (a boolean; default: "false")
    print usage
  --input [-i] (a string; default: "")
    input AndroidManifest.xml
  --mode [-m] (a string; default: "modify")
    insert|remove|modify
  --output [-o] (a string; default: "AndroidManifest_out.xml")
    output AndroidManifest.xml
  --resources [-r] (a string; default: "")
    seperated by ','(e.g., -t attr -m insert -r application,package,debuggable,
    true -i AndroidManifest.xml)
  --type [-t] (a string; default: "attr")
    tag|attr
  --[no]verbose [-v] (a boolean; default: "false")
    verbose mode
```

####2.bazel   
```shell
bazel build //:ammt

./bazel-bin/ammt -h
Usage: ammt OPTIONS
Options category 'misc':
  --[no]help [-h] (a boolean; default: "false")
    print usage
  --input [-i] (a string; default: "")
    input AndroidManifest.xml
  --mode [-m] (a string; default: "modify")
    insert|remove|modify
  --output [-o] (a string; default: "AndroidManifest_out.xml")
    output AndroidManifest.xml
  --resources [-r] (a string; default: "")
    seperated by ','(e.g., -t attr -m insert -r application,package,debuggable,
    true -i AndroidManifest.xml)
  --type [-t] (a string; default: "attr")
    tag|attr
  --[no]verbose [-v] (a boolean; default: "false")
    verbose mode
```
