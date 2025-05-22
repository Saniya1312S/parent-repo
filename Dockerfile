# Use an official JDK with Android SDK
FROM openjdk:11-jdk-slim

# Install required dependencies
RUN apt-get update && apt-get install -y \
    wget unzip git curl build-essential lib32stdc++6 lib32z1 \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV ANDROID_SDK_ROOT /sdk
ENV PATH ${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/tools/bin:${ANDROID_SDK_ROOT}/platform-tools

# Download Android command line tools
RUN mkdir -p $ANDROID_SDK_ROOT/cmdline-tools && \
    cd $ANDROID_SDK_ROOT/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O tools.zip && \
    unzip tools.zip && rm tools.zip

# Accept licenses and install SDKs + Build tools
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2"

# Set work directory
WORKDIR /app

# Copy all files into Docker
COPY . /app

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the debug APK
RUN ./gradlew assembleDebug

# Output APK location
CMD ["find", ".", "-name", "*.apk"]
