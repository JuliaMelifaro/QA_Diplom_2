package apitests;

import pojo.LoginData;
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

public class LoginUserTest {

    String token;

    @Before
    public void setUp() {
        RestAssured.baseURI="https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Login under existing user")
    @Description("Creating user, then login under it")
    public void LoginExistingUserTest(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        Response testResponse = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register");
        token = testResponse.body().as(LoginData.class).getAccessToken().substring(7);
        given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/login").then().statusCode(200).and()
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Login under not existing user")
    @Description("Login without creating user")
    public void LoginNotExistingUserTest(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/login").then().statusCode(401).and()
                .assertThat().body("success", equalTo(false));
    }

    @After
    public void deletingTestData(){
        if (token != null) given().header("Content-Type","application/json").auth()
                .oauth2(token).delete("/api/auth/user");
    }

}
