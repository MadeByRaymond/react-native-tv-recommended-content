import { Text, View, StyleSheet } from 'react-native';
import TVRecommendedContent from 'react-native-tv-recommended-content';

const channels = TVRecommendedContent.getChannels();

export default function App() {
  return (
    <View style={styles.container}>
      <Text>Result: {channels.then(c => (c.length))}</Text>
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
