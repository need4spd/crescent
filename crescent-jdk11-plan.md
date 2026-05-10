# Crescent Java 7 → Java 11 마이그레이션 계획

## 현황 요약

| 항목 | 이전 | 현재(완료) | 상태 |
|------|------|-----------|------|
| Java | 1.7 | 11 | ✅ 완료 |
| Gradle | 구버전 (폐기 DSL) | 7.6.4 | ✅ 완료 |
| Spring | 3.1.2.RELEASE | 5.3.39 | ✅ 완료 |
| Lucene | 4.4.0 | 9.10.0 | ✅ 완료 |
| Jackson | 1.9.x + 2.2.x 혼용 | 2.15.4 단일화 | ✅ 완료 |
| Servlet API | 2.5 | 4.0 | ✅ 완료 |
| Groovy | 2.1.6 | 3.0.22 | ✅ 완료 |
| Jetty (utils) | 9.0.6 | 9.4.56.v20240826 | ✅ 완료 |
| commons-dbcp | 1.4 | commons-dbcp2:2.12.0 | ✅ 완료 |
| dom4j | 1.6.1 | 2.1.4 | ✅ 완료 |
| XStream | 1.4.3 | 1.4.20 | ✅ 완료 |
| Logback | 1.0.13 | 1.4.14 | ✅ 완료 |
| SLF4J | 1.6.6 | 2.0.16 | ✅ 완료 |

**빌드/테스트 상태**: `./gradlew test` → **BUILD SUCCESSFUL** (72 tests, 0 failures)

---

## 사전 작업. `korean-analyzer` Java 11 빌드 준비 ✅ 완료

- GitHub 저장소: `need4spd/lucene-Korean-Analyzer` (master 브랜치)
- Lucene 9.10.0 + Java 11 호환 버전으로 업그레이드 완료
- `./gradlew :korean-analyzer-4.x:publishToMavenLocal` 로 로컬 Maven 저장소에 배포
- 루트 `build.gradle`에 `mavenLocal()` 저장소 추가

---

## Phase 1. Gradle 빌드 스크립트 현대화 ✅ 완료

### 완료 내용

**루트 `build.gradle`**
- `task hello << { }` → `tasks.register('hello') { doLast { } }`
- `javaVersion = '1.7'` 제거 → `java { sourceCompatibility/targetCompatibility = VERSION_11 }`
- HTTP SpringSource 저장소 제거, `mavenCentral()` + `mavenLocal()` 유지
- `webAppDirName = 'webapp'` → `war { webAppDirectory = file('webapp') }` (Gradle 9 호환)
- `tasks.withType(ProcessResources).configureEach { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }` 추가 (logback.groovy 중복 리소스 방지)
- `tasks.withType(JavaCompile).configureEach { options.compilerArgs += ['--release', '11', '-Xlint:deprecation'] }` 추가

**`crescent_core_web/build.gradle`**
- eclipse 플러그인 및 설정 블록 전체 제거
- `compile` → `implementation`, `testRuntime` → `testImplementation`

**`crescent_utils/build.gradle`**
- eclipse 플러그인 및 설정 블록 전체 제거
- `compile` → `implementation`

---

## Phase 2. Spring 3.1.2 → 5.3.39 업그레이드 ✅ 완료

### 완료 내용

**의존성 변경** (`crescent_core_web/build.gradle`)
- `org.springframework.web.servlet:3.1.2.RELEASE` → `org.springframework:spring-webmvc:5.3.39`
- groupId 변경: `org.springframework.XXX` → `org.springframework`
- `compileOnly`: `javax.servlet:javax.servlet-api:4.0.1`
- `testImplementation`에도 `javax.servlet-api:4.0.1` 추가 (MockHttpServletRequest 호환)
- `javax.annotation:javax.annotation-api:1.3.2` 추가 (JDK 11에서 제거된 `@PostConstruct` 지원)

**`applicationContext.xml`**
- 스키마: `spring-beans-3.0.xsd` → `spring-beans.xsd` (버전 미지정)
- `<ref local="messageSource">` → `<ref bean="messageSource">` (Spring 4+ `local` 속성 제거)

**`action-config.xml`**
- 스키마 버전 업데이트
- `DefaultAnnotationHandlerMapping` → `RequestMappingHandlerMapping` (Spring 4에서 폐기)

---

## Phase 3. Jackson 의존성 통합 ✅ 완료

### 완료 내용

