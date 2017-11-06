package com.fgecctv.trumpet.shell.data.ad.repository;

import com.fgecctv.trumpet.shell.data.ad.Repeat;

import java.util.List;

public class FakeBuilder extends Ad.Builder {
    @Override
    public FakeBuilder setId(String id) {
        super.setId(id);
        return this;
    }

    @Override
    public FakeBuilder setEffectiveDate(long effectiveDate) {
        super.setEffectiveDate(effectiveDate);
        return this;
    }

    @Override
    public FakeBuilder setExpiryDate(long expiryDate) {
        super.setExpiryDate(expiryDate);
        return this;
    }

    @Override
    public FakeBuilder setRepeats(List<Repeat> repeats) {
        super.setRepeats(repeats);
        return this;
    }

    @Override
    public FakeBuilder setPriority(long priority) {
        super.setPriority(priority);
        return this;
    }
}
