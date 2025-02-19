package com.bank.demo.controller;
import com.bank.demo.entities.Account;
import com.bank.demo.service.*;

import org.springframework.ui.Model;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BankController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping("/dashboard")
	public String dashboard(Model model)
	{
		String username=SecurityContextHolder.getContext().getAuthentication().getName();
		Account account=accountService.findAccountByUsername(username);
		model.addAttribute("account",account);
		return "dashboard";
	}
	
	@GetMapping("/register")
	public String showRegistrationForm()
	{
		return "register";
	}
	
	@PostMapping("/register")
	public String registerAccount(@RequestParam String username,@RequestParam String password, Model model )
	{
		   try
		   {
			   accountService.registerAccount(username, password);
			   return "redirect:/login";
		   }
		   catch(Exception e)
		   {
			   model.addAttribute("error",e.getMessage());
			   return "/register";
		   }
	}
	
	@GetMapping("/login")
	public String login()
	{
		return "login";
	}
	
	@GetMapping("/deposit")
	public String deposit()
	{
		return "redirect:/dashboard";
	}
	
	@PostMapping("/deposit")
	public String deposit(@RequestParam BigDecimal amount)
	{
		String username=SecurityContextHolder.getContext().getAuthentication().getName();
		Account account=accountService.findAccountByUsername(username);
		accountService.deposit(account,amount);
		return "redirect:/deposit";
	}
	
	@PostMapping("withdraw")
	public String withdrawl(@RequestParam BigDecimal amount,Model model)
	{
		String username=SecurityContextHolder.getContext().getAuthentication().getName();
		Account account=accountService.findAccountByUsername(username);
		try
		{
			accountService.withdraw(account, amount);
		}
		catch(Exception e)
		{
			model.addAttribute("error",e.getMessage());
			model.addAttribute("account",account);
			return "dashboard";
		}
		return "redirect:/dashboard";
	}
	
	@GetMapping("/transactions")
	public String transactionHistory(Model  model)
	{
		String username=SecurityContextHolder.getContext().getAuthentication().getName();
		Account account=accountService.findAccountByUsername(username);
		model.addAttribute("transactions",accountService.getTransactionHistory(account));
		return "transaction";
	}
	
	@PostMapping("/transfer")
	public String transferAmount(@RequestParam BigDecimal amount,@RequestParam String tousername ,Model model)
	{
		String username=SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(username);
		Account fromaccount=accountService.findAccountByUsername(username);
		try
		{
			accountService.transferAmount(fromaccount, tousername, amount);
		}
		catch(Exception e)
		{
			model.addAttribute("error",e.getMessage());
			model.addAttribute("account",fromaccount);
			return "dashboard";
		}
		return "redirect:/dashboard";
	}
	
}
