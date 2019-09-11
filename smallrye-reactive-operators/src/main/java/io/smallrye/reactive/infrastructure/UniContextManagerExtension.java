package io.smallrye.reactive.infrastructure;

import org.eclipse.microprofile.context.ThreadContext;
import org.eclipse.microprofile.context.spi.ContextManager;
import org.eclipse.microprofile.context.spi.ContextManagerExtension;

public class UniContextManagerExtension implements ContextManagerExtension {

    @Override
    public void setup(ContextManager manager) {
        ThreadContext threadContext = manager.newThreadContextBuilder().build();
        Infrastructure.setCompletableFutureWrapper(cf -> threadContext.withContextCapture(cf));
    }

}
