package net.joeclark;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * This class implements a Merkle Tree (either the root, or a node in the tree)
 * as an unbalanced binary search tree where each leaf node holds the SHA3 hash
 * of a piece of data, and every root or branch node holds a hash of the two hashes
 * beneath it.
 * 
 * Given a piece of data, we can hash it and very quickly find if that data's hash
 * is in the tree.  Furthermore, we can send *just enough* of the tree to another
 * party so that he can verify for himself that the hash is part of the tree.
 */
public class MerkleTree {

    private MerkleTree left;
    private MerkleTree right;
    private byte[] hash;

    public MerkleTree(String data) throws Exception {
        this.hash = HashHelper.hash256(data);
    }

    private MerkleTree(byte[] hash) {
        this.hash = hash;
    }

    public void setLeft(MerkleTree left) {
        this.left = left;
    }

    public void setRight(MerkleTree right) {
        this.right = right;
    }

    public byte[] getHash() {
        return hash;
    }

    /**
     * Inserts the hash of the new data into the tree.
     * This is an "unbalanced" binary search tree, not the most sophisticated algorithm.
     * 
     * Note: the data itself is not stored, just its hash, which is enough to verify it
     * if we assume it's infeasible for a malicious actor or a malfunction to come up
     * with different 'data' that produces the same output when hashed.
     */
    public void insert(String data) throws Exception {
        byte[] newHash = HashHelper.hash256(data);
        insertHash(newHash);
    }

    private void insertHash(byte[] newHash) throws IOException {
        if(isLeaf()) {
            // if we're a leaf node, create two new leaves and turn this into a branch node with a hash of the two hashes
            if(new BigInteger(newHash).compareTo(new BigInteger(hash))<0) {
                left = new MerkleTree(newHash);
                right = new MerkleTree(hash);
            } else {
                left = new MerkleTree(hash);
                right = new MerkleTree(newHash);
            }
        } else {
            // we're not a leaf node. pass the hash down the tree
            if(new BigInteger(newHash).compareTo(new BigInteger(right.minHash()))<0) { // todo: easier way to compare hashes?
                left.insertHash(newHash);
            } else {
                right.insertHash(newHash);
            }
        }
        this.hash = hashTogether(left.getHash(), right.getHash());
    }

    private boolean isLeaf() {
        return left==null && right==null;
    }

    private byte[] minHash() {
        if(isLeaf()) {
            return this.hash;
        } else {
            return left.minHash();
        }
    }

    /**
     * Combines the two hashes (from 'left' and 'right' nodes) and hashes the combination.
     */
    private byte[] hashTogether(byte[] leftHash, byte[] rightHash) throws IOException {
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        baoStream.write(leftHash);
        baoStream.write(rightHash);
        byte[] combinedHashes = baoStream.toByteArray();
        return HashHelper.hash256(combinedHashes);
    }

    /**
     * Checks if the data's hash is stored in the tree.
     * 
     * Don't misunderstand: the data itself is not stored in the Merkle tree, only its hash,
     * as we assume it is practically infeasible to come up with altered 'data' that gives
     * the same hash.
     */
    public boolean contains(String data) throws Exception {
        byte[] queryHash = HashHelper.hash256(data);
        return this.containsHash(queryHash);
    }

    /**
     * Checks if a given hash is stored in the tree.
     */
    public boolean containsHash(byte[] queryHash) {
        if(isLeaf()) {
            return Arrays.equals(queryHash, this.hash);
        } else {
            return left.containsHash(queryHash) || right.containsHash(queryHash);
        }
    }

    /**
     * Returns a partial tree with just enough nodes and hashes to allow another party to
     * run the `contains` method and verify that the data's hash is stored in the tree.
     */
    public MerkleTree getProofTreeFor(String data) throws Exception {
        byte[] queryHash = HashHelper.hash256(data);
        if(!containsHash(queryHash)) {
            // This check requires searching the tree twice, but is much easier to read than if we tried
            // to build the proof at the same time as confirming the presence of the data.
            throw new Exception("Merkle Tree does not contain that data, so no proof is possible.");
        }
        return getProofTreeForHash(queryHash);
    }

    /**
     * We know the tree contains the hash, since this is called by `getProofTreeFor` *after* it
     * checks whether the tree `contains` the hash.  For each branch node, we need the sub-tree leading
     * to the query hash, and only the root hash of the other sub-tree.
     */
    private MerkleTree getProofTreeForHash(byte[] queryHash) {
        MerkleTree proofTree = new MerkleTree(this.hash);
        if (!isLeaf()) {
            if (left.containsHash(queryHash)) {
                proofTree.setRight(new MerkleTree(right.getHash()));
                proofTree.setLeft(left.getProofTreeForHash(queryHash));
            } else {
                proofTree.setLeft(new MerkleTree(left.getHash()));
                proofTree.setRight(right.getProofTreeForHash(queryHash));
            }
        }
        return proofTree;
    }

    /**
     * To verify the root hash (Merkle root), we rebuild it from the leaf nodes, combining hashes as we go.
     */
    public boolean verifyRootHash(byte[] knownRootHash) throws IOException {
        return Arrays.equals(knownRootHash, recalculateRootHash());
    }

    /**
     * Recalculate the root hash from the leaves all the way up.
     */
    public byte[] recalculateRootHash() throws IOException {
        if(isLeaf()) {
            return this.hash;
        } else {
            return hashTogether(left.recalculateRootHash(), right.recalculateRootHash());
        }
    }

    /** 
     * Traverses the whole tree in order to count how many nodes (hashes) it contains. For testing/analysis.
     */
    public int getSize() {
        if(isLeaf()) { 
            return 1;
        } else {
            return left.getSize() + right.getSize();
        }
    }

}
