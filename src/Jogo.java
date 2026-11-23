record Position (int line,int col) {
	
	boolean isAdjacent(Position p) {
		return Math.abs(line - p.line())==1 && Math.abs(col - p.col()) == 1;   //verificar se esta nas diagonais
	}
}
class Jogo{
	private static final int EMPTY=0;
	private static final int BRANCAS=1;
	private static final int PRETAS=2;
	
	public int COLS;
	public int numPeças;
	public int numPeçasBrancas;
	public int numPeçasPretas;
	public int []grid;
	public boolean brancasJogam;
	public boolean isSelected;
	public int selected;
	public Position selecionada;
	private Position [] obrigatoria = new Position [100000];
	private int obrigatoriaCounter=0;
	private Position comida;
	private Position [] podeComer= new Position [100000];
	private int podeComerCounter=0;
	
	void initializeBoard() {  													//posições iniciais
	    int brancasColocadas=0;
	    int pretasColocadas=0;
	    	 
		for (int i = 0; i < COLS/2+1; i++) {  										//linhas
	        for (int j = 0; j < COLS; j++) {									//colunas
	            if (isBlackSquare(new Position(i, j)) && pretasColocadas <numPeças) {  				//parte superior
	                grid[i * COLS + j] = PRETAS;
	                pretasColocadas++;
	            	}
	            }
	        }
		for (int i=COLS-1;i>=COLS/2+1;i--) {
			for(int j=COLS-1;j>=0;j--) {
				if (isBlackSquare(new Position(i, j)) && brancasColocadas < numPeças) {		//parte inferior
	                grid[i * COLS + j] = BRANCAS;
	                brancasColocadas++;
					}
				}
			}
	}
	
	Jogo (int numCols, int numPeças, int numPeçasBrancas, int numPeçasPretas,boolean brancasTurn,int[]grid){
		this.COLS = numCols;
		this.numPeças= numPeças;
		this.numPeçasBrancas=numPeçasBrancas;
		this.numPeçasPretas=numPeçasPretas;
		this.grid= grid;
		brancasJogam = brancasTurn;
		int c=0;
		for(int i=0;i<grid.length;i++)
			if(grid[i]==EMPTY)
				c++;
		if(c==grid.length) {
		initializeBoard();
		}
	}
	int numeroColunas() {
		return COLS;
	}

