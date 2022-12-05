import { NativeModules } from "react-native";


export const { RNNativeLogs } = NativeModules;


export const NativeLogs = {
  getLogs: async (identifier: string): Promise<string[] | null> => {
    console.log("getLogs");
    return RNNativeLogs.readOutputLogs(identifier);
  },
  redirectLogs: async (identifier: string): Promise<void> => {
        console.log("redirectLogs");
     return RNNativeLogs.setUpRedirectLogs(identifier);
  },
};
