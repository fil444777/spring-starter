package spring.util;


import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ConnectionProperties {
    private String url;
    private String userName;
    private String password;

}
