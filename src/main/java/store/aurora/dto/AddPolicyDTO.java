package store.aurora.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddPolicyDTO {
    private List<Long> categoryId;
    private List<Long> bookId;
}
