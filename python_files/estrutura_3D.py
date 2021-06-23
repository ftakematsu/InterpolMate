
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

