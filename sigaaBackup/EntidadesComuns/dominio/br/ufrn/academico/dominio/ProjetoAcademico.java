package br.ufrn.academico.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.util.Formatador;

/**
 * Entidade que representa um projeto acadêmico cadastrado no sistema acadêmico.
 * Inicialmente será utilizado pelo sistema administrativo para validação de convênios.
 * 
 * @author wendell
 *
 */
public class ProjetoAcademico {

	private int id;
	private int tipo;
	
	private String titulo;
	
	private Date inicio;
	private Date fim;
	
	private String identificacaoObjeto;
	private String justificativa;
	
	private Integer idAreaConhecimento;
	
	private int idCoordenador;
	
	private Collection<MembroProjeto> membros;
	
	public ProjetoAcademico() {
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public String getIdentificacaoObjeto() {
		return identificacaoObjeto;
	}

	public void setIdentificacaoObjeto(String objetivos) {
		this.identificacaoObjeto = objetivos;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Integer getIdAreaConhecimento() {
		return idAreaConhecimento;
	}

	public void setIdAreaConhecimento(Integer idAreaConhecimento) {
		this.idAreaConhecimento = idAreaConhecimento;
	}

	public int getIdCoordenador() {
		return idCoordenador;
	}

	public void setIdCoordenador(int idCoordenador) {
		this.idCoordenador = idCoordenador;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricaoTipo() {
		return TipoProjetoAcademico.getDescricao(tipo);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjetoAcademico other = (ProjetoAcademico) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getDescricaoTipo() + "  -  " +  titulo;
	}
	
	public String getTituloDescricao() {
		Formatador formatador = Formatador.getInstance();
		return getDescricaoTipo() + " - " +  
		titulo.toUpperCase() + " (" + formatador.formatarData(inicio) + " - " + formatador.formatarData(fim) + ")";
	}
	
	public String getIdProjetoTipo() {
		return id + "_" + tipo;
	}
	
	public void adicionarMembro(MembroProjeto membro) {
		if (membros == null) {
			membros = new ArrayList<MembroProjeto>();
		}
		membros.add(membro);
	}

	public Collection<MembroProjeto> getMembros() {
		return membros;
	}

	public void setMembros(Collection<MembroProjeto> membros) {
		this.membros = membros;
	}
}
