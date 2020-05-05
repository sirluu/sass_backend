package jp.co.japantaxi.config;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

public class CustomBanner implements Banner {

  @Override
  public void printBanner(Environment environment, Class<?> aClass, PrintStream printStream) {
    printStream.println(" >>> PROCESS ID : " + ManagementFactory.getRuntimeMXBean().getName());
    printStream.println(
        "|=====================================================================================|");
    printStream.println(
        "|                                                                                     |");
    printStream.println(
        "|                                                                                     |");
    printStream.println(
        "|                     STOCKHOLM SERVICE OF JAPANTAXI APPLICATION                      |");
    printStream.println(
        "|                                                                                     |");
    printStream.println(
        "|                                                                                     |");
    printStream.println(
        "|=====================================================================================|");
    printStream.println(" >>> OS : " + ManagementFactory.getOperatingSystemMXBean().getName());
  }
}
