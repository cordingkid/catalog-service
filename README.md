# catalog-service
클라우드 네이티브 스프링 인 액션 칼탈로그 서비스

### 도커를 통한 애플리케이션 컨테이너화
현재(2024-08-01)서는 클라우드 네이티브 빌드팩을 통해
도커파일 작성안하고 스프링 부트 애플리케이션 컨테이너화 진행


- ./gradlew bootBulidImage
    - 내부적으로 클라우드 네이티브 빌드팩을 사용해 애플리케이션 컨테이너 이미지로 패키징

- docker images <이미지이름>
    - 이미지의 세부 정보를 확인 가능

- docker run --rm --name <컨테이너이름> -p 8080:8080 <실행할 이미지 이름>
    - docker run : 이미지에서 컨테이너 실행
    - --rm : 실행이 끝난 후 컨테이너 삭제
    - --name catalog-service : 컨테이너의 이름
    - -p 8080:8080 : 8080 포트를 통해 컨테이너 외부로 서비스 노출
이렇게 해서 localhost:8080 으로 확인하면 애플리케이션 실행 확인 가능하다.

### 쿠버네티스 설치
미니큐브를 사용한다.

- minikube start --driver=docker
    - 미니큐브를 설치 후 도커 드라이버를 사용해 새로운 로컬 쿠버네티스 클러스터를 시작할 수 있다.

- minikube config set driver docker
    - 도커를 미니큐브 기본 드라이버로 설정

- kubectl 설치후
    - kubectl get nodes
    - 미니큐브 클러스터가 시작되었는지 확인하고 로컬 클러스터에서 노드가 실행되는지 확인

- minikube stop
    - 미니큐브 사용하지 않을때는 로컬 환경의 리소스를 아끼기 위해 미니큐브 중지

### 쿠버네티스에서 스프링 애플리케이션 실행
컨테이너 이미지로 카탈로그 서비스를 배포하도록 쿠버네티스에 명령해야 한다.

미니큐브는 도커 허브 레지스트리에서 이미지를 가져오도록 기본 설정되어 있기 때문에 로컬 레지스트리에는 엑세스할 수 없다.

그러나 수동 작업을 통해 로컬 클러스터로 가져올수 있다.

- minikube image load catalog-serivce:0.0.1-SNAPSHOT

- kubectl create deployment catalog-service --image=catalog-service-SNAPSHOT
    - 컨테이너 이미지에 배포를 생성하는 쿠버네티스 명령어다.

- kubectl get deplyment
    - 배포 객체 생성 확인
- kubectl get pod
    - 생성된 파드 객체 확인
    - 쿠버네티스의 기본 설정으로는 파드로 실행중인 애플리케이션 엑세스할 수 없다.
    - 밑에 명령어를 통해 서비스 리소스를 통해 카탈로그 서비스를 클러스터에 노출 할 수 있다.

- kubectl expose deployment catalog-service --name=catalog-service --port=8080
    - 카탈로그 서비스 애플리케이션은 8080포트를 통해 클러스터 네트워크에 노출

- kubectl get service catalog-service
    - 서비스 실행 확인

- kubectl port-forward service/catalog-service 8000:8080
    - 해당 요청은 로컬 컴퓨터에서 8000 포트에 엑세스할 때마다 카탈로그 서비스 애플리케이션을 노출하는 쿠버네티스 클러스터 내의 서비스로 전달된다.

kubectl delete service catalog-service로 서비스를 삭제 후에

kubectl delete deployment catalog-service로 배포 객체도 삭제

minikube stop