	boolean brancasTurn() {
		return brancasJogam;
	}
	int numeroPeçasWhite() {
		return numPeçasBrancas;
	}
	int numeroPeçasBlack() {
		return numPeçasPretas;
	}
	boolean isBlackSquare(Position p) {											//quadrados pretos
		if(COLS%2==0){
			
		if(p.line()%2==0 && (p.line()*COLS+p.col())%2 !=0) {					//linhas pares
			return true;
		}
		if(p.line()%2!=0 &&(p.line()*COLS+p.col())%2==0) {						//linhas impares
			return true;
		}else
			return false;
			}else
				if(p.line()%2==0 && (p.line()*COLS+p.col())%2!=0) {					//linhas pares
					return true;
				}
				if(p.line()%2!=0 &&(p.line()*COLS+p.col())%2!=0) {						//linhas impares
					return true;
		}return false;
	}
	boolean isValid (Position p) {												//posição clicada é valida?
		return p.line() >=0 && p.line() < COLS && p.col() >=0 && p.col() < COLS;
	}
	boolean isSelected () {													//alguma peça esta selecionada?
		return isSelected==true;
	}
	boolean isBrancas(Position p) {										//verificar se é brancas na posição
		return grid[p.line()*COLS+p.col()]==BRANCAS;
		}
	boolean isEmpty(Position p){										//verificar se esta vazia a posição
		assert (isValid(p));
		if (grid[p.line()*COLS+p.col()]==EMPTY)
			return true;
		return false;
	}
	Position [] empty() {									//todas as posições vazias
		int c=0;
		for (int i=0; i<grid.length;i++) 
			if(grid[i]==EMPTY)
				c++;
		Position[] empty= new Position[c];
		int j=0;
		for(int i=0;i<grid.length;i++)
			if(grid[i]==EMPTY) {
				empty[j]= new Position(i/COLS, i%COLS);
				j++;
			}
		return empty;
	}
	boolean pieceCanMove(Position p) {							//se a peça consegue se mover
		Position topRight= new Position(p.line()-1,p.col()+1);
		Position topLeft= new Position (p.line()-1,p.col()-1);
		Position bottomRight= new Position (p.line()+1, p.col()+1);
		Position bottomLeft= new Position (p.line()+1,p.col()-1);
		if(brancasTurn()) {
			if(isValid(topRight)) 
				if(grid[topRight.line()*COLS+topRight.col()]==EMPTY)
					return true;
			if (isValid(topLeft))
				if(grid[topLeft.line()*COLS+topLeft.col()]==EMPTY)
					return true;
		}else if(!brancasTurn()) {
			if(isValid(bottomLeft))
				if(grid[bottomLeft.line()*COLS+bottomLeft.col()]==EMPTY)
					return true;
			if(isValid(bottomRight))
				if(grid[bottomRight.line()*COLS+bottomRight.col()]==EMPTY)
					return true;
		}
			return false;
	}
	void playRandom() {						//escolhe uma posiçao aleatoria para jogar
		if(eatPossible()) {
			double r= Math.random();
			int p=(int)(r*obrigatoriaCounter);
			if(SelectedCome(obrigatoria[p],selecionada)) {
				play(obrigatoria[p]);
			}else {
				while(!SelectedCome(obrigatoria[p], selecionada) || obrigatoria[p]==null) {
					r=Math.random();
					p= (int)(r*obrigatoriaCounter);
					if(obrigatoria[p]!=null) {
					play(obrigatoria[p]);
					return;
					}
				}
			}
		}
		Position[] empty= empty();
		assert empty.length >0;
		double r= Math.random();
		int p= (int)(r*empty.length);
		while(grid[selected]!=EMPTY) {
			r= Math.random();
			p=(int)(r*empty.length);
			play(empty[p]);
		}
	}
	Position [] branca() {					//todas as posições que teem brancas
		int c=0;
		for (int i=0; i<grid.length;i++) 
			if(grid[i]==BRANCAS)
				c++;
		Position[] brancas= new Position[c];
		int j=0;
		for(int i=0;i<grid.length;i++)
			if(grid[i]==BRANCAS) {
				brancas[j]= new Position(i/COLS, i%COLS);
				j++;
			}
		return brancas;
	}
	void selectRandomBranca() {				//seleciona uma branca aleatoria
		Position[] brancas= branca();
		assert brancas.length >0;
		double r= Math.random();
		int p= (int)(r*brancas.length);
		if(eatPossible()) {
			if(PPodeComer(brancas[p])) {
				play(brancas[p]);
				return;
			}else {
				while(!PPodeComer(brancas[p])) {
					r=Math.random();
					p=(int)(r*brancas.length);
					if(PPodeComer(brancas[p])) {
						play(brancas[p]);
						return;
					}
				}
			}
			
		}
		if(pieceCanMove(brancas[p])) {
			play(brancas[p]);
			return;
		}else {
			while(!pieceCanMove(brancas[p])) {
				r=Math.random();
				p= (int)(r*brancas.length);
				if(pieceCanMove(brancas[p])) {
					play(brancas[p]);
					return;
				}
			}
		}
	}
	Position [] preta() {						//todas as posições pretas
		int c=0;
		for (int i=0; i<grid.length;i++) 
			if(grid[i]==PRETAS)
				c++;
		Position[] pretas= new Position[c];
		int j=0;
		for(int i=0;i<grid.length;i++)
			if(grid[i]==PRETAS) {
				pretas[j]= new Position(i/COLS, i%COLS);
				j++;
			}
		return pretas;
	}
	void selectRandomPretas() {				//seleciona uma preta random
		Position[] pretas= preta();
		assert pretas.length >0;
		double r= Math.random();
		int p= (int)(r*pretas.length);
		if(eatPossible()) {
			if(PPodeComer(pretas[p])) {
				play(pretas[p]);
				return;
			}else {
				while(!PPodeComer(pretas[p])) {
					r=Math.random();
					p=(int)(r*pretas.length);
					if(PPodeComer(pretas[p])) {
						play(pretas[p]);
						return;
					}
				}
			}		
		}
		if(pieceCanMove(pretas[p])) {
			play(pretas[p]);
			return;
		}else {
			while(!pieceCanMove(pretas[p])) {
				r=Math.random();
				p= (int)(r*pretas.length);
				if(pieceCanMove(pretas[p])) {
					play(pretas[p]);
					return;
				}
			}
		}
	}
	
