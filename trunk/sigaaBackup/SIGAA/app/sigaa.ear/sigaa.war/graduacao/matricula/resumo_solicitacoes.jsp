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
				Solicitação de Matrícula N° ${matriculaGraduacao.numeroSolicitacaoFormatado }
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
			<caption style="text-align: center;">Informação Importante</caption>
			<tr>
				<td width="10%" style="text-align: center">
					<html:img page="/img/warning.gif"/> 
				</td>
				<td valign="top" style="text-align: justify">
										
					<c:if test="${!matriculaGraduacao.discente.regular}">
						<b>Atenção!</b> 
						O período de matrícula on-line é de
						<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaAlunoEspecial}"/>
						a <ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaAlunoEspecial}"/>.
						Durante esse período você poderá efetuar a matrícula nos componentes curriculares desejados, de acordo com a oferta de turmas.
						<p>
							Os chefes de departamento (ou coordenadores de curso, no caso de alunos em mobilidade estudantil ou em complementação de estudos)
							analisarão suas solicitações de matrícula e poderão aprovar ou indeferí-las, de acordo com seu plano de estudos.
						</p>
						<p>
							Caso aprovadas, após a data final desse período você será automaticamente matriculado "EM ESPERA"
							em todas as turmas escolhidas e submetidas. <b style="color: #922;">Até a data final do período de matrícula on-line,
							é permitida a alteração das turmas selecionadas, adicionando outras turmas ou removendo aquelas previamente escolhidas.</b>
							O deferimento das suas matrículas está sujeito ao processamento de matrícula, que classificará
							os alunos em cada turma com base nos critérios definidos no	regulamento dos cursos de graduação da ${ configSistema['siglaInstituicao'] }.
						</p>
					</c:if>
					
					
					<c:if test="${!matriculaGraduacao.tutorEad && matriculaGraduacao.discente.regular}">
						<b>Atenção!</b> 
						O período de matrícula on-line é de 
						<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaOnline}"/>
						a <ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaOnline}"/>.
						<p>
							Após a data final desse período o discente será automaticamente matriculado "EM ESPERA"
							em todas as turmas escolhidas e exibidas abaixo. Até a data final,
							é permitido ao discente escolher outras turmas ou remover as já selecionadas.
						</p>
						<p>
							O deferimento das suas matrículas está sujeito ao processamento de matrícula que classificará os alunos
							em cada turma com base nos critérios definidos no regulamento dos cursos de graduação da ${ configSistema['siglaInstituicao'] }.
						</p>
						<p>
							Os orientadores acadêmicos e coordenadores do seu curso poderão fazer observações sobre as turmas
							escolhidas até 
							<strong>
								<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimCoordenacaoAnaliseMatricula}"/>.
							</strong>
						</p>
						<p>
							É importante lembrar que essas observações têm o objetivo somente de orientar os discentes, nenhuma orientação
							é capaz de cancelar ou excluir uma matrícula escolhida pelo discente. Apenas o próprio discente pode excluir
							as matrículas nas turmas escolhidas até o prazo final da matrícula on-line.
						</p>
					</c:if>
					
					<c:if test="${matriculaGraduacao.tutorEad}">
						<b>Atenção!</b> 
						O período de matrícula on-line é de 
						<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaOnline}"/>
						a <ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaOnline}"/>.
						
						Durante este período o tutor poderá efetuar quaisquer modificações nas matrículas de seus alunos.
						<br />
 						Após este período apenas os coordenadores de Curso poderão efetuar modificações. 
 						O período de validação das matrículas pelos coordenadores de curso é de 
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
				<td width="30%">Horário</td>
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
						<h:commandButton id="btnConfirmar" value="Confirmar Matrículas" action="#{matriculaGraduacao.confirmarSubmissaoSolicitacao}"/>
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