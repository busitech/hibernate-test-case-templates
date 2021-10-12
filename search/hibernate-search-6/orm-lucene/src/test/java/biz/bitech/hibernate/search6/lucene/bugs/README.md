# BIT Test Cases for Hibernate Search 6

The goal of this series of test cases is to correctly index an object after persist and merge when the
object concerned has a `@OneToMany` relationship that is `@IndexedEmbedded`.

`BITTestCase2` - This test case illustrates a regression of Hibernate Search 6 with respect to Hibernate 5 
after a call to **persist**.

`BITTestCase3` - This test case illustrates a regression of Hibernate Search 6 with respect to Hibernate 5
after a call to **merge** when a detached entity with modifications is passed in.  For this test case, the 
code was adjusted to avoid the problem illustrated in `BITTestCase2` to allow the test to continue.

`BITTestCase4` - This test case illustrates the only way persist and merge can be used if Hibernate
Search 6 is to index the object correctly.

`BITTestCase6` - This test case illustrates the regression of Hibernate Search 6 from what Hibernate Search 5
supported with respect to getting an entity A reindexed after persisting an new instance of an entity B 
which is @ContainedIn entity A.

`BITTestCase7` - This test case illustrates the changes necessary with Hibernate Search 6 to avoid the
problem illustrated by `BITTestCase6`.

`BITTestCase8` - This test case removes the calls to `refresh()`, and instead uses lazy loading and bi-directional 
association management features provided by the Hibernate bytecode enhancer

`BITTestCase9` - This test case removes the calls to `refresh()`, and uses lazyLoading and special Hibernate methods
to instantiate entities after the session has been opened.

`BITTestCase10` - This test case removes the calls to `refresh()`, and uses lazyLoading and code from PojoEntityInstantiator
to apply interceptor functionality to entities instantiated outside the session.