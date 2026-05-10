# Crescent 프로젝트 초기 분석

## 개요

**Crescent**는 Java 기반의 HTTP 검색 엔진으로, Apache Lucene을 코어로 사용하고 Spring Framework를 웹 레이어로 사용합니다. 한글 처리에 최적화되어 있으며, 관리자 UI와 HTTP API를 통해 검색/인덱싱/사전 관리 기능을 제공합니다.

- **버전**: 0.5-SNAPSHOT
- **라이선스**: Apache License 2.0
- **주요 개발자**: need4spd (need4spd@naver.com)

---

## 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| 언어 | Java | 7 |
| 빌드 도구 | Gradle | - |
| 검색 엔진 | Apache Lucene | 4.4.0 |
| 웹 프레임워크 | Spring Framework | 3.1.2.RELEASE |
| 웹 서버 | Jetty | 9.0.6.v20130930 |
| 한글 분석기 | lucene-Korean-Analyzer | 0.7-SNAPSHOT |
| JSON 처리 | Jackson (FasterXML) | 2.2.3 |
| XML 처리 | XStream / Dom4j | 1.4.3 / 1.6.1 |
| 로깅 | SLF4J + Logback | 1.7.5 / 1.0.13 |
| 테스트 | JUnit | 4.10 |

---

## 프로젝트 구조

멀티 모듈 Gradle 프로젝트로 구성됩니다.

```
crescent/
├── crescent_core_web/          # 메인 웹 애플리케이션 모듈 (WAR)
│   ├── src/main/java/
│   │   ├── common/filter/                  # UTF-8 인코딩 필터
│   │   └── com/tistory/devyongsik/crescent/
│   │       ├── admin/          # 관리자 컨트롤러 & 서비스
│   │       ├── collection/     # 컬렉션 엔티티
│   │       ├── config/         # 설정 및 초기화
│   │       ├── index/          # 인덱싱 로직
│   │       ├── search/         # 검색 로직
│   │       ├── query/          # 쿼리 파싱
│   │       ├── logger/         # 커스텀 로깅
│   │       ├── data/           # 데이터 핸들러
│   │       └── utils/          # 유틸리티
│   ├── src/main/resources/
│   │   ├── spring/             # applicationContext.xml, action-config.xml
│   │   └── collection/         # 환경별 컬렉션 정의 (local, aws, production)
│   ├── src/test/               # JUnit 테스트
│   └── webapp/
│       ├── WEB-INF/            # web.xml
│       ├── jsp/                # JSP 뷰 (admin, common)
│       ├── js/                 # 자바스크립트
│       └── css/                # 스타일시트
│
├── crescent_utils/             # 유틸리티 모듈 (인덱싱 도구, DB→JSON 변환)
│   ├── src/main/java/
│   │   └── com/tistory/devyongsik/utils/
│   │       ├── IndexingUtil.java           # HTTP 기반 인덱싱 유틸리티
│   │       ├── MakeJsonFormFileFromDB.java # DB → JSON 변환
│   │       └── MakeJsonFormFileFromFiles.java # 파일 → JSON 변환
│   └── *.sh                    # 테스트 및 인덱싱 셸 스크립트
│
├── build.gradle                # 루트 빌드 파일 (프로필 기반 리소스 선택)
├── settings.gradle
├── README.md
└── LICENSE.txt
```

---

## 핵심 기능 및 모듈

### 1. 컬렉션 관리
XML 파일로 컬렉션을 정의하며, 필드별 타입·분석기·부스트를 설정합니다.
- `CrescentCollection` / `CrescentCollectionField` / `CrescentCollections`
- `CrescentCollectionHandler`: XML 파일 로드 및 컬렉션 관리
- `CrescentAnalyzerHolder`: 인덱싱/검색 모드별 분석기 관리

### 2. 인덱싱
- `CrescentIndexer`: 문서 추가·수정·삭제 처리
- `LuceneDocumentBuilder` / `LuceneFieldBuilder`: Map → Lucene Document 변환
- `CrescentIndexerExecutor`: 인덱싱 실행기
- `IndexWriterManager`: 컬렉션별 IndexWriter 풀 관리

