package com.about.mantle.amazon.productAdvertisingApi.tasks;

import com.about.mantle.amazon.productAdvertisingApi.model.ResponseGroup;
import com.about.mantle.amazon.productAdvertisingApi.services.AmazonProductAdvertisingApiFacade;
import com.about.mantle.amazon.productAdvertisingApi.services.impl.AmazonProductAdvertisingApiFacadeImpl;
import com.about.mantle.amazon.productAdvertisingApi.services.impl.AmazonProductAdvertisingApiImpl;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.Item;
import com.about.mantle.app.MantleExternalConfigKeys;
import com.about.mantle.model.commerce.AmazonCommerceModel;
import com.google.common.collect.ImmutableList;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This test is set to @Ignore because it requires the secret key to run.  And, since Amazon data is always changing,
 * we can't really test what we get back.  It's meant for during-development testing
 */
@Ignore
public class AmazonProductTaskTest {

    private static Logger logger = LoggerFactory.getLogger(AmazonProductTaskTest.class);

    private static String awsAccessKeyId = "AKIAINYWQL7SPW7D7JCA";
    private static String awsSecretKey = System.getProperty(MantleExternalConfigKeys.AWS_ECOMM_SECRET_KEY);
    private static String associateTag = "aboutcom02lifewire-20";

    private static AmazonProductAdvertisingApiImpl amazon;
    private static AmazonProductTask sut;

    @BeforeClass
    public static void setupClass() {

        amazon = new AmazonProductAdvertisingApiImpl(
                awsAccessKeyId,
                awsSecretKey,
                associateTag,
                true);

        amazon.initialize();

        AmazonProductAdvertisingApiFacade amazonFacade = new AmazonProductAdvertisingApiFacadeImpl(amazon);

        sut = new AmazonProductTask(amazonFacade);
    }

    public String buildBigAmazonQuery() {

        StringBuilder sb = new StringBuilder();
        sb.append("B00KWHMR6G").append(", ");
        sb.append("B006TZM6XO").append(", ");
        sb.append("B003Y5RYNY").append(", ");
        sb.append("B00DWFPDNO").append(", ");
        sb.append("B000BTL0OA").append(", ");
        sb.append("B001UI4RTG").append(", ");
        sb.append("B01MFF4ZKO").append(", ");
        sb.append("B00HXT858A").append(", ");
        sb.append("B01669UOLI").append(", ");
        sb.append("B010B6EJQS").append(", ");
        sb.append("B00NJTNQ82").append(", ");
        sb.append("B01669UNR8").append(", ");
        sb.append("B001F0RPGG").append(", ");
        sb.append("B004S22KFE").append(", ");
        sb.append("B000U89KGW").append(", ");
        sb.append("B00DNJU61S").append(", ");
        sb.append("B01CO2JPYS").append(", ");
        sb.append("B01BV14I40").append(", ");
        sb.append("B00I58LNF0").append(", ");
        sb.append("B01N97XM8X").append(", ");
        sb.append("B01M14ATO0").append(", ");
        sb.append("B01C3LE716").append(", ");
        sb.append("B00KW5OKU4").append(", ");
        sb.append("B014US3FQI").append(", ");
        sb.append("B00XI87KV8").append(", ");
        sb.append("B002MFLWVM").append(", ");
        sb.append("B013HBO39C").append(", ");
        sb.append("B00GO6AP90").append(", ");
        sb.append("B01J7L88RE").append(", ");
        sb.append("B01MZZGBPS").append(", ");
        sb.append("B01I1C8VGI").append(", ");
        sb.append("B003XDU2Y0").append(", ");
        sb.append("B003VBKSWK");

        return sb.toString();

    }
}