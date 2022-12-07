import { NativeModules } from "react-native";

export const { RNNativeLogs } = NativeModules;

export const NativeLogs = {
  currentLogIndex: 0,
  async getLogs(identifier: string) {
    const allNativeLogs = await RNNativeLogs.readOutputLogs(identifier);
    const arrayWithNewlogs = allNativeLogs.slice(this.currentLogIndex);
    this.currentLogIndex = allNativeLogs.length - 1;
    if (arrayWithNewlogs.length < 0) {
      return null;
    }
    return arrayWithNewlogs;
  },
  async redirectLogs(identifier: string) {
    this.currentLogIndex = 0;
    return RNNativeLogs.setUpRedirectLogs(identifier);
  },
};
