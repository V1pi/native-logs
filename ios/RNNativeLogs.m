#import "RNNativeLogs.h"
#import <React/RCTUtils.h>

@interface RNNativeLogs ()

@end

@implementation RNNativeLogs

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(setUpRedirectLogs:fileIdentifier resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,
                                                         NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *logPath = [documentsDirectory stringByAppendingPathComponent:fileIdentifier];
    freopen([logPath fileSystemRepresentation],"w",stderr);

    resolve(NULL);
}


RCT_EXPORT_METHOD(readOutputLogs:fileIdentifier resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths firstObject];
    NSString *logPath = [documentsDirectory stringByAppendingPathComponent:fileIdentifier];
    
    NSString* fileContents =
    [NSString stringWithContentsOfFile:logPath
                              encoding:NSUTF8StringEncoding error:nil];
    
    NSArray* allLinedStrings =
    [fileContents componentsSeparatedByCharactersInSet:
     [NSCharacterSet newlineCharacterSet]];
    
    if(allLinedStrings.count < 1) {
        resolve(NULL);

        return;
    }
    resolve(allLinedStrings);
}

@end
