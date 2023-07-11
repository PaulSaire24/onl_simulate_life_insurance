package com.bbva.rbvd.lib.r302.service.dao;

import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;

import java.util.Map;

public interface IProductDAO {

    ProductInformationDAO getProductInformationById(String productId);

}
