
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


