package com.bbva.rbvd.lib.r302.service.dao;

import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;

public interface IProductDAO {

    ProductInformationDAO getProductInformationById(String productId);

}
