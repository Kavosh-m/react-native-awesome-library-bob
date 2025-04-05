import type { HybridObject } from 'react-native-nitro-modules';

export interface AwesomeLibraryBob
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  multiply(a: number, b: number): number;
  sum(a: number, b: number): number;
}
