#!/usr/bin/python3

import re
import subprocess


def write_tree():
    treeOutput = subprocess.check_output(["tree", "-d", "--noreport"])
    treeOutput = str(treeOutput, 'utf8')
    with open("README_skel.md", 'r') as skeleton:
        content = skeleton.read()
        new = re.sub('<TREE>', treeOutput, content)

        with open("README.md", 'w') as output:
            output.write(new)


if __name__ == '__main__':
    write_tree()
