package com.winterfarmer.virgo.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangtianhang on 15-4-15.
 * cooy from reilost
 */
public class TextUtil {
    private static final Pattern urlPattern = Pattern
            .compile(
                    new StringBuilder()
                            .append("((?:(http|https):")
                            .append("\\/\\/(?:(?:[a-z0-9\\$\\-\\_\\.\\+\\!\\*\\(\\)")
                            .append("\\,\\;\\?\\&\\=]|(?:\\%[a-f0-9]{2})){1,64}(?:\\:(?:[a-z0-9\\$\\-\\_")
                            .append("\\.\\+\\!\\*\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-f0-9]{2})){1,25})?\\@)?)")
                            .append("(?:(?:[a-z0-9][a-z0-9\\-]{0,64}\\.)+")
                                    // named host
                            .append(")")

                            .append("(?:\\:\\d{1,5})?)")
                                    // plus option port number
                            .append("((?:(?:[a-z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~")
                                    // plus option query params
                            .append("\\-\\.\\+\\!\\*\\(\\)\\,\\_])|(?:\\%[a-f0-9]{2}))*)?")
                            .append("(?:\\b|$)").append("?(?:\\/)*").toString(),
                    Pattern.CASE_INSENSITIVE
            ); // 不区分大小写
    private static final Pattern pattern1 = Pattern
            .compile(
                    new StringBuilder()
                            .append("((?:(http|https|Http|Https|rtsp|Rtsp):")
                            .append("\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)")
                            .append("\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_")
                            .append("\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?")
                            .append("((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+")
                                    // named host
                            .append("(?:")
                                    // plus top level domain
                            .append("(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])")
                            .append("|(?:biz|b[abdefghijmnorstvwyz])")
                            .append("|(?:cat|com|coop|c[acdfghiklmnoruvxyz])")
                            .append("|d[ejkmoz]")
                            .append("|(?:edu|e[cegrstu])")
                            .append("|f[ijkmor]")
                            .append("|(?:gov|g[abdefghilmnpqrstuwy])")
                            .append("|h[kmnrtu]")
                            .append("|(?:info|int|i[delmnoqrst])")
                            .append("|(?:jobs|j[emop])")
                            .append("|k[eghimnrwyz]")
                            .append("|l[abcikrstuvy]")
                            .append("|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])")
                            .append("|(?:name|net|n[acefgilopruz])")
                            .append("|(?:org|om)")
                            .append("|(?:pro|p[aefghklmnrstwy])")
                            .append("|qa")
                            .append("|r[eouw]")
                            .append("|s[abcdeghijklmnortuvyz]")
                            .append("|(?:tel|travel|t[cdfghjklmnoprtvwz])")
                            .append("|u[agkmsyz]")
                            .append("|v[aceginu]")
                            .append("|w[fs]")
                            .append("|y[etu]")
                            .append("|z[amw]))")
                            .append("|(?:(?:25[0-5]|2[0-4]")
                                    // or ip address
                            .append("[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]")
                            .append("|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]")
                            .append("[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}")
                            .append("|[1-9][0-9]|[0-9])))")
                            .append("(?:\\:\\d{1,5})?)")
                                    // plus option port number
                            .append("((?:\\/)*(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~")
                                    // plus option query params
                            .append("\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?")
                            .append("(?:\\b|$)").append("?(?:\\/)*").toString(),
                    Pattern.CASE_INSENSITIVE
            ); // 不区分大小写

    public static boolean containsUrl(String text) {
        return containsUrl(text, false);
    }

    public static boolean containsUrl(String text, boolean newRegex) {
        return collectUrl(text, newRegex).size() > 0;
    }

    public static List<String> collectUrl(String text) {
        return collectUrl(text, false);
    }

    public static List<String> collectUrl(String text, boolean newRegex) {
        List<String> ret = new ArrayList<String>();
        Matcher matcher = newRegex ? urlPattern.matcher(patchRegx(text))
                : pattern1.matcher(patchRegx(text));
        while (matcher.find()) {
            ret.add(matcher.group(0));
        }
        return ret;
    }

