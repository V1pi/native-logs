import { NativeModules, Platform } from "react-native";

const { RNNativeLogs } = NativeModules;

export const NativeLogs = {
  /** Redirects the native logs from the console to a given file.
    * @param {string[] | undefined} tags - [ANDROID only] An array of tags and log level to pass to logcat. It should be in the format TAG:LOG_LEVEL, e.g ReactNativeJS:V. You can see more in logcat docs
    * @returns {Promise<void>} - A promise that resolves when the logs are redirected.
  */
  async redirectLogs(tags?: string[]): Promise<void> {
    if (Platform.OS === "ios" || !tags) {
      await RNNativeLogs.setUpRedirectLogs();
    } else {
      await RNNativeLogs.setUpRedirectLogs(tags);
    }
  },

  /** Gets all new logs since the last call to this function.
    * @returns {Promise<string[] | null>} - A promise that resolves with the new logs or null if there are no logs.
  */
  async getNewLogs(): Promise<string[] | null> {
    const allNativeLogs = await RNNativeLogs.readOutputLogs();

    if (allNativeLogs.length < 1) {
      return null;
    }

    return allNativeLogs;
  },


  /** Get file path of the log file.
    * @returns {Promise<string>} - A promise that resolves with the path of the log file.
  */
  async getFilePath(): Promise<string> {
    return await RNNativeLogs.getFilePath();
  }
};
