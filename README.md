An Experimental Chinese SMS spam filter
===========================
An Experimental Chinese SMS spam filter , use Aho-Corasick Automata on pattern matching procedure.

Only implemented basic function , without spam word list **fully** stuffed.

Update
--------
* 2013-05-08 21:38:48 : Added "it's not a spam" button to resend sms in spam filter 
* 2013-05-07 20:51:40 : could start service after boot
* 2013-04-15 : added contact check, if sender not in contact, raise the risk rate of spam

TODO-LIST
--------
* Add a blacklist of phone numbers, save it in DB
* Add function: Check whether a number in blacklist
* refactor UI
* need a visible blacklist 
* need i18n
