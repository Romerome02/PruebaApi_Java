import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class ApiTests {

    private int userId; // Variable de instancia para almacenar el ID de usuario generado


    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void testGetUserEmail() {

        // Generamos un ID de usuario aleatorio
        userId = getRandomUserId();

        // Realizamos la solicitud para obtener el correo electrónico del usuario
        Response response = given()
                .when()
                .get("/users/" + userId)
                .then()
                .extract()
                .response();

        // Verificamos el código de estado
        response.then().statusCode(200).log().status();

        // Obtenemos y mostramos el correo electrónico del usuario
        String email = response.jsonPath().getString("email");
        System.out.println("ID del usuario: " + userId);
        System.out.println("Email del usuario: " + email);

        // Verificamos que el correo electrónico no sea nulo
        Assert.assertNotNull(email);
    }

    @Test(priority = 2, dependsOnMethods = "testGetUserEmail") // Dependencia para asegurar que este test se ejecute después de testGetUserEmail()
    public void testGetUserPosts() {
        // Realizamos la solicitud para obtener las publicaciones del usuario
        Response postsResponse = given()
                .pathParam("userId", userId)
                .when()
                .get("/posts?userId={userId}")
                .then()
                .extract()
                .response();

        // Obtenemos la lista de IDs de las publicaciones
        List<Integer> postIds = postsResponse.jsonPath().getList("id");

        // Mostramos el número de publicaciones del usuario
        System.out.println("La cantidad total de  publicaciones del usuario " + userId +" ** "+ "Son: " + postIds.size());
    }

    @Test(priority = 3, dependsOnMethods = "testGetUserEmail")
    public void testCreatePost() {
        String title = "Post title";
        String body = "Post body";

        // Generaramos un ID único para el post
        String postId = UUID.randomUUID().toString();

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"userId\": " + userId + ", \"title\": \"" + title + "\", \"body\": \"" + body + "\" }")
                .when()
                .post("/posts")
                .then()
                .extract()
                .response();

        // Imprimimos la respuesta de la API
        System.out.println("Creación de la publicación:");
        System.out.println(response.asString());

        // Verificamos que se haya creado correctamente
        response.then().statusCode(201).log().status();

        // Imprimir el ID del post generado localmente
        //System.out.println("ID del post creado: " + postId);
    }


    private int getRandomUserId() {
        // Esto devuelve un ID de usuario aleatorio entre 1 y 10 (para este ejemplo)
        return (int) (Math.random() * 10) + 1;
    }
}
