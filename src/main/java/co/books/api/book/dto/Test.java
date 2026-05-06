package co.books.api.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Test {

    private String testName;

    public Test(String testName) {
        this.testName = testName;
    }

}
