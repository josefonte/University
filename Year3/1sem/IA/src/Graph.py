#LICENCIATURA EM ENGENHARIA INFORMÁTICA
#MESTRADO integrado EM ENGENHARIA INFORMÁTICA

#Inteligência Artificial
#2022/23

#Draft Ficha 3

# Classe grafo para representaçao de grafos,
import math
from queue import Queue


from nodo import Node



def inPrevPath(pathsFound,nodo2,path):
    r = False
    lp = len(path)
    for p in pathsFound:
        if (len(p)>lp and p[lp].x==nodo2.x and p[lp].y==nodo2.y):
            return True
    return r

def isFinal(x,y,pFinais):
    for p in pFinais:
        if (p[0]==x and p[1]==y): return True
    return False

class Grafo:

    def __init__(self):
        self.heuristica = {}
        self.m_nodes = []
        self.m_graph = {}  # dicionario para armazenar os nodos e arestas

    #############
    # Escrever o grafo como string
    #############
    def __str__(self):
        out = ""
        for key in self.m_graph.keys():
            out = out + "node" + str(key) + ": " + str(self.m_graph[key]) + "\n"
            return out

    ##############################3
    # Imprimir arestas
    ############################333333

    def imprime_aresta(self):
        listaA = ""
        for nodo in self.m_graph.keys():
            for (nodo2, custo) in self.m_graph[nodo]:
                listaA = listaA + str(nodo) + " -> " + str(nodo2) + " custo:" + str(custo) + "\n"
        return listaA

    #############################
    # Adicionar   aresta no grafo
    #############################

    def add_edge(self,n1, n2, weight):
        if (n1 not in self.m_nodes):
            self.m_nodes.append(n1)
            self.m_graph[n1] = list()
        if (n2 not in self.m_nodes):
            self.m_nodes.append(n2)
            self.m_graph[n2] = list()

        self.m_graph[n1].append((n2, weight))
        
     #############################
    # Adicionar   heurística no grafo
    #############################
    
    def getDist(self,nodo1,nodo2):
        return math.sqrt(pow(nodo1.x-nodo2.x,2) + pow(nodo1.y-nodo2.y,2))
        
        

    def addHeuristica(self,nodo,pFinais):
        minDis = math.inf
        for p in pFinais:
            dist = math.sqrt(pow(nodo.x-p[0],2) + pow(nodo.y-p[1],2))
            if (dist< minDis): minDis = dist
        self.heuristica[nodo] = minDis

    #############################
    # Devolver nodos do Grafo
    ############################

    def getNodes(self):
        return self.m_nodes


    #####################################################
    # Procura BFS
    ######################################################

#BFS
    def procura_BFS(self, posInicial, posFinais, pathsFound):
        # nodos visitados
        visited = set()
        fila = Queue()

        # adicionar o nodo inicial à fila e aos visitados
        fila.put((Node(posInicial[0],posInicial[1],0,0),0,[]))
        visited.add(Node(posInicial[0],posInicial[1],0,0))
        while not fila.empty():
            nodo_atual,c,path = fila.get()
            # ver se chegou à posição final
            if isFinal(nodo_atual.x,nodo_atual.y,posFinais):
                return path+[nodo_atual] , c
            else:
                #ver adjacentes e acrescentar os não visitados à fila
                for (nodo2, peso) in self.m_graph[nodo_atual]:
                    if nodo2 not in visited and not inPrevPath(pathsFound,nodo2,path+[nodo_atual]):
                        fila.put((nodo2,c+peso,path+[nodo_atual]))
                        visited.add(nodo2)
        return None




     ################################################################################
    # Procura DFS
    ####################################################################################

    def procura_DFS(self, start, end, pathsFound, path=[], visited=set(),custo=0,begginng=True):
        if (begginng):
            start = Node(start[0],start[1],0,0)
            visited = {start}
            path=[]
        if isFinal(start.x,start.y,end):return path+[start], custo
        for (nodo2, peso) in self.m_graph[start]:
            if nodo2 not in visited and not inPrevPath(pathsFound,nodo2,path+[start]):
                visited.add(nodo2)
                resultado = self.procura_DFS(nodo2, end, pathsFound,path+[start], visited,custo+peso,False)
                if resultado: return resultado
        return None
    

    def greedySearch(self, start, end, pathsFound, path=[], visited=set(),custo=0,begginng=True):
        if (begginng):
            path=[]
            start = Node(start[0],start[1],0,0)
            visited = {start}
        if isFinal(start.x,start.y,end): 
            return path+[start], custo
        nodos = self.m_graph[start]
        nodos.sort(key=lambda p: self.heuristica[p[0]])
        for (nodo2, peso) in nodos:
            if nodo2 not in visited and not inPrevPath(pathsFound,nodo2,path+[start]):
                visited.add(start)
                resultado = self.greedySearch(nodo2, end, pathsFound,path+[start], visited,custo+peso,False)
                if resultado is not None:
                    return resultado
        return None
        
    
    def aStarSearch(self,inicial,final,pathsFound):
        posInicial = Node(inicial[0],inicial[1],0,0)
        queue=[(posInicial,[],0,self.heuristica[posInicial],0)]
        vis={posInicial}
        r=None
        while queue:
            queue.sort(key=lambda p: p[3])
            n,p,c,ce,cf = queue.pop(0)
            newp = p + [n]
            if isFinal(n.x,n.y,final):
                r=newp,cf
                break
            for nodo,custo in self.m_graph[n]:
                if nodo not in vis and not inPrevPath(pathsFound,nodo,newp):
                    vis.add(nodo)
                    queue.append((nodo,newp,c+custo*self.getDist(n,nodo),c+self.heuristica[nodo],cf+custo))
        return r

    def dijkstra(self,inicial,final,pathsFound):
        # nodos visitados
        visited = set()
        fila = list()

        # adicionar o nodo inicial à fila e aos visitados
        fila.append((Node(inicial[0],inicial[1],0,0),0,[]))
        visited.add(Node(inicial[0],inicial[1],0,0))
        while fila:
            fila.sort(key=lambda p: p[1])
            nodo_atual,c,path = fila.pop(0)
            # ver se chegou à posição final
            if isFinal(nodo_atual.x,nodo_atual.y,final):
                return path+[nodo_atual] , c
            else:
                #ver adjacentes e acrescentar os não visitados à fila
                for (nodo2, peso) in self.m_graph[nodo_atual]:
                    if nodo2 not in visited and not inPrevPath(pathsFound,nodo2,path+[nodo_atual]):
                        fila.append((nodo2,c+peso,path+[nodo_atual]))
                        visited.add(nodo2)
        return None
        