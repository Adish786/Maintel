package com.about.mantle.model;

import org.junit.Assert;
import org.junit.Test;

public class PriceInfoTest {

    private void displayText(String price, String expected) {
        PriceInfo priceInfo = new PriceInfo();
        priceInfo.setPrice(price);
        Assert.assertEquals(priceInfo.getDisplayText(), expected);
    }

    private void simpleDisplayText(String price, String expected) {
        PriceInfo priceInfo = new PriceInfo();
        priceInfo.setPrice(price);
        Assert.assertEquals(priceInfo.getSimpleDisplayText(), expected);
    }

    /**
     * testing getDisplayText()
     */

    @Test
    public void testWholePrice() {
        displayText("13", "$13.00");
    }

    @Test
    public void testOneDecimalPrice() {
        displayText("13.1", "$13.10");
    }

    @Test
    public void testMultipleDecimalPrice() {
        displayText("13.14567", "$13.15");
    }

    @Test
    public void testNoPrice() {
        displayText("", null);
    }

    @Test
    public void testNullPrice() {
        displayText(null, null);
    }

    @Test
    public void testBigPriceWithAComma() {
        displayText("1899.99", "$1,899.99");
    }

    @Test
    public void testLeadingWhitespace() {
        displayText(" 1899.99", "$1,899.99");
    }

    @Test
    public void testTrailingWhitespace() {
        displayText("1899.99 ", "$1,899.99");
    }

    @Test
    public void testNonNumericCharacter() {
        displayText("$1899.99", "$1,899.99");
    }

    @Test
    public void testNonNumericOtherCharacter() {
        displayText("$1,899.99", "$1,899.99");
    }

    @Test
    public void testPlainText() {
        displayText("Price Upon Request", "Price Upon Request");
    }

    @Test
    // We trust editors to not enter gibberish
    public void testPlainTextGibberish() {
        displayText("kxhfkags op", "kxhfkags op");
    }



    /**
     * Testing getSimpleDisplayText()
     */

    @Test
    public void testSimpleWholePrice() {
        simpleDisplayText("13", "13.00");
    }

    @Test
    public void testSimpleOneDecimalPrice() {
        simpleDisplayText("13.1", "13.10");
    }

    @Test
    public void testSimpleMultipleDecimalPrice() {
        simpleDisplayText("13.14567", "13.15");
    }

    @Test
    public void testSimpleNoPrice() {
        simpleDisplayText("", null);
    }

    @Test
    public void testSimpleNullPrice() {
        simpleDisplayText(null, null);
    }

    @Test
    public void testSimpleBigPriceWithAComma() {
        simpleDisplayText("1899.99", "1899.99");
    }

    @Test
    public void testSimpleLeadingWhitespace() {
        simpleDisplayText(" 1899.99", "1899.99");
    }

    @Test
    public void testSimpleTrailingWhitespace() {
        simpleDisplayText("1899.99 ", "1899.99");
    }

    @Test
    public void testSimpleNonNumericCharacter() {
        simpleDisplayText("$1899.99", "1899.99");
    }

    @Test
    public void testSimpleNonNumericOtherCharacter() {
        simpleDisplayText("$1,899.99", "1899.99");
    }

    @Test
    public void testSimplePlainText() {
        simpleDisplayText("Price Upon Request", null);
    }

    @Test
    // We trust editors to not enter gibberish
    public void testSimplePlainTextGibberish() {
        simpleDisplayText("kxhfkags op", null);
    }

}
