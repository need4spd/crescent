# Crescent JDK11 마이그레이션 — 후속 작업 및 버그 분석

## 1. "파이썬" 검색 0건 버그 분석

### 재현 조건

- 컬렉션: `sample`
- 색인 데이터:
  - `title`: "파이썬 프로그래밍 입문"
  - `dscr`: "**파이썬은** 쉽고 강력한 프로그래밍 언어입니다"
- 검색어: `파이썬`

### 검색 결과 비교

| 검색 조건 | 결과 |
|---|---|
| `keyword=파이썬` (기본: title+dscr) | **0건** ❌ |
| `keyword=파이썬&search_field=title` | **1건** ✅ |
| `keyword=파이썬&search_field=dscr` | **0건** ❌ |
| `keyword=프로그래밍` (기본) | **2건** ✅ |

### 원인 분석

#### 1단계: Lucene 쿼리 생성 구조

`DefaultKeywordParser`는 `collections.xml`의 `must` 속성에 따라 BooleanClause의 Occur를 결정합니다.

```java
// CrescentCollectionField.java
public Occur getOccur() {
    return must ? Occur.MUST : Occur.SHOULD;
}
```

```xml
<!-- collections.xml -->
<field name="title" boost="2.0" must="false" .../>  <!-- Occur.SHOULD -->
<field name="dscr"  boost="0.0" must="true"  .../>  <!-- Occur.MUST  -->
```

"파이썬" 검색 시 생성되는 Lucene 쿼리:

```
+dscr:파이썬  (title:파이썬)^2.0
```

#### 2단계: 인덱스 실제 토큰

`KoreanAnalyzer(true)` (색인용 분석기)가 생성하는 토큰:

| 필드 | 원문 | 실제 인덱스 토큰 |
|---|---|---|
| `title` | "파이썬 프로그래밍 입문" | `파이썬`, `프로그래밍`, `입문` |
| `dscr` | "파이썬은 쉽고 강력한 프로그래밍 언어입니다" | `파이썬은`, `쉽고`, `강력한`, `프로그래밍`, `언어입니다`, `언어`, `입`, `니` |

#### 3단계: 매칭 실패 경로

```
검색 쿼리 토큰 : "파이썬"   (3글자)
dscr 인덱스 토큰: "파이썬은" (4글자)  ← 조사 '은'이 제거되지 않음
```

`KoreanAnalyzer`는 **외래어(loanword) "파이썬" 뒤에 붙은 주격조사 "은/는"을 제거하지 못합니다.**
일반 한자어·고유어와 달리, 외래어 어절은 형태소 분석기가 전체를 단일 토큰으로 처리하는 한계가 있습니다.

`dscr` 필드는 `must="true"` (MUST 조건)이므로:
- dscr에서 매칭 실패 → BooleanQuery 전체가 0건
- title에 "파이썬"이 존재해도 MUST 조건 실패로 제외됨

#### 왜 "프로그래밍"은 정상 동작하는가?

dscr 텍스트 "...프로그래밍 언어..." 에서 "프로그래밍" 뒤에 조사가 붙지 않으므로,
인덱스에 "프로그래밍" 토큰 그대로 저장 → 검색 성공.

---

## 2. 수정 권장 사항 (4가지)

### 권장 1 — `dscr` 필드를 MUST에서 SHOULD로 변경 (단기 해결책)

**대상 파일**: `crescent_core_web/src/main/resources-local/collection/collections.xml`

```xml
<!-- 변경 전 -->
<field name="dscr" store="true" index="true" type="STRING" analyze="true"
       termposition="true" termoffset="true" boost="0.0" must="true" termvector="true"/>

<!-- 변경 후 -->
<field name="dscr" store="true" index="true" type="STRING" analyze="true"
       termposition="true" termoffset="true" boost="0.0" must="false" termvector="true"/>
```

변경 후 생성되는 쿼리:
```
dscr:파이썬  (title:파이썬)^2.0   ← MUST → SHOULD
```

- **장점**: 즉시 적용 가능, 재색인 불필요, title 매칭만으로도 결과 반환
- **단점**: dscr 매칭이 필수 조건에서 선택 조건으로 바뀌어 검색 정밀도 낮아질 수 있음

---

### 권장 2 — `KoreanAnalyzer` 라이브러리 수정 (근본 해결책)

**대상**: `korean-analyzer-4.x` 라이브러리 (외부 의존성)

외래어 어절에서 조사를 분리하는 로직을 형태소 분석기에 추가해야 합니다.

예시: "파이썬은" → 형태소 분석 → `파이썬` + `은(조사)` 분리 후 `파이썬` 토큰만 인덱싱

- **장점**: 분석기 수준에서 올바르게 처리, 모든 외래어에 공통 적용
- **단점**: 외부 라이브러리 소스 수정 및 재빌드/재배포 필요, 기존 인덱스 전체 재색인 필요

---

### 권장 3 — 색인 전 텍스트 전처리 (중간 해결책)

**대상 파일**: `CrescentIndexer.java` 또는 색인 데이터 전처리 단계

