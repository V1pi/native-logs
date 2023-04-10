import { NativeModules } from "react-native";

const { RNNativeLogs } = NativeModules;

export const NativeLogs = {
  currentLogsIndex: {} as { [key: string]: number },

  /** Redirects the native logs from the console to a given file.
    * @param {string} identifier - The name of the file to write the logs to.
    * @param {string} logLevel - The log level to redirect. Can be one of: "D" for debug, "I" for info, "W" for warn, "E" for error, "F" for fatal.
    * @returns {Promise<void>} - A promise that resolves when the logs are redirected.
  */
  async redirectLogs(identifier: string, logLevel: string): Promise<void> {
    this.currentLogsIndex[identifier] = 0;
    await RNNativeLogs.setUpRedirectLogs(identifier, logLevel);
  },

  /** Gets all new logs since the last call to this function.
    * @param {string} identifier - The name of the file to read the logs to.
    * @param {string[]} tags - An array of tags to exclude from the logs. Only logs that do not match these tags will be redirected.
    * @returns {Promise<string[] | null>} - A promise that resolves with the new logs or null if there are no logs.
  */
  async getNewLogs(identifier: string, tags: string[]): Promise<string[] | null> {
    const allNativeLogs = await RNNativeLogs.readOutputLogs(identifier, tags);
    const arrayWithNewlogs = allNativeLogs.slice(this.currentLogsIndex[identifier] || 0);
    this.currentLogsIndex[identifier] = Math.max(0, allNativeLogs.length);
    if (arrayWithNewlogs.length < 1) {
      return null;
    }
    return arrayWithNewlogs;
  },

  /** Gets all logs from the given file.
    * @param {string} identifier - The name of the file to read the logs to.
    * @param {string[]} tags - An array of tags to exclude from the logs. Only logs that do not match these tags will be redirected.
    * @returns {Promise<string[] | null>} - A promise that resolves with the logs or null if there are no logs.
  */
  async getLogs(identifier: string, tags: string[]): Promise<string[] | null> {
    const allNativeLogs = await RNNativeLogs.readOutputLogs(identifier, tags);
    if (allNativeLogs.length < 1) {
      return null;
    }
    return allNativeLogs;
  },
};
