package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    BookKeeper bookKeeper;
    InvoiceRequest invoiceRequest;
    ProductData productData;
    TaxPolicy taxPolicy;

    @Before
    public void setup(){
        bookKeeper = new BookKeeper(new InvoiceFactory());
        invoiceRequest = new InvoiceRequest(null);
        productData = new ProductData(new Id("1"),new Money(new BigDecimal(1.0)),"testname",ProductType.STANDARD, Date.from(Instant.now()));
        invoiceRequest.add(new RequestItem(productData,1,new Money(1)));
        taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class)))
                .thenReturn(new Tax(new Money(1),null));

    }

    @Test
    public void shouldReturnOneInvoiceIfOneIsRequested() {

        Invoice responseInvoice = bookKeeper.issuance(invoiceRequest,taxPolicy);
        assertThat(responseInvoice.getItems().size(),is(1));
    }

    @Test
    public void shouldReturnProperNetValue() {
        Invoice responseInvoice = bookKeeper.issuance(invoiceRequest,taxPolicy);
        assertThat(responseInvoice.getNet(),is(new Money(1)));
    }


    @Test
    public void shouldReturnProperQuantityValue() {

        Invoice responseInvoice = bookKeeper.issuance(invoiceRequest,taxPolicy);
        assertThat(responseInvoice.getItems().get(0).getQuantity(),is(1));
    }

    @Test
    public void shouldCallTwoTimesCalculateMethodIfTwoTimesRequested() {

        invoiceRequest.add(new RequestItem(productData,1,new Money(1)));
        bookKeeper.issuance(invoiceRequest,taxPolicy);

        Mockito.verify(taxPolicy,Mockito.times(2)).calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class));
    }

    @Test
    public void shouldCallOnceGetTypeMethodIfOneIsRequested() {

        bookKeeper.issuance(invoiceRequest,taxPolicy);
        Mockito.verify(taxPolicy,Mockito.times(1)).calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class));
    }

    @Test
    public void shouldNotCallCalculateMethodIfItemIsZero() {

        InvoiceRequest invoiceRequest = new InvoiceRequest(null);
        bookKeeper.issuance(invoiceRequest,taxPolicy);

        verify(taxPolicy,Mockito.times(0)).calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class));
    }
}
