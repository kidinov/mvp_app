package org.kidinov.rijksmuseum.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import org.kidinov.rijksmuseum.injection.component.ApplicationComponent;
import org.kidinov.rijksmuseum.test.common.injection.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {
}
