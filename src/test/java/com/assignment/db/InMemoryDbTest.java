package com.assignment.db;

import com.assignment.model.Entity;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link InMemoryDb}
 */
public class InMemoryDbTest {

    private InMemoryDb<TestEntity> inMemoryDb;

    private TestEntity testEntity1;
    private TestEntity testEntity2;
    private TestEntity testEntity2Same;
    private TestEntity testEntityNullId;

    private class TestEntity extends Entity {

        String value;

        TestEntity(String id, String value) {
            super(id);
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    @Before
    public void setUp() throws Exception {

        inMemoryDb = new InMemoryDb<>();

        testEntity1 = new TestEntity("id_1", "value_1");
        testEntity2 = new TestEntity("id_2", "value_2");
        testEntity2Same = new TestEntity("id_2", "value_2");
        testEntityNullId = new TestEntity(null, "value_3");
    }

    @Test
    public void addTest() throws Exception {

        // act
        boolean result1 = inMemoryDb.add(testEntity1);
        boolean result2 = inMemoryDb.add(testEntity2);

        // assert
        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    public void addWithSameIdTest() throws Exception {

        // arrange
        inMemoryDb.add(testEntity2);

        // act
        boolean result = inMemoryDb.add(testEntity2Same);

        // assert
        assertFalse(result);
    }

    @Test
    public void addNullTest() throws Exception {

        // act
        boolean result1 = inMemoryDb.add(testEntityNullId);
        boolean result2 = inMemoryDb.add(null);

        // assert
        assertFalse(result1);
        assertFalse(result2);
    }

    @Test
    public void getTest() throws Exception {

        // arrange
        inMemoryDb.add(testEntity1);

        // act
        TestEntity actualEntity = inMemoryDb.get(testEntity1.getId());

        // assert
        assertEquals(testEntity1, actualEntity);
    }

    @Test
    public void getUnexistingEntityTest() throws Exception {

        // arrange
        inMemoryDb.add(testEntity1);

        // act
        TestEntity actualEntity = inMemoryDb.get(testEntity2.getId());

        // assert
        assertNull(actualEntity);
    }

    @Test
    public void getWithNullIdTest() throws Exception {

        // arrange
        inMemoryDb.add(testEntity1);

        // act
        TestEntity actualEntity = inMemoryDb.get(null);

        // assert
        assertNull(actualEntity);
    }

    @Test
    public void getAllTest() throws Exception {

        // arrange
        inMemoryDb.add(testEntity1);
        inMemoryDb.add(testEntity2);
        List<TestEntity> expectedEntities = Arrays.asList(testEntity1, testEntity2);
        List<TestEntity> actualEntities = new ArrayList<>();

        // act
        Collection<TestEntity> result = inMemoryDb.getAll();
        actualEntities.addAll(result);

        Collections.sort(actualEntities, (e1, e2) -> e1.getId().compareTo(e2.getId()));

        assertEquals(expectedEntities.size(), actualEntities.size());

        for (int i = 0; i < expectedEntities.size(); i++) {
            TestEntity expectedEntity = expectedEntities.get(i);
            TestEntity actualEntity = actualEntities.get(i);
            assertEquals(expectedEntity.getId(), actualEntity.getId());
            assertEquals(expectedEntity.getValue(), actualEntity.getValue());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAllTryToAddTest() throws Exception {

        // arrange
        inMemoryDb.add(testEntity1);
        inMemoryDb.add(testEntity2);
        Collection<TestEntity> result = inMemoryDb.getAll();

        // act
        result.add(new TestEntity("a", "b"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAllTryToRemoveTest() throws Exception {

        // arrange
        inMemoryDb.add(testEntity1);
        inMemoryDb.add(testEntity2);
        Collection<TestEntity> result = inMemoryDb.getAll();

        // act
        result.remove(testEntity1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAllTryToRemoveViaIteratorTest() throws Exception {

        // arrange
        inMemoryDb.add(testEntity1);
        inMemoryDb.add(testEntity2);
        Collection<TestEntity> result = inMemoryDb.getAll();

        // act
        Iterator<TestEntity> iterator = result.iterator();
        iterator.next();
        iterator.remove();
    }
}