import matplotlib.pyplot as plt
import csv

x = []
y = []

with open('1586275664919-solverLog.txt','r') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    skip = False
    for row in plots:
        if skip == False:
            skip = True
            continue
        x.append(float(row[0]))
        y.append(float(row[2]))

plt.plot(x,y, label='Loaded from file!')
plt.xlabel('t')
plt.ylabel('v')
plt.title('Velocity Plot')
plt.legend()
plt.show()
