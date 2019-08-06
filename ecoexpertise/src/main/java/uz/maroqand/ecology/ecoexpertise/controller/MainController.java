package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysUrls;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Controller
public class MainController {

    private final String apiKey = "800D00EAA115F8BB0D5083ECBDEC6ABDF9124235BDD6C2CD558CD686B583743524FB947AF54B11A6A98D3A303CF14CF6D86BD71C975E95B45E34D07F327674CA";

    @RequestMapping("/")
    public String getPage(Model model) throws Exception {


        String url = "http://eco.gov.uz/uz/api/post/get";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("POST");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine="";
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        model.addAttribute("element",response.toString());
        return "index";
    }

    @RequestMapping("/login")
    public String getLoginPage(Model model) {

        model.addAttribute("api_key", apiKey);
        model.addAttribute("api_key_domain", "eco-service.uz");
        return "login";
    }

    @RequestMapping("/dashboard")
    public String getDashboardPage() {
        System.out.println("--dashboard");
        return "dashboard";
    }

    @RequestMapping("/news")
    public String getNewsPage(
            Model model,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "create") String create,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "image") String image
    ) {
//        System.out.println(elem);
        model.addAttribute("create",create);
        model.addAttribute("title",title);
        model.addAttribute("img_source",image);
        model.addAttribute("newsText",content);
        System.out.println("--news");
        return "news";
    }
    
    @RequestMapping("/logout")
    public String logout(){
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

    @RequestMapping(SysUrls.ErrorInternalServerError)
    public String getError500Page() {
        return SysTemplates.ErrorInternalServerError;
    }

    @RequestMapping(SysUrls.ErrorNotFound)
    public String getError404Page() {
        return SysTemplates.ErrorNotFound;
    }

    @RequestMapping(SysUrls.ErrorForbidden)
    public String getError403Page() {
        return SysTemplates.ErrorForbidden;
    }

}
