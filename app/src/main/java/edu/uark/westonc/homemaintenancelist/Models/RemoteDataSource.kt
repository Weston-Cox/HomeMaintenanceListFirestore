package edu.uark.westonc.homemaintenancelist.Models

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import edu.uark.westonc.homemaintenancelist.Models.JSON.JSONFirebaseService
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource(context: Context) {
    var retrofit: Retrofit
    var remoteDataService: JSONFirebaseService

    init {
        retrofit = createRetrofitClient(context)
        remoteDataService = retrofit.create(JSONFirebaseService::class.java)
    }

    private fun createRetrofitClient(context: Context): Retrofit {
        val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(createClient(context))
            .build()
    }

    private fun createCache(context: Context): Cache {
        //Create 5 MB Cache
        return Cache(context.cacheDir, (5 * 1024 * 1024))
    }

    private fun hasNetwork(context: Context):Boolean{
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null;
    }


    private fun createClient(context: Context): OkHttpClient
    {

        val okHttpClient = OkHttpClient.Builder()
            .cache(createCache(context))
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (hasNetwork(context))
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                else
                    request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                    ).build()
                chain.proceed(request)
            }
            .build()
        return okHttpClient
    }

    suspend fun getTasksByUserId(userId:Int):List<Task>?{
        val response = remoteDataService.getTasksByUserId(userId)
        if(response.isSuccessful){
            val tasks = response.body()
            if(tasks != null) {
                return tasks
            }
        }
        return null
    }
    suspend fun getTasks():List<Task>?{
        val response = remoteDataService.getTasks()
        if(response.isSuccessful){
            val tasks = response.body()
            if(tasks != null) {
                return tasks
            }
        }
        return null
    }

    suspend fun insertTask(task: Task):Task?{
        val call = remoteDataService.insertTask(task)
        call.enqueue(object : Callback<Task> {
            override fun onFailure(call: Call<Task>, t: Throwable) {
                Log.d("RemoteDataSource", "Call Failed")
            }

            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                Log.d("RemoteDataSource", response.body()?.id.toString())
            }

        })
        return null
    }

    suspend fun deleteTask(id: Int):Task?{
        val call = remoteDataService.deleteTask(id)
        call.enqueue(object : Callback<Task> {
            override fun onFailure(call: Call<Task>, t: Throwable) {
                Log.d("RemoteDataSource", "Call Failed")
            }

            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                Log.d("RemoteDataSource", response.body()?.id.toString())
            }

        })
        return null
    }





}