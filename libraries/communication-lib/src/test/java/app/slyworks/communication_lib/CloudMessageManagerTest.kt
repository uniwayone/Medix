package app.slyworks.communication_lib

import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Rule
import retrofit2.Retrofit

/**
 * Created by Joshua Sylvanus, 10:28 PM, 03/11/2022.
 */
class CloudMessageManagerTest{
    /*private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }*/
    @get:Rule
    val mockWebServer = MockWebServer()

    /*
    * fun getRandomJokeGetsRandomJokeJson() {
// 1
mockWebServer.enqueue(
MockResponse()
.setBody(testJson)
.setResponseCode(200))
// 2
val testObserver = jokeService.getRandomJoke().test()
// 3
testObserver.assertNoErrors()
// 4
assertEquals("/random_joke.json",
mockWebServer.takeRequest().path)
    * }
    * */
}