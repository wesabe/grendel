The Grendel API
===============

Grendel is a RESTful webservice, using JSON objects for data exchange.

URI Structure
-------------

* `/users`
* `/users/{id}`
* `/users/{id}/documents`
* `/users/{id}/documents/{name}`
* `/users/{id}/documents/{name}/links`
* `/users/{id}/documents/{name}/links/{otherid}`
* `/users/{id}/linked-documents`
* `/users/{id}/linked-documents/{otherid}/{name}`


Concurrency
-----------

Grendel uses `Last-Modified` and `ETag` headers, and supports `If-None-Match`,
`If-Match`, `If-Unmodified-Since`, and `If-Modified-Since` preconditions.

Your Grendel client should either use these headers or accept the possibility
of overwriting fresh data with stale data.

**To safely update a document or user:**

1. `GET` the existing resource.
2. Make any changes you need.
3. `PUT` the new resource, using `If-Match` and the existing resource's `ETag`
   or `If-Unmodified-Since` and the existing resource's `Last-Modified`.
4. If you receive a `2xx` response, your change was successfully written. If you
   receive a `412 Preconditions Failed`, you should retry the process, starting
   with Step 1.

**To efficiently re-read a document or user:**

1. `GET` the existing resource.
2. `GET` the possibly-changed resource using `If-None-Match` and the existing
   resource's `ETag` or `If-Modified-Since` and the existing resource's
   `Last-Modified`.
3. If you receive a `200 OK` response, the resource was changed since you last
   requested it. If you receive a `304 Not Modified` response, the resource has
   not been changed.

This will work for both `/users/{id}` and `/users/{id}/document`.


Examples
--------

