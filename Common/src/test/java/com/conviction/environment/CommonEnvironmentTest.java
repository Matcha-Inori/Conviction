package com.conviction.environment;

import org.junit.Assert;
import org.junit.Test;

public class CommonEnvironmentTest
{
    @Test
    public void testLoad() throws Exception
    {
        IEnvironment iEnvironment = new CommonEnvironment();
        String aValue = iEnvironment.getProperty("abc", "A");
        String bValue = iEnvironment.getProperty("common", "B");
        String cValue = iEnvironment.getProperty("abc.EEE", "C");
        Assert.assertEquals(aValue, "1");
        Assert.assertEquals(bValue, "2");
        Assert.assertNull(cValue);
    }
}