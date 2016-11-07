package tw.com.softleader.starter_snippets.demo.service;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import tw.com.softleader.starter_snippets.demo.dao.DemoDao;
import tw.com.softleader.starter_snippets.demo.entity.Demo;
import tw.com.softleader.commons.validation.constraints.IdNo;
import tw.com.softleader.data.dao.CrudCodeDao;
import tw.com.softleader.domain.AbstractCrudCodeService;
import tw.com.softleader.domain.exception.ValidationException;
import tw.com.softleader.domain.guarantee.constraints.EntityUnique;
import tw.com.softleader.domain.guarantee.constraints.EntityUpToDate;

/**
 * @see https://github.com/softleader/softleader-framework-docs/wiki/Entity-Guarantee
 */
@Service
@Validated
public class DemoService extends AbstractCrudCodeService<Demo, Long> {

  @Autowired
  private DemoDao demoDao;

  @Override
  public CrudCodeDao<Demo, Long> getDao() {
    return demoDao;
  }

  @Override
  public Demo save(@EntityUnique @EntityUpToDate Demo entity) throws ValidationException {
    return super.save(entity);
  }

  public void doSomeBusinessLogic(@NotNull @IdNo String idno) {
    // just no-op for demo
  }

}