java-hwp
========

 본 제품은 한글과컴퓨터의 한글 문서 파일(.hwp) 공개 문서를 참고하여 개발하였습니다.
 cogniti님과 https://groups.google.com/forum/#!forum/libhwp 그룹에 감사드립니다.

HWP 파일에서 텍스트를 추출하는 자바 라이브러리이며
https://github.com/cogniti/ruby-hwp 의 자바 버전입니다. 
ruby-hwp의 로직을 대부분 그대로 사용하며 
HWP 3.0버전의 문자 매핑 부분에서는 https://github.com/cogniti/libghwp 의 
hnc2unicode.c 의 로직과 매핑 데이터로 hnc2unicode.inc 파일을 사용합니다.
hnc2unicode.inc에는 특수문자 매핑을 일부 추가했습ㄴ다.

HWP 5.0 버전은 Apache-POI를 사용하여 OLE 파일을 처리합니다.


사용방법
--------

  File hwp = new File("hangul.hwp"); // 텍스트를 추출할 HWP 파일
  Writer writer = new StringWriter(); // 추출된 텍스트를 출력할 버퍼
  HwpTextExtractor.extract(hwp, writer); // 파일로부터 텍스트 추출
  String text = writer.toString(); // 추출된 텍스트