	void Select (Position p) {
		assert isValid (p);
		if(isSelected==true && p.equals(selecionada)) {		//caso estaja selecionada e clique novamente na mesma
			isSelected=false;
			selecionada= null;
			return;
		} else if(isSelected==true) {						//caso estaja selecionada e clique numa posição invalida
			return;
		}else if(grid[p.line()*COLS+p.col()]== BRANCAS && brancasJogam==true) {		//selecionar peça branca
			selected = p.line()*COLS+p.col();
			isSelected =true;
			selecionada = new Position (p.line(), p.col());
		}else if(grid[p.line()*COLS+p.col()]== PRETAS && brancasJogam==false) {		//selecionar peça preta
			selected =p.line()*COLS+p.col();
			isSelected=true;
			selecionada = new Position (p.line(), p.col());
		}else {
			isSelected=false;												//selecionar a peça na vez errada
		}
	}
				
	boolean PositionCanEat (Position p) {//se uma certa posição consegue comer outra peça
		boolean canEat = false;
		Position [] end = new Position[4];		//posiçoes da jogada para comer
		end [0]= new Position (p.line()+2, p.col()+2);
		end [1]= new Position (p.line()+2, p.col()-2);
		end [2]= new Position (p.line()-2, p.col()+2);
		end [3]= new Position (p.line()-2, p.col()-2);
		Position [] middle = new Position [4];		//posições da peça a ser comida
		middle [0] = new Position ((p.line()+ end[0].line())/2, (p.col()+end[0].col())/2);
		middle [1] = new Position ((p.line()+ end[1].line())/2, (p.col()+end[1].col())/2);
		middle [2] = new Position ((p.line()+ end[2].line())/2, (p.col()+end[2].col())/2);
		middle [3] = new Position ((p.line()+ end[3].line())/2, (p.col()+end[3].col())/2);
		
		if((brancasTurn() && isBrancas(p)) || (!brancasTurn() && !isBrancas(p) && !isEmpty(p))) {	//se for a vez da peça nessa posição
			for (int i=0; i<4; i++) {
				if(isValid(end[i]) && isValid(middle[i])) {			//verifica se as posições sao validas				
				if((brancasTurn() && !isBrancas(middle [i]) && !isEmpty(middle[i])) || (!brancasTurn() && isBrancas(middle [i]))) {	//verifica se a peça a ser comida nao é da mesma equipa
					if(isEmpty(end[i])) {		//verifica se posição final esta vazia
						if(brancasTurn() && end[i].line()<p.line()) {
							obrigatoria[obrigatoriaCounter]= end[i];	// posição final passa a ser a jogada obrigatoria
							comida= middle[i];		// a posiçao da peça a ser comida é guardada
							obrigatoriaCounter++;
							canEat= true;	
						}
						if(!brancasTurn()&& end[i].line()>p.line()) {
							obrigatoria[obrigatoriaCounter]= end[i];	// posição final passa a ser a jogada obrigatoria
							comida= middle[i];		// a posiçao da peça a ser comida é guardada
							obrigatoriaCounter++;
							canEat= true;			//exite uma possibilidade para comer
						}
					}
				}
			}
		}
	}
		return canEat;
	}
	boolean eatPossible () {		//varre todas as posições para ver se existe alguma que possa comer outra
		boolean canEat=false;
		for (int line=0; line<COLS; line++) {
			for(int col=0; col<COLS; col++) {
				Position p= new Position (line,col);
				if(isValid(p) && ((brancasTurn() && isBrancas(p)) || (!brancasTurn() && !isBrancas(p) && !isEmpty(p)))){
					if(PositionCanEat(p)) {
						podeComer[podeComerCounter] = p;   	//posição que pode comer outra
						podeComerCounter++;
						canEat=true;			//exite uma possibilidade para cojer
					}		
				}
			}
		}
		return canEat;
	}
	boolean isPObrigatoria(Position p) {
		boolean isObrigatoria=false;
		for (int i=0; i<obrigatoriaCounter;i++) {
			if(obrigatoria[i].equals(p))
				isObrigatoria=true;
		}
		return isObrigatoria;
	}
	boolean PPodeComer(Position p) {
		boolean Comer=false;
		for (int i=0; i<podeComerCounter;i++) {
			if(podeComer[i].equals(p))
				Comer=true;
		}
		return Comer;
	}
	boolean SelectedCome (Position p, Position selecionada) {		//se a peça selecionada consegue comer atraves do ponto final
		boolean come=false;
		Position [] target= new Position [4];
		target [0]= new Position (selecionada.line()+2, selecionada.col()+2);
		target [1]= new Position (selecionada.line()+2, selecionada.col()-2);
		target [2]= new Position (selecionada.line()-2, selecionada.col()+2);
		target [3]= new Position (selecionada.line()-2, selecionada.col()-2);
		for (int i=0; i<4;i++) {
			if(target[i].equals(p))
				come=true;
		}
		return come;
	}
	boolean checkMoves() {
		boolean moves=false;
		if(eatPossible())
			return true;
		for (int i=0; i<COLS;i++) {
			for(int j=0;j<COLS;j++) {
				Position p=new Position (i,j);
				Position [] middle= new Position [2];
				assert (isValid(p));				
				if(!brancasTurn()&& !isBrancas(p) && !isEmpty(p)) {
					middle[0]=new Position (i+1, j+1);
					middle[1]= new Position (i+1,j-1);
					if(!isValid(middle[0]) && !isValid(middle[1]))
						return false;
					assert isValid(middle[0]);
					if(isEmpty(middle[0]))
						return true;
					assert isValid(middle[1]);
					if(isEmpty(middle[1]))
						return true;

				}
			}
		}
		for (int i=COLS-1;i>=0;i--) {
			for (int j=COLS-1;j>=0;j--) {
				Position p=new Position (i,j);
				Position [] middle= new Position [2];
				assert (isValid(p));
				if(brancasTurn()&& isBrancas(p)) {
					middle[0]=new Position(i-1,j+1);
					middle[1]= new Position (i-1,j-1);
					if(!isValid(middle[0])&& !isValid(middle[1]))
						return false;
					assert (isValid(middle[0]));
					if(isEmpty(middle[0])) {
						return true;
					}
					assert (isValid(middle[1]));
					if(isEmpty(middle[1])) {
						return true;
					}
				}
			}
		}
		return moves;
	}
		
