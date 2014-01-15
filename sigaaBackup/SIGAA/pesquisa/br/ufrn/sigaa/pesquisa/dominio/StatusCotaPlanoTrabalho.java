package br.ufrn.sigaa.pesquisa.dominio;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Armazena as ordem dos status das cotas do plano de pesquisa.
 * 
 * @author Jean Guerethes
 */
@Entity
@Table(name = "status_plano_trabalho", schema = "pesquisa")
public class StatusCotaPlanoTrabalho implements Validatable {

	/** Apresenta as situações que o plano pode assumir antes da avaliação */
	public static final int ANTES_AVALIACAO = 1;
	/** Apresenta as situações que o plano pode assumir depois da avaliação */
	public static final int DEPOIS_AVALIACAO = 2;
	/** Apresenta as situações que o plano pode assumir depois da indicação */
	public static final int DEPOIS_INDICACAO = 3;
	/** Apresenta as situações que o plano pode assumir */
	public static final int OUTROS = 4;

	/** Chave primária */
	@Id
	@Column(name = "id_status_plano_trabalho", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	/** 
	 * Informação em qual situação o status está presente
	 * 
	 * Ex.: Depois da Avaliação : Aprovado, Não Aprovado e Necessita de Correção 
	 */
	@Column(name = "ordem")
	private int ordemStatus;
	
	/** Referência ao status do plano de trabalho */
	@Column(name = "id_tipo_status_plano_trabalho")
	private int statusPlanoTrabalho;
	
	/** Retorna a descrição do status da cota */
	public String getDescricaoOrdemStatusCota(){
		return getDescricaoOrdemStatusCota(ordemStatus);
	}
	
	/** Retornar a descrição da ordem */
	public static String getDescricaoOrdemStatusCota( int ordem ){
		switch ( ordem ) {
			case ANTES_AVALIACAO: 
				return "Antes de Avaliação";
			case DEPOIS_AVALIACAO: 
				return "Depois da Avaliação";
			case DEPOIS_INDICACAO: 
				return "Depois da Indicação";
			case OUTROS: 
				return "Outros";
			}
		return null;
	}
	
	/** Retorna a descrição do plano de trabalho */
	public String getDescricaoStatus(){
		return TipoStatusPlanoTrabalho.getDescricao( statusPlanoTrabalho );
	}
	
	/** Monta os possíveis categorias que o status do plano pode está presente */
	public static Map<Integer, String> status(){
			Map<Integer, String> status = new HashMap<Integer, String>();
			status.put(StatusCotaPlanoTrabalho.ANTES_AVALIACAO, StatusCotaPlanoTrabalho.getDescricaoOrdemStatusCota(StatusCotaPlanoTrabalho.ANTES_AVALIACAO));
			status.put(StatusCotaPlanoTrabalho.DEPOIS_AVALIACAO, StatusCotaPlanoTrabalho.getDescricaoOrdemStatusCota(StatusCotaPlanoTrabalho.DEPOIS_AVALIACAO));
			status.put(StatusCotaPlanoTrabalho.DEPOIS_INDICACAO, StatusCotaPlanoTrabalho.getDescricaoOrdemStatusCota(StatusCotaPlanoTrabalho.DEPOIS_INDICACAO));
			status.put(StatusCotaPlanoTrabalho.OUTROS, StatusCotaPlanoTrabalho.getDescricaoOrdemStatusCota(StatusCotaPlanoTrabalho.OUTROS));
		return status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrdemStatus() {
		return ordemStatus;
	}

	public void setOrdemStatus(int ordemStatus) {
		this.ordemStatus = ordemStatus;
	}

	public int getStatusPlanoTrabalho() {
		return statusPlanoTrabalho;
	}

	public void setStatusPlanoTrabalho(int statusPlanoTrabalho) {
		this.statusPlanoTrabalho = statusPlanoTrabalho;
	}

	/** Realiza a validação dos campos de preenchimento obrigatórios */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(ordemStatus, "Situação", lista);
		ValidatorUtil.validateRequiredId(statusPlanoTrabalho, "Status", lista);
		return lista;
	}
	
}