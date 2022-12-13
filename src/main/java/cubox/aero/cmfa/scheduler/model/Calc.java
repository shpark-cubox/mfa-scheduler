package cubox.aero.cmfa.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calc {
    private int idCalc;
    private int idCustomer;
    private int idServ;
    private int usePrice;
    private int calcPrice;
    private String useDate;
    private String useYn;
    private String publishDate;
    private String registerDate;
    private String updateDate;
}
