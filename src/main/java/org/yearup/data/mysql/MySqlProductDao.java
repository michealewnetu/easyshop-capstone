package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao {

    public MySqlProductDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color) {
        String sql = "SELECT * FROM products WHERE 1=1 ";

        if (categoryId != null) {
            sql += " AND category_id = ? ";
        }
        if (minPrice != null) {
            sql += " AND price >= ? ";
        }
        if (maxPrice != null) {
            sql += " AND price <= ? ";
        }

        List<Product> products = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int parameterIndex = 1;

            if (categoryId != null) {
                statement.setInt(parameterIndex++, categoryId);
            }
            if (minPrice != null) {
                statement.setBigDecimal(parameterIndex++, minPrice);
            }
            if (maxPrice != null) {
                statement.setBigDecimal(parameterIndex++, maxPrice);
            }

            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    Product product = mapRow(row);
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    @Override
    public List<Product> listByCategoryId(int categoryId) {
        String sql = "SELECT * FROM products WHERE category_id = ?";

        List<Product> products = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, categoryId);

            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    Product product = mapRow(row);
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    @Override
    public Product getById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productId);

            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    return mapRow(row);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Product create(Product product) {
        String sql = "INSERT INTO products (name, price, category_id, description, stock, featured, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setInt(5, product.getStock());
            statement.setBoolean(6, product.isFeatured());
            statement.setString(7, product.getImageUrl());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        product.setProductId(newId);
                        return product;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void update(int productId, Product product) {
        String sql = "UPDATE products " +
                "SET name = ?, price = ?, category_id = ?, description = ?, " +
                "stock = ?, featured = ?, image_url = ? " +
                "WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setInt(5, product.getStock());
            statement.setBoolean(6, product.isFeatured());
            statement.setString(7, product.getImageUrl());
            statement.setInt(8, productId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected Product mapRow(ResultSet row) throws SQLException {
        Product product = new Product();
        product.setProductId(row.getInt("product_id"));
        product.setName(row.getString("name"));
        product.setPrice(row.getBigDecimal("price"));
        product.setCategoryId(row.getInt("category_id"));
        product.setDescription(row.getString("description"));
        product.setStock(row.getInt("stock"));
        product.setFeatured(row.getBoolean("featured"));
        product.setImageUrl(row.getString("image_url"));
        return product;
    }
}