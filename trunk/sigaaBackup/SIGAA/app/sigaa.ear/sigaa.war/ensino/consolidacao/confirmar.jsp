<% request.setAttribute("res1024","true"); %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2>Consolidação de Turma</h2>

<h:form id="confirmar">
<input type="hidden" name="gestor" value="${ param['gestor'] }"/>
<h3>Turma: ${ consolidarTurma.turma.descricaoSemDocente }</h3>
	<hr/>
	<br/>
		<table class="listagem" style="width: 100%">
		<caption>Alunos matriculados</caption>
		</table>
		
		<c:set var="codigoSubturma" value="" />
		
		<c:forEach var="matricula" items="${ consolidarTurma.turma.matriculasDisciplina }" varStatus="i">
		
			<c:if test="${codigoSubturma != matricula.codigoSubturma}">
				<c:if test="${consolidarTurma.turma.subTurma || consolidarTurma.turma.agrupadora}">
					<h:outputText value="</table>" escape="false" />
					<div style="padding-top:10px;font-weight:bold;font-size:11pt;">${ matricula.codigoSubturma }</div>
				</c:if>
				
				<h:outputText value="<table class='listagem' style='width: 100%'>" escape="false" />
					<thead>
						<tr>
							<th width="10%" style="text-align: center">Matrícula</th><th>Nome</th>
							<c:if test="${ consolidarTurma.nota }">
								<c:forEach var="u" items="${ consolidarTurma.notas }">
									<th colspan="${ u.numeroAvaliacoes }" style="text-align: center">Unid. ${ u.unidade }</th>
								</c:forEach>
								<c:if test="${ consolidarTurma.ead && consolidarTurma.metodologiaAvaliacao.permiteTutor && !consolidarTurma.estagioEad && !consolidarTurma.lato && !consolidarTurma.turmaFeriasEad}">
									<th width="5%" style="text-align: center">Tutor</th>
								</c:if>			
								<c:if test="${ consolidarTurma.nota  && !consolidarTurma.lato && (!consolidarTurma.ead || consolidarTurma.estagioEad || consolidarTurma.turmaFeriasEad || (consolidarTurma.ead && (consolidarTurma.duasNotas || consolidarTurma.umaNota))) }">
									<th width="5%">Recuperação</th>
								</c:if>
							</c:if>
							<th width="5%">Resultado</th><c:if test="${ !consolidarTurma.ead }"><th width="5%">Faltas</th></c:if><th style="text-align: center;">Sit.</th>
						</tr>
						
						<c:if test="${ consolidarTurma.avaliacao }">
							<tr><th></th><th></th>
							<c:forEach var="u" items="${ consolidarTurma.notas }">
								<c:forEach var="avaliacao" items="${ u.avaliacoes }">
									<th style="text-align: center">${ avaliacao.abreviacao }</th>
								</c:forEach>
								<th style="text-align: center">N</th>
							</c:forEach>
							<th></th><th></th><th></th><th></th></tr>
						</c:if>
					</thead>
			</c:if>
				
			<c:set var="codigoSubturma" value="${ matricula.codigoSubturma }" />
		
			<c:if test="${ !matricula.emRecuperacao || !consolidarTurma.parcial }">
			<tr class="${ i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${ matricula.discente.matricula }</td><td>${ matricula.discente.pessoa.nome }</td>
			
			<c:if test="${ consolidarTurma.nota }">
			
			<c:forEach var="u" items="${ matricula.notas }" varStatus="loop">
				<%--  Avaliações --%>
				<c:if test="${ fn:length(consolidarTurma.notas) > 0 }">
				<c:if test="${ consolidarTurma.avaliacao }">
					<c:forEach var="aval" items="${ u.avaliacoes }">
						<td bgcolor="#C4D2EB" align="center">
						<span title="${aval.abreviacao} - ${aval.denominacao}"><fmt:formatNumber pattern="#0.0" value="${ aval.nota }"/></span>
						<input type="hidden" name="aval_${ aval.id }" value="<fmt:formatNumber pattern="#0.0" value="${ aval.nota }"/>"/>
						</td>
					</c:forEach>
				</c:if>
				<td align="center" bgcolor="#FDF3E1">
					<span title="${u.unidade}&ordf; unidade"><fmt:formatNumber pattern="#0.0" value="${ u.nota }"/></span>
					<input type="hidden" name="nota_${ u.id }" value="<fmt:formatNumber pattern="#0.0" value="${ u.nota }"/>"/>
				</td>
				</c:if>
			</c:forEach>
			<c:if test="${ fn:length(matricula.notas) == 0 }">
				<c:forEach var="u" items="${ consolidarTurma.notas }">
					<td align="center" bgcolor="#FDF3E1"><fmt:formatNumber pattern="#0.0" value="0"/></td>
				</c:forEach>
			</c:if>
			
			<c:if test="${ consolidarTurma.ead && consolidarTurma.metodologiaAvaliacao.permiteTutor && !consolidarTurma.estagioEad && !consolidarTurma.lato && !consolidarTurma.turmaFeriasEad}">
				<td align="center"><label><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor }"/></label></td>
			</c:if>
			
			<c:if test="${ consolidarTurma.nota  && !consolidarTurma.lato && (!consolidarTurma.ead || (consolidarTurma.ead && (consolidarTurma.duasNotas || consolidarTurma.estagioEad || consolidarTurma.umaNota || consolidarTurma.turmaFeriasEad))) }">
			<td align="center">
				<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacao ? '' : matricula.recuperacao }"/>
				<input type="hidden" name="recup_${ matricula.id }" value="<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacao ? '' : matricula.recuperacao }"/>"/>
			</td>
			</c:if>
			</c:if>
			
			<td align="center">
				<c:if test="${ consolidarTurma.nota }">
					<c:if test="${ !matricula.consolidada }">
					<label>${ matricula.media == null ? '--' : '' }<fmt:formatNumber pattern="#0.0" value="${ matricula.media }"/></label>
					</c:if>
					<c:if test="${ matricula.consolidada }">
					<label><fmt:formatNumber pattern="#0.0" value="${ matricula.mediaFinal }"/></label>
					</c:if>
				</c:if>
				<c:if test="${ consolidarTurma.conceito }">
						<input type="hidden" name="conceito_${ matricula.id }" value="${ matricula.conceito }"/>
						${ matricula.conceitoChar }
				</c:if>
				<c:if test="${ consolidarTurma.competencia }">
						<input type="hidden" name="competencia_${ matricula.id }" value="${ matricula.apto == null ? 'null' : matricula.apto ? 'true' : 'false' }"/>
						${ matricula.apto == null ? '--' : matricula.apto ? 'Apto' : 'Não Apto' }
				</c:if>				
			</td>
			<c:if test="${ !consolidarTurma.ead }">
			<td align="center">${ matricula.numeroFaltas }
				<input type="hidden" name="faltas_${ matricula.id }" value="${ matricula.numeroFaltas }"/>
			</td>
			</c:if>
			<td align="center">${ matricula.situacaoAbrev }</td>
			</tr>
			</c:if>
		</c:forEach>
		<tfoot>
		
		</tfoot>
		<h:outputText value="</table>" escape="false" />
