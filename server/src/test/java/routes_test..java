package routes_test

import (
	"bytes"
	"encoding/json"
	"errors"
	"net/http"
	"net/http/httptest"
	"testing"


func TestCreateFinance(t *testing.T) {
	app := setupTestApp()

	// Тестируем успешный запрос
	finance := models.Finance{
		Profit:   100,
		Revenue:  200,
		Cost:     50,
		Taxes:    30,
		Expenses: 20,
	}
	financeJSON, _ := json.Marshal(finance)
	req, err := http.NewRequest("POST", "/api/v1/finances", bytes.NewBuffer(financeJSON))
	assert.NoError(t, err)
	req.Header.Set("Content-Type", "application/json")
	resp, err := app.Test(req)
	assert.NoError(t, err)
	assert.Equal(t, 200, resp.StatusCode)

	// Проверяем, что ответ содержит все поля модели Finance
	responseFinance := routes.Finance{}
	err = json.NewDecoder(resp.Body).Decode(&responseFinance)
	assert.NoError(t, err)
	assert.NotZero(t, responseFinance.ID)
	assert.Equal(t, finance.Profit, responseFinance.Profit)
	assert.Equal(t, finance.Revenue, responseFinance.Revenue)
	assert.Equal(t, finance.Cost, responseFinance.Cost)
	assert.Equal(t, finance.Taxes, responseFinance.Taxes)
	assert.Equal(t, finance.Expenses, responseFinance.Expenses)

	// Тестируем ошибку при неправильных входных данных
	invalidFinance := models.Finance{}
	invalidFinanceJSON, _ := json.Marshal(invalidFinance)
	req, err = http.NewRequest("POST", "/api/v1/finances", bytes.NewBuffer(invalidFinanceJSON))
	assert.NoError(t, err)
	req.Header.Set("Content-Type", "application/json")
	resp, err = app.Test(req)
	assert.NoError(t, err)
	assert.Equal(t, 400, resp.StatusCode)
}

func TestGetFinances(t *testing.T) {
	app := setupTestApp()

	// Создаем несколько объектов Finance в базе данных для тестирования
	finance1 := models.Finance{
		Profit:   100,
		Revenue:  200,
		Cost:     50,
		Taxes:    30,
		Expenses: 20,
	}
	database.Database.Db.Create(&finance1)

	finance2 := models.Finance{
		Profit:   200,
		Revenue:  400,
		Cost:     100,
		Taxes:    60,
		Expenses: 40,
	}
	database.Database.Db.Create(&finance2)

	// Тестируем успешный запрос
	req, err := http.NewRequest("GET", "/api/v1/finances", nil)
	assert.NoError(t, err)
	resp, err := app.Test(req)
	assert.NoError(t, err)
	assert.Equal(t, 200, resp.StatusCode)

	// Проверяем, что ответ содержит все объекты Finance
	responseFinances := []routes.Finance{}
	err = json.NewDecoder(resp.Body).Decode(&responseFinances)
	assert.NoError(t, err)
	assert.Len(t, responseFinances, 2)
	assert.Equal(t, finance1.Profit, responseFinances[0].Profit)
	func TestCreateFinance(t *testing.T) {
	// Создаем новый экземпляр приложения Fiber
	app := fiber.New()

	// Создаем тестовый запрос
	req := httptest.NewRequest(http.MethodPost, "/finance", strings.NewReader(`{"profit": 100, "revenue": 200, "cost": 50, "taxes": 20, "expenses": 30}`))

	// Создаем контекст запроса
	ctx := app.AcquireCtx(req, nil)

	// Запускаем функцию CreateFinance
	err := CreateFinance(ctx)

	// Убеждаемся, что ошибки нет
	assert.Nil(t, err)

	// Проверяем, что код состояния ответа равен 200
	assert.Equal(t, http.StatusOK, ctx.Response().StatusCode())

	// Проверяем, что тело ответа содержит правильные данные
	expected := `{"id":1,"profit":100,"revenue":200,"cost":50,"taxes":20,"expenses":30}`
	assert.JSONEq(t, expected, ctx.Response().Body())
}

func TestGetFinances(t *testing.T) {
	// Создаем новый экземпляр приложения Fiber
	app := fiber.New()

	// Добавляем несколько записей в базу данных
	db := database.Database.Db
	db.Create(&models.Finance{Profit: 100, Revenue: 200, Cost: 50, Taxes: 20, Expenses: 30})
	db.Create(&models.Finance{Profit: 200, Revenue: 300, Cost: 100, Taxes: 50, Expenses: 50})

	// Создаем тестовый запрос
	req := httptest.NewRequest(http.MethodGet, "/finances", nil)

	// Создаем контекст запроса
	ctx := app.AcquireCtx(req, nil)

	// Запускаем функцию GetFinances
	err := GetFinances(ctx)

	// Убеждаемся, что ошибки нет
	assert.Nil(t, err)

	// Проверяем, что код состояния ответа равен 200
	assert.Equal(t, http.StatusOK, ctx.Response().StatusCode())

	// Проверяем, что тело ответа содержит правильные данные
	expected := `[{"id":1,"profit":100,"revenue":200,"cost":50,"taxes":20,"expenses":30},{"id":2,"profit":200,"revenue":300,"cost":100,"taxes":50,"expenses":50}]`
	assert.JSONEq(t, expected, ctx.Response().Body())
}

func TestGetFinance(t *testing.T) {
	// Создаем новый экземпляр приложения Fiber
	app := fiber.New()

	// Добавляем запись в базу данных
	db := database.Database.Db
	finance := models.Finance{Profit: 100, Revenue: 200, Cost: 50, Taxes: 20, Expenses: 30}
	db.Create(&finance)

	// Создаем тестовый запрос
	req := httptest.NewRequest(http.MethodGet, "/finance/"+strconv.Itoa(int(finance.ID)), nil)

	// Создаем контекст запроса
	ctx := app.AcquireCtx(req, nil)

	// Запускаем функцию GetFinance
	err := GetFinance(ctx)

	// Убеждаемся, что ошибки нет
	assert.Nil(t, err)

func TestCreateProduct(t *testing.T) {
	// Initialize database
	database.ConnectToTestDB()
	database.Database.Db.AutoMigrate(&models.Product{})
	defer database.Database.Db.Migrator().DropTable(&models.Product{})

	// Create test product
	product := models.Product{Name: "Test Product", Price: "10.99"}

	// Convert product to JSON
	jsonProduct, err := json.Marshal(product)
	assert.NoError(t, err)

	// Send POST request to /api/v1/products
	req := httptest.NewRequest(http.MethodPost, "/api/v1/products", bytes.NewBuffer(jsonProduct))
	req.Header.Set("Content-Type", "application/json")
	resp, err := app.Test(req)
	assert.NoError(t, err)

	// Assert response status code and JSON response body
	assert.Equal(t, http.StatusOK, resp.StatusCode)
	expected := Product{ID: 1, Name: product.Name, Price: product.Price}
	var actual Product
	err = json.NewDecoder(resp.Body).Decode(&actual)
	assert.NoError(t, err)
	assert.Equal(t, expected, actual)
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
