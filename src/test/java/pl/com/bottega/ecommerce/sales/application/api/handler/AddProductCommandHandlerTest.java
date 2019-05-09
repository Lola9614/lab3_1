package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTest {

    ProductRepository productRepository;
    SuggestionService suggestionService;
    ClientRepository clientRepository;
    ReservationRepository reservationRepository;
    Product product;
    AddProductCommand addProductCommand;
    SystemContext systemContext;
    Reservation reservation;
    AddProductCommandHandler addProductCommandHandler;
    Client client = new Client();

    @Before
    public void setup() {

        productRepository = mock(ProductRepository.class);
        suggestionService = mock(SuggestionService.class);
        clientRepository = mock(ClientRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        systemContext = new SystemContext();
        addProductCommandHandler = new AddProductCommandHandlerBuilder().withClientRepository(clientRepository)
                                                                        .withProductRepository(productRepository)
                                                                        .withReservationRepository(reservationRepository)
                                                                        .withSuggestionService(suggestionService)
                                                                        .withSystemContext(systemContext)
                                                                        .bulid();

        product = new Product(new Id("product_ID"),new Money(new BigDecimal(8)),"productTestName11",ProductType.STANDARD);
        reservation = new Reservation(new Id("reservation_ID"), Reservation.ReservationStatus.OPENED, new ClientData(new Id("client_ID"),"user"),new Date(1220227200));
        addProductCommand = new AddProductCommand(new Id("reservation_ID"),new Id("product_ID"),1);

    }

    @Test
    public void shouldAddSuggestedProductToReservationIfFirstIsNotAvailable() {

        Product product2 = new Product(new Id("product_ID"),new Money(new BigDecimal(8)),"productTestName22",ProductType.STANDARD);

        when(reservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);
        when(productRepository.load(addProductCommand.getProductId())).thenReturn(product);
        product.markAsRemoved();
        when(clientRepository.load(systemContext.getSystemUser().getClientId())).thenReturn(client);
        when(suggestionService.suggestEquivalent(product,client)).thenReturn(product2);

        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(reservationRepository).save(reservation);
        assertThat(reservation.contains(product2), is(true));
    }

    @Test
    public void shouldNotCallSuqqestMethodIfProductIsAvailable() {


        when(reservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);
        when(productRepository.load(addProductCommand.getProductId())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(reservationRepository).save(reservation);
        Mockito.verify(suggestionService, never()).suggestEquivalent(product, new Client());
    }
}
