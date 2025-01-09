package store.aurora.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "UserClient", url = "${api.gateway.base-url}" + "/api/users")
public interface UserClient {

    @GetMapping
    List<String> getUserIdByMonth(int currentMonth);
}