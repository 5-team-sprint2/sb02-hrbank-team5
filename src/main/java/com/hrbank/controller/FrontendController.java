package com.hrbank.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 없는 경로 요청을 모두 index.html으로 포워딩하는 로직
@Controller
public class FrontendController {

  @GetMapping(value = {
      "/",
      "/dashboard",
      "/departments",
      "/employees",
      "/change-logs",
      "/backups"
  })
  public String forwardToFrontend() {
    return "forward:/index.html";
  }
}