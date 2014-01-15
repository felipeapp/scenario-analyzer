<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Visualiza��o do Calend�rio Acad�mico</h2>
	<h:form id="form">
		<table class="visualizacao" width="80%">		
			<caption>Dados do Calend�rio Acad�mico</caption>
			<tr>
				<th>Unidade Respons�vel:</th>
				<td>
					<h:outputText value="#{calendario.calendario.unidade.nome}"/>
				</td>
			</tr>
			<tr>
				<th>N�vel de Ensino:</th>
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
				<th>Conv�nio Acad�mico:</th>
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
				<td>${calendario.calendario.vigente ? 'Sim' : 'N�o'}</td>
			</tr>
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Ano - Per�odo de F�rias Vigente:</th>
					<td>
						<c:if test="${not empty calendario.calendario.anoFeriasVigente}">
							${calendario.calendario.anoFeriasVigente}-${calendario.calendario.periodoFeriasVigente}	
						</c:if>
						<c:if test="${empty calendario.calendario.anoFeriasVigente}">
							N�o Definido
						</c:if>
					</td>
				</tr>
				
				<tr>
					<th>Ano - Per�odo das Turmas na Solicita��o:</th>
					<td>
						<c:if test="${not empty calendario.calendario.anoNovasTurmas}">
					    ${calendario.calendario.anoNovasTurmas}-${calendario.calendario.periodoNovasTurmas}
					    </c:if>
					    <c:if test="${empty calendario.calendario.anoNovasTurmas}">
					    	N�o Definido
					    </c:if>
					</td>
				</tr>
			</c:if>
			<tr>
				<th width="50%">Per�odo Letivo:</th>
				<td >
					  <c:if test="${not empty calendario.calendario.inicioPeriodoLetivo}">
					    De <ufrn:format type="data" valor="${calendario.calendario.inicioPeriodoLetivo}" /> 
					    at� <ufrn:format type="data" valor="${calendario.calendario.fimPeriodoLetivo}" />
				      </c:if>
				      <c:if test="${empty calendario.calendario.inicioPeriodoLetivo}">
				      	N�o Definido
				      </c:if>
				</td>
			</tr>
			
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Per�odo de F�rias:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioFerias}"> 
					    	De <ufrn:format type="data" valor="${calendario.calendario.inicioFerias}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimFerias}" />
					    </c:if>
					    <c:if test="${empty calendario.calendario.inicioFerias}">
					    	N�o Definido
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
				<th>Consolida��o de Turmas:</th>
				<td>  
					<c:if test="${not empty calendario.calendario.inicioConsolidacaoTurma}">
				    	De <ufrn:format type="data" valor="${calendario.calendario.inicioConsolidacaoTurma}" /> 
				    	at� <ufrn:format type="data" valor="${calendario.calendario.fimConsolidacaoTurma}" />
				    </c:if>
				    <c:if test="${empty calendario.calendario.inicioConsolidacaoTurma}">
				    	N�o Definido
				    </c:if>
				</td>				
			</tr>
								
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Solicita��o de Cadastro de Turmas do Pr�ximo Per�odo:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioSolicitacaoTurma}">
						    De <ufrn:format type="data" valor="${calendario.calendario.inicioSolicitacaoTurma}" />
						    at� <ufrn:format type="data" valor="${calendario.calendario.fimSolicitacaoTurma}" />
					    </c:if>
					    <c:if test="${empty calendario.calendario.inicioSolicitacaoTurma}">
					    	N�o Definido
					    </c:if>					
					</td>
				</tr>
				<tr>
					<th>Cadastro de Turmas:</th>
					<td> 
						<c:if test="${not empty calendario.calendario.inicioCadastroTurma}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioCadastroTurma}" />
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimCadastroTurma}" />
						</c:if> 
						<c:if test="${empty calendario.calendario.inicioCadastroTurma}">
							N�o Definido
						</c:if>	   					
					</td>
				</tr>
			</c:if>								
			<tr>
				<th>Trancamento de Turmas:</th>
				<td> 
					<c:if test="${not empty calendario.calendario.inicioTrancamentoTurma }" > 
				    	De <ufrn:format type="data" valor="${calendario.calendario.inicioTrancamentoTurma}" /> 
				    	at� <ufrn:format type="data" valor="${calendario.calendario.fimTrancamentoTurma}" />
				    </c:if>
				    <c:if test="${empty calendario.calendario.inicioTrancamentoTurma }" >
				    	N�o Definido
				    </c:if>					
				</td>
			</tr>
			<c:if test="${calendario.obj.graduacao}">
				<tr>
					<th>Trancamento de Programa</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioTrancamentoPrograma }" > 
					    	De <ufrn:format type="data" valor="${calendario.calendario.inicioTrancamentoPrograma}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimTrancamentoPrograma}" />
					    </c:if>
					    <c:if test="${empty calendario.calendario.inicioTrancamentoPrograma }" >
					    	N�o Definido
					    </c:if>
					</td>
				</tr>
				<tr>
					<th>Trancamento de Programa a Posteriori</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioTrancamentoProgramaPosteriori }" > 
					    	De <ufrn:format type="data" valor="${calendario.calendario.inicioTrancamentoProgramaPosteriori}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimTrancamentoProgramaPosteriori}" />
					    </c:if>
					    <c:if test="${empty calendario.calendario.inicioTrancamentoProgramaPosteriori }" >
					    	N�o Definido
					    </c:if>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<td colspan="2" class="subFormulario">
						Datas para Turmas de F�rias
					</td>
				</tr>
				<tr>
					
						<th>Requerimento de Turmas de F�rias:</th>
						<td>  
							<c:if test="${not empty calendario.calendario.inicioRequerimentoTurmaFerias}">
								De <ufrn:format type="data" valor="${calendario.calendario.inicioRequerimentoTurmaFerias}" /> 
						    	at� <ufrn:format type="data" valor="${calendario.calendario.fimRequerimentoTurmaFerias}" />
							</c:if>
							<c:if test="${empty calendario.calendario.inicioRequerimentoTurmaFerias}">
								N�o Definido
							</c:if>
						    
						</td>
						<tr>
							<th>Solicita��o de Cadastro de Turmas de F�rias:</th>
							<td>  
								<c:if test="${not empty calendario.calendario.inicioSolicitacaoTurmaFerias }">
							    	De <ufrn:format type="data" valor="${calendario.calendario.inicioSolicitacaoTurmaFerias}" /> 
							    	at� <ufrn:format type="data" valor="${calendario.calendario.fimSolicitacaoTurmaFerias}" />
							    </c:if>
							    <c:if test="${empty calendario.calendario.inicioSolicitacaoTurmaFerias }">
							    	N�o Definido
							    </c:if>					    					
							</td>
						</tr>
						<tr>
							<th>Cadastro de Turmas de F�rias:</th>
							<td> 
								<c:if test="${not empty calendario.calendario.inicioCadastroTurmaFerias }">
									De <ufrn:format type="data" valor="${calendario.calendario.inicioCadastroTurmaFerias}" /> 
						    		at� <ufrn:format type="data" valor="${calendario.calendario.fimCadastroTurmaFerias}" />
								</c:if> 
								<c:if test="${empty calendario.calendario.inicioCadastroTurmaFerias }">
									N�o Definido
								</c:if>
							    
							    					
							</td>
						</tr>
						
						<tr>
							<th>Matr�cula em Turmas de F�rias:</th>
							<td> 
								<c:if test="${not empty calendario.calendario.inicioMatriculaTurmaFerias}">	
								    De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaTurmaFerias}" /> 
								    at� <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaTurmaFerias}" />
								</c:if> 
								<c:if test="${empty calendario.calendario.inicioMatriculaTurmaFerias}">
									N�o Definido
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
						    	at� <ufrn:format type="data" valor="${calendario.calendario.fimRequerimentoEnsinoIndiv}" />
							</c:if>
							<c:if test="${empty calendario.calendario.inicioRequerimentoEnsinoIndiv}">
								N�o Definido
							</c:if>		
						</td>
					</tr>
					<tr>
						<th>Solicita��o de turma de ensino individualizado:</th>
						<td>
							<c:if test="${not empty calendario.calendario.inicioSolicitacaoTurmaEnsinoIndiv}">
								De <ufrn:format type="data" valor="${calendario.calendario.inicioSolicitacaoTurmaEnsinoIndiv}" /> 
							    at� <ufrn:format type="data" valor="${calendario.calendario.fimSolicitacaoTurmaEnsinoIndiv}" />
							</c:if>
							<c:if test="${empty calendario.calendario.inicioSolicitacaoTurmaEnsinoIndiv}">
								N�o Definido
							</c:if>  
						</td>
					</tr>
					<tr>
						<th>Cadastro de turma de ensino individualizado:</th>
						<td>
							<c:if test="${not empty calendario.calendario.inicioCadastroTurmaEnsinoIndiv}">
								De <ufrn:format type="data" valor="${calendario.calendario.inicioCadastroTurmaEnsinoIndiv}" /> 
						    	at� <ufrn:format type="data" valor="${calendario.calendario.fimCadastroTurmaEnsinoIndiv}" />
							</c:if>
							<c:if test="${empty calendario.calendario.inicioCadastroTurmaEnsinoIndiv}">
								N�o Definido
							</c:if>		
						</td>
					</tr>
			</c:if>
			<tr>
				<td colspan="2" class="subFormulario">
					Datas para Per�odos de Matr�culas, Rematr�cula e Processamentos
				</td>
			</tr>
			<tr>
				<th>Matr�cula OnLine:</th>
				<td>
					<c:if test="${not empty calendario.calendario.inicioMatriculaOnline}">
						De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaOnline}" /> 
				    	at� <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaOnline}" />
					</c:if>
					<c:if test="${empty calendario.calendario.inicioMatriculaOnline}">
						N�o Definido
					</c:if>  
				</td>
			</tr>
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>Matr�cula de Alunos Ingressantes:</th>
					<td>  
						<c:if test="${not empty calendario.calendario.inicioMatriculaAlunoCadastrado}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaAlunoCadastrado}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaAlunoCadastrado}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioMatriculaAlunoCadastrado}">
							N�o Definido
						</c:if>			
					</td>
				</tr>
				<tr>
					<th>Matr�cula de Aluno Especial:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioMatriculaAlunoEspecial}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaAlunoEspecial}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaAlunoEspecial}" />
						</c:if>  
					    <c:if test="${empty calendario.calendario.inicioMatriculaAlunoEspecial}">
					    	N�o Definido
					    </c:if>		
					</td>
				</tr>
				<tr>
					<th>Matr�cula Extraordin�ria:</th>
					<td> 
						<c:if test="${not empty calendario.calendario.inicioMatriculaExtraordinaria}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioMatriculaExtraordinaria}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimMatriculaExtraordinaria}" />
						</c:if> 
					    <c:if test="${empty calendario.calendario.inicioMatriculaExtraordinaria}">
					    	N�o Defindo
					    </c:if>
					</td>
				</tr>
			</c:if>

			<c:if test="${!calendario.calendario.stricto}">
				<tr>
					<th>An�lise dos Coordenadores/Orientadores da Matr�cula:</th>
					<td>  
						<c:if test="${not empty calendario.calendario.inicioCoordenacaoAnaliseMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioCoordenacaoAnaliseMatricula}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimCoordenacaoAnaliseMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioCoordenacaoAnaliseMatricula}">
							N�o Definido
						</c:if>
					    
					</td>
				</tr>
			</c:if>
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>An�lise dos Discentes:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioDiscenteAnaliseMatricula }">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioDiscenteAnaliseMatricula}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimDiscenteAnaliseMatricula}" />
						</c:if>  
					    <c:if test="${empty calendario.calendario.inicioDiscenteAnaliseMatricula }">
					    	N�o Definido
					    </c:if>		
					</td>
				</tr>
				<tr>
					<th>Processamento de Matr�cula:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioProcessamentoMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioProcessamentoMatricula}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimProcessamentoMatricula}" />
						</c:if> 
						<c:if test="${empty calendario.calendario.inicioProcessamentoMatricula}">
							N�o Definido
						</c:if> 
					</td>
				</tr>
				<tr>
					<th>Ajustes das Matr�culas/Turmas:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioAjustesMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioAjustesMatricula}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimAjustesMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioAjustesMatricula}">
							N�o Definido
						</c:if>  
					    
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Rematr�cula:</th>
				<td>  
					<c:if test="${not empty calendario.calendario.inicioReMatricula}">
						De <ufrn:format type="data" valor="${calendario.calendario.inicioReMatricula}" /> 
				    	at� <ufrn:format type="data" valor="${calendario.calendario.fimReMatricula}" />
					</c:if>
					<c:if test="${empty calendario.calendario.inicioReMatricula}">
						N�o Definido
					</c:if>		
				</td>
			</tr>
			<c:if test="${!calendario.calendario.stricto}">
				<tr>
					<th>An�lise dos Coordenadores/Orientadores para Rematr�cula:</th>
					<td>  
						<c:if test="${not empty calendario.calendario.inicioCoordenacaoAnaliseReMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioCoordenacaoAnaliseReMatricula}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimCoordenacaoAnaliseReMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioCoordenacaoAnaliseReMatricula}">
							N�o Definido
						</c:if>			
					</td>
				</tr>
			</c:if>
			<c:if test="${calendario.calendario.graduacao}">
				<tr>
					<th>An�lise dos Discentes para Rematr�cula:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioDiscenteAnaliseReMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioDiscenteAnaliseReMatricula}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimDiscenteAnaliseReMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioDiscenteAnaliseReMatricula}">
							N�o Definido
						</c:if>  
					    						
					</td>
				</tr>
					<tr>
					<th>Processamento de Rematr�cula:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioProcessamentoReMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioProcessamentoReMatricula}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimProcessamentoReMatricula}" />
						</c:if>
						<c:if test="${empty calendario.calendario.inicioProcessamentoReMatricula}">
							N�o Definido
						</c:if>  
					</td>
				</tr>
				<tr>
					<th>Ajustes das Rematr�culas/Turmas:</th>
					<td>
						<c:if test="${not empty calendario.calendario.inicioAjustesReMatricula}">
							De <ufrn:format type="data" valor="${calendario.calendario.inicioAjustesReMatricula}" /> 
					    	at� <ufrn:format type="data" valor="${calendario.calendario.fimAjustesReMatricula}" />					
						</c:if>
						<c:if test="${empty calendario.calendario.inicioAjustesReMatricula}">
							N�o Definido
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
	