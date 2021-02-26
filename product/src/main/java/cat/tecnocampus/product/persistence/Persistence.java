package cat.tecnocampus.product.persistence;

import cat.tecnocampus.product.application.ProductPersistence;
import cat.tecnocampus.product.domain.Product;
import cat.tecnocampus.product.domain.ProductDoesNotExistException;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.ResultSetExtractorImpl;
import org.simpleflatmapper.jdbc.spring.RowMapperImpl;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class Persistence implements ProductPersistence {
    private JdbcTemplate jdbcTemplate;

    ResultSetExtractorImpl<Product> productResultSetExtractor =
            JdbcTemplateMapperFactory
                    .newInstance()
                    .addKeys("id")
                    .newResultSetExtractor(Product.class);

    RowMapperImpl<Product> productRowMapper = JdbcTemplateMapperFactory.newInstance().addKeys("id")
            .newRowMapper(Product.class);


    public Persistence(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> getProducts() {
        final var query = "select * from product";
        return jdbcTemplate.query(query, productResultSetExtractor);
    }

    @Override
    public void createProduct(Product product) {
        final var insertProduct = "INSERT INTO product (name, description) VALUES (?,?)";
        jdbcTemplate.update(insertProduct, product.getName(), product.getDescription());
    }

    @Override
    public Product getProduct(long id) {
        final var query = "select * from product where id = ?";
        try {
            return jdbcTemplate.queryForObject(query, productRowMapper, id);
        } catch (
        EmptyResultDataAccessException e) {
            throw new ProductDoesNotExistException(id);
        }

}
}
