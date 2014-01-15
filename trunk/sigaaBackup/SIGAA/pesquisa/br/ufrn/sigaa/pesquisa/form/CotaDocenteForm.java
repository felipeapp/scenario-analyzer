/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CotaDocente;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;

/**
 * Form utilizado durante as distribuições/ajustes de cotas de bolsas
 * a docentes
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
public class CotaDocenteForm extends SigaaForm<CotaDocente> {

	/** Relatório de classificação a ser utilizado para a geração automática */
	private ClassificacaoRelatorio classificacao = new ClassificacaoRelatorio();

	/** Mapas de cotas por docente */
	private HashMap<String, Object> cotasPibic = new HashMap<String, Object>();
	private HashMap<String, Object> cotasPropesq = new HashMap<String, Object>();
	
	/** Mapa de cotas por tipo de bolsa e docente */
	private HashMap<String, Object> cotasTipoBolsasDocente = new HashMap<String, Object>();

	private Set<CotaDocente> cotas = new TreeSet<CotaDocente>();

	private boolean geracao;

	/** Utilizado para filtrar as cotas geradas */
	private Unidade centro;

	/** Ordenação do resultado */
	private String ordenacao;

	public CotaDocenteForm() {
		this.obj = new CotaDocente();
		this.obj.setEdital( new EditalPesquisa() );
		this.centro =  new Unidade();
	}

	/** Cotas Genéricas (qualquer tipo de bolsa) */
	public void setCota(String idTipoBolsaDocente, Object numero){
		this.cotasTipoBolsasDocente.put(idTipoBolsaDocente, numero);
	}
	
	public Object getCota(String idTipoBolsaDocente){
		return this.cotasTipoBolsasDocente.get(idTipoBolsaDocente);
	}
	
	/**  Cotas PIBIC */
	public void setCotaPibic(String idDocente, Object numero) {
		this.cotasPibic.put(idDocente, numero );
	}

	public Object getCotaPibic(String idDocente) {
		return this.cotasPibic.get(idDocente);
	}

	public HashMap<String, Object> getCotasPibic() {
		return cotasPibic;
	}

	public void setCotasPibic(HashMap<String, Object> cotasPibic) {
		this.cotasPibic = cotasPibic;
	}

	/**  Cotas PROPESQ */
	public void setCotaPropesq(String idDocente, Object numero) {
		this.cotasPropesq.put(idDocente, numero );
	}

	public Object getCotaPropesq(String idDocente) {
		return this.cotasPropesq.get(idDocente);
	}

	public HashMap<String, Object> getCotasPropesq() {
		return cotasPropesq;
	}

	public void setCotasPropesq(HashMap<String, Object> cotasPropesq) {
		this.cotasPropesq = cotasPropesq;
	}

	public boolean isGeracao() {
		return geracao;
	}

	public void setGeracao(boolean geracao) {
		this.geracao = geracao;
	}

	public ClassificacaoRelatorio getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(ClassificacaoRelatorio classificacao) {
		this.classificacao = classificacao;
	}

	public Set<CotaDocente> getCotas() {
		return cotas;
	}

	public void setCotas(Set<CotaDocente> cotas) {
		this.cotas = cotas;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public String getOrdenacao() {
		return this.ordenacao;
	}

	public void setOrdenacao(String ordenacao) {
		this.ordenacao = ordenacao;
	}



}
