#!/usr/bin/python
# -*- coding: utf-8 -*-
import codecs
import sys

ignorelist = ('!', '-', '_', '(', ')', ',', '.', ':', ';', '"', '\'', '?', '#', '@', '$', '^', '&', '*', '+', '=', '{', '}', '[', ']', '\\', '|', '<', '>', '/', u'—', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

def print_token(token):
	for punc in ignorelist:
		token = token.replace(punc, ' ')
	for subtoken in token.strip().split():
		print subtoken.upper(),
	
def main():
	fhandle = codecs.open(sys.argv[1], "r", "utf-8")
	line = fhandle.readline()
	while line != "":
		items = line.split()
		for token in items:
			print_token(token)
		line = fhandle.readline()

if __name__ == "__main__":
	main()
