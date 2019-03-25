def main(begin, end):
    for i in range(begin, end):
        fibb = fibbo(i)
        print(str(i) + ': ' + str(fibb))


def fibbo(n):
    if n == 0:
        return 0
    elif n == 1:
        return 1
    else:
        return fibbo(n - 1) + fibbo(n - 2)
