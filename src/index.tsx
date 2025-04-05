import { NitroModules } from 'react-native-nitro-modules';
import type { AwesomeLibraryBob } from './AwesomeLibraryBob.nitro';

const AwesomeLibraryBobHybridObject =
  NitroModules.createHybridObject<AwesomeLibraryBob>('AwesomeLibraryBob');

export function multiply(a: number, b: number): number {
  return AwesomeLibraryBobHybridObject.multiply(a, b);
}

export function sum(a: number, b: number): number {
  return AwesomeLibraryBobHybridObject.sum(a, b);
}
