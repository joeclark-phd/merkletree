package net.joeclark;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MerkleTreeTest 
{
    /** Test that tests are running normally I guess. */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    private MerkleTree generateTestTree() throws Exception {
        MerkleTree tree = new MerkleTree("alpha");
        tree.insert("beta");
        tree.insert("gamma");
        tree.insert("delta");
        tree.insert("epsilon");
        return tree;
    }

    @Test
    public void testMerkleTreeContains() throws Exception {
        MerkleTree tree = generateTestTree();
        assertTrue(tree.contains("delta"));
        assertFalse(tree.contains("omega"));
    }


}
