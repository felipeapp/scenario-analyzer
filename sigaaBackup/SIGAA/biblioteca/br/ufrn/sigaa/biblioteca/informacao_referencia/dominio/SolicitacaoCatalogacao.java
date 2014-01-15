/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Sep 19, 2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;

/**
 *	<p>Esta entidade registra uma solicita��o de cataloga��o na fonte (gera��o da ficha catalogr�fica).</p>
 *
 * 	@author Felipe Rivas
 */
@Entity
@Table(name = "solicitacao_catalogacao", schema = "biblioteca")
public class SolicitacaoCatalogacao extends SolicitacaoServicoDocumento {

	/** Id da solicita��o */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.solicitacoes_usuario_sequence") })
	@Column(name = "id_solicitacao_catalogacao", nullable = false)
	private int id;

	/** Palavras-chave do documento. */
	@CollectionOfElements
	@JoinTable(name = "palavra_chave_solicitacao_catalogacao", schema = "biblioteca",
	      joinColumns = @JoinColumn(name = "id_solicitacao_catalogacao"))
	@Column(name = "palavra_chave")
	private List<String> palavrasChave;

	/** n�mero de p�ginas do documento */
	@Column(name="numero_paginas")
	private Integer numeroPaginas;
	
	/** Ficha catalogr�fica gerada. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_ficha_catalografica")
	private FichaCatalografica fichaGerada;
	
	/** Refer�ncia para o identificador do arquivo referente � ficha catalogr�fica */
	@Column(name="id_ficha_digitalizada")
	private Integer idFichaDigitalizada;
	
	/** Motivo do reenvio da solicita��o por parte do usu�rio. */
	@Column(name="motivo_reenvio")
	private String motivoReenvio;
	
	/** Para ser utilizado nos formul�rios. */
	@Transient
	private String palavrasChaveString;

	/////////////////////////// sets e gets ////////////////////////////
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<String> getPalavrasChave() {
		return palavrasChave;
	}

	public void setPalavrasChave(List<String> palavrasChave) {
		this.palavrasChave = palavrasChave;
	}

	public Integer getNumeroPaginas() {
		return numeroPaginas;
	}

	public void setNumeroPaginas(Integer numeroPaginas) {
		this.numeroPaginas = numeroPaginas;
	}
	
	public FichaCatalografica getFichaGerada() {
		return fichaGerada;
	}
	
	public void setFichaGerada(FichaCatalografica fichaGerada) {
		this.fichaGerada = fichaGerada;
	}

	public Integer getIdFichaDigitalizada() {
		return idFichaDigitalizada;
	}

	public void setIdFichaDigitalizada(Integer idFichaDigitalizada) {
		this.idFichaDigitalizada = idFichaDigitalizada;
	}

	public String getMotivoReenvio() {
		return motivoReenvio;
	}

	public void setMotivoReenvio(String motivoReenvio) {
		this.motivoReenvio = motivoReenvio;
	}

	/**
	 * Retorna as palavras-chave separadas por v�rgula em uma �nica String 
	 * 
	 * @return
	 */
	public String getPalavrasChaveString() {
		if (palavrasChave != null && !palavrasChave.isEmpty()){
			StringBuffer buffer = new StringBuffer();
			for (String s : palavrasChave){
				buffer.append(s);
				buffer.append(", ");
			}
			buffer.delete(buffer.length() - 2, buffer.length());
			palavrasChaveString = buffer.toString();
		}
		return palavrasChaveString;
	}
	
	public void setPalavrasChaveString(String palavrasChaveString) {
		this.palavrasChaveString = palavrasChaveString;
	}

	@Override
	public String getTipoServico() {
		return "Cataloga��o na Fonte";
	}
	
	/**
	 * Valida o preenchimento dos campos do objeto.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = super.validate();
		
		if( numeroPaginas == null || numeroPaginas == 0 )
			erros.addErro("O n�mero de p�ginas deve ser informado.");
		
		/*
		 * Deve haver entre 3 e 6 palavras chaves.
		 * N�o pode haver palavras chaves repetidas.
		 */
		if (palavrasChave != null){
			
			if (palavrasChave.size() > 6)
				erros.addErro("A solicita��o deve conter no m�ximo 6 palavras-chave.");

			if (palavrasChave.size() < 3)
				erros.addErro("A solicita��o deve conter no m�mino 3 palavras-chave.");
			
			HashMap<String, String> mapa = new HashMap<String, String>();
			for(String s : palavrasChave ){
				if( StringUtils.notEmpty( mapa.get( StringUtils.toAsciiAndUpperCase(s)))){
					erros.addErro("A palavra-chave " + s + " est� duplicada.");
				}
				mapa.put(StringUtils.toAsciiAndUpperCase(s), s);
			}
		} else {
			erros.addErro("A solicita��o deve conter no m�mino 3 palavras-chave.");
		}
		
		return erros;
	}
	
}