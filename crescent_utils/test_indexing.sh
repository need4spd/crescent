cd target

java -classpath crescent_utils-0.5-SNAPSHOT.jar:. -Durl=http://127.0.0.1:8080/update.devys -Dcollection_name=sample -Dfile=/Users/need4spd/Programming/Java/workspace/crescent/crescent_core_web/src/test/resources/sample_data_files com.tistory.devyongsik.utils.IndexingUtil

java -classpath crescent_utils-0.5-SNAPSHOT.jar:. -Durl=http://127.0.0.1:8080/update.devys -Dcollection_name=sample_wiki -Dfile=/Users/need4spd/Programming/Java/workspace/crescent/crescent_core_web/src/test/resources/sample_wiki_data_files com.tistory.devyongsik.utils.IndexingUtil
