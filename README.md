# Crescent

Crescent는 HTTP 기반으로 동작하는 Java 검색 엔진입니다. 웹 애플리케이션으로 제공되며 Apache Lucene을 검색 코어로 사용합니다. 컬렉션, 필드, 분석기, 인덱스 경로는 XML 설정으로 관리하고, 검색과 색인은 HTTP API로 수행합니다.

## 주요 기능

- Lucene 기반 전문 검색
- 컬렉션별 독립 인덱스 관리
- XML 기반 컬렉션/필드/Analyzer 설정
- 한국어 Analyzer 기반 형태소 분석
- JSON 기반 색인 API
- 검색 API 및 검색 테스트 화면
- 사전 관리, 형태소 분석 테스트, 컬렉션 관리, 인덱스 파일 관리용 관리자 화면

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| 언어 | Java 11 |
| 빌드 | Gradle 7.6.4 Wrapper |
| 웹 프레임워크 | Spring Framework 5.3.39 |
| 검색 엔진 | Apache Lucene 9.10.0 |
| 웹 서버 | Gretty + Jetty 9.4 |
| JSON | Jackson 2.15.4 |
| 로깅 | SLF4J 2.0.16 + Logback 1.4.14 |
| 테스트 | JUnit 4.13.2 |
| 패키징 | WAR |

## 프로젝트 구조

```text
crescent/
├── crescent_core_web/       # 메인 웹 애플리케이션 모듈
│   ├── src/main/java/       # 검색, 색인, 관리자 기능
│   ├── src/main/resources/  # Spring 설정, 기본 리소스
│   ├── src/main/resources-local/
│   ├── src/main/resources-aws/
│   ├── src/main/resources-production/
│   ├── src/test/            # 단위 테스트
│   └── webapp/              # JSP, CSS, JS, web.xml
├── crescent_utils/          # 색인용 JSON 생성 및 HTTP 색인 유틸리티
├── build.gradle
├── settings.gradle
└── gradlew
```

## 모듈

### crescent_core_web

검색 엔진의 메인 웹 모듈입니다.

- `collection`: 컬렉션/필드/Analyzer 설정 모델
- `config`: 컬렉션 XML 로딩 및 Spring 컨텍스트 보조
- `index`: Lucene Document/Field 생성
- `index.indexer`: 문서 추가, 수정, 삭제, 커밋
- `query`: 키워드/커스텀 쿼리 파싱
- `search`: 검색 요청/응답, 검색 실행, 하이라이팅
- `admin`: 관리자 컨트롤러와 서비스

### crescent_utils

색인 데이터를 준비하거나 서버에 전송하기 위한 보조 도구 모듈입니다.

- 파일 목록을 JSON 색인 포맷으로 변환
- DB 조회 결과를 JSON 색인 포맷으로 변환
- HTTP 색인 API 호출 스크립트 제공

## 빌드와 테스트

전체 테스트 실행:

```bash
./gradlew test
```

WAR 패키징:

```bash
./gradlew war
```

현재 테스트는 `crescent_core_web` 기준 72개가 있으며, 검색/색인/쿼리 파서/하이라이팅/설정 로딩 등을 검증합니다.

## 로컬 실행

Gretty를 사용해 로컬 서버를 실행할 수 있습니다.

```bash
./gradlew :crescent_core_web:appRun
```

기본 설정:

- URL: `http://localhost:8080/crescent`
- Context path: `/crescent`
- Servlet mapping: `*.devys`
- Gretty 실행 모드: `-DrunningMode=test`

## 인덱스 경로 설정

인덱스 디렉터리 경로는 `collections.xml`의 `<indexingDirectory>`로 지정합니다.

| 값 | 동작 |
| --- | --- |
| `memory` | 메모리 인덱스 사용 (테스트 전용) |
| 절대 경로 (`/data/index/sample`) | 해당 경로 그대로 사용 |
| 상대 경로 (`crescent_index/sample`) | `crescentHome` 또는 `webapp.root` 기준으로 변환 |

상대 경로 해석 우선순위:

1. `-DcrescentHome=<경로>` 시스템 프로퍼티가 설정된 경우 → `${crescentHome}/crescent_index/sample`
2. `webapp.root` 시스템 프로퍼티가 설정된 경우 → `${webapp.root}crescent_index/sample`
3. 둘 다 없으면 → 현재 작업 디렉터리 기준 상대 경로

