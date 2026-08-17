package dev.learningbank.web;

import dev.learningbank.domain.*;
import dev.learningbank.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService admin;

    public AdminController(AdminService admin) { this.admin = admin; }

    @GetMapping
    String dashboard(Authentication auth, Model model) {
        common(auth, model);
        model.addAttribute("customerCount", admin.customerCount());
        model.addAttribute("accountCount", admin.accountCount());
        model.addAttribute("frozenAccountCount", admin.frozenAccountCount());
        return "admin/dashboard";
    }

    @GetMapping("/customers")
    String customers(@RequestParam(required = false) String q, Authentication auth, Model model) {
        common(auth, model);
        model.addAttribute("customers", admin.customers(q));
        model.addAttribute("q", q == null ? "" : q);
        return "admin/customers";
    }

    @GetMapping("/customers/new")
    String newCustomer(Authentication auth, Model model) {
        common(auth, model);
        return "admin/customer-new";
    }

    @PostMapping("/customers")
    String createCustomer(@RequestParam String customerNumber, @RequestParam String username,
                          @RequestParam String password, @RequestParam String displayName,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String phone,
                          RedirectAttributes flash) {
        try {
            AppUser created = admin.createCustomer(customerNumber, username, password, displayName, email, phone);
            flash.addFlashAttribute("success", "顧客を登録しました。");
            return "redirect:/admin/customers/" + created.getId();
        } catch (BankingException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/customers/new";
        }
    }

    @GetMapping("/customers/{id}")
    String customer(@PathVariable Long id, Authentication auth, Model model) {
        common(auth, model);
        model.addAttribute("customer", admin.customer(id));
        model.addAttribute("accounts", admin.accountsFor(id));
        model.addAttribute("statuses", CustomerStatus.values());
        model.addAttribute("accountTypes", AccountType.values());
        model.addAttribute("accountStatuses", AccountStatus.values());
        model.addAttribute("branches", admin.branches());
        return "admin/customer-detail";
    }

    @PostMapping("/customers/{id}")
    String updateCustomer(@PathVariable Long id, @RequestParam String displayName,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String phone,
                          @RequestParam CustomerStatus status, RedirectAttributes flash) {
        return execute("redirect:/admin/customers/" + id, flash,
            () -> admin.updateCustomer(id, displayName, email, phone, status), "顧客情報を更新しました。");
    }

    @PostMapping("/customers/{id}/accounts")
    String openAccount(@PathVariable Long id, @RequestParam AccountType accountType,
                       @RequestParam String branchCode,
                       @RequestParam(defaultValue = "0") BigDecimal initialBalance,
                       RedirectAttributes flash) {
        return execute("redirect:/admin/customers/" + id, flash,
            () -> admin.openAccount(id, accountType, branchCode, initialBalance), "口座を開設しました。");
    }

    @GetMapping("/branches")
    String branches(Authentication auth, Model model) {
        common(auth, model);
        model.addAttribute("branches", admin.branches());
        return "admin/branches";
    }

    @PostMapping("/customers/{customerId}/accounts/{accountId}/status")
    String accountStatus(@PathVariable Long customerId, @PathVariable Long accountId,
                         @RequestParam AccountStatus status, RedirectAttributes flash) {
        return execute("redirect:/admin/customers/" + customerId, flash,
            () -> admin.changeAccountStatus(accountId, status), "口座状態を更新しました。");
    }

    private void common(Authentication auth, Model model) { model.addAttribute("loginName", auth.getName()); }

    private String execute(String redirect, RedirectAttributes flash, Runnable action, String success) {
        try {
            action.run();
            flash.addFlashAttribute("success", success);
        } catch (BankingException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return redirect;
    }
}
