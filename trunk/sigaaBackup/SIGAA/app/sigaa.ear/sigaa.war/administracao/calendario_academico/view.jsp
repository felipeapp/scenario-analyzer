<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Visualização do Calendário Acadêmico</h2>
	<h:form id="form">
		<table class="visualizacao" width="80%">		
			<caption>Dados do Calendário Acadêmico</caption>
			<tr>
				<th>Unidade Responsável:</th>
				<td>
					<h:outputText value="#{calendario.calendario.unidade.nome}"/>
				</td>
			</tr>
			<tr>
				<th>Nível de Ensino:</th>
				<td>
					${calendario.calendario.nivelDescr}
				</td>
			</tr>	
			<tr>
				<th>Ano/Semestre:</th>
				<td>
					${calendario.calendario.ano}/${calendario.calendario.periodo}
				</td>
			</tr>
			<tr>
				<th>Modalidade de Ensino:</th>
				<td>
					${calendario.calendario.modalidade.descricao}
				</td>
			</tr>	
			<tr>
				<th>Convênio Acadêmico:</th>
				<td>
					${calendario.calendario.convenio.descricao}
				</td>
			</tr>	
			<c:if test="${!empty calendario.calendario.curso}">
				<tr>
					<th>Curso:</th>
					<td>
						${calendario.calendario.curso.descricao}
					</td>
				</tr>															
			</c:if>
			<tr>
				<th>
					Vigente:
				</th>
				<td>${calendario.calendario.vigente ? 'Sim' : 'Não'}</td>
			</tr>
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Ano - Período de Férias Vigente:</th>
					<td>
						<c:if test="${not empty calendario.calendario.anoFeriasVigente}">
							${calendario.calendario.anoFeriasVigente}-${calendario.calendario.periodoFeriasVigente}	
						</c:if>
						<c:if test="${empty calendario.calendario.anoFeriasVigente}">
							Não Definido
						</c:if>
					</td>
				</tr>
				
				<tr>
					<th>Ano - Período das Turmas na Solicitação:</th>
					<td>
						<c:if test="${not empty calendario.calendario.anoNovasTurmas}">
					    ${calendario.calendario.anoNovasTurmas}-${calendario.calendario.periodoNovasTurmas}
					    </c:if>
					    <c:if test="${empty calendario.calendario.anoNovasTurmas}">
					    	Não Definido
					    </c:if>
					</td>
				</tr>
			</c:if>
			<tr>
				<th width="50%">Período Letivo:</th>
				<td >
					  <c:if test="${not empty calendario.calendario.inicioPeriodoLetivo}">
					    De <ufrn:format type="data" valor="${calendario.calendario.inicioPeriodoLetivo}" /> 
					    até <ufrn:format type="data" valor="${calendario.calendario.fimPeriodoLetivo}" />
				      </c:if>
				      <c:if test="${empty calendario.calendario.inicioPeriodoLetivo}">
				      	Não Definido
				      </c:if>
				</td>
			</tr>
			
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Período de Férias:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioFerias}"> 
					    	De <ufrn:format type="data" valor="${calendario.calendario.inicioFerias}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimFerias}" />
					    </c:if>
					    <c:if test="${empty calendario.calendario.inicioFerias}">
					    	Não Definido
					    </c:if>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="2" class="subFormulario">
					Datas para Turmas de Regulares
				</td>
			</tr>
			
			<tr>
				<th>Consolidação de Turmas:</th>
				<td>  
					<c:if test="${not empty calendario.calendario.inicioConsolidacaoTurma}">
				    	De <ufrn:format type="data" valor="${calendario.calendario.inicioConsolidacaoTurma}" /> 
				    	até <ufrn:format type="data" valor="${calendario.calendario.fimConsolidacaoTurma}" />
				    </c:if>
				    <c:if test="${empty calendario.calendario.inicioConsolidacaoTurma}">
				    	Não Definido
				    </c:if>
				</td>				
			</tr>
								
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Solicitação de Cadastro de Turmas do Próximo Período:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioSolicitacaoTurma}">
						    De <ufrn:format type="data" valor="${calendario.calendario.inicioSolicitacaoTurma}" />
						    até <ufrn:format type="data" valor="${calendario.calendario.fimSolicitacaoTurma}" />
					    </c:if>
					    <c:if test="${empty calendario.calendario.inicioSolicitacaoTurma}">
					    	Não Definido
					    </c:if>					
					</td>
				</tr>
				<tr>
					<th>Cadastro de Turmas:</th>
					<td> 
						<c:if test="${not empty calendario.calendario.inicioCadastroTurma}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioCadastroTurma}" />
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimCadastroTurma}" />
						</c:if> 
						<c:if test="${empty calendario.calendario.inicioCadastroTurma}">
							Não Definido
						</c:if>	   					
					</td>
				</tr>
			</c:if>								
			<tr>
				<th>Trancamento de Turmas:</th>
				<td> 
					<c:if test="${not empty calendario.calendario.inicioTrancamentoTurma }" > 
				    	De <ufrn:format type="data" valor="${calendario.calendario.inicioTrancamentoTurma}" /> 
				    	até <ufrn:format type="data" valor="${calendario.calendario.fimTrancamentoTurma}" />
				    </c:if>
				    <c:if test="${empty calendario.calendario.inicioTrancamentoTurma }" >
				    	Não Definido
				    </c:if>					
				</td>
			</tr>
			<c:if test="${calendario.obj.graduacao}">
				<tr>
					<th>Trancamento de Programa</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioTrancamentoPrograma }" > 
					    	De <ufrn:format type="data" valor="${calendario.calendario.inicioTrancamentoPrograma}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimTrancamentoPrograma}" />
					    </c:if>
					    <c:if test="${empty calendario.calendario.inicioTrancamentoPrograma }" >
					    	Não Definido
					    </c:if>
					</td>
				</tr>
				<tr>
					<th>Trancamento de Programa a Posteriori</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioTrancamentoProgramaPosteriori }" > 
					    	De <ufrn:format type="data" valor="${calendario.calendario.inicioTrancamentoProgramaPosteriori}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimTrancamentoProgramaPosteriori}" />
					    </c:if>
					    <c:if test="${empty calendario.calendario.inicioTrancamentoProgramaPosteriori }" >
					    	Não Definido
					    </c:if>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<td colspan="2" class="subFormulario">
						Datas para Turmas de Férias
					</td>
				</tr>
				<tr>
					
						<th>Requerimento de Turmas de Férias:</th>
						<td>  
							<c:if test="${not empty calendario.calendario.inicioRequerimentoTurmaFerias}">
								De <ufrn:format type="data" valor="${calendario.calendario.inicioRequerimentoTurmaFerias}" /> 
						    	até <ufrn:format type="data" valor="${calendario.calendario.fimRequerimentoTurmaFerias}" />
							</c:if>
							<c:if test="${empty calendario.calendario.inicioRequerimentoTurmaFerias}">
								Não Definido
							</c:if>
						    
						</td>
						<tr>
							<th>Solicitação de Cadastro de Turmas de Férias:</th>
							<td>  
								<c:if test="${not empty calendario.calendario.inicioSolicitacaoTurmaFerias }">
							    	De <ufrn:format type="data" valor="${calendario.calendario.inicioSolicitacaoTurmaFerias}" /> 
							    	até <ufrn:format type="data" valor="${calendario.calendario.fimSolicitacaoTurmaFerias}" />
							    </c:if>
							    <c:if test="${empty calendario.calendario.inicioSolicitacaoTurmaFerias }">
							    	Não Definido
							    </c:if>					    					
							</td>
						</tr>
						<tr>
							<th>Cadastro de Turmas de Férias:</th>
							<td> 
								<c:if test="${not empty calendario.calendario.inicioCadastroTurmaFerias }">
									De <ufrn:format type="data" valor="${calendario.calendario.inicioCadastroTurmaFerias}" /> 
						    		até <ufrn:format type="data" valor="${calendario.calendario.fimCadastroTurmaFerias}" />
								</c:if> 
								<c:if test="${empty calendario.calendario.inicioCadastroTurmaFerias }">
									Não Definido
								</c:if>
							    
							    					
							</td>
						</tr>
						
						<tr>
							<th>Matrícula em Turmas de Férias:</th>
							<td> 
								<c:if test="${not empty calendario.calendario.inicioMatriculaTurmaFerias}">	
								    De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaTurmaFerias}" /> 
								    até <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaTurmaFerias}" />
								</c:if> 
								<c:if test="${empty calendario.calendario.inicioMatriculaTurmaFerias}">
									Não Definido
								</c:if>		
							</td>
						</tr>
						
				</tr>
			</c:if>
			 
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<td colspan="2" class="subFormulario">
						Datas para Turmas de Ensino Individualizado
					</td>
				</tr>
					<tr>
						<th>Requerimento de Ensino Individualizado:</th>
						<td>
							<c:if test="${not empty calendario.calendario.inicioRequerimentoEnsinoIndiv}">
								De <ufrn:format type="data" valor="${calendario.calendario.inicioRequerimentoEnsinoIndiv}" /> 
						    	até <ufrn:format type="data" valor="${calendario.calendario.fimRequerimentoEnsinoIndiv}" />
							</c:if>
							<c:if test="${empty calendario.calendario.inicioRequerimentoEnsinoIndiv}">
								Não Definido
							</c:if>		
						</td>
					</tr>
					<tr>
						<th>Solicitação de turma de ensino individualizado:</th>
						<td>
							<c:if test="${not empty calendario.calendario.inicioSolicitacaoTurmaEnsinoIndiv}">
								De <ufrn:format type="data" valor="${calendario.calendario.inicioSolicitacaoTurmaEnsinoIndiv}" /> 
							    até <ufrn:format type="data" valor="${calendario.calendario.fimSolicitacaoTurmaEnsinoIndiv}" />
							</c:if>
							<c:if test="${empty calendario.calendario.inicioSolicitacaoTurmaEnsinoIndiv}">
								Não Definido
							</c:if>  
						</td>
					</tr>
					<tr>
						<th>Cadastro de turma de ensino individualizado:</th>
						<td>
							<c:if test="${not empty calendario.calendario.inicioCadastroTurmaEnsinoIndiv}">
								De <ufrn:format type="data" valor="${calendario.calendario.inicioCadastroTurmaEnsinoIndiv}" /> 
						    	até <ufrn:format type="data" valor="${calendario.calendario.fimCadastroTurmaEnsinoIndiv}" />
							</c:if>
							<c:if test="${empty calendario.calendario.inicioCadastroTurmaEnsinoIndiv}">
								Não Definido
							</c:if>		
						</td>
					</tr>
			</c:if>
			<tr>
				<td colspan="2" class="subFormulario">
					Datas para Períodos de Matrículas, Rematrícula e Processamentos
				</td>
			</tr>
			<tr>
				<th>Matrícula OnLine:</th>
				<td>
					<c:if test="${not empty calendario.calendario.inicioMatriculaOnline}">
						De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaOnline}" /> 
				    	até <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaOnline}" />
					</c:if>
					<c:if test="${empty calendario.calendario.inicioMatriculaOnline}">
						Não Definido
					</c:if>  
				</td>
			</tr>
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Matrícula de Alunos Ingressantes:</th>
					<td>  
						<c:if test="${not empty calendario.calendario.inicioMatriculaAlunoCadastrado}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaAlunoCadastrado}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaAlunoCadastrado}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioMatriculaAlunoCadastrado}">
							Não Definido
						</c:if>			
					</td>
				</tr>
				<tr>
					<th>Matrícula de Aluno Especial:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioMatriculaAlunoEspecial}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaAlunoEspecial}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaAlunoEspecial}" />
						</c:if>  
					    <c:if test="${empty calendario.calendario.inicioMatriculaAlunoEspecial}">
					    	Não Definido
					    </c:if>		
					</td>
				</tr>
				<tr>
					<th>Matrícula Extraordinária:</th>
					<td> 
						<c:if test="${not empty calendario.calendario.inicioMatriculaExtraordinaria}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaExtraordinaria}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaExtraordinaria}" />
						</c:if> 
					    <c:if test="${empty calendario.calendario.inicioMatriculaExtraordinaria}">
					    	Não Defindo
					    </c:if>
					</td>
				</tr>
			</c:if>

			<c:if test="${!calendario.calendario.stricto}">
				<tr>
					<th>Análise dos Coordenadores/Orientadores da Matrícula:</th>
					<td>  
						<c:if test="${not empty calendario.calendario.inicioCoordenacaoAnaliseMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioCoordenacaoAnaliseMatricula}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimCoordenacaoAnaliseMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioCoordenacaoAnaliseMatricula}">
							Não Definido
						</c:if>
					    
					</td>
				</tr>
			</c:if>
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Análise dos Discentes:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioDiscenteAnaliseMatricula }">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioDiscenteAnaliseMatricula}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimDiscenteAnaliseMatricula}" />
						</c:if>  
					    <c:if test="${empty calendario.calendario.inicioDiscenteAnaliseMatricula }">
					    	Não Definido
					    </c:if>		
					</td>
				</tr>
				<tr>
					<th>Processamento de Matrícula:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioProcessamentoMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioProcessamentoMatricula}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimProcessamentoMatricula}" />
						</c:if> 
						<c:if test="${empty calendario.calendario.inicioProcessamentoMatricula}">
							Não Definido
						</c:if> 
					</td>
				</tr>
				<tr>
					<th>Ajustes das Matrículas/Turmas:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioAjustesMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioAjustesMatricula}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimAjustesMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioAjustesMatricula}">
							Não Definido
						</c:if>  
					    
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Rematrícula:</th>
				<td>  
					<c:if test="${not empty calendario.calendario.inicioReMatricula}">
						De <ufrn:format type="data" valor="${calendario.calendario.inicioReMatricula}" /> 
				    	até <ufrn:format type="data" valor="${calendario.calendario.fimReMatricula}" />
					</c:if>
					<c:if test="${empty calendario.calendario.inicioReMatricula}">
						Não Definido
					</c:if>		
				</td>
			</tr>
			<c:if test="${!calendario.calendario.stricto}">
				<tr>
					<th>Análise dos Coordenadores/Orientadores para Rematrícula:</th>
					<td>  
						<c:if test="${not empty calendario.calendario.inicioCoordenacaoAnaliseReMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioCoordenacaoAnaliseReMatricula}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimCoordenacaoAnaliseReMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioCoordenacaoAnaliseReMatricula}">
							Não Definido
						</c:if>			
					</td>
				</tr>
			</c:if>
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Análise dos Discentes para Rematrícula:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioDiscenteAnaliseReMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioDiscenteAnaliseReMatricula}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimDiscenteAnaliseReMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioDiscenteAnaliseReMatricula}">
							Não Definido
						</c:if>  
					    						
					</td>
				</tr>
					<tr>
					<th>Processamento de Rematrícula:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioProcessamentoReMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioProcessamentoReMatricula}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimProcessamentoReMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioProcessamentoReMatricula}">
							Não Definido
						</c:if>  
					</td>
				</tr>
				<tr>
					<th>Ajustes das Rematrículas/Turmas:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioAjustesReMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioAjustesReMatricula}" /> 
					    	até <ufrn:format type="data" valor="${calendario.calendario.fimAjustesReMatricula}" />					
						</c:if>
						<c:if test="${empty calendario.calendario.inicioAjustesReMatricula}">
							Não Definido
						</c:if>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${calendario.calendario.stricto}">
				<tr>
					<td colspan="2" class="subFormulario">
						Outros eventos
					</td>
				</tr>
				<tr>
					<td colspan="2">			
						<table width="100%" style="border-collapse: collapse;" class="listagem">
							<c:if test="${empty calendario.calendario.eventosExtra }">
								<tr><td colspan="3"><i>Nenhum evento extra cadastrado</i></td></tr>
							</c:if>
							<c:if test="${not empty calendario.calendario.eventosExtra }">
								<c:forEach items="${calendario.calendario.eventosExtra}" var="ev">
										<tr>
											<td width="22%"><ufrn:format type="data" valor="${ev.inicio}" /> - <ufrn:format type="data" valor="${ev.fim}" /></td>
											<td>${ev.descricao}</td>
										</tr>
								</c:forEach>
							</c:if>
						</table>
					</td>
				</tr>
			</c:if>
			
			
		</table>	
	</h:form>
	<br />
	<center>
	<a href="javascript: history.go(-1);"> << Voltar </a>
	</center>
	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	