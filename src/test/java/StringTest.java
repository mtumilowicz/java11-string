import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mtumilowicz on 2018-11-07.
 */
public class StringTest {
    
    @Test
    public void chars_latin() {
        IntStream chars = "abcdef".chars();
        
        assertThat(convert(chars), is("97-98-99-100-101-102"));
    }
    
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

    @Test
    public void codePoints_latin() {
        IntStream chars = "abcdef".codePoints();

        assertThat(convert(chars), is("97-98-99-100-101-102"));
    }
    
    @Test
    public void strip() {
        String stripped = "  abc ".strip();
        
        assertThat(stripped, is("abc"));
    }

    @Test
    public void stripLeading() {
        String stripped = "  abc ".stripLeading();

        assertThat(stripped, is("abc "));
    }

    @Test
    public void stripTrailing() {
        String stripped = "  abc ".stripTrailing();

        assertThat(stripped, is("  abc"));
    }
    
    @Test
    public void isBlank_true() {
        boolean blank = "  ".isBlank();
        
        assertTrue(blank);
    }

    @Test
    public void isBlank_false() {
        boolean blank = " a ".isBlank();

        assertFalse(blank);
    }
    
    @Test
    public void lines() {
        Stream<String> lines = "line1\nline2\nline3\nline4\nline5".lines();

        String convert = convert(lines);
        
        assertThat(convert, is("line1-line2-line3-line4-line5"));
    }
    
    @Test
    public void repeat() {
        String repeat = "abc".repeat(3);
        
        assertThat(repeat, is("abcabcabc"));
    }
    
    private String convert(IntStream chars) {
        return chars.mapToObj(String::valueOf).collect(Collectors.joining("-"));
    }

    private String convert(Stream<String> lines) {
        return lines.collect(Collectors.joining("-"));
    }
}
