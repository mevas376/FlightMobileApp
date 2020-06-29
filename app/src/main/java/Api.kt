import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {
    @POST("/api/command")
    fun postControl(@Body requestBody: RequestBody): Call<ResponseBody>

    @GET("/screenshot")
    fun getImg(): Call <ResponseBody>
}