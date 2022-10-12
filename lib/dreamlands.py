#!/usr/bin/python3



# ---------------- DREAMLANDS Library ----------------
# |                                                  |
# |                                                  |
# |                                                  |
# ----------------------------------------------------






# ---- CONSTANTS ----

#shebang
SHEBANG = "#!/usr/bin/dreamlands\n"

#characters allowed
ALPHABET     = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_"
NUMBERS      = "0123456789"
ALPHANUMERIC = ALPHABET + NUMBERS

#special characters
COMMENT_CHARACTER    = '#'
END_CHARACTER        = '\n'
NEW_FILE_CHARACTER   = '>'
SEPARATION_CHARACTER = ':'
TABULATION_CHARACTER = '\t'






# ---- UTILITIES ----

#errors
def paramError():
	'''
	Raise a parameter error.
	'''

	raise TypeError("{INTERNAL} Invalid parameters types.")

def parsingError(line_nbr, message):
	'''
	Raise a parsing error.

	line_nbr: int
	message: str
	'''

	#typecheck
	if not isinstance(line_nbr, int) or not isinstance(message, str):
		paramError()

	raise ValueError("Line " + str(line_nbr) + " : " + message)



#keywords
def verifyKeyword(line_nbr, keyword):
		'''
		Verify the integrity of a DREAMLANDS keyword.

		line_nbr: int
		keyword: str
		'''

		#typecheck (line_nbr will be checked in error handlings)
		if not isinstance(keyword, str):
			paramError()

		#error case
		if len(keyword) == 0:
			parsingError(line_nbr, "Keyword empty.")

		#first character
		if keyword[0] not in ALPHABET:
			parsingError(line_nbr, "Forbidden character in first character of keyword.")

		#other characters
		for k in range(len(keyword)-1):
			if keyword[k] not in ALPHANUMERIC:
				parsingError(line_nbr, "Forbidden character in keyword.")






# ---- READ / WRITE ----

#read
def getDREAMLANDSElement(lineNbr, lines, degree):
	'''
	Get an element and all its subelements from some text lines.

	line_nbr: int
	lines: list[str]
	degree: int
	'''

	if not isinstance(lines, list) or not isinstance(degree, int):
		paramError()

	#analyse
	data = {}

	return data

def read(filename):
	'''
	Read a DREAMLANDS file.

	filename: str

	This function is recursive.
	Returns a dictionnary representing the data read from the file.
	'''

	#typecheck
	if not isinstance(filename, str):
		paramError()

	#read text from file
	f = open(filename, "r")
	lines = f.readlines()
	f.close()

	#analyse
	return getDREAMLANDSElement(1, lines, 0)



#write
def getDREAMLANDSText(line_nbr, data, degree):
	'''
	Get the DREAMLANDS text corresponding to a dictionnary.

	line_nbr: int
	data: dict
	degree: int

	This function is recursive.
	The degree correspond to the tabulation shift
	'''

	#typecheck (line_nbr will be checked in errors)
	if not isinstance(data, dict) or not isinstance(degree, int):
		paramError()

	#for each element in dictionnary 'data'
	text = ""
	for k in data:
		verifyKeyword(line_nbr, k)

		#case 1: parent element
		if isinstance(data[k], dict):

			#add parent text
			text += TABULATION_CHARACTER*degree + k + SEPARATION_CHARACTER + END_CHARACTER

			#add children text
			subtext   = getDREAMLANDSText(line_nbr, data[k], degree+1)
			line_nbr += subtext.count('\n')
			text     += subtext

		#case 2: child element
		else:
			text += TABULATION_CHARACTER*degree + k + SEPARATION_CHARACTER + str(data[k]) + END_CHARACTER
			line_nbr += 1

	return text


def write(data, filename):
	'''
	Write data into a DREAMLANDS file.

	data: dict
	filename: str

	Write the data respecting the DREAMLANDS syntax.
	'''

	#typecheck (no need to check data, it will be checked in getSAMLText())
	if not isinstance(filename, str):
		paramError()

	#analyse
	text = SHEBANG + getDREAMLANDSText(1, data, 0)

	#write out
	f = open(filename, "w")
	f.write(text)
	f.close()
