import pygame
import sys
import os
import main
from Graph import Grafo
import time

from plot import plot

import pygame as pg

from pygame.locals import *


pygame.init()  # initialize pygame

clock = pygame.time.Clock()

screen = pygame.display.set_mode((0,0),pygame.FULLSCREEN)
comprimento=pygame.display.get_window_size()[0]
altura=pygame.display.get_window_size()[1]


pygame.mixer.music.load("yt1s.com - Character Select  Super Smash Bros Music Extended.mp3")
pygame.mixer.music.play()

print(altura)
print(comprimento)
# Load the background image

bg = pygame.image.load(os.path.join("./", "background2.png"))
bg = pygame.transform.scale(bg, (comprimento,altura))

pygame.mouse.set_visible(1)
pygame.display.set_caption('VectorRace')

font = pygame.font.Font('freesansbold.ttf', 60)
font2 = pygame.font.Font('freesansbold.ttf', 30)
font3 = pygame.font.Font('freesansbold.ttf', 45)

#variável global
newCircuito ="./circuito1.txt"

# Load the cars image
car1 = pygame.image.load(os.path.join("./", "car1.png"),)
car2 = pygame.image.load(os.path.join("./", "car2.png"))
car3 = pygame.image.load(os.path.join("./", "car3.png"),)
car4 = pygame.image.load(os.path.join("./", "car4.png"))   
car5 = pygame.image.load(os.path.join("./", "car5.png"))   
car1_1 = pygame.transform.scale(car1, (450,450))
car3_1 = pygame.transform.scale(car3, (450,450))
car4_1 = pygame.transform.scale(car4, (450,450))
car5_1 = pygame.transform.scale(car5, (450,450)) 
car2_1 = pygame.transform.scale(car2, (450,450))



# Menu principal
def main_menu () :

    global newCircuito
    global largura
    global altura

    while True:

        clock.tick(60)
        screen.blit(bg, (0, 0))

        x, y = pygame.mouse.get_pos()

        # Menu
        text = font.render('Menu', True, 'white')
        screen.blit(text, ((comprimento/2)- comprimento*0.05,altura/4))

        # Quit
        colour= 'white'
        xquit=comprimento*9/10
        yquit= altura*3/4
        if ((x>xquit and x< xquit+80) and (y<yquit+80 and y>yquit)): 
            colour = (255,181,21)
        quittext = font2.render('Sair', True, (colour))    
        screen.blit(quittext, (xquit, yquit))

        # Circuito Atual
        # Box
        #circbox= pg.Surface((310,110))
        #circbox.fill('black')
        #screen.blit(circbox, (comprimento*2/8, altura*3/7))

        
        # Car1
        colour= 'white'
        xcar1= comprimento/10 +180
        ycar1= altura/5+300
        if ((x>xcar1 and x< xcar1+210) and (y<ycar1+50 and y>ycar1)): 
            colour = (255,181,21)

        circtext = font2.render('Circuito Atual', True, colour)
        screen.blit(car1,(comprimento/10, altura/5))
        screen.blit(circtext, (xcar1, ycar1))

        # Car2
        colour= 'white'
        xcar2= comprimento/2+230
        ycar2= altura/5+300
        if ((x>xcar2 and x< xcar2+210) and (y<ycar2+50 and y>ycar2)): 
            colour = (255,181,21)

        circtext2 = font2.render('Mudar circuito', True, colour)
        screen.blit(car2,(comprimento/2+50, altura/5))
        screen.blit(circtext2, (xcar2,ycar2))


        # Eventos
        click = False
        for event in pygame.event.get():
            if ((x>xquit and x< xquit+80) and (y<yquit+80 and y>yquit)): 
                if click: 
                    sys.exit()
            if ((x>xcar1 and x< xcar1+210) and (y<ycar1+50 and y>ycar1)):     
               if click: 
                    circuito()
            if ((x>xcar2 and x< xcar2+210) and (y<ycar2+50 and y>ycar2)):    
                if click: 
                    newCircuito = insertCircuito()     
            if event.type == MOUSEBUTTONDOWN:
                if event.button ==1 :
                    click = True                    
            if event.type == pygame.QUIT:
                sys.exit()

        pygame.display.update()


