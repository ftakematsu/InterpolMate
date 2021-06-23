from openalea.aml import *
from math import *

mtg_ervamate=MTG("D:/Mestrado_UEL/InterpolMateANN/temp/temp.mtg")


# Vetor com todas as plantas do MTG
plantas = VtxList(Scale=1)

dress = DressingData("D:/Mestrado_UEL/InterpolMateANN/python_files/ervamate.drf")


########################################
## Lendo a estrutura do galho
########################################

# Procedimentos para ler os atributos dos galhos
def Comp_Entreno(x):
	if Class(Complex(x,Scale=2))=="T":
		no = Sons(x,EdgeType="+")
		return Feature(no[0], "AlturaSup")
	else:
		if Class(Complex(x,Scale=2))=="S":
			return Feature(x, "CompSRam")
		else:
			return Feature(x, "CompEnt")

def Top_Diameter(x):
	if Class(Complex(x,Scale=2))=="T":
		return 4.5
	else:
		if Class(Complex(x,Scale=2))=="S":
			return 2.5
		else:
			ax = Axis(x)
			if (x==ax[-1]):
				return 0.4
			else:
				return None

def Bottom_Diameter(x):
	if Class(Complex(x,Scale=2))=="T" and Rank(x)==0:
		return 6
	else:
		if Class(Complex(x,Scale=2))=="S" and Rank(x)==0:
			return 4
		else:
			if Class(Complex(x,Scale=2))=="G":
				if Order(x)==2:
					return 1.2
				elif Order(x)==3:
					return 0.7
				elif Order(x)==4:
					return 0.5
				elif Order(x)==5:
					return 0.4
				else:
					return None
			else:
				return None

def Alfa_galho(x):
	if Class(Complex(x,Scale=2)) == "T":
		alpha = Feature(x,"AlfaTronco")
	elif Class(Complex(x,Scale=2)) =="S":
		alpha = Feature(x,"AlfaSup")
	elif Class(Complex(x,Scale=2)) == "G":
		alpha = Feature(x,"AlfaRamo")
	else: 
		alpha = None
	return alpha

# Armazena na estrutura da planta
plant_frame = PlantFrame(plantas, Scale=4, DressingData=dress, Length=Comp_Entreno, Alpha=Alfa_galho, BottomDiameter=Bottom_Diameter, TopDiameter=Top_Diameter)


########################################
## Lendo os atributos das folhas
########################################

#Procedimentos para ler dados de folhas de cada entreno
def VerticiloFolha(x):
	return 1

def Tem_folha(x):
	larg_folha = Feature(x,"AreaFolha")
	if (larg_folha!=None):
		return 1 # tem folha
	else:
		return 0 # nao tem folha


# Cor de folhas
#Black 0	White 1
#Green 2 	Red 3
#Blue 4  	Yellow 5
#Violet 6 	LightBlue 7
def CorFolha(x):
	return 2 # cor verde

def RetornaAlometriaComp(x):
	if (Feature(4,"Sexo")=="F"):
		if (Feature(4,"Local")=="SOMBRA"):
			return 1.957859
		else:
			return 2.173913
	else:
		if (Feature(4,"Local")=="SOMBRA"):
			return 1.9509476
		else:
			return 2.0895522

			
def RetornaAlometriaLarg(x):
	if (Feature(4,"Sexo")=="F"):
		if (Feature(4,"Local")=="SOMBRA"):
			return 0.937452
		else:
			return 0.844284
	else:
		if (Feature(4,"Local")=="SOMBRA"):
			return 0.9407736
		else:
			return 0.87837

			
	
def Diametro_folha(x): 
	larg_folha = sqrt(Feature(x,"AreaFolha"))*RetornaAlometriaComp(x)
	return larg_folha

def Larg_folha(x): 
	larg_folha = sqrt(Feature(x,"AreaFolha"))*RetornaAlometriaLarg(x)
	return larg_folha 

def Alfa_folha(x):
	f = Feature(x,"AlfaFolha")
	return f

def FilotaxiaFolha(x):
	return 137.5

# Armazena os atributos foliares no VirtualPattern
folha_virtual = VirtualPattern("Leaf",WhorlSize=VerticiloFolha, PatternNumber=Tem_folha, Alpha=Alfa_folha,Phyllotaxy=FilotaxiaFolha, Color=CorFolha, Length=Larg_folha, TopDiameter=Diametro_folha, BottomDiameter=Diametro_folha)


# Plota a planta da erva-mate em 3D
Plot(plant_frame, VirtualLeaves=folha_virtual, DressingData=dress)
raw_input("Pressione qualquer tecla para continuar...")