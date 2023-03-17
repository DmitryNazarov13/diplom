func TestGetProjAPIUnauthorized(t *testing.T) {
	// create a new request with JSON body containing project ID
	requestBody := reqProj{Proj: 3}
	reqBodyBytes, err := json.Marshal(requestBody)
	if err != nil {
		t.Errorf("Failed to create request body: %v", err)
	}
	req, err := http.NewRequest("GET", "/projects", bytes.NewBuffer(reqBodyBytes))
	if err != nil {
		t.Errorf("Failed to create request: %v", err)
	}

	// create a new ProjsBase instance with mock database and memorydb
	pb := &ProjsBase{}

	// create a new response recorder
	rr := httptest.NewRecorder()

	// call GetProjAPI handler function
	http.HandlerFunc(pb.GetProjAPI).ServeHTTP(rr, req)
}

func TestTaskHandler(t *testing.T) {

	tb := &TasksBase{}

	// Test creating a new item
	t.Run("CreateNewItem", func(t *testing.T) {
		// Create a test http request with a JSON payload
		taskJSON := `{"Tag": "", "Category": "", "count": "date", "content": "createButton": "Создать"}`
		req, err := http.NewRequest(http.MethodPost, "http://127.0.0.1:8090/tasks/task/new", bytes.NewBufferString(taskJSON))
		if err != nil {
			t.Fatal(err)
		}

		rr := httptest.NewRecorder()

		// Call the task handler function
		handler := http.HandlerFunc(tb.TaskHandler)
		handler.ServeHTTP(rr, req)

		// Confirm the response status
		if st := rr.Result().StatusCode; st != http.StatusSeeOther {
			t.Errorf("handler returned wrong status code: got %v expected %v", st, http.StatusSeeOther)
		}
	})

	// Test creating a new category
	t.Run("UpdateTask", func(t *testing.T) {
		// Create a test http request with a JSON payload
		taskJSON := `{"tag": "", "category": "", "count": "date", "content": "updateButton": "Создать"}`
		req, err := http.NewRequest(http.MethodPost, "http://127.0.0.1:8090/tasks/task/10", bytes.NewBufferString(taskJSON))
		if err != nil {
			t.Fatal(err)
		}

		rr := httptest.NewRecorder()

		// Call the task handler function
		handler := http.HandlerFunc(tb.TaskHandler)
		handler.ServeHTTP(rr, req)

		// Confirm the response status
		if st := rr.Result().StatusCode; st != http.StatusSeeOther {
			t.Errorf("handler returned wrong status code: got %v expected %v", st, http.StatusSeeOther)
		}
	})

}
