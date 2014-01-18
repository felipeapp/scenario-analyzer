/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed-Bean para geração do arquivo de texto de inscrição do ENADE.
 * 
 * @author Gleydson
 */

@Component(value = "gerarArquivoENADE")
@Scope("request")
public class GerarArquivoENADEMBean extends SigaaAbstractController<Discente> {

	/** Tipos de separadores CSV utilizados. */
	private enum Separador {
		PONTO_E_VIRGULA, VIRGULA, TABULACAO, ESPACO;
		public String getCaractereSeparador() {
			switch (this) {
			case PONTO_E_VIRGULA : return ";";
			case VIRGULA : return ",";
			case TABULACAO : return "\t";
			case ESPACO : return " ";
			default : return null;
			}
		}
	}
	
	/** Lista de matrículas para geração do arquivo. */
	private String matriculas;
	/** Tipo de discente. */
	private String tipo;
	/** Caractere separador utilizado na lista de matrículas. */
	private Separador separador;

	/**
	 * Gera o arquivo para upload no ENADE.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/geracao_enade.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws IOException
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarArquivo() throws IOException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_SIGAA);
		ArrayList<Long> listaMatriculas = new ArrayList<Long>();
		// separa as matrículas informadas pelo usuário
		if (!isEmpty(matriculas)) {
			String delim = separador.getCaractereSeparador();
			StringTokenizer st = new StringTokenizer(matriculas, delim);
			while (st.hasMoreTokens()) {
				String token = st.nextToken().trim().replace("\r", "").replace("\n", "");
				if (isEmpty(token)) continue;
				Long matricula;
				try {
					matricula = Long.parseLong(token);
				} catch(NumberFormatException e) {
					addMensagemErro("A matrícula " + token + " é inválida.");
					return null;
				}
				listaMatriculas.add(matricula);
			}
		}
		validateRequired(listaMatriculas, "Matrículas", erros);
		if (hasErrors()) return null;
		// consulta os discentes
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		Collection<DiscenteGraduacao> discentes = dao.findByMatriculas(listaMatriculas);
		// valida se todas as matrículas foram encontradas
		StringBuffer naoEncontrada = new StringBuffer();
		for (Long matricula : listaMatriculas) {
			boolean encontrada = false;
			for (DiscenteGraduacao discente : discentes) {
				if (matricula.equals(discente.getMatricula())) {
					encontrada = true; 
					continue;
				}
			}
			if (!encontrada) naoEncontrada.append(matricula).append(", ");
		}
		if (naoEncontrada.lastIndexOf(", ") > 0) {
			naoEncontrada.delete(naoEncontrada.lastIndexOf(", "), naoEncontrada.length());
			addMensagemErro("A(s) seguinte(s) matrícula(s) não foi(ram) encontrada(s): " +naoEncontrada);
			return null;
		}
		String codigoINEP = null;
		String nomeCurso = null;
		
		DecimalFormat df = new DecimalFormat("00000000000");
		ArrayList<String> linhas = new ArrayList<String>();
		for (DiscenteGraduacao discente : discentes) {
			StringBuffer buffer = new StringBuffer();
			nomeCurso = StringUtils.toAscii(discente.getCurso().getDescricao()).replace(" ", "_");
			append(buffer, "0570" );
			codigoINEP = discente.getMatrizCurricular().getCodigoINEP();
			if (isEmpty(codigoINEP)) codigoINEP = discente.getCurso().getCodigoINEP();
			append(buffer, codigoINEP);

			
			if (isConcluinte()) {
				buffer.append("C;");
			} else {
				buffer.append("I;");
			}
			
			Pessoa p = discente.getPessoa();
			// Tratamento do CPF

			Long cpf = p.getCpf_cnpj();
			if ( cpf == null ) {
				cpf = 0l;
			}
			append(buffer, df.format(cpf));
			
			append(buffer, p.getNomeAscii());
			
			if ( p.getIdentidade() != null )
				append(buffer, p.getIdentidade().getNumero());
			else
				append(buffer, "0");
			
			
			buffer.append("0;");
			buffer.append("0;");
			buffer.append("0;");
			if ( p.getEnderecoContato() == null || StringUtils.isEmpty(p.getEnderecoContato().getCep()) ) {
				append(buffer,"59000000");
			} else {
				String cep = p.getEnderecoContato().getCep();
				cep = cep.replace("-", "");
				cep = cep.replace(".", "");
				append(buffer, cep.trim());
			}
			if ( p.getEnderecoContato() != null ) {
				append(buffer, p.getEnderecoContato().getLogradouro());
				append(buffer, p.getEnderecoContato().getNumero());
				append(buffer, p.getEnderecoContato().getComplemento());
				append(buffer, p.getEnderecoContato().getBairro());
				append(buffer, p.getEnderecoContato()
						.getUnidadeFederativa().getSigla());
			} else {
				append(buffer, " ");
				append(buffer, "0");
				append(buffer, " ");
				append(buffer, " ");
				append(buffer, "RN");
			}
			if ( p.getEnderecoContato() != null && p.getEnderecoContato().getMunicipio() != null ) {
				append(buffer, p.getEnderecoContato().getMunicipio()
					.getNome());
			} else {
				append(buffer, "Natal");
			}
			String DDD = p.getCodigoAreaNacionalTelefoneCelular() != null ? p.getCodigoAreaNacionalTelefoneCelular().toString() : "";
			String telefone = !isEmpty(p.getCelular()) ? p.getCelular() : p.getTelefone();
			if (telefone != null) {
				telefone = telefone.replace("-", "");
				telefone = telefone.replace(".", "");
			} else
				telefone = "";
			
			append(buffer, DDD+telefone);
			
			// Ano de conclusão do ensino médio
			Short anoConclusaoMedio =  discente.getAnoConclusaoMedio();
			if (anoConclusaoMedio == null) {
				anoConclusaoMedio = new Short( (short) (discente.getAnoEntrada() - 1));
			}
			append(buffer, anoConclusaoMedio + "");

			// Ano de início da graduação
			if ( isConcluinte() ) {
				append(buffer, (discente.getAnoEntrada()) + ""); 
			} else {
				append(buffer, "");
			}
			
			// CEP do Município do pólo (somente para alunos EAD)
			if (discente.getPolo() != null) {
				if(discente.getPolo().getCep() == null || StringUtils.isEmpty(discente.getPolo().getCep())){
					append(buffer,"59000000");
				} else {
					String cep = discente.getPolo().getCep();
					cep = cep.replace("-", "");
					cep = cep.replace(".", "");
					append(buffer, cep.trim());
				}
			} else {
				append(buffer, "");
			}
			
			buffer.append((discente.getMatrizCurricular().getTurno().isManha() == true ? "1" : "0") + ";");
			buffer.append((discente.getMatrizCurricular().getTurno().isTarde() == true ? "1" : "0")	+ ";");
			buffer.append((discente.getMatrizCurricular().getTurno().isNoite() == true ? "1" : "0")	+ ";");

			
			linhas.add(buffer.toString());

		}

		// Retornar arquivo de exportação
		if (linhas.size() > 0) {
			getCurrentResponse().setContentType("text/plain");
			getCurrentResponse().setHeader("Content-disposition",
					"attachment; filename=\"ENADE_" + (nomeCurso == null ? "" : nomeCurso + "_") + (codigoINEP == null ? "" : codigoINEP + "_") + tipo + ".txt\"");
	
			PrintWriter out = new PrintWriter(getCurrentResponse().getWriter());
			for (String linha : linhas ) {
				out.println(linha);
			}
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();
		} else {
			addMensagemErro("O arquivo a ser gerado não possui dados");
			return null;
		}

		return null;
		
	}

	private boolean isConcluinte() {
		return tipo.equalsIgnoreCase("Concluinte");
	}

	public void append(StringBuffer buffer, String texto) {
		buffer.append(StringUtils.removePV(texto) + ";");
	}

	public String getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(String matriculas) {
		this.matriculas = matriculas;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Collection<SelectItem> getSeparadoresCombo() {
		Collection<SelectItem> lista = new LinkedList<SelectItem>();
		lista.add(new SelectItem(Separador.PONTO_E_VIRGULA, "PONTO E VÍRGULA"));
		lista.add(new SelectItem(Separador.VIRGULA, "VÍRGULA"));
		lista.add(new SelectItem(Separador.TABULACAO, "TABULAÇÃO"));
		lista.add(new SelectItem(Separador.ESPACO, "ESPAÇO"));
		return lista;
	}

	public Separador getSeparador() {
		return separador;
	}

	public void setSeparador(Separador separador) {
		this.separador = separador;
	}

}