    private static String patchRegx(String src) {
        char[] chars = src.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 0x80) {
                chars[i] = ' ';
            }
        }
        return new String(chars);
    }

    /**
     * 计算文本长度
     * code 在 0~255之间的算1个字符，其他的算2个字符
     *
     * @param text
     * @return
     */
    public static int getLength(String text) {
        if (text == null || text.length() == 0) {
            return 0;
        }
        int len = text.length();
        int index = 0;
        char c;
        for (int i = 0; i < len; i++) {
            c = text.charAt(i);
            if ((c > 0xFF)) {
                index += 2;
            } else {
                index++;
            }
        }
        return index;
    }


    /**
     * 检查是否为饭本保留昵称
     *
     * @param name
     * @return
     */

    public static boolean isReservedName(String name) {
        if (name.indexOf("ricebook") != -1) {
            return true;
        } else if (name.indexOf("饭本") != -1) {
            return true;
        }
        return false;
    }

    /**
     * 统一对昵称进行格式化处理(目前只是trim,后续可能处理特殊字符)
     *
     * @param nickName
     * @return
     */

    public static String formatNickName(String nickName) {
        return nickName.trim();
    }


    /**
     * 统一对邮件处理，trim和toLowerCase
     *
     * @param email
     * @return
     */
    public static String formatEmail(String email) {
        email = email.trim();
        return email.toLowerCase();
    }


    public static String escape(String content) {
        if (StringUtils.isNotBlank(content)) {
            content = content.replaceAll("\\r\\n|\\r|\\n", "");
            content = JSON.toJSONString(content);
            content = content.substring(1, content.length() - 1);

            return HtmlUtils.htmlEscape(content);
        }
        return "";
    }

    public static String unescape(String content) {
        if (StringUtils.isNotBlank(content)) {
            return HtmlUtils.htmlUnescape(HtmlUtils.htmlUnescape(content));
        }
        return "";
    }

    public static void trim(StringBuffer sb, char c) {
        if (sb == null) {
            return;
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == c) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }


    public static String protectEmail(String email) {
        StringBuffer sb = new StringBuffer();
        char[] chars = email.toLowerCase().trim().toCharArray();
        boolean replace = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == '@') {
                replace = false;
            }
            if (replace) {
                sb.append('*');
            } else {
                sb.append(c);
            }
            if (i == 1) {
                replace = true;
            }
        }
        return sb.toString();
    }

    public static String protectMobilePhone(String mobile) {
        if (mobile.startsWith("+86")) {
            mobile = mobile.substring(3);
        }

        // 134 2647 5023
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    public static void main(String[] args) {

        System.out.println(TextUtil.escape("123  "));

    }
}

/**
 * copy
 */
abstract class HtmlUtils {

    /**
     * Shared instance of pre-parsed HTML character entity references.
     */
    private static final HtmlCharacterEntityReferences characterEntityReferences =
            new HtmlCharacterEntityReferences();


    /**
     * Turn special characters into HTML character references.
     * Handles complete character set defined in HTML 4.01 recommendation.
     * <p>Escapes all special characters to their corresponding
     * entity reference (e.g. <code>&lt;</code>).
     * <p>Reference:
     * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
     * http://www.w3.org/TR/html4/sgml/entities.html
     * </a>
     *
     * @param input the (unescaped) input string
     * @return the escaped string
     */
    public static String htmlEscape(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            String reference = characterEntityReferences.convertToReference(character);
            if (reference != null) {
                escaped.append(reference);
            } else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }

    /**
     * Turn special characters into HTML character references.
     * Handles complete character set defined in HTML 4.01 recommendation.
     * <p>Escapes all special characters to their corresponding numeric
     * reference in decimal format (&#<i>Decimal</i>;).
     * <p>Reference:
     * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
     * http://www.w3.org/TR/html4/sgml/entities.html
     * </a>
     *
     * @param input the (unescaped) input string
     * @return the escaped string
     */
    public static String htmlEscapeDecimal(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (characterEntityReferences.isMappedToReference(character)) {
                escaped.append(HtmlCharacterEntityReferences.DECIMAL_REFERENCE_START);
                escaped.append((int) character);
                escaped.append(HtmlCharacterEntityReferences.REFERENCE_END);
            } else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }

