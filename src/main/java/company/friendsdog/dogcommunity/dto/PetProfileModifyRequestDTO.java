package company.friendsdog.dogcommunity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
// 필요한 값 dto로만들고
// 밑에 설정 네개 필수

@Setter @Getter
@ToString
@NoArgsConstructor

//받는 DTO
public class PetProfileModifyRequestDTO {

    private String petPhoto;
    private Long petNo;
}
