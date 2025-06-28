# Project Brief: voboost-config-demo

## 1. Project Overview

`voboost-config-demo` is a **demonstration Android application** that showcases the functionality of the `voboost-config` library. This is a separate project that exists at the same directory level as the main library, not as a submodule.

## 2. Main Goal

The primary goal of this demo application is to provide a working example of how to integrate and use the `voboost-config` library in a real Android application. It demonstrates all key features including configuration loading, saving, and real-time change detection.

## 3. Key Demo Features

- **Configuration Display** - Shows current configuration values in a user-friendly interface
- **Real-time Updates** - Demonstrates file watching and change detection
- **File Integration** - Shows how to work with Android file system
- **Error Handling** - Demonstrates proper error handling patterns

## 4. Project Structure

```
voboost-config-demo/
├── src/main/
│   ├── java/ru/voboost/config/demo/
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── layout/
│   │   └── values/
│   └── assets/
│       └── config.yaml
├── build.gradle.kts
└── memory-bank/
```

## 5. Relationship to Main Library

- **Dependency**: Demo app depends on `voboost-config` library
- **Location**: Sibling project at same directory level
- **Purpose**: Demonstrates library usage patterns
- **Testing**: Serves as integration testing platform

## 6. Language Policy

- All code and documentation in English

