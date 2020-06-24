package edu.duke.rs.baseProject.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

public class UpdatedEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {
  private static final long serialVersionUID = 1L;

  public UpdatedEvent(T source) {
    super(source);
  }

  @Override
  public ResolvableType getResolvableType() {
    return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getSource()));
  }

}
