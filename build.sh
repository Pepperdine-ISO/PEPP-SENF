javac -source 1.6 -target 1.6 -cp lib/tika.jar senf/*.java streams/*.java streamsources/*.java seeds/*.java parsers/*.java
jar cmf manifest senf.jar senf/*.class senf/images/* streams/*.class streamsources/*.class
