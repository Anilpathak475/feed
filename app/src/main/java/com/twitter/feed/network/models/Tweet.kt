package com.twittwe.feedmanager.network.models


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("friends_count")
    val friendsCount: Int = 0,
    @SerializedName("profile_image_url_https")
    val profileImageUrlHttps: String = "",
    @SerializedName("listed_count")
    val listedCount: Int = 0,
    @SerializedName("profile_background_image_url")
    val profileBackgroundImageUrl: String = "",
    @SerializedName("default_profile_image")
    val defaultProfileImage: Boolean = false,
    @SerializedName("favourites_count")
    val favouritesCount: Int = 0,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("is_translator")
    val isTranslator: Boolean = false,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("profile_background_image_url_https")
    val profileBackgroundImageUrlHttps: String = "",
    @SerializedName("protected")
    val protected: Boolean = false,
    @SerializedName("screen_name")
    val screenName: String = "",
    @SerializedName("id_str")
    val idStr: String = "",
    @SerializedName("profile_link_color")
    val profileLinkColor: String = "",
    @SerializedName("show_all_inline_media")
    val showAllInlineMedia: Boolean = false,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("geo_enabled")
    val geoEnabled: Boolean = false,
    @SerializedName("profile_background_color")
    val profileBackgroundColor: String = "",
    @SerializedName("lang")
    val lang: String = "",
    @SerializedName("profile_sidebar_border_color")
    val profileSidebarBorderColor: String = "",
    @SerializedName("profile_text_color")
    val profileTextColor: String = "",
    @SerializedName("verified")
    val verified: Boolean = false,
    @SerializedName("profile_image_url")
    val profileImageUrl: String = "",
    @SerializedName("time_zone")
    val timeZone: String,
    @SerializedName("url")
    val url: String = "",
    @SerializedName("contributors_enabled")
    val contributorsEnabled: Boolean = false,
    @SerializedName("profile_background_tile")
    val profileBackgroundTile: Boolean = false,
    @SerializedName("entities")
    val entities: Entities,
    @SerializedName("follow_request_sent")
    val followRequestSent: Boolean = false,
    @SerializedName("statuses_count")
    val statusesCount: Int = 0,
    @SerializedName("default_profile")
    val defaultProfile: Boolean = false,
    @SerializedName("profile_use_background_image")
    val profileUseBackgroundImage: Boolean = false,
    @SerializedName("followers_count")
    val followersCount: Int = 0,
    @SerializedName("following")
    val following: Boolean = false,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("profile_sidebar_fill_color")
    val profileSidebarFillColor: String = "",
    @SerializedName("location")
    val location: String = "",
    @SerializedName("notifications")
    val notifications: Boolean = false
)


data class Tweet(
    @SerializedName("truncated")
    val truncated: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("source")
    val source: String = "",
    @SerializedName("retweet_count")
    val retweetCount: Int = 0,
    @SerializedName("retweeted")
    val retweeted: Boolean = false,
    @SerializedName("entities")
    val entities: Entities,
    @SerializedName("id_str")
    val idStr: String = "",
    @SerializedName("text")
    val text: String = "",
    @SerializedName("contributors")
    val contributors: String,
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("place")
    val place: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("favorited")
    val favorited: Boolean = false
)


data class UrlsItem(
    @SerializedName("display_url")
    val displayUrl: String,
    @SerializedName("indices")
    val indices: List<Number>?,
    @SerializedName("expanded_url")
    val expandedUrl: String,
    @SerializedName("url")
    val url: String = ""
)


data class Url(
    @SerializedName("urls")
    val urls: List<UrlsItem>?
)


data class Entities(
    @SerializedName("description")
    val description: String,
    @SerializedName("url")
    val url: Url
)


