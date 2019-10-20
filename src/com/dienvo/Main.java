package com.dienvo;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    private static List<String> dict;
    private static List<String> prohibited;
    private static List<Message> messageList = new ArrayList<>();

    public static void main(String[] args){
        //write your code here
        readFile();
        for (int i = 0; i < messageList.size(); i++) {
            if (checkTime(messageList.get(i))) {
                System.out.println("Message #" + (i + 1) + ": " + messageList.get(i).getContent().getContentOfMessage());
            } else {
                if (checkContent(messageList.get(i)) && checkSpell(messageList.get(i)) && checkProhibited(messageList.get(i))) {
                    System.out.println("Message #" + (i + 1) + ": " + messageList.get(i).getContent().getContentOfMessage());
                } else
                    System.out.println("Message #" + (i + 1) + ": FAILED TO SEND.");
            }
        }

    }

    //read file
    private static void readFile() {
        try {
            String fileName = "textmsg.txt";
            //read number of dictionary, prohibited and message
            List<String> itemNumber = Files.lines(Paths.get(fileName).toAbsolutePath(), StandardCharsets.UTF_8)
                    .filter(line -> line.matches("\\d+"))
                    .collect(Collectors.toList());
            //dictionary start from second line, limit by number of item dictionary
            dict = Files.lines(Paths.get(fileName).toAbsolutePath(), StandardCharsets.UTF_8)
                    .skip(1)
                    .limit(Integer.parseInt(itemNumber.get(0)))
                    .collect(Collectors.toList());
            //prohibited words start after sum of dictionary line and number of dict, prohibited words (2) limit by number of prohibited words
            prohibited = Files.lines(Paths.get(fileName).toAbsolutePath(), StandardCharsets.UTF_8)
                    .skip(Integer.parseInt(itemNumber.get(0)) + 2)
                    .limit(Integer.parseInt(itemNumber.get(1)))
                    .collect(Collectors.toList());
            //message start after dict and prohibited words. Increase by 2 (*2) because 2 line is one message.
            List<String> message = Files.lines(Paths.get(fileName).toAbsolutePath(), StandardCharsets.UTF_8)
                    .skip(Integer.parseInt(itemNumber.get(0)) + Integer.parseInt(itemNumber.get(1)) + 3)
                    .limit(Integer.parseInt(itemNumber.get(2)) * 2)
                    .collect(Collectors.toList());
            for (int i = 0; i < message.size(); i += 2) {
                String time = message.get(i);
                String content = message.get(i + 1);
                String[] arr = content.split(" ");
                String numberOfWords = arr[0];
                String[] arr2 = Arrays.copyOfRange(arr, 1, arr.length);
                String contentOfMessage = String.join(" ", arr2);
                Message mess = new Message(time, new Content(numberOfWords, contentOfMessage));
                messageList.add(mess);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //check time of message
    private static boolean checkTime(@NotNull Message message) {
        DateTimeFormatter df = DateTimeFormat.forPattern("hh:mm a").withLocale(Locale.US);
        DateTime actual = df.parseLocalTime(message.getTime()).toDateTimeToday();
        DateTime limit1 = df.parseLocalTime("12:59 AM").toDateTimeToday();
        DateTime limit2 = df.parseLocalTime("6:59 AM").toDateTimeToday();
        //return true of message don't need to check
        //return false, so that message must check
        return actual.isBefore(limit1) || actual.isAfter(limit2);
    }

    //check message contain "i love you"
    private static boolean checkContent(@NotNull Message message) {
        return !Pattern.compile(Pattern.quote("i love you"),
                Pattern.CASE_INSENSITIVE).matcher(message.getContent().getContentOfMessage()).find();
    }

    //check spell, if message have more than 3 words is misspelled return false
    private static boolean checkSpell(@NotNull Message message) {
        String[] arr = message.getContent().getContentOfMessage().split(" ");
        int count = 0;
        String dictionary = String.join(" ", dict);
        for (String s : arr) {
            if (!Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(dictionary).find()) {
                count++;
            }
        }
        return count < 3;
    }

    //check message contain prohibited words
    private static boolean checkProhibited(@NotNull Message message) {
        String contentOfMessage = message.getContent().getContentOfMessage();
        for (String s : prohibited) {
            if (Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(contentOfMessage).find()) {
                return false;
            }
        }
        return true;
    }
}
