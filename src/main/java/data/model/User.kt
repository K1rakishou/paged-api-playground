package data.model

import com.google.gson.annotations.SerializedName

data class User(
  @SerializedName("user_id") val userId: Long
)