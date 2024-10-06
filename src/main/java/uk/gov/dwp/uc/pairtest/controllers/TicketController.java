package uk.gov.dwp.uc.pairtest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;


    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseTickets(@RequestParam long accountId,
                                                  @RequestBody TicketTypeRequest... ticketTypeRequests) {
        try {
            ticketService.purchaseTickets(accountId, ticketTypeRequests);
            return new ResponseEntity<>("Tickets purchased successfully!", HttpStatus.OK);
        } catch (InvalidPurchaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while processing the purchase.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


