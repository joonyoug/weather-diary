package zerobase.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class jdbcMemoRepositoryTest {
    @Autowired
    jdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest(){
        //given
        Memo newMemo=new Memo(2,"this is memo");
        //when
        jdbcMemoRepository.save(newMemo);
        //then
        Optional<Memo> result=jdbcMemoRepository.findById(1);
        assertEquals("this is memo",result.get().getText());

    }
    @Test
    void findAllMemoTest() {

        List<Memo> memoList=jdbcMemoRepository.findAll();
        System.out.println(memoList);
        assertNotNull(memoList);

    }







}