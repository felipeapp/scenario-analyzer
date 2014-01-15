/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
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

	/** Lista de matrículas para geração do arquivo. */
	private String matriculas;
	/** Tipo de discente. */
	private String tipo;

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

		DiscenteDao dao = getDAO(DiscenteDao.class);

		ArrayList<Integer> listaMatriculas = new ArrayList<Integer>();

		// Busca as matrículas com o separador do "\n"
		StringTokenizer st = new StringTokenizer(matriculas);
		while (st.hasMoreTokens()) {
			
			String token = st.nextToken();
			Integer matricula = 0;
			
			try {
				matricula = Integer.parseInt(token);
			} catch(NumberFormatException e) {
				addMensagemErro("A matrícula " + token + " é inválida.");
				return null;
			}
			
			listaMatriculas.add(matricula);
		}

		String codigoINEP = null;

		String nomeCurso = null;
		
		ArrayList<String> linhas = new ArrayList<String>();
		
		DecimalFormat df = new DecimalFormat("00000000000");
		
		for (Integer matricula : listaMatriculas) {

			StringBuffer buffer = new StringBuffer();
			
			DiscenteAdapter d = dao.findByMatricula(matricula, NivelEnsino.GRADUACAO);
			
			if (isEmpty(d)) {
				addMensagemErro("O discente " + matricula + " não foi encontrado");
				return null;
			}
			
			DiscenteGraduacao dg = (DiscenteGraduacao) dao.findByPK(d.getId());
			

			nomeCurso = StringUtils.toAscii(d.getCurso().getDescricao()).replace(" ", "_");
			
			append(buffer, "0570" );
			append(buffer, dg.getCurso().getCodigoINEP());
			codigoINEP = dg.getCurso().getCodigoINEP();

			if (isConcluinte()) {
				buffer.append("C;");
			} else {
				buffer.append("I;");
			}
			
			Pessoa p = dg.getPessoa();
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
			Short anoConclusaoMedio =  dg.getAnoConclusaoMedio();
			if (anoConclusaoMedio == null) {
				anoConclusaoMedio = new Short( (short) (dg.getAnoEntrada() - 1));
			}
			append(buffer, anoConclusaoMedio + "");

			// Ano de início da graduação
			if ( isConcluinte() ) {
				append(buffer, (dg.getAnoEntrada()) + ""); 
			} else {
				append(buffer, "");
			}
			
			// CEP do Município do pólo (somente para alunos EAD)
			if (dg.getPolo() != null) {
				if(dg.getPolo().getCep() == null || StringUtils.isEmpty(dg.getPolo().getCep())){
					append(buffer,"59000000");
				} else {
					String cep = dg.getPolo().getCep();
					cep = cep.replace("-", "");
					cep = cep.replace(".", "");
					append(buffer, cep.trim());
				}
			} else {
				append(buffer, "");
			}
			
			buffer.append((dg.getMatrizCurricular().getTurno().isManha() == true ? "1" : "0") + ";");
			buffer.append((dg.getMatrizCurricular().getTurno().isTarde() == true ? "1" : "0")	+ ";");
			buffer.append((dg.getMatrizCurricular().getTurno().isNoite() == true ? "1" : "0")	+ ";");

			
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

	
}
