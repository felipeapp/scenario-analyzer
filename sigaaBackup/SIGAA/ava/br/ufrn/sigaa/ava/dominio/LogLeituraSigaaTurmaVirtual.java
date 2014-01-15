/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe sem comportamento utilizada apenas para representar o conteúdo do
 * relatório de acesso a Turma Virtual. Essa classe foi necessária
 * pois a modelagem da tabela log_db_leitura não permite criar uma classe
 * de domínio real.
 * 
 * @author Agostinho
 */

public class LogLeituraSigaaTurmaVirtual implements Comparable<LogLeituraSigaaTurmaVirtual>, Serializable {
    
	/** Usuário que acessou a turma virtual */
    private int idUsuario;
    /** Nome do discente que acessou a turma virtual */
    private String nomeDiscente;
    /** Detalhes do acesso do discente à turma virtual  */
    private List<LogLeituraSigaaTurmaVirtualDetalhes> detalhes = new ArrayList<LogLeituraSigaaTurmaVirtualDetalhes>();
    /** Número de vezes que o usuário acessou a turma virtual */
    private int qntEntrouTurmaVirtual;
    /** Número de vezes que o usuário acessou um arquivo */
    private int qntArquivo;
    /** Número de vezes que o usuário acessou um conteúdo */
    private int qntConteudoTurma;
    /** Número de vezes que o usuário acessou as datas de avaliação */
    private int qntDataAvaliacao;
    /** Número de vezes que o usuário acessou os tópicos de aula */
    private int qntTopicoAula;
    /** Mês de acesso do usuário na turma virtual */
    private int mes;
    /** Dia de acesso do usuário na turma virtual */
    private int dia;
    /** Semana de acesso do usuário na turma virtual */
    private int semana;
    /** Nome do Mês por extenso */
    private String nomeMes;
    /** Nome do semana por extenso */
    private String nomeSemana;
    /** Detalhar Mês */
    private boolean detalharMes;
    /** Detalhar Semana */
    private boolean detalharSemana;
    
    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    public String getNomeDiscente() {
        return nomeDiscente;
    }
    public void setNomeDiscente(String nomeDiscente) {
        this.nomeDiscente = nomeDiscente;
    }
    public List<LogLeituraSigaaTurmaVirtualDetalhes> getDetalhes() {
        return detalhes;
    }
    public void setDetalhes(List<LogLeituraSigaaTurmaVirtualDetalhes> detalhes) {
        this.detalhes = detalhes;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idUsuario;
        result = prime * result
                + ((nomeDiscente == null) ? 0 : nomeDiscente.hashCode());
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
        LogLeituraSigaaTurmaVirtual other = (LogLeituraSigaaTurmaVirtual) obj;
        if (idUsuario != other.idUsuario)
            return false;
        if (nomeDiscente == null) {
            if (other.nomeDiscente != null)
                return false;
        } else if (!nomeDiscente.equals(other.nomeDiscente))
            return false;
        return true;
    }
    
    public int compareTo(LogLeituraSigaaTurmaVirtual obj) {
        int ultimaComparacao = nomeDiscente.compareTo(obj.nomeDiscente);
        return (ultimaComparacao != 0 ? ultimaComparacao : nomeDiscente.compareTo(obj.nomeDiscente)); 
    }
	public int getQntEntrouTurmaVirtual() {
		return qntEntrouTurmaVirtual;
	}
	public void setQntEntrouTurmaVirtual(int qntEntrouTurmaVirtual) {
		this.qntEntrouTurmaVirtual = qntEntrouTurmaVirtual;
	}
	public int getQntArquivo() {
		return qntArquivo;
	}
	public void setQntArquivo(int qntArquivo) {
		this.qntArquivo = qntArquivo;
	}
	public int getQntConteudoTurma() {
		return qntConteudoTurma;
	}
	public void setQntConteudoTurma(int qntConteudoTurma) {
		this.qntConteudoTurma = qntConteudoTurma;
	}
	public int getQntDataAvaliacao() {
		return qntDataAvaliacao;
	}
	public void setQntDataAvaliacao(int qntDataAvaliacao) {
		this.qntDataAvaliacao = qntDataAvaliacao;
	}
	public int getQntTopicoAula() {
		return qntTopicoAula;
	}
	public void setQntTopicoAula(int qntTopicoAula) {
		this.qntTopicoAula = qntTopicoAula;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getDia() {
		return dia;
	}
	public void setDia(int dia) {
		this.dia = dia;
	}
	public int getSemana() {
		return semana;
	}
	public void setSemana(int i) {
		this.semana = i;
	}
	public String getNomeMes() {
		return nomeMes;
	}
	public void setNomeMes(String nomeMes) {
		this.nomeMes = nomeMes;
	}
	public String getNomeSemana() {
		return nomeSemana;
	}
	public void setNomeSemana(String nomeSemana) {
		this.nomeSemana = nomeSemana;
	}
	public boolean isDetalharMes() {
		return detalharMes;
	}
	public void setDetalharMes(boolean detalharMes) {
		this.detalharMes = detalharMes;
	}
	public boolean isDetalharSemana() {
		return detalharSemana;
	}
	public void setDetalharSemana(boolean detalharSemana) {
		this.detalharSemana = detalharSemana;
	}
	
}