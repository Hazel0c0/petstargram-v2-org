package company.friendsdog.dogcommunity.repository;

import company.friendsdog.dogcommunity.entity.Chat;
import company.friendsdog.dogcommunity.entity.Reply;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatMapperTest {

    @Autowired
    ChatMapper chatMapper;

    @Test
    @DisplayName("save 테스트")
    void save() {
        String message = "테스트";
        Chat chat = Chat.builder()
                .message(message)
                .petNo(13L)
                .petName("리트")
                .petPhoto("https://dalkompet.com/wp-content/uploads/2020/01/%EC%8A%88%EB%82%98%EC%9A%B0%EC%A0%80-2.jpg")
                .build();
        System.out.println("chat = " + chat);

        chatMapper.save(chat);
    }
}