### 3. 검색
- `SearchService` / `SearchServiceImpl`: 검색 비즈니스 로직
- `SearchRequest` / `SearchResult`: 검색 요청·응답 모델
- `RequestBuilder`: HTTP 요청 → SearchRequest 변환 (`@RequestParamName` 애노테이션 활용)
- `CrescentDefaultDocSearcher`: Lucene 기반 문서 검색 구현
- `CrescentSearcherManager`: 컬렉션별 IndexSearcher 풀 관리
- `CrescentHighlighter` / `CrescentFastVectorHighlighter`: 하이라이팅
- `JsonFormConverter`: 검색 결과 → JSON 변환

### 4. 쿼리 처리
- `CustomQueryStringParser` / `DefaultKeywordParser`: 커스텀 쿼리 파싱
- `CrescentSearchRequestWrapper`: SearchRequest → Lucene Query 변환

### 5. 관리자 기능

| 컨트롤러 | 경로 | 기능 |
|---------|------|------|
| `AdminMainController` | `/dictionaryManage.devys` | 사전 관리 (추가/제거/검색) |
| `SearchController` | `/search.devys` | 검색 API (JSON 반환) |
| `MorphAdminMainController` | `/morphMain.devys` | 형태소 분석 테스트 |
| `SearchTestMainController` | `/searchTest.devys` | 검색 테스트 UI |
| `IndexFileManageController` | `/indexFileManage.devys` | 인덱스 파일 관리 |
| `CollectionManageMainController` | `/collectionManageMain.devys` | 컬렉션 관리 |
| `UpdateController` | - | 인덱싱/업데이트 처리 |

---

## 아키텍처 특징

1. **계층화 구조**: Controller → Service → Searcher/Indexer 패턴
2. **멀티 컬렉션**: XML 기반 컬렉션 정의, 컬렉션별 독립적인 IndexWriter/Searcher 관리
3. **환경별 프로필**: Gradle 빌드 시 `local`, `aws`, `production` 프로필로 리소스 분리
4. **HTTP API**: `*.devys` URL 패턴, JSON 요청/응답
5. **한글 최적화**: KoreanAnalyzer를 이용한 한글 형태소 분석

---

## 테스트 구조

`crescent_core_web/src/test/java/` 하위에 단위 테스트가 위치합니다.

| 테스트 파일 | 대상 |
|-----------|------|
| `CrescentCollectionHandlerTest` | 컬렉션 XML 로드 |
| `LuceneDocumentBuilderTest` / `LuceneFieldBuilderTest` | 문서/필드 생성 |
| `IndexSearcherTest` / `SearcherManagerTest` | 인덱스 검색 |
| `CrescentDefaultDocSearcherTest` | 검색 로직 |
| `CrescentHighlighterTest` | 하이라이팅 |
| `RequestBuilderTest` / `SearchRequestBuilderTest` | 요청 파싱 |
| `SearchServiceImplTest` | 검색 서비스 통합 |
| `JsonFormConverterTest` | JSON 변환 |

테스트 데이터: `src/test/resources/sample_data_files/`, `sample_wiki_data_files/`

---

## 웹 엔드포인트 요약

| URL | 설명 |
|-----|------|
| `index.jsp` | 메인 페이지 |
| `/adminMain.devys` | 관리자 메인 |
| `/search.devys` | 검색 API |
| `/dictionaryManage.devys` | 사전 관리 |
| `/morphMain.devys` | 형태소 분석 |
| `/indexFileManage.devys` | 인덱스 파일 관리 |
| `/collectionManageMain.devys` | 컬렉션 관리 |

---

## 현재 개발 현황

### 최근 커밋 요약 (최신순)

| 커밋 | 내용 |
|------|------|
| `#103` | `@RequestParamName` 애노테이션 도입 — HTTP 파라미터 매핑 개선 |
| `#96` | 색인 관리 페이지 오류 수정 (Long 타입 필드 지원 미완료) |
| 진행중 | 인덱스 파일 관리 페이지 개발 |

### 알려진 이슈 / 미완성 기능

- **Long 타입 필드 지원 미완료**: `IndexFileManageController` 관련
- **Lucene 4.4 마이그레이션 진행 중**
- 일부 Jackson 구버전(1.9.13) 의존성 잔존 (호환성 목적)

---

## 외부 의존성

- **lucene-Korean-Analyzer**: [https://github.com/need4spd/lucene-Korean-Analyzer](https://github.com/need4spd/lucene-Korean-Analyzer)
  저자의 별도 프로젝트로, Maven 빌드 시 자동 다운로드됩니다.
