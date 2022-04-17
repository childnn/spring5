// package org.anonymous.test.tx;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
//
// /**
//  * ~~ Talk is cheap. Show me the code. ~~ :-)
//  *
//  * @author MiaoOne
//  * @since 2022/03/09
//  */
// // @RestController
// // @RequestMapping("test")
// public class TestController {
//
//     @Autowired
//     private TestService testService;
//
//
//     // @PostMapping("thisTx")
//     public ResponseMessage useThis(@RequestParam("name") String name, @RequestParam("id") String id) {
//         testService.update(name, id);
//
//         return ResponseMessage.success();
//     }
//
//
//     @PostMapping("serviceTx")
//     public ResponseMessage serviceTx(@RequestParam("name") String name, @RequestParam("id") String id) {
//         testService.update2(name, id);
//
//         return ResponseMessage.success();
//     }
//
// }
