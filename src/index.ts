import { NativeModules } from "react-native";

const { RNNativeLogs } = NativeModules;

export const NativeLogs = {
  currentLogsIndex: {} as { [key: string]: number },
  
  /** Redirects the native logs from the console to a given file.
    * @param {string} identifier - The name of the file to write the logs to.
    * @returns {Promise<void>} - A promise that resolves when the logs are redirected.
  */
  async redirectLogs(identifier: string): Promise<void> {
    this.currentLogsIndex[identifier] = 0;
    await RNNativeLogs.setUpRedirectLogs(identifier);
  },

  /** Gets all new logs since the last call to this function.
    * @param {string} identifier - The name of the file to read the logs to.
    * @returns {Promise<string[] | null>} - A promise that resolves with the new logs or null if there are no logs.
  */
  async getNewLogs(identifier: string): Promise<string[] | null> {
    const allNativeLogs = await RNNativeLogs.readOutputLogs(identifier);
    const arrayWithNewlogs = allNativeLogs.slice(this.currentLogsIndex[identifier] || 0);
    this.currentLogsIndex[identifier] = Math.max(0, allNativeLogs.length);
    if (arrayWithNewlogs.length < 0) {
      return null;
    }
    return arrayWithNewlogs;
  },
  
  /** Gets all logs from the given file.
    * @param {string} identifier - The name of the file to read the logs to.
    * @returns {Promise<string[] | null>} - A promise that resolves with the logs or null if there are no logs.
  */
  async getLogs(identifier: string): Promise<string[] | null> {
    const allNativeLogs = await RNNativeLogs.readOutputLogs(identifier);
    if (allNativeLogs.length < 0) {
      return null;
    }
    return allNativeLogs;
  },
};
