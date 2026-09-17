package com.madebyraymond.tvRecommendedContent

import android.content.ContentUris
import android.database.Cursor
import android.media.tv.TvContentRating
import android.net.Uri
import androidx.tvprovider.media.tv.BasePreviewProgram
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import androidx.core.net.toUri

class TvRecommendedContentModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName() = "TvRecommendedContentModule"

    private fun findUriByContentId(contentId: String): Uri? {
        val resolver = reactApplicationContext.contentResolver
        val projection = arrayOf(TvContractCompat.WatchNextPrograms._ID, TvContractCompat.WatchNextPrograms.COLUMN_INTERNAL_PROVIDER_ID)
        resolver.query(
            TvContractCompat.WatchNextPrograms.CONTENT_URI, projection, null, null, null
        )?.use { cursor: Cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(TvContractCompat.WatchNextPrograms._ID)
            val providerIdIndex = cursor.getColumnIndexOrThrow(TvContractCompat.WatchNextPrograms.COLUMN_INTERNAL_PROVIDER_ID)
            while (cursor.moveToNext()) {
                if (cursor.getString(providerIdIndex) == contentId) {
                    return TvContractCompat.buildWatchNextProgramUri(cursor.getLong(idIndex))
                }
            }
        }
        return null
    }

    private fun findChannelContentUri(channelId: Long, contentId: String): Uri? {
        reactApplicationContext.contentResolver.query(
            TvContractCompat.PreviewPrograms.CONTENT_URI,
            arrayOf(TvContractCompat.PreviewPrograms._ID, TvContractCompat.PreviewPrograms.COLUMN_CHANNEL_ID, TvContractCompat.PreviewPrograms.COLUMN_INTERNAL_PROVIDER_ID),
            null, null, null
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(TvContractCompat.PreviewPrograms._ID)
            val chIdx = cursor.getColumnIndexOrThrow(TvContractCompat.PreviewPrograms.COLUMN_CHANNEL_ID)
            val pidIdx = cursor.getColumnIndexOrThrow(TvContractCompat.PreviewPrograms.COLUMN_INTERNAL_PROVIDER_ID)
            while (cursor.moveToNext()) {
                if (cursor.getLong(chIdx) == channelId && cursor.getString(pidIdx) == contentId) {
                    return TvContractCompat.buildPreviewProgramUri(cursor.getLong(idIdx))
                }
            }
        }
        return null
    }

    private fun <T : BasePreviewProgram.Builder<T>> applyCommonFields(builder: T, programData: ReadableMap): T {
        val contentId = programData.getString("contentId") ?: throw IllegalArgumentException("Missing contentId")
        val posterUrl = programData.getString("posterUrl") ?: throw IllegalArgumentException("Missing posterUrl")
        val deepLinkUri = programData.getString("deepLinkUri") ?: throw IllegalArgumentException("Missing deepLinkUri")
        val title = programData.getString("title") ?: throw IllegalArgumentException("Missing title")

        val description = if (programData.hasKey("description")) programData.getString("description") else null
        val price = if (programData.hasKey("price")) programData.getString("price") else null
        val startingPrice = if (programData.hasKey("startingPrice")) programData.getString("startingPrice") else null
        val playbackPosition = if (programData.hasKey("playbackPosition")) programData.getInt("playbackPosition") else 1
        val startTime = if (programData.hasKey("startTime")) programData.getLong("startTime") else null
        val endTime = if (programData.hasKey("endTime")) programData.getLong("endTime") else null
        val duration = if (programData.hasKey("duration")) programData.getInt("duration") else 10000
        val previewVideoUrl = if (programData.hasKey("previewVideoUrl")) Uri.parse(programData.getString("previewVideoUrl")) else null
        val thumbnailUrl = if (programData.hasKey("thumbnailUrl")) Uri.parse(programData.getString("thumbnailUrl")) else null
        val logoUrl = if (programData.hasKey("logoUrl")) Uri.parse(programData.getString("logoUrl")) else null
        val releaseDate = if (programData.hasKey("releaseDate")) programData.getString("releaseDate") else null
        val logoContentDescription = if (programData.hasKey("logoContentDescription")) programData.getString("logoContentDescription") else null
        val isLive = if (programData.hasKey("isLive")) programData.getBoolean("isLive") else false
        val videoHeight = if (programData.hasKey("videoHeight")) programData.getInt("videoHeight") else null
        val videoWidth = if (programData.hasKey("videoWidth")) programData.getInt("videoWidth") else null

        // Get the type string from JS side (optional)
        val typeStr = if (programData.hasKey("type")) programData.getString("type") else null
        val programType = when (typeStr?.uppercase()) {
            "MOVIE" -> TvContractCompat.PreviewPrograms.TYPE_MOVIE
            "TV_EPISODE" -> TvContractCompat.PreviewPrograms.TYPE_TV_EPISODE
            "TV_SERIES" -> TvContractCompat.PreviewPrograms.TYPE_TV_SERIES
            "CLIP" -> TvContractCompat.PreviewPrograms.TYPE_CLIP
            else -> TvContractCompat.PreviewPrograms.TYPE_MOVIE // Default fallback
        } // TYPE_MOVIE, TYPE_TV_SERIES, TYPE_TV_EPISODE or TYPE_CLIP


        // Get the type string from JS side (optional)
        val genreStr = if (programData.hasKey("genre")) programData.getString("genre") else null
        val genre = when (genreStr?.uppercase()) {
            "ANIMAL_WILDLIFE" -> TvContractCompat.Programs.Genres.ANIMAL_WILDLIFE
            "ARTS" -> TvContractCompat.Programs.Genres.ARTS
            "COMEDY" -> TvContractCompat.Programs.Genres.COMEDY
            "DRAMA" -> TvContractCompat.Programs.Genres.DRAMA
            "EDUCATION" -> TvContractCompat.Programs.Genres.EDUCATION
            "ENTERTAINMENT" -> TvContractCompat.Programs.Genres.ENTERTAINMENT
            "FAMILY_KIDS" -> TvContractCompat.Programs.Genres.FAMILY_KIDS
            "GAMING" -> TvContractCompat.Programs.Genres.GAMING
            "LIFE_STYLE" -> TvContractCompat.Programs.Genres.LIFE_STYLE
            "MOVIES" -> TvContractCompat.Programs.Genres.MOVIES
            "MUSIC" -> TvContractCompat.Programs.Genres.MUSIC
            "NEWS" -> TvContractCompat.Programs.Genres.NEWS
            "PREMIER" -> TvContractCompat.Programs.Genres.PREMIER
            "SHOPPING" -> TvContractCompat.Programs.Genres.SHOPPING
            "SPORTS" -> TvContractCompat.Programs.Genres.SPORTS
            "TECH_SCIENCE" -> TvContractCompat.Programs.Genres.TECH_SCIENCE
            "TRAVEL" -> TvContractCompat.Programs.Genres.TRAVEL
            else -> null // Default fallback
        }

        val canonicalGenres = if (programData.hasKey("genres")) {
            val genresArray = programData.getArray("genres")
            val genreList = mutableListOf<String>()
            genresArray?.let {
                for (i in 0 until it.size()) {
                    when (it.getString(i)?.uppercase()) {
                        "ANIMAL_WILDLIFE" -> genreList.add(TvContractCompat.Programs.Genres.ANIMAL_WILDLIFE)
                        "ARTS" -> genreList.add(TvContractCompat.Programs.Genres.ARTS)
                        "COMEDY" -> genreList.add(TvContractCompat.Programs.Genres.COMEDY)
                        "DRAMA" -> genreList.add(TvContractCompat.Programs.Genres.DRAMA)
                        "EDUCATION" -> genreList.add(TvContractCompat.Programs.Genres.EDUCATION)
                        "ENTERTAINMENT" -> genreList.add(TvContractCompat.Programs.Genres.ENTERTAINMENT)
                        "FAMILY_KIDS" -> genreList.add(TvContractCompat.Programs.Genres.FAMILY_KIDS)
                        "GAMING" -> genreList.add(TvContractCompat.Programs.Genres.GAMING)
                        "LIFE_STYLE" -> genreList.add(TvContractCompat.Programs.Genres.LIFE_STYLE)
                        "MOVIES" -> genreList.add(TvContractCompat.Programs.Genres.MOVIES)
                        "MUSIC" -> genreList.add(TvContractCompat.Programs.Genres.MUSIC)
                        "NEWS" -> genreList.add(TvContractCompat.Programs.Genres.NEWS)
                        "PREMIER" -> genreList.add(TvContractCompat.Programs.Genres.PREMIER)
                        "SHOPPING" -> genreList.add(TvContractCompat.Programs.Genres.SHOPPING)
                        "SPORTS" -> genreList.add(TvContractCompat.Programs.Genres.SPORTS)
                        "TECH_SCIENCE" -> genreList.add(TvContractCompat.Programs.Genres.TECH_SCIENCE)
                        "TRAVEL" -> genreList.add(TvContractCompat.Programs.Genres.TRAVEL)
                    }
                }
            }
            genreList.toTypedArray()
        } else null


        // Get the type string from JS side (optional)
        val posterArtAspectRatioStr = if (programData.hasKey("posterArtAspectRatio")) programData.getString("posterArtAspectRatio") else null
        val posterArtAspectRatio = when (posterArtAspectRatioStr?.uppercase()) {
            "1_1" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_1_1
            "2_3" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_2_3
            "3_2" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_3_2
            "3_4" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_3_4
            "4_3" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_4_3
            "16_9" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9
            "MOVIE_POSTER" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_MOVIE_POSTER
            else -> null // Default fallback
        }


        // Get the type string from JS side (optional)
        val thumbnailAspectRatioStr = if (programData.hasKey("thumbnailAspectRatio")) programData.getString("thumbnailAspectRatio") else null
        val thumbnailAspectRatio = when (thumbnailAspectRatioStr?.uppercase()) {
            "1_1" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_1_1
            "2_3" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_2_3
            "3_2" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_3_2
            "3_4" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_3_4
            "4_3" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_4_3
            "16_9" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9
            "MOVIE_POSTER" -> TvContractCompat.PreviewPrograms.ASPECT_RATIO_MOVIE_POSTER
            else -> null // Default fallback
        }


        // Get the type string from JS side (optional)
        val availabilityStr = if (programData.hasKey("availability")) programData.getString("availability") else null
        val availability = when (availabilityStr?.uppercase()) {
            "AVAILABILITY_AVAILABLE" -> TvContractCompat.PreviewPrograms.AVAILABILITY_AVAILABLE
            "AVAILABILITY_FREE_WITH_SUBSCRIPTION" -> TvContractCompat.PreviewPrograms.AVAILABILITY_FREE_WITH_SUBSCRIPTION
            "AVAILABILITY_FREE_WITH_ADS" -> TvContractCompat.PreviewPrograms.AVAILABILITY_FREE_WITH_ADS
            "AVAILABILITY_PAID_CONTENT" -> TvContractCompat.PreviewPrograms.AVAILABILITY_PAID_CONTENT
            "AVAILABILITY_FREE" -> TvContractCompat.PreviewPrograms.AVAILABILITY_FREE
            "AVAILABILITY_PURCHASED" -> TvContractCompat.PreviewPrograms.AVAILABILITY_PURCHASED
            else -> null // Default fallback
        }


        val contentRatings = if (programData.hasKey("contentRatings")) {
            val ratingsArray = programData.getArray("contentRatings")
            val ratingList = mutableListOf<TvContentRating>()
            ratingsArray?.let {
                for (i in 0 until it.size()) {
                    val r = it.getMap(i) ?: continue
                    val ratingSystem = r.getString("ratingSystem") ?: continue  // e.g. "US_TV"
                    val rating = r.getString("rating") ?: continue              // e.g. "US_TV_MA"
                    val domain = if (r.hasKey("domain")) r.getString("domain") else "com.android.tv"

                    val subRatingsArray = if (r.hasKey("subRatings")) r.getArray("subRatings") else null
                    val subRatingsList = mutableListOf<String>()
                    subRatingsArray?.let { array ->
                        for (j in 0 until array.size()) {
                            array.getString(j)?.let { subRatingStr -> subRatingsList.add(subRatingStr) }
                        }
                    }
                    ratingList
                        .add(TvContentRating.createRating(domain, ratingSystem, rating, *subRatingsList.toTypedArray()))
                }
            }
            ratingList.toTypedArray()
        } else null


        val reviewRating = if (programData.hasKey("reviewRating")) programData.getString("reviewRating") else null
        val reviewRatingStyleStr = if (programData.hasKey("reviewRatingStyle")) programData.getString("reviewRatingStyle") else null
        val reviewRatingStyle = when (reviewRatingStyleStr?.uppercase()) {
            "STARS" -> TvContractCompat.PreviewPrograms.REVIEW_RATING_STYLE_STARS
            "THUMBS_UP_DOWN" -> TvContractCompat.PreviewPrograms.REVIEW_RATING_STYLE_THUMBS_UP_DOWN
            "PERCENTAGE" -> TvContractCompat.PreviewPrograms.REVIEW_RATING_STYLE_PERCENTAGE
            else -> TvContractCompat.PreviewPrograms.REVIEW_RATING_STYLE_STARS // non-null default, same pattern as your other Int fields
        }

        builder.setInternalProviderId(contentId)
            .setType(programType)
            .setTitle(title)
            .setDescription(description)
            .setLastPlaybackPositionMillis(playbackPosition)
            .setDurationMillis(duration)
            .setGenre(genre)
            .setPreviewVideoUri(previewVideoUrl)
            .setLogoUri(logoUrl)
            .setLogoContentDescription(logoContentDescription)
            .setPosterArtUri(Uri.parse(posterUrl))
            .setThumbnailUri(thumbnailUrl)
            .setOfferPrice(price)
            .setStartingPrice(startingPrice)
            .setLive(isLive)
            .setReleaseDate(releaseDate)
            .setIntentUri(Uri.parse(deepLinkUri))

        startTime?.let { builder.setStartTimeUtcMillis(it) }
        endTime?.let { builder.setEndTimeUtcMillis(it) }
        canonicalGenres?.let { builder.setCanonicalGenres(it) }
        contentRatings?.let { builder.setContentRatings(it) }
        reviewRating?.let { builder.setReviewRating(it).setReviewRatingStyle(reviewRatingStyle) }
        posterArtAspectRatio?.let { builder.setPosterArtAspectRatio(it) }
        thumbnailAspectRatio?.let { builder.setThumbnailAspectRatio(it) }
        availability?.let { builder.setAvailability(it) }
        videoHeight?.let { builder.setVideoHeight(it) }
        videoWidth?.let { builder.setVideoWidth(it) }

        if (programType == TvContractCompat.PreviewPrograms.TYPE_TV_EPISODE) {
            val seasonNumber = if (programData.hasKey("seasonNumber")) programData.getInt("seasonNumber") else 1
            val episodeNumber = if (programData.hasKey("episodeNumber")) programData.getInt("episodeNumber") else 1
            val episodeTitle = if (programData.hasKey("episodeTitle")) programData.getString("episodeTitle") else null
            val seasonTitle = if (programData.hasKey("seasonTitle")) programData.getString("seasonTitle") else "Season $seasonNumber"

            builder.setEpisodeTitle(episodeTitle)
                .setEpisodeNumber(episodeNumber)
                .setSeasonNumber(seasonNumber)
                .setSeasonTitle(seasonTitle)
        }

        if (programType == TvContractCompat.PreviewPrograms.TYPE_CLIP) {
            val author = if (programData.hasKey("author")) programData.getString("author") else null
            val googleContentIndexingId = if (programData.hasKey("googleContentIndexingId")) programData.getString("googleContentIndexingId") else null
            builder.setAuthor(author)
                .setContentId(googleContentIndexingId)
        }

        if (programType == TvContractCompat.PreviewPrograms.TYPE_TV_SERIES) {
            val episodesCount = if (programData.hasKey("episodesCount")) programData.getInt("episodesCount") else 1
            builder.setItemCount(episodesCount)
        }

        return builder
    }

    @ReactMethod
    fun addProgramToWatchNext(
        programData: ReadableMap, promise: Promise
    ) {
        try {
            val contentId = programData.getString("contentId") ?: throw IllegalArgumentException("Missing contentId")

            val builder = WatchNextProgram.Builder()
            .setWatchNextType(TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE)
            .setLastEngagementTimeUtcMillis(System.currentTimeMillis())
            applyCommonFields(builder, programData)

            val program = builder.build()

            val resolver = reactApplicationContext.contentResolver
            val existingUri = findUriByContentId(contentId)

            val resultUri: Uri? = if (existingUri != null) {
                resolver.update(existingUri, program.toContentValues(), null, null)
                existingUri
            } else {
                resolver.insert(TvContractCompat.WatchNextPrograms.CONTENT_URI, program.toContentValues())
            }

            promise.resolve(resultUri?.toString())
        } catch (e: Exception) {
            promise.reject("WATCH_NEXT_ERROR", e)
        }
    }

    private fun getChannelCountFunc(): Int {
        var count = 0
        reactApplicationContext.contentResolver.query(
            TvContractCompat.Channels.CONTENT_URI, arrayOf(TvContractCompat.Channels._ID), null, null, null
        )?.use { cursor -> count = cursor.count }
        return count
    }


    @ReactMethod
    fun getChannelsCount(promise: Promise) {
        try { promise.resolve(getChannelCountFunc()) }
        catch (e: Exception) { promise.reject("CHANNEL_ERROR", e) }
    }

    @ReactMethod
    fun createChannel(programData: ReadableMap, promise: Promise) {
        try {
            val displayName = programData.getString("displayName") ?: throw IllegalArgumentException("Missing displayName")
            val appLinkUri = programData.getString("appLinkUri") ?: throw IllegalArgumentException("Missing appLinkUri")
            val description = if (programData.hasKey("description")) programData.getString("description") else null

            val isFirstChannel = getChannelCountFunc() == 0

            val builder = Channel.Builder()
                .setType(TvContractCompat.Channels.TYPE_PREVIEW)
                .setDisplayName(displayName)
                .setDescription(description)
                .setAppLinkIntentUri(appLinkUri.toUri())

            if (programData.hasKey("appLinkText")) builder.setAppLinkText(programData.getString("appLinkText"))
            if (programData.hasKey("appLinkColor")) builder.setAppLinkColor(programData.getInt("appLinkColor"))
            if (programData.hasKey("appLinkIconUri")) builder.setAppLinkIconUri(programData.getString("appLinkIconUri")!!.toUri())
            if (programData.hasKey("appLinkPosterArtUri")) builder.setAppLinkPosterArtUri(programData.getString("appLinkPosterArtUri")!!.toUri())
            if (programData.hasKey("displayOrder")) builder.setConfigurationDisplayOrder(programData.getInt("displayOrder"))
            if (programData.hasKey("searchable")) builder.setSearchable(programData.getBoolean("searchable"))

            if (isFirstChannel) builder.setInternalProviderId("default_channel")

            val channel = builder.build()

            val channelUri = reactApplicationContext.contentResolver.insert(
                TvContractCompat.Channels.CONTENT_URI, channel.toContentValues()
            )
            val channelId = ContentUris.parseId(channelUri!!)

            // Only non-default channels need the explicit browsable request —
            // the first channel your app creates auto-appears without it
            if (!isFirstChannel) {
                TvContractCompat.requestChannelBrowsable(reactApplicationContext, channelId)
            }

            promise.resolve(channelId.toString())
        } catch (e: Exception) {
            promise.reject("CHANNEL_ERROR", e)
        }
    }

    @ReactMethod
    fun updateChannel(channelId: String, programData: ReadableMap, promise: Promise) {
        try {
            val chId = channelId.toLong()
            val resolver = reactApplicationContext.contentResolver

            var existingChannel: Channel? = null
            resolver.query(TvContractCompat.buildChannelUri(chId), null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) existingChannel = Channel.fromCursor(cursor)
            }
            val existing = existingChannel ?: throw IllegalArgumentException("No channel found for id $channelId")

            val builder = Channel.Builder(existing) // preserves untouched fields

            if (programData.hasKey("displayName")) builder.setDisplayName(programData.getString("displayName"))
            if (programData.hasKey("description")) builder.setDescription(programData.getString("description"))
            if (programData.hasKey("appLinkUri")) builder.setAppLinkIntentUri(programData.getString("appLinkUri")!!.toUri())
            if (programData.hasKey("appLinkText")) builder.setAppLinkText(programData.getString("appLinkText"))
            if (programData.hasKey("appLinkColor")) builder.setAppLinkColor(programData.getInt("appLinkColor"))
            if (programData.hasKey("appLinkIconUri")) builder.setAppLinkIconUri(programData.getString("appLinkIconUri")!!.toUri())
            if (programData.hasKey("appLinkPosterArtUri")) builder.setAppLinkPosterArtUri(programData.getString("appLinkPosterArtUri")!!.toUri())
            if (programData.hasKey("displayOrder")) builder.setConfigurationDisplayOrder(programData.getInt("displayOrder"))
            if (programData.hasKey("searchable")) builder.setSearchable(programData.getBoolean("searchable"))

            resolver.update(TvContractCompat.buildChannelUri(chId), builder.build().toContentValues(), null, null)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("CHANNEL_ERROR", e)
        }
    }

    @ReactMethod
    fun deleteChannel(options: ReadableMap, promise: Promise) {
        try {
            val channelId = options.getString("channelId") ?: throw IllegalArgumentException("Missing channelId")
            val forceDeleteIfDefault = if (options.hasKey("forceDeleteIfDefault")) options.getBoolean("forceDeleteIfDefault") else false

            val chId = channelId.toLong()
            val resolver = reactApplicationContext.contentResolver

            var isDefault = false
            resolver.query(TvContractCompat.buildChannelUri(chId), null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    isDefault = Channel.fromCursor(cursor).internalProviderId == "default_channel"
                }
            }

            if (isDefault && !forceDeleteIfDefault) {
                promise.reject("DEFAULT_CHANNEL_PROTECTED", "This is the default channel — pass forceDeleteIfDefault: true to delete it anyway.")
                return
            }

            resolver.delete(TvContractCompat.buildChannelUri(chId), null, null)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("CHANNEL_ERROR", e)
        }
    }

    @ReactMethod
    fun getChannels(promise: Promise) {
        try {
            val channels = Arguments.createArray()
            reactApplicationContext.contentResolver.query(
                TvContractCompat.Channels.CONTENT_URI, null, null, null, null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val channel = Channel.fromCursor(cursor)
                    val map = Arguments.createMap()
                    map.putString("id", channel.id.toString())
                    map.putString("packageName", channel.packageName)
                    map.putString("inputId", channel.inputId)
                    map.putString("type", channel.type)
                    map.putString("displayName", channel.displayName)
                    map.putString("displayNumber", channel.displayNumber)
                    map.putString("description", channel.description)
                    map.putString("videoFormat", channel.videoFormat)

                    map.putBoolean("isBrowsable", channel.isBrowsable)
                    map.putBoolean("isSearchable", channel.isSearchable)
                    map.putBoolean("isLocked", channel.isLocked)
                    map.putBoolean("isTransient", channel.isTransient)

                    map.putString("appLinkUri", channel.appLinkIntentUri?.toString())
                    map.putString("appLinkText", channel.appLinkText)
                    map.putString("appLinkColor", channel.appLinkColor?.toString())
                    map.putString("appLinkIconUri", channel.appLinkIconUri?.toString())
                    map.putString("appLinkPosterArtUri", channel.appLinkPosterArtUri?.toString())
                    map.putString("internalProviderId", channel.internalProviderId)

                    channels.pushMap(map)
                }
            }
            promise.resolve(channels)
        } catch (e: Exception) {
            promise.reject("CHANNEL_ERROR", e)
        }
    }

    @ReactMethod
    fun addProgramToChannel(channelId: String, programData: ReadableMap, promise: Promise) {
        try {
            val contentId = programData.getString("contentId") ?: throw IllegalArgumentException("Missing contentId")
            val chId = channelId.toLong()

            val builder = PreviewProgram.Builder().setChannelId(chId)
            applyCommonFields(builder, programData) // see refactor note below
            val program = builder.build()

            val resolver = reactApplicationContext.contentResolver
            val existingUri = findChannelContentUri(chId, contentId)
            val resultUri = if (existingUri != null) {
                resolver.update(existingUri, program.toContentValues(), null, null)
                existingUri
            } else {
                resolver.insert(TvContractCompat.PreviewPrograms.CONTENT_URI, program.toContentValues())
            }
            promise.resolve(resultUri.toString())
        } catch (e: Exception) {
            promise.reject("CHANNEL_ERROR", e)
        }
    }

    @ReactMethod
    fun remove(contentId: String, promise: Promise) {
        try {
            val resolver = reactApplicationContext.contentResolver
            var removedCount = 0

            // Remove from Watch Next, if present
            findUriByContentId(contentId)?.let {
                resolver.delete(it, null, null)
                removedCount++
            }

            // Remove from every channel that has this contentId — not just the first match
            resolver.query(
                TvContractCompat.PreviewPrograms.CONTENT_URI,
                arrayOf(TvContractCompat.PreviewPrograms._ID, TvContractCompat.PreviewPrograms.COLUMN_INTERNAL_PROVIDER_ID),
                null, null, null
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(TvContractCompat.PreviewPrograms._ID)
                val pidIdx = cursor.getColumnIndexOrThrow(TvContractCompat.PreviewPrograms.COLUMN_INTERNAL_PROVIDER_ID)
                while (cursor.moveToNext()) {
                    if (cursor.getString(pidIdx) == contentId) {
                        resolver.delete(TvContractCompat.buildPreviewProgramUri(cursor.getLong(idIdx)), null, null)
                        removedCount++
                    }
                }
            }

            promise.resolve(removedCount)
        } catch (e: Exception) {
            promise.reject("REMOVE_ERROR", e)
        }
    }

    @ReactMethod
    fun removeFromChannel(contentId: String, channelId: String, promise: Promise) {
        try {
            val uri = findChannelContentUri(channelId.toLong(), contentId)
            if (uri != null) {
                val rowsDeleted = reactApplicationContext.contentResolver.delete(uri, null, null)
                promise.resolve(rowsDeleted > 0)
            } else {
                promise.resolve(false)
            }
        } catch (e: Exception) {
            promise.reject("WATCH_NEXT_ERROR", e)
        }
    }

    @ReactMethod
    fun removeWatchNext(contentId: String, promise: Promise) {
        try {
            val uri =  findUriByContentId(contentId)
            if (uri != null) {
                val rowsDeleted = reactApplicationContext.contentResolver.delete(uri, null, null)
                promise.resolve(rowsDeleted > 0)
            } else {
                promise.resolve(false)
            }
        } catch (e: Exception) {
            promise.reject("WATCH_NEXT_ERROR", e)
        }
    }

    private fun clearAllWatchNextInternal(): Int {
        var removed = 0
        reactApplicationContext.contentResolver.query(
            TvContractCompat.WatchNextPrograms.CONTENT_URI, arrayOf(TvContractCompat.WatchNextPrograms._ID), null, null, null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(TvContractCompat.WatchNextPrograms._ID)
            while (cursor.moveToNext()) {
                reactApplicationContext.contentResolver.delete(TvContractCompat.buildWatchNextProgramUri(cursor.getLong(idIndex)), null, null)
                removed++
            }
        }
        return removed
    }

    @ReactMethod
    fun clearAll(promise: Promise) {
        try {
            var removed = 0
            reactApplicationContext.contentResolver.query(
                TvContractCompat.PreviewPrograms.CONTENT_URI,
                arrayOf(TvContractCompat.PreviewPrograms._ID, TvContractCompat.PreviewPrograms.COLUMN_CHANNEL_ID),
                null, null, null
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(TvContractCompat.PreviewPrograms._ID)
                while (cursor.moveToNext()) {
                    reactApplicationContext.contentResolver.delete(
                        TvContractCompat.buildPreviewProgramUri(cursor.getLong(idIdx)), null, null
                    )
                    removed++
                }
            }

            val totalRemoved = removed + clearAllWatchNextInternal()

            promise.resolve(totalRemoved)
        } catch (e: Exception) {
            promise.reject("CHANNEL_ERROR", e)
        }
    }

    @ReactMethod
    fun clearChannel(channelId: String, promise: Promise) {
        try {
            val chId = channelId.toLong()
            var removed = 0
            reactApplicationContext.contentResolver.query(
                TvContractCompat.PreviewPrograms.CONTENT_URI,
                arrayOf(TvContractCompat.PreviewPrograms._ID, TvContractCompat.PreviewPrograms.COLUMN_CHANNEL_ID),
                null, null, null
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(TvContractCompat.PreviewPrograms._ID)
                val chIdx = cursor.getColumnIndexOrThrow(TvContractCompat.PreviewPrograms.COLUMN_CHANNEL_ID)
                while (cursor.moveToNext()) {
                    if (cursor.getLong(chIdx) == chId) {
                        reactApplicationContext.contentResolver.delete(
                            TvContractCompat.buildPreviewProgramUri(cursor.getLong(idIdx)), null, null
                        )
                        removed++
                    }
                }
            }
            promise.resolve(removed)
        } catch (e: Exception) {
            promise.reject("CHANNEL_ERROR", e)
        }
    }

    @ReactMethod
    fun clearWatchNext(promise: Promise) {
        try { promise.resolve(clearAllWatchNextInternal()) }
        catch (e: Exception) { promise.reject("WATCH_NEXT_ERROR", e) }
    }
}
