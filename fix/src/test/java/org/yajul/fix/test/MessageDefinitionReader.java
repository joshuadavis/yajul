package org.yajul.fix.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;

/**
 * Reads FIX acceptance test definitions and creates FIX messages.
 * <br>
 * User: josh
 * Date: Jun 12, 2009
 * Time: 11:46:36 AM
 */
public class MessageDefinitionReader {
    private static final Logger log = LoggerFactory.getLogger(MessageDefinitionReader.class);
    private static final String BASE_DIR = "etc/qfj-test/acceptance/definitions/";
    private int heartBeatOverride;

    public static void main(String[] args) {
        try {
            MessageDefinitionReader reader = new MessageDefinitionReader();
            reader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class TestDefinitionFilter implements FileFilter {
        public boolean accept(File file) {
            return (file.getName().endsWith(".def") &&
                    !file.getName().startsWith(".") &&
                    !file.getParentFile().getName().equals("future"));
        }
    }
    private void readAll() throws IOException {
        File d = new File(BASE_DIR + "server/fix44");
        File[] files = d.listFiles(new TestDefinitionFilter());
        for (File file : files) {
            readFile(file);
        }
    }

    private void readFile(File file) throws IOException {
        String filename = file.getPath();
        log.info("load test: " + filename);
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(filename));
            String line = in.readLine();
            while (line != null) {
                if (line.matches("^[ \t]*#.*")) {
                    // steps.add(new PrintComment(line));
                } else if (line.startsWith("I")) {
                    // steps.add(new InitiateMessageStep(line));
                    generateMessage(line);
                } else if (line.startsWith("E")) {
                    // steps.add(new ExpectMessageStep(line));
                } else if (line.matches("^i\\d*,?CONNECT")) {
                    // steps.add(new ConnectToServerStep(line, transportType, port));
                } else if (line.matches("^e\\d*,?DISCONNECT")) {
                    // steps.add(new ExpectDisconnectStep(line));
                }
                line = in.readLine();
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static final Pattern MESSAGE_PATTERN = Pattern
            .compile("I(\\d,)*(8=FIX\\.\\d\\.\\d\\001)(.*?)(10=.*|)$");

    private static final Pattern TIME_PATTERN = Pattern.compile("<TIME([+-](\\d+))*>");

    private static final Pattern HEARTBEAT_PATTERN = Pattern.compile("108=\\d+\001");

    private static final DecimalFormat CHECKSUM_FORMAT = new DecimalFormat("000");

    private void generateMessage(String data) {
        Matcher messageStructureMatcher = MESSAGE_PATTERN.matcher(data);
        String message;
        int clientId;
        if (messageStructureMatcher.matches()) {
            if (messageStructureMatcher.group(1) != null
                    && !messageStructureMatcher.group(1).equals("")) {
                clientId = Integer.parseInt(messageStructureMatcher.group(1).replaceAll(",", ""));
            } else {
                clientId = 1;
            }
            String version = messageStructureMatcher.group(2);
            String messageTail = insertTimes(messageStructureMatcher.group(3));
            messageTail = modifyHeartbeat(messageTail);
            String checksum = messageStructureMatcher.group(4);
            if ("10=0\001".equals(checksum)) {
                checksum = "10=000\001";
            }
            message = version
                    + (!messageTail.startsWith("9=") ? "9=" + messageTail.length() + "\001" : "")
                    + messageTail + checksum;
        } else {
            log.info("garbled message being sent");
            clientId = 1;
            message = data.substring(1);
        }
        if (message.indexOf("\00110=") == -1) {
            message += "10=" + CHECKSUM_FORMAT.format(checksum(message)) + '\001';
        }
        log.debug("sending to client " + clientId + ": " + message);

    }

    private String insertTimes(String message) {
        Matcher matcher = TIME_PATTERN.matcher(message);
        while (matcher.find()) {
            long offset = 0;
            if (matcher.group(2) != null) {
                offset = Long.parseLong(matcher.group(2)) * 1100L;
                if (matcher.group(1).startsWith("-")) {
                    offset *= -1;
                }
            }
            String beginString = message.substring(2, 9);
            boolean includeMillis = beginString.compareTo("FIX4.2") >= 0;
            SimpleDateFormat format = new SimpleDateFormat(includeMillis ?
                    "yyyyMMdd-HH:mm:ss.SSS" : "yyyyMMdd-HH:mm:ss");
            message = matcher.replaceFirst(format.format(new Date(System
                    .currentTimeMillis()
                    + offset)));
            matcher = TIME_PATTERN.matcher(message);
        }
        return message;
    }

    private int checksum(String message) {
        int sum = 0;
        for (int i = 0; i < message.length(); i++) {
            sum += message.charAt(i);
        }
        return sum % 256;
    }

    private String modifyHeartbeat(String messageTail) {
        if (heartBeatOverride > 0 && messageTail.indexOf("35=A\001") != -1) {
            Matcher matcher = HEARTBEAT_PATTERN.matcher(messageTail);
            if (matcher.find()) {
                return matcher.replaceFirst("108=" + heartBeatOverride + "\001");
            }
        }
        return messageTail;
    }

}
