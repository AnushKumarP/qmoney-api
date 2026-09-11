package com.anushkumar.qmoney;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

  @GetMapping("/sample")
  public PortfolioResponse sample() {
    return calculate(new PortfolioRequest(LocalDate.of(2024, 1, 2), List.of(
        new TradeInput("AAPL", LocalDate.of(2019, 1, 2), 39.48, 185.64),
        new TradeInput("MSFT", LocalDate.of(2019, 1, 2), 101.12, 370.87),
        new TradeInput("GOOGL", LocalDate.of(2019, 1, 2), 52.73, 138.17))));
  }

  @PostMapping("/returns")
  @ResponseStatus(HttpStatus.OK)
  public PortfolioResponse returns(@Valid @RequestBody PortfolioRequest request) {
    return calculate(request);
  }

  private PortfolioResponse calculate(PortfolioRequest request) {
    List<ReturnResult> results = request.trades().stream()
        .map(trade -> calculateReturn(trade, request.endDate()))
        .sorted(Comparator.comparing(ReturnResult::annualizedReturn).reversed())
        .toList();
    return new PortfolioResponse(request.endDate(), results);
  }

  private ReturnResult calculateReturn(TradeInput trade, LocalDate endDate) {
    long days = ChronoUnit.DAYS.between(trade.purchaseDate(), endDate);
    if (days <= 0) {
      throw new IllegalArgumentException("endDate must be after the purchase date for " + trade.symbol());
    }
    double totalReturn = (trade.sellPrice() - trade.buyPrice()) / trade.buyPrice();
    double years = days / 365.0;
    double annualizedReturn = Math.pow(1 + totalReturn, 1 / years) - 1;
    return new ReturnResult(trade.symbol().toUpperCase(), annualizedReturn, totalReturn);
  }

  public record PortfolioRequest(@NotNull LocalDate endDate, @NotEmpty List<@Valid TradeInput> trades) {}
  public record TradeInput(@NotBlank String symbol, @NotNull LocalDate purchaseDate,
                           @Positive double buyPrice, @Positive double sellPrice) {}
  public record ReturnResult(String symbol, double annualizedReturn, double totalReturn) {}
  public record PortfolioResponse(LocalDate endDate, List<ReturnResult> returns) {}
}
