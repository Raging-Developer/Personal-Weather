# Personal-Weather
Local Weather App

In January 2019 Yahoo! closed down their yql query database. 
This query made it possible to get the WOEID from the location provided by the GPS of the device. 
Now uses the signpost OAuth library because Yahoo! is still using version 1, which was deprecated by google in 2012.

Also has to use city,country as a parameter rather than lat,long.
