package br.ufrn.sigaa.monitoria.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.RelatorioProjetosReducaoBolsasDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;

/** 
 * MBean responsável por relatórios de projetos com redução de bolsas.
 * */
@Scope("request")
@Component("relatorioReducaoBolsa") 
public class RelatorioProjetosReducaoBolsasMBean extends SigaaAbstractController<ProjetoEnsino> {

	/** Indica se a Busca é filtrada por título. */
	private boolean checkBuscaTitulo;

	/** Indica se a busca é filtrada por ano. */
	private boolean checkBuscaAno;
	
	/** Nome do projeto a ser filtrado na busca. */
	private String buscaNomeProjeto;
	
	/** Ano a ser filtrado na busca. */
	private Integer buscaAnoProjeto;
	
	/** Coleção de projetos encontrados. */
	private Collection<ProjetoEnsino> projetosLocalizados;
	
	public RelatorioProjetosReducaoBolsasMBean() {
		obj = new ProjetoEnsino();
	}
	
	public String iniciaRelatorio() throws SegurancaException{
		checkListRole();
		return forward("/monitoria/RelatorioProjetosReducaoBolsas/seleciona_reducao_bolsasi.jsp");
	}
	
	public String geraRelatorio() throws DAOException{
		
		if(isCheckBuscaTitulo()){
			ValidatorUtil.validateRequired(buscaNomeProjeto, "Projeto", erros);
		} else {
			buscaNomeProjeto = "";
		}
		if(isCheckBuscaAno()){
			ValidatorUtil.validaInt(buscaAnoProjeto, "Ano do Projeto", erros);
		} else {
			buscaAnoProjeto = null;
		}
		if(!isCheckBuscaAno() && !isCheckBuscaTitulo()){
			addMensagemErro("Selecione alguma opção de busca.");
		}
		
		if(hasErrors()){
			return null;
		} else {
			RelatorioProjetosReducaoBolsasDao dao = getDAO(RelatorioProjetosReducaoBolsasDao.class);
			projetosLocalizados = dao.findProjetosReducaoBolsas(buscaAnoProjeto, buscaNomeProjeto);
			if( projetosLocalizados == null || projetosLocalizados.isEmpty() ){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}else{
				forward("/monitoria/RelatorioProjetosReducaoBolsas/relatorio_reducao_bolsas.jsp");
			}
		}
		
		return null;
	}
	

	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}

	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public String getBuscaNomeProjeto() {
		return buscaNomeProjeto;
	}

	public void setBuscaNomeProjeto(String buscaNomeProjeto) {
		this.buscaNomeProjeto = buscaNomeProjeto;
	}

	public Integer getBuscaAnoProjeto() {
		return buscaAnoProjeto;
	}

	public void setBuscaAnoProjeto(Integer buscaAnoProjeto) {
		this.buscaAnoProjeto = buscaAnoProjeto;
	}

	public Collection<ProjetoEnsino> getProjetosLocalizados() {
		return projetosLocalizados;
	}

	public void setProjetosLocalizados(Collection<ProjetoEnsino> projetosLocalizados) {
		this.projetosLocalizados = projetosLocalizados;
	}
	
	
	
}
