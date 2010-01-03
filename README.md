Grendel
=======

Grendel is a RESTful web service which allows for the secure storage of users'
documents.

When a Grendel user is created, an OpenPGP keyset (a master key for
signing/verifying and a sub key for encrypting/decrypting) is generated. When
the user stores a document, the document is signed with the user's master key
and encrypted with their sub key.

Other users can be granted read-only access to these documents. For instance,
if a web service stores documents securely for users, a user might grant the
service administrators temporary shared access to their documents for support 
purposes, or may grant permanent access to another user for sharing purposes.

**To get started using Grendel, read [GETTING-STARTED.md](http://github.com/wesabe/grendel/blob/master/GETTING-STARTED.md).**

**To read about how to use Grendel, read [API.md](http://github.com/wesabe/grendel/blob/master/API.md).**

**To read Grendel's distribution license, read [LICENSE.md](http://github.com/wesabe/grendel/blob/master/LICENSE.md).**