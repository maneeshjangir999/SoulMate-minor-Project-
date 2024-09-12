package com.soulmate.Controller;

import com.soulmate.Entites.UserInfo;
import com.soulmate.Exceptions.UserAlreadyExistsException;
import com.soulmate.Repository.UserRepository;
import com.soulmate.Services.CustomUserService;
import com.soulmate.Services.EmailService;
import com.soulmate.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UserController {

   private  final UserService userService;

   private final CustomUserService customUserService;
   private  String generatedOtp;

   private final EmailService emailService;

   private final HttpSession session;
   private final UserRepository userRepository;
    public UserController(UserService userService, CustomUserService customUserService, EmailService emailService, HttpSession session, UserRepository userRepository) {
        this.userService = userService;
        this.customUserService = customUserService;
        this.emailService = emailService;
        this.session = session;
        this.userRepository = userRepository;
    }
//    @GetMapping("/")
//    public String homepage(Model model,Authentication authentication){
//        if(authentication!=null &&  authentication.isAuthenticated()){
//            System.out.println(authentication.getName());
//            UserDetails userDetails=(UserDetails) authentication.getPrincipal();
//            String email =userDetails.getUsername();
//            System.out.println(STR."username is\{email}");
//            Optional<UserInfo> user= userRepository.findByEmail(email);
//            if(user.isPresent()){
//                UserInfo userInfo=user.get();
//                model.addAttribute("firstname",userInfo.getFirstname());
//                model.addAttribute("userInfo",userInfo);
//            }
//        }
//        return "home";
//    }
@GetMapping("/")
public String homepage(Model model, Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String firstname = userDetails.getUsername();
        Optional<UserInfo> user = userService.FindByUsername(firstname);
        if (user.isPresent()) {
            UserInfo userInfo = user.get();
            model.addAttribute("userInfo", userInfo);
        }
    }
    return "home";
}


    @GetMapping("/profile")
    public String profilePage(Model model,UserInfo userInfo,Authentication authentication){

//        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails userDetails)){
//            return "form";
//        }
        return "profile";
    }
    @GetMapping("/form")
    public String loginpage(Model model, HttpSession session,RedirectAttributes redirectAttributes){
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        if(userInfo!=null){
            model.addAttribute("userInfo",userInfo);
        }
        else {
            model.addAttribute("userInfo", new UserInfo());
        }
//
//       return "form";
        model.addAttribute("verified", model.asMap().get("verified"));

        model.addAttribute("error", model.asMap().get("error"));
        model.addAttribute("success", model.asMap().get("success"));
        return "form";
    }

    @PostMapping("/logout")
    public String logoutUser(){
        return "home";
    }

    @GetMapping("/help")
    public  String help(){
        return "check";
    }

    @GetMapping("/register")
    public String registerUser(Model model){
        model.addAttribute("userInfo", new UserInfo());
        return "register";
    }

    @GetMapping("/otppage")
    public String verifyotp(Model model){
        UserInfo userInfo =(UserInfo) session.getAttribute("userInfo");
        if(userInfo==null){
            return "redirect:/register";
        }
        model.addAttribute("userInfo",userInfo);
        return "otpVerification";
    }

    @PostMapping("/save")
    public String registerUser(@Valid @ModelAttribute("userInfo") UserInfo userInfo, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        try{
            String otp=emailService.generateOtp();
            System.out.println(STR."generated otp" +otp);
            System.out.println(generatedOtp);
            session.setAttribute("otp",otp);
            session.setAttribute("userInfo",userInfo);
            emailService.sendOtp(userInfo.getEmail(),otp);
            return "redirect:/otppage";
        }catch (UserAlreadyExistsException e){
            redirectAttributes.addFlashAttribute("userpresent","User already exists");
            return "redirect:/register";
        }
    }
    @PostMapping("/login")
    public String loginUSer(@ModelAttribute("userInfo") UserInfo userInfo , Model model, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "form";
        }
        boolean isAuthenticate = customUserService.loginUser(userInfo.getEmail(), userInfo.getPassword());
        if (isAuthenticate) {
//            Authentication authentication = SecurityContextHolder .getContext().getAuthentication();
//            UserDetails userDetails=
            Optional<UserInfo> logedInUser= userRepository.findByEmail(userInfo.getEmail());

            if(logedInUser.isPresent()){
                UserInfo userInfo1= logedInUser.get();
                session.setAttribute("userInfo",userInfo1);
                model.addAttribute("userInfo",userInfo1);
                return "home";
            }else{
                System.out.println("User not found in database ");
                model.addAttribute("loginError","user not found");
                return "form";
            }
        }
        else {
            model.addAttribute("loginError", "Email or password is incorrect");
            return "form";
        }

    }

//    @PostMapping("/login")
//    public String loginUser(@ModelAttribute UserInfo userInfo,
//                            BindingResult result,Model model,
//                            RedirectAttributes redirectAttributes,
//                            HttpServletRequest request) {
//        if (result.hasErrors()) {
//            model.addAttribute("loginError","Email or Password is incorrect");
//            return "redirect:/form";
//        }
//
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(userInfo.getEmail(), userInfo.getPassword());
//
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Redirect to the home page after successful login
//        return "redirect:/";
//    }


    @PostMapping("/verifyotp")
        public String verifyOtp(@RequestParam("email") String email,@RequestParam("otp") String otp,Model model, RedirectAttributes redirectAttributes ){
        String sessionOtp=(String) session.getAttribute("otp");
        UserInfo userInfo=(UserInfo) session.getAttribute("userInfo");

            System.out.println(STR."Email\{email}");
            System.out.println(STR."Enterd otp\{otp}");
            System.out.println(STR."Session otp\{sessionOtp}");
            System.out.println(STR."UserInfo from Session\{userInfo}");


            if(userInfo==null && sessionOtp==null){
                redirectAttributes.addFlashAttribute("sessionerror","The Session has been expired please try again letter");
                System.out.println("Session Expired Redirecting to register page");
                return "redirect:/register";
            }
            if(otp.equals(sessionOtp)){
                System.out.println("Otp Matches redirecting form page");
                System.out.println(userInfo);

                session.removeAttribute("otp");
                session.removeAttribute("userInfo");
                userService.createUser(userInfo);
                System.out.println(userInfo);

                redirectAttributes.addFlashAttribute("verified","Email Verified Successfully");
                System.out.println(userInfo);
                return "redirect:/form";
            }
            else if(!otp.equals((sessionOtp))){
                redirectAttributes.addFlashAttribute("error","Invalid otp, please try again");
                System.out.println("Invalid otp Redirecting to Otp Verification page ");
                return "redirect:/otppage";
            }

            if (!otp.equals(sessionOtp)) {
                redirectAttributes.addFlashAttribute("success", "Registration complete");
                return "redirect:/form";
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid otp");
                return "redirect:/otppage";

            }
        }
}


