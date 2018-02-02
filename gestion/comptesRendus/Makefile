SRCS=$(wildcard *CR*.tex)
OBJS=$(SRCS:.tex=.pdf)

all: $(OBJS)

%.pdf: %.tex
	pdflatex $<
