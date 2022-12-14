package cau.handson.business.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cau.handson.business.constant.Code;
import cau.handson.business.dto.UserDto;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("Controller - User")
class UserControllerTest extends BaseControllerTest {

    AuthControllerTest authControllerTest = new AuthControllerTest();

    @BeforeEach
    void setUp() throws Exception {
        authControllerTest.mockMvc = mockMvc;
        authControllerTest.getJwt();
    }

    @Test
    @DisplayName("User CRUD")
    void getMeTest() throws Exception {
        String jwt = (String) authControllerTest.users.get(0).get("jwt");
        UserDto res = getMe(jwt, Code.OK.getCode(), true);

        body = new HashMap<>() {{
            put("id", "20186274");
            put("name", "?????????2");
        }};
        res = update(jwt, body, Code.OK.getCode(), true);
        then(res.getName()).isEqualTo(body.get("name"));

        del(jwt, Code.OK.getCode(), true);
    }


    UserDto getMe(String id, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(get("/user/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", id)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("???????????????",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    responseFields(
                        fieldWithPath("success").description("?????? ??????")
                        , fieldWithPath("code").description("?????? ??????")
                        , fieldWithPath("message").description("?????????")
                        , fieldWithPath("data.id").description("??????(id)")
                        , fieldWithPath("data.email").description("?????????")
                        , fieldWithPath("data.name").description("??????")
                        , fieldWithPath("data.role").description("??????(student/professor)")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), UserDto.class);
    }

    UserDto update(String id, HashMap<String, Object> body, int code, boolean doDocs) throws Exception {
        String temp = (String) body.remove("id");
        MvcResult res = mockMvc.perform(patch("/user/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .header("Authorization", id)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("???????????????",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    requestFields(
                        fieldWithPath("id").optional().description("????????? ??????/??????").type(JsonFieldType.STRING)
                        , fieldWithPath("email").optional().description("????????? ?????????").type(JsonFieldType.STRING)
                        , fieldWithPath("name").optional().description("????????? ??????").type(JsonFieldType.STRING)
                        , fieldWithPath("role").optional().description("????????? ??????(student/professor)")
                            .type(JsonFieldType.STRING)
                    ),
                    responseFields(
                        fieldWithPath("success").description("?????? ??????")
                        , fieldWithPath("code").description("?????? ??????")
                        , fieldWithPath("message").description("?????????")
                        , fieldWithPath("data.id").description("??????(id)")
                        , fieldWithPath("data.name").description("??????")
                        , fieldWithPath("data.email").description("?????????")
                        , fieldWithPath("data.role").description("??????(student/professor)")
                    )
                )
            )
            .andReturn();
        body.put("id", temp);

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), UserDto.class);
    }

    void del(String id, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(delete("/user/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", id)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("??????",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    responseFields(
                        fieldWithPath("success").description("?????? ??????")
                        , fieldWithPath("code").description("?????? ??????")
                        , fieldWithPath("message").description("?????????")
                        , fieldWithPath("data").description("????????????")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
    }
}

