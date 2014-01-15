/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.AutorResumoCongresso;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;

/**
 * Form utilizado nas operações relacionadas ao
 * cadastro dos resumos do congresso de iniciação
 * científica
 *
 * @author Ricardo Wendell
 *
 */
public class ResumoCongressoForm extends SigaaForm<ResumoCongresso> {

	/**Geração do relatório pela área de conhecimento */
	public static final int BUSCA_AREA_CONHECIMENTO = 2;
	/**Geração do relatório pelo nome do autor */
	public static final int BUSCA_NOME_AUTOR = 3;
	/**Geração do relatório pelo CPF do autor */
	public static final int BUSCA_CPF_AUTOR = 4;
	/**Geração do relatório buscando pelo status */
	public static final int BUSCA_STATUS = 5;
	/**Geração do relatório buscando pelo centro */
	public static final int BUSCA_CENTRO = 6;
	/**Geração do relatório buscando pelo código */
	public static final int BUSCA_CODIGO = 7;
	/**Geração do relatório buscando pelo orientador */
	public static final int BUSCA_ORIENTADOR = 8;
	
	/** Armazena o tipo de busca realizada */
	private int tipoBusca;
	/** Armazena informação se deve ser gerado ou não o relatório */
	private boolean relatorio;

	/** Armazena o filtros da busca */
	private int[] filtros = {};
	/** Armazena a informação do centro */
	private Unidade centro;
	/** Armazena a chave primária do congresso */
	private int idCongresso;
	/** Armazena a informação do Autor do Resumo do Congresso */
	private AutorResumoCongresso autor;
	/** Armazena a informação do Orientador do Resumo do Congresso */
	private AutorResumoCongresso orientador;

	/** Utilizado no caso de autores externos */
	private String cpf;

	/** Utilizado para discentes sem CPF cadastrado */
	private String cpfAutor;

	/** Indica se o resumo estão ou não associado a um plano de trabalho */
	private boolean isolado;

	/** Indica se deve ou não exibir o CoAutor do Resumo do Congresso. */
	private boolean exibirFormCoAutor = false;
	
	/** Indica se o usuário possui permissão de gestor. */
	private boolean permissaoGestor = false;
	
	public ResumoCongressoForm() throws Exception {
		this.clear();
	}

	/**
	 * Inicializa todas as informações referente ao Resumo do Congresso.
	 */
	@Override
	public void clear() throws Exception {
		this.obj = new ResumoCongresso();
		this.autor = new AutorResumoCongresso();

		orientador = new AutorResumoCongresso();
		orientador.setTipoParticipacao(AutorResumoCongresso.ORIENTADOR);
		orientador.setCategoria(AutorResumoCongresso.DOCENTE);

		centro = new Unidade();
	}
	
	/**
	 * Reseta todos os atributos utilizados.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		//reset properties
		this.relatorio = false;
		this.filtros = new int[0];
	}

	public AutorResumoCongresso getOrientador() {
		return orientador;
	}

	public void setOrientador(AutorResumoCongresso orientador) {
		this.orientador = orientador;
	}

	public boolean isIsolado() {
		return isolado;
	}

	public void setIsolado(boolean isolado) {
		this.isolado = isolado;
	}

	public AutorResumoCongresso getAutor() {
		return autor;
	}

	public void setAutor(AutorResumoCongresso autor) {
		this.autor = autor;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getCpfAutor() {
		return cpfAutor;
	}

	public void setCpfAutor(String cpfAutor) {
		this.cpfAutor = cpfAutor;
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public int getIdCongresso() {
		return idCongresso;
	}

	public void setIdCongresso(int idCongresso) {
		this.idCongresso = idCongresso;
	}

	public Unidade getCentro() {
		return this.centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public boolean isRelatorio() {
		return this.relatorio;
	}

	public void setRelatorio(boolean relatorio) {
		this.relatorio = relatorio;
	}

	public boolean isExibirFormCoAutor() {
		return exibirFormCoAutor;
	}

	public void setExibirFormCoAutor(boolean exibirFormCoAutor) {
		this.exibirFormCoAutor = exibirFormCoAutor;
	}

	public boolean isPermissaoGestor() {
		return permissaoGestor;
	}

	public void setPermissaoGestor(boolean permissaoGestor) {
		this.permissaoGestor = permissaoGestor;
	}
	
}