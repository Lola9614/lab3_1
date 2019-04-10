package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTest {

    ProductRepository productRepository = mock(ProductRepository.class);
    SuggestionService suggestionService = mock(SuggestionService.class);
    ClientRepository clientRepository = mock(ClientRepository.class);
    ReservationRepository reservationRepository = mock(ReservationRepository.class);
    Product product = mock(Product.class);
    AddProductCommand addProductCommand = mock(AddProductCommand.class);
    ArgumentCaptor<Reservation> reservation = ArgumentCaptor.forClass(Reservation.class);
    SystemContext systemContext = mock(SystemContext.class);
    Reservation reservation1 = mock(Reservation.class);


    @Test
    public void shouldNotAddProductToReservationIfItIsNotAvailable() {

        when(productRepository.load(any())).thenReturn(product);
        when(product.isAvailable()).thenReturn(false);
        when(reservationRepository.load(any())).thenReturn(reservation1);
        when(clientRepository.load(any())).thenReturn(new Client());
        when(systemContext.getSystemUser()).thenCallRealMethod();

        AddProductCommandHandler addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository, suggestionService,
                clientRepository, systemContext);


        addProductCommandHandler.handle(addProductCommand);


        Mockito.verify(reservationRepository).save(reservation.capture());
        assertThat(reservation.getAllValues().contains(product),is(false));
    }
}
