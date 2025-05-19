# Use an official Android SDK image with build tools
FROM openjdk:17-slim

# Install required packages and Android SDK dependencies
RUN apt-get update && apt-get install -y wget unzip git curl lib32stdc++6 lib32z1

# Set environment variables
ENV ANDROID_SDK_ROOT /opt/android-sdk
ENV PATH ${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/emulator

# Download Android Command Line Tools
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    cd ${ANDROID_SDK_ROOT}/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O tools.zip && \
    unzip tools.zip && rm tools.zip && \
    mv cmdline-tools latest

# Accept licenses and install SDK packages
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Create app directory
WORKDIR /app

# Copy your entire project into container
COPY . .

# Run Gradle build (requires your gradle wrapper to exist)
CMD ["./gradlew", "assembleDebug"]
