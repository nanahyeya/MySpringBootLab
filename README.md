### [실습2-4] Spring Boot와 JPA(Java Persistence API) 활용
* Entity,Repository,RepositoryTest
* Book 과 BookDetail  1:1 (OneToOne) 연관관계
    * FetchType.LAZY vs FetchType.EAGER
    * @JoinColumn, mappedBy
    * 연관관계의 주인(owner 와 종속(non-owner)
    * Owner(BookDetail), Non-Owner(Book)
    * FK(외래키) 가지고 있는 쪽이 주인(owner)이다.
* Service
* DTO(Data Transfer Object)
* Controller