- `org.codehaus.jackson:jackson-mapper-asl:1.9.13` 제거
- `org.codehaus.jackson:jackson-core-asl:1.9.13` 제거
- `com.fasterxml.jackson.core:jackson-core/databind/annotations:2.15.4` 유지
- 두 서브프로젝트 전체 `org.codehaus.jackson` import → `com.fasterxml.jackson` 교체
  - `org.codehaus.jackson.map.ObjectMapper` → `com.fasterxml.jackson.databind.ObjectMapper`
  - `org.codehaus.jackson.JsonGenerationException` → `com.fasterxml.jackson.core.JsonGenerationException`
  - `org.codehaus.jackson.map.JsonMappingException` → `com.fasterxml.jackson.databind.JsonMappingException`

---

## Phase 4. 기타 의존성 업그레이드 ✅ 완료

### 완료 내용

**`crescent_core_web/build.gradle`**
- `org.codehaus.groovy:groovy-all:2.1.6` → `3.0.22`
- `com.thoughtworks.xstream:xstream:1.4.3` → `1.4.20`
- `ch.qos.logback:logback-core/classic:1.0.13` → `1.4.14`
- `org.slf4j:slf4j-api:1.6.6` → `2.0.16`
- `dom4j:dom4j:1.6.1` → `org.dom4j:dom4j:2.1.4` (groupId 변경)
- `jaxen:jaxen:1.1.4` 추가 (dom4j XPath 지원)

**`crescent_utils/build.gradle`**
- `commons-dbcp:commons-dbcp:1.4` → `org.apache.commons:commons-dbcp2:2.12.0`
- `commons-pool:commons-pool:1.6` → `org.apache.commons:commons-pool2:2.12.0`
- `org.eclipse.jetty:jetty-server:9.0.6` → `9.4.56.v20240826`
- `javax.xml.bind:jaxb-api:2.3.1` 추가 (JDK 11에서 제거된 JAXB 지원)
- `com.thoughtworks.xstream:xstream:1.4.3` → `1.4.20`
- Jackson 동일하게 2.15.4로 통합
- `MakeJsonFormFileFromDB.java`: `org.apache.commons.dbcp.BasicDataSource` → `org.apache.commons.dbcp2.BasicDataSource`

---

## Phase 5. Lucene 4.4.0 → 9.10.0 업그레이드 ✅ 완료

### 완료 내용

**의존성 변경**
- `lucene-analyzers-common` → `lucene-analysis-common` (artifact 이름 변경)
- `lucene-sandbox:9.10.0` 추가
- 버전: `4.4.0` → `9.10.0`

**수정된 주요 파일 및 변경 내용**

| 파일 | 주요 변경 |
|------|----------|
| `IndexWriterManager.java` | `RAMDirectory` → `ByteBuffersDirectory`, `FSDirectory.open(File)` → `.open(Path)`, `Version` 파라미터 제거, `IndexWriterConfig(Version, Analyzer)` → `IndexWriterConfig(Analyzer)` |
| `CrescentSearcherManager.java` | `SearcherManager(writer, true, factory)` → `SearcherManager(writer, factory)` |
| `LuceneFieldBuilder.java` | `setIndexed(true)` 제거 → `IndexOptions` 사용, `NumericField` → `LongField`/`IntField` |
| `LogInfo.java` | `Filter` 타입 → `Query` 타입으로 변경 |
| `CustomQueryStringParser.java` | `BooleanQuery.Builder` 패턴, `BoostQuery` 래퍼, `NumericRangeQuery` → `LongField.newRangeQuery()`, `RegexQuery` → `AutomatonQuery(new RegExp(pattern).toAutomaton())` |
| `DefaultKeywordParser.java` | `BooleanQuery.Builder`, `BoostQuery` 래퍼 |
| `CrescentSearchRequestWrapper.java` | `getFilter()→Filter` → `getFilterQuery()→Query`, `QueryWrapperFilter` 제거 |
| `CrescentDefaultDocSearcher.java` | 필터: `BooleanQuery.Builder`로 통합, `(int) topDocs.totalHits.value`, `indexSearcher.storedFields().document()` |
| `CrescentIndexer.java` | `setCommitData(map)` → `setLiveCommitData(map.entrySet())` |
| `IndexFileManageServiceImpl.java` | `FSDirectory.open(.toPath())`, `MultiTerms.getTerms()`, `terms.iterator()`, `NumericUtils.sortableBytesToLong/Int` |
| `LongDecoder.java` | `NumericUtils.prefixCodedToLong` → `NumericUtils.sortableBytesToLong(ref.bytes, ref.offset)` |
| `CrescentCollectionHandler.java` | `newInstance()` → `getDeclaredConstructor().newInstance()`, XStream 1.4.20 보안 설정(`allowTypesByWildcard`) 추가 |
| `RequestBuilder.java` | `clazz.newInstance()` → `clazz.getDeclaredConstructor().newInstance()` |
| `MorphServiceImpl.java` | `Token` 클래스 제거, `CharTermAttribute`/`OffsetAttribute`/`TypeAttribute` 직접 사용 |

