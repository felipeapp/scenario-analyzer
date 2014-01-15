/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.dominio;

import java.sql.Timestamp;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Essa classe representa os "detalhes" (a parte muitos) e se relaciona com a 
 * classe LogLeituraSigaaTurmaVirtual. Serve para representar os vários acessos
 * que os discentes podem fazer em um Turma Virtual. 
 * 
 * @author Agostinho
 */
public class LogLeituraSigaaTurmaVirtualDetalhes {
    
	/** Tipo de acesso à turma virtual */
    private String tipoDeAcesso;
    /** Id do arquivo que o usuário baixou */
    private int arquivoBaixado;
    /** Descrição do arquivo que o usuário baixou */
    private String descricaoArquivoBaixado;
    /** Data em que o detalhe foi registrado. */
    private Timestamp data;
    /** A data por extenso. */
    private String dataExtenso;

	public String getDescricaoArquivoBaixado() {
        return descricaoArquivoBaixado;
    }
    public void setDescricaoArquivoBaixado(String descricaoArquivoBaixado) {
        this.descricaoArquivoBaixado = descricaoArquivoBaixado;
    }
    public String getTipoDeAcesso() {
        return tipoDeAcesso;
    }
    
	/**
	 * Exibe um texto amigável que representa o tipo de acesso do usuário.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/Relatorios/relatorio_acesso_sintetico.jsp
	 * @return
	 * @throws DAOException
	 */
    public String getTipoDeAcessoTexto() {
    	if (tipoDeAcesso.equals(Turma.class.getName()))
    		return "Entrou na Turma Virtual";
    	else if (tipoDeAcesso.equals(Arquivo.class.getName()))
    		return "Arquivo";
    	else if (tipoDeAcesso.equals(ConteudoTurma.class.getName()))
    		return "Conteúdo";
    	else
    		return tipoDeAcesso;
    }
    
    public void setTipoDeAcesso(String tipoDeAcesso) {
        this.tipoDeAcesso = tipoDeAcesso;
    }
    public int getArquivoBaixado() {
        return arquivoBaixado;
    }
    public void setArquivoBaixado(int arquivoBaixado) {
        this.arquivoBaixado = arquivoBaixado;
    }
    
    public Timestamp getData() {
        return data;
    }
    public void setData(Timestamp data) {
        this.data = data;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arquivoBaixado;
		result = prime
				* result
				+ ((descricaoArquivoBaixado == null) ? 0
						: descricaoArquivoBaixado.hashCode());
		result = prime * result
				+ ((tipoDeAcesso == null) ? 0 : tipoDeAcesso.hashCode());
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
		LogLeituraSigaaTurmaVirtualDetalhes other = (LogLeituraSigaaTurmaVirtualDetalhes) obj;
		if (arquivoBaixado != other.arquivoBaixado)
			return false;
		if (descricaoArquivoBaixado == null) {
			if (other.descricaoArquivoBaixado != null)
				return false;
		} else if (!descricaoArquivoBaixado
				.equals(other.descricaoArquivoBaixado))
			return false;
		if (tipoDeAcesso == null) {
			if (other.tipoDeAcesso != null)
				return false;
		} else if (!tipoDeAcesso.equals(other.tipoDeAcesso))
			return false;
		return true;
	}
	public String getDataExtenso() {
		return dataExtenso;
	}
	public void setDataExtenso(String dataExtenso) {
		this.dataExtenso = dataExtenso;
	}
}
