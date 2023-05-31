#import "RNNativeLogs.h"
#import <React/RCTUtils.h>
#import "CircularQueue.h"
#import <Foundation/Foundation.h>

#define BUFFER_SIZE 512
#define CHUNK_SIZE 256
#define MAX_LINES 64

@interface RNNativeLogs ()

@property (atomic) NSFileHandle* fileHandle;
@property (atomic) CircularQueue* queue;

@end

@implementation RNNativeLogs

RCT_EXPORT_MODULE();

NSString* _identifier = NULL;

-(NSFileHandle*)getOutputLogsHandler {
    return [NSFileHandle fileHandleForReadingAtPath:[self getFilePath]];
}

- (NSString*)identifier {
    if(_identifier == NULL) {
        _identifier = [NSString stringWithFormat:@"NativeLogs-%@.txt", [[NSProcessInfo processInfo] globallyUniqueString]];
    }
    return _identifier;
}

-(NSString*)getFilePath {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths firstObject];
    return [documentsDirectory stringByAppendingPathComponent:self.identifier];
}

- (void)fileDidChange:(NSNotification *)notification {
    @synchronized (self.fileHandle) {
        NSData *data = NULL;
        while ((data = [self.fileHandle readDataOfLength:CHUNK_SIZE]) != NULL && data.length > 0) {
            NSString *text = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
            @synchronized (self.queue) {
                [self.queue enqueue:text];
            }
        }
        [self.fileHandle waitForDataInBackgroundAndNotify];
    }
}

RCT_EXPORT_METHOD(getFilePath:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve([self getFilePath]);
}

RCT_EXPORT_METHOD(setUpRedirectLogs:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    if(self.queue != NULL && self.fileHandle != NULL) {
        resolve(NULL);
        return;
    }
    
    @synchronized (self.queue) {
        self.queue = [[CircularQueue alloc] initWithSize:BUFFER_SIZE];
    }
    
    NSString *logPath = [self getFilePath];
    freopen([logPath fileSystemRepresentation],"w",stderr);
    
    dispatch_async(dispatch_get_main_queue(), ^{
        @synchronized (self.fileHandle) {
            self.fileHandle = [self getOutputLogsHandler];
            if(self.fileHandle != NULL) {
                unsigned long long endOfFile;
                NSError* error;
                [self.fileHandle seekToOffset:0 error:&error];
                
                if(error) {
                    reject(@"500", @"Failed to read the file", NULL);
                }
                
                [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(fileDidChange:) name:NSFileHandleDataAvailableNotification object:self.fileHandle];
                [self.fileHandle waitForDataInBackgroundAndNotify];
                resolve(NULL);
            }
        }
    });

}


RCT_EXPORT_METHOD(readOutputLogs:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSMutableArray* result = [[NSMutableArray alloc] initWithCapacity:MAX_LINES];
    int count = 0;
    
    @synchronized (self.queue) {
        while (count < MAX_LINES && ![self.queue isEmpty]) {
            NSString* value = [self.queue dequeue];
            NSArray<NSString*>* allLinedStrings = [value componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]];
            while([allLinedStrings count] < 2 && ![self.queue isEmpty]) {
                value = [value stringByAppendingString:[self.queue dequeue]];
                allLinedStrings = [value componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]];
            }
            
            if([allLinedStrings count] >= 2) {
                [result addObject:[allLinedStrings objectAtIndex:0]];
                for(int i = 1; i < allLinedStrings.count; i++) {
                    [self.queue insertAtTop:[allLinedStrings objectAtIndex:i]];
                }
            } else {
                [self.queue insertAtTop:[allLinedStrings objectAtIndex:0]];
                break;
            }
            
            count++;
        }
    }
    
    if(!result) {
        resolve(@[]);
        return;
    }
    resolve(result);
}

@end
