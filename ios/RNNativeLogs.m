#import "RNNativeLogs.h"
#import <React/RCTUtils.h>

@interface RNNativeLogs ()

@property NSUInteger currentLogIndex;

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
    
    self.currentLogIndex = 0;
    
    resolve(NULL);
    
}


RCT_EXPORT_METHOD(readOutputLogs:fileIdentifier resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths firstObject];
    NSString *logPath = [documentsDirectory stringByAppendingPathComponent:fileIdentifier];
    
    // read everything from text
    NSString* fileContents =
          [NSString stringWithContentsOfFile:logPath
           encoding:NSUTF8StringEncoding error:nil];
  
    // first, separate by new line
    NSArray* allLinedStrings =
          [fileContents componentsSeparatedByCharactersInSet:
          [NSCharacterSet newlineCharacterSet]];
    
   NSInteger startIndex = self.currentLogIndex;
   self.currentLogIndex = allLinedStrings.count - 1;
     
   NSArray* allnNewlogsArray  = [allLinedStrings subarrayWithRange:NSMakeRange(startIndex, self.currentLogIndex - startIndex)];
    
    // Return false when there is no new logs
    if(allnNewlogsArray.count < 1) {
        resolve(NULL);
        return;
    }
    resolve(allnNewlogsArray);
}


@end
