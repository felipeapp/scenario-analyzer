		 <ul>

		  <li>Ensino
		   <ul>
			<li><h:commandLink action="#{areaSesu.listar}" value="Área da SESU" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{campusIes.listar}" value="Campus da IES" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{cargoAcademico.listar}" value="Cargo Acadêmico" onclick="setAba('cadastro')" /></li>			
			<li><h:commandLink action="#{formaIngresso.listar}" value="Forma de Ingresso" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{categoriaDiscenteEspecial.listar}" value="Categoria de Discente Especial" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{grauAcademico.listar}" value="Grau Acadêmico" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{grauFormacao.listar}" value="Grau de Formação" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{modalidadeEducacao.listar}" value="Modalidade de Educação" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{naturezaCurso.listar}" value="Natureza do Curso" onclick="setAba('cadastro')" /></li>
			
			<li><h:commandLink action="#{processoSeletivo.listar}" value="Processo Seletivo" onclick="setAba('cadastro')" /></li>
			
			<li><h:commandLink action="#{situacaoCursoHabil.listar}" value="Situação de Curso Hábil" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{situacaoDiploma.listar}" value="Situação de Diploma" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{situacaoMatricula.listar}" value="Situação de Matrícula" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{situacaoPropostaMBean.listar}" value="Situação de Proposta Lato Sensu" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoAtividade.listar}" value="Tipo de Atividade" onclick="setAba('cadastro')" /></li>
			<%--<li><h:commandLink action="#{tipoAvaliacao.listar}" value="Tipo de Avaliação" onclick="setAba('cadastro')" /></li>--%>
			<li><h:commandLink action="#{tipoAtividadeComplementar.listar}" value="Tipo de Atividade Complementar" onclick="setAba('cadastro')" /></li>
			<%--<li><h:commandLink action="#{tipoEntrada.listar}" value="Tipo de Entrada" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{tipoProcedenciaAluno.listar}" value="Tipo de Procedência do Aluno" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoRedeEnsino.listar}" value="Tipo de Rede de Ensino" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoRegimeAluno.listar}" value="Tipo de Regime do Aluno" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoRegimeLetivo.listar}" value="Tipo de Regime Letivo" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>

		  <li>Pesquisa
		   <ul>
			<%--<li><h:commandLink action="#{areaConhecimentoUnesco.listar}" value="Area de Conhecimento da Unesco" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{classificacaoFinanciadora.listar}" value="Classificação da Entidade Financiadora" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{entidadeFinanciadora.listar}" value="Entidade Financiadora" onclick="setAba('cadastro')" /></li>
			<%--<li><h:commandLink action="#{entidadeFinanciadoraEstagio.listar}" value="Entidade Financiadora de Estágio" onclick="setAba('cadastro')" /></li> --%>
			<%--<li><h:commandLink action="#{entidadeFinanciadoraOutra.listar}" value="Entidade Financiadora Outra" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{grupoEntidadeFinanciadora.listar}" value="Grupo da Entidade Financiadora" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoCampusUnidade.listar}" value="Tipo de Campus da Unidade" onclick="setAba('cadastro')" /></li>
			<%--<li><h:commandLink action="#{tipoOrientacaoDiscente.listar}" value="Tipo de Orientação do Discente" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{tipoPublicoAlvo.listar}" value="Tipo de Público Alvo" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>
		  
		  <li>Extensão
			<ul>  
                 <li><h:commandLink action="#{gerenciarCadastrosParticipantesMBean.iniciarAlteracaoCadastroParticipante}" value="Gerenciar Cadastro de Participantes" onclick="setAba('cadastro')" /></li>
			</ul>
		  </li>

		  <li>Outros
		   <ul>
		    <li><h:commandLink action="#{estadoCivil.listar}" value="Estado Civil" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{instituicoesEnsino.listar}" value="Instituições de Ensino" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{municipio.listar}" value="Município" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{pais.listar}" value="País" onclick="setAba('cadastro')" /></li>
		 	<%-- <li><h:commandLink action="#{tipoDocumentoLegal.listar}" value="Tipo de Documento Legal" onclick="setAba('cadastro')" /></li> --%>
			<li><h:commandLink action="#{tipoEtnia.listar}" value="Tipo de Etnia" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoLogradouro.listar}" value="Tipo de Logradouro" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoNecessidadeEspecial.listar}" value="Tipo de Necessidade Especial" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{tipoRaca.listar}" value="Tipo de Raça" onclick="setAba('cadastro')" /></li>
			<%-- <li><h:commandLink action="#{tipoVeiculacaoEad.listar}" value="Tipo de Veiculação de Ensino à Distância" onclick="setAba('cadastro')" /></li>--%>
			<li><h:commandLink action="#{turno.listar}" value="Turno" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{unidadeFederativa.listar}" value="Unidade Federativa" onclick="setAba('cadastro')" /></li>
			<li><h:commandLink action="#{integracaoTipoBolsaMBean.listar}" value="Tipo Bolsa Integração" onclick="setAba('cadastro')" /></li>
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
		  <li>Fórum de Curso
		   <ul>
		    <li><h:commandLink action="#{forumCursoDocente.iniciar}" value="Vincular Servidor a Fórum de Curso" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>
		  <li>GRU - Guia de Recolhimento da União
		   <ul>
		    <li><h:commandLink action="#{configuracaoGRUMBean.preCadastrar}" value="Cadastrar Configuração de GRU" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{configuracaoGRUMBean.listar}" value="Listar/Alterar Configuração de GRU" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{grupoEmissaoGRUMBean.preCadastrar}" value="Cadastrar Grupo de Emissão de GRU" onclick="setAba('cadastro')" /></li>
		    <li><h:commandLink action="#{grupoEmissaoGRUMBean.listar}" value="Listar/Alterar Grupo de Emissão de GRU" onclick="setAba('cadastro')" /></li>
		   </ul>
		  </li>
		  <li>Relações Internacionais
		   <ul>
		  	<li> <h:commandLink action="#{entidadeTraducaoMBean.preCadastrar}" value="Cadastrar Entidade do Histórico" onclick="setAba('cadastro')"/> </li>
			<li> <h:commandLink action="#{entidadeTraducaoMBean.listar}" value="Listar/Alterar Entidade do Histórico" onclick="setAba('cadastro')"/></li>
			<li> <h:commandLink action="#{itemTraducaoMBean.preCadastrar}" value="Cadastrar Elemento do Histórico" onclick="setAba('cadastro')"/> </li>
			<li> <h:commandLink action="#{itemTraducaoMBean.listar}" value="Listar/Alterar Elemento do Histórico" onclick="setAba('cadastro')"/></li>
			<li> <h:commandLink action="#{constanteTraducaoMBean.preCadastrar}" value="Cadastrar Constantes para Internacionalização" onclick="setAba('cadastro')"/> </li>
			<li> <h:commandLink action="#{constanteTraducaoMBean.listar}" value="Listar/Alterar Constantes para Internacionalização" onclick="setAba('cadastro')"/></li>
		   </ul>
		  </li>
		 </ul>