package net.joeclark;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

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

    /** Generates a tree from the string values of the integers 0 to 1000. */
    private MerkleTree generateBigTree() throws Exception {
        MerkleTree tree = new MerkleTree(Integer.toString(0));
        for(int i = 1; i<=1000; i++) {
            tree.insert(Integer.toString(i));
        }
        return tree;
    }

    @Test
    public void testTreeInsertsChangeRootHash() throws Exception {
        MerkleTree tree = new MerkleTree("foo");
        byte[] merkleRoot1 = tree.getHash();
        tree.insert("bar");
        byte[] merkleRoot2 = tree.getHash();
        tree.insert("baz");
        byte[] merkleRoot3 = tree.getHash();
        assertFalse(Arrays.equals(merkleRoot1, merkleRoot2));
        assertFalse(Arrays.equals(merkleRoot1, merkleRoot3));
        assertFalse(Arrays.equals(merkleRoot2, merkleRoot3));
    }

    @Test
    public void testMerkleTreeContains() throws Exception {
        MerkleTree tree = generateTestTree();
        assertTrue(tree.contains("delta"));
        assertFalse(tree.contains("omega"));
    }


    @Test
    public void testProofTreesWork() throws Exception {
        MerkleTree tree = generateTestTree();
        assertTrue(tree.contains("gamma"));
        assertFalse(tree.contains("spam"));
        // we can generate a "proof tree" which is much smaller than the real tree 
        // but has enough of the tree to confirm the existence of our data
        MerkleTree proof = tree.getProofTreeFor("gamma");
        assertTrue(proof.contains("gamma"));
        assertFalse(proof.contains("spam"));
        assertTrue(proof.getSize() <= tree.getSize());

        MerkleTree bigTree = generateBigTree();
        assertTrue(bigTree.contains("42"));
        assertFalse(bigTree.contains("3.14"));
        MerkleTree bigProof = bigTree.getProofTreeFor("42");
        assertTrue(bigProof.contains("42"));
        assertFalse(bigProof.contains("3.14"));

        // When the tree is very big, the proof tree will be significantly smaller
        System.out.println("Proof tree (bigProof) should be significantly smaller than the tree (bigTree) it's generated from: ");
        System.out.println("Size of bigTree: "+bigTree.getSize());
        System.out.println("Size of bigProof: "+bigProof.getSize());
        assertTrue(bigProof.getSize() <= bigTree.getSize());
    }

    @Test
    public void testGetSize() throws Exception {
        MerkleTree tree = generateTestTree();
        assertEquals(5, tree.getSize());

        MerkleTree bigTree = generateBigTree();
        assertEquals(1001, bigTree.getSize());
    }

    @Test
    public void testVerifyingHashes() throws Exception {
        MerkleTree bigTree = generateBigTree();
        assertTrue(bigTree.contains("42"));
        byte[] merkleRoot = bigTree.getHash();
        // Assume the Merkle Root is published openly by the owner of the full data set.
        // Since the data is too large to efficiently send us everything, the owner can
        // send us just the portion of the tree needed to calculate the hash, a 'proof' tree.
        MerkleTree bigProof = bigTree.getProofTreeFor("42");
        // We may hash the data ourselves because we don't trust the owner,
        // and check that our data is indeed hashed in this tree.
        byte[] ourHash = HashHelper.hash256("42");
        assertTrue(bigProof.containsHash(ourHash));
        // We then need to re-hash all the hashes from the leaves up to the root
        // and get the original Merkle Root back, to verify the proof tree.
        assertArrayEquals(merkleRoot, bigProof.recalculateRootHash());
        // A shorter notation for the above:
        assertTrue(bigProof.verifyRootHash(merkleRoot));
    }

}
