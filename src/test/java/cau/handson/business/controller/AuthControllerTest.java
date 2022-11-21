package cau.handson.business.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cau.handson.business.constant.Code;
import cau.handson.business.dto.JwtDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("Controller - Auth")
class AuthControllerTest extends BaseControllerTest {

    List<HashMap<String, Object>> users = new ArrayList<>() {{
        add(new HashMap<>() {{
            put("id", "20186274");
            put("email", "msk@cau.ac.kr");
            put("name", "김명승");
            put("role", "student");
            put("password", "1qaz2wsx");
        }});
        add(new HashMap<>() {{
            put("id", "102849");
            put("email", "prof_lee@cau.ac.kr");
            put("name", "이교수");
            put("role", "professor");
            put("password", "1qaz2wsx");
        }});
        add(new HashMap<>() {{
            put("id", "20230123");
            put("email", "newbie@cau.ac.kr");
            put("name", "새내기");
            put("role", "student");
            put("password", "1qaz2wsx");
        }});
    }};


    @BeforeEach
    void setUp() throws Exception {

    }

    @Test
    @DisplayName("signUp")
    void register() throws Exception {
        JwtDto res = register(users.get(0), Code.OK.getCode(), true);
        then(res.getAccessToken()).isNotNull();
    }

    @Test
    @DisplayName("signIn")
    void signIn() throws Exception {
        register();
        HashMap<String, Object> body = new HashMap<>() {{
            put("email", "msk@cau.ac.kr");
            put("password", "1qaz2wsx");
        }};

        JwtDto res = signin(body, Code.OK.getCode(), true);
        then(res.getAccessToken()).isNotNull();

        body.put("password", "qaaa");
        signin(body, Code.UNAUTHORIZED.getCode(), true);

    }


    JwtDto register(HashMap<String, Object> body, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("가입",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email").description("이메일")
                        , fieldWithPath("password").description("비밀번호")
                        , fieldWithPath("id").description("학번/교번")
                        , fieldWithPath("name").description("성명")
                        , fieldWithPath("role").description("역할(student/professor)")
                    ),
                    relaxedResponseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.accessToken").description("Access Token")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), JwtDto.class);
    }

    JwtDto signin(HashMap<String, Object> body, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document(code == Code.OK.getCode() ? "로그인" : "로그인실패",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email").description("이메일")
                        , fieldWithPath("password").description("비밀번호")
                    ),
                    relaxedResponseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.accessToken").description("Access Token").optional()
                            .type(JsonFieldType.STRING)
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), JwtDto.class);
    }

    void getJwt() throws Exception {
        for (HashMap<String, Object> user : users) {
            user.put("jwt", getJwt((String) user.get("id"), (String) user.get("name"), (String) user.get("email"),
                (String) user.get("role"), (String) user.get("password")));
        }
    }

    String getJwt(String id, String name, String email, String role, String password) throws Exception {
        HashMap<String, Object> body = new HashMap<>() {{
            put("id", id);
            put("email", email);
            put("name", name);
            put("role", role);
            put("password", password);
        }};

        return "Bearer " + register(body, Code.OK.getCode(), false).getAccessToken();
    }
}

