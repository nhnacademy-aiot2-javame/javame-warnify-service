<h1 align="center">⚠️javame-warnify-service⚠️</h1>
<div align="center">
Trans-Service에서 임계치가 초과 됬을때 사용자에게 알림을 보내주는 서비스 입니다.
</br></br>
알람은 SMS, Email, Dooray를 선택하거나 통합적으로 전송이 가능하며, DB에 저장이 됩니다.
</div>
</br>
<div align="center">
<h3 tabindex="-1" class="heading-element" dir="auto">사용스택</h3>



  
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">

![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Linux](https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=linux&logoColor=black)</br>
<img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white">
![SonarQube](https://img.shields.io/badge/SonarQube-black?style=for-the-badge&logo=sonarqube&logoColor=4E9BCD)
</br>
</br>
</div>


<div align="center">
<h3 tabindex="-1" class="heading-element" dir="auto">사용 외부 API</h3>
  
![archiecture](https://github.com/user-attachments/assets/49911324-f43b-4743-b239-3fe482c39c29)

<h3 tabindex="-1" class="heading-element" dir="auto">1. Mqtt Broker → Trans-Service → Rule-API 임계치 확인</h3> 
<li>임계치 확인 이상값 발생시 Warnify-Service에 응답</li>
<li>회사 도메인 확인 후 POST에 해당하는 메신저로 응답 알림</li>

<h3 tabindex="-1" class="heading-element" dir="auto">2. Warnify-Service↔ MySQL</h3> 
<li>Warnify 저장 및 조회</li>

</div>
<div align=center>
<h3 tabindex="-1" class="heading-element" dir="auto">Test Coverage (Targe:Line coverage 80%)</h3> 
  <li>
    Line coverage: 85.3%
  </li>

![스크린샷 2025-06-11 00-27-20](https://github.com/user-attachments/assets/bfcba850-1f47-4f75-aca1-7478cafad082)

</div>