---

## Phase 6. Java 11 호환성 검증 ✅ 완료

### 완료 내용

- `javax.annotation.PostConstruct` 제거 대응: `javax.annotation-api:1.3.2` compileOnly + testImplementation 추가
- `javax.xml.bind` 제거 대응: `jaxb-api:2.3.1` 추가 (crescent_utils)
- `Class.newInstance()` deprecated 대응: `getDeclaredConstructor().newInstance()` 교체
- `IndexSearcher.doc(int)` deprecated 대응: `storedFields().document(int)` 교체

---

## Phase 7. 테스트 및 검증

### 완료 내용 ✅

**1. `./gradlew compileJava`** → 성공 (deprecation 경고만 존재)

**2. `./gradlew test`** → **72 tests, 0 failures**

수정된 테스트 파일 목록:

| 파일 | 수정 내용 |
|------|----------|
| `CrescentHighlighterTest.java` | `RAMDirectory` → `ByteBuffersDirectory`, `Version` 제거, `IndexOptions` import 수정 |
| `CrescentSearchRequestWrapperTest.java` | `getFilter()` → `getFilterQuery()`, `Filter` → `Query` |
| `CustomQueryStringParserTest.java` | `Filter` 제거, `BooleanQuery.Builder`, `IntField.newRangeQuery()`, Lucene 9 assertion 업데이트 |
| `IndexSearcherTest.java` | `(int) topDocs.totalHits.value` |
| `ResourceLoaderTest.java` | dom4j 2.x: `selectNodes()` 반환타입 `List<Node>` (Element 캐스팅) |
| `RequestBuilderTest.java` | `javax.servlet-api` testImplementation 추가 |
| `DefaultKeywordParserTest.java` | 한국어 분석기 신버전 토큰 + Lucene 9 BoostQuery 포맷 반영 |
| `JsonFormConverterTest.java` | `HashMap` → `LinkedHashMap` (Jackson 2.x 직렬화 순서 보장) |

**테스트 assertion 변경 배경:**
- 한국어 분석기 업그레이드로 토크나이즈 결과 변경 (`파이` 바이그램 삭제, `공` 유니그램 추가)
- Lucene 9 `BoostQuery.toString()` 포맷 변경: `term^2.0` → `(term)^2.0`

**3. `./gradlew war`** → **BUILD SUCCESSFUL**

### 남은 작업 ⬜

**4. 로컬 서버 기동 확인** ✅ 완료

- Gretty 3.1.3 + Jetty 9.4 조합으로 서버 기동 성공
- Spring 컨텍스트 정상 로드
- `CrescentCollectionHandler` 초기화 (collections.xml 읽기) 정상
- Lucene 인덱스 디렉토리 접근 정상

**5. 인덱싱 API 확인** ✅ 완료

```
POST /crescent/update.devys?collection_name=sample
Content-type: application/json
{"command":"ADD","indexingType":"BULK","documentList":[...]}
```
- BULK / INCREMENTAL 색인 모두 정상 동작

**6. 검색 API 확인** ✅ 완료

```
GET /crescent/search.devys?collection=sample&keyword=프로그래밍
```
- 검색 결과 반환 정상
- 한국어 분석기 동작 확인
- **발견된 버그**: 외래어 + 조사 조합(예: "파이썬은")에서 0건 반환 → `post-follow-up.md` 참조

---

## 단계별 진행 현황

```
✅ 사전 작업 (korean-analyzer 빌드)
✅ Phase 1 (Gradle 7.6.4)
✅ Phase 2 (Spring 5.3.39)
✅ Phase 3 (Jackson 2.15.4 단일화)
✅ Phase 4 (기타 의존성)
✅ Phase 5 (Lucene 9.10.0)
✅ Phase 6 (Java 11 호환성)
✅ Phase 7-1 (컴파일 검증)
✅ Phase 7-2 (단위 테스트: 72/72 통과)
✅ Phase 7-3 (WAR 패키징)
✅ Phase 7-4 (로컬 서버 기동)
✅ Phase 7-5 (인덱싱 API 검증)
✅ Phase 7-6 (검색 API 검증)
```
