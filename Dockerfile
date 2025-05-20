# Use an official JDK base image
FROM openjdk:17-slim

# Install dependencies
RUN apt-get update && apt-get install -y \
    wget unzip git curl lib32stdc++6 lib32z1 \
    && apt-get clean

# Set environment variables for Android SDK
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator

# Download and install Android Command Line Tools
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    cd ${ANDROID_SDK_ROOT}/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O tools.zip && \
    unzip tools.zip && rm tools.zip && \
    mv cmdline-tools latest

# Accept licenses and install required SDK packages
RUN yes | sdkmanager --sdk_root=${ANDROID_SDK_ROOT} --licenses && \
    sdkmanager --sdk_root=${ANDROID_SDK_ROOT} \
        "platform-tools" \
        "platforms;android-34" \
        "build-tools;34.0.0"

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Give execute permission to Gradle wrapper
RUN chmod +x ./gradlew

# Build the app
CMD ["./gradlew", "assembleDebug"]
