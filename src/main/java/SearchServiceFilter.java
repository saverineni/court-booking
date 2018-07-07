import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Arrays;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * Created by suresh.averineni on 24/05/2018.
 */
public class SearchServiceFilter {

    private static final String[] SEARCH_FIELDS = {
            "declarationId",
            "lines.commodityCode",
            "lines.originCountryCode",
    };

    public static void main(String args[]) {


        int[] array = {23, 43, 56, 97, 32};
        int startValue = 100;
        int sum = Arrays.stream(array).reduce(startValue, (x, y) -> x + y);

        SearchCriteria criteria1 = new SearchCriteria("1", Arrays.asList("OC1"), Arrays.asList("DC1"));
        SearchCriteria criteria2 = new SearchCriteria(null, Arrays.asList("OC1"), Arrays.asList("DC2"));
        SearchCriteria criteria3 = new SearchCriteria(null, Arrays.asList("OC1", "OC2"), Arrays.asList("DC1"));
        SearchCriteria criteria = new SearchCriteria(null, Arrays.asList("OC1", "OC2"), Arrays.asList("DC1", "DC2"));


        BoolQueryBuilder query23 = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery());

        query23
                .must(QueryBuilders.boolQuery()
                            .should(termQuery("address1", "address"))
                            .should(termQuery("address1", "address")))
                .must(QueryBuilders.boolQuery()
                        //.should(termsQuery("address2", "address"))
                        .should(termQuery("address2", "address")));

        BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery());
        BoolQueryBuilder queryWithSearchTerm = buildSearchTermQuery(query, criteria);
        BoolQueryBuilder queryWithOriginCountryFacets = buildOriginCountryQuery(queryWithSearchTerm, criteria);
        BoolQueryBuilder queryWithDispatchCountryFacets = buildDispatchCountryQuery(queryWithOriginCountryFacets, criteria);

        System.out.print(queryWithDispatchCountryFacets.toString());
    }

    private static BoolQueryBuilder buildSearchTermQuery(BoolQueryBuilder query, SearchCriteria searchCriteria) {
        return searchCriteria.optionalSearchTerm()
                .map(it -> query.must(multiMatchQuery(it, SEARCH_FIELDS)))
                .orElse(query);
    }

    private static BoolQueryBuilder buildOriginCountryQuery(BoolQueryBuilder queryWithSearchTerm, SearchCriteria searchCriteria) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        return searchCriteria.getOriginCountryCode().stream().map(countryCode -> shouldArray(queryBuilder, "lines.originCountry.code", countryCode)
        ).reduce(queryWithSearchTerm,(first, second) -> first.must(second));
    }

    private static BoolQueryBuilder buildDispatchCountryQuery(BoolQueryBuilder queryWithOriginCountryFacets, SearchCriteria searchCriteria) {

        if (!searchCriteria.getDispatchCountryCode().isEmpty()) {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            for (String countryCode : searchCriteria.getDispatchCountryCode()) {
                queryBuilder = shouldArray(queryBuilder, "dispatchCountry.code", countryCode);
            }
            return queryWithOriginCountryFacets.must(queryBuilder);
        }
        return queryWithOriginCountryFacets;
    }

//    private static BoolQueryBuilder buildDispatchCountryQuery(BoolQueryBuilder queryWithOriginCountryFacets, SearchCriteria searchCriteria) {
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//
//        BoolQueryBuilder boolQueryBuilder = searchCriteria.getDispatchCountryCode().stream().map(countryCode -> shouldArray(queryBuilder, "dispatchCountry.code", countryCode )
//        ).reduce(queryWithOriginCountryFacets, (intermediateResult, second) -> intermediateResult.must(second));
//
//        return boolQueryBuilder;
//    }

    static BoolQueryBuilder shouldArray(BoolQueryBuilder queryBuilder, String term, String countryCode) {
        return queryBuilder.must(QueryBuilders.termQuery(term, countryCode));
    }
}