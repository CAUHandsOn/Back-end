package cau.handson.business.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cau.handson.business.constant.Code;
import cau.handson.business.dto.RoomDto;
import cau.handson.business.dto.RoomUserDto;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("Controller - Room")
class RoomControllerTest extends BaseControllerTest {

    AuthControllerTest authControllerTest = new AuthControllerTest();

    @BeforeEach
    void setUp() throws Exception {
        authControllerTest.mockMvc = mockMvc;
        authControllerTest.getJwt();
    }

    @Test
    @DisplayName("Room CRUD")
    void getMeTest() throws Exception {
        HashMap<String, Object> body = new HashMap<>() {{
            put("id", "34:14:B5:41:C6:82");
            put("name", "310관 B312호");
        }};

        insert(body, Code.OK.getCode(), true);
        RoomDto res = getRoom((String) body.get("id"), Code.OK.getCode(), false);
        then(res.getId()).isEqualTo(body.get("id"));

        HashMap<String, Object> body2 = new HashMap<>() {{
            put("name", "310관 B311호");
        }};
        res = update((String) body.get("id"), body2, Code.OK.getCode(), true);
        then(res.getName()).isEqualTo(body2.get("name"));

        HashMap<String, Object> body3 = new HashMap<>() {{
            put("id", "34:14:B5:41:A2:7E");
            put("name", "310관 B313호");
        }};
        insert(body3, Code.OK.getCode(), false);
        List<RoomDto> all = getAll(Code.OK.getCode(), true);
        then(all.get(1).getId()).isEqualTo(body.get("id"));
        then(all.get(0).getId()).isEqualTo(body3.get("id"));

        del((String) body.get("id"), Code.OK.getCode(), true);
    }

    @Test
    @DisplayName("Room In/Out")
    void inOutTest() throws Exception {

        HashMap<String, Object> body = new HashMap<>() {{
            put("id", "34:14:B5:41:C6:82");
            put("name", "310관 B312호");
        }};
        insert(body, Code.OK.getCode(), true);

        RoomUserDto res1 = getIn(0, (String) body.get("id"), Code.OK.getCode(), true);
        then(res1.getRoom().getId()).isEqualTo(body.get("id"));
        then(res1.getUser().getId()).isEqualTo((String) authControllerTest.users.get(0).get("id"));
        for (int i = 1; i < authControllerTest.users.size(); i++) {
            getIn(i, (String) body.get("id"), Code.OK.getCode(), false);
        }

        RoomDto res2 = getRoom((String) body.get("id"), Code.OK.getCode(), true);

        for (int i = 0; i < authControllerTest.users.size(); i++) {
            then(res2.getRoomMembers().get(i).getUser().getId()).isEqualTo(authControllerTest.users.get(i).get("id"));
        }
        then(res2.getRoomMembers().size()).isEqualTo(authControllerTest.users.size());

        getIn(1, (String) body.get("id"), Code.OK.getCode(), false);
        then(res2.getRoomMembers().size()).isEqualTo(authControllerTest.users.size());

        getOut(0, (String) body.get("id"), Code.OK.getCode(), true);
        res2 = getRoom((String) body.get("id"), Code.OK.getCode(), false);
        then(res2.getRoomMembers().size()).isEqualTo(authControllerTest.users.size() - 1);
        res2 = getRoom((String) body.get("id"), Code.OK.getCode(), false);
        then(res2.getRoomMembers().size()).isEqualTo(authControllerTest.users.size() - 1);

    }


    List<RoomDto> getAll(int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.get("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authControllerTest.users.get(0).get("jwt"))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("강의실전체조회",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.[].id").description("강의실 id(비콘 id)")
                        , fieldWithPath("data.[].name").description("강의실 명"))
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), new TypeToken<ArrayList<RoomDto>>() {
        }.getType());
    }

    RoomDto getRoom(String id, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.get("/room/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authControllerTest.users.get(0).get("jwt"))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("강의실조회",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    pathParameters(
                        parameterWithName("id").description("강의실 id(비콘 id)")
                    ),
                    relaxedResponseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.id").description("강의실 id(비콘 id)")
                        , fieldWithPath("data.name").description("강의실 명")
                        , fieldWithPath("data.roomMembers").description("강의실 인원")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), RoomDto.class);
    }

    RoomDto insert(HashMap<String, Object> body, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(post("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .header("Authorization", authControllerTest.users.get(0).get("jwt"))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("강의실등록",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    requestFields(
                        fieldWithPath("id").description("강의실 id(비콘 id)")
                        , fieldWithPath("name").description("강의실 명")
                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.id").description("강의실 id(비콘 id)")
                        , fieldWithPath("data.name").description("강의실 명")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), RoomDto.class);
    }


    RoomDto update(String id, HashMap<String, Object> body, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.patch("/room/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .header("Authorization", authControllerTest.users.get(0).get("jwt"))

            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("강의실수정",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    pathParameters(
                        parameterWithName("id").description("강의실 id(비콘 id)")
                    ),
                    requestFields(
                        fieldWithPath("id").optional().description("변경할 강의실 id(비콘 id)").type(JsonFieldType.STRING)
                        , fieldWithPath("name").optional().description("변경할 강의실 명").type(JsonFieldType.STRING)

                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.id").description("변경된 강의실 id(비콘 id)")
                        , fieldWithPath("data.name").description("변경된 강의실 명")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), RoomDto.class);
    }

    void del(String id, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.delete("/room/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authControllerTest.users.get(0).get("jwt"))

            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("강의실삭제",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("강의실 id(비콘 id)")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data").description("성공여부")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
    }


    RoomUserDto getIn(int userIdx, String roomId, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.post("/room/{roomId}/me", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authControllerTest.users.get(userIdx).get("jwt"))

            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("강의실입장",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    pathParameters(
                        parameterWithName("roomId").description("강의실 id(비콘 id)")
                    ),
                    relaxedResponseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.user").description("입장한 유저")
                        , fieldWithPath("data.room").description("입장한 강의실")
                        , fieldWithPath("data.getIn").description("입장 시각")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), RoomUserDto.class);
    }


    void getOut(int userIdx, String roomId, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.delete("/room/{roomId}/me", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authControllerTest.users.get(userIdx).get("jwt"))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("강의실퇴장",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Access Token")
                    ),
                    pathParameters(
                        parameterWithName("roomId").description("강의실 id(비콘 id)")
                    ),
                    relaxedResponseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data").description("퇴장 성공 여부")
                    )
                )
            )
            .andReturn();
    }
}

