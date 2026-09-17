/**
 * Supported program types for the Android TV Watch Next channel.
 * Maps directly to native `TvContractCompat.WatchNextPrograms` types.
 */
export enum WatchNextProgramType {
  /** Used for feature-length films. */
  MOVIE = 'MOVIE',
  /** Used for an individual episode of a TV show or series. Enables season and episode UI formatting. */
  TV_EPISODE = 'TV_EPISODE',
  /** Used for an entire TV show/series entry rather than an individual episode. */
  TV_SERIES = 'TV_SERIES',
  /** Used for short-form video clips, previews, or trailers. */
  CLIP = 'CLIP',
}

/**
 * Supported content types for programs published to either the Watch Next
 * channel or a custom home screen channel.
 *
 * Maps directly to native `TvContractCompat.PreviewPrograms`/`TvContractCompat.WatchNextPrograms` types.
 */
export enum ProgramType {
  /** Used for feature-length films. */
  MOVIE = 'MOVIE',
  /** Used for an individual episode of a TV show or series. Enables season and episode UI formatting. */
  TV_EPISODE = 'TV_EPISODE',
  /** Used for an entire TV show/series entry rather than an individual episode. */
  TV_SERIES = 'TV_SERIES',
  /** Used for short-form video clips, previews, or trailers. */
  CLIP = 'CLIP',
}

/**
 * The review rating score style used for `reviewRating`, matching `TvContractCompat.PreviewPrograms.REVIEW_RATING_STYLE_*`.
 * @enum ReviewRatingStyle
 */
export enum ReviewRatingStyle {
  /** The review rating style for five-star rating. */
  STARS = 'STARS',
  /** The review rating style for thumbs-up and thumbs-down rating. */
  THUMBS_UP_DOWN = 'THUMBS_UP_DOWN',
  /** The review rating style for 0 to 100 point system. */
  PERCENTAGE = 'PERCENTAGE',
}

/**
 * Canonical genre classification, matching `TvContractCompat.Programs.Genres`.
 * @enum GenreType
 */
export enum GenreType {
  ANIMAL_WILDLIFE = 'ANIMAL_WILDLIFE',
  ARTS = 'ARTS',
  COMEDY = 'COMEDY',
  DRAMA = 'DRAMA',
  EDUCATION = 'EDUCATION',
  ENTERTAINMENT = 'ENTERTAINMENT',
  FAMILY_KIDS = 'FAMILY_KIDS',
  GAMING = 'GAMING',
  LIFE_STYLE = 'LIFE_STYLE',
  MOVIES = 'MOVIES',
  MUSIC = 'MUSIC',
  NEWS = 'NEWS',
  PREMIER = 'PREMIER',
  SHOPPING = 'SHOPPING',
  SPORTS = 'SPORTS',
  TECH_SCIENCE = 'TECH_SCIENCE',
  TRAVEL = 'TRAVEL',
}

/**
 * Aspect ratio hints for poster art / thumbnail images, matching
 * `TvContractCompat.PreviewPrograms.ASPECT_RATIO_*` constants.
 */
export enum AspectRatio {
  RATIO_1_1 = '1_1',
  RATIO_2_3 = '2_3',
  RATIO_3_2 = '3_2',
  RATIO_3_4 = '3_4',
  RATIO_4_3 = '4_3',
  RATIO_16_9 = '16_9',
  MOVIE_POSTER = 'MOVIE_POSTER',
}

/**
 * Content availability status, matching
 * `TvContractCompat.PreviewPrograms.AVAILABILITY_*` constants.
 */
export enum Availability {
  AVAILABILITY_AVAILABLE = 'AVAILABILITY_AVAILABLE',
  AVAILABILITY_FREE = 'AVAILABILITY_FREE',
  AVAILABILITY_FREE_WITH_ADS = 'AVAILABILITY_FREE_WITH_ADS',
  AVAILABILITY_FREE_WITH_SUBSCRIPTION = 'AVAILABILITY_FREE_WITH_SUBSCRIPTION',
  AVAILABILITY_PAID_CONTENT = 'AVAILABILITY_PAID_CONTENT',
  AVAILABILITY_PURCHASED = 'AVAILABILITY_PURCHASED',
}

/**
 * Content rating systems, matching Android System Defined Constants for Content Ratings.
 *
 * @enum ContentRatingSystem
 * @see {@link https://developer.android.com/reference/android/media/tv/TvContentRating#system-defined-strings-for-rating-systems Android System Defined Strings for Rating Systems}
 */
