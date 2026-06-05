# PocketMind AI - Phase 5: Model Training Script
# Run this on your Laptop using: python train_model.py

import tensorflow as tf
from tensorflow import keras
import numpy as np

def generate_and_train():
    print("PocketMind AI Training Tool")

    # 1. Define the model architecture
    model = keras.Sequential([
        keras.layers.Input(shape=(10,)), # Example: 10 features (time, battery, sensors, etc.)
        keras.layers.Dense(64, activation='relu'),
        keras.layers.Dense(32, activation='relu'),
        keras.layers.Dense(6, activation='softmax') # 6 Contexts: HOME, WORK, STUDY, GYM, SLEEP, COMMUTE
    ])

    model.compile(optimizer='adam',
                  loss='sparse_categorical_crossentropy',
                  metrics=['accuracy'])

    print("Model Architecture Created.")

    # 2. Convert to TFLite
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()

    # 3. Save the model
    with open('model.tflite', 'wb') as f:
        f.write(tflite_model)

    print("Success! 'model.tflite' is ready.")
    print("Now copy this file to your Android project: app/src/main/assets/")

if __name__ == "__main__":
    generate_and_train()
