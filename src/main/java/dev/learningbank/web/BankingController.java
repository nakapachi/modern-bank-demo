package dev.learningbank.web;

import dev.learningbank.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
public class BankingController {
    private final BankingService banking;
    public BankingController(BankingService banking) { this.banking = banking; }

    @GetMapping("/")
    String home(Authentication auth) {
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return admin ? "redirect:/admin" : "redirect:/accounts";
    }
    @GetMapping("/login") String login() { return "login"; }

    @GetMapping("/accounts")
    String accounts(Authentication auth, Model model) {
        model.addAttribute("accounts", banking.accountsFor(auth.getName()));
        model.addAttribute("loginName", auth.getName());
        return "accounts";
    }

    @GetMapping("/accounts/{id}")
    String account(@PathVariable Long id, Authentication auth, Model model) {
        model.addAttribute("account", banking.accountFor(id, auth.getName()));
        model.addAttribute("loginName", auth.getName());
        return "account";
    }

    @GetMapping("/accounts/{id}/{operation:deposit|withdraw|transfer}")
    String operation(@PathVariable Long id, @PathVariable String operation,
                     Authentication auth, Model model) {
        model.addAttribute("account", banking.accountFor(id, auth.getName()));
        model.addAttribute("loginName", auth.getName());
        model.addAttribute("operation", operation);
        return "transaction-form";
    }

    @GetMapping("/accounts/{id}/history")
    String history(@PathVariable Long id, Authentication auth, Model model) {
        model.addAttribute("account", banking.accountFor(id, auth.getName()));
        model.addAttribute("entries", banking.historyFor(id, auth.getName()));
        model.addAttribute("loginName", auth.getName());
        return "history";
    }

    @PostMapping("/accounts/{id}/deposit")
    String deposit(@PathVariable Long id, @RequestParam BigDecimal amount, Authentication auth, RedirectAttributes flash) {
        return execute(id, "deposit", flash, () -> banking.deposit(id, auth.getName(), amount), "入金しました。");
    }

    @PostMapping("/accounts/{id}/withdraw")
    String withdraw(@PathVariable Long id, @RequestParam BigDecimal amount, Authentication auth, RedirectAttributes flash) {
        return execute(id, "withdraw", flash, () -> banking.withdraw(id, auth.getName(), amount), "出金しました。");
    }

    @PostMapping("/accounts/{id}/transfer")
    String transfer(@PathVariable Long id, @RequestParam String destination, @RequestParam BigDecimal amount,
                    Authentication auth, RedirectAttributes flash) {
        return execute(id, "transfer", flash, () -> banking.transfer(id, auth.getName(), destination.trim(), amount), "振込が完了しました。");
    }

    private String execute(Long id, String operation, RedirectAttributes flash, Runnable action, String success) {
        try {
            action.run();
            flash.addFlashAttribute("success", success);
        } catch (BankingException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/accounts/" + id + "/" + operation;
    }
}
