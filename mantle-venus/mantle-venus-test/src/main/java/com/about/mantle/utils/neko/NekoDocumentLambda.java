package com.about.mantle.utils.neko;

import java.util.function.Supplier;

public class NekoDocumentLambda<T> extends NekoDocument
{
    private final Supplier<? extends T> supplier;
    public NekoDocumentLambda(Supplier<? extends T> supplier)
    {
        this.supplier = supplier;
    }

    @Override
    public String getNeko()
    {
        return (String) this.supplier.get();
    }
}

