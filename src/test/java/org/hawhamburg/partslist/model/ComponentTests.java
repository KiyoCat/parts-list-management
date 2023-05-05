package org.hawhamburg.partslist.model;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComponentTests {

    /**
     * Test fetching the price of a Material.
     */
    @Test
    public void fetchPriceMaterial() {
        // Arrange
        var material1 = new Material("m1", 20);
        int expectedResult = 20;
        // Act
        int actualResult = material1.fetchTotalPrice();
        // Assert
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test fetching the price of a Product containing two Materials.
     */
    @Test
    public void fetchPriceProductWithTwoMaterials() throws CyclicStructureException {
        // Arrange
        var material1 = new Material("m1", 23);
        var material2 = new Material("m2", 6);
        var product1 = new Product("p1",37);
        product1.addPart(material1,1);
        product1.addPart(material2,1);
        int expectedResult = 23+6+37;
        // Act
        int actualResult = product1.fetchTotalPrice();
        // Assert
        assertEquals(expectedResult,actualResult);
    }

    /**
     * Test fetching the price of a Product containing Products and Materials.
     */
    @Test
    public void fetchPriceProductWithMultipleSubProducts() throws CyclicStructureException {
        // Arrange
        var material1 = new Material("m1", 23);
        var material2 = new Material("m2", 6);
        var product1 = new Product("p1",37);
        var product2 = new Product("p2", 72);
        product1.addPart(material1,1);
        product2.addPart(material2,3);
        product2.addPart(product1,2);
        int expectedResult = (2*23)+(3*6)+(2*37)+72;
        // Act
        int actualResult = product2.fetchTotalPrice();
        // Assert
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void fetchMaterialListProductWithMultipleSubProducts() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 2);
        var p2 = new Product("p2", 5);
        var p3 = new Product("p3", 10);
        var p4 = new Product("p4", 1);
        var m1 = new Material("m1", 5);
        var m2 = new Material("m2", 10);
        var m3 = new Material("m3", 1);
        var m4 = new Material("m4", 15);

        p1.addPart(p2, 2);
        p1.addPart(p3, 1);
        p1.addPart(m3, 5);

        p2.addPart(m1, 10);
        p2.addPart(p4, 3);

        p3.addPart(m1, 3);
        p3.addPart(m2, 5);
        p3.addPart(p4, 2);

        p4.addPart(m1, 2);
        p4.addPart(m4, 1);

        var m1ExpectedResult = Collections.singletonList(m1);
        var m2ExpectedResult = Collections.singletonList(m2);
        var m3ExpectedResult = Collections.singletonList(m3);
        var m4ExpectedResult = Collections.singletonList(m4);

        var p4ExpectedResult = List.of(m1, m1, m4);
        var p3ExpectedResult = Stream.of(List.of(m1, m1, m1, m2, m2, m2, m2, m2), p4ExpectedResult, p4ExpectedResult).flatMap(Collection::stream).toList();
        var p2ExpectedResult = Stream.of(List.of(m1, m1, m1, m1, m1, m1, m1, m1, m1, m1), p4ExpectedResult, p4ExpectedResult, p4ExpectedResult).flatMap(Collection::stream).toList();
        var p1ExpectedResult = Stream.of(p2ExpectedResult, p2ExpectedResult, p3ExpectedResult, List.of(m3, m3, m3, m3, m3)).flatMap(Collection::stream).toList();


        // Act & Assert
        assertEquals(m1ExpectedResult, m1.fetchMaterialList());
        assertEquals(m2ExpectedResult, m2.fetchMaterialList());
        assertEquals(m3ExpectedResult, m3.fetchMaterialList());
        assertEquals(m4ExpectedResult, m4.fetchMaterialList());

        assertEquals(p4ExpectedResult, p4.fetchMaterialList());
        assertEquals(p3ExpectedResult, p3.fetchMaterialList());
        assertEquals(p2ExpectedResult, p2.fetchMaterialList());
        assertEquals(p1ExpectedResult, p1.fetchMaterialList());
    }

    @Test
    public void addPartReflexiveCycle() {
        // Arrange
        var p1 = new Product("p1", 1);

        // Act & Assert
        assertThrows(CyclicStructureException.class, () -> p1.addPart(p1, 1));
    }

    @Test
    public void addPartDirectCycle() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 1);
        var p2 = new Product("p2", 2);
        p1.addPart(p2, 1);

        // Act & Assert
        assertThrows(CyclicStructureException.class, () -> p2.addPart(p1, 1));
    }

    @Test
    public void addPartTransitiveCycle() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 1);
        var p2 = new Product("p2", 2);
        var p3 = new Product("p3", 3);
        p1.addPart(p2, 1);
        p2.addPart(p3, 1);

        // Act & Assert
        assertThrows(CyclicStructureException.class, () -> p3.addPart(p1, 1));
    }
}