	void play (Position p) {
		assert isValid(p);
		if(isSelected==true && eatPossible()==true) {		//se uma peça estiver selecionada e for possivel comer uma outra
			if(brancasTurn() && p.line() < selecionada.line() ||!brancasTurn() && p.line() > selecionada.line()) {  //se a linha for superior na vez das brancas ou a linha for inferior na vez das pretas
				if((isPObrigatoria(p) && PPodeComer(selecionada))) {	//se a posiçao for a posição obrigatoria e a selecionada for a que pode comer
					if(SelectedCome(p,selecionada)) {	//se a posição selecionada esta apta para ir ate ao ponto final (p)
						grid[selected]=0;
						grid[comida.line()*COLS+comida.col()]=0;
						grid[p.line()*COLS+p.col()]= brancasJogam ? BRANCAS:PRETAS;
						if(brancasTurn())
							numPeçasPretas--;
						if(!brancasTurn())
							numPeçasBrancas--;
						brancasJogam= !brancasJogam;
						isSelected=false;
						obrigatoria= new Position [100000];
						podeComer = new Position [100000];
						podeComerCounter=0;
						obrigatoriaCounter=0;
					}
				}
			}
		}else if(isSelected==true && p.isAdjacent(selecionada)) {		//se estiver selecionada e a posição for adjacente
				if(brancasTurn() && p.line() < selecionada.line() ||!brancasTurn() && p.line() > selecionada.line()) { //se a linha for superior na vez das brancas ou a linha for inferior na vez das pretas
					if(!isEmpty(p)) 			//se a posição estiver ocupada
						return;
					grid[selected]=0;
					grid[p.line()*COLS+p.col()]= brancasJogam ? BRANCAS:PRETAS;
					brancasJogam= !brancasJogam;
					isSelected=false;
				}
			}
		Select(p);  //selecionar a peça

	}	
}