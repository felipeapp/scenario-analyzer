		    <c:if test="${ acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo }">
			    <ul>
					<li> Avalia��o Institucional
			            <ul>
				    		<li><h:commandLink action="#{ avaliacaoInstitucional.iniciarDiscente }" value="Preencher a Avalia��o Institucional" /> </li>
			    			<li><h:commandLink action="#{ calendarioAvaliacaoInstitucionalBean.listar }" value="Avalia��o Institucional da Doc�ncia Assistida" /> </li>
			    			<li><h:commandLink action="#{ avaliacaoInstitucionalAnterior.reverAnteriorDiscente }" value="Rever a Avalia��o Anterior" /> </li>
			    			<li><h:commandLink action="#{ relatorioAvaliacaoMBean.iniciarConsultaPublica }" value="Consultar o Resultado da Avalia��o" /> </li>
		    				<li><h:commandLink action="#{ relatorioAvaliacaoMBean.iniciarObservacoesTurmasDiscente }" value="Observa��es dos Docentes Sobre Minhas Turmas" /> </li>
			            </ul>
					</li>
				</ul>
			</c:if>
			<ul>
				<li> Diversos
					<ul>
	   					<li><h:commandLink action="#{ relatorioNotasAluno.gerarRelatorio }" value="Minhas Notas"  /> </li>
	   					<li><h:commandLink action="#{ portalDiscente.atestadoMatricula }" value="Atestado de Matr�cula"  /> </li>
	   					<li><h:commandLink action="#{ portalDiscente.historico }" value="Consultar Hist�rico"  /> </li>
			   			
		   				<c:if test="${ portalDiscente.passivelEmissaoRelatorioIndices and !portalDiscente.modoReduzido }">
				   			<li><h:commandLink action="#{ indiceAcademicoMBean.selecionaDiscente }" value="Consultar �ndices Acad�micos" /> </li>
		   				</c:if>
		   				<c:if test="${portalDiscente.passivelEmissaoRelatorioIndices and portalDiscente.modoReduzido}">
		   					<li><h:commandLink value="Consultar �ndices Acad�micos" action="#{ indiceAcademicoMBean.relatorioInativo }"/>
		   				</c:if>
		   				<c:if test="${!usuario.discenteAtivo.tecnico && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar}">
			   				<li><h:commandLink action="#{ declaracaoVinculo.emitirDeclaracao }" value="Declara��o de V�nculo" /> </li>
		   				</c:if>
			   			<c:if test="${usuario.discenteAtivo.stricto}">
				   			<li><h:commandLink action="#{ termoPublicacaoTD.iniciarDiscente }" value="Termo de Autoriza��o para Publica��o de Teses e Disserta��es - TEDE"  /> </li>
			   			</c:if>
			   			<c:if test="${destrancarPrograma.discenteUsuario.trancado}">
				   			<li><h:commandLink action="#{ destrancarPrograma.iniciar }" value="Destrancar Curso" /> </li>
			   			</c:if>
			   			
			   			<c:if test="${usuario.discenteAtivo.graduacao}">
				   			<li><h:commandLink action="#{ selecaoSegundoCiclo.iniciar }" value="Sele��o de Cursos para Segundo Ciclo" /> </li>
			   			</c:if>
			
						<c:if test="${ usuario.discenteAtivo.discenteEad }">
							<li><h:commandLink value="Ver Fichas de Avalia��o" action="#{ fichaAvaliacaoEad.verFichaDiscente }" /></li>
						</c:if>
					</ul>
				</li>
			</ul>

			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar}">
			 	<ul>
				 	<li> Matricula On-Line
				 		<ul>
				 			<c:if test="${!usuario.discenteAtivo.stricto}">
						 		<li><h:commandLink value="Realizar Matr�cula" action="#{ matriculaGraduacao.telaInstrucoes}" /> </li>
				 			</c:if>
					 		<c:if test="${usuario.discenteAtivo.stricto}">
						 		<li><h:commandLink value="Realizar Matr�cula" action="#{ matriculaStrictoBean.iniciar}" /> </li>
					 		</c:if>
					 		<c:if test="${!usuario.discenteAtivo.stricto}">
						 		<li><h:commandLink value="Realizar Matr�cula em Turma de F�rias" action="#{ confirmacaoMatriculaFeriasBean.iniciar}" /> </li>
					 		</c:if>
					 		<li><h:commandLink value="Ver Comprovante de Matr�cula" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}"  /> </li>
							<li><h:commandLink value="Ver Orienta��es de Matr�cula" action="#{ matriculaGraduacao.acompanharSolicitacoes}"  /> </li>
					 		<li><h:commandLink value="Ver Resultado do Processamento" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}"  /> </li>
					 		<li><h:commandLink value="Meu Plano de Matr�culas" action="#{ planoMatriculaBean.gerar }" /> </li>
				 		</ul>
				 	</li>
				 </ul>
			 </c:if>

			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar && usuario.discenteAtivo.graduacao}">
			 	<ul>
				 	<li> Solicita��es de Ensino Individual
				 		<ul>
							<li><h:commandLink value="Solicitar Ensino Individual" action="#{ solicitacaoEnsinoIndividual.iniciarEnsinoIndividualizado }"  /> </li>
							<li><h:commandLink value="Visualizar Solicita��es Enviadas" action="#{ solicitacaoEnsinoIndividual.listarEnsinoIndividual }" /> </li>
							<li><h:commandLink value="Emitir Comprovante de Solicita��es" action="#{ solicitacaoEnsinoIndividual.emitirComprovanteEnsinoIndividual }" /> </li>
				 		</ul>
				 	</li>
				 </ul>
			</c:if>
			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar && usuario.discenteAtivo.graduacao}">
			 	<ul>
				 	<li> Solicita��es de Turma de F�rias
				 		<ul>
							<li><h:commandLink value="Solicitar Turma de F�rias" action="#{ solicitacaoEnsinoIndividual.iniciarFerias }"  /> </li>
							<li><h:commandLink value="Visualizar Solicita��es Enviadas" action="#{ solicitacaoEnsinoIndividual.listarFerias}" /> </li>
							<li><h:commandLink value="Emitir Comprovante de Solicita��es" action="#{ solicitacaoEnsinoIndividual.emitirComprovanteFerias }" /> </li>
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
							<li><h:commandLink value="Exibir Solicita��es de Trancamento" action="#{ trancamentoPrograma.exibirMinhasSolicitacoes }" /> </li>
				 		</ul>
				 	</li>
				 </ul>
			</c:if>
			<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar 
							&& usuario.discenteAtivo.graduacao}">
				<ul>
				 	<li> Reposi��o de Prova
				 		<ul>
							<li><h:commandLink value="Solicitar Reposi��o de Prova" action="#{ solicitacaoReposicaoProva.iniciar }" /> </li>
							<li><h:commandLink value="Exibir Solicita��es" action="#{ solicitacaoReposicaoProva.listarSolicitacoes }" /> </li>
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
				<li><h:commandLink value="Unidades Acad�micas" action="#{ unidade.popularBuscaGeral }" />
			</ul>
			<ul>
				<li><h:commandLink value="Consultar Calend�rio Acad�mico" action="#{calendario.iniciarBusca}" />
    		</ul>