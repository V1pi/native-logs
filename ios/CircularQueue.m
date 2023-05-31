#import "CircularQueue.h"

@interface CircularQueue()

@property (nonatomic, assign) NSUInteger head;
@property (nonatomic, assign) NSUInteger tail;
@property (nonatomic, assign) NSUInteger size;
@property (nonatomic, strong) NSMutableArray *array;

- (instancetype)initWithSize:(NSUInteger)size;
- (void)enqueue:(id)object;
- (id)dequeue;

@end

@implementation CircularQueue

- (instancetype)initWithSize:(NSUInteger)size {
    self = [super init];
    if (self) {
        self.head = 0;
        self.tail = 0;
        self.size = size;
        self.array = [NSMutableArray arrayWithCapacity:size];
    }
    return self;
}

- (void)insertAtTop:(id)object {
    if ([self isFull]) {
        return;
    }
    if([self isEmpty]) {
        [self enqueue:object];
        return;
    }
    
    if(self.head == 0) {
        [self.array insertObject:object atIndex:0];
        self.tail = (self.tail + 1) % self.size;
        return;
    }
    
    self.head = (self.head - 1) % self.size;
    [self.array replaceObjectAtIndex:self.head withObject:object];
}

- (void)enqueue:(id)object {
    if ([self isFull]) {
        self.head = (self.head + 1) % self.size;
    }
    if(self.array.count >= self.tail + 1) {
        [self.array replaceObjectAtIndex:self.tail withObject:object];
    } else {
        [self.array addObject:object];
    }
    
    self.tail = (self.tail + 1) % self.size;
}

- (id)dequeue {
    if ([self isEmpty]) {
        return NULL;
    }

    id object = [self.array objectAtIndex:self.head];
    self.head = (self.head + 1) % self.size;
    return object;
}

- (BOOL)isEmpty {
    return self.head == self.tail;
}

- (BOOL)isFull {
    return (self.tail + 1) % self.size == self.head;
}

@end
