# merkletree

A simple implementation of a Merkle Tree for verifying a large set of data has not been tampered with.

To run tests, download this repo and use Maven:

    mvn clean test

## How it works

A Merkle Tree is a binary search tree containing hashes of the data you want to validate; each leaf node contains the hash of a particular "document", and each branching node as well as the root node contains a hash of the two hashes beneath it.  This structure allows us to:

1. Quickly determine if a given document exists in the data set, by hashing it and searching the tree for its hash.
2. Provide another party with *just enough* of the tree for him to verify for himself that the document exists, assuming he knows the root node's hash (the "Merkle root") and the transaction he wants to verify.

Merkle Trees are used in blockchain verification, in distributed databases, and in version control systems like `git`, to allow distributed parties to efficiently determine that a large data base has not been tampered with.
