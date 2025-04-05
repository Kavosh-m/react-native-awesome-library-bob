import { Text, View, StyleSheet } from 'react-native';
import { multiply, sum } from 'react-native-awesome-library-bob';

const result = multiply(3, 7);
const result2 = sum(3, 7);

export default function App() {
  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Text>Result: {result2}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
