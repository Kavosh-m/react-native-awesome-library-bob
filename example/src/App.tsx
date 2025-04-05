import { Text, View, StyleSheet } from 'react-native';
import {
  isBluetoothClassicFeatureAvailable,
  isBluetoothOn,
  multiply,
  sum,
} from 'react-native-awesome-library-bob';

const result = multiply(3, 7);
const result2 = sum(3, 7);
const result3 = isBluetoothClassicFeatureAvailable();
const result4 = isBluetoothOn();

export default function App() {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>Result1: {result}</Text>
      <Text style={styles.text}>Result2: {result2}</Text>
      <Text
        style={styles.text}
      >{`Bluetooth is ${result3 ? 'supported' : 'not supported'} on this device.`}</Text>
      <Text style={styles.text}>
        {`Bluetooth is ${result4 ? 'On' : 'Off'}`}
      </Text>
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
});
