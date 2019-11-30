[![Build Status](https://travis-ci.com/mtumilowicz/java11-string.svg?branch=master)](https://travis-ci.com/mtumilowicz/java11-string)

# java11-string
Summary of Java 9 & 11 String news.

_Reference_: https://4comprehension.com/java-11-string-api-updates/  
_Reference_: https://www.baeldung.com/java-9-compact-string  
_Reference_: https://www.vojtechruzicka.com/java-9-compact-strings/

# preface
## java 9 String revolution
String in Java internally
Strings in Java are internally represented by a `char[]` containing the 
characters of the String. And, every char is made up of 2 bytes 
because Java internally uses `UTF-16`, but statistically most strings 
require only 8 bits — `LATIN-1` character representation.

Whenever we create a String if all the characters of the String can be 
represented using a byte — `LATIN-1` representation, a byte array will 
be used internally, such that one byte is given for one character. 
In other cases, if any character requires more than 8-bits to represent 
it, all the characters are stored using two bytes for each — 
`UTF-16` representation.

How will it distinguish between the `LATIN-1` and `UTF-16` 
representations?

We have a final field coder, that preserves this information.

```
private final byte coder;

static final byte LATIN1 = 0;
static final byte UTF16 = 1;
```

Most of the String operations now check the coder and dispatch to 
the specific implementation.

```
private boolean isLatin1() {
    return COMPACT_STRINGS && coder == LATIN1; // +XX:-CompactStrings is JVM option enabled by default
}
```

## methods
### java 9
* `public IntStream chars()`
* `public IntStream codePoints()`

More info at: [characters, unicode appendix](#characters-unicode-appendix)

### java 11
* `public String strip()`
* `public String stripLeading()`
* `public String stripTrailing()`
* `public boolean isBlank()`
* `public Stream<String> lines()`
* `public String repeat(int count)`

WhiteSpace is clearly described via internals:
`CharacterData` is an abstract class with many methods,
one of them is: 
* `abstract boolean isWhitespace(int ch);`
We get implementations of this class by its method:
```
static final CharacterData of(int ch) {
    if (ch >>> 8 == 0) {
        return CharacterDataLatin1.instance;
    ...
}
```
and (for example) `isWhitespace` from `CharacterDataLatin1.instance`:
```
boolean isWhitespace(int ch) {
    int props = getProperties(ch);
    return ((props & 0x00007000) == 0x00004000);
}
```

# project description
We provide tests for methods mentioned above.

The most interesting part is `codePoints()` vs `chars()`:
* in case of LATIN String - there is no difference
* in case of surrogates:
    ```
    @Test
    public void chars_utf16() {
        IntStream chars = "\uD800\uDF22".chars();

        assertThat(convert(chars), is("55296-57122"));
    }

    @Test
    public void codePoints_utf16() {
        IntStream chars = "\uD800\uDF22".codePoints();

        assertThat(convert(chars), is("66338"));
    }    
    ```
    where 
    ```
    Old Italic   U+10300 – U+1032F   (66304–66351)
    𐌢 = http://www.alanwood.net/unicode/unicode_samples.html
    ```

# characters-unicode-appendix
_Reference_: https://www.javaworld.com/article/3067393/learn-java/when-is-a-character-not-a-character.html  
_Reference_: https://docs.oracle.com/javase/tutorial/i18n/text/supplementaryChars.html  
_Reference_: https://stackoverflow.com/questions/23979676/java-what-are-characters-code-points-and-surrogates-what-difference-is-there  
_Reference_: [WJUG #250 - Tomasz Nurkiewicz: Charbuster: 10 mitów o Unicode](https://www.youtube.com/watch?v=QIEpZ0MGoBc)

## preface
* correct java type for one character is: String
    * but it depends heavily on the definition what one character is
    * string could store emojis - for example family (event two ints are insufficient)
* UTF-16 may vary (LE - little endian, BE - big endian) 
    * byte order is important when we code on many bytes
    * BOM: byte order mark - magic number at the start of a text stream
* example:
    * a, U+0061
        * UTF-8:           61
        * UTF-16:       00 61
        * UTF-32: 00 00 00 61
    * ą, U+0105
        * UTF-8:        C4 85 // 2 bytes, one character
        * UTF-16:       01 85
        * UTF-32: 00 00 01 05
        * "ą".equals("ą") // could be false, ą = a + ogonek
    * 𝄞, U+1D11E
        * UTF-8:  F0 9D 84 9E
        * UTF-16: D8 34 DD 1E
        * UTF-32: 00 01 D1 1e
        * char in java: 2 bytes; violin key: 4 bytes
        * "𝄞".length() == 4 // how to validate user input?
        * surrogate pairs - we need two code-points for one symbol
* `String.getBytes()` - UTF-16? UTF-32?
    * no, it depends on the system properties, so if you use it
    your system will not be portable
    * if you create bytes from string with that method, send it via web and recreate the string
    - you have to know about encoding
    * https://blog.thetaphi.de/2012/07/default-locales-default-charsets-and.html
* `public int length()` returns code units (code unit in java is char)
* interesting questions: how twitter counts twit length?
* use UTF-8: http://utf8everywhere.org/

## description
A character set is a collection of characters, and a coded character 
set is a character set in which code points (numeric values) are 
associated with characters. For example, the American Standard Code 
for Information Interchange (ASCII) is a coded character set 
(e.g., hexadecimal value 41 is assigned to "A").

ASCII is an old coded character set standard with an English 
language bias. In 1987, work began on a universal coded character 
set that could accommodate all of the characters of the world's 
living (and, eventually, dead) languages. The resulting standard 
became known as Unicode.

Unicode 1.0 fixed the size of a character at 16 bits, limiting the 
maximum number of characters that could be represented to 65,536. 
To support the thousands of rarely used or obsolete characters 
(e.g., Egyptian Hieroglyphs) found in historic scripts, Unicode 
2.0 increased its codespace to more than one million code points 
by introducing a new architecture based on planes and surrogates.

A plane is a group of 65,536 code points; Unicode supports 17 planes.
The first plane (code points 0 through 65535), known as the Basic 
Multilingual Plane (BMP)

Code points found in the BMP are directly accessible. However, 
code points in the supplementary planes, which represent 
supplementary characters, are accessed indirectly via surrogate 
(substitute) pairs in UTF-16.

