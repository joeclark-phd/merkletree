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

    /**
     * Checks if the data's hash is stored in the tree.
     * 
     * Don't misunderstand: the data itself is not stored in the Merkle tree, only its hash,
     * as we assume it is practically infeasible to come up with altered 'data' that gives
     * the same hash.
     */
    public boolean contains(String data) throws Exception {
        byte[] newHash = HashHelper.hash256(data);
        return this.containsHash(newHash);
    }

    private boolean containsHash(byte[] newHash) {
        if(isLeaf()) {
            return Arrays.equals(newHash, this.hash);
        } else {
            return left.containsHash(newHash) || right.containsHash(newHash);
        }
    }

    private boolean isLeaf() {
        return left==null && right==null;
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
                hash = hashTogether(newHash,hash);
            } else {
                left = new MerkleTree(hash);
                right = new MerkleTree(newHash);
                hash = hashTogether(hash, newHash);
            }
        } else {
            // we're not a leaf node. pass the hash down the tree
            if(new BigInteger(newHash).compareTo(new BigInteger(right.minHash()))<0) { // todo: easier way to compare hashes?
                left.insertHash(newHash);
            } else {
                right.insertHash(newHash);
            }
        }
    }

    private byte[] minHash() {
        if(isLeaf()) {
            return this.hash;
        } else {
            return left.minHash();
        }
    }

    private byte[] hashTogether(byte[] leftHash, byte[] rightHash) throws IOException {
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        baoStream.write(leftHash);
        baoStream.write(rightHash);
        byte[] combinedHashes = baoStream.toByteArray();
        return HashHelper.hash256(combinedHashes);
    }

}