# Menu Circuito Atual
def circuito():

    global newCircuito

    running = True 
    screen.blit(bg, (0, 0))

    while running:    

        x, y = pygame.mouse.get_pos()   

        # Circuito 1
        text = font3.render('Circuito', True, 'white')
        screen.blit(text, ((comprimento/2)- comprimento*0.1,altura/4-20))

        # Criar a imagem com o circuito
        p = plot()
        mapa,pInicial,nlinhas,ncolunas,pFinais=p.getMatrix(newCircuito)
        image=p.plot(mapa)
        screen.blit(image, (image.get_rect(center= screen.get_rect().center))) 

        # Continuar
        colour= 'white'
        xcontinuar=comprimento*9/10-80
        ycontinuar= altura*3/4
        if ((x>xcontinuar and x< xcontinuar+160) and (y<ycontinuar+80 and y>ycontinuar)): 
            colour = (255,181,21)
        continuartext = font2.render('Continuar', True, (colour))    
        screen.blit(continuartext, (xcontinuar, ycontinuar))

        # Voltar
        colour= 'white'
        xvoltar=comprimento*1/10-50
        yvoltar= altura*3/4
        if ((x>xvoltar and x< xvoltar+100) and (y<yvoltar+80 and y>yvoltar)): 
            colour = (255,181,21)
        voltartext = font2.render('Voltar', True, (colour))    
        screen.blit(voltartext, (xvoltar, yvoltar)) 

        # A criar grafo...
        voltartext = font2.render('A criar grafo...', True, (255,255,255))    
 
        criar= True
        click = False
        for event in pygame.event.get():
            if ((x>xcontinuar and x< xcontinuar+160) and (y<ycontinuar+80 and y>ycontinuar)):  
                if click: 
                    #screen.blit(voltartext, ((comprimento/2)- comprimento*0.1, altura*3/4))
                    grafo = main.criaGrafo(mapa,pInicial,nlinhas,ncolunas,pFinais)
                    menuAlgoritmos(grafo)
            if ((x>xvoltar and x< xvoltar+100) and (y<yvoltar+80 and y>yvoltar)): 
                if click: 
                    running = False
            if event.type == MOUSEBUTTONDOWN:
                if event.button ==1 :
                    click = True          

        pygame.display.update()
  

def insertCircuito():
    global newCircuito
    running = True 
    text = ""

    while running:

        screen.blit(bg, (0, 0))
        x, y = pygame.mouse.get_pos()  

        # Text 
        inseretext = font.render('Insira o caminho para o circuito:', True, 'white')
        screen.blit(inseretext, ((comprimento/2 -500,altura/4-20)))
 
        # Voltar
        colour= 'white'
        xvoltar=comprimento*1/10-50
        yvoltar= altura*3/4
        if ((x>xvoltar and x< xvoltar+100) and (y<yvoltar+80 and y>yvoltar)): 
            colour = (255,181,21)
        voltartext = font2.render('Voltar', True, (colour))    
        screen.blit(voltartext, (xvoltar, yvoltar)) 

        click = False

        for event in pygame.event.get():       
            if event.type == pygame.QUIT:
                running = False
            if ((x>xvoltar and x< xvoltar+100) and (y<yvoltar+80 and y>yvoltar)): 
                if click:
                    running = False
            if event.type == pygame.MOUSEBUTTONDOWN:
                click = True
                text = ""  
            if event.type == pygame.KEYDOWN :
                if event.key == pygame.K_RETURN:
                    running=False
                    #print(text)
                if event.key == pygame.K_BACKSPACE:
                    text =  text[:-1]
                else:
                    text += event.unicode              
        
        #screen.fill(0)  
        text_surf = font2.render(text, True, (255,181,21))
        screen.blit(text_surf, text_surf.get_rect(center = screen.get_rect().center))
        #screen.blit(text_surf, (400,400))
        pygame.display.update()
    if os.path.exists(text[:-1]): return text[:-1]
    else : return newCircuito


