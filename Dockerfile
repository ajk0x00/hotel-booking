FROM openjdk:17-jdk
LABEL authors="abhijith"
ADD build/libs/hotel-booking.jar /hotel-booking.jar

ENTRYPOINT ["java", "-jar", "/hotel-booking.jar"]