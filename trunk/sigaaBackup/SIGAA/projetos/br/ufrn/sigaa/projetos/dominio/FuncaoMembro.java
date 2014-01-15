/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * 
 *  Descreve a função do membro da equipe da atividade de extensão:
 *  
 *  COORDENADOR;
 *  VICE_COORDENADOR;
 *  COLABORADOR;
 *  MINISTRANTE;
 *  INSTRUTOR / SUPERVISOR;
 *  CONSULTOR / TUTOR;
 *  ORIENTADOR;
 *  ASSESSOR;
 *  PALESTRANTE;
 *  AUXILIAR TÉCNICO;
 *  PRESTADOR DE SERVIÇOS;
 *  MONITOR;
 *  ALUNO BOLSISTA;
 *  ALUNO VOLUNTARIO;
 *  OUTROS;
 *  ETC...
 * 
 *
 * @author Gleydson
 * @author Victor Hugo
 * @author ilueny santos
 * 
 */

@Entity
@Table(schema = "projetos", name = "funcao_membro")
public class FuncaoMembro implements Validatable {

	//Constantes de escopo das funções
	/** Atributo utilizado para representar o Escorpo dos Servidores */
	public static int ESCOPO_SERVIDOR = 1;
	/** Atributo utilizado para representar o Escorpo dos Discentes */
	public static int ESCOPO_DISCENTE = 2;	
	/** Atributo utilizado para representar o Escorpo  do Membro Externo */
	public static int ESCOPO_EXTERNO  = 3;
	/** Atributo utilizado para representar o Escorpo de Todos os Membros */
	public static int ESCOPO_TODOS    = 4;
	/** Atributo utilizado para representar o Escorpo do Membro Docente */
	public static int ESCOPO_DOCENTE  = 5;
	
	/** Atributo utilizado para identificar o Coordenador */
	public static final int COORDENADOR 		 = 1;
	/** Atributo utilizado para identificar o Vice Coordenador */
	public static final int VICE_COORDENADOR 	 = 2;
	/** Atributo utilizado para identificar o Colaborador */
	public static final int COLABORADOR 		 = 3;
	/** Atributo utilizado para identificar o Ministrante */
	public static final int MINISTRANTE 		 = 4;
	/** Atributo utilizado para identificar o Instrutor Supervisor */
	public static final int INSTRUTOR_SUPERVISOR = 5;
	/** Atributo utilizado para identificar o Consultor Tutor */
	public static final int CONSULTOR_TUTOR 	 = 6;
	/** Atributo utilizado para identificar o Monitor */
	public static final int MONITOR							= 101;
	/** Atributo utilizado para identificar o Bolsista */
	public static final int BOLSISTA						= 102;
	/** Atributo utilizado para identificar o Voluntário */
	public static final int VOLUNTARIO						= 103;
	/** Atributo utilizado para identificar o Aluno em Atividade Curricular */
	public static final int ALUNO_EM_ATIVIDADE_CURRICULAR 	= 104;
	
	/** Atributo utilizado para representar o ID */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_funcao_membro")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

    /** Descrição da Função */
    @Column(name = "descricao", nullable = false)
    private String descricao;

    /** Escopo da função, conforme o tipo de membro: 1 - servidor, 2 - discente, 3 - externo. */
    @Column(name = "escopo", nullable = false)
    private int escopo;

    /** Indica se essa função é utilizada nos projetos de pesquisa */
    @Column(name = "pesquisa", nullable = false)
    private boolean pesquisa;

    /** Indica se essa função é utilizada nos projetos de extensão */
    @Column(name = "extensao", nullable = false)
    private boolean extensao;

    /** Indica se essa função é utilizada nos projetos Integrados  */
    @Column(name = "integrados", nullable = false)
    private boolean integrados;

    /** Indica se essa função é utilizada nos projetos de ensino */
    @Column(name = "ensino", nullable = false)
    private boolean ensino;

    /** Serve para indicar quais os projeto que possuem tal função */
	@Transient
    private List<String> cadastrarEm;
    
    /** Creates a new instance of FuncaoMembro */
    public FuncaoMembro() {
    }

    public FuncaoMembro(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Determina o escopo da função 
     * do membro da equipe
     * 
	 * @return 1 = Servidor, 2 = Discente
	 */
	public int getEscopo() {
		return escopo;
	}
	
	/**
	 * Método utilizado para infomrar o Escorpo
	 * @return
	 */
	public String getScopoString() {
	    switch (escopo) {
	    case 1: return "SERVIDOR";
	    case 2: return "DISCENTE";
	    case 3: return "EXTERNO";
	    case 4: return "TODOS";
	    case 5: return "DOCENTE";
	    default:
		return "NÃO INFORMADO";
	    }
	}

	/**
	 * Método utilizado para setar o escorpo
	 * @param escopo The escopo to set.
	 */
	public void setEscopo(int escopo) {
		this.escopo = escopo;
	}

	public boolean isPesquisa() {
		return pesquisa;
	}

	public void setPesquisa(boolean pesquisa) {
		this.pesquisa = pesquisa;
	}

	public boolean isExtensao() {
		return extensao;
	}

	public void setExtensao(boolean extensao) {
		this.extensao = extensao;
	}

	public boolean isIntegrados() {
		return integrados;
	}

	public void setIntegrados(boolean integrados) {
		this.integrados = integrados;
	}

	public boolean isEnsino() {
		return ensino;
	}

	public void setEnsino(boolean ensino) {
		this.ensino = ensino;
	}

	public List<String> getCadastrarEm() {
		return cadastrarEm;
	}

	public void setCadastrarEm(List<String> cadastrarEm) {
		this.cadastrarEm = cadastrarEm;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();		
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		ValidatorUtil.validateRequired(escopo, "Escopo", lista);
		return lista;
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
	    FuncaoMembro other = (FuncaoMembro) obj;
	    if (id != other.id)
		return false;
	    return true;
	}
	
}
