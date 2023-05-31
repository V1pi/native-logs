#import <Foundation/Foundation.h>

@interface CircularQueue : NSObject

- (instancetype)initWithSize:(NSUInteger)size;
- (void)insertAtTop:(id)object;
- (void)enqueue:(id)object;
- (id)dequeue;
- (BOOL)isEmpty;
- (BOOL)isFull;

@end
