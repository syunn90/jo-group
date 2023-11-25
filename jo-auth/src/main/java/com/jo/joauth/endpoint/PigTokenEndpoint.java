//package com.jo.joauth.endpoint;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.ModelAndView;
//
///**
// * @author xtc
// * @date 2023/11/20
// */
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/token")
//public class PigTokenEndpoint {
//
//
//    /**
//     * 认证页面
//     * @param modelAndView
//     * @param error 表单登录失败处理回调的错误信息
//     * @return ModelAndView
//     */
//    @GetMapping("/login")
//    public ModelAndView require(ModelAndView modelAndView, @RequestParam(required = false) String error) {
//        modelAndView.setViewName("ftl/login");
//        modelAndView.addObject("error", error);
//        return modelAndView;
//    }
//}
