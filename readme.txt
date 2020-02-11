Overview:
The program goes character by character through the code, continuously checking whether or not it is in a comment, string or general code
and reacts to code inside accordingly
The code has a main that requires the input of the file. It also has a constructor that takes in a file. Both result in the desired output
The program can recognize line comments, block comments, up to two types of strings and multi line strings and escape characters
The program can work for any language that is confined to the assumptions and requirements below assuming that it is in the languages.cfg file
The program is aware of many special cases such as nested comments and strings, identifiers that begin like other identifiers and multiple others. 
If the language is unrecognized, it will output a message to the screen.

Assumptions:
TODOs are not considered valid if they are immediately preceeded or followed by a letter or number
Consecutive single line comments are counted as block comments if they are the first non space in the line and the language doesn't have
a symbol for block comments
Lines are ended by end of line characters (ASCII value 10) 

Requirements:
This program is only meant to be used with valid programs, otherwise performance is unknown
All the files must be in the same location to run properly

Languages:
languages.cfg contains all the currently implementable languages
in order to add more, follow the below format EXACTLY other than the contents of the brackets that should be replaced. 
If the language doesn't support a given component, leave it completely blank
The format begins after this point:
extention: (language extention, including the .)
(start of a single line comment)
(start of a multi line comment)
(end of a multi line comment)
(start of a string)
(other start of a string) (some languages have both single and double quotes)
(escape character)
(start of a multi line string)
(other start of a multi line string) (some languages have both single and double quotes)
The format ends here
Any deviations from this format will result in unpredictable performance


