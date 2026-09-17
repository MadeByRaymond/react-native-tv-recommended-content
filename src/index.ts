import { NativeModules, Platform } from 'react-native';
import type {
  ITvRecommendedContentModule,
  ProgramData,
  CreateChannelData,
  UpdateChannelData,
  DeleteChannelOptions,
  TvChannel,
} from './types';

export * from './types';

const { TvRecommendedContentModule } = NativeModules;

// Safety check to ensure it safely resolves/fails if called on iOS/AppleTV,
// or if the native module somehow isn't linked on Android TV itself.
const isAndroidTV =
  Platform.isTV &&
  Platform.OS === 'android' &&
  TvRecommendedContentModule != null;

const TVRecommendedContent: ITvRecommendedContentModule = {
  // --- Watch Next ---
  addProgramToWatchNext: (data: ProgramData) =>
    isAndroidTV ? TvRecommendedContentModule.addProgramToWatchNext(data) : Promise.resolve(null),
  removeWatchNext: (contentId: string) =>
    isAndroidTV ? TvRecommendedContentModule.removeWatchNext(contentId) : Promise.resolve(false),
  clearWatchNext: () =>
    isAndroidTV ? TvRecommendedContentModule.clearWatchNext() : Promise.resolve(0),

  // --- Channels ---
  createChannel: (data: CreateChannelData) =>
    isAndroidTV ? TvRecommendedContentModule.createChannel(data) : Promise.resolve(null),
  updateChannel: (channelId: string, data: UpdateChannelData) =>
    isAndroidTV ? TvRecommendedContentModule.updateChannel(channelId, data) : Promise.resolve(false),
  deleteChannel: (options: DeleteChannelOptions) =>
    isAndroidTV ? TvRecommendedContentModule.deleteChannel(options) : Promise.resolve(false),
  getChannels: () =>
    isAndroidTV ? TvRecommendedContentModule.getChannels() : Promise.resolve([] as TvChannel[]),
  getChannelsCount: () =>
    isAndroidTV ? TvRecommendedContentModule.getChannelsCount() : Promise.resolve(0),

  // --- Programs within a channel ---
  addProgramToChannel: (channelId: string, programData: ProgramData) =>
    isAndroidTV ? TvRecommendedContentModule.addProgramToChannel(channelId, programData) : Promise.resolve(null),
  removeFromChannel: (contentId: string, channelId: string) =>
    isAndroidTV ? TvRecommendedContentModule.removeFromChannel(contentId, channelId) : Promise.resolve(false),
  clearChannel: (channelId: string) =>
    isAndroidTV ? TvRecommendedContentModule.clearChannel(channelId) : Promise.resolve(0),

  // --- Cross-cutting ---
  remove: (contentId: string) =>
    isAndroidTV ? TvRecommendedContentModule.remove(contentId) : Promise.resolve(0),
  clearAll: () =>
    isAndroidTV ? TvRecommendedContentModule.clearAll() : Promise.resolve(0),
};

export default TVRecommendedContent;

export const {
  addProgramToWatchNext,
  removeWatchNext,
  clearWatchNext,
  createChannel,
  updateChannel,
  deleteChannel,
  getChannels,
  getChannelsCount,
  addProgramToChannel,
  removeFromChannel,
  clearChannel,
  remove,
  clearAll,
} = TVRecommendedContent;
