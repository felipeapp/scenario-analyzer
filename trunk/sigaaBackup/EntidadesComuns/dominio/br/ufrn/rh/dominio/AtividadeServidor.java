package br.ufrn.rh.dominio;

import java.util.Arrays;
import java.util.List;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.parametrizacao.ParametroHelper;

/**
 * Informações da função do Servidor. Migrado do RH.
 *
 * @author Gleydson Lima
 *
 */
public class AtividadeServidor implements PersistDB {

	public static final List<Integer> CHEFE_DEPARTAMENTO;

	public static final List<Integer> DIRETOR_CENTRO;
	
	public static final List<Integer> VICE_DIRETOR_CENTRO;

	public static final List<Integer> DIRETOR_UNIDADE;

	public static final List<Integer> DIRETOR_MUSEU;



	private int id;

	private int codigoRH;

	private char origemRH;

	private String descricao;
	
	private Integer pontuacao;

	
	static{
		CHEFE_DEPARTAMENTO = Arrays.asList( ParametroHelper.getInstance().getParametroIntegerArray(ConstantesParametroGeral.ATIVIDADES_CHEFE_DEPARTAMENTO) );
		DIRETOR_CENTRO = Arrays.asList( ParametroHelper.getInstance().getParametroIntegerArray(ConstantesParametroGeral.ATIVIDADES_DIRETOR_CENTRO) );	
		VICE_DIRETOR_CENTRO = Arrays.asList( ParametroHelper.getInstance().getParametroIntegerArray(ConstantesParametroGeral.ATIVIDADES_VICE_DIRETOR_CENTRO) );
		DIRETOR_UNIDADE = Arrays.asList( ParametroHelper.getInstance().getParametroIntegerArray(ConstantesParametroGeral.ATIVIDADES_DIRETOR_UNIDADE) );
		DIRETOR_MUSEU = Arrays.asList( ParametroHelper.getInstance().getParametroIntegerArray(ConstantesParametroGeral.ATIVIDADES_DIRETOR_MUSEU) );
	}
	
	/** Construtores */
	public AtividadeServidor() {
	}

	public AtividadeServidor(int id) {
		this.id = id;
	}
	
	
	public int getCodigoRH() {
		return codigoRH;
	}

	public void setCodigoRH(int codigoRH) {
		this.codigoRH = codigoRH;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public char getOrigemRH() {
		return origemRH;
	}

	public void setOrigemRH(char origemRH) {
		this.origemRH = origemRH;
	}

	public Integer getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(Integer pontuacao) {
		this.pontuacao = pontuacao;
	}

}
