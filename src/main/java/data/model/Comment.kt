package data.model

import com.google.gson.annotations.SerializedName

data class Comment(
  @SerializedName("comment_id") val commentId: Long,
  @SerializedName("user_id") val userId: Long,
  @SerializedName("photo_id") val photoId: Long,
  @SerializedName("message") val message: String
)