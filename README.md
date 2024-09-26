###항해플러스 1주차 Test Driven Development

6기 8팀 김태현

Step 2
* 동시성 제어 방식에 대한 분석 및 보고서 작성 



동시성(Concurrency)이란?

-> 실제로 동시에 실행하는 것이 아니라. Context Switching 함으로써 "동시에 실행되는 것처럼 보이게 하는 것"


동시에 오는 요청을 제어 할려면 어떻게 해야 될까?

-> 요청이 오는 순서대로 실행하면 된다.


순서대로? Frist In Frist Out? 

-> Queue를 사용하면 되겠구나!

---

####ConcurrentLinkedQueue
자바에서 동시성을 다루기 위한 자료구조.


해당 큐로 모든 요청들을 다 보내면 된다는 생각을 했음

해당 Queue에 요청들을 넣기 위해 Reqeust라는 요청 객체를 생성했고 충전, 사용의 요청이 들어올 때마다 요청객체가 큐에 들어가게 된다.

그 후 acationRequest를 통해서 해당 큐에 요청이 없을 때까지 수행한다.

---

###동시성 제어 테스트

####ExecutoerService

각기 다른 Thread를 생성해서 원하는 작업을 진행할 수 있다.

.execute()를 통해서 실행하고 .shutdown()을 통해서 종료한다.



####CountDownLatch 

CountDownLatch 객체를 사용해서 Thread에서 작업 실행될 때 .countDown()을 해준다.

모든 작업이 완료되어서 제대로 테스트를 진행하려면 각 Thread의 작업이 완료될 때(CountDownLatch가 0이 될 때)까지 .await()를 통해 대기한다.

그 후 모든 Thread가 종료되고 나서 테스트 검증을 해야 된다.






위의 세 가지를 가지고 동시성 제어 테스트를 진행하였습니다.







처음이라 익숙하지 않았고 어려웠던 첫 번째 과제.

좀 더 TDD가 익숙해진다면 다시 해당 과제를 다시 진행해보면 색다를 것 같다.
