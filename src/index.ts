import { NativeModules, Platform } from "react-native";

const { RNNativeLogs } = NativeModules;

type LogLevel = "D" | "I" | "W" | "E" | "F";

export const NativeLogs = {
  currentLogsIndex: {} as { [key: string]: number },

  /** Redirects the native logs from the console to a given file.
    * @param {string} identifier - The name of the file to write the logs to.
    * @param {LogLevel | undefined} logLevel - [ANDROID only] The log level to redirect. Can be one of: "D" for debug, "I" for info, "W" for warn, "E" for error, "F" for fatal.
    * @returns {Promise<void>} - A promise that resolves when the logs are redirected.
  */
  async redirectLogs(identifier: string, logLevel?: LogLevel): Promise<void> {
    this.currentLogsIndex[identifier] = 0;
    if (Platform.OS === "ios" || !logLevel) {
      await RNNativeLogs.setUpRedirectLogs(identifier);
    } else {
      await RNNativeLogs.setUpRedirectLogs(identifier, logLevel);
    }
  },

  /** Gets all new logs since the last call to this function.
    * @param {string} identifier - The name of the file to read the logs to.
    * @param {string[] | undefined} tags - [ANDROID only] An array of tags to exclude from the logs. Only logs that do not match these tags will be redirected.
    * @returns {Promise<string[] | null>} - A promise that resolves with the new logs or null if there are no logs.
  */
  async getNewLogs(identifier: string, tags?: string[]): Promise<string[] | null> {
    const allNativeLogs = Platform.OS === "ios" || !tags ?
      await RNNativeLogs.readOutputLogs(identifier) :
      await RNNativeLogs.readOutputLogs(identifier, tags);

    const arrayWithNewlogs = allNativeLogs.slice(this.currentLogsIndex[identifier] || 0);
    this.currentLogsIndex[identifier] = Math.max(0, allNativeLogs.length);
    if (arrayWithNewlogs.length < 1) {
      return null;
    }
    return arrayWithNewlogs;
  },

  /** Gets all logs from the given file.
    * @param {string} identifier - The name of the file to read the logs to.
    * @param {string[] | undefined} tags - [ANDROID only] An array of tags to exclude from the logs. Only logs that do not match these tags will be redirected.
    * @returns {Promise<string[] | null>} - A promise that resolves with the logs or null if there are no logs.
  */
  async getLogs(identifier: string, tags?: string[]): Promise<string[] | null> {
    const allNativeLogs = Platform.OS === "ios" || !tags ?
      await RNNativeLogs.readOutputLogs(identifier) :
      await RNNativeLogs.readOutputLogs(identifier, tags);
    if (allNativeLogs.length < 1) {
      return null;
    }
    return allNativeLogs;
  },
};
