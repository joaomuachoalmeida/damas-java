import pt.iscte.guitoo.Color;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class View {
	Jogo model;
	Board board;

	View(Jogo model) {
		this.model = model;
		board = new Board(model.brancasTurn() ? "Brancas Jogam": "Pretas Jogam", model.numeroColunas(), model.numeroColunas(), 60);
		board.setIconProvider(this::icon);
		board.addMouseListener(this::click);
		board.setBackgroundProvider(this::background);
		board.addAction("New", this::newGame);
		board.addAction("Random", this::random);
		board.addAction("Save", this::saveGame);
		board.addAction("Carregar", this::loadGame);
	}
	void newGame() {
		int numCols = board.promptInt("Numero de Colunas?");
		int numPeças= board.promptInt("Numero de Peças?");
		int[] grid= new int[numCols*numCols];
			Jogo newModel = new Jogo(numCols,numPeças,numPeças,numPeças,true,grid);
			View gui = new View(newModel);
			gui.start();
	}
	String icon(int line, int col) {
	    Position p = new Position(line, col);
	    if (model.isEmpty(p))
	    		return null;
	    if(model.isBrancas(p))
	    	return "white.png";
	    return "black.png";
	}
	void random() {
		if(model.checkMoves()==false) {
			if(model.numeroPeçasBlack()>model.numeroPeçasWhite()) {
				board.showMessage("Vitoria pretas");
				return;
			}
			else if(model.numeroPeçasWhite()>model.numeroPeçasBlack()) {
				board.showMessage("Vitoria Brancas");
				return;
			}
			else {
				board.showMessage("Empate");
				return;
			}
		}

		if(model.numeroPeçasBlack()==0) {
			board.showMessage("VITORIA BRANCAS!!!");
			return;
		}
		if( model.numeroPeçasWhite()==0) {
			board.showMessage("VITORIA PRETAS!!!");
			return;
		}
		if(model.brancasTurn()&& model.isSelected()==false)
			model.selectRandomBranca();
		if(model.brancasTurn()==false && model.isSelected()==false)
			model.selectRandomPretas();
		model.playRandom();
		board.setTitle((model.brancasTurn()? "Brancas" : "Pretas")+" Jogam");
	}


	void click (int line, int col) {
		Position p = new Position (line,col);
		model.play(p);
		board.setTitle((model.brancasTurn()? "Brancas" : "Pretas")+" Jogam");
		if(model.checkMoves()==false) {
			if(model.numeroPeçasBlack()>model.numeroPeçasWhite()) {
				board.showMessage("Vitoria pretas");
				return;
			}
			else if(model.numeroPeçasWhite()>model.numeroPeçasBlack()) {
				board.showMessage("Vitoria Brancas");
				return;
			}
			else {
				board.showMessage("Empate");
				return;
			}
		}

		if(model.numeroPeçasBlack()==0 || model.numeroPeçasWhite()==0) {
			board.showMessage("VITORIA!!!");
			return;
		}
	}
	Color background (int line,int col) {
		Position p= new Position (line,col);
		Position select = model.selecionada;
		if(model.isSelected()== true && select.equals(p)) {
				return StandardColor.YELLOW;
			}
		if(model.isBlackSquare(p)==true)
			return StandardColor.BLACK;
		return StandardColor.WHITE;
		
	}
	void saveGame() {
		String name= board.promptText("Nome Do Ficheiro");
		try (PrintWriter saveGame= new PrintWriter(new File (name))){
			saveGame.println(model.brancasJogam);
			saveGame.println(model.COLS);
			saveGame.println(model.numPeças);
			saveGame.println(model.numPeçasBrancas);
			saveGame.println(model.numPeçasPretas);
			for(int i=0; i< model.COLS*model.COLS;i++) {
				saveGame.println(model.grid[i]);
			}
		}
		catch(FileNotFoundException e) {
			board.showMessage("erro");
		}
	}
	void loadGame() {
		String file= board.promptText("nome do ficheiro");
		try(Scanner scanner = new Scanner (new File(file))){
			boolean x=false;
			int Cols=0;
			if(scanner.hasNext()) {
				x= scanner.nextBoolean();
			}
			if(scanner.hasNext())
				Cols=scanner.nextInt();
			int [] array= new int[Cols*Cols +3];
			while(scanner.hasNext()) {
				for(int i=0; i<array.length; i++) {
					array[i]= scanner.nextInt();
				}
			}
			int[]grid= new int[Cols*Cols];
			int count=0;
			for(int i=3; i<array.length;i++) {
				grid[count]= array[i];
				count++;
			}
				scanner.close();
				Jogo newModel = new Jogo(Cols,array[0],array[1],array[2],x,grid);
				View gui = new View(newModel);
				gui.start();
		}catch(FileNotFoundException e) {
			board.showMessage("erro");
			}
	}

	void start() {
		board.open();
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Bem-vindo ao Jogo de Damas sem Damas!");
        
        System.out.print("Digite o número de colunas do tabuleiro: ");
        int numCols = scanner.nextInt();
        
        System.out.print("Digite o número de peças por equipa: ");
        int numPecas = scanner.nextInt();
             
        scanner.close();
               
        int[] grid= new int[numCols*numCols];
        
		View gui = new View(new Jogo(numCols, numPecas,numPecas,numPecas, true, grid));
		gui.start();
	}
}