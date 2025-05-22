# Use an official JDK with Android SDK
FROM openjdk:11-slim

# Set environment variables
ENV ANDROID_SDK_ROOT /opt/android-sdk
ENV PATH ${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/tools/bin:${ANDROID_SDK_ROOT}/platform-tools

# Install required dependencies
RUN apt-get update && apt-get install -y wget unzip git curl lib32stdc++6 lib32z1

# Download Android SDK command line tools
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    cd ${ANDROID_SDK_ROOT}/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O tools.zip && \
    unzip tools.zip && rm tools.zip

# Accept licenses and install platforms
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2"

# Copy project files
WORKDIR /app
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the Android project
RUN ./gradlew build --warning-mode all

CMD ["./gradlew", "build"]
