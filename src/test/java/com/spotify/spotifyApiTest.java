package com.spotify;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import com.google.common.io.Resources;
import com.spotify.constants.Constants;
import io.restassured.RestAssured;
import io.restassured.internal.http.HttpResponseException;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class spotifyApiTest {
    String userId = "";
    String playlistId="";
    String trackUri="";
    public void getRegisteredUserID() throws IOException {
        RestAssured.baseURI = "https://api.spotify.com/v1";

        Response responseBody =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + Constants.AUTH_TOKEN)
                        .when()
                        .get("/me")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();


        userId = responseBody.getBody().jsonPath().getString("id");
        System.out.println(userId);

}

    public void createNewPlaylist() throws IOException {
        URL file = Resources.getResource("newPlayList.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject(myJson);
        Response playlistResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " +  Constants.AUTH_TOKEN)
                        .body(json.toString())
                        .when()
                        .post("users/{userId}/playlists",userId)
                        .then()
                        .statusCode(201)
                        .extract()
                        .response();
//            playlistResponse.getBody().prettyPeek();
        playlistId = playlistResponse.getBody().jsonPath().getString("id");
        System.out.println("PlaylistID: "+  playlistId);
    }
    public int getAPlaylist() throws IOException {
        URL file = Resources.getResource("newPlayList.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject(myJson);
        Response myPlaylistResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + Constants.AUTH_TOKEN)
                        .when()
                        .get("playlists/{playlistId}", playlistId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        ArrayList arrayList = myPlaylistResponse.path("tracks.items");
       return arrayList.size();
    }

    public ArrayList searchSong(String artistName) {
        Response searchSongResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + Constants.AUTH_TOKEN)
                        .queryParam("q", artistName)
                        .queryParam("type", "track")
                        .queryParam("limit", "3")
                        .when()
                        .get("search")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        //ArrayList arrayList = searchSongResponse.path("tracks.items.album.id");
        ArrayList arrayList = searchSongResponse.path("tracks.items.album.artists.uri");
        System.out.println(arrayList);
        return arrayList;
    }
    public void addItemsToPlaylist(String trackUri){
        given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + Constants.AUTH_TOKEN)
                .queryParam("playlist_id",playlistId)
                .queryParam("uris",trackUri)
                .when()
                .post("playlists/{playlist_id}/tracks",playlistId)
                .then()
                .statusCode(201);
    }

@Test
public void spotifyTest() throws IOException {
    getRegisteredUserID();
    createNewPlaylist();
    getAPlaylist();
    assertEquals(getAPlaylist(),0);
    searchSong("inna");



}
}