<br/>

		<div class="opcoes">
			<table cellpadding="2" align="center" width="40%" class="caixaCinza">
			<tr><td align="center">Caro Professor,<br/> Por questões de segurança, solicitamos que a sua senha seja redigitada para que a a operação possa ser concluída.</td></tr>
			<tr><td></td></tr>
			<tr><td align="center">Senha: <h:inputSecret value="#{ consolidarTurma.senha }" size="10"/> </td></tr>
			<tr><td></td></tr>
			</table>
			<br/>
			<table align="center" width="35%">
			<tr>
				<c:if test="${ !consolidarTurma.parcial }">
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.consolidar }" image="/img/consolidacao/disk_blue_ok.png" alt="Finalizar (Consolidar)" title="Finalizar (Consolidar)"/></td>
				</c:if>
				<c:if test="${ consolidarTurma.parcial && !consolidarTurma.turmaFinalizadaConsolidacaoParcial }">
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.parcial }" image="/img/consolidacao/disk_yellow.png" alt="Consolidar Parcialmente" title="Consolidar Parcialmente"/></td>
				</c:if>
				<c:if test="${ consolidarTurma.parcial && consolidarTurma.turmaFinalizadaConsolidacaoParcial }">
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.parcial }" image="/img/consolidacao/disk_blue_ok.png" alt="Finalizar (Consolidar)" title="Finalizar (Consolidar)"/></td>
				</c:if>								
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.escolherTurma }" image="/img/consolidacao/nav_left_red.png" alt="Voltar" title="Voltar" immediate="true"/></td>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.imprimir }" image="/img/consolidacao/printer.png" alt="Imprimir" title="Imprimir"/></td>
				
			</tr>
			<tr>
				<c:if test="${ !consolidarTurma.parcial }">
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.consolidar }" value="Finalizar (Consolidar)"/></td>
				</c:if>
				<c:if test="${ consolidarTurma.parcial && !consolidarTurma.turmaFinalizadaConsolidacaoParcial }">
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.parcial }" value="Consolidar Parcialmente"/></td>
				</c:if>
				<c:if test="${ consolidarTurma.parcial && consolidarTurma.turmaFinalizadaConsolidacaoParcial }">
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.parcial }" value="Finalizar (Consolidar)"/></td>
				</c:if>				
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.escolherTurma }" value="Voltar" immediate="true"/></td>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.imprimir }" value="Imprimir"/></td>				
			</tr>
			</table>
		</div>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
