
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNRnMediaLibrarySpec.h"

@interface RnMediaLibrary : NSObject <NativeRnMediaLibrarySpec>
#else
#import <React/RCTBridgeModule.h>

@interface RnMediaLibrary : NSObject <RCTBridgeModule>
#endif

@end
