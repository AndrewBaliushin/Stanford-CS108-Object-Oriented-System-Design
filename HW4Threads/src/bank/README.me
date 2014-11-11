### Recommendations from pdf:

- **Account** needs to store an id number, the current balance for the account, and the 
number of transactions that have occurred on the account. Remember that multiple 
worker threads may be accessing an account simultaneously and you must ensure 
that they cannot corrupt its data. You may also want to override the toString
method to handle printing of account information.

- **Transaction** is a simple class that stores information on each transaction (see below 
for more information about each transaction). If you’re careful you can treat the 
Transaction as immutable. This means that you do not have to worry about multiple 
threads accessing it. Remember an immutable object’s values never change, 
therefore its values are not subject to corruption in a concurrent environment.

- The **Bank** class maintains a list of accounts and the BlockingQueue used to 
communicate between the main thread and the worker threads. The Bank is also 
responsible for starting up the worker threads, reading transactions from the file, and 
printing out all the account values when everything is done. Note: make sure you 
start up all the worker threads before reading the transactions from the file.

- I recommend making the **Worker** class is an inner class of the Bank class. This way 
it gets easy access to the list of accounts and the queue used for communication. 
Workers should check the queue for transactions. If they find a transaction they 
should process it. If the queue is empty, they will wait for the Bank class to read in 
another transaction (you’ll get this behavior for free by using a BlockingQueue). 
Workers terminate when all the transactions have been processed. We’ll discuss 
ways to communicate this below.

