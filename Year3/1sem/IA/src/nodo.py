
class Node:
    #construtor do nodo, tem a posição e a velocidade
    def __init__(self, x,y,vx,vy):
        self.vx =vx
        self.vy = vy
        self.x = x
        self.y = y


    def __repr__(self):
        return f"Posição({self.x},{self.y}) Velocidade ({self.vx},{self.vy})" 

    def __eq__(self, other):
        return self.vx==other.vx and self.vy==other.vy and self.x==other.x and self.y==other.y

    def __hash__(self):
        return hash(self.vx + self.vy + self.x + self.y)