**crescentHome 지정 예시:**

```bash
# Gretty로 로컬 실행 시
./gradlew :crescent_core_web:appRun -DcrescentHome=/opt/crescent

# WAR 배포 후 Tomcat/Jetty JVM 옵션으로 설정
java -DcrescentHome=/opt/crescent -jar ...

# 또는 CATALINA_OPTS / JAVA_OPTS 환경변수로 설정
export CATALINA_OPTS="-DcrescentHome=/opt/crescent"
```

`crescentHome`을 지정하면 `collections.xml`의 위치도 `${crescentHome}/collections.xml`로 변경됩니다.

## 프로필과 리소스

기본 프로필은 `local`입니다.

```bash
./gradlew test -Pprofile=local
./gradlew war -Pprofile=production
```

프로필별 리소스 디렉터리:

- `crescent_core_web/src/main/resources-local`
- `crescent_core_web/src/main/resources-aws`
- `crescent_core_web/src/main/resources-production`

컬렉션 설정은 일반적으로 다음 위치에서 관리합니다.

```text
collection/collections.xml
```

테스트 실행 시에는 `-DrunningMode=test` 설정에 따라 `collection/test-collections.xml`을 사용합니다.

## 검색 API

검색 API는 `GET /crescent/search.devys`로 호출합니다.

예시:

```bash
curl "http://localhost:8080/crescent/search.devys?col_name=sample&keyword=프로그래밍"
```

주요 파라미터:

| 파라미터 | 설명 | 기본값 |
| --- | --- | --- |
| `col_name` | 컬렉션 이름 | `sample` |
| `keyword` | 검색어 | 빈 문자열 |
| `page_num` | 페이지 번호 | `1` |
| `page_size` | 페이지 크기 | `10` |
| `search_field` | 검색 대상 필드 목록 | 컬렉션 기본 필드 |
| `sort` | 정렬 조건. 예: `board_id_sort desc` | 없음 |
| `cq` | 커스텀 쿼리 | 없음 |
| `rq` | 정규식 쿼리 | 없음 |
| `ft` | 필터 쿼리 | 없음 |

응답은 JSON입니다.

## 색인 API

색인 API는 `POST /crescent/update.devys`로 호출합니다.

예시:

```bash
curl -X POST \
  "http://localhost:8080/crescent/update.devys?collection_name=sample" \
  -H "Content-Type: application/json" \
  -d '{
    "command": "ADD",
    "indexingType": "BULK",
    "documentList": [
      {
        "board_id": "1",
        "title": "파이썬 프로그래밍 입문",
        "dscr": "파이썬은 쉽고 강력한 프로그래밍 언어입니다",
        "creuser": "admin"
      }
    ]
  }'
```

지원 명령:

- `ADD`
- `UPDATE`
- `UPDATE_BY_FIELD_VALUE`
- `DELETE`

지원 색인 타입:

- `BULK`: 색인 후 commit 수행
- `INCREMENTAL`: 색인 작업만 수행

## 컬렉션 설정

컬렉션은 XML로 정의합니다. 주요 설정은 다음과 같습니다.

- Analyzer 클래스와 생성자 인자
- 인덱스 디렉터리
- 필드명, 타입, 저장 여부, 색인 여부, 분석 여부
- 기본 검색 필드
- 정렬용 필드

예시:

```xml
<collection name="sample">
  <analyzers>
    <analyzer type="indexing" className="com.tistory.devyongsik.analyzer.KoreanAnalyzer" constructor-args="true" />
    <analyzer type="search" className="com.tistory.devyongsik.analyzer.KoreanAnalyzer" constructor-args="false" />
  </analyzers>
  <indexingDirectory>memory</indexingDirectory>
  <fields>
    <field name="title" store="true" index="true" type="STRING" analyze="true" boost="2.0" must="false" />
    <field name="dscr" store="true" index="true" type="STRING" analyze="true" must="true" termvector="true" />
  </fields>
</collection>
```

## 한국어 Analyzer

Crescent는 `com.tistory.devyongsik:korean-analyzer-4.x:0.7-SNAPSHOT`을 사용합니다. 이 의존성은 로컬 Maven 저장소와 `need4spd` Maven snapshot 저장소에서 해석합니다.

## 라이선스

Apache License 2.0을 따릅니다. 자세한 내용은 `LICENSE.txt`를 참고하세요.
