/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/05/2010
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.mensagens.MensagensPesquisa;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe utilizada para registrar os docentes que tem permissão
 * para extrapolar o limite de cotas global definido pelo gestor do módulo de pesquisa.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "limite_cota_excepcional", schema = "pesquisa", uniqueConstraints = {})
public class LimiteCotaExcepcional implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	@Column(name = "id_limite_cota_excepcional", nullable = false)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_servidor")
	private Servidor servidor = new Servidor();
	
	private short limite;
	
	private Boolean ativo = true;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(servidor, "Docente", lista);
		ValidatorUtil.validaInt(limite, "Limite", lista);
		int limitePadrao = ParametroHelper.getInstance().getParametroInt(ParametrosPesquisa.LIMITE_COTAS_ORIENTADOR);
		if(limite <= limitePadrao)
			lista.addMensagem(MensagensPesquisa.LIMITE_EXCEPCIONAL_DEVE_SER_MAIOR_QUE_LIMITE_PADRAO, limitePadrao);
		return lista;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public short getLimite() {
		return limite;
	}

	public void setLimite(short limite) {
		this.limite = limite;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