    /**
     * Turn special characters into HTML character references.
     * Handles complete character set defined in HTML 4.01 recommendation.
     * <p>Escapes all special characters to their corresponding numeric
     * reference in hex format (&#x<i>Hex</i>;).
     * <p>Reference:
     * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
     * http://www.w3.org/TR/html4/sgml/entities.html
     * </a>
     *
     * @param input the (unescaped) input string
     * @return the escaped string
     */
    public static String htmlEscapeHex(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (characterEntityReferences.isMappedToReference(character)) {
                escaped.append(HtmlCharacterEntityReferences.HEX_REFERENCE_START);
                escaped.append(Integer.toString((int) character, 16));
                escaped.append(HtmlCharacterEntityReferences.REFERENCE_END);
            } else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }

    /**
     * Turn HTML character references into their plain text UNICODE equivalent.
     * <p>Handles complete character set defined in HTML 4.01 recommendation
     * and all reference types (decimal, hex, and entity).
     * <p>Correctly converts the following formats:
     * <blockquote>
     * &amp;#<i>Entity</i>; - <i>(Example: &amp;amp;) case sensitive</i>
     * &amp;#<i>Decimal</i>; - <i>(Example: &amp;#68;)</i><br>
     * &amp;#x<i>Hex</i>; - <i>(Example: &amp;#xE5;) case insensitive</i><br>
     * </blockquote>
     * Gracefully handles malformed character references by copying original
     * characters as is when encountered.<p>
     * <p>Reference:
     * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
     * http://www.w3.org/TR/html4/sgml/entities.html
     * </a>
     *
     * @param input the (escaped) input string
     * @return the unescaped string
     */
    public static String htmlUnescape(String input) {
        if (input == null) {
            return null;
        }
        return new HtmlCharacterEntityDecoder(characterEntityReferences, input).decode();
    }
}

class HtmlCharacterEntityReferences {

    private static final String PROPERTIES_FILE = "HtmlCharacterEntityReferences.properties";

    static final char REFERENCE_START = '&';

    static final String DECIMAL_REFERENCE_START = "&#";

    static final String HEX_REFERENCE_START = "&#x";

    static final char REFERENCE_END = ';';

    static final char CHAR_NULL = (char) -1;


    private final String[] characterToEntityReferenceMap = new String[3000];

    private final Map<String, Character> entityReferenceToCharacterMap = new HashMap<String, Character>(252);


    /**
     * Returns a new set of character entity references reflecting the HTML 4.0 character set.
     */
    public HtmlCharacterEntityReferences() {
        Properties entityReferences = new Properties();

        // Load reference definition file
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (is == null) {
            throw new IllegalStateException(
                    "Cannot find reference definition file [HtmlCharacterEntityReferences.properties] as class path resource");
        }
        try {
            try {
                entityReferences.load(is);
            } finally {
                is.close();
            }
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed to parse reference definition file [HtmlCharacterEntityReferences.properties]: " + ex.getMessage());
        }

        // Parse reference definition properties
        Enumeration keys = entityReferences.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            int referredChar = Integer.parseInt(key);
            Assert.isTrue((referredChar < 1000 || (referredChar >= 8000 && referredChar < 10000)),
                    "Invalid reference to special HTML entity: " + referredChar);
            int index = (referredChar < 1000 ? referredChar : referredChar - 7000);
            String reference = entityReferences.getProperty(key);
            this.characterToEntityReferenceMap[index] = REFERENCE_START + reference + REFERENCE_END;
            this.entityReferenceToCharacterMap.put(reference, new Character((char) referredChar));
        }
    }


    /**
     * Return the number of supported entity references.
     */
    public int getSupportedReferenceCount() {
        return this.entityReferenceToCharacterMap.size();
    }

    /**
     * Return true if the given character is mapped to a supported entity reference.
     */
    public boolean isMappedToReference(char character) {
        return (convertToReference(character) != null);
    }

    /**
     * Return the reference mapped to the given character or <code>null</code>.
     */
    public String convertToReference(char character) {
        if (character < 1000 || (character >= 8000 && character < 10000)) {
            int index = (character < 1000 ? character : character - 7000);
            String entityReference = this.characterToEntityReferenceMap[index];
            if (entityReference != null) {
                return entityReference;
            }
        }
        return null;
    }

    /**
     * Return the char mapped to the given entityReference or -1.
     */
    public char convertToCharacter(String entityReference) {
        Character referredCharacter = this.entityReferenceToCharacterMap.get(entityReference);
        if (referredCharacter != null) {
            return referredCharacter.charValue();
        }
        return CHAR_NULL;
    }

}

