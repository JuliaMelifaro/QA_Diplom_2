package apitests;

import pojo.LoginData;
import pojo.OrderBody;
import pojo.UserData;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreatingOrderTest {
    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI="https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Creating order - full data")
    @Description("Creating order with ingredients authorized")
    public void CreateOrderRightDataTest(){
        String[] ingredientList = new String[]{"61c0c5a71d1f82001bdaaa6c","61c0c5a71d1f82001bdaaa79"};
        OrderBody testOrder = new OrderBody(ingredientList);
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        Response testResponse = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register");
        token = testResponse.body().as(LoginData.class).getAccessToken().substring(7);
        given().header("Content-Type","application/json").auth()
                .oauth2(token).body(testOrder).post("/api/orders").then().statusCode(200).and()
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Create order without authorization")
    @Description("Creating order with ingredients but not authorized")
    public void CreateOrderWithoutAuthorizationTest(){
        String[] ingredientList = new String[]{"61c0c5a71d1f82001bdaaa6c","61c0c5a71d1f82001bdaaa79"};
        OrderBody testOrder = new OrderBody(ingredientList);
        given().header("Content-Type","application/json").body(testOrder).post("/api/orders").then().statusCode(200).and()
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Create order without ingredients")
    @Description("Creating order without ingredients but authorized")
    public void CreateOrderWithoutIngredientsTest(){
        String[] ingredientList = new String[]{};
        OrderBody testOrder = new OrderBody(ingredientList);
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        Response testResponse = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register");
        token = testResponse.body().as(LoginData.class).getAccessToken().substring(7);
        given().header("Content-Type","application/json").auth()
                .oauth2(token).body(testOrder).post("/api/orders").then().statusCode(400).and()
                .assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order wrong ingredient hash")
    @Description("Create order with wrong ingredient hash")
    public void CreateOrderWrongHashTest(){
        String[] ingredientList = new String[]{"1111","2222"};
        OrderBody testOrder = new OrderBody(ingredientList);
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        Response testResponse = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register");
        token = testResponse.body().as(LoginData.class).getAccessToken().substring(7);
        given().header("Content-Type","application/json").auth()
                .oauth2(token).body(testOrder).post("/api/orders").then().statusCode(500);
    }

    @After
    public void deletingTestData(){
        if (token != null) given().header("Content-Type","application/json").auth()
                .oauth2(token).delete("/api/auth/user");
    }

}
