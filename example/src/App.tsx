import { Text, View, StyleSheet, Pressable, ScrollView } from 'react-native';
import {
  enableBluetooth,
  isBluetoothClassicFeatureAvailable,
  isBluetoothOn,
  getPairedDevices,
  startScan,
  stopScan,
  getScannedDevices,
} from 'react-native-awesome-library-bob';
import {
  handleBluetoothConnectPermission,
  handleBluetoothScanPermission,
} from './utils/permissions';
import { useState } from 'react';
import type { TBluetoothDevice } from '../../src/AwesomeLibraryBob.nitro';

const result3 = isBluetoothClassicFeatureAvailable();
const result4 = isBluetoothOn();

export default function App() {
  const [pairedDevices, setPairedDevices] = useState<TBluetoothDevice[]>([]);

  console.log(getScannedDevices());

  const handleEnablingBluetooth = async () => {
    handleBluetoothConnectPermission(async () => {
      try {
        await enableBluetooth();
        console.log('Bluetooth enabled successfully');
      } catch (error) {
        console.log('Error enabling Bluetooth:', error);
      }
    });
  };

  const handleGetPairedDevices = () => {
    handleBluetoothConnectPermission(() => {
      const pairedDevicess = getPairedDevices();
      setPairedDevices(() => pairedDevicess);
      console.log('Paired devices:', JSON.stringify(pairedDevicess, null, 2));
      // console.log('heyyyyyyyyyyyyyyyyyy');
    });
  };

  const handleStartScanDevices = () => {
    handleBluetoothConnectPermission(() => {
      handleBluetoothScanPermission(() => {
        startScan();
      });
    });
  };

  return (
    <View style={styles.container}>
      <Text
        style={styles.text}
      >{`Bluetooth is ${result3 ? 'supported' : 'not supported'} on this device.`}</Text>
      <Text style={styles.text}>
        {`Bluetooth is ${result4 ? 'On' : 'Off'}`}
      </Text>
      <Pressable style={styles.button} onPress={handleEnablingBluetooth}>
        <Text style={styles.text}>Enable Bluetooth</Text>
      </Pressable>
      <Pressable style={styles.button} onPress={handleGetPairedDevices}>
        <Text style={styles.text}>Get Paired Devices</Text>
      </Pressable>

      {/**list of paired devices */}
      {pairedDevices.length > 0 && (
        <View style={styles.flexWrapper}>
          <ScrollView style={styles.flexWrapper}>
            <Text style={[styles.text, styles.title]}>Paired Devices:</Text>
            {pairedDevices.map((device, index) => (
              <View key={index} style={styles.deviceWrapper}>
                <Text style={styles.text}>{`Device Name: ${device.name}`}</Text>
                <Text
                  style={styles.text}
                >{`Device MacAddress: ${device.macAddress}`}</Text>
              </View>
            ))}
          </ScrollView>
        </View>
      )}

      {/**list of scanned devices */}
      {getScannedDevices()?.length > 0 && (
        <View style={styles.flexWrapper}>
          <ScrollView style={styles.flexWrapper}>
            <Text style={[styles.text, styles.title]}>Scanned Devices:</Text>
            {getScannedDevices().map((device, index) => (
              <View key={index} style={styles.deviceWrapper}>
                <Text style={styles.text}>{`Device Name: ${device.name}`}</Text>
                <Text
                  style={styles.text}
                >{`Device MacAddress: ${device.macAddress}`}</Text>
              </View>
            ))}
          </ScrollView>
        </View>
      )}

      <View style={styles.rowWrapper}>
        {/**start scan */}
        <Pressable style={styles.button} onPress={handleStartScanDevices}>
          <Text style={styles.text}>Scan Devices</Text>
        </Pressable>

        <Pressable
          style={styles.button}
          onPress={() => {
            stopScan();
          }}
        >
          <Text style={styles.text}>Stop Scan</Text>
        </Pressable>

        <Pressable
          style={styles.button}
          onPress={() => {
            console.log(JSON.stringify(getScannedDevices(), null, 2));
          }}
        >
          <Text style={styles.text}>Get Scanned devices</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontSize: 16,
    color: '#fff',
  },
  button: {
    backgroundColor: '#007AFF',
    padding: 10,
    borderRadius: 5,
    marginTop: 20,
  },
  flexWrapper: {
    flex: 1,
  },
  title: {
    marginVertical: 12,
  },
  deviceWrapper: {
    margin: 14,
  },
  rowWrapper: {
    flexDirection: 'row',
  },
});
