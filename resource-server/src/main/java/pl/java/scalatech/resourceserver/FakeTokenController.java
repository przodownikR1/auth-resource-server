package pl.java.scalatech.resourceserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/fake")
class FakeTokenController {

    public static final String TOKEN_FILE_NAME = "token_simple.json";
    final String content;
    ObjectMapper objectMapper = new ObjectMapper();

    FakeTokenController() throws IOException {
        File token = new ClassPathResource(TOKEN_FILE_NAME).getFile();
        content = new String(Files.readAllBytes(token.toPath()));
    }

    @PostMapping("/check_token")
    Map<String, ?> token(@RequestParam("token") String value) throws JsonProcessingException {
        log.debug("incoming raw token :{}", value);
        Map<String, ?> map = objectMapper.readValue(content, Map.class);
        log.info("token as json: {}", map);
        return map;
    }
}
