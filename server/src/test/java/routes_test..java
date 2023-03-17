
		}
package routes_test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FinanceRoutesTest {

scss
Copy code
private MockMvc mockMvc;

@Test
public void testCreateFinance() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(new FinanceController()).build();

    // Тестируем успешный запрос
    Finance finance = new Finance(100, 200, 50, 30, 20);
    ObjectMapper objectMapper = new ObjectMapper();
    String financeJSON = objectMapper.writeValueAsString(finance);
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/finances")
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(financeJSON)).andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(200, response.getStatus());

    // Проверяем, что ответ содержит все поля модели Finance
    String responseContent = response.getContentAsString(StandardCharsets.UTF_8);
    Finance responseFinance = objectMapper.readValue(responseContent, Finance.class);
    assertNotNull(responseFinance.getId());
    assertEquals(finance.getProfit(), responseFinance.getProfit());
    assertEquals(finance.getRevenue(), responseFinance.getRevenue());
    assertEquals(finance.getCost(), responseFinance.getCost());
    assertEquals(finance.getTaxes(), responseFinance.getTaxes());
    assertEquals(finance.getExpenses(), responseFinance.getExpenses());

    // Тестируем ошибку при неправильных входных данных
    Finance invalidFinance = new Finance();
    String invalidFinanceJSON = objectMapper.writeValueAsString(invalidFinance);
    result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/finances")
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(invalidFinanceJSON)).andReturn();
    response = result.getResponse();
    assertEquals(400, response.getStatus());
}
@Test
public void testCreateFinance() throws JsonProcessingException {
    // Тестируем успешный запрос
    Finance finance = new Finance(100, 200, 50, 30, 20);
    String financeJSON = mapper.writeValueAsString(finance);
    RestAssured.given()
            .contentType(ContentType.JSON)
            .body(financeJSON)
            .post("http://localhost:7000/api/v1/finances")
            .then()
            .statusCode(200);
	@Test
public void testGetFinances() {
    App app = setupTestApp();

    // Создаем несколько объектов Finance в базе данных для тестирования
    Finance finance1 = new Finance(100, 200, 50, 30, 20);
    Database.get().createFinance(finance1);

    Finance finance2 = new Finance(200, 400, 100, 60, 40);
    Database.get().createFinance(finance2);

    // Тестируем успешный запрос
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("/api/v1/finances"))
            .GET()
            .build();
    HttpResponse<String> response = app.handle(request);

    assertEquals(200, response.statusCode());

    // Проверяем, что ответ содержит все объекты Finance
    List<Finance> responseFinances = new ArrayList<>();
    Gson gson = new Gson();
    responseFinances = gson.fromJson(response.body(), responseFinances.getClass());
    assertEquals(2, responseFinances.size());
    assertEquals(finance1.getProfit(), responseFinances.get(0).getProfit());

}

@Test
public void testCreateFinance() {
    // Создаем новый экземпляр приложения
    App app = new App();

    // Создаем тестовый запрос
    String requestBody = "{\"profit\": 100, \"revenue\": 200, \"cost\": 50, \"taxes\": 20, \"expenses\": 30}";
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("/finance"))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .header("Content-Type", "application/json")
            .build();

    // Запускаем функцию CreateFinance
    HttpResponse<String> response = app.handle(request);

    // Проверяем, что код состояния ответа равен 200
    assertEquals(200, response.statusCode());

    // Проверяем, что тело ответа содержит правильные данные
    String expected = "{\"id\":1,\"profit\":100,\"revenue\":200,\"cost\":50,\"taxes\":20,\"expenses\":30}";
    assertEquals(expected, response.body());
}

@Test
public void testGetFinances() {
    // Создаем новый экземпляр приложения
    App app = new App();

    // Добавляем несколько записей в базу данных
    Finance finance1 = new Finance(100, 200, 50, 20, 30);
    Database.get().createFinance(finance1);

    Finance finance2 = new Finance(200, 300, 100, 50, 50);
    Database.get().createFinance(finance2);

    // Создаем тестовый запрос
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("/finances"))
            .GET()
            .build();

    // Запускаем функцию GetFinances
    HttpResponse<String> response = app.handle(request);

    // Проверяем, что код состояния ответа равен 200
    assertEquals(200, response.statusCode());

    // Проверяем, что тело ответа содержит правильные данные
String expected = "[{\"id\":1,\"profit\":100,\"revenue\":200,\"cost\":50,\"taxes\":20,\"expenses\":30}]";
assertEquals(expected, response.getBody());

