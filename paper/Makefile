all:	main 

main:	main.ps main.pdf

TEX-FILES =	\
	abstract.tex		main.tex	\
	conclusions.tex		discussion.tex	\
	intro.tex		related.tex	\
	acknowledgements.tex design.tex 

main.dvi: ${TEX-FILES} *.bib
	latex main
	bibtex main
	latex main
	latex main
	latex main
	latex main

main.pdf: main.ps
	ps2pdf main.ps main.pdf

main.ps:	main.dvi
	dvips -t letter main.dvi -o main.ps

clean:
	rm -f *.aux *.bbl *.blg *.log *.dvi *.bak *~ main.ps main.pdf