export enum ContentRatingSystem {
  /** TV content rating system for Argentina */
  AR_TV = 'AR_TV',
  /** TV content rating system for Australia */
  AU_TV = 'AU_TV',
  /** TV content rating system for Brazil */
  BR_TV = 'BR_TV',
  /** TV content rating system for Canada (English) */
  CA_TV_EN = 'CA_TV_EN',
  /** TV content rating system for Canada (French) */
  CA_TV_FR = 'CA_TV_FR',
  /** DTMB content rating system */
  DTMB = 'DTMB',
  /** DVB content rating system */
  DVB = 'DVB',
  /** DVB content rating system for Spain */
  ES_DVB = 'ES_DVB',
  /** DVB content rating system for France */
  FR_DVB = 'FR_DVB',
  /** ISDB content rating system */
  ISDB = 'ISDB',
  /** TV content rating system for South Korea */
  KR_TV = 'KR_TV',
  /** TV content rating system for New Zealand */
  NZ_TV = 'NZ_TV',
  /** TV content rating system for Singapore */
  SG_TV = 'SG_TV',
  /** TV content rating system for Thailand */
  TH_TV = 'TH_TV',
  /** Movie content rating system for the United States */
  US_MV = 'US_MV',
  /** TV content rating system for the United States */
  US_TV = 'US_TV',
}

/**
 * A single content rating entry, built natively via `TvContentRating.createRating()`.
 */
export interface ContentRating {
  /**
   * The rating system this value belongs to, e.g. `"US_TV"` or `"US_MV"`.
   *
   * You can use the enum `ContentRatingSystem`, or custom rating system if you have set a custom `domain`
   *
   * @see {@link https://developer.android.com/reference/android/media/tv/TvContentRating#system-defined-strings-for-rating-systems Android System Defined Strings for Rating Systems}
   */
  ratingSystem: ContentRatingSystem | string;
  /**
   * The specific rating within that system, e.g. `"US_TV_MA"`.
   *
   * @see {@link https://developer.android.com/reference/android/media/tv/TvContentRating#system-defined-strings-for-ratings Android System Defined Strings for Ratings}
   */
  rating: string;
  /**
   * Content descriptors alongside the main rating, e.g. `["D", "L", "S", "V"]` for dialogue/language/sex/violence in the US (and Brazil) rating systems.
   *
   * **Note that** `D`,`L`,`S`,`V` are not the actual values so verify the proper sub-rating content descriptors for the rating system chosen.
   *
   * @see {@link https://developer.android.com/reference/android/media/tv/TvContentRating#system-defined-strings-for-sub-ratings Android System Defined Strings for Sub-ratings}
   * */
  subRatings?: string[];
  /**
   * The rating domain.
   *
   * Defaults natively to `com.android.tv` if omitted -
   * only override this for a non-standard/custom rating domain.
   *
   * @default com.android.tv
   * @see {@link https://developer.android.com/reference/android/media/tv/TvContentRating#system-defined-string-for-domains Android System Defined Strings for Domains}
   */
  domain?: string;
}

/**
 * Metadata object required to publish or update a program.
 *
 * Used by both `addProgramToWatchNext` (the system Watch Next row) and `addProgramToChannel`
 * (a custom channel you created). The set of fields that get used natively
 * depends on the `type` (ProgramType) set.
 */
export interface ProgramData {
  /**
   * Unique identifier generated by your backend/app.
   *
   * Used as the internal provider lookup key to find, update or remove this specific asset.
   */
  contentId: string;

  /**
   * The primary title of the content displayed on the home screen.
   *
   * **Important Note:** for type: `TV_EPISODE`, this must be the **series name**
   * (e.g. "Breaking Bad"), not the episode's own name. The episode's name
   * goes in `episodeTitle` instead. Getting it backwards fails silently
   * *(no error, just a mislabeled card)*.
   */
  title: string;

  /** A short description, synopsis, or plot summary of the content. */
  description?: string;

  /**
   * The category classification of the content.
   *
   * @type ProgramType
   * @default ProgramType.MOVIE
   */
  type?: ProgramType;

  /** The fully qualified URL or local resource path to the poster art.
   *
   * It will also be used as the thumbnail if `thumbnailUrl` is not provided. */
  posterUrl: string;

  /** Aspect ratio hint for `posterUrl`.
   * @type AspectRatio
   */
  posterArtAspectRatio?: AspectRatio;

  /** Optional secondary/alternate thumbnail image, distinct from the poster art. */
  thumbnailUrl?: string;

  /**
   * Aspect ratio hint for `thumbnailUrl`.
   * @type AspectRatio
   * */
  thumbnailAspectRatio?: AspectRatio;

  /**
   * A short autoplay video clip shown when the card receives focus.
   */
  previewVideoUrl?: string;

  /** Small overlay logo/badge shown on the card (e.g. a studio or "Original" mark). */
  logoUrl?: string;

  /** Accessibility content description for `logoUrl`. */
  logoContentDescription?: string;

