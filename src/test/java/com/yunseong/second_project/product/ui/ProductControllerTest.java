package com.yunseong.second_project.product.ui;

import com.yunseong.second_project.category.command.domain.Category;
import com.yunseong.second_project.common.BaseTest;
import com.yunseong.second_project.product.commend.application.ProductCommandService;
import com.yunseong.second_project.product.commend.application.dto.ProductCreateRequest;
import com.yunseong.second_project.product.commend.application.dto.ProductUpdateRequest;
import com.yunseong.second_project.product.commend.domain.Product;
import com.yunseong.second_project.product.query.application.dto.ProductSearchCondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends BaseTest {

    @Autowired
    private ProductCommandService productCommandService;

    @Test
    public void 물품_생성() throws Exception {
        //given
        String jwtToken = this.getJwtToken();
        String productName = "productName";
        String description = "This Product is Example";
        int value = 10000;
        Category category = this.createCategory("p-category1", null);
        ProductCreateRequest request = new ProductCreateRequest(productName, description, value, Arrays.asList(category.getId()));
        //when
        ResultActions perform = this.mockMvc.perform(post("/v1/products/register")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header("X-AUTH-TOKEN", jwtToken)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(request)));
        //then
        perform
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("product-create",
                        links(
                                linkWithRel("profile").description("API 관련 정보")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("자원의 접근하기위한 해당 유저의 토큰값")
                        ),
                        requestFields(
                                fieldWithPath("productName").type(JsonFieldType.STRING).description("상품 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("value").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("types").type(JsonFieldType.ARRAY).description("상품 카테고리 아이디 목록")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 자원 uri")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("상품 아이디"),
                                fieldWithPath("productName").type(JsonFieldType.STRING).description("상품 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("value").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("view").type(JsonFieldType.NUMBER).description("상품 조회수"),
                                fieldWithPath("createdTime").type(JsonFieldType.STRING).description("생성 시각"),
                                fieldWithPath("updatedTime").type(JsonFieldType.STRING).description("수정 시각"),
                                subsectionWithPath("recommend").description("추천자 명단"),
                                subsectionWithPath("categories").description("관련 카테고리 정보"),
                                subsectionWithPath("_links").description("상품생성 관련 주소")
                        )));
    }

    @Test
    public void 물품_삭제() throws Exception {
        //given
        String jwtToken = this.getJwtToken();
        Product product = this.createProduct("Test", "Test Product", 1000, Arrays.asList(
                this.createCategory("p-category2", null)));
        //when
        ResultActions perform = this.mockMvc.perform(delete("/v1/products/{id}", product.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header("X-AUTH-TOKEN", jwtToken));
        //then
        perform
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("product-delete",
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("자원의 접근하기위한 해당 유저의 토큰값")
                        ),
                        pathParameters(
                                parameterWithName("id").description("상품 아이디")
                        )));
    }

    @Test
    public void 물품_변경() throws Exception {
        //given
        String jwtToken = this.getJwtToken();
        Category oldCategory = this.createCategory("p-oldCategory3", null);
        Product product = this.createProduct("product", "product!!!", 100, Arrays.asList(oldCategory));
        Category newCategory = this.createCategory("p-ewCategory3", null);
        ProductUpdateRequest request = new ProductUpdateRequest("updateProduct",
                "updateProduct!!!!", 1000000, Arrays.asList(newCategory.getId()));
        //when
        ResultActions perform = this.mockMvc.perform(put("/v1/products/{id}", product.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header("X-AUTH-TOKEN", jwtToken)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(request)));
        //then
        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-update",
                        links(
                                linkWithRel("self").description("현재 상품 정보"),
                                linkWithRel("profile").description("API 관련 정보")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("자원의 접근하기위한 해당 유저의 토큰값")
                        ),
                        pathParameters(
                                parameterWithName("id").description("상품 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("상품 아이디"),
                                fieldWithPath("productName").type(JsonFieldType.STRING).description("상품 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("value").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("createdTime").type(JsonFieldType.STRING).description("생성 시각"),
                                fieldWithPath("updatedTime").type(JsonFieldType.STRING).description("수정 시각"),
                                subsectionWithPath("types").description("카테고리 목록"),
                                subsectionWithPath("_links").description("상품생성 관련 주소")
                        )));
    }

    @Test
    public void 물품_추천() throws Exception {
        //given
        String jwtToken = this.getJwtToken();
        Category oldCategory = this.createCategory("p-oldCategory4", null);
        Product product = this.createProduct("product", "product!!!", 100, Arrays.asList(oldCategory));
        //when
        ResultActions perform = this.mockMvc.perform(put("/v1/products/{id}/recommend", product.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header("X-AUTH-TOKEN", jwtToken));
        //then
        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-recommend",
                        links(
                                linkWithRel("self").description("현재 상품 정보"),
                                linkWithRel("profile").description("API 관련 정보")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("자원의 접근하기위한 해당 유저의 토큰값")
                        ),
                        pathParameters(
                                parameterWithName("id").description("상품 아이디")
                        ),
                        responseFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("추천자 아이디"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("추천자 닉네임"),
                                subsectionWithPath("_links").description("상품생성 관련 주소")
                        )));
    }

    @Test
    public void 물품_조회() throws Exception {
        //given
        Category oldCategory = this.createCategory("p-oldCategory5", null);
        Product product = this.createProduct("product", "product!!!", 100, Arrays.asList(oldCategory));
        //when
        ResultActions perform = this.mockMvc.perform(get("/v1/products/{id}", product.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .param("view", "true"));
        //then
        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-select",
                        links(
                                linkWithRel("self").description("현재 상품 정보"),
                                linkWithRel("list").description("상품 목록"),
                                linkWithRel("profile").description("API 관련 정보")
                        ),
                        pathParameters(
                                parameterWithName("id").description("상품 아이디")
                        ),
                        requestParameters(
                                parameterWithName("view").description("상품 조회여부")
                        ),
                        responseFields(
                                fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 아이디"),
                                fieldWithPath("productName").type(JsonFieldType.STRING).description("상품 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("value").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("view").type(JsonFieldType.NUMBER).description("상품 조회수"),
                                fieldWithPath("memberId").type(JsonFieldType.STRING).description("회원 아이디").optional(),
                                subsectionWithPath("recommends").description("추천자 명단"),
                                subsectionWithPath("types").description("관련 카테고리 정보"),
                                subsectionWithPath("_links").description("상품생성 관련 주소")
                        )));
    }

    @Test
    public void 물품_목록_조회() throws Exception {
        //given
        Category category1 = this.createCategory("p-Category8", null);
        Category category2 = this.createCategory("p-Category9", null);
        IntStream.range(1, 6).forEach(i -> this.createProduct("firstCategory", "Good", 1000*i, Arrays.asList(category1)));
        IntStream.range(6, 11).forEach(i -> this.createProduct("secondCategory", "Good", 1000*i, Arrays.asList(category2)));
        //when
        ResultActions perform = this.mockMvc.perform(get("/v1/products")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .param("size", "10"));
        //then
        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-list-select",
                        links(
                                linkWithRel("self").description("현재 상품 목록"),
                                linkWithRel("profile").description("API 관련 정보")
                        ),
                        requestParameters(
                                parameterWithName("size").optional().description("페이지 크기"),
                                parameterWithName("page").optional().description("현재 페이지"),
                                parameterWithName("sort").optional().description("정렬방식")
                        ),
                        responseFields(
                                subsectionWithPath("page").description("현재 페이지 정보"),
                                subsectionWithPath("_embedded.productResponseList").description("상품 리스트 정보"),
                                subsectionWithPath("_links").description("상품생성 관련 주소")
                        )));
    }

    @Test
    public void 물품_목록_검색() throws Exception {
        //given
        Category category1 = this.createCategory("p-Category10", null);
        Category category2 = this.createCategory("p-Category11", null);
        IntStream.range(1, 6).forEach(i -> this.createProduct("firstCategory", "Good", 1000*i, Arrays.asList(category1)));
        IntStream.range(6, 11).forEach(i -> this.createProduct("secondCategory", "Good", 1000*i, Arrays.asList(category2)));
        ProductSearchCondition condition = new ProductSearchCondition();
        condition.setProduct("category");
        condition.setCategory("category");
        //when
        ResultActions perform = this.mockMvc.perform(get("/v1/products/search")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .param("category", condition.getCategory())
                .param("product", condition.getProduct())
                .param("min", condition.getMin())
                .param("max", condition.getMax()));
        //then
        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-list-select-search",
                        links(
                                linkWithRel("self").description("현재 상품 목록"),
                                linkWithRel("profile").description("API 관련 정보")
                        ),
                        requestParameters(
                                parameterWithName("size").optional().description("페이지 크기"),
                                parameterWithName("page").optional().description("현재 페이지"),
                                parameterWithName("sort").optional().description("정렬방식"),
                                parameterWithName("min").optional().description("가격최소값"),
                                parameterWithName("max").optional().description("가격최대값"),
                                parameterWithName("product").optional().description("상품이름"),
                                parameterWithName("category").optional().description("카테고리이름")
                        ),
                        responseFields(
                                subsectionWithPath("page").description("현재 페이지 정보"),
                                subsectionWithPath("_embedded.productResponseList").description("상품 리스트 정보"),
                                subsectionWithPath("_links").description("상품생성 관련 주소")
                        )));
    }

    @Test
    public void 물품_조회_최근탑10() throws Exception {
        //given
        Category category = this.createCategory("p-Category12", null);
        IntStream.range(1, 20).forEach(i -> this.createProduct("firstCategory", "Good", 1000*i, Arrays.asList(category)));
        //when
        ResultActions perform = this.mockMvc.perform(get("/v1/products/recent")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaTypes.HAL_JSON_VALUE));
        //then
        perform
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 물품_조회_조회수탑10() throws Exception {
        //given
        Category category = this.createCategory("p-Category13", null);
        List<Product> list = new ArrayList<>();
        IntStream.range(1, 15).forEach(i -> list.add(this.createProduct("firstCategory", "Good", 1000*i, Arrays.asList(category))));
        for (int i = 0; i < 10; i++) {
            for (int j = i; j < 10; j++) {
                this.productCommandService.updateView(list.get(j).getId());
            }
        }
        //when
        ResultActions perform = this.mockMvc.perform(get("/v1/products/view")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaTypes.HAL_JSON_VALUE));
        //then
        perform
                .andDo(print())
                .andExpect(status().isOk());
    }

/*    @Test
    public void 물품_조회_추천수탑10() throws Exception {
        //given
        this.getJwtToken();
        Category category = this.createCategory("Category", null);
        List<Product> list = new ArrayList<>();
        IntStream.range(1, 15).forEach(i -> list.add(this.createProduct("firstCategory", "Good", 1000*i, Arrays.asList(category))));
        for (int i = 0; i < 12; i++) {
            for (int j = i; j < 12; j++) {
                this.productCommandService.updateRecommendProduct(list.get(j).getId());
            }
        }
        //when
        ResultActions perform = this.mockMvc.perform(get("/v1/products/best")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaTypes.HAL_JSON_VALUE));
        //then
        perform
                .andDo(print())
                .andExpect(status().isOk());
    }*/
}