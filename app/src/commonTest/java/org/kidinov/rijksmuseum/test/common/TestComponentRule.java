package org.kidinov.rijksmuseum.test.common;

import android.content.Context;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.kidinov.rijksmuseum.App;
import org.kidinov.rijksmuseum.data.DataManager;
import org.kidinov.rijksmuseum.test.common.injection.component.DaggerTestComponent;
import org.kidinov.rijksmuseum.test.common.injection.component.TestComponent;
import org.kidinov.rijksmuseum.test.common.injection.module.ApplicationTestModule;
import org.kidinov.rijksmuseum.util.RxEventBus;

/**
 * Test rule that creates and sets a Dagger TestComponent into the application overriding the
 * existing application component.
 * Use this rule in your test case in order for the app to use mock dependencies.
 * It also exposes some of the dependencies so they can be easily accessed from the tests, e.g. to
 * stub mocks etc.
 */
public class TestComponentRule implements TestRule {
    private final TestComponent testComponent;
    private final Context context;

    public TestComponentRule(Context context) {
        this.context = context;
        App application = App.get(context);
        testComponent = DaggerTestComponent.builder()
                .applicationTestModule(new ApplicationTestModule(application))
                .build();
    }

    public Context getContext() {
        return context;
    }

    public DataManager getMockDataManager() {
        return testComponent.dataManager();
    }

    public RxEventBus getEventbus() {
        return testComponent.eventBus();
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                App application = App.get(context);
                application.setComponent(testComponent);
                base.evaluate();
                application.setComponent(null);
            }
        };
    }
}
