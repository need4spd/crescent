# Crescent 한국어 분석기 Nori 전환 계획

## 배경

`dscr must=false`(c7b839e)는 외래어+조사 검색 0건 버그의 **단기 임시 조치**다.
근본 원인은 현재 `korean-analyzer-4.x`(`com.tistory.devyongsik.analyzer.KoreanAnalyzer`)가
외래어 어절의 조사를 분리하지 못하는 한계다.

예: `"파이썬은"` → 토큰 `[파이썬은]` (조사 "은" 미분리)

근본 해결책은 Apache Lucene 공식 한국어 분석기인 **Nori**(`lucene-analysis-nori`)로
교체하는 것이다. Nori는 세종 코퍼스 기반으로 외래어 조사 분리를 포함한 정확한 형태소 분석을 지원한다.

예: `"파이썬은"` → 토큰 `[파이썬]` (조사 분리됨)

---

## 발견한 결합 관계 (중요)

`korean-analyzer-4.x` 라이브러리는 **두 가지**를 제공한다.

1. **`KoreanAnalyzer` 클래스** → Nori(`org.apache.lucene.analysis.ko.KoreanAnalyzer`)로 교체 대상
2. **사전 관리 서브시스템** (`DictionaryFactory`, `DictionaryType`, `DictionaryProperties`)
   → 관리자 화면의 **사전 관리** 기능에서 사용 중

문제: **Nori에는 동적 사전 관리 API가 없다.** Nori는 분석기 생성 시점에 정적 사용자 사전
(`UserDictionary`)을 로드하는 방식이라, 런타임에 단어를 추가/삭제/재빌드하는 현재 관리자 기능과
구조가 다르다.

### 사전 관리에 의존하는 코드

| 파일 | 의존 내용 |
|------|----------|
| `admin/service/DictionaryServiceImpl.java` | `DictionaryFactory`, `DictionaryProperties`, `DictionaryType` 전면 의존 |
| `admin/controller/AdminMainController.java` | `DictionaryType`, 사전 관리 매핑 5개 (`dictionaryManage*`) |
| `admin/service/DictionaryService.java` | `DictionaryType` 인터페이스 시그니처 |
| `webapp/jsp/admin/dictionaryManage.jsp` | 사전 관리 화면 |

> `admin/service/MorphServiceImpl.java`는 컬렉션에 설정된 분석기를 그대로 사용하므로
> 분석기 비종속 → Nori 전환에 영향 없음.

---

## Nori 사전 관리 가능성

기존 4종 사전을 Nori 구성요소로 매핑할 수 있다.

| 기존 사전 | Nori 대응 |
|-----------|-----------|
| 명사사전 | `UserDictionary` (사용자 명사 등록) |
| 복합명사사전 | `UserDictionary` (`세종시 세종 시` 형식의 분해 규칙) |
| 불용어사전 | `StopFilter` + 불용어 파일, 또는 `KoreanPartOfSpeechStopFilter`(품사 기반) |
| 동의어사전 | `SynonymGraphFilter` (lucene-analysis-common, 이미 의존성 보유) |

Nori `KoreanAnalyzer` 생성자:

```java
new KoreanAnalyzer(userDict, decompoundMode, stopTags, outputUnknownUnigrams)
```

**핵심 차이**: Nori의 `UserDictionary`는 불변(immutable)이다. 런타임에 단어를 추가하려면
사전 객체를 새로 만들어 **분석기를 재생성**해야 한다. 현재 구조가 컬렉션별로 분석기를 보관
(`collection.setIndexingModeAnalyzer()`)하므로, 재빌드 시 새 분석기로 교체하는 방식으로 구현 가능하다.

---

## 단계 계획

### Phase 1 — 분석기만 Nori로 교체 (이번 브랜치: `feature/nori-analyzer`) ✅ 완료

목표: 검색을 Nori 기본 분석기로 동작시키고, 빌드/테스트를 녹색으로 유지한다.

1. ✅ `lucene-analysis-nori:9.10.0` 의존성 추가 (`crescent_core_web/build.gradle`)
2. ✅ `collections.xml` 4개 프로필(test/local/production/aws) 분석기를 Nori로 교체
   ```xml
   <!-- 변경 전 -->
   <analyzer type="indexing" className="com.tistory.devyongsik.analyzer.KoreanAnalyzer" constructor-args="true" />
   <analyzer type="search"   className="com.tistory.devyongsik.analyzer.KoreanAnalyzer" constructor-args="false" />

   <!-- 변경 후 -->
   <analyzer type="indexing" className="org.apache.lucene.analysis.ko.KoreanAnalyzer" />
   <analyzer type="search"   className="org.apache.lucene.analysis.ko.KoreanAnalyzer" />
   ```
