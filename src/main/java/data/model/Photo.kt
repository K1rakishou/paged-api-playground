package data.model

import com.google.gson.annotations.SerializedName

data class Photo(
  @SerializedName("photo_id") val photoId: Long,
  @SerializedName("user_id") val userId: Long,
  @SerializedName("photo_name") val photoName: String
)