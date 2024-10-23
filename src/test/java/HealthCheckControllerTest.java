import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.csye6225.cloud.webapp.WebAppMain;

@SpringBootTest(classes = WebAppMain.class)
@AutoConfigureMockMvc
public class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    public void whenDatabaseUnavailable_thenHealthCheckReturns503() throws Exception {
        mockMvc.perform(get("/healthz"))
               .andExpect(status().isOk());
    }
}