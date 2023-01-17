package shop.mtcoding.orange.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import shop.mtcoding.orange.model.Product;
import shop.mtcoding.orange.model.ProductRepository;

@Controller
public class ProductController {

    @Autowired // type으로 찾아냄, request 사용x -> 값의 중복 때문에
    private ProductRepository productRepository;

    @Autowired
    private HttpSession session;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/redirect")
    public void main(HttpServletRequest request, HttpServletResponse response) throws IOException {
        session = request.getSession(); // Autowired 때문에 HttpSession 필요x
        session.setAttribute("name", "session metacoding");
        request.setAttribute("name", "metacoding");
        response.sendRedirect("/test");
    }

    @GetMapping("/dispatcher")
    public void dispatcher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        session = request.getSession();
        request.setAttribute("name", "metacoding");
        RequestDispatcher dis = request.getRequestDispatcher("/test");
        dis.forward(request, response);
    }

    @GetMapping({ "/", "/product" })
    public String findAll(Model model) { // model = request
        List<Product> productList = productRepository.findAll();
        model.addAttribute("productList", productList);
        return "product/main"; // request 새로 만들어짐 - 덮어쒸움(프레임워크)
    }

    @GetMapping("/product/{id}")
    public String findOne(@PathVariable int id, Model model) {
        Product product = productRepository.findOne(id);
        model.addAttribute("product", product);
        return "product/detail";
    }

    @GetMapping("/product/addForm")
    public String addForm() {
        return "product/addForm";
    }

    @PostMapping("/product/add")
    public String add(String name, int price, int qty) {
        int result = productRepository.insert(name, price, qty);
        if (result == 1) {
            return "redirect:/product";
        } else {
            return "redirect:product/addForm";
        }
    }

    @PostMapping("/product/delete/{id}")
    public String delete(@PathVariable int id) {
        int result = productRepository.delete(id);
        if (result == 1) {
            return "redirect:/";
        } else {
            return "redirect:/product/" + id;
        }
    }

    @GetMapping("product/{id}/updateForm")
    public String updateForm(@PathVariable int id, Model model) {
        Product product = productRepository.findOne(id);
        model.addAttribute("product", product);
        return "product/updateForm";
    }

    @PostMapping("/product/{id}/update")
    public String update(@PathVariable int id, String name, int price, int qty) {
        // 레파지토리.update 메소드 호출
        // 결과받기(1,-1)
        int result = productRepository.update(id, name, price, qty);
        // 성공 -> 상세페이지
        // 실패 -> 수정페이지
        if (result == 1) {
            return "redirect:/product/" + id;
        } else {
            return "redirect:/product/" + id + "/updateForm";
        }
    }
    // @GetMapping("/api/product")
    // @ResponseBody
    // public List<Product> apiFindAllproduct() {
    // List<Product> productList = productRepository.findAll();
    // return productList;
    // }

    // @GetMapping("/api/product/{id}")
    // @ResponseBody
    // public Product apiFindOneproduct(@PathVariable int id) {
    // Product product = productRepository.findOne(id);
    // return product;
    // }
}
