package org.kidinov.rijksmuseum;


import org.junit.Test;
import org.kidinov.rijksmuseum.data.model.collection.ArtObject;
import org.kidinov.rijksmuseum.test.common.TestDataFactory;
import org.kidinov.rijksmuseum.util.ListUtil;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * JVM test of {@link ListUtil}
 */
public class ListUtilTest {
    @Test
    public void compareInstaItemListsReturnsTrueIfListsAreEqual() {
        List<ArtObject> leftList = TestDataFactory.makeCollectionWithSeqIds(10).getArtObjects();
        List<ArtObject> rightList = TestDataFactory.makeCollectionWithSeqIds(10).getArtObjects();

        assertTrue(ListUtil.compareListItems(leftList, rightList));
    }

    @Test
    public void compareInstaItemListsReturnsFalseIfListsAreDifferentSize() {
        List<ArtObject> leftList = TestDataFactory.makeCollectionWithSeqIds(10).getArtObjects();
        List<ArtObject> rightList = TestDataFactory.makeCollectionWithSeqIds(11).getArtObjects();

        assertFalse(ListUtil.compareListItems(leftList, rightList));
    }

    @Test
    public void compareInstaItemListsReturnsFalseIfListsAreNotEqual() {
        List<ArtObject> leftList = TestDataFactory.makeCollectionWithSeqIds(10).getArtObjects();
        List<ArtObject> rightList = TestDataFactory.makeCollection(10, "").getArtObjects();

        assertFalse(ListUtil.compareListItems(leftList, rightList));
    }
}
