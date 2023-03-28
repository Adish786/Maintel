package com.about.mantle.utils.selene.api.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataWithStatus<T> {
	private Status status;
	private T data;
}
