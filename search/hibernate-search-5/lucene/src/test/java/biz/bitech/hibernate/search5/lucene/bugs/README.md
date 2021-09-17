# BIT Test Cases for Hibernate Search 5

`BITTestCase1` - This test case illustrates what Hibernate Search 5 supported with respect to persisting 
a new entity, and merging a detached entity, when the entity concerned has a `@OneToMany` relationship 
that is `@IndexedEmbedded`.

`BITTestCase5` - This test case illustrates what Hibernate Search 5 supported with respect to getting an 
entity A reindexed after persisting an new instance of an entity B which is @ContainedIn entity A.