색인 시 dscr 텍스트를 정규표현식 등으로 전처리하여 조사를 제거하거나,
색인 입력 데이터 자체를 사전에 조사 없이 정제해서 전달합니다.

예시:
```java
// 색인 전 dscr 텍스트: "파이썬은 쉽고..." → "파이썬 쉽고..."
// (조사 제거 전처리 후 색인)
```

- **장점**: 분석기 수정 없이 인덱스 토큰 제어 가능
- **단점**: 전처리 규칙이 복잡하고 불완전할 수 있음, 기존 데이터 재색인 필요

---

### 권장 4 — Nori 등 고품질 한국어 분석기로 교체 (장기 개선)

**대상 파일**: `crescent_core_web/build.gradle`, `collections.xml`

Lucene 공식 한국어 분석기인 **Nori** (`lucene-analysis-nori`) 또는 
Elasticsearch에서 검증된 분석기로 교체하는 방안입니다.

Nori는 세종 코퍼스 기반으로 외래어 조사 분리를 포함한 정확한 한국어 형태소 분석을 지원합니다.

```groovy
// build.gradle 의존성 추가 예시
[group: 'org.apache.lucene', name: 'lucene-analysis-nori', version: "${versions.lucene}"]
```

```xml
<!-- collections.xml 분석기 교체 예시 -->
<analyzer type="indexing" className="org.apache.lucene.analysis.ko.KoreanAnalyzer" />
<analyzer type="search"   className="org.apache.lucene.analysis.ko.KoreanAnalyzer" />
```

- **장점**: 외래어 조사 분리 포함 정확한 형태소 분석, Apache 공식 지원
- **단점**: 기존 `KoreanAnalyzer` API 차이로 인한 코드 수정 필요, 기존 인덱스 전체 재색인 필요

---

## 3. 요약

| 방안 | 난이도 | 재색인 필요 | 검색 품질 |
|---|---|---|---|
| 권장 1: dscr must=false | 낮음 | 불필요 | 다소 낮아짐 |
| 권장 2: KoreanAnalyzer 수정 | 높음 | 필요 | 근본 해결 |
| 권장 3: 색인 전 전처리 | 중간 | 필요 | 부분 해결 |
| 권장 4: Nori 분석기 교체 | 높음 | 필요 | 가장 높음 |

**즉시 적용 가능한 조치**: 권장 1  
**중장기 방향**: 권장 4 (Nori 분석기 교체)

---

## 4. 선택적 후속 작업 (Phase 7-6 완료 후 제안)

### 4-1. Suffix Pattern Matching 개선

**현황**: `web.xml`의 URL 패턴이 `*.devys`이고, Spring 5.3에서 suffix pattern matching을 명시적으로 활성화(`mvc:path-matching suffix-pattern="true"`)하여 동작 중.

**문제**: suffix pattern matching은 Spring 5.3에서 **deprecated** 되었으며 향후 버전에서 제거될 예정.

**권장 방향**: `web.xml`의 서블릿 매핑을 `*.devys` → `/*`로 변경하고, 각 컨트롤러의 `@RequestMapping`에 `.devys`를 명시적으로 포함하도록 정리.

```xml
<!-- web.xml 변경 -->
<servlet-mapping>
    <servlet-name>action</servlet-name>
    <url-pattern>/*</url-pattern>  <!-- *.devys → /* -->
</servlet-mapping>
```

```java
// 컨트롤러 변경 예시
@RequestMapping("/search.devys")   // 기존: @RequestMapping("/search")
@RequestMapping("/update.devys")   // 기존: @RequestMapping("/update")
```

```xml
<!-- action-config.xml에서 suffix-pattern 설정 제거 -->
<mvc:annotation-driven />  <!-- suffix-pattern="true" 제거 -->
```

---

### 4-2. "파이썬" 검색 0건 버그 수정

위 **섹션 1~2** 참조.

---

### 4-3. 운영 환경 배포 설정

**현황**: `collections.xml`의 인덱스 디렉토리 경로가 Windows 경로(`d:/Programming/Java/crescent_index/sample`)로 하드코딩되어 있음.

**권장 방향**: 운영 서버 환경에 맞는 절대 경로로 변경. 프로파일별(`resources-local`, `resources-production` 등) `collections.xml`을 분리 관리.

```xml
<!-- 운영용 collections.xml -->
<indexingDirectory>/data/crescent_index/sample</indexingDirectory>
```

---

### 4-4. Gretty 설정 정리

**현황**: `crescent_core_web/build.gradle`의 Gretty 설정은 개발/테스트 목적으로 추가된 것.

**권장 방향**: 운영 환경에서는 실제 WAS(Tomcat 등)에 WAR 파일을 배포. Gretty는 로컬 개발 전용임을 주석으로 명시하거나, 별도 `build-dev.gradle`로 분리하는 것을 검토.

```groovy
// 개발 전용임을 명시
// ※ 운영 배포는 ./gradlew war 후 생성된 WAR를 WAS에 배포
gretty {
    httpPort = 8080
    contextPath = '/crescent'
    servletContainer = 'jetty9.4'
    ...
}
```
