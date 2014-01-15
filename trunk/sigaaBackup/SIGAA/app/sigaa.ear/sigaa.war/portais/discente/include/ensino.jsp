		    <c:if test="${ acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo }">
			    <ul>
					<li> Avaliação Institucional
			            <ul>
				    		<li><h:commandLink action="#{ avaliacaoInstitucional.iniciarDiscente }" value="Preencher a Avaliação Institucional" /> </li>
			    			<li><h:commandLink action="#{ calendarioAvaliacaoInstitucionalBean.listar }" value="Avaliação Institucional da Docência Assistida" /> </li>
			    			<li><h:commandLink action="#{ avaliacaoInstitucionalAnterior.reverAnteriorDiscente }" value="Rever a Avaliação Anterior" /> </li>
			    			<li><h:commandLink action="#{ relatorioAvaliacaoMBean.iniciarConsultaPublica }" value="Consultar o Resultado da Avaliação" /> </li>
		    				<li><h:commandLink action="#{ relatorioAvaliacaoMBean.iniciarObservacoesTurmasDiscente }" value="Observações dos Docentes Sobre Minhas Turmas" /> </li>
			            </ul>
					</li>
				</ul>
			</c:if>
			<ul>
				<li> Diversos
					<ul>
	   					<li><h:commandLink action="#{ relatorioNotasAluno.gerarRelatorio }" value="Minhas Notas"  /> </li>
	   					<li><h:commandLink action="#{ portalDiscente.atestadoMatricula }" value="Atestado de Matrícula"  /> </li>
	   					<li><h:commandLink action="#{ portalDiscente.historico }" value="Consultar Histórico"  /> </li>
			   			
		   				<c:if test="${ portalDiscente.passivelEmissaoRelatorioIndices and !portalDiscente.modoReduzido }">
				   			<li><h:commandLink action="#{ indiceAcademicoMBean.selecionaDiscente }" value="Consultar Índices Acadêmicos" /> </li>
		   				</c:if>
		   				<c:if test="${portalDiscente.passivelEmissaoRelatorioIndices and portalDiscente.modoReduzido}">
		   					<li><h:commandLink value="Consultar Índices Acadêmicos" action="#{ indiceAcademicoMBean.relatorioInativo }"/>
		   				</c:if>
		   				<c:if test="${!usuario.discenteAtivo.tecnico && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar}">
			   				<li><h:commandLink action="#{ declaracaoVinculo.emitirDeclaracao }" value="Declaração de Vínculo" /> </li>
		   				</c:if>
			   			<c:if test="${usuario.discenteAtivo.stricto}">
				   			<li><h:commandLink action="#{ termoPublicacaoTD.iniciarDiscente }" value="Termo de Autorização para Publicação de Teses e Dissertações - TEDE"  /> </li>
			   			</c:if>
			   			<c:if test="${destrancarPrograma.discenteUsuario.trancado}">
				   			<li><h:commandLink action="#{ destrancarPrograma.iniciar }" value="Destrancar Curso" /> </li>
			   			</c:if>
			   			
			   			<c:if test="${usuario.discenteAtivo.graduacao}">
				   			<li><h:commandLink action="#{ selecaoSegundoCiclo.iniciar }" value="Seleção de Cursos para Segundo Ciclo" /> </li>
			   			</c:if>
			
						<c:if test="${ usuario.discenteAtivo.discenteEad }">
							<li><h:commandLink value="Ver Fichas de Avaliação" action="#{ fichaAvaliacaoEad.verFichaDiscente }" /></li>
						</c:if>
					</ul>
				</li>
			</ul>

			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar}">
			 	<ul>
				 	<li> Matricula On-Line
				 		<ul>
				 			<c:if test="${!usuario.discenteAtivo.stricto}">
						 		<li><h:commandLink value="Realizar Matrícula" action="#{ matriculaGraduacao.telaInstrucoes}" /> </li>
				 			</c:if>
					 		<c:if test="${usuario.discenteAtivo.stricto}">
						 		<li><h:commandLink value="Realizar Matrícula" action="#{ matriculaStrictoBean.iniciar}" /> </li>
					 		</c:if>
					 		<c:if test="${!usuario.discenteAtivo.stricto}">
						 		<li><h:commandLink value="Realizar Matrícula em Turma de Férias" action="#{ confirmacaoMatriculaFeriasBean.iniciar}" /> </li>
					 		</c:if>
					 		<li><h:commandLink value="Ver Comprovante de Matrícula" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}"  /> </li>
							<li><h:commandLink value="Ver Orientações de Matrícula" action="#{ matriculaGraduacao.acompanharSolicitacoes}"  /> </li>
					 		<li><h:commandLink value="Ver Resultado do Processamento" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}"  /> </li>
					 		<li><h:commandLink value="Meu Plano de Matrículas" action="#{ planoMatriculaBean.gerar }" /> </li>
				 		</ul>
				 	</li>
				 </ul>
			 </c:if>

			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar && usuario.discenteAtivo.graduacao}">
			 	<ul>
				 	<li> Solicitações de Ensino Individual
				 		<ul>
							<li><h:commandLink value="Solicitar Ensino Individual" action="#{ solicitacaoEnsinoIndividual.iniciarEnsinoIndividualizado }"  /> </li>
							<li><h:commandLink value="Visualizar Solicitações Enviadas" action="#{ solicitacaoEnsinoIndividual.listarEnsinoIndividual }" /> </li>
							<li><h:commandLink value="Emitir Comprovante de Solicitações" action="#{ solicitacaoEnsinoIndividual.emitirComprovanteEnsinoIndividual }" /> </li>
				 		</ul>
				 	</li>
				 </ul>
			</c:if>
			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar && usuario.discenteAtivo.graduacao}">
			 	<ul>
				 	<li> Solicitações de Turma de Férias
				 		<ul>
							<li><h:commandLink value="Solicitar Turma de Férias" action="#{ solicitacaoEnsinoIndividual.iniciarFerias }"  /> </li>
							<li><h:commandLink value="Visualizar Solicitações Enviadas" action="#{ solicitacaoEnsinoIndividual.listarFerias}" /> </li>
							<li><h:commandLink value="Emitir Comprovante de Solicitações" action="#{ solicitacaoEnsinoIndividual.emitirComprovanteFerias }" /> </li>
				 		</ul>
				 	</li>
				 </ul>
			</c:if>
					
			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar 
							&& usuario.discenteAtivo.graduacao && usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
				<ul>
				 	<li> Trancamento de Componente Curricular
				 		<ul>
							<li><h:commandLink value="Trancar" action="#{ trancamentoMatricula.popularSolicitacao }" /> </li>
							<li><h:commandLink value="Exibir Andamento do Trancamento" action="#{ trancamentoMatricula.iniciarMeusTrancamentos }" /> </li>
				 		</ul>
				 	</li>
				</ul>
			</c:if>
			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar 
							&& usuario.discenteAtivo.graduacao && usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
				<ul>
				 	<li> Trancamento de Programa
				 		<ul>
							<li><h:commandLink value="Solicitar Trancamento" action="#{ trancamentoPrograma.iniciarSolicitacao }" /> </li>
							<li><h:commandLink value="Exibir Solicitações de Trancamento" action="#{ trancamentoPrograma.exibirMinhasSolicitacoes }" /> </li>
				 		</ul>
				 	</li>
				 </ul>
			</c:if>
			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar 
							&& usuario.discenteAtivo.graduacao}">
				<ul>
				 	<li> Reposição de Prova
				 		<ul>
							<li><h:commandLink value="Solicitar Reposição de Prova" action="#{ solicitacaoReposicaoProva.iniciar }" /> </li>
							<li><h:commandLink value="Exibir Solicitações" action="#{ solicitacaoReposicaoProva.listarSolicitacoes }" /> </li>
				 		</ul>
				 	</li>
				</ul>
			</c:if>
			<ul>				
				<li><h:commandLink value="Consultar Curso" action="#{ curso.popularBuscaGeral }"/></li>
			</ul>
			<ul>
				<li><h:commandLink value="Consultar Componente Curricular" action="#{ componenteCurricular.popularBuscaGeral }" /></li>
			</ul>
			
			<c:if test="${usuario.discenteAtivo.graduacao}">
				<ul>
					<li><h:commandLink value="Consultar Estrutura Curricular" action="#{ curriculo.popularBuscaGeral }"/></li>
				</ul>
			</c:if>
			<ul>
				<li><h:commandLink value="Consultar Turma" action="#{ buscaTurmaBean.popularBuscaGeral }" />
			</ul>
			<c:if test="${usuario.discenteAtivo.tecnico}">
				<ul>
					<li><h:commandLink value="Consultar Estrutura Curricular" actionListener="#{ menuDiscente.consultaEstruturaTec }"/>
				</ul>
			</c:if>
			<ul>
				<li><h:commandLink value="Unidades Acadêmicas" action="#{ unidade.popularBuscaGeral }" />
			</ul>
			<ul>
				<li><h:commandLink value="Consultar Calendário Acadêmico" action="#{calendario.iniciarBusca}" />
    		</ul>