def menuAlgoritmos(grafo):
    global newCircuito
    running = True
    continuar = True
    paths = []
    pl=plot()
    mapa,posInicial,_,_,_=pl.getMatrix(newCircuito)
    for pi in posInicial:
        while running:   
        
            screen.blit(bg, (0, 0))
            x, y = pygame.mouse.get_pos()   

            # Voltar
            colour= 'white'
            xvoltar=comprimento*1/10-50
            yvoltar= altura*3/4
            if ((x>xvoltar and x< xvoltar+100) and (y<yvoltar+80 and y>yvoltar)): 
                colour = (255,181,21)
            voltartext = font2.render('Voltar', True, (colour))    
            screen.blit(voltartext, (xvoltar, yvoltar)) 

            # Text Algoritmos
            algotext = font3.render('Algoritmos', True, 'white')
            screen.blit(algotext, ((comprimento/2 -140,altura/4-20)))

            # Car1_1
            colour= 'white'
            xcar1_1= comprimento/10-30 +160
            ycar1_1= altura/9 +215
            if ((x>xcar1_1 and x< xcar1_1+210) and (y<ycar1_1+50 and y>ycar1_1)): 
                colour = (255,181,21)

            circtext = font2.render('BFS', True, colour)
            screen.blit(car1_1,(comprimento/10-30, altura/9))
            screen.blit(circtext, (xcar1_1, ycar1_1))

            # Car2_1
            colour= 'white'
            xcar2_1= comprimento*2/3 + 145
            ycar2_1= altura/9 + 215
            if ((x>xcar2_1 and x< xcar2_1+210) and (y<ycar2_1+50 and y>ycar2_1)): 
                colour = (255,181,21)

            circtext2 = font2.render('DFS', True, colour)
            screen.blit(car2_1,(comprimento*2/3, altura/9))
            screen.blit(circtext2, (xcar2_1,ycar2_1))

            # Car3_1
            colour= 'white'
            xcar3_1= comprimento/3 +70 +135
            ycar3_1= altura/4 +220
            if ((x>xcar3_1 and x< xcar3_1+210) and (y<ycar3_1+50 and y>ycar3_1)): 
                colour = (255,181,21)

            circtext2 = font2.render('Greedy', True, colour)
            screen.blit(car3_1,(comprimento/3 +70, altura/4))
            screen.blit(circtext2, (xcar3_1,ycar3_1))

            # Car4_1
            colour= 'white'
            xcar4_1= comprimento/10-30 +160
            ycar4_1=  altura/2-100+220
            if ((x>xcar4_1 and x< xcar4_1+210) and (y<ycar4_1+50 and y>ycar4_1)): 
                colour = (255,181,21)

            circtext = font2.render('A*', True, colour)
            screen.blit(car4_1,(comprimento/10-30, altura/2-100))
            screen.blit(circtext, (xcar4_1, ycar4_1))

            # Car5_1
            colour= 'white'
            xcar5_1= comprimento*2/3 + 145
            ycar5_1= altura/2-100+220
            if ((x>xcar5_1 and x< xcar5_1+210) and (y<ycar5_1+50 and y>ycar5_1)): 
                colour = (255,181,21)

            circtext2 = font2.render('Dijkstra', True, colour)
            screen.blit(car5_1,(comprimento*2/3, altura/2-100))
            screen.blit(circtext2, (xcar5_1,ycar5_1))

            click = False 
            algrt = None
            for event in pygame.event.get():

                if ((x>xvoltar and x< xvoltar+100) and (y<yvoltar+80 and y>yvoltar) and click):  
                    running = False
                    continuar = False
                    break
                #BFS
                if ((x>xcar1_1 and x< xcar1_1+210) and (y<ycar1_1+50 and y>ycar1_1) and click): 
                    algrt = 'bfs' 
                    p = algoritmo(grafo, algrt,pi,paths)
                    paths.append(p)
                    running = False
                    break
                #DFS
                elif ((x>xcar2_1 and x< xcar2_1+210) and (y<ycar2_1+50 and y>ycar2_1)and click):  
                    algrt = 'dfs'
                    p = algoritmo(grafo, algrt,pi,paths)
                    paths.append(p)
                    running = False
                    break

                #Greedy
                elif ((x>xcar3_1 and x< xcar3_1+210) and (y<ycar3_1+50 and y>ycar3_1) and click): 
                    algrt = 'greedy' 
                    p = algoritmo(grafo, algrt,pi,paths)
                    paths.append(p)
                    running = False
                    break

                #A*
                elif ((x>xcar4_1 and x< xcar4_1+210) and (y<ycar4_1+50 and y>ycar4_1)and click):       
                    algrt = 'A*'
                    p = algoritmo(grafo, algrt,pi,paths)
                    paths.append(p)
                    running = False
                    break
                #Dijkstra
                elif ((x>xcar5_1 and x< xcar5_1+210) and (y<ycar5_1+50 and y>ycar5_1) and click):     
                    algrt = 'dijkstra'
                    p = algoritmo(grafo, algrt,pi,paths)
                    paths.append(p)
                    running = False
                    break

                if event.type == MOUSEBUTTONDOWN:
                    if event.button ==1 :
                        click = True          

            pygame.display.update()
        screen.blit(bg, (0, 0))
        running=True
    
    flag=1
    while running and continuar:    
        x, y = pygame.mouse.get_pos()   
        if (flag):
            pl.plotSolucao(mapa,paths,screen)
            flag=0
        #screen.blit(image, (image.get_rect(center= screen.get_rect().center)))
        # Voltar
        colour= 'white'
        xvoltar=comprimento*1/10-50
        yvoltar= altura*3/4
        if ((x>xvoltar and x< xvoltar+100) and (y<yvoltar+80 and y>yvoltar)): 
            colour = (255,181,21)
        voltartext = font2.render('Voltar', True, (colour))    
        screen.blit(voltartext, (xvoltar, yvoltar)) 
        # Continuar
        colour= 'white'
        xrepetir=comprimento*9/10-80
        yrepetir= altura*3/4
        if ((x>xrepetir and x< xrepetir+160) and (y<yrepetir+80 and y>yrepetir)): 
            colour = (255,181,21)
        continuartext = font2.render('Repetir', True, (colour))    
        screen.blit(continuartext, (xrepetir, yrepetir))
        click = False
        for event in pygame.event.get():
            if ((x>xvoltar and x< xvoltar+100) and (y<yvoltar+80 and y>yvoltar) and click): 
                running = False
            if ((x>xrepetir and x< xrepetir+160) and (y<yrepetir+80 and y>yrepetir) and click):
                flag=1        
            if event.type == MOUSEBUTTONDOWN:
                if event.button ==1 :
                    click = True          
        pygame.display.update()
    if continuar: menuAlgoritmos(grafo)



