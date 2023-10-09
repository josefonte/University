from nodo import Node
from Graph import Grafo
import math

def getLine(mapa,x1,y1,x2,y2):
    declive = (y1-y2)/(x1-x2)
    m = y1-(declive*x1)
    if (x1>x2):
        aux = x1
        x1 = x2
        x2 = aux
    if (y1>y2):
        aux = y1
        y1 = y2
        y2 = aux
    for i in range(x1+1,x2):
        j1 = math.floor(declive*i + m)
        j2 = math.ceil(declive*i + m)
        if (mapa[i][j1]=="X"): return True
        if (mapa[i][j2]=="X"): return True
    for j in range(y1+1,y2):
        i1 = math.floor((j- m)/declive)
        i2 = math.ceil((j- m)/declive)
        if (mapa[i1][j]=="X"): return True
        if (mapa[i2][j]=="X"): return True
    return False

def goForward(mapa,x1,y1,x2,y2):
    r=[]
    if (x1==x2 and y1<y2 and mapa[x1][y1+1]!="X"):
        r.append((x1,y1+1))
    if (x1==x2 and y1>y2 and mapa[x1][y1-1]!="X"):
        r.append((x1,y1-1))
    if (x1<x2 and mapa[x1+1][y1]!="X"):
        r.append((x1+1,y1))
    if (x1>x2 and mapa[x1-1][y1]!="X"):
        r.append((x1-1,y1))
    return r


def canGoThere(mapa,x1,y1,x2,y2):
    path_found = False
    if x1!=x2 and y1!=y2 and getLine(mapa,x1,y1,x2,y2): return False
    queue = [(x1,y1)]
    while (queue and not path_found):
        x,y = queue.pop(0)
        if (x==x2 and y==y2 ): path_found=True
        else:
            for p in goForward(mapa,x,y,x2,y2):
                queue.append(p)  
    return path_found
        

def getPossibitily(mapa,x,y,vx,vy,vis,nlinhas,ncolunas,r:list):
    xf =  x+vx
    yf = y+vy
    off=False
    if (xf>=0 and xf<nlinhas and 
        yf>=0 and yf<ncolunas and 
        canGoThere(mapa,x,y,xf,yf) and
        (xf,yf,vx,vy) not in vis):
        r.append((xf,yf,vx,vy,1))
    elif ((xf,yf,vx,vy) not in vis): off=True
    return off
        

def getPossibilities(mapa,x,y,vx,vy,vis,nlinhas,ncolunas,r):
    off = False
    #aceleracaoy 0
    if getPossibitily(mapa,x,y,vx,vy,vis,nlinhas,ncolunas,r): off = True
    #aceleracaoy -1
    if getPossibitily(mapa,x,y,vx,vy-1,vis,nlinhas,ncolunas,r): off=True
    #aceleracaoy 1
    if getPossibitily(mapa,x,y,vx,vy+1,vis,nlinhas,ncolunas,r): off=True
    return off

def getAllPossibilities(mapa,x,y,vx,vy,vis,nlinhas,ncolunas):
    r = []
    off = False
    #aceleracaox 0
    if getPossibilities(mapa,x,y,vx,vy,vis,nlinhas,ncolunas,r): off=True
    #aceleracaox -1
    if getPossibilities(mapa,x,y,vx-1,vy,vis,nlinhas,ncolunas,r): off=True
    #aceleracaox 1
    if getPossibilities(mapa,x,y,vx+1,vy,vis,nlinhas,ncolunas,r): off=True
    if off and (x,y,0,0) not in vis: r.append((x,y,0,0,25))
    return r




def criaGrafo(mapa,pInicial,nlinhas,ncolunas,pFinais):
    g = Grafo()
    queue = list()
    vis=set()
    for pi in pInicial:
        n = Node(pi[0],pi[1],0,0)
        queue.append(n)
        vis.add((n.x,n.y,n.vx,n.vy))
    while queue:
        nodo1 = queue.pop(0)
        for x1,y1,v1,v2,c1 in getAllPossibilities(mapa,nodo1.x,nodo1.y,nodo1.vx,nodo1.vy,vis,nlinhas,ncolunas):
                vis.add((x1,y1,v1,v2))
                nodo2= Node(x1,y1,v1,v2)
                g.add_edge(nodo1,nodo2,c1)
                queue.append(nodo2)
    for nodo in g.m_graph.keys(): g.addHeuristica(nodo,pFinais)
    return g