class HtmlCharacterEntityDecoder {

    private static final int MAX_REFERENCE_SIZE = 10;


    private final HtmlCharacterEntityReferences characterEntityReferences;

    private final String originalMessage;

    private final StringBuilder decodedMessage;

    private int currentPosition = 0;

    private int nextPotentialReferencePosition = -1;

    private int nextSemicolonPosition = -2;


    public HtmlCharacterEntityDecoder(HtmlCharacterEntityReferences characterEntityReferences, String original) {
        this.characterEntityReferences = characterEntityReferences;
        this.originalMessage = original;
        this.decodedMessage = new StringBuilder(originalMessage.length());
    }

    public String decode() {
        while (currentPosition < originalMessage.length()) {
            findNextPotentialReference(currentPosition);
            copyCharactersTillPotentialReference();
            processPossibleReference();
        }
        return decodedMessage.toString();
    }

    private void findNextPotentialReference(int startPosition) {
        nextPotentialReferencePosition = Math.max(startPosition, nextSemicolonPosition - MAX_REFERENCE_SIZE);

        do {
            nextPotentialReferencePosition =
                    originalMessage.indexOf('&', nextPotentialReferencePosition);

            if (nextSemicolonPosition != -1 &&
                    nextSemicolonPosition < nextPotentialReferencePosition)
                nextSemicolonPosition = originalMessage.indexOf(';', nextPotentialReferencePosition + 1);

            boolean isPotentialReference =
                    nextPotentialReferencePosition != -1
                            && nextSemicolonPosition != -1
                            && nextPotentialReferencePosition - nextSemicolonPosition < MAX_REFERENCE_SIZE;

            if (isPotentialReference) {
                break;
            }
            if (nextPotentialReferencePosition == -1) {
                break;
            }
            if (nextSemicolonPosition == -1) {
                nextPotentialReferencePosition = -1;
                break;
            }

            nextPotentialReferencePosition = nextPotentialReferencePosition + 1;
        }
        while (nextPotentialReferencePosition != -1);
    }


    private void copyCharactersTillPotentialReference() {
        if (nextPotentialReferencePosition != currentPosition) {
            int skipUntilIndex = nextPotentialReferencePosition != -1 ?
                    nextPotentialReferencePosition : originalMessage.length();
            if (skipUntilIndex - currentPosition > 3) {
                decodedMessage.append(originalMessage.substring(currentPosition, skipUntilIndex));
                currentPosition = skipUntilIndex;
            } else {
                while (currentPosition < skipUntilIndex)
                    decodedMessage.append(originalMessage.charAt(currentPosition++));
            }
        }
    }

    private void processPossibleReference() {
        if (nextPotentialReferencePosition != -1) {
            boolean isNumberedReference = originalMessage.charAt(currentPosition + 1) == '#';
            boolean wasProcessable = isNumberedReference ? processNumberedReference() : processNamedReference();
            if (wasProcessable) {
                currentPosition = nextSemicolonPosition + 1;
            } else {
                char currentChar = originalMessage.charAt(currentPosition);
                decodedMessage.append(currentChar);
                currentPosition++;
            }
        }
    }

    private boolean processNumberedReference() {
        boolean isHexNumberedReference =
                originalMessage.charAt(nextPotentialReferencePosition + 2) == 'x' ||
                        originalMessage.charAt(nextPotentialReferencePosition + 2) == 'X';
        try {
            int value = (!isHexNumberedReference) ?
                    Integer.parseInt(getReferenceSubstring(2)) :
                    Integer.parseInt(getReferenceSubstring(3), 16);
            decodedMessage.append((char) value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean processNamedReference() {
        String referenceName = getReferenceSubstring(1);
        char mappedCharacter = characterEntityReferences.convertToCharacter(referenceName);
        if (mappedCharacter != HtmlCharacterEntityReferences.CHAR_NULL) {
            decodedMessage.append(mappedCharacter);
            return true;
        }
        return false;
    }

    private String getReferenceSubstring(int referenceOffset) {
        return originalMessage.substring(nextPotentialReferencePosition + referenceOffset, nextSemicolonPosition);
    }

}