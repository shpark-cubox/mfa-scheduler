package cubox.aero.cmfa.scheduler.repository;

import cubox.aero.cmfa.scheduler.model.Calc;
import cubox.aero.cmfa.scheduler.model.CustomerServDtl;
import cubox.aero.cmfa.scheduler.model.CustomerTransaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalcRepository {

    List<CustomerServDtl> srvDtlList(CustomerServDtl customerServDtl) throws Exception;

    void updateCstSrvDtl(CustomerServDtl customerServDtl) throws Exception;

    void insertCalc(Calc calc);

    int getSuccessTransaction(CustomerTransaction customerTransaction);
}
