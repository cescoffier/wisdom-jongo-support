package org.wisdom.jongo.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ow2.chameleon.testing.helpers.OSGiHelper;
import org.wisdom.api.model.Crud;
import org.wisdom.api.model.Repository;
import org.wisdom.test.parents.WisdomTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test checking the crud instantiation.
 */
public class JongoBridgeIT extends WisdomTest {

    private OSGiHelper osgi;

    @Before
    public void setUp() {
        osgi = new OSGiHelper(context);
    }

    @After
    public void after() {
        osgi.dispose();
    }

    @Test
    public void testServices() {
        final Repository repository = osgi.getServiceObject(Repository.class);
        assertThat(repository).isNotNull();
        assertThat(repository.getName()).isEqualToIgnoringCase("kitten");
        assertThat(repository.getCrudServices()).hasSize(2);
        final List<Crud> cruds = osgi.getServiceObjects(Crud.class);
        assertThat(cruds).hasSize(2);
    }


}
