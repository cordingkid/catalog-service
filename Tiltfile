# 빌드
custom_build(
    # 컨테이너 이미지 이름
    ref = 'catalog-service',
    # 컨테이너 이미지 빌드를 위한 명령
    command = './gradlew bootBuildImage --imageName $EXPECTED_REF',
    # 새로운 빌드를 시작하기 위해 지켜봐야 하는 파일
    deps = ['build.gradle', 'src']
)

# 배포
k8s_yaml(kustomize('k8s'))

# 관리
k8s_resource('catalog-service', port_forwards=['9001'])