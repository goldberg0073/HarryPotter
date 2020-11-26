package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HarryPotterTests {

    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI="http://www.potterapi.com/v1/";
    }

    @Test
    public void verifySortinHat(){
        Response response = when().get("SortingHat");
        assertThat(response.statusCode(),is(200));
        assertThat(response.contentType(),equalTo("application/json; charset=utf-8"));
        List<String>list=new ArrayList<>(Arrays.asList("\"Gryffindor\"","\"Ravenclaw\"", "\"Slytherin\"", "\"Hufflepuff\""));
        assertTrue(list.contains(response.body().asString()));

    }
    @Test
    public void VerifyNoKey(){
        Response response = when().get("Characters").prettyPeek();
        assertThat(response.statusCode(),is(409));
        assertThat(response.contentType(),equalTo("application/json; charset=utf-8"));
        assertTrue(response.statusLine().contains("Conflict"));
        assertThat(response.jsonPath().getString("error"),equalTo("Must pass API key for request"));

    }

    @Test
    public void VerifyBadKey(){
        /**
         * $2a$10$N831Hc0zoRVu12HN8bEzz.8Jlqu2moPpUDJTpg6rcodPjwdx/bj66
         *
         */

        Response response = given().queryParam("key", "6a4dca5d").
                get("Characters").prettyPeek();
        assertThat(response.statusCode(),is(401));
        assertThat(response.contentType(),is("application/json; charset=utf-8"));
        assertTrue(response.statusLine().contains("Unauthorized"));
        assertThat(response.jsonPath().getString("error"),equalTo("API Key Not Found"));

    }

    @Test
    public void NumberOfCharacters(){
        Response response = given().
        queryParam("key", "$2a$10$N831Hc0zoRVu12HN8bEzz.8Jlqu2moPpUDJTpg6rcodPjwdx/bj66").
                get("Characters").prettyPeek();
        assertThat(response.statusCode(),is(200));
        assertThat(response.contentType(),equalTo("application/json; charset=utf-8"));
        List<Object> list = response.jsonPath().getList("");
        System.out.println("list.size() = " + list.size());
        assertThat(list.size(),equalTo(195));

    }

    @Test
    public void VerifyNumberOfCharacterIdAndHouse(){
        Response response = given().accept(ContentType.JSON).
                queryParam("key", "$2a$10$N831Hc0zoRVu12HN8bEzz.8Jlqu2moPpUDJTpg6rcodPjwdx/bj66").
                get("Characters");
        assertThat(response.statusCode(),is(200));
        assertThat(response.contentType(),equalTo("application/json; charset=utf-8"));
        assertThat(response.jsonPath().getString("_id"),is(notNullValue()));
        String string = response.jsonPath().getString("dumbledoresArmy[1]");



    }

}
