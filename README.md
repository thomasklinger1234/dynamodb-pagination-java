# DynamoDB Pagination Library

This is a Java library for performing pagination on DynamoDB tables. It is a (nearly) direct extraction from [awslabs/realworld-serverless-application](https://github.com/awslabs/realworld-serverless-application/tree/master/backend/src/main/java/software/amazon/serverless/apprepo/api/impl/pagination).

## Background

For most services, we use the `nextToken` approach used by various
AWS services for pagination. Because DynamoDB does not have any sense of *pages*, we need to remember the last item that was retrieved and return it to the client in an opaque form. 

For this, we also encrypt the token to prevent tampering or misuse. 

## Algorithm 

We paginagte with the following decision process:

- If there are more items to fetch from the database: add a `nextToken` property to the response
- Otherwise set the `nextToken` property to `null`

**IMPORTANT** Pagination tokens should ALWAYS be seen as opaque from the client side, i.e. they should never assume or be able to decode the token structure!

We utilize the `lastEvaluatedKey` property for `Query` actions against
a DynamoDB table. If this property is, set it will be processed with the following algorithms:

### Serialization

```bash
base64.encode(kms.encrypt(json.serialize(lastEvaluatedKey)&<current-timestamp>))
```

### Deserialization

```bash
json.deserialize(kms.decrypt(base64.decode(nextToken)))
```

### Representation

The pseudo-code representation is:

```bash
<lastEvaluatedKey-encrypted>&<timestamp>
```