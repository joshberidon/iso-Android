package iso.io.iso.net;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by tbrown on 4/16/16.
 */
public interface WebAPI {

  @POST("/api/v1/upload")
  void sendFile(@Query("fileName") String name, @Query("data") String data, Callback<Response> responseCallback);

}
