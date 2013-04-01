#!/usr/bin/python
# -*- coding: utf-8 -*-
import fileinput
import codecs
import sys

ignorelist = ('!', '-', '_', '(', ')', ',', '.', ':', ';', '"', '\'', '?', '#', '@', '$', '^', '&', '*', '+', '=', '{', '}', '[', ']', '\\', '|', '<', '>', '/', u'—')
#ignorelist = ('!', '-', '_', '(', ')', ',', '.', ':', ';', '"', '\'', '?', '#', '@', '$', '^', '&', '*', '+', '=', '{', '}', '[', ']', '\\', '|', '<', '>', '/')

def print_token(token):
#	print "before clean: "+ token
	for punc in ignorelist:
		token = token.replace(punc, ' ')
#		print "after clean: "+token
	for subtoken in token.strip().split():
		print subtoken.upper(),
	
def main():
#	for line in fileinput.input(openhook=fileinput.hook_encoded("utf-8")):
	fhandle = codecs.open(sys.argv[1], "r", "utf-8")
	line = fhandle.readline()
	while line != "":
		items = line.split()
		for token in items:
			print_token(token)
		line = fhandle.readline()

if __name__ == "__main__":
	main()
