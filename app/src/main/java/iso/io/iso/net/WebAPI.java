package iso.io.iso.net;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by tbrown on 4/16/16.
 */
public interface WebAPI {

  @Multipart
  @POST("api/v1/upload")
  void sendFile(@Part("filename") TypedFile file, Callback<Response> responseCallback);

}
