def is_numero_perfeito(n=0):
	if n == 0:
		return False
	return sum(x for x in range(1,n) if not n%x) == n

def numeros_perfeitos(n=0):
	cont = 0
	i = 0 
	while cont < n:
		if is_numero_perfeito(i):
			print i
			cont = cont+1
		i=i+1
   
numeros_perfeitos(8)
