# ***DREAMLANDS : Data file format***

**The simplest syntax to describe a data structure.**

&nbsp;

&nbsp;



## Content

This project is divided in different branches :
- [master](https://github.com/iasebsil83/DREAMLANDS), general information about the DREAMLANDS.
- [command](https://github.com/iasebsil83/DREAMLANDS/tree/command), command executable for reading/writing DREAMLANDS.
- [python](https://github.com/iasebsil83/DREAMLANDS/tree/python), library for reading/writting DREAMLANDS in Python (Python3).
- [C     ](https://github.com/iasebsil83/DREAMLANDS/tree/c), library for reading/writting DREAMLANDS in C.
- [JS    ](https://github.com/iasebsil83/DREAMLANDS/tree/javascript), library for reading/writing DREAMLANDS in JavaScript.
- [Kotlin](https://github.com/iasebsil83/DREAMLANDS/tree/kotlin), library for reading/writting DREAMLANDS in Kotlin.
- [Go    ](https://github.com/iasebsil83/DREAMLANDS/tree/go), library for reading/writting DREAMLANDS in Go.
- [Lua   ](https://github.com/iasebsil83/DREAMLANDS/tree/lua), library for reading/writting DREAMLANDS in Lua.

We are currently on branch **master**.

&nbsp;

&nbsp;



## I] Objective

This language is inspired on the basics of the [YAML](https://yaml.org).

DREAMLANDS is a serializable syntax for representing any kind of data structure.

&nbsp;

The objective behind DREAMLANDS is to get/set information from a data file as quickly as possible for both computers and users.

That means that the syntax must be **very light** (for user accessibility) and **restrictive** (for faster parsing).

&nbsp;

***NOTE:*** The default DREAMLANDS syntax is (almost) **included** into the YAML syntax.

That means that a YAML reader can (almost) understand a DREAMLANDS text but the opposite is not totally true.

&nbsp;

&nbsp;



## II] Practical handbook

In this section, you will see all you have to know about DREAMLANDS in just a few lines.

### Special Variables

All the interpretation of the syntax is based on 5 special characters defined as follow :
```python
COMMENT    CHARACTER : '#'
LINE_END   CHARACTER : '\n' #line feed
NEW_FILE   CHARACTER : '>'
SEPARATION CHARACTER : ':'
TABULATION CHARACTER : '\t' #tabulation
```
They are customizable in the settings of all DREAMLANDS programs/libraries.

***WARNING:*** Do not use alphanumerical characters or underscores or you may have surprises ! (a-z, A-Z, 0-9, _)

Also pay attention to the character used, especially because of its encoding.

&nbsp;

### Rules

Here are all the rules of the DREAMLANDS syntax (in 3 parts) :

```
PART 1 : LINES

-  1) Is considerated as a line every serie of character preceding a LINE_END CHARACTER or EOF (end of file).

-  2) Empty lines are ignored.

-  3) Every COMMENT CHARACTER out of text declaration (character/string) marks a comment section.
      Every characters from this one until the next LINE_END CHARACTER will be ignored.

-  4) Every non-commented line must be composed by a key-value pair as described here :
  - 4.a) A serie of consecutive TABULATION CHARACTERs (giving the depth degree of the data)
  - 4.b) The key name of the current piece of data (1 byte minimum)
  - 4.c) A SEPARATION CHARACTER
  - 4.d) 2 possibilities for the last element :
           If the current element is an ending child : The piece of data itself
           If the current key is a parent            : Nothing (the children themselves are the data)
         (see next rule for parent/child definition)



PART 2 : STRUCTURATION

-  5) Every line indented one more time from the previous one is a child of this one.
      However, only one indent of difference is allowed, more are not allowed.
      A child without children is called an ending child (or branch ending).

-  6) Every line indented the same as the previous one is a brother/sister of this one.

-  7) Every line less indented than the previous one is a brother of the last element declared with the same indent.

-  8) The first element to analyze must have 0 indentation (root element).



PART 3 : KEY-VALUE PAIRS

-  9) Key names must contain only alphanumerical characters and underscores : [a-z], [A-Z], [0-9], '_'
      There is only one exception : Setting a minus character '-' as key name significates that the line refers to a list element.

- 10) If a list element is declared, every one of its brothers must be list elements as well (no alphanumerical key names allowed).
      The parent becomes a list and every element will be stored in an ordered list respecting the declaration order.

- 11) Data can be only of these types :
  - 11.a) Booleans must be either true or false.
  - 11.b) Characters must be delimited between simple quotes : '
          They can contain any special character that requires a backslash header (such as line feeds '\n', tabulations '\t'...)
  - 11.c) Integers must be only composed of numerical characters except the first one that can be a negative sign '-'.
  - 11.d) Floats must respect the same rules as integers with the exception that a dot '.' is allowed as coma and numbers allowed as well after this dot.
  - 11.e) Strings must be delimited by double quotes : "
          They can contain any special caracters too (same rule for backslashes).



PART 4 : IMPORTATIONS

- 12) When a NEW_FILE character is found, the rest of the line is interpreted as a path to another DREAMLANDS file to parse.
      All data taken from the new file is combined with the current one.

- 13) Importation paths can be either absolute or relative.
      In case of a relative paths, the current working directory is the one that holds the current file (not the one where the user executes something).

- 14) Every file analysed has its path stored and cannot be interpreted twice.

Please note that the features present in this section can be disabled at will.
If so, present importations are NOT raising errors but simply ignored.
```

Pay attention to those rules : Every non-matching format will raise an error on parsing.

**NOTE :** Rule 11 : That does NOT mean that ALL dreamlands files must start with a 0-indent element.
The different libraries give examples of this.

&nbsp;

Note that an option can be enabled to allow additional spaces in non-text fields.

&nbsp;

&nbsp;

### Coloration

Last but not least, the file *dreamlands.nanorc* given is a syntax coloration file made for the [nano](https://www.nano-editor.org/) text editor.

To add it to your current nano configuration, use the following command:
```bash
sudo cp dreamlands.nanorc /usr/share/nano
```
There you go.

&nbsp;

&nbsp;



## Signification

For those interrested, here is the signification of DREAMLANDS :

***Data<br>
Representation<br>
Embedding<br>
Another<br>
Markup<br>
Language<br>
Accepting<br>
No<br>
Disturbing<br>
Syntax***

Now you know why the last **S** is important !

&nbsp;

&nbsp;


*Contact      : i.a.sebsil83@gmail.com*<br>
*Youtube      : https://www.youtube.com/user/IAsebsil83*<br>
*Repositories : https://github.com/iasebsil83*<br>

Let's Code ! &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;By I.A.
