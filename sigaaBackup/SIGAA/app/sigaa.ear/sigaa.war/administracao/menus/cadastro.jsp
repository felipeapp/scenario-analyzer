		 <ul>

		  <li>Ensino
		   <ul>
			<li><h:commandLink action="#{areaSesu.listar}" value="�rea da SESU" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{campusIes.listar}" value="Campus da IES" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{cargoAcademico.listar}" value="Cargo Acad�mico" onclick="setAba('cadastro')" /></li>			
			<li><h:commandLink action="#{formaIngresso.listar}" value="Forma de Ingresso" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{categoriaDiscenteEspecial.listar}" value="Categoria de Discente Especial" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{grauAcademico.listar}" value="Grau Acad�mico" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{grauFormacao.listar}" value="Grau de Forma��o" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{modalidadeEducacao.listar}" value="Modalidade de Educa��o" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{naturezaCurso.listar}" value="Natureza do Curso" onclick="setAba('cadastro')" /></li>
			
			<li><h:commandLink action="#{processoSeletivo.listar}" value="Processo Seletivo" onclick="setAba('cadastro')" /></li>
			
			<li><h:commandLink action="#{situacaoCursoHabil.listar}" value="Situa��o de Curso H�bil" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{situacaoDiploma.listar}" value="Situa��o de Diploma" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{situacaoMatricula.listar}" value="Situa��o de Matr�cula" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{situacaoPropostaMBean.listar}" value="Situa��o de Proposta Lato Sensu" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoAtividade.listar}" value="Tipo de Atividade" onclick="setAba('cadastro')" /></li>
			<%--<li><h:commandLink action="#{tipoAvaliacao.listar}" value="Tipo de Avalia��o" onclick="setAba('cadastro')" /></li>--%>
			<li><h:commandLink action="#{tipoAtividadeComplementar.listar}" value="Tipo de Atividade Complementar" onclick="setAba('cadastro')" /></li>
			<%--<li><h:commandLink action="#{tipoEntrada.listar}" value="Tipo de Entrada" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{tipoProcedenciaAluno.listar}" value="Tipo de Proced�ncia do Aluno" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoRedeEnsino.listar}" value="Tipo de Rede de Ensino" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoRegimeAluno.listar}" value="Tipo de Regime do Aluno" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoRegimeLetivo.listar}" value="Tipo de Regime Letivo" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>

		  <li>Pesquisa
		   <ul>
			<%--<li><h:commandLink action="#{areaConhecimentoUnesco.listar}" value="Area de Conhecimento da Unesco" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{classificacaoFinanciadora.listar}" value="Classifica��o da Entidade Financiadora" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{entidadeFinanciadora.listar}" value="Entidade Financiadora" onclick="setAba('cadastro')" /></li>
			<%--<li><h:commandLink action="#{entidadeFinanciadoraEstagio.listar}" value="Entidade Financiadora de Est�gio" onclick="setAba('cadastro')" /></li> --%>
			<%--<li><h:commandLink action="#{entidadeFinanciadoraOutra.listar}" value="Entidade Financiadora Outra" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{grupoEntidadeFinanciadora.listar}" value="Grupo da Entidade Financiadora" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoCampusUnidade.listar}" value="Tipo de Campus da Unidade" onclick="setAba('cadastro')" /></li>
			<%--<li><h:commandLink action="#{tipoOrientacaoDiscente.listar}" value="Tipo de Orienta��o do Discente" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{tipoPublicoAlvo.listar}" value="Tipo de P�blico Alvo" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>
		  
		  <li>Extens�o
			<ul>  
                 <li><h:commandLink action="#{gerenciarCadastrosParticipantesMBean.iniciarAlteracaoCadastroParticipante}" value="Gerenciar Cadastro de Participantes" onclick="setAba('cadastro')" /></li>
			</ul>
		  </li>

		  <li>Outros
		   <ul>
		    <li><h:commandLink action="#{estadoCivil.listar}" value="Estado Civil" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{instituicoesEnsino.listar}" value="Institui��es de Ensino" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{municipio.listar}" value="Munic�pio" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{pais.listar}" value="Pa�s" onclick="setAba('cadastro')" /></li>
		 	<%-- <li><h:commandLink action="#{tipoDocumentoLegal.listar}" value="Tipo de Documento Legal" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{tipoEtnia.listar}" value="Tipo de Etnia" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoLogradouro.listar}" value="Tipo de Logradouro" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoNecessidadeEspecial.listar}" value="Tipo de Necessidade Especial" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoRaca.listar}" value="Tipo de Ra�a" onclick="setAba('cadastro')" /></li>
			<%-- <li><h:commandLink action="#{tipoVeiculacaoEad.listar}" value="Tipo de Veicula��o de Ensino � Dist�ncia" onclick="setAba('cadastro')" /></li>--%>
			<li><h:commandLink action="#{turno.listar}" value="Turno" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{unidadeFederativa.listar}" value="Unidade Federativa" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{integracaoTipoBolsaMBean.listar}" value="Tipo Bolsa Integra��o" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>
<%--
		  <li>Edital
		   <ul>
		    <li><h:commandLink action="#{editalMBean.listar}" value="Editais" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>
 
	 	  <li>Unidade
		   <ul>
		    <li><h:commandLink action="#{unidade.listar}" value="Unidade" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>
--%> 
		  <li>Servidor
		   <ul>
		    <li><h:commandLink actionListener="#{menu.redirecionar}" onclick="setAba('relatorios')" value="Servidor">
					<f:param value="/administracao/cadastro/Servidor/lista.jsf" name="url"/>
				</h:commandLink>
			</li>
		   </ul>
		  </li> 
		  <li>F�rum de Curso
		   <ul>
		    <li><h:commandLink action="#{forumCursoDocente.iniciar}" value="Vincular Servidor a F�rum de Curso" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>
		  <li>GRU - Guia de Recolhimento da Uni�o
		   <ul>
		    <li><h:commandLink action="#{configuracaoGRUMBean.preCadastrar}" value="Cadastrar Configura��o de GRU" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{configuracaoGRUMBean.listar}" value="Listar/Alterar Configura��o de GRU" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{grupoEmissaoGRUMBean.preCadastrar}" value="Cadastrar Grupo de Emiss�o de GRU" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{grupoEmissaoGRUMBean.listar}" value="Listar/Alterar Grupo de Emiss�o de GRU" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>
		  <li>Rela��es Internacionais
		   <ul>
		  	<li> <h:commandLink action="#{entidadeTraducaoMBean.preCadastrar}" value="Cadastrar Entidade do Hist�rico" onclick="setAba('cadastro')"/> </li>
			<li> <h:commandLink action="#{entidadeTraducaoMBean.listar}" value="Listar/Alterar Entidade do Hist�rico" onclick="setAba('cadastro')"/></li>
			<li> <h:commandLink action="#{itemTraducaoMBean.preCadastrar}" value="Cadastrar Elemento do Hist�rico" onclick="setAba('cadastro')"/> </li>
			<li> <h:commandLink action="#{itemTraducaoMBean.listar}" value="Listar/Alterar Elemento do Hist�rico" onclick="setAba('cadastro')"/></li>
			<li> <h:commandLink action="#{constanteTraducaoMBean.preCadastrar}" value="Cadastrar Constantes para Internacionaliza��o" onclick="setAba('cadastro')"/> </li>
			<li> <h:commandLink action="#{constanteTraducaoMBean.listar}" value="Listar/Alterar Constantes para Internacionaliza��o" onclick="setAba('cadastro')"/></li>
		   </ul>
		  </li>
		 </ul>