# react-native-tv-recommended-content

> Publish content to the **Android TV / Google TV home screen** from your React Native app to the "Continue Watching" (Watch Next) row, and your own fully custom recommendation channels.

[![npm version](https://img.shields.io/npm/v/react-native-tv-recommended-content)](https://www.npmjs.com/package/react-native-tv-recommended-content)
![Typescript](https://img.shields.io/badge/typescript-compatible-brightgreen)
![NPM Downloads](https://img.shields.io/npm/d18m/react-native-tv-recommended-content)
[![license](https://img.shields.io/npm/l/react-native-tv-recommended-content)](./LICENSE)
[![platform](https://img.shields.io/badge/platform-Android%20TV%20%7C%20Google%20TV-brightgreen)](#platform-support)

Built directly on top of `androidx.tvprovider` (`WatchNextProgram`, `PreviewProgram`, and `Channel`), fully typed, and safe to call from any platform — it no-ops gracefully on iOS, tvOS, and non-TV Android instead of crashing.

---

## 📋 Table of Contents

- [Platform Support](#platform-support)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Core Concepts](#core-concepts)
- [API Reference](#api-reference)
  - [Watch Next](#watch-next)
  - [Channel Management](#channel-management)
  - [Programs Within a Channel](#programs-within-a-channel)
  - [Cross-cutting](#cross-cutting)
- [Types](#types)
- [Watch Next Quality Guidelines](#watch-next-quality-guidelines)
- [Troubleshooting](#troubleshooting)
- [Testing Locally](#testing-locally)
- [Contributing](#contributing)
- [License](#license)
- [Support](#support)

---

## 🖥️ Platform Support


| Platform                | Behavior                                                                                                                                                                                                                                 |
| ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Android TV / Google TV  | Fully functional                                                                                                                                                                                                                         |
| Android (mobile/tablet) | All methods resolve to safe defaults (`null` / `false` / `0` / `[]`) — no-op, no crash                                                                                                                                                  |
| iOS / tvOS              | Same safe no-op behavior. There is no tvOS equivalent of Watch Next/Channels (tvOS has its own separate, unrelated "Top Shelf" API), so no native implementation exists for this package at at this time. But could exist in the future. |

You never need to branch your own code by platform — every method is safe to call unconditionally from shared code.

---

## 📦 Installation

```sh
npm install react-native-tv-recommended-content
# or
yarn add react-native-tv-recommended-content
```

No manual native linking or `AndroidManifest.xml` changes are required — autolinking handles registration, and Watch Next / Channels don't require any special Android permissions.

---

## 🔧 Quick Start

```ts
import TVRecommendedContent, { ProgramType } from 'react-native-tv-recommended-content';

// Add/update a movie in the Watch Next row (call this on pause/exit)
await TVRecommendedContent.addProgramToWatchNext({
  contentId: 'movie-123',
  title: 'The Great Adventure',
  posterUrl: 'https://example.com/poster.jpg',
  deepLinkUri: 'myapp://play/movie-123?resume=452000',
  playbackPosition: 452000, // 7m32s in, in milliseconds
  duration: 5400000,        // 90 minutes total
  type: ProgramType.MOVIE,
});

// Create a custom channel — the first one you ever create becomes your
// app's protected "default" channel automatically (see Core Concepts below)
const channelId = await TVRecommendedContent.createChannel({
  displayName: 'New Releases',
  appLinkUri: 'myapp://browse/new-releases',
});

// Add content into that channel
await TVRecommendedContent.addProgramToChannel(channelId, {
  contentId: 'movie-456',
  title: 'Another Great Film',
  posterUrl: 'https://example.com/poster2.jpg',
  deepLinkUri: 'myapp://play/movie-456',
  type: ProgramType.MOVIE,
});
```

You can also import individual functions instead of the default export:

```ts
import { getChannels, remove } from 'react-native-tv-recommended-content';

const channels = await getChannels();
const removedCount = await remove('movie-456'); // removes from everywhere at once
```

---

## ❤️ Core Concepts

1. **Watch Next** is a single, system-managed row ("Continue Watching") shared across all apps. You don't create it, you just publish/remove entries from it.
2. **Channels** are rows *you* create and fully control (e.g. "New Releases", "Because You Watched X"). Each channel can hold many programs.
3. **The default channel:** the very first channel your app creates is automatically treated by the system as browsable. It appears on the home screen immediately, with no user approval needed. Every channel after that requires the user to explicitly approve adding it (the system shows a permission prompt when you call `createChannel`). This package tracks which channel is yours to protect. The `deleteChannel` api will refuse to delete the default channel unless you explicitly pass `forceDeleteIfDefault: true`, since a deleted default channel can't automatically reappear.
4. **`contentId`** is your own stable identifier for a piece of content (e.g. your backend's movie/episode ID). Every method that looks up, updates, or removes a program does so by matching this field, not by any system-generated ID.

---

## 🔑️ API Reference

### Watch Next

- #### `addProgramToWatchNext(programData: ProgramData): Promise<string | null>`:
  Publishes or updates an entry in the Watch Next row. If an entry with the same `contentId` already exists, it's updated in place (its progress and metadata refreshed) rather than duplicated. Resolves with the content provider URI string, or `null` on unsupported platforms.
- #### `removeWatchNext(contentId: string): Promise<boolean>`:
  Removes one entry from Watch Next specifically. Resolves `true` if something was found and removed, `false` otherwise.
- #### `clearWatchNext(): Promise<number>`:
  Removes every Watch Next entry belonging to your app. Resolves with the count removed.

### Channel Management

- #### `createChannel(data: CreateChannelData): Promise<string | null>`:
  Creates a new home screen channel. Resolves with the new channel's ID.
- #### `updateChannel(channelId: string, data: UpdateChannelData): Promise<boolean>`:
  Updates an existing channel. Only fields present in `data` are changed — everything else is preserved as-is.
- #### `deleteChannel(options: DeleteChannelOptions): Promise<boolean>`:
  Deletes a channel and, per the platform's own behavior, its associated programs. Rejects with error code `DEFAULT_CHANNEL_PROTECTED` if the target is your app's default channel and `forceDeleteIfDefault` wasn't set to `true`.
- #### `getChannels(): Promise<TvChannel[]>`: Lists every channel belonging to your app.
- #### `getChannelsCount(): Promise<number>`: Returns how many channels your app currently has.

### Programs Within a Channel

- #### `addProgramToChannel(channelId: string, programData: ProgramData): Promise<string | null>`:
  Publishes or updates a program inside a specific channel (same update-in-place-by-`contentId` behavior as `addProgramToWatchNext`).
- #### `removeFromChannel(contentId: string, channelId: string): Promise<boolean>`:
  Removes one program from one specific channel.
- #### `clearChannel(channelId: string): Promise<number>`:
  Removes every program from a channel without deleting the channel itself. Resolves with the count removed.

### Cross-cutting

- #### `remove(contentId: string): Promise<number>`:
  Removes **every** instance of a given `contentId`, across all your channels *and* Watch Next — useful for a single "delete this title everywhere" action (e.g. when content is taken down entirely). Resolves with the total count removed across all locations.
- #### `clearAll(): Promise<number>`:
  Wipes everything your app owns — every channel's content and Watch Next. Does not delete the channels themselves, only their contents. Resolves with the total count removed.

---

## ⚙️️ Types and Enums

All types are exported from the package root: <br/>(`import { ProgramType, GenreType, ... } from 'react-native-tv-recommended-content'`).


| Type                   | Import Type | Description                                                                                                                                                                                                                             |
| ---------------------- | ----------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `ProgramType`          | *ENUM*      | `MOVIE`, `TV_EPISODE`, `TV_SERIES`, `CLIP`                                                                                                                                                                                              |
| `ReviewRatingStyle`    | *ENUM*      | `STARS`, `THUMBS_UP_DOWN`, `PERCENTAGE`                                                                                                                                                                                                 |
| `GenreType`            | *ENUM*      | Canonical genre tags (`COMEDY`, `DRAMA`, `SPORTS`, etc.)                                                                                                                                                                                |
| `AspectRatio`          | *ENUM*      | `RATIO_16_9`, `RATIO_1_1`, `RATIO_2_3`, `RATIO_3_2`, `RATIO_3_4`, `RATIO_4_3`, `MOVIE_POSTER`                                                                                                                                           |
| `Availability`         | *ENUM*      | `AVAILABILITY_AVAILABLE`, `AVAILABILITY_FREE`, `AVAILABILITY_FREE_WITH_ADS`, `AVAILABILITY_FREE_WITH_SUBSCRIPTION`, `AVAILABILITY_PAID_CONTENT`, `AVAILABILITY_PURCHASED`                                                               |
| `ContentRatingSystem`  | *ENUM*      | Country-specific rating system identifiers (`US_TV`, `US_MV`, `KR_TV`, `AU_TV`, etc.)                                                                                                                                                   |
| `ContentRating`        | *TYPE*      | `{ ratingSystem, rating, subRatings?, domain? }`                                                                                                                                                                                        |
| `ProgramData`          | *TYPE*      | Full metadata object accepted by`addProgramToWatchNext` / `addProgramToChannel` — see inline JSDoc in `types.ts` for every field, including TV-episode-specific (`episodeTitle`, `seasonNumber`, etc.) and clip/series-specific fields |
| `CreateChannelData`    | *TYPE*      | `{ displayName, appLinkUri, description?, appLinkText?, appLinkColor?, appLinkIconUri?, appLinkPosterArtUri?, displayOrder?, searchable? }`                                                                                             |
| `UpdateChannelData`    | *TYPE*      | `Partial<CreateChannelData>`                                                                                                                                                                                                            |
| `DeleteChannelOptions` | *TYPE*      | `{ channelId, forceDeleteIfDefault? }`                                                                                                                                                                                                  |
| `TvChannel`            | *TYPE*      | Shape returned by`getChannels()`                                                                                                                                                                                                        |

> **Important field-naming gotcha:** for `type: ProgramType.TV_EPISODE`, when adding a program to a channel or the "Watch Next" row, the `title` field must hold the **series name** (e.g. "Breaking Bad"), not the episode's own name. The episode's own name goes in `episodeTitle`. This mirrors Android's own underlying data model and is easy to get backwards; getting it wrong doesn't throw an error, it just mislabels the card on screen.

---

## 📜 Watch Next Quality Guidelines

These aren't rules this library enforces for you — they're Google's own published guidelines for what belongs in Watch Next, worth building your calling logic around:

- Only add movies and TV episodes — not clips, trailers, or short-form content.
- **"Started"** thresholds: a movie counts as started after 3% or 2 minutes watched (whichever comes first); a TV episode after 2 minutes.
- **"Finished"** means the end credits have started — remove the entry at that point rather than leaving it stale (an approximation like "under 3 minutes remaining" works if you don't have real credit-detection).
- Keep at most **one Watch Next entry per TV series** at a time.
- When an episode finishes, add the *next* episode in the series rather than just removing the finished one.

---

## 🔧 Troubleshooting

- **`"Unsupported class file major version NN"` during a Gradle build.**
  This is a JDK-vs-Gradle version mismatch, unrelated to this package — your system's active JDK is newer than the project's Gradle version supports. Point Gradle at an older JDK (17 is the safe default) via `org.gradle.java.home` in `android/gradle.properties`, rather than changing your system-wide `JAVA_HOME`.
  <br /><br />
- **`Cannot add extension with name 'kotlin'` / a `ClassCastException` involving `BaseExtension`.**
Both are Android Gradle Plugin 9.0+ migration issues (its new built-in Kotlin support and new DSL implementation), not issues with this package. Add to your app's `android/gradle.properties`:

  ```
  android.builtInKotlin=false
  android.newDsl=false
  ```

  Note these are temporary opt-outs Google has said will be removed in a future AGP major version — treat as a stopgap, not a permanent fix.
  <br /><br />
- **`My Watch Next / channel entries aren't appearing on the home screen at all.`**
  These can be one of two independent causes:

  1. You're missing a required field — this package sets `lastEngagementTimeUtcMillis` for you automatically, but if entries still don't show, verify `posterUrl` is a real, reachable URL.
  2. **On genuine Google TV devices**, the "Continue Watching" row specifically requires prior certification approval from Google — a server-side gate, separate from whether your code is correct. Plain AOSP Android TV (non-Google-TV-branded, leanback launcher) doesn't have this restriction. Custom channels you create yourself are not affected by this — only the system Watch Next row is.
     **A newly created channel isn't showing up.**
     Every channel after your app's first one requires the user to explicitly approve it — this package triggers that system permission prompt automatically inside `createChannel`, but the user still has to accept it. Only the very first channel your app ever creates is exempt from this.

---

## 🧪 Testing

1. Clone the repo and run `yarn` at the root.
2. `yarn example android` runs the included example app.
3. Test against a real Android TV device or the Android TV emulator where possible — some behavior (notably the Google TV certification gate mentioned above) can only be observed on genuine Google TV hardware/launcher, not a plain AOSP Android TV emulator image.
4. There's no meaningful unit-test surface on the JS side by design — this package is a thin, deliberately logic-free pass-through to native code; the real behavior to verify is always on-device.

---

## 🤝 Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) and please follow the [Code of Conduct](./CODE_OF_CONDUCT.md).

---

## 🔒 License

Apache-2.0 © MadeByRaymond ([Daniel Obiekwe](https://github.com/MadeByRaymond))

---

## ❤️ Support

If this package saved you from writing raw `androidx.tvprovider` Kotlin yourself, consider buying me a coffee:

[![Buy Me a Smoothie](https://img.buymeacoffee.com/button-api/?text=Buy%20Me%20a%20Smoothie&emoji=🍹&slug=MadeByRaymond&button_colour=FFDD00&font_colour=000000&font_family=Comic&outline_colour=000000&coffee_colour=ffffff)](https://www.buymeacoffee.com/MadeByRaymond)

Issues and feature requests: [GitHub Issues](https://github.com/MadeByRaymond/react-native-tv-recommended-content/issues)
