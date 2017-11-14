DOCUMENT = rapport_bibliographique

$(DOCUMENT).pdf: $(DOCUMENT).tex
	pdflatex -shell-escape $(DOCUMENT).tex
	biber $(DOCUMENT)
	pdflatex -shell-escape $(DOCUMENT)
	pdflatex -shell-escape $(DOCUMENT)
