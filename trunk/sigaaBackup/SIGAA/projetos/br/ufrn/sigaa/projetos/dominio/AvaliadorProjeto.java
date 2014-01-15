/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/10/2010
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Usuario;

/*******************************************************************************
 * Avaliador de projetos. Formado por membros da comunidade acad�mica
 * (docentes). <br/>
 * Existem 2 grupos de avaliadores de projetos.<br/>
 * 
 * Um grupo � formado por docentes selecionados pela Pr�-Reitoria para
 * avaliarem propostas de projetos e s�o conhecidos como avaliadores Ad Hoc. 
 * As avalia��es dos Avaliadores Ad hoc servem para auxiliar a
 * avalia��o dos membros do comit� de projetos analisando o m�rito acad�mico da 
 * proposta.<br/>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "avaliador_projeto", schema = "projetos")
public class AvaliadorProjeto implements Validatable {
    
	/** Atributo chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_avaliador_projeto")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** Dados sobre o avaliador */ 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_avaliador")
	private Usuario usuario = new Usuario();

	/** Registro de entrada do avaliador */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** O avaliador s� pode avaliar projetos sobre duas �reas de conhecimento: Esta � a �rea tem�tica principal. */ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_conhecimento1")
	private AreaConhecimentoCnpq areaConhecimento1 = new AreaConhecimentoCnpq();

	/** O avaliador s� pode avaliar projetos sobre duas �reas de conhecimento: Esta � a �rea tem�tica secund�ria. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_conhecimento2")
	private AreaConhecimentoCnpq areaConhecimento2 = new AreaConhecimentoCnpq();

	/** Atributo utilizado pare representar se o Avaliador est� ou n�o ativo no sistema */
	@CampoAtivo
	private Boolean ativo = true;

	/** Data a partir do qual � avaliador */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data a partir do qual deixou de ser avaliador. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fim")
	private Date dataFim;

	public int getId() {
	    return id;
	}

	public void setId(int id) {
	    this.id = id;
	}

	public Usuario getUsuario() {
	    return usuario;
	}

	public void setUsuario(Usuario usuario) {
	    this.usuario = usuario;
	}

	public RegistroEntrada getRegistroEntrada() {
	    return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
	    this.registroEntrada = registroEntrada;
	}

	public AreaConhecimentoCnpq getAreaConhecimento1() {
	    return areaConhecimento1;
	}

	public void setAreaConhecimento1(AreaConhecimentoCnpq areaConhecimento1) {
	    this.areaConhecimento1 = areaConhecimento1;
	}

	public AreaConhecimentoCnpq getAreaConhecimento2() {
	    return areaConhecimento2;
	}

	public void setAreaConhecimento2(AreaConhecimentoCnpq areaConhecimento2) {
	    this.areaConhecimento2 = areaConhecimento2;
	}

	public Boolean getAtivo() {
	    return ativo;
	}

	public void setAtivo(Boolean ativo) {
	    this.ativo = ativo;
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

	public ListaMensagens validate() {
	    ListaMensagens lista = new ListaMensagens();

	    ValidatorUtil.validateRequired(dataInicio, "Data In�cio", lista);
	    ValidatorUtil.validateRequired(dataFim, "Data Fim", lista);
	    ValidatorUtil.validateRequired(usuario, "Avaliador(a)", lista);
	    ValidatorUtil.validateRequired(areaConhecimento1, "�rea de Conhecimento", lista);

	    if (((dataFim != null) && (dataInicio != null)) && (dataFim.before(dataInicio))) {
		lista.addErro("Data de fim deve ser maior que a data de in�cio.");
	    }

	    if (((areaConhecimento1 != null) && (areaConhecimento2 != null)) && (areaConhecimento1.getId() == areaConhecimento2.getId())) {
		lista.addErro("1� �rea de Conhecimento deve ser diferente da 2�.");
	    }
	    return lista;
	}

}
