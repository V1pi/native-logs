import { NativeModules } from "react-native";

export const { RNNativeLogs } = NativeModules;

export const NativeLogs = {
  getLogs: async (identifier: string): Promise<string[] | null> => {
    return RNNativeLogs.readOutputLogs(identifier);
  },
  redirectLogs: async (identifier: string): Promise<void> => {
    return RNNativeLogs.setUpRedirectLogs(identifier);
  },
};