  /**
   * The deep-link URI (e.g. `myApp://play/123`) that Android TV will trigger
   * when the user selects this card from the home screen launcher.
   */
  deepLinkUri: string;

  /**
   * The user's current playback position in milliseconds.
   *
   * Android TV uses this to calculate and render the progress bar under the card.
   *
   * Defaults to `1ms`
   *
   * @default 1
   */
  playbackPosition?: number;

  /**
   * The total duration of the media in `milliseconds`.
   *
   * Although it is optional, and defaults to `10secs` (`10000ms`), it is expected you provide a valid number here
   * to display the accurate, real watch progress of this content.
   *
   * @default 10000
   */
  duration?: number;

  /**
   * Single genre classification.
   *
   * For multiple genres, use `genres` instead.
   * @type GenreType
   * */
  genre?: GenreType;

  /**
   * Multiple genre tags for this title (a superset of `genre`).
   * @type GenreType[]
   * */
  genres?: GenreType[];

  /**
   * Availability (or pricing status) shown as a badge on the card.
   * @type Availability
   * */
  availability?: Availability;

  /** Display price for rent/purchase content (e.g. `"$3.99"`). */
  price?: string;

  /** Starting price for content offered at multiple price tiers (e.g. `"$1.99"`). */
  startingPrice?: string;

  /**
   * Content ratings (parental guidance) for this title.
   * @type ContentRating[]
   * */
  contentRatings?: ContentRating[];

  /**
   * The review rating score for this program. The format of the value is dependent on `ReviewRatingStyle`.
   *
   * - `STARS`: the value should be a real number between 0.0 and 5.0. (e.g. "`4.5`")
   * - `THUMBS_UP_DOWN`: the value should be two integers, one for thumbs-up count and the other for thumbs-down count, with a comma between them. (e.g. "`200,40`"). Follow this format "`<up count>,<down count>`"
   * - `PERCENTAGE`: the value should be a real number between 0 and 100. (e.g. "`99.9`")
   */
  reviewRating?: string;

  /**
   * The review rating score style used for `reviewRating`
   *
   * @type ReviewRatingStyle
   * @default ReviewRatingStyle.STARS
   */
  reviewRatingStyle?: ReviewRatingStyle;

  /** The content's original release date. */
  releaseDate?: string;

  /**
   * Whether this is a live broadcast rather than on-demand content.
   * @default false
   * */
  isLive?: boolean;

  /** Scheduled start time in epoch milliseconds.
   *
   * Only displayed for `isLive: true`. */
  startTime?: number;

  /** Scheduled end time in epoch milliseconds.
   *
   * Only displayed for `isLive: true`. */
  endTime?: number;

  /** Source video height in pixels, if known. */
  videoHeight?: number;

  /** Source video width in pixels, if known. */
  videoWidth?: number;

  // --- Fields only applied when type === ProgramType.TV_EPISODE ---

  /**
   * The specific title of this episode (e.g., "Pilot").
   *
   * Only rendered on the TV launcher if `type` is set to `ProgramType.TV_EPISODE`.
   */
  episodeTitle?: string;

  /**
   * The sequential number of this episode.
   *
   * Only processed if `type` is set to `ProgramType.TV_EPISODE`.
   * @default 1
   */
  episodeNumber?: number;

  /**
   * The sequential number of this episode's season.
   *
   * Only processed if `type` is set to `ProgramType.TV_EPISODE`.
   * @default 1
   */
  seasonNumber?: number;

  /**
   * The display name of the season (e.g., "The Final Season").
   *
   * Only processed if `type` is set to `ProgramType.TV_EPISODE`.
   *
   * @default "Season [seasonNumber]"
   */
  seasonTitle?: string;

  // --- Fields only applied when type === ProgramType.CLIP ---

  /** The clip's author/artist.
   *
   * Only processed if `type` is set to `ProgramType.CLIP`.
   */
  author?: string;

  /** ID linking this clip to a Google content-indexing entry.
   *
   * Only processed if `type` is set to `ProgramType.CLIP`.
   */
  googleContentIndexingId?: string;

  // --- Fields only applied when type === ProgramType.TV_SERIES ---

  /** Total number of episodes in the series, shown as a badge.
   *
   * Only processed if `type` is set to `ProgramType.TV_SERIES`.
   *
   * @default 1 */
  episodesCount?: number;
}

