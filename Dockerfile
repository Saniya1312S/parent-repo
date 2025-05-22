# Use OpenJDK 17 with Debian base image
FROM openjdk:17-slim

# Install required tools
RUN apt-get update && \
    apt-get install -y wget unzip git curl lib32stdc++6 lib32z1 && \
    rm -rf /var/lib/apt/lists/*

# Set environment variables for Android SDK
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator

# Create Android SDK directory and download command line tools
RUN mkdir -p $ANDROID_SDK_ROOT/cmdline-tools && \
    cd $ANDROID_SDK_ROOT/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O tools.zip && \
    unzip tools.zip && rm tools.zip && \
    mv cmdline-tools latest

# Accept licenses and install essential packages
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "build-tools;34.0.0" "platforms;android-34"

# Set working directory
WORKDIR /app

# Copy all project files into container
COPY . .

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the project with full warnings and debug output
RUN ./gradlew build --warning-mode all --stacktrace
