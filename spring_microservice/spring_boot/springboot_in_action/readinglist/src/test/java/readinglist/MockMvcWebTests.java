package readinglist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static  org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest//(classes ={ ReadingListApplication.class })@SpringApplicationConfiguration 已不支持
@WebAppConfiguration // 让SpringJUnit4ClassRunner生成WebApplicationContext，而不是ApplicationContext类
public class MockMvcWebTests {
    @Autowired
    private WebApplicationContext webContext; //此处注入
    private MockMvc mockMvc;
    @Before
    public void setupMockMvc() {
        //非安全测试
        mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
        //安全测试
        //mockMvc = MockMvcBuilders.webAppContextSetup(webContext).apply(springSecurity()).build();
    }
    //非安全测试 开始
    @Test
    public void homePage() throws Exception {
        mockMvc.perform(get("/readingList"))
        .andExpect(status().isOk())
        .andExpect(view().name("readingList"))
        .andExpect(model().attributeExists("books"))
        .andExpect(model().attribute("books", is(empty())));
    }
    @Test
    public void postBook() throws Exception {
        mockMvc.perform(post("/craig")// 请求地址/readingList，原书有误
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("title", "BOOK TITLE")
            .param("author", "BOOK AUTHOR")
            .param("isbn", "1234567890")
            .param("description", "DESCRIPTION"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", "/craig"));// 跳转地址/readingList，原书有误
    
            Book expectedBook = new Book();
            expectedBook.setId(1L);
            expectedBook.setReader("craig");
            expectedBook.setTitle("BOOK TITLE");
            expectedBook.setAuthor("BOOK AUTHOR");
            expectedBook.setIsbn("1234567890");
            expectedBook.setDescription("DESCRIPTION");
            
            mockMvc.perform(get("/craig"))// 请求地址/readingList，原书有误
            .andExpect(status().isOk())
            .andExpect(view().name("readingList"))
            .andExpect(model().attributeExists("books"))
            .andExpect(model().attribute("books", hasSize(1)))
            .andExpect(model().attribute("books",contains(samePropertyValuesAs(expectedBook))));
    }
    //非安全测试 结束
    //安全测试
/*    @Test
    public void homePage_unauthenticatedUser0() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location","http://localhost/login"));
    }*/
    //使用WithMockUser
/*    @Test
    @WithMockUser(username="craig",password="password",roles="READER")
    public void homePage_authenticatedUser1() throws Exception {
        mockMvc.perform(get("/readingList")).andExpect(status().is2xxSuccessful())
                .andExpect(header().string("Location",not("http://localhost/login")));
    }*/
    //使用WithUserDetails
    /*@Test
    @WithUserDetails("craig")//声明craig,当执行些方法时，会被 加入安全上下文中
    public void homePage_authenticatedUser2() throws Exception {
        Reader expectedReader = new Reader();
        expectedReader.setUsername("craig");
        expectedReader.setPassword("password");
        expectedReader.setFullname("Craig Walls");

        mockMvc.perform(get("/craig"))
                .andExpect(status().isOk())
                .andExpect(view().name("readingList"))
                .andExpect(model().attribute("reader",samePropertyValuesAs(expectedReader)))
                .andExpect(model().attribute("books", hasSize(0)));
    }*/
}