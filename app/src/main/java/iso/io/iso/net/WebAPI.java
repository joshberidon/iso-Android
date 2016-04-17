package iso.io.iso.net;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by tbrown on 4/16/16.
 */
public interface WebAPI {

  @POST("/api/v1/upload")
  void sendFile(@Body WebData body, Callback<Response> responseCallback);
}