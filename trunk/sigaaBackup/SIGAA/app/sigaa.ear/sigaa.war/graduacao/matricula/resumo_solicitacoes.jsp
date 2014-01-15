<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<f:view>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	
	<style>
		p { line-height: 1.25em; margin: 4px 0; }
	</style>
	
	<h:form>
	<center>
<c:if test="${!matriculaGraduacao.discenteLogado}">
	<div class="infoAltRem">
		${matriculaGraduacao.discente.matriculaNome}
		<c:if test="${matriculaGraduacao.matriculaEAD}">
			<br>
			${matriculaGraduacao.discente.polo.descricao}
		</c:if>
	</div>
</c:if>	<table width="100%">
	<tr>
		<c:if test="${matriculaGraduacao.solicitacaoConfirmada}">
			<td style="font-size: 1.4em; font-weight: bold; text-align: center; margin: 5px; font-variant: small-caps;">
				Solicita��o de Matr�cula N� ${matriculaGraduacao.numeroSolicitacaoFormatado }
			</td>
	
			<td width="20%">
			<table>
				<tr><td align="center"><h:commandLink title="Imprimir Comprovante"  target="_blank"
				 action="#{matriculaGraduacao.verComprovanteSolicitacoes}"  id="comprovanteImprimir">
				 <h:graphicImage url="/img/printer_ok.png" />
				 </h:commandLink>
				 </td></tr>
				 <tr><td style="font-size: 1.3em; text-align: center;">
				 <h:commandLink title="Imprimir Comprovante"  target="_blank" value="Imprimir Comprovante"
				 action="#{matriculaGraduacao.verComprovanteSolicitacoes}"  id="impComprovant"/>
				 </td></tr>
			</table>
			</td>
		</c:if>
		<td width="20%">
		<table>
			<tr><td align="center"><h:commandLink title="Portal Discente"
			 action="#{matriculaGraduacao.cancelar}" id="cancelOperation" >
			 <h:graphicImage url="/img/graduacao/matriculas/discente.gif" />
			 </h:commandLink>
			 </td></tr>
			 <tr><td style=" text-align: center;">
			 <h:commandLink title="Voltar ao Portal Discente"  value="Voltar ao Portal Discente"
			 	action="#{matriculaGraduacao.cancelar}" rendered="#{acesso.discente}" id="voltarAPortalDiscente"/>
			 <h:commandLink title="Outro Discente"  value="Outro Discente"
			 	action="#{matriculaGraduacao.iniciar}" rendered="#{!acesso.discente && !matriculaGraduacao.matriculaTurmasNaoOnline}" id="outroDiscente"/>
			 <h:commandLink title="Outro Discente"  value="Outro Discente"
			 	action="#{matriculaGraduacao.iniciarMatriculaTurmasNaoMatriculaveis}" rendered="#{!acesso.discente && matriculaGraduacao.matriculaTurmasNaoOnline}" id="outroDiscente2"/>
			 </td></tr>
		</table>
		</td>
	</tr>
	</table>
	<br>
	
	<c:if test="${matriculaGraduacao.discente.graduacao}">
		<table  class="subFormulario" style="width: 100%">
			<caption style="text-align: center;">Informa��o Importante</caption>
			<tr>
				<td width="10%" style="text-align: center">
					<html:img page="/img/warning.gif"/> 
				</td>
				<td valign="top" style="text-align: justify">
										
					<c:if test="${!matriculaGraduacao.discente.regular}">
						<b>Aten��o!</b> 
						O per�odo de matr�cula on-line � de
						<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaAlunoEspecial}"/>
						a <ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaAlunoEspecial}"/>.
						Durante esse per�odo voc� poder� efetuar a matr�cula nos componentes curriculares desejados, de acordo com a oferta de turmas.
						<p>
							Os chefes de departamento (ou coordenadores de curso, no caso de alunos em mobilidade estudantil ou em complementa��o de estudos)
							analisar�o suas solicita��es de matr�cula e poder�o aprovar ou indefer�-las, de acordo com seu plano de estudos.
						</p>
						<p>
							Caso aprovadas, ap�s a data final desse per�odo voc� ser� automaticamente matriculado "EM ESPERA"
							em todas as turmas escolhidas e submetidas. <b style="color: #922;">At� a data final do per�odo de matr�cula on-line,
							� permitida a altera��o das turmas selecionadas, adicionando outras turmas ou removendo aquelas previamente escolhidas.</b>
							O deferimento das suas matr�culas est� sujeito ao processamento de matr�cula, que classificar�
							os alunos em cada turma com base nos crit�rios definidos no	regulamento dos cursos de gradua��o da ${ configSistema['siglaInstituicao'] }.
						</p>
					</c:if>
					
					
					<c:if test="${!matriculaGraduacao.tutorEad && matriculaGraduacao.discente.regular}">
						<b>Aten��o!</b> 
						O per�odo de matr�cula on-line � de 
						<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaOnline}"/>
						a <ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaOnline}"/>.
						<p>
							Ap�s a data final desse per�odo o discente ser� automaticamente matriculado "EM ESPERA"
							em todas as turmas escolhidas e exibidas abaixo. At� a data final,
							� permitido ao discente escolher outras turmas ou remover as j� selecionadas.
						</p>
						<p>
							O deferimento das suas matr�culas est� sujeito ao processamento de matr�cula que classificar� os alunos
							em cada turma com base nos crit�rios definidos no regulamento dos cursos de gradua��o da ${ configSistema['siglaInstituicao'] }.
						</p>
						<p>
							Os orientadores acad�micos e coordenadores do seu curso poder�o fazer observa��es sobre as turmas
							escolhidas at� 
							<strong>
								<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimCoordenacaoAnaliseMatricula}"/>.
							</strong>
						</p>
						<p>
							� importante lembrar que essas observa��es t�m o objetivo somente de orientar os discentes, nenhuma orienta��o
							� capaz de cancelar ou excluir uma matr�cula escolhida pelo discente. Apenas o pr�prio discente pode excluir
							as matr�culas nas turmas escolhidas at� o prazo final da matr�cula on-line.
						</p>
					</c:if>
					
					<c:if test="${matriculaGraduacao.tutorEad}">
						<b>Aten��o!</b> 
						O per�odo de matr�cula on-line � de 
						<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaOnline}"/>
						a <ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaOnline}"/>.
						
						Durante este per�odo o tutor poder� efetuar quaisquer modifica��es nas matr�culas de seus alunos.
						<br />
 						Ap�s este per�odo apenas os coordenadores de Curso poder�o efetuar modifica��es. 
 						O per�odo de valida��o das matr�culas pelos coordenadores de curso � de 
 						<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioCoordenacaoAnaliseMatricula}"/> 
 						a <ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimCoordenacaoAnaliseMatricula}"/>.
					</c:if>
					<br>
				</td>
			</tr>
		</table>
	</c:if>
	
	<br/>
	</center>
	<c:if test="${not empty matriculaGraduacao.turmas or not empty matriculaGraduacao.turmasJaMatriculadas}">
	<table class="listagem" style="width: 100%">
		<caption>Turmas selecionadas</caption>
		<thead>
			<tr>
				<td width="2%">Turma</td>
				<td>Componente Curricular</td>
				<td width="10%">Local</td>
				<td width="30%">Hor�rio</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{matriculaGraduacao.turmas}" var="turma" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td align="center">
					<a href="javascript:noop();" onclick="PainelTurma.show(${turma.id})" title="Ver Detalhes dessa turma">
					${turma.codigo}
					</a>
					</td>
					<td>
					<a href="javascript:noop();" onclick="PainelComponente.show(${turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
					${turma.disciplina.detalhes.codigo}
					</a> - ${turma.disciplina.nome} (${turma.disciplina.crTotal} crs.)
					</td>
					<td>${turma.local}</td>
					<td>${turma.descricaoHorario}</td>
				</tr>
			</c:forEach>
			<c:forEach items="#{matriculaGraduacao.turmasJaMatriculadas}" var="turmaJaMatriculada" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td align="center">
					<a href="javascript:noop();" onclick="PainelTurma.show(${turmaJaMatriculada.id})" title="Ver Detalhes dessa turma">
					${turmaJaMatriculada.codigo}
					</a>
					</td>
					<td>
					<a href="javascript:noop();" onclick="PainelComponente.show(${turmaJaMatriculada.disciplina.id})" title="Ver Detalhes do Componente Curricular">
					${turmaJaMatriculada.disciplina.detalhes.codigo}
					</a> - ${turmaJaMatriculada.disciplina.nome} (${turmaJaMatriculada.disciplina.crTotal} crs.)
					</td>
					<td>${turmaJaMatriculada.local}</td>
					<td>${turmaJaMatriculada.descricaoHorario}</td>
				</tr>
			</c:forEach>
			<c:if test="${!matriculaGraduacao.solicitacaoConfirmada}">
			<tr>
				<td colspan="4">
					<div align="center">
						<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
					</div>
				</td>	
			</tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4" align="center">
					<c:if test="${!matriculaGraduacao.solicitacaoConfirmada}">
						<h:commandButton id="btnConfirmar" value="Confirmar Matr�culas" action="#{matriculaGraduacao.confirmarSubmissaoSolicitacao}"/>
						<h:commandButton id="btnCancelar" value="Cancelar" action="#{matriculaGraduacao.telaSelecaoTurmas}"/>
					</c:if>
				</td>
			</tr>
		</tfoot>			
	</table>
	</c:if>
	<br>

</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>