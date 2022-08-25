package ApiTests;

import POJO.LoginData;
import POJO.UserData;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrderTest {

    String token;

    @Before
    public void setUp() {
        RestAssured.baseURI="https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Get order authorized")
    @Description("Getting order while being authorized")
    public void GetOrdersAuthorized(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        Response testResponse = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register");
        token = testResponse.body().as(LoginData.class).getAccessToken().substring(7);
        given().header("Content-Type","application/json").auth()
                .oauth2(token).get("/api/orders").then().statusCode(200).and()
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Get order without authorization")
    @Description("Trying to get order without authorization")
    public void GetOrdersNotAuthorized(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        Response testResponse = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register");
        token = testResponse.body().as(LoginData.class).getAccessToken().substring(7);
        given().header("Content-Type","application/json").get("/api/orders").then().statusCode(401)
            .and().assertThat().body("message", equalTo("You should be authorised"));
    }

    @After
    public void deletingTestData(){
        if (token != null) given().header("Content-Type","application/json").auth()
                .oauth2(token).delete("/api/auth/user");
    }
}
