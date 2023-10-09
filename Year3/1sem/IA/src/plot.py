import matplotlib.pyplot as plt
import pygame
import time
import math

class plot:

    def __init__(self) :
        self.nlinhas = 0
        self.ncolunas =0
        self.largura = 0
        self.cores = {1:(0,112,0),2:(112,0,0),3:(0,0,112),4:(112,112,0)}

    def createSquare(self,x, y, color,janela):
        pygame.draw.rect(janela, color, [x, y, math.ceil(self.largura/self.ncolunas), math.ceil(300/self.nlinhas)])


    def getMatrix (self,newCircuito):
        mapa=[]
        pInicial = []
        pFinais = []
        with open(newCircuito,"r") as f:
            for nl,line in enumerate(f.readlines()):
                l=[]
                for nc,c in enumerate(line):
                    if (c!="\n"): l.append(c)
                    if (c=="P"): pInicial.append((nl,nc))
                    if (c=="F"): pFinais.append((nl,nc))
                if (len(l)>1):mapa.append(l)
        self.nlinhas = len(mapa)
        self.ncolunas = len(mapa[0])
        return mapa,pInicial,self.nlinhas,self.ncolunas,pFinais   

    def plot(self,mapa):  
        self.largura = self.ncolunas*300/self.nlinhas
        screen = pygame.Surface((self.largura, 300))
        screen.fill('white')
        
        y=0
        for line in mapa:
            x=0
            for c in (line):
                if (c=="X"): self.createSquare(x, y, (0, 0, 0),screen)
                elif (c=="P"): self.createSquare(x,y,(0,112,0),screen)
                elif (c=="F"): self.createSquare(x,y,(204,0,0),screen)
                else: self.createSquare(x, y, (128,128,128),screen)
                x += self.largura/self.ncolunas
            y+= 300/self.nlinhas
        return screen 
    
    def plotSolucao(self,mapa,paths,screen):  
        self.largura = self.ncolunas*300/self.nlinhas
        image = pygame.Surface((self.largura, 300))
        image1 = pygame.Surface((self.largura, 300))
        image.fill('white')
        image1.fill('white')
        y=0
        counterP = 0
        for line in mapa:
            x=0
            for c in (line):
                if (c=="X"): self.createSquare(x, y, (0, 0, 0),image)
                elif (c=="P"):
                    counterP+=1
                    self.createSquare(x,y,self.cores[counterP],image)
                elif (c=="F"): self.createSquare(x,y,(204,0,0),image)
                else: self.createSquare(x, y, (128,128,128),image)
                x += self.largura/self.ncolunas
            y+= 300/self.nlinhas
        screen.blit(image, (image.get_rect(center= screen.get_rect().center)))
        pygame.display.update()
        i = 0
        maxlen=0
        for path in paths:
            if (len(path)> maxlen): maxlen=len(path)
        while maxlen>i:
            time.sleep(0.5)
            for j in range(len(paths)):
                path= paths[j]
                if (len(path)>i):
                    nodo = path[i]
                    y1 = (nodo.x)*(300/self.nlinhas)
                    x1 = (nodo.y)*(self.largura/self.ncolunas)
                    self.createSquare(x1, y1, self.cores[j+1], image)
            screen.blit(image, (image.get_rect(center= screen.get_rect().center)))
            pygame.display.update()
            for j in range(len(paths)):
                path= paths[j]
                if (len(path)>i+1):
                    nodo = path[i]
                    y1 = (nodo.x)*(300/self.nlinhas)
                    x1 = (nodo.y)*(self.largura/self.ncolunas)
                    self.createSquare(x1, y1, (128, 128, 128), image)
                    screen.blit(image, (image.get_rect(center= screen.get_rect().center)))
            i+=1
        
        return image               
        
         
        