package cubox.aero.cmfa.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServDtl {

    private int idCustomer;
    private String customerName;
    private int idServ;
    private String servName;
    private String servType;

    // 정책 설정 값
    private String payType;
    private int payValue;
    private int servExp;
    private int useCount;
    private int maxCount;
    private int calcDate;
    private String calcCycle;
    private String useYn;
    private String startDate;
    private String registerDate;
    private String updateDate;
}