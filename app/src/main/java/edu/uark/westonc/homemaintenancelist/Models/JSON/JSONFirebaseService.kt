package edu.uark.westonc.homemaintenancelist.Models.JSON

import edu.uark.westonc.homemaintenancelist.Models.Task
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JSONFirebaseService {

    @GET("tasks")
    suspend fun getTasks(): Response<List<Task>>

    @GET("tasks/{localID}")
    suspend fun getTask(@Path("id")id:Int): Response<Task>

    @GET("tasks/")
    suspend fun getTasksByUserId(@Path("userId")userId:Int): Response<List<Task>>

    @POST("tasks")
    fun insertTask(@Body task: Task): Call<Task>

    @DELETE("tasks/{localID}")
    fun deleteTask(@Path("id")id:Int): Call<Task>

}