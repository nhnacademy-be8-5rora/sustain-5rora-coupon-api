package store.aurora;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)  // JUnit 5 extension
@SpringBootTest(classes = CouponApplication.class)
class CouponApplicationTest {

    @Test
    void contextLoads() {
        // 애플리케이션 컨텍스트가 잘 로드되는지 확인하는 테스트
        // 특별한 검증 로직 없이 context가 로드되면 성공
    }
}