package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    @Test
    public void shouldReturnOneInvoiceIfOneIsRequested() {

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);

        ProductData productData = Mockito.mock(ProductData.class);
        Mockito.when(productData.getType()).thenReturn(ProductType.STANDARD);

        invoiceRequest.add(new RequestItem(productData,1,new Money(1)));

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class)))
                .thenReturn(new Tax(new Money(1),null));

        Invoice responseInvoice = bookKeeper.issuance(invoiceRequest,taxPolicy);

        assertThat(responseInvoice.getItems().size(),is(1));
    }

    @Test
    public void shouldReturnProperNetValue() {

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);

        ProductData productData = Mockito.mock(ProductData.class);
        Mockito.when(productData.getType()).thenReturn(ProductType.STANDARD);

        invoiceRequest.add(new RequestItem(productData,1,new Money(1)));

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class)))
                .thenReturn(new Tax(new Money(1),null));

        Invoice responseInvoice = bookKeeper.issuance(invoiceRequest,taxPolicy);

        assertThat(responseInvoice.getNet(),is(1));
    }


    @Test
    public void shouldReturnProperQuantityValue() {

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);

        ProductData productData = Mockito.mock(ProductData.class);
        Mockito.when(productData.getType()).thenReturn(ProductType.STANDARD);

        invoiceRequest.add(new RequestItem(productData,1,new Money(1)));

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class)))
                .thenReturn(new Tax(new Money(1),null));

        Invoice responseInvoice = bookKeeper.issuance(invoiceRequest,taxPolicy);

        assertThat(responseInvoice.getItems().get(0).getQuantity(),is(new Money(2)));
    }

    @Test
    public void shouldCallTwoTimesCalculateMethodIfTwoTimesRequested() {

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);

        ProductData productData = Mockito.mock(ProductData.class);
        Mockito.when(productData.getType()).thenReturn(ProductType.STANDARD);

        invoiceRequest.add(new RequestItem(productData,1,new Money(1)));
        invoiceRequest.add(new RequestItem(productData,1,new Money(1)));

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class)))
                .thenReturn(new Tax(new Money(1),null));

        bookKeeper.issuance(invoiceRequest,taxPolicy);

        Mockito.verify(taxPolicy,Mockito.times(2)).calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class));
    }

    @Test
    public void shouldCallTwoTimesGetTypeMethodIfTwoTimesRequested() {

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);

        ProductData productData = Mockito.mock(ProductData.class);
        Mockito.when(productData.getType()).thenReturn(ProductType.STANDARD);

        invoiceRequest.add(new RequestItem(productData,1,new Money(1)));
        invoiceRequest.add(new RequestItem(productData,1,new Money(1)));

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class)))
                .thenReturn(new Tax(new Money(1),null));

        bookKeeper.issuance(invoiceRequest,taxPolicy);

        Mockito.verify(productData,Mockito.times(2)).getType();
    }
}
