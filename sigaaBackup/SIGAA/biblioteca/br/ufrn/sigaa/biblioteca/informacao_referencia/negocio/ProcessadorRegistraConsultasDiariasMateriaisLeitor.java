/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 04/05/2009
 * 
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.RegistroConsultasMateriaisDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ClasseMaterialConsultado;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroConsultaMaterialInformacional;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroConsultasDiariasMateriais;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica.OrdemClassificacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 * Processador que registra as consultas de materiais informacionais com leitor �tico. <br/><br/>
 * 
 * <strong>OBSERVA��O.: Esse caso de uso gera 2 registros de consulta de materiais. 
 * Gera o registro que era para gerar no caso de uso e tamb�m gera ou incrementa o registro quantitativo de consulta do material
 * </strong>
 * 
 * @author Fred_Castro
 * @since 30/04/2009
 *
 */
public class ProcessadorRegistraConsultasDiariasMateriaisLeitor extends AbstractProcessador {

	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoRegistroConsultaMaterialLeitor personalMov = (MovimentoRegistroConsultaMaterialLeitor) mov;
		RegistroConsultasMateriaisDao dao = null;
		ExemplarDao daoExemplar = null;
		FasciculoDao daoFasciculo = null;
		MaterialInformacionalDao daoMaterial = null;
		
		ClassificacaoBibliograficaDao daoClassificacao = null;
		
		/* Guarda a lista quantitativa gerada por esse caso de uso, essas informa��es quantitativas tamb�m s�o geradas 
		 * manualmente pelo usu�rio, quando o usu�rio utiliza o caso de uso de registrar as consultas sem o leitor */
		List<QuantidadeConsultaClasse> listaConsultas = new ArrayList<QuantidadeConsultaClasse>();
		
		
		
