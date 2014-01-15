<%-- MENU DE OPÇÕES PARA O ADMINISTRAÇÃO --%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<c:if test="${acesso.administracao}">

<div id="menu-dropdown">
<div class="wrapper"><h:form>
		<input type="hidden" name="jscook_action" />
	<t:jscookMenu layout="hbr" theme="ThemeOffice"
		styleLocation="/css/jscookmenu">
		<t:navigationMenuItem itemLabel="Cadastro" id="cadastro">

			<!-- Ensino -->
			<t:navigationMenuItem itemLabel="Ensino">

				<t:navigationMenuItem itemLabel="Área SESU"
					action="#{areaSesu.listar}" />

				<t:navigationMenuItem itemLabel="Campus da IES"
					action="#{campusIes.listar}"
					 />
					 
				<t:navigationMenuItem itemLabel="Cargo Acadêmico"
					action="#{cargoAcademico.listar}"
					 />

				<t:navigationMenuItem itemLabel="Estado de Atividade"
					action="#{estadoAtividade.listar}"
					 />

				<t:navigationMenuItem itemLabel="Forma de Ingresso"
					action="#{formaIngresso.listar}"
					 />

				<t:navigationMenuItem itemLabel="Grau Acadêmico"
					action="#{grauAcademico.listar}"
					 />

				<t:navigationMenuItem itemLabel="Grau de Formação"
					action="#{grauFormacao.listar}"
					 />

				<t:navigationMenuItem itemLabel="Modalidade de Educação"
					action="#{modalidadeEducacao.listar}"
					 />

				<t:navigationMenuItem itemLabel="Natureza do Curso"
					action="#{naturezaCurso.listar}"
					 />

				<t:navigationMenuItem itemLabel="Processo Seletivo"
					action="#{processoSeletivo.listar}"
					 />

				<t:navigationMenuItem itemLabel="Situação de Curso Hábil"
					action="#{situacaoCursoHabil.listar}"
					 />

				<t:navigationMenuItem itemLabel="Situação de Diploma"
					action="#{situacaoDiploma.listar}"
					 />

				<t:navigationMenuItem itemLabel="Situação de Matrícula"
					action="#{situacaoMatricula.listar}"
				 />

				<t:navigationMenuItem itemLabel="Tipo de Atividade"
					action="#{tipoAtividade.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Avaliação"
					action="#{tipoAvaliacao.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Atividade Complementar"
					action="#{tipoAtividadeComplementar.listar}"
			     />

				<t:navigationMenuItem itemLabel="Tipo de Entrada"
					action="#{tipoEntrada.listar}"
				 />

				<t:navigationMenuItem itemLabel="Tipo de Procedência do Aluno"
					action="#{tipoProcedenciaAluno.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Rede de Ensino"
					action="#{tipoRedeEnsino.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Regime do Aluno"
					action="#{tipoRegimeAluno.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Regime Letivo"
					action="#{tipoRegimeLetivo.listar}"
					 />

			</t:navigationMenuItem>
			<!-- Fim de Ensino -->

			<!-- Pesquisa -->

			<t:navigationMenuItem itemLabel="Pesquisa">

				<t:navigationMenuItem itemLabel="Area de Conhecimento da Unesco"
					action="#{areaConhecimentoUnesco.listar}"
					 />

				<t:navigationMenuItem
					itemLabel="Classificação da Entidade Financiadora"
					action="#{classificacaoFinanciadora.listar}"
					 />

				<t:navigationMenuItem itemLabel="Entidade Financiadora"
					action="#{entidadeFinanciadora.listar}"
					 />

				<t:navigationMenuItem itemLabel="Entidade Financiadora de Estágio"
					action="#{entidadeFinanciadoraEstagio.listar}"
					 />

				<t:navigationMenuItem itemLabel="Entidade Financiadora Outra"
					action="#{entidadeFinanciadoraOutra.listar}"
					 />

				<t:navigationMenuItem itemLabel="Grupo da Entidade Financiadora"
					action="#{grupoEntidadeFinanciadora.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Campus da Unidade"
					action="#{tipoCampusUnidade.listar}"
				 />

				<t:navigationMenuItem itemLabel="Tipo de Orientação do Discente"
					action="#{tipoOrientacaoDiscente.listar}"
				 />

				<t:navigationMenuItem itemLabel="Tipo de Público Alvo"
					action="#{tipoPublicoAlvo.listar}"
				 />

			</t:navigationMenuItem>
			<!-- Fim de Pesquisa -->


			<!-- Outros -->
			<t:navigationMenuItem itemLabel="Outros">

				<t:navigationMenuItem itemLabel="Estado Civil"
					action="#{estadoCivil.listar}"
					 />

				<t:navigationMenuItem itemLabel="Instituições de Ensino"
					action="#{instituicoesEnsino.listar}"
					 />

				<t:navigationMenuItem itemLabel="Município"
					action="#{municipio.listar}"/>

				<t:navigationMenuItem itemLabel="País"
					action="#{pais.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Documento Legal"
					action="#{tipoDocumentoLegal.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Etnia"
					action="#{tipoEtnia.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Logradouro"
					action="#{tipoLogradouro.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Necessidade Especial"
					action="#{tipoNecessidadeEspecial.listar}"
					 />

				<t:navigationMenuItem itemLabel="Tipo de Raça"
					action="#{tipoRaca.listar}"
					 />

				<t:navigationMenuItem
					itemLabel="Tipo de Veiculação de Ensino à Distância"
					action="#{tipoVeiculacaoEad.listar}"
					 />

				<t:navigationMenuItem itemLabel="Turno"
					action="#{turno.listar}"
					 />

				<t:navigationMenuItem itemLabel="Unidade Federativa"
					action="#{unidadeFederativa.listar}"
					 />

			</t:navigationMenuItem>
			<!-- Fim de Outros -->

			<!-- Unidades -->
			<t:navigationMenuItem itemLabel="Unidade"
				action="#{unidade.listar}"
			 />
			<!-- Fim de Unidades -->

			<!-- Servidores -->
			<t:navigationMenuItem itemLabel="Servidor"
				actionListener="#{menu.redirecionar}"
				itemValue="/administracao/cadastro/Servidor/lista.jsf" />

			<!-- Fim de Servidores -->

		</t:navigationMenuItem>

		<!-- ELEICOES -->
		<t:navigationMenuItem itemLabel="Eleição">

				<t:navigationMenuItem itemLabel="Eleição" action="#{eleicao.listar}" />

				<t:navigationMenuItem itemLabel="Candidato" action="#{candidato.listar}" />
		</t:navigationMenuItem>
		<!-- FIM de ELEICOES -->

		<t:navigationMenuItem itemLabel="Administração" id="admin">

			<t:navigationMenuItem itemLabel="Calendário Acadêmico"
				action="#{calendario.iniciar}" />

			<t:navigationMenuItem itemLabel="Horário de Turmas"
				action="#{horario.iniciar}" />

			<t:navigationMenuItem split="true"/>

			<t:navigationMenuItem itemLabel="Processamento de Matrícula">
				<t:navigationMenuItem itemLabel="Pré-Processamento"
					action="#{preProcessamentoMatricula.iniciar}" />
	
				<t:navigationMenuItem itemLabel="Processamento de Matrículas"
					action="#{processamentoMatricula.iniciar}" />
	
				<t:navigationMenuItem itemLabel="Resultado do Processamento de Matrículas"
					action="#{resultadoProcessamentoMatriculaBean.iniciar}" />
			</t:navigationMenuItem>

			<t:navigationMenuItem split="true"/>

			<t:navigationMenuItem itemLabel="Resultado da Avaliação Institucional"
				action="#{resultadoAvaliacaoInstitucionalMBean.iniciar}" />
			<t:navigationMenuItem itemLabel="Trancamentos da Avaliação Institucional"
				action="#{motivosTrancamentoMBean.iniciar}" />

			<t:navigationMenuItem itemLabel="Recalcular Discentes" actionListener="#{menu.redirecionar}" itemValue="/administracao/recalculo_discentes.jsf"/>
			<t:navigationMenuItem itemLabel="Recalcular Estruturas Curriculares" actionListener="#{menu.redirecionar}" itemValue="/administracao/recalculo_curriculos.jsf"/>
			<t:navigationMenuItem itemLabel="Resetar Última Atualização de Totais" actionListener="#{menu.redirecionar}" itemValue="/administracao/resetar_calculo_discentes.jsf"/>

			<t:navigationMenuItem itemLabel="Histórico em formato Excel" action="#{ historico.buscarDiscenteExcel }"/>

			<t:navigationMenuItem itemLabel="Parâmetros do Sistema"
				action="#{parametros.iniciar}" />
			<t:navigationMenuItem itemLabel="Docentes Externos">
				<t:navigationMenuItem itemLabel="Cadastrar"
					action="#{docenteExterno.popular}" />
				<t:navigationMenuItem itemLabel="Alterar/Remover"
					actionListener="#{menu.redirecionar}" itemValue="/administracao/docente_externo/lista.jsf" />
			</t:navigationMenuItem>



			<t:navigationMenuItem itemLabel="Arquivo STTU">
				<t:navigationMenuItem itemLabel="Médio">
					<t:navigationMenuItem itemLabel="Lista de Alunos" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=M"/>
					<t:navigationMenuItem itemLabel="Log de Erros" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=M&log=true"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Técnico">
					<t:navigationMenuItem itemLabel="Escola de Música">
						<t:navigationMenuItem itemLabel="Lista de Alunos" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=T&unidade=284"/>
						<t:navigationMenuItem itemLabel="Log de Erros" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=T&log=true&unidade=284"/>
					</t:navigationMenuItem>
					<t:navigationMenuItem itemLabel="Escola de Enfermagem">
						<t:navigationMenuItem itemLabel="Lista de Alunos" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=T&unidade=205"/>
						<t:navigationMenuItem itemLabel="Log de Erros" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=T&log=true&unidade=205"/>
					</t:navigationMenuItem>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Graduação">
					<t:navigationMenuItem itemLabel="Lista de Alunos" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=G"/>
					<t:navigationMenuItem itemLabel="Log de Erros" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=G&log=true"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Lato Sensu">
					<t:navigationMenuItem itemLabel="Lista de Alunos" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=L"/>
					<t:navigationMenuItem itemLabel="Log de Erros" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=L&log=true"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Stricto Sensu">
					<t:navigationMenuItem itemLabel="Lista de Alunos" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=S"/>
					<t:navigationMenuItem itemLabel="Log de Erros" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=S&log=true"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Médicos Residentes">
					<t:navigationMenuItem itemLabel="Lista de Alunos" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=R"/>
					<t:navigationMenuItem itemLabel="Log de Erros" actionListener="#{menu.redirecionar}" itemValue="/arquivoSttu?nivel=R&log=true"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Consulta personalizada" actionListener="#{menu.redirecionar}" itemValue="/administracao/sttu/consulta.jsp" />
			</t:navigationMenuItem>

			<t:navigationMenuItem itemLabel="Migrar Campos MARC21" action="#{migracaoMarc.executar}" />
			<t:navigationMenuItem itemLabel="Migrar Campos MARC21 de Autoridades" action="#{migracaoMarcAutoridades.migrarAutoridades}" />
			
		</t:navigationMenuItem>


		<t:navigationMenuItem itemLabel="Usuário" id="usuario">

			<t:navigationMenuItem itemLabel="Logar como Outro Usuário"
				actionListener="#{menu.redirecionar}"
				itemValue="/administracao/usuario/logar_como.jsf" />

			<t:navigationMenuItem itemLabel="Usuários Logados" actionListener="#{menu.redirecionar}"
			itemValue="/administracao/usuario/logados.jsf"/>

			<t:navigationMenuItem itemLabel="Ver Registro de Entrada" actionListener="#{menu.redirecionar}"
			itemValue="/administracao/usuario/ver_registro.jsf"/>


		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Portais" id="portais">
			<t:navigationMenuItem itemLabel="Portal do Prodocente"
				actionListener="#{menu.redirecionar}" itemValue="/verMenuProdocente.do" />
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Gerenciamento" id="gerenciamento">
			<t:navigationMenuItem itemLabel="SQL Console"
				action="#{sqlConsole.entrar}" />
			<t:navigationMenuItem itemLabel="Desempenho Consultas"
				actionListener="#{menu.redirecionar}" itemValue="/administracao/desempenhoConsultas.jsp" />
		</t:navigationMenuItem>

	</t:jscookMenu>
</h:form>
</div>
</div>

</c:if>