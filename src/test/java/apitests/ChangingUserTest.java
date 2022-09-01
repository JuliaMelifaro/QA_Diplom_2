package apitests;

import pojo.LoginData;
import pojo.UserData;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class ChangingUserTest {
    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI="https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Changing users data - authorized")
    @Description("Changing users data - authorized attempt")
    public void ChangingUserDataAuthorizedTest(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        Response testResponse = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register");
        token = testResponse.body().as(LoginData.class).getAccessToken().substring(7);
        UserData testUser2 = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        given().header("Content-Type","application/json").auth()
                .oauth2(token).body(testUser2).patch("/api/auth/user")
                .then().statusCode(200).and()
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Changing users data - not authorized")
    @Description("Changing users data - not authorized attempt")
    public void ChangingUserDataNotAuthorizedTest(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        Response testResponse = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register");
        token = testResponse.body().as(LoginData.class).getAccessToken().substring(7);
        UserData testUser2 = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        given().header("Content-Type","application/json").body(testUser2).patch("/api/auth/user")
                .then().statusCode(401).and()
                .assertThat().body("success", equalTo(false));
    }

    @After
    public void deletingTestData(){
        if (token != null) given().header("Content-Type","application/json").auth()
                .oauth2(token).delete("/api/auth/user");
    }
}
