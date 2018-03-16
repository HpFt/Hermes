package ru.tykvin.hermes.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

@Data
@ApiModel("Тело запроса токена для пользователя")
public class TokenRequest {

    @NonNull
    @ApiModelProperty("IP адрес компьютера, с которого пользователь делает запрос")
    private final String ip;

}