3. ✅ `CrescentCollectionHandler` 분석기 생성 로직 — 변경 불필요
   - `constructor-args` 속성 미지정 시 기본 생성자 경로로 동작 확인 (Nori 무인자 생성자 사용)
4. ✅ 토큰 결과 변경에 맞춰 테스트 assertion 수정
   - `CustomQueryStringParserTest` (6건): `공부`에서 `공` 토큰 미생성 → 제거
   - `DefaultKeywordParserTest`: `나이키청바지` → `[나이키][청][바지]`
   - `CrescentSearchRequestWrapperTest.getQuery`: `공` 토큰 제거
   - `CrescentHighlighterTest`:
     - `highlightUsage`: devyongsik `KoreanAnalyzer` import 제거 → `WhitespaceAnalyzer` 사용
     - `fastVectorTest`: dscr 검색어 `입니다` → `텍스트` (Nori 토큰 `[텍스트][이][0]`에 맞춤)
5. ✅ `korean-analyzer-4.x` 의존성 **유지**
   - 기존 사전 관리 UI 컴파일·동작 유지 (Nori가 그 사전을 사용하지 않는 임시 분리 상태)

**Phase 1 완료 기준**: ✅ `./gradlew clean test` 78건 통과, ✅ `./gradlew war` 성공.
검색 쿼리가 Nori 토큰으로 생성됨을 테스트로 확인.

#### Nori 실측 토큰 (참고)

| 입력 | Nori 토큰 |
|------|-----------|
| `파이썬은 쉽고 강력한 프로그래밍 언어입니다` | `[파이썬] [쉽] [강력] [프로그래밍] [언어] [이]` |
| `나이키청바지` | `[나이키] [청] [바지]` |
| `청바지` | `[청] [바지]` |
| `공부` | `[공부]` |
| `텍스트 입니다0` | `[텍스트] [이] [0]` |

→ 목표였던 외래어 조사 분리 확인: `파이썬은` → `[파이썬]`

### Phase 2 — Nori 사전 관리 시스템 구축 ✅ 완료

목표: Nori가 커스텀 사전을 실제로 사용하도록 하고, 사전 관리 UI를 Nori 사전에 연결한다.

1. ✅ 커스텀 Nori 분석기 작성: `analyzer/CrescentNoriAnalyzer.java`
   체인: `KoreanTokenizer(UserDictionary)` → `KoreanPartOfSpeechStopFilter`
   → `KoreanReadingFormFilter` → `LowerCaseFilter` → `StopFilter(불용어)`
   → `SynonymGraphFilter(동의어)` [색인 모드: + `FlattenGraphFilter`]
   - collections.xml에서 `constructor-args="true"`(색인)/`"false"`(검색)로 모드 지정
2. ✅ 사전 홀더 + 서비스 재작성
   - `dictionary/CrescentDictionaryType.java`: 로컬 enum (CUSTOM/COMPOUND/STOP/SYNONYM)
   - `dictionary/CrescentNoriDictionary.java`: 싱글톤. 4개 파일 로드, 단어 get/add/remove/find/write,
     Nori 컴포넌트(UserDictionary/CharArraySet/SynonymMap) 빌드·재빌드
     - 명사(custom.txt) + 복합명사(compounds.txt `N:A,B` → `N A B`) → UserDictionary
     - 불용어(stop.txt) → CharArraySet
     - 동의어(synonym.txt `a,b,c`) → SynonymMap
   - `DictionaryServiceImpl`: CrescentNoriDictionary에 위임, rebuild 시
     `CrescentCollectionHandler.rebuildAnalyzers()`로 모든 컬렉션 분석기 재생성
3. ✅ 사전 관리 UI 연결
   - `DictionaryService` 인터페이스 / `AdminMainController` → `CrescentDictionaryType` 사용
   - 기존 JSP/매핑(단어 추가/삭제/찾기/형태소 테스트)은 그대로 동작
4. ✅ `korean-analyzer-4.x` 의존성 **완전 제거**
   - `SearchRequestValidator`의 `org.apache.lucene.analysis.kr.utils.StringUtil`
     → `org.apache.commons.lang.StringUtils`로 교체
   - `CrescentCollectionHandler`: 분석기 생성 로직을 `instantiateAnalyzers()`로 추출,
     `rebuildAnalyzers()` 추가

**Phase 2 완료 기준**: ✅ 사전 단어 추가 + rebuild 시 재생성된 분석기가 검색/형태소분석에 반영됨
(`DictionaryRebuildTest`로 검증), ✅ `./gradlew clean test` 84건 통과, ✅ `./gradlew war` 성공.

#### CrescentNoriAnalyzer 사전 적용 실측 (참고)

