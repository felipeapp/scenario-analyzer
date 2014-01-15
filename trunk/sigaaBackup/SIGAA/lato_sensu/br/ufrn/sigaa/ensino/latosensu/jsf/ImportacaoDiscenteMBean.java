package br.ufrn.sigaa.ensino.latosensu.jsf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

@Component @Scope("session")
public class ImportacaoDiscenteMBean extends SigaaAbstractController<DiscenteLato> {

	// 0 - nome; 
	// 1 - email;
	// 2 - Telefone;
	// 3 - Celular;
	// 4 - RG;
	// 5 - CPF;
	// 6 - Curso;
	// 7 - Processo Seletivo;
	// 8 - TurmaEntrada;
	private List<String[]> dados;

	private UploadedFile arquivo;

	public ImportacaoDiscenteMBean() {
		obj = new DiscenteLato();
		obj.setDiscente(new Discente());
	}

	public String preImportar() {
		return forward("/administracao/importacao/form.jsp");
	}
	
	public void importar() throws NegocioException, ArqException, IOException {
		carregarDados();
		
		for (int i = 1; i < dados.size(); i++) {
			String[] linha = dados.get(i);

			if ( linha[1] != null && linha[5] != null && !linha[1].isEmpty() && !linha[5].isEmpty() ) {
			
				DiscenteLato dl = new DiscenteLato();
				dl.setDiscente(new Discente());
	
				if ( i== 61) {
					System.out.println();
				}
				
				PessoaDao dao = getDAO(PessoaDao.class);
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + i + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
				Pessoa p = (Pessoa) dao.findMaisRecenteByCPF(Long.parseLong(Formatador.getInstance().parseStringCPFCNPJ(linha[5])));
				
				if ( p == null ) {
					dl.getDiscente().setPessoa(new Pessoa());
					dl.getDiscente().getPessoa().setNome(linha[0]);	
					dl.getDiscente().getPessoa().setEmail(linha[1]);	
					dl.getDiscente().getPessoa().setTelefone(linha[2]);
					dl.getDiscente().getPessoa().setCelular(linha[3]);
					dl.getDiscente().getPessoa().setIdentidade(new Identidade());
					dl.getDiscente().getPessoa().getIdentidade().setNumero(linha[4]);
					dl.getDiscente().getPessoa().setCpf_cnpj(Long.parseLong(Formatador.getInstance().parseStringCPFCNPJ(linha[5])));
					dl.getDiscente().getPessoa().setSexo('M');
					dl.getDiscente().getPessoa().setDataNascimento(CalendarUtils.createDate(01, 05, 1980));
					dl.getDiscente().getPessoa().setNomeMae("Não Informado");
					dl.getDiscente().getPessoa().setNomePai("Não Informado");
					dl.getDiscente().setObservacao("Importado da SEDIS");
				} else {
					dl.getDiscente().setPessoa(p);
				}

				dl.getDiscente().setNivel(NivelEnsino.LATO);
				
				dl.getDiscente().setStatus(StatusDiscente.ATIVO);
				
				dl.setCurso(getGenericDAO().findByPrimaryKey(Integer.parseInt(linha[6]), Curso.class));
				dl.setProcessoSeletivo(getGenericDAO().findByPrimaryKey(Integer.parseInt(linha[7]), ProcessoSeletivo.class));
				dl.setTurmaEntrada(getGenericDAO().findByPrimaryKey(Integer.parseInt(linha[8]), TurmaEntradaLato.class));
				dl.getDiscente().setFormaIngresso(getGenericDAO().findByPrimaryKey(994409, FormaIngresso.class));
				
				dl.setTipoProcedenciaAluno(null);
				
				dl.setAnoIngresso(2013);
				dl.setPeriodoIngresso(1);
				dl.setMesEntrada(4);
			
				ProcessadorDiscente processadorDiscente = new ProcessadorDiscente();
				DiscenteMov discmov = new DiscenteMov();
				discmov.setDiscenteAntigo(false);
				discmov.setCodMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
				discmov.setObjMovimentado(dl);
				discmov.setRegistroEntrada(getRegistroEntrada());
				discmov.setUsuarioLogado(getUsuarioLogado());
				discmov.setSistema(Sistema.SIGAA);
			
				processadorDiscente.execute(discmov);
			
			}
		}
	}
	
	private void carregarDados() throws IOException {
		dados = new ArrayList<String[]>();
		String[] linha;
		
		BufferedReader StrR = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));

		String Str;
		String[] TableLine;
		while ((Str = StrR.readLine()) != null) {
			TableLine = Str.split("\t");
			int col = 0;
			linha = new String[9];
				lido : for (String cell : TableLine) {
					linha[col++] = new String(cell.trim().getBytes(), "UTF-8");
					System.out.println( cell.trim() + " | " + linha[col-1] );
					if ( col == 9) break lido;
				}
					dados.add(linha);
				}

		StrR.close();		
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}
	
}