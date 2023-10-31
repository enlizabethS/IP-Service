package testTask.ip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IPScanner {
    private final String ipAddressRange;
    private final int threadCount;

    public IPScanner(String ipAddressRange, int threadCount) {
        this.ipAddressRange = ipAddressRange;
        this.threadCount = threadCount;
    }

    public void scan() {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        if (isValidIPAddress(ipAddressRange)) {
            String[] range = ipAddressRange.split("/");
            if (range.length == 2 && isValidSubnetMask(range[1])) {
                String baseIP = range[0];
                try {
                    int subnetMask = Integer.parseInt(range[1]);
                    int totalIPs = (int) Math.pow(2, (32 - subnetMask));
                    for (int i = 0; i < totalIPs; i++) {
                        int currentIP = i;
                        executor.execute(() -> {
                            String ipAddress = incrementIP(baseIP, subnetMask, currentIP);
                            CertificateScanner certificateScanner = new CertificateScanner();
                            certificateScanner.scanCertificate(ipAddress).forEach(this::saveDomainToFile);
                        });
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Incorrect IP address and subnet mask format.");
            }
        } else {
            System.out.println("Invalid IP address format.");
        }
        executor.shutdown();
    }

    private String incrementIP(String baseIP, int subnetMask, int increment) {
        String[] ipAddressParts = baseIP.split("\\.");
        long ip = 0;

        for (int i = 0; i < 4; i++) {
            ip += Long.parseLong(ipAddressParts[i]) << ((3 - i) * 8);
        }

        ip += increment;

        StringBuilder ipAddress = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            ipAddress.insert(0, Long.toString((ip >> (i * 8)) & 0xff));
            if (i > 0) {
                ipAddress.insert(0, ".");
            }
        }
        return ipAddress.toString();
    }

    private synchronized void saveDomainToFile(String domain) {
        try {
            File file = new File("results/found_domains.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileWriter fileWriter = new FileWriter(file, true);
                 BufferedWriter writer = new BufferedWriter(fileWriter)) {
                writer.write(domain);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidIPAddress(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidSubnetMask(String subnetMask) {
        try {
            int mask = Integer.parseInt(subnetMask);
            return mask >= 0 && mask <= 32;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
