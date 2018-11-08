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

# characters-unicode-appendix
_Reference_: https://www.javaworld.com/article/3067393/learn-java/when-is-a-character-not-a-character.html  
_Reference_: https://docs.oracle.com/javase/tutorial/i18n/text/supplementaryChars.html  
_Reference_: https://stackoverflow.com/questions/23979676/java-what-are-characters-code-points-and-surrogates-what-difference-is-there

