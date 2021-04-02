package com.github.remering.krutheye.message

import javax.validation.constraints.NotBlank

typealias ProfilesRequest =  List<@NotBlank String>

typealias ProfilesResponse = List<YggdrasilProfileMessage>