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