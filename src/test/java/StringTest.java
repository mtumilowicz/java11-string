import org.junit.Test;

import java.util.PrimitiveIterator;
import java.util.StringJoiner;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        IntStream chars = "\u0000\uffff".chars();

        assertThat(convert(chars), is("0-65535"));
    }

    @Test
    public void codePoints_latin() {
        IntStream chars = "abcdef".codePoints();

        assertThat(convert(chars), is("97-98-99-100-101-102"));
    }

    @Test
    public void codePoints_utf16() {
        IntStream chars = "\u0000\uffff".codePoints();

        assertThat(convert(chars), is("0-65535"));
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