		try {
			
			dao = getDAO(RegistroConsultasMateriaisDao.class, personalMov);
			daoExemplar = getDAO(ExemplarDao.class, personalMov);
			daoFasciculo = getDAO(FasciculoDao.class, personalMov);
			daoMaterial = getDAO(MaterialInformacionalDao.class, personalMov);
			daoClassificacao = getDAO(ClassificacaoBibliograficaDao.class, personalMov);
			
			
			/* ***********************************************************************************
			 *  Para cada material consultado, gera um registro de consulta desse material e incremente 
			 * o registro quantitativo
			 **************************************************************************************/
			for (MaterialInformacional m : personalMov.getMateriais()){
				
				// Cria um novo registro.
				RegistroConsultaMaterialInformacional reg = 
						new RegistroConsultaMaterialInformacional(personalMov.getTurno(), m, personalMov.getData());
				
				// Salva o registro.
				dao.createNoFlush(reg);
				
				// Guarda a informa��o da quantidade consultada por classe para salvar no banco, depois de contar todos
				
				Object o =  daoMaterial.findIdsBibliotecaColecaoETipoDoMaterial(m.getId());
				
				Object[] idsInformacoesMaterial = (Object[]) o ;
				
				// Utilizado para s� registrar consultas para a classifica��es utilizada na biblioteca
				ClassificacaoBibliografica classificacaoUtilizada =  daoClassificacao.findClassificacaoUtilizadaPelaBiblioteca( (Integer) idsInformacoesMaterial[0]);
				
				QuantidadeConsultaClasse qtd = new QuantidadeConsultaClasse((Integer) idsInformacoesMaterial[0], (Integer)idsInformacoesMaterial[1]
				  , (Integer) idsInformacoesMaterial[2], personalMov.getTurno(), personalMov.getData() );
				
				String classificacao = ClassificacoesBibliograficasUtil.encontraClassePrincipal(m.getNumeroChamada(), classificacaoUtilizada.getClassesPrincipaisClassificacaoBibliografica());
								
				if(StringUtils.isEmpty(classificacao)){
					throw new NegocioException("N�o foi poss�vel registrar a consulta, pois n�o foi informada a classifica��o "+classificacaoUtilizada.getDescricao()+" na cataloga��o do material "+m.getCodigoBarras()+". ");
				}
				
				qtd.setClassePrincipalClassificacao(classificacao, classificacaoUtilizada.getOrdem());
				
				if(listaConsultas.contains(qtd)){ // se j� tem, apenas incrementa a quantidade j� existetendo
					
					QuantidadeConsultaClasse qtdTemp = listaConsultas.get(listaConsultas.indexOf(qtd));
					qtdTemp.incrementaQuantidadeConsultas();
					
				}else{
					
					qtd.incrementaQuantidadeConsultas(); // quantidadeConsultas = 1;
					listaConsultas.add(qtd);
				}
				
			
			}

			
			// Para cada registro quantitativo, verifica se vai criar um novo ou atualizar a quantidade de um que j� esteja salvo //
			for (QuantidadeConsultaClasse qtdConsultaClasse : listaConsultas) {
				
				RegistroConsultasDiariasMateriais registroQuantitativo = dao.findRegistroByBibliotecaTipoMaterialColecaoETurno(
								qtdConsultaClasse.getIdBiblioteca(),
								qtdConsultaClasse.getIdTipoMaterial(),
								qtdConsultaClasse.getIdColecao(), 
								qtdConsultaClasse.getTurno(),
								qtdConsultaClasse.getData());
				
				
				// Cria os dados da classe consultada  //
				ClasseMaterialConsultado cmc = new ClasseMaterialConsultado();
				cmc.setQuantidade(qtdConsultaClasse.getQuantidade());
				cmc.setAtivo(true);
				
				if ( qtdConsultaClasse.getClassePrincipalClassificacao1() != null ){
					
					cmc.setClassificacao1(qtdConsultaClasse.getClassePrincipalClassificacao1());
					cmc.setClassificacao2(null);
					cmc.setClassificacao3(null);
				}
				
				if ( qtdConsultaClasse.getClassePrincipalClassificacao2() != null ){
					
					cmc.setClassificacao1(null);
					cmc.setClassificacao2(qtdConsultaClasse.getClassePrincipalClassificacao2());
					cmc.setClassificacao3(null);
				}


				if ( qtdConsultaClasse.getClassePrincipalClassificacao3() != null ){
					
					cmc.setClassificacao1(null);
					cmc.setClassificacao2(null);
					cmc.setClassificacao3(qtdConsultaClasse.getClassePrincipalClassificacao3());
				}
				
				

				
				// Se n�o existem um registro ainda, cria um novo registro quantitativo //
				if ( registroQuantitativo == null ) {
					
					registroQuantitativo = new RegistroConsultasDiariasMateriais(
							new TipoMaterial(qtdConsultaClasse.getIdTipoMaterial()),
							new Colecao(qtdConsultaClasse.getIdColecao()),
							new Biblioteca(qtdConsultaClasse.getIdBiblioteca()),
							qtdConsultaClasse.getTurno(),
							qtdConsultaClasse.getData());
					
					registroQuantitativo.getClassesConsultadas().add(cmc);
					cmc.setRegistroConsultaMaterial(registroQuantitativo);
					
					dao.createNoFlush(registroQuantitativo);
				
				// Se j� existem um registro salvo, Incrementa a quantidade de consultas j� registradas  //	
				} else {
					
					
					// J� existem no banco um quantidade registrada para para a classifica��o do material //
					if ( registroQuantitativo.getClassesConsultadas().contains(cmc) ) {
						
					
						ClasseMaterialConsultado classeSalva = registroQuantitativo.getClassesConsultadas().get(  registroQuantitativo.getClassesConsultadas().indexOf(cmc));
						
						if ( classeSalva.isAtivo() ){
						
							classeSalva.setQuantidade(classeSalva.getQuantidade()+cmc.getQuantidade());
							dao.updateNoFlush(classeSalva);
							
						} else { // a quantidade para a classe foi removida, ent�o cria uma nova.
							registroQuantitativo.getClassesConsultadas().add(cmc);
							cmc.setRegistroConsultaMaterial(registroQuantitativo);
							dao.updateNoFlush(registroQuantitativo);
						}
						
					} else {
						// � o primeiro registro para a classe do material.
						
						registroQuantitativo.getClassesConsultadas().add(cmc);
						cmc.setRegistroConsultaMaterial(registroQuantitativo);
						dao.updateNoFlush(registroQuantitativo);
					}
					
				}
			}

			return null;
		
		} finally {
			if (dao != null)  dao.close();
			if (daoExemplar != null) daoExemplar.close();
			if (daoFasciculo != null) daoFasciculo.close();
			if (daoMaterial != null) daoMaterial.close();
			if (daoClassificacao != null) daoClassificacao.close();
		}
		
	}

	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoRegistroConsultaMaterialLeitor personalMov = (MovimentoRegistroConsultaMaterialLeitor) mov;
		
		
		if (personalMov.getData() == null)
			erros.addErro("A data deve ser informada");
		else{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, 1);
			cal.set(Calendar.YEAR, 2000);
			
			if (CalendarUtils.estorouPrazo(personalMov.getData(), cal.getTime()))
				erros.addErro("A data deve ser posterior a 01/01/2000");
			else if (CalendarUtils.estorouPrazo(new Date(), personalMov.getData()))
				erros.addErro("N�o se pode realizar o cadastro para uma data futura");
		}
		
		if (personalMov.getMateriais().isEmpty())
			erros.addErro("Nenhum Material foi adicionado � lista para realizar a opera��o de registro da consulta.");
		
		if (personalMov.getTurno() != RegistroConsultasDiariasMateriais.TURNO_MATUTINO
				|| personalMov.getTurno() != RegistroConsultasDiariasMateriais.TURNO_VERPERTINO 
				|| personalMov.getTurno() != RegistroConsultasDiariasMateriais.TURNO_NOTURNO )
			erros.addErro("O turno da consulta informado n�o � v�lido.");
		
		
		checkValidation(erros);
		
	}
	
	
	/**
	 * Guarda temporariamente a quantidade de consultas por classe principal para as classifica��es bibliogr�ficas.
	 */
	private class QuantidadeConsultaClasse{
		
		/** O id da biblioteca que registou a consulta */
		private int idBiblioteca;
		/** O id da cole��o que registou a consulta */
		private int idColecao;
		/** O id do tipo de material que registou a consulta */
		private int idTipoMaterial;
		/** O turo que registou a consulta */
		private int turno;
		/** a data que registou a consulta */
		private Date data;
		/** a classifica��o 1 para a qual se registou a consulta */
		private String classePrincipalClassificacao1;
		/** a classifica��o 2 para a qual se registou a consulta */
		private String classePrincipalClassificacao2;
		/** a classifica��o 3 para a qual se registou a consulta */
		private String classePrincipalClassificacao3;
		/** A quantidade de materiais registrados */
		private int quantidade = 0;
		
		public QuantidadeConsultaClasse(int idBiblioteca, int idColecao, int idTipoMaterial, int turno, Date data) {
			super();
			this.idBiblioteca = idBiblioteca;
			this.idColecao = idColecao;
			this.idTipoMaterial = idTipoMaterial;
			this.turno = turno;
			this.data = data;
		}
		
		/**
		 *  Incrementa a quantidade de consultas.
		 */
		public void incrementaQuantidadeConsultas(){
			quantidade++;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((classePrincipalClassificacao1 == null) ? 0 : classePrincipalClassificacao1.hashCode());
			result = prime * result + ((classePrincipalClassificacao2 == null) ? 0 : classePrincipalClassificacao2.hashCode());
			result = prime * result + ((classePrincipalClassificacao3 == null) ? 0 : classePrincipalClassificacao3.hashCode());
			result = prime * result + ((data == null) ? 0 : data.hashCode());
			result = prime * result + idBiblioteca;
			result = prime * result + idColecao;
			result = prime * result + idTipoMaterial;
			result = prime * result + turno;
			return result;
		}

		/** A quantidade � por classifica��o, biblioteca, cole��a, tipo de material, turno e data do cadastro */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			QuantidadeConsultaClasse other = (QuantidadeConsultaClasse) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (classePrincipalClassificacao1 == null) {
				if (other.classePrincipalClassificacao1 != null)
					return false;
			} else if (!classePrincipalClassificacao1.equals(other.classePrincipalClassificacao1))
				return false;
			if (classePrincipalClassificacao2 == null) {
				if (other.classePrincipalClassificacao2 != null)
					return false;
			} else if (!classePrincipalClassificacao2.equals(other.classePrincipalClassificacao2))
				return false;
			if (classePrincipalClassificacao3 == null) {
				if (other.classePrincipalClassificacao3 != null)
					return false;
			} else if (!classePrincipalClassificacao3.equals(other.classePrincipalClassificacao3))
				return false;
			if (data == null) {
				if (other.data != null)
					return false;
			} else if (!data.equals(other.data))
				return false;
			if (idBiblioteca != other.idBiblioteca)
				return false;
			if (idColecao != other.idColecao)
				return false;
			if (idTipoMaterial != other.idTipoMaterial)
				return false;
			if (turno != other.turno)
				return false;
			return true;
		}

		private ProcessadorRegistraConsultasDiariasMateriaisLeitor getOuterType() {
			return ProcessadorRegistraConsultasDiariasMateriaisLeitor.this;
		}

		@Override
		public String toString() {
			return
					"Biblioteca: "+idBiblioteca+
					" Colecao: "+idColecao+
					" Tipo Material: " +idTipoMaterial+
					" Turno: "+turno+
					" Data: "+data+
					" Classificaca��o 1 : "+classePrincipalClassificacao1+
					" Classificaca��o 2 : "+classePrincipalClassificacao2+
					" Classificaca��o 3 : "+classePrincipalClassificacao3+
					" Quantidade: "+quantidade;
		}

		//////////////// sets e gets /////////////////
		
		public int getIdBiblioteca() { return idBiblioteca; }
		public int getIdColecao() { return idColecao; }
		public int getIdTipoMaterial() { return idTipoMaterial; }
		public int getTurno() { return turno; }
		public Date getData() { return data; }
		public String getClassePrincipalClassificacao1() {return classePrincipalClassificacao1;}
		public String getClassePrincipalClassificacao2() {return classePrincipalClassificacao2;}
		public String getClassePrincipalClassificacao3() {return classePrincipalClassificacao3;}
		
		/** Configura a classifica��o correta, dependendo da ordem  */
		public void setClassePrincipalClassificacao(String classePrincipalClassificacao, OrdemClassificacao ordem) {
			if(ordem == OrdemClassificacao.PRIMERA_CLASSIFICACAO)
				this.classePrincipalClassificacao1 = classePrincipalClassificacao;
			if(ordem == OrdemClassificacao.SEGUNDA_CLASSIFICACAO)
				this.classePrincipalClassificacao2 = classePrincipalClassificacao;
			if(ordem == OrdemClassificacao.TERCEIRA_CLASSIFICACAO)
				this.classePrincipalClassificacao3 = classePrincipalClassificacao;
		}
		
		public int getQuantidade() { return quantidade; }
		
	} 
	
}