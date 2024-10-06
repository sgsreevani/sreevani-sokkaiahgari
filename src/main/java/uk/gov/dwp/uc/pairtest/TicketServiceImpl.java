package uk.gov.dwp.uc.pairtest;

import org.springframework.beans.factory.annotation.Autowired;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

    private static final int ADULT_TICKET_PRICE = 25 ;
    private static final int CHILD_TICKET_PRICE = 15 ;
    @Autowired
    private TicketPaymentService paymentService;

    @Autowired
    private SeatReservationService reservationService;

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        // Validate account ID
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account ID, please provide valid account ID.");
        }

        // Validating ticket request
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("No ticket requests provided, please provide number of tickets to purchase.");
        }

        // Calculating tickets count
        int totalNoOfTickets = 0;
        int adultTickets = 0;
        int childTickets = 0;
        int infantTickets = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request != null) {
                switch (request.getTicketType()) {
                    case ADULT:
                        adultTickets += request.getNoOfTickets();
                        break;
                    case CHILD:
                        childTickets += request.getNoOfTickets();
                        break;
                    case INFANT:
                        infantTickets += request.getNoOfTickets();
                        break;
                }
                totalNoOfTickets += request.getNoOfTickets();
            }
        }


        if (totalNoOfTickets > 25) {
            throw new InvalidPurchaseException("Sorry, your tickets count exceeded 25, please purchase less tickets.");
        }


        if ((childTickets > 0 || infantTickets > 0) && adultTickets == 0) {
            throw new InvalidPurchaseException("Sorry, child or infant tickets cannot be purchased without an adult ticket.");
        }

        // Calculate total price
        int totalPrice = (adultTickets * ADULT_TICKET_PRICE) + (childTickets * CHILD_TICKET_PRICE);

        int totalSeatsToReserve = adultTickets + childTickets;

        // Calling thirdparty methods
        paymentService.makePayment(accountId, totalPrice);

        reservationService.reserveSeat(accountId, totalSeatsToReserve);
    }



}
