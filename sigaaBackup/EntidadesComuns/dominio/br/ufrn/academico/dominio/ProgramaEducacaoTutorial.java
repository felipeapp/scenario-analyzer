package br.ufrn.academico.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validaInicioFim;
import static br.ufrn.arq.util.ValidatorUtil.validaInt;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Transient;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que define as informações compartilhadas
 * sobre os grupos do Programa de Educação Tutorial - PET
 * a ser persistida nos bancos acadêmico e admnistrativo
 * 
 * Os mapeamentos foram realizados com XML por possuírem
 * diferentes características em cada banco
 * 
 * A persistencia deve ser feita utilizando o DAO
 * disponível na arquitetura (ProgramaEducacaoTutorialDAO)
 * 
 * @author wendell
 *
 */
public class ProgramaEducacaoTutorial extends AbstractMovimento implements Validatable {

	private int id;
	
	private String descricao;

	// Um grupo pode estar vinculado a um curso ou a uma área de conhecimento
	private Integer idCurso;
	private Integer idAreaConhecimentoCnpq;
	
	private Date dataInicio;
	private Date dataFim;
	
	// Cada grupo tem um limite de bolsistas ativos simultaneamente
	private Integer limiteBolsas;
	
	
	private boolean ativo = true;

	@Transient
	private Curso curso;
	
	@Transient
	private String nomeAreaConhecimentoCnpq;

	public ProgramaEducacaoTutorial() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public Integer getIdAreaConhecimentoCnpq() {
		return idAreaConhecimentoCnpq;
	}

	public void setIdAreaConhecimentoCnpq(Integer idAreaConhecimentoCnpq) {
		this.idAreaConhecimentoCnpq = idAreaConhecimentoCnpq;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getLimiteBolsas() {
		return limiteBolsas;
	}

	public void setLimiteBolsas(Integer limiteBolsas) {
		this.limiteBolsas = limiteBolsas;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	
	public void setNomeAreaConhecimentoCnpq(String nomeArea) {
		this.nomeAreaConhecimentoCnpq = nomeArea;
	}
	
	public String getNomeAreaConhecimentoCnpq() {
		return nomeAreaConhecimentoCnpq;
	}
	
	@Override
	public String toString() {
		return descricao;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		validateRequired(descricao, "Descrição", lista);

		if ( isEmpty(idAreaConhecimentoCnpq) && isEmpty(idCurso) ) {
			lista.addErro("É necessário selecionar um curso ou área de conhecimento para este grupo");
		}

		validateRequired(dataInicio, "Data de Início", lista);
		validaInicioFim(dataInicio, dataFim, "Período do Grupo", lista);
		
		validaInt(limiteBolsas, "Limite de bolsas do grupo", lista);
		
		return lista;
	}


}
