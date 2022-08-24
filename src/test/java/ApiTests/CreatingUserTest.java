package ApiTests;

import POJO.LoginData;
import POJO.UserData;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreatingUserTest {
    String token;

    @Before
    public void setUp() {
        RestAssured.baseURI="https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Creating new user - all data - successful")
    @Description("Creating user while entering email, password and name")
    public void CreatingUserFullDataTest(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        Response testResponse = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register");
        token = testResponse.body().as(LoginData.class).getAccessToken().substring(7);
        testResponse.then().statusCode(200).and()
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Creating new user - duplicate")
    @Description("Trying to create a duplicate of user")
    public void CreatingDuplicateUserTest(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        token = given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register").body().as(LoginData.class).getAccessToken().substring(7);
        given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register").then().statusCode(403).and()
                .assertThat().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Creating new user - without email")
    @Description("Trying to create user without entering email")
    public void CreatingUserWithoutEmailTest(){
        UserData testUser = new UserData("", "qwerty" + Math.random()*1000,
                "HungryUser" + Math.random()*1000);
        given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register").then().statusCode(403).and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Creating new user - without password")
    @Description("Trying to create user without entering password")
    public void CreatingUserWithoutPasswordTest(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "",
                "HungryUser" + Math.random()*1000);
        given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register").then().statusCode(403).and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Creating new user - without name")
    @Description("Trying to create user without entering name")
    public void CreatingUserWithoutNameTest(){
        UserData testUser = new UserData("Tapioka" + Math.random()*1000 + "@ya.ru", "qwerty" + Math.random()*1000,
                "");
        given().header("Content-Type","application/json").body(testUser)
                .post("/api/auth/register").then().statusCode(403).and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void deletingTestData(){
        if (token != null) given().header("Content-Type","application/json").auth()
                .oauth2(token).delete("/api/auth/user");
    }
}