| 입력 | 결과 | 적용 사전 |
|------|------|-----------|
| `맥북에어` | `[맥북] [에어]` | 복합명사 (compounds.txt) |
| `나이키청바지` | `[나이키] [청바지]` | 사용자 명사 (custom.txt: 청바지) |
| `오라클` | `[오라클] [oracle]` | 동의어 (synonym.txt) |
| `를` | (제거) | 불용어 (stop.txt) |
| `공부` | `[공] [부]` | (사용자 사전 로드로 분절 변화) |

> 주의: 사용자 사전(약 5700개 명사) 로드로 일부 일반어 분절이 Phase 1 기본 Nori와 달라진다.
> 예) `공부` → `[공][부]`. custom.txt의 단일자 명사 등 사전 데이터 품질 점검은 별도 과제.

---

## 리스크 및 고려 사항

1. **검색 품질 변화**: 바이그램 방식 → 형태소 방식으로 변경되어 일부 검색 결과가 달라질 수 있음.
   충분한 QA 필요.
2. **전체 재색인 필요**: 토크나이즈 결과가 달라지므로 기존 인덱스 전체 재색인 필요.
3. **사전 마이그레이션**: 기존 명사/불용어/동의어/복합명사 사전을 Nori 포맷으로 변환 필요 (Phase 2).
4. **테스트 대량 수정**: 토큰 결과 변경으로 분석기 의존 테스트의 기대값을 전면 업데이트해야 함.
5. **별도 브랜치 진행**: `feature/nori-analyzer` 브랜치에서 독립적으로 작업, master 영향 최소화.

---

## 남은 권장 후속 작업

Phase 1·2 완료(PR: [#112](https://github.com/need4spd/crescent/pull/112)) 이후 남은 작업.

### 1. 브랜치 병합 (진행 중)

- `feature/nori-analyzer` → master PR [#112](https://github.com/need4spd/crescent/pull/112) 생성 완료.
- 리뷰 후 병합.

### 2. 사전 데이터 품질 점검 ✅ 완료

custom.txt 점검 및 정제 완료 (5694줄 → 5104줄).

- **단일 글자 명사 66종 제거**: 공·물·옷·책·집·차 등 일반 음절 + 톰·폰·컵 등 1음절 외래어.
  과분절(`공부` → `[공][부]`) 해소. 제거 후 `공부` → `[공부]`로 정상화, Nori 기본 형태소 모델이 처리.
- **중복 523줄 제거**: 442종 중복(아이나비 10회 등) → 고유 5170종.
- **빈 줄 2개 제거**, **CRLF → LF 정규화**.
- compounds.txt(분해규칙 5건 정상)·stop.txt·synonym.txt는 문제 없어 유지.
- 관련 테스트 assertion을 `공부` 단일 토큰으로 재조정 (84건 통과).

> 남은 사전 품질 개선(저빈도 명사 정리, 동의어/불용어 확충 등)은 운영 데이터 기반으로 지속.

### 3. 관리자 화면 smoke test (수동/브라우저)

- jQuery 3.7.1 업그레이드 + 사전 관리 UI 동작을 브라우저에서 직접 확인 (자동 테스트로 검증 어려운 영역).
- 점검 항목:
  - 사전 관리: 명사/불용어/동의어/복합명사 단어 추가·삭제·찾기
  - 형태소 분석 테스트 (Ajax) — Nori 토큰 표시 확인
  - 추가/삭제 후 검색 테스트에 반영되는지 (rebuild 전파)
  - 컬렉션 관리 폼, 인덱스 파일 관리, 메뉴 이동

### 4. (선택) DecompoundMode 튜닝

- 현재 `KoreanTokenizer.DEFAULT_DECOMPOUND`(DISCARD) 사용.
- 복합어 원형 보존이 필요하면 `MIXED` 검토 (색인량 증가 trade-off).
- 변경 시 분석기 의존 테스트 기대값 재확정 필요.

---

## 토큰 분석 차이 예시 (참고)

**"파이썬은 쉽고 강력한 프로그래밍 언어입니다"**

```
korean-analyzer-4.x: [파이썬은] [쉽고] [강력한] [프로그래밍] [언어입니다] [언어] [입] [니]
Nori:                [파이썬] [쉽] [강력] [프로그래밍] [언어]
```

**"나이키청바지"**

```
korean-analyzer-4.x: [나이키청바지] [나이키] [청바지]   (바이그램 + 사전)
Nori:                [나이키] [청바지]                  (형태소 분리)
```

> Nori 실제 토큰은 `DecompoundMode`(NONE/DISCARD/MIXED)와 사용자 사전 설정에 따라 달라진다.
> Phase 1 적용 후 `DefaultKeywordParserTest` 등에서 실제 토큰을 확인하여 assertion을 확정한다.
