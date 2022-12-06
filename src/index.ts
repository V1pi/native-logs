import { NativeModules } from "react-native";

export const { RNNativeLogs } = NativeModules;

const logsExample = [
  "2022-12-06 09:54:21.095736-0300 VouchKeyManuApp[60763:305544] [CoreBluetooth] API MISUSE: <CBPeripheralManager: 0x600000078240> can only accept this command while in the powered on state",
  "2022-12-06 09:54:21.096097-0300 VouchKeyManuApp[60763:305544] [CoreBluetooth] API MISUSE: <CBPeripheralManager: 0x600000078240> can only accept this command while in the powered on state",
  "2022-12-06 09:54:21.098020-0300 VouchKeyManuApp[60763:304760] Connecting to device using GATT Server transport",
  "2022-12-06 09:54:21.098693-0300 VouchKeyManuApp[60763:305544] [CoreBluetooth] XPC connection invalid ",
  "2022-12-06 09:54:21.098914-0300 VouchKeyManuApp[60763:305544] Bad state can't start advertising",
];

let currentLogIndex: number;

export const NativeLogs = {
  getLogs: async (identifier: string): Promise<string[] | null> => {
    const allNativeLogs = await RNNativeLogs.readOutputLogs(identifier);
    const arrayWithNewlogs = logsExample.slice(currentLogIndex);
    currentLogIndex = logsExample.length - 1;
    return arrayWithNewlogs;
  },
  redirectLogs: async (identifier: string): Promise<void> => {
    return RNNativeLogs.setUpRedirectLogs(identifier);
    currentLogIndex = 0;
  },
};