def algoritmo(grafo:Grafo, algrt,posInicial,paths):
    screen.blit(bg, (0, 0))   


    x, y = pygame.mouse.get_pos()   
    # Criar a imagem com o circuito
    p = plot()
    _,_,_,_,posFinal=p.getMatrix(newCircuito)      
    if (algrt == 'bfs'):
        path,c = grafo.procura_BFS(posInicial,posFinal,paths)
        #text = font3.render('BFS', True, 'white')
        #screen.blit(text, ((comprimento/2+100)- comprimento*0.1,altura/4-20))
    elif(algrt == 'dfs'):
         path,c = grafo.procura_DFS(posInicial,posFinal,paths)
         #text = font3.render('DFS', True, 'white')
         #screen.blit(text, ((comprimento/2+100)- comprimento*0.1,altura/4-20))
    elif(algrt == 'greedy'):
        path,c = grafo.greedySearch(posInicial,posFinal,paths)
        #text = font3.render('Greedy', True, 'white')
       # screen.blit(text, ((comprimento/2+70)- comprimento*0.1,altura/4-20))
    elif(algrt == 'A*'):
        path,c = grafo.aStarSearch(posInicial,posFinal,paths)
        #text = font3.render('A*', True, 'white')
        #screen.blit(text, ((comprimento/2+150)- comprimento*0.1,altura/4-20))
    elif(algrt == 'dijkstra'):
        path,c = grafo.dijkstra(posInicial,posFinal,paths)  
        #text = font3.render('Dijkstra', True, 'white')
        #screen.blit(text, ((comprimento/2+80)- comprimento*0.1,altura/4-20))  
    return path        




main_menu()        