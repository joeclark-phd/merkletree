package net.joeclark;

/**
 * This class implements a Merkle Tree (either the root, or a node in the tree)
 * as an unbalanced binary search tree where each leaf node holds a piece of data
 * and its SHA3 hash, and every root or branch node holds a hash of the two hashes
 * beneath it.
 * 
 * Given a piece of data, we can hash it and very quickly find if that data's hash
 * is in the tree.  Furthermore, we can send *just enough* of the tree to another
 * party so that he can verify for himself that the hash is part of the tree.
 */
public class MerkleTree {



}
