/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 10/02/2011
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.RelatoriosConcluintesDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean responsável por gerar os Relatórios de discentes concluíntes de graduação
 *
 * @author Arlindo Rodrigues
 *
 */
@Component("relatoriosConcluintesMBean") @Scope("session")
public class RelatoriosConcluintesMBean extends SigaaAbstractController<Discente> {
	
	/** Ano selecionado */
	private int ano;
	/** Periodo selecionado */
	private int periodo;
	/** Unidade selecionada */
	private Unidade unidade = new Unidade();	
	
	/** Título do Relatório */
	private String tituloRelatorio;
	
	/** Listagem com o resultado da consulta */
	private List<Map<String, Object>> listagem = new ArrayList<Map<String, Object>>();
	
	/** Enum com os tipos de relatórios possíveis */
	private enum TipoRelatorio {
			POTENCIAIS_CONCLUINTES
	}

	/** Indica qual relatório será gerado */
	private TipoRelatorio operacao;
	
	/**
	 * Inicia o Relatório de Potênciais Concluintes
	 * @return
	 */
	public String iniciarPotenciaisConcluintes(){		
		operacao = TipoRelatorio.POTENCIAIS_CONCLUINTES;
		tituloRelatorio = "Prováveis Concluintes por Ano/Semestre";
		periodo = getCalendarioVigente().getPeriodo();
		return iniciar();
	}		
	
	/**
	 * Inicia o formulário para seleção dos dados para geração
	 * @return
	 */
	private String iniciar(){
		ano = CalendarUtils.getAnoAtual()-1;
		return forward(getFormPage());
	}
	
	/**
	 * Gera o Relatório conforme a operação
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws DAOException{
	
		if (isPotenciaisConcluintes() && periodo == 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Periodo");
		}
		
		if (ano <= 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		}
		
		if (ano > 0 && ano < 1900){
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Ano");
		}
		
		if (hasErrors())
			return null;
		
		if (unidade.getId() > 0)
			unidade = getGenericDAO().findByPrimaryKey(unidade.getId(), Unidade.class, "id","nome");
		
		if (operacao.equals(TipoRelatorio.POTENCIAIS_CONCLUINTES))	
			return gerarPotenciaisConcluintes();
		
		return null;
	}
	
	/**
	 * Gera o relatório de Potenciais Concluintes
	 * 
	 * @return
	 */
	public String gerarPotenciaisConcluintes(){
		
		RelatoriosConcluintesDao dao = getDAO(RelatoriosConcluintesDao.class);
		try {
			listagem = dao.findPotenciaisConcluintes(unidade.getId(), ano, periodo);
			
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/graduacao/relatorios/concluintes/potenciais_concluintes.jsf");
	}	
	
	@Override
	public String getFormPage() {
		return "/graduacao/relatorios/concluintes/form.jsf";
	}
	
	/**
	 * Indica se a operação é do relatório de Potenciais concluintes
	 * @return
	 */
	public boolean isPotenciaisConcluintes(){
		return operacao.equals(TipoRelatorio.POTENCIAIS_CONCLUINTES);
	}

	public List<Map<String, Object>> getListagem() {
		return listagem;
	}

	public void setListagem(List<Map<String, Object>> listagem) {
		this.listagem = listagem;
	}

	public String getTituloRelatorio() {
		return tituloRelatorio;
	}

	public void setTituloRelatorio(String tituloRelatorio) {
		this.tituloRelatorio = tituloRelatorio;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public TipoRelatorio getOperacao() {
		return operacao;
	}

	public void setOperacao(TipoRelatorio operacao) {
		this.operacao = operacao;
	}
}
