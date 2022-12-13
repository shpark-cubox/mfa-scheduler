package cubox.aero.cmfa.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTransaction {
    private int idCustomer;
    private int idServ;
    private String transactionStartDate;
    private String transactionEndDate;
}

