package br.ufrn.sigaa.monitoria.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.HashCodeUtil;

/*******************************************************************************
 * <p>
 * Classe responsável por interligar a prova de seleção com todos os componentes
 * curriculares que devem ser considerados quando o discente for fazer a
 * inscrição para concorrer a vaga de monitor.
 * </p>
 * 
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "monitoria", name = "prova_selecao_componente_curricular")
public class ProvaSelecaoComponenteCurricular implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_prova_selecao_componente_curricular")
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_prova_selecao")
	private ProvaSelecao provaSelecao;

	@ManyToOne
	@JoinColumn(name = "id_componente_curricular_monitoria")
	private ComponenteCurricularMonitoria componenteCurricularMonitoria;

	@Column(name = "obrigatorio")
	private boolean obrigatorio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registro;

	public ProvaSelecaoComponenteCurricular() {

	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProvaSelecao getProvaSelecao() {
		return provaSelecao;
	}

	public void setProvaSelecao(ProvaSelecao provaSelecao) {
		this.provaSelecao = provaSelecao;
	}

	public ComponenteCurricularMonitoria getComponenteCurricularMonitoria() {
		return componenteCurricularMonitoria;
	}

	public void setComponenteCurricularMonitoria(
			ComponenteCurricularMonitoria componenteCurricularMonitoria) {
		this.componenteCurricularMonitoria = componenteCurricularMonitoria;
	}

	/**
	 * Informa se o componente curricular é obrigatório para inscrição do
	 * discente na prova seletiva
	 * 
	 * @return
	 */
	public boolean isObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public ListaMensagens validate() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		ProvaSelecaoComponenteCurricular outro = (ProvaSelecaoComponenteCurricular) obj;
		return ((this.provaSelecao.getId() == outro.getProvaSelecao().getId()) && (this.componenteCurricularMonitoria
				.getId() == outro.getComponenteCurricularMonitoria().getId()));
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(this.provaSelecao.getId(),
				this.componenteCurricularMonitoria.getId());
	}

	public RegistroEntrada getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroEntrada registro) {
		this.registro = registro;
	}

}
