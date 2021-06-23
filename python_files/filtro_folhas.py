
########################################
## Mostrar somente os galhos (faz um filtro)
########################################

def Filtro_Folhas(x):
	if Class(x)=='E':
		return True
	else:
		return False

# Plota a planta da erva-mate em 3D
Plot(plant_frame, VirtualLeaves=folha_virtual, DressingData=dress, Filter=Filtro_Folhas)

