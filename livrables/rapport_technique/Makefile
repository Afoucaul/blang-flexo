DOCUMENT = rapport_technique

$(DOCUMENT).pdf: $(DOCUMENT).tex
	pdflatex -shell-escape $(DOCUMENT).tex
	biber $(DOCUMENT)
	pdflatex -shell-escape $(DOCUMENT)
	pdflatex -shell-escape $(DOCUMENT)

clean:
	rm -f *.aux
	rm -f *.log
	rm -f *.pdf
	rm -f *.bbl
	rm -f *.bcf
	rm -f *.blg
	rm -f *.log
	rm -f *.toc
	rm -f *.run.xml
	rm -f *.out
