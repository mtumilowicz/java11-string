import org.junit.Test;

import java.util.PrimitiveIterator;
import java.util.StringJoiner;
import java.util.stream.IntStream;

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

    // Old Italic   U+10300 – U+1032F   (66304–66351)
    // 𐌢 = http://www.alanwood.net/unicode/unicode_samples.html
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
    
    private String convert(IntStream chars) {
        PrimitiveIterator.OfInt iterator = chars.iterator();

        StringJoiner sj = new StringJoiner("-");
        
        while (iterator.hasNext()) {
            sj.add(String.valueOf(iterator.nextInt()));
        }
        
        return sj.toString();
    }
}
