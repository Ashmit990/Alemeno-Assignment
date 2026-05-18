/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useEffect, useState, useRef } from 'react';
import { StyleSheet, View, Text, TouchableOpacity, Image, ScrollView, Dimensions, StatusBar } from 'react-native';
import { Camera, useCameraDevices } from 'react-native-vision-camera';
import 'react-native-reanimated';

const { width: SCREEN_WIDTH, height: SCREEN_HEIGHT } = Dimensions.get('window');

function App() {
  const devices = useCameraDevices();
  const device = devices.back;
  const [hasPermission, setHasPermission] = useState(false);
  const [processedMarkers, setProcessedMarkers] = useState<string[]>([]);
  const [isScanning, setIsScanning] = useState(true);

  useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
    })();
  }, []);

  const onMarkerDetected = (imagePath: string) => {
    if (processedMarkers.length < 20) {
      setProcessedMarkers((prev) => [...prev, imagePath]);
    } else {
      setIsScanning(false);
    }
  };

  if (device == null || !hasPermission) {
    return (
      <View style={styles.container}>
        <Text style={styles.text}>Requesting Camera Permission...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <StatusBar hidden />
      {isScanning ? (
        <>
          <Camera
            style={StyleSheet.absoluteFill}
            device={device}
            isActive={true}
          />
          <View style={styles.overlay}>
            <View style={styles.scanner} />
            <Text style={styles.text}>Scanning for Marker... ({processedMarkers.length}/20)</Text>
          </View>
        </>
      ) : (
        <ScrollView contentContainerStyle={styles.resultContainer}>
          <Text style={styles.title}>Processed Markers</Text>
          <View style={styles.grid}>
            {processedMarkers.map((uri, index) => (
              <Image key={index} source={{ uri }} style={styles.markerImage} />
            ))}
          </View>
          <TouchableOpacity style={styles.button} onPress={() => { setProcessedMarkers([]); setIsScanning(true); }}>
            <Text style={styles.buttonText}>Rescan</Text>
          </TouchableOpacity>
        </ScrollView>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
    justifyContent: 'center',
    alignItems: 'center',
  },
  overlay: {
    ...StyleSheet.absoluteFillObject,
    justifyContent: 'center',
    alignItems: 'center',
  },
  scanner: {
    width: 250,
    height: 250,
    borderWidth: 2,
    borderColor: '#00FF00',
    backgroundColor: 'rgba(0, 255, 0, 0.1)',
  },
  text: {
    color: '#fff',
    marginTop: 20,
    fontSize: 18,
    fontWeight: 'bold',
  },
  resultContainer: {
    padding: 20,
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    color: '#fff',
    marginBottom: 20,
    marginTop: 40,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
  },
  markerImage: {
    width: 100,
    height: 100,
    margin: 5,
    borderRadius: 5,
    backgroundColor: '#333',
  },
  button: {
    marginTop: 30,
    backgroundColor: '#007AFF',
    paddingHorizontal: 30,
    paddingVertical: 15,
    borderRadius: 25,
    marginBottom: 40,
  },
  buttonText: {
    color: '#fff',
    fontSize: 18,
  },
});

export default App;