/** Data required to create a new custom home screen channel. */
export interface CreateChannelData {
  /** The channel's title, shown on the home screen. */
  displayName: string;
  /** Deep link triggered when the user selects the channel header itself. */
  appLinkUri: string;
  /** General description of what this channel contains. */
  description?: string;
  /** Custom button label on the app-link card (default is system-generated, e.g. "Open MyApp"). */
  appLinkText?: string;
  /** Accent color (as an ARGB int) for the app-link card. */
  appLinkColor?: number;
  /** Badge icon shown on the app-link card. */
  appLinkIconUri?: string;
  /** Background image for the app-link card. */
  appLinkPosterArtUri?: string;
  /** Controls ordering relative to your other channels on the home screen (lower = higher priority). */
  displayOrder?: number;
  /** Whether this channel's content can surface in system search. */
  searchable?: boolean;
}

/** Partial update to an existing channel - only fields present are changed; the rest are preserved. */
export type UpdateChannelData = Partial<CreateChannelData>;

/** Options for deleting a channel. */
export interface DeleteChannelOptions {
  channelId: string;
  /**
   * The default channel (the first one your app ever created) is protected
   * from deletion by default, since it can't automatically reappear once removed.
   *
   * Pass `true` to force delete it anyway.
   * @default false
   */
  forceDeleteIfDefault?: boolean;
}

/** A channel as returned by `getChannels()`. */
export interface TvChannel {
  id: string;
  packageName: string | null;
  inputId: string | null;
  type: string | null;
  displayName: string | null;
  displayNumber: string | null;
  description: string | null;
  videoFormat: string | null;
  isBrowsable: boolean;
  isSearchable: boolean;
  isLocked: boolean;
  isTransient: boolean;
  appLinkUri: string | null;
  appLinkText: string | null;
  /** Stringified ARGB color int. */
  appLinkColor: string | null;
  appLinkIconUri: string | null;
  appLinkPosterArtUri: string | null;
  /** `"default_channel"` if this is the app's protected default channel; otherwise `null`. */
  internalProviderId: string | null;
}

/**
 * Interface representing the exposed React Native Bridge Module methods.
 */
export interface ITvRecommendedContentModule {
  /**
   * Publishes a program to the Android TV "Watch Next" row.
   * If the item already exists (matched by `contentId`), its progress and metadata will be updated instead.
   *
   * @returns The system content provider URI string for the created/updated entry.
   */
  addProgramToWatchNext(programData: ProgramData): Promise<string>;

  /**
   * Removes a program from the Watch Next row specifically.
   * @returns A promise resolving to `true` if an item was found and deleted, or `false` if not found.
   */
  removeWatchNext(contentId: string): Promise<boolean>;

  /** Wipes all Watch Next entries belonging to your app.
   * @returns Count of items removed. */
  clearWatchNext(): Promise<number>;

  /**
   * Creates a new custom channel on the home screen.
   *
   * If your app has no channels yet, the first created channel is automatically tagged and
   * treated as the default channel (auto-appears, no user approval needed) -
   * every subsequent channel requires explicit user approval to appear.
   * @returns The new channel's ID.
   */
  createChannel(data: CreateChannelData): Promise<string>;

  /**
   * Updates an existing channel. Only fields present in `data` are changed -
   * anything omitted is left as-is.
   *
   * @returns A promise resolving to `true` if a channel was found and updated, or `false` otherwise.
   */
  updateChannel(channelId: string, data: UpdateChannelData): Promise<boolean>;

  /**
   * Deletes a channel.
   *
   * Promise rejects with status code `DEFAULT_CHANNEL_PROTECTED` if the
   * target channel is the app's default channel and `forceDeleteIfDefault` isn't set to `true`.
   */
  deleteChannel(options: DeleteChannelOptions): Promise<boolean>;

  /**
   * Lists all channels belonging to your app.
   * @return TvChannel[]
   * */
  getChannels(): Promise<TvChannel[]>;

  /** @returns The number of channels your app currently has. */
  getChannelsCount(): Promise<number>;

  /**
   * Publishes a program into a specific channel. If the item already exists
   * in that channel (matched by `contentId`), it's updated in place.
   * @returns The system content provider URI string for the created/updated entry.
   */
  addProgramToChannel(
    channelId: string,
    programData: ProgramData
  ): Promise<string>;

  /**
   * Removes a program from one specific channel.
   * @returns A promise resolving to `true` if an item was found and deleted, or `false` if not found.
   */
  removeFromChannel(contentId: string, channelId: string): Promise<boolean>;

  /** Wipes all programs from one specific channel *(the channel itself is not deleted)*.
   * @returns Removed items Count */
  clearChannel(channelId: string): Promise<number>;

  /**
   * Removes every instance of a given program's `contentId` across **all** channels and
   * the Watch Next row.
   *
   * @param contentId The unique internal ID matching the program to be deleted.
   * @returns The total count of entries removed across all locations.
   */
  remove(contentId: string): Promise<number>;

  /** Wipes everything your app owns - all channel content and Watch Next.
   * @returns Total count removed items. */
  clearAll(): Promise<number>;
}
