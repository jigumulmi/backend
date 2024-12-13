package com.jigumulmi.admin.banner;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "배너 관리")
@RestController
@RequestMapping("/admin/banner")
public class AdminBannerController {


}
