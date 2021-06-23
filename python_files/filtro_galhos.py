
########################################
## Mostrar somente as folhas (faz um filtro)
########################################

def Filtro_Galhos(x):
	if Class(x)=='E':
		return False
	else:
		return True

# Plota a planta da erva-mate em 3D
Plot(plant_frame, VirtualLeaves=folha_virtual, DressingData=dress, Filter=Filtro_Galhos)

