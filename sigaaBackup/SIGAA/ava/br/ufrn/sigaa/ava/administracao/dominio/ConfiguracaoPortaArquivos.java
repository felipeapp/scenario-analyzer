package br.ufrn.sigaa.ava.administracao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Configurações do porta arquivos.
 *
 * @author Diego Jácome
 *
 */
@Entity @Table(name="configuracao_porta_arquivos", schema="ava") @HumanName(value="Configuração do Porta Arquivos", genero='F')
public class ConfiguracaoPortaArquivos implements PersistDB {

	/**
	 * Define a unicidade da configuração da turma virtual.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_configuracao_porta_arquivos", nullable = false)
	private int id;
	
	/**
	 * Define o tamanho máximo do porta arquivos.
	 */
	@Column(name="tamanho_maximo_porta_arquivos")	
	private long tamanhoMaximoPortaArquivos;
	
	/**
	 * Docente externo dono do porta arquivos.
	 */
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	public ConfiguracaoPortaArquivos () {}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTamanhoMaximoPortaArquivos() {
		return tamanhoMaximoPortaArquivos;
	}

	public void setTamanhoMaximoPortaArquivos(long tamanhoMaximoPortaArquivos) {
		this.tamanhoMaximoPortaArquivos = tamanhoMaximoPortaArquivos;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}
}
