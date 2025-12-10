package com.ssafy.yogiattacku.attraction.repository;

import com.pgvector.PGvector;
import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.attraction.dto.response.SimilarAttraction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AttractionDescriptionJdbcRepository {
    private final DataSource dataSource;
    private String tableName = "attraction_description";

    public List<SimilarAttraction> findSimilarAttractions(float[] queryVector, int limit) {
        String sql =
                "SELECT id, " +
                        "       sido_name, " +
                        "       gugun_name, " +
                        "       content_type_name, " +
                        "       attraction_name, " +
                        "       address, " +
                        "       latitude, " +
                        "       longitude, " +
                        "       embedding <-> ? AS distance " +
                        "FROM " + tableName + " " +
                        "WHERE embedding IS NOT NULL " +
                        "ORDER BY distance ASC " +
                        "LIMIT ?";

        List<SimilarAttraction> result = new ArrayList<>();

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            PGvector.registerTypes(conn);
            stmt.setObject(1, new PGvector(queryVector));
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new SimilarAttraction(
                            rs.getLong("id"),
                            rs.getString("sido_name"),
                            rs.getString("gugun_name"),
                            rs.getString("content_type_name"),
                            rs.getString("attraction_name"),
                            rs.getString("address"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getDouble("distance")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new GlobalException(ErrorCode.ATTRACTION_VECTOR_SEARCH_ERROR);
        }
        return result;
    }
}