All of the operations documented below are demonstrated with shell scripts that
you can find in the
[examples directory](http://github.com/wesabe/grendel/tree/master/examples/).
These assume that Grendel is running on localhost, port 8080. If you run them 
without arguments and they require arguments, a help line will appear showing you
how to use them.

The full request and response will be shown as output. All the example scripts
use `curl`, a command-line HTTP client.


Authentication
--------------

Most Grendel resources require Basic HTTP Authentication credentials with the
user's `id` and `password`.


Managing Users
==============

A Grendel user is effectively an `id` — an arbitrary identifier — and an OpenPGP
signing key/encryption key pair.


Listing Registered Users
------------------------

Sending a `GET` request to `/users/` will return an `application/json` object
with a list of all users' ids and URIs:

    > GET /users/ HTTP/1.1
    > Accept: application/json
    >
    
    < HTTP/1.1 200 OK
    < Content-Type: application/json
    <
    < {
    <   "users":[
    <     {
    <       "id":"codahale",
    <       "uri":"http://example.com/users/codahale"
    <     }
    <   ]
    < }


Creating A New User
-------------------

Sending a `POST` request to `/users` with an `application/json` object will
create a new user and return its URI in the response's `Location` header:

    > POST /users/ HTTP/1.1
    > Content-Type: application/json
    >
    > {
    >   "id": "codahale",
    >   "password": "woowoo"
    > }
    
    < HTTP/1.1 201 Created
    < Location: http://example.com/users/codahale

Both the `id` and the `password` properties are **required**. This action may
take some time (~1s), as Grendel will generate an OpenPGP keyset for the user.

**The user's `id` property is immutable.** Please use an immutable piece of data
(e.g., a primary key) instead of a modifiable piece of data (e.g., a username or
email address).

If the user `id` is taken, a `422 Unprocessable Entity` response will be
returned with an explanation.


Viewing A User
--------------

Sending a `GET` request to `/users/codahale` will return an `application/json`
object with information about the user `codahale`:

    > GET /users/codahale HTTP/1.1
    > Accept: application/json
    >
    
    < HTTP/1.1 200 OK
    < Content-Type: application/json
    < 
    < {
    <   "id":"codahale",
    <   "modified-at":"20091227T211120Z",
    <   "created-at":"20091227T211120Z",
    <   "keys":"[2048-RSA/0A895A19, 2048-RSA/39D1621B]"
    < }

The `created-at` and `modified-at` properties are timestamps in ISO 8601 format.


Changing A User's Password
--------------------------

*Requires authentication.*

Sending a `PUT` request to `/users/codahale` with an `application/json` object
will change the user `codahale`'s password:

    > PUT /users/codahale HTTP/1.1
    > Content-Type: application/json
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    >
    > {
    >   "password": "secretstuff"
    > }
    
    < HTTP/1.1 204 No Content

**The `password` property is required**. The Basic authentication should use the
old password; the next request will require Basic authentication credentials
with the new password.


Deleting A User
---------------

*Requires authentication.*

Sending a `DELETE` request to `/users/codahale` will delete user `codahale`
**and all their documents**:

    > DELETE /users/codahale HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    >
    
    < HTTP/1.1 204 No Content


Managing Documents
==================

A Grendel document is an arbitrary series of bytes which belongs to a user.
Documents are stored as OpenPGP messages, signed by the document's owner, and
encrypted for the owner and any linked users. The MIME type of the document is
stored along with the document.


Listing A User's Documents
--------------------------

*Requires authentication.*

Sending a `GET` request to `/users/codahale/documents/` will return an
`application/json` object with a list of documents belonging to user `codahale`:

    > GET /users/codahale/documents/ HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    > Accept: application/json
    >
    
    < HTTP/1.1 200 OK
    < Content-Type: application/json
    <
    < {
    <   "documents":[
    <     {
    <       "name":"document1.txt",
    <       "uri":"http://example.com/users/codahale/documents/document1.txt"
    <     }
    <   ]
    < }


Viewing A User's Document
-------------------------

*Requires authentication.*

Sending a `GET` request to `/users/codahale/documents/document1.txt` will return
the document named `document1.txt` belonging to user `codahale` in whatever
content type the document was stored with:

    > GET /users/codahale/documents/document1.txt HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    >
    
    < HTTP/1.1 200 OK
    < Content-Length: 10
    < Cache-Control: private, no-cache, no-store, no-transform
    < Content-Type: text/plain
    <
    < yay for me


Storing A User's Document
-------------------------

*Requires authentication.*

Sending a `PUT` request to `/users/codahale/documents/document1.txt` will store
the request entity as `document1.txt` with the specified content type:

    > PUT /users/codahale/documents/document1.txt HTTP/1.1
    > Content-Type: text/plain
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    >
    > i am super secret
    
    < HTTP/1.1 204 No Content

Sending `PUT` request to an existing document will overwrite its contents;
doing so to a non-existent document will create it.

**Currently Grendel does not support `If-None-Match`, so Grendel will silently
overwrite fresh data with stale data.**


Deleting A User's Document
--------------------------

*Requires authentication.*

Sending a `DELETE` request to `/users/codahale/documents/document1.txt` will
delete the document named `document1.txt` belonging to user `codahale`:

    > DELETE /users/codahale/documents/document1.txt HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    >
    
    < HTTP/1.1 204 No Content


Linking Documents
=================

A Grendel document can be linked by its owner with other users. Doing so
provides other users *read-only* access to the document.


Viewing A Document's Linked Users
---------------------------------

*Requires authentication.*

Sending a `GET` request to `/users/codahale/documents/document1.txt/links` will
return a list of links from the document `document1.txt` belonging to user
`codahale` to other users:

    > GET /users/codahale/documents/document1.txt/links HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    > Accept: application/json
    >
    
    < HTTP/1.1 200 OK
    < Content-Type: application/json
    <
    < {
    <   "links":[
    <     {
    <       "user":{
    <         "id":"precipice",
    <         "uri":"http://example.com/users/precipice"
    <       },
    <       "uri":"http://example.com/users/codahale/documents/document1.txt/links/precipice"
    <     }
    <   ]
    < }


Linking Another User To A Document
----------------------------------

*Requires authentication.*

Sending a `PUT` request to
`/users/codahale/documents/document1.txt/links/precipice` will link user
`precipice` to document `document1.txt` belonging to user `codahale`:

    > PUT /users/codahale/documents/document1.txt/links/precipice HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    > Accept: application/json
    >
    
    < HTTP/1.1 204 No Content

User `precipice` will now have read-only access to the document.


Unlinking A User From A Document
--------------------------------

*Requires authentication.*

Sending a `DELETE` request to
`/users/codahale/documents/document1.txt/links/precipice` will unlink user
`precipice` to document `document1.txt` belonging to user `codahale`:

    > DELETE /users/codahale/documents/document1.txt/links/precipice HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    > Accept: application/json
    >
    
    < HTTP/1.1 204 No Content

User `precipice` will no longer have access to the document.


Managing Linked Documents
-------------------------

The documents shared with a user are stored in their own namespace to avoid
document name collisions. If the document's owner modifies the document, the
linked users will see the changes. Likewise, if the document's owner deletes the
document (or the owner is deleted), the documents will be removed from the
user's linked documents.


Listing A User's Linked Documents
---------------------------------

*Requires authentication.*

Sending a `GET` request to `/users/codahale/linked-documents/` will return a
list of documents to which user `codahale` has read-only access:

    > GET /users/codahale/linked-documents/ HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    > Accept: application/json
    >

    < HTTP/1.1 200 OK
    < Content-Type: application/json
    <
    < {
    <   "linked-documents":[
    <     {
    <       "name":"document1.txt",
    <       "uri":"http://example.com/users/codahale/linked-documents/precipice/document1.txt",
    <       "owner":{
    <         "id": "precipice",
    <         "uri": "http://example.com/users/precipice"
    <       }
    <     }
    <   ]
    < }


Viewing A Linked Document
-------------------------

*Requires authentication.*

Sending a `GET` request to
`/users/codahale/linked-documents/precipice/document1.txt` will return the
document named `document1.txt` belonging to user `precipice` in whatever content
type the document was stored with:

    > GET /users/codahale/linked-documents/precipice/document1.txt HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    >

    < HTTP/1.1 200 OK
    < Content-Length: 10
    < Cache-Control: private, no-cache, no-store, no-transform
    < Content-Type: text/plain
    <
    < yay for me


Deleting A Linked Document
--------------------------

*Requires authentication.*

Sending a `DELETE` request to
`/users/codahale/linked-documents/precipice/document1.txt` will remove the user
`codahale`'s access to the document named `document1.txt` belonging to user
`precipice`:

    > DELETE /users/codahale/linked-documents/precipice/document1.txt HTTP/1.1
    > Authorization: Basic Y29kYWhhbGU6d29vd29v
    >

    < HTTP/1.1 204 No Content

**This will *not* delete the document itself**, it will simply remove the
document from the user's list of linked documents. It will also **not**
re-encrypt the document; the next time the document is written to, however, the
user will be excluded from the recipients.


