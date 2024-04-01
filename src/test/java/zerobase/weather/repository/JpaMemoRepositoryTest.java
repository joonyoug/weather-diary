package zerobase.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Transactional
@SpringBootTest
class JpaMemoRepositoryTest {
    @Autowired
    JpaMemoRepository jpaMemoRepository;


    @Test
    void insertMemoTest() {
        //given
        Memo newMemo=new Memo(2,"test123");
        //when
        jpaMemoRepository.save(newMemo);
        //then
       List<Memo> result=jpaMemoRepository.findAll();
       assertTrue(result.size()>0);

    }
    @Test
    void findById() {
        //given
        Memo newMemo=new Memo(11,"jpa");
        //when
        Memo memo=jpaMemoRepository.save(newMemo); //mysql에서 자동생성 되기 떄문에 11은 아무 의미 없음 
        System.out.println(memo.getId());

        //then
        Optional<Memo> result=jpaMemoRepository.findById(memo.getId());
        assertEquals(result.get().getText(),"jpa");

    }




}