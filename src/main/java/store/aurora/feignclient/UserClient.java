package store.aurora.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "UserClient", url = "${api.gateway.base-url}" + "/api/users")
public interface UserClient {

    @GetMapping("birth/coupon")
    List<String> getUserIdByMonth(@RequestParam int currentMonth);
}