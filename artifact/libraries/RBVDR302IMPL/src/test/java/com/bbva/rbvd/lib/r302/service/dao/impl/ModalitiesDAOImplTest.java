package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/RBVDR302-app.xml",
        "classpath:/META-INF/spring/RBVDR302-app-test.xml",
        "classpath:/META-INF/spring/RBVDR302-arc.xml",
        "classpath:/META-INF/spring/RBVDR302-arc-test.xml" })
public class ModalitiesDAOImplTest {
    private PISDR350 pisdR350;
    private ModalitiesDAOImpl modalities;
    @Before
    public void setup(){
        pisdR350 = mock(PISDR350.class);
        modalities = new ModalitiesDAOImpl(pisdR350);
    }
    @Test(expected = BusinessException.class)
    public void getModalitiesInfoIsEmpit(){
        List<InsuranceProductModalityDAO> result = modalities.getModalitiesInfo("null", BigDecimal.valueOf(1), null);

        Assert.assertNotNull(result);
    }
}