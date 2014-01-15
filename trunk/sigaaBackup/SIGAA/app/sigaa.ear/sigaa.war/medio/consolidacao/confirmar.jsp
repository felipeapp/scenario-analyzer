<% request.setAttribute("res1024","true"); %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<style>
	table#notas_turma thead tr th{
		text-align:center;
	}
</style>		
<h2><ufrn:subSistema/> &gt;Consolidação de Disciplina</h2>
<a4j:keepAlive beanName="consolidarDisciplinaMBean"/>
<h:form id="confirmar">
<input type="hidden" name="gestor" value="${ param['gestor'] }"/>
<h3>${ consolidarDisciplinaMBean.descricaoTurma }</h3>
	<hr/>
	<br/>
	<div class="notas" style="clear: both;">
		<c:set var="regrasNotas" value="#{consolidarDisciplinaMBean.regraNotas}"/>
		<table class="listagem" width="100%" id="notas_turma">
		<caption>Alunos matriculados</caption>
		<thead>
			<tr>
				<th width="10%" rowspan="2"><p style="text-align: center;padding-right:5px;">Matrícula</p></th>
				
				<th  rowspan="2"><p style="padding-right:5px;">Nome</p> <input type="hidden" name="unidade" id="unidade"/></th>
				
				<c:set var="provaFinal" value=""/>
				<c:forEach items="#{regrasNotas}" var="item">
					<c:if test="${!item.provaFinal}">
						<th colspan="${item.nota ? '2' : '1'}" rowspan="${item.nota ? '1' : '2'}" id="thBorda">
							<h:outputText value="#{item.titulo}"/>
						</th>
					</c:if>
					<c:if test="${item.provaFinal}">
						<c:set var="provaFinal" value="#{item.titulo}"/>		
					</c:if>
				</c:forEach>
				<th colspan="${not empty provaFinal ? '3' : '2'}" >MÉDIA</th>
				<th rowspan="2" id="thFaltas">Faltas</th>
				<th rowspan="2" id="thFaltas">Sit.</th>
			</tr>
			<tr>		
				<c:forEach items="#{regrasNotas}" var="item">
					<c:if test="${item.nota}">
						<th id="thMedia">Média</th>
						<c:if test="${item.nota}">
							<th>Faltas</th>						
						</c:if>
					</c:if>
				</c:forEach>			
				<th id="thMedia">Anual</th>	
				<c:if test="${not empty provaFinal}">
					<th>${provaFinal}</th>
				</c:if>
				<th>Final</th>		
			</tr>
		</thead>
	
		<tbody>
			<c:forEach items="#{consolidarDisciplinaMBean.notasDisciplina}" var="itemDisc"  varStatus="i">
			
			<tr id="linha_${ i.index }" class="${ i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<c:if test="${!consolidarDisciplinaMBean.parcial or !itemDisc.matricula.emRecuperacao }">
				
					<td class="disciplina" align="center">${itemDisc.matricula.discente.matricula}</td>
					<td class="disciplina">${itemDisc.matricula.discente.nome}</td>
	
					<c:set var="notaRecFinal" value="-"/>
					<c:forEach items="#{regrasNotas}" var="regra" varStatus="r">
						
						<c:if test="${regra.nota || regra.recuperacao}">
							
							<c:forEach items="#{itemDisc.notas}" var="itemNota">
								<c:if test="${itemNota.regraNota.id == regra.id}">
									<c:set var="idNota" value="#{itemNota.notaUnidade.id}"/>
									
									<td id="tdNota" bgcolor="#FDF3E1" align="center">
										<h:outputText value="#{itemNota.notaUnidade.nota eq null ? '--' : itemNota.notaUnidade.nota}" />
									</td>	
									
									<c:if test="${regra.nota}">
									<td class="nota" id="tdFaltas" bgcolor="#FDF3E1" align="center">
										<h:outputText value="#{itemNota.notaUnidade.faltas}"/>
									</td>
									</c:if>					
								</c:if>
							</c:forEach>
							
						</c:if>
						
					</c:forEach>			
					
					<td id="tdNota" align="center">
						<h:outputFormat value="#{itemDisc.mediaParcial > 0.0 ? itemDisc.mediaParcial : '--'}" />
					</td>	
					<td id="tdNota" align="center">
						<h:outputText value="#{itemDisc.matricula.recuperacao ne null ? itemDisc.matricula.recuperacao : '--' }" />
					</td>
					<td id="tdNota" align="center">
						<h:outputFormat value="#{itemDisc.matricula.mediaFinal ne null ? itemDisc.matricula.mediaFinal : '--'}" />
					</td>			
					<td id="tdFaltasTotal" class="nota" align="center">
						<h:outputText value="#{itemDisc.matricula.numeroFaltas}" />
					</td>			
					<td id="tdNota" class="nota" align="center">
						<h:outputText value="#{itemDisc.matricula.situacaoAbrev}" />
					</td>
				</c:if>
			</tr>
			
		</c:forEach>
		</tbody>
		
		</table>
	</div>		
		<br/><br/>

		<div class="opcoes">
			<table cellpadding="2" align="center" width="40%" class="caixaCinza">
			<tr><td align="center">Caro Professor,<br/> Por questões de segurança, solicitamos que a sua senha seja redigitada para que a a operação possa ser concluída.</td></tr>
			<tr><td></td></tr>
			<tr><td align="center">Senha: <h:inputSecret value="#{ consolidarDisciplinaMBean.senha }" size="10"/> </td></tr>
			<tr><td></td></tr>
			</table>
			<br/>
			
			<table align="center" width="35%">
			<tr>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarDisciplinaMBean.selecionarDisciplina }" image="/img/consolidacao/nav_left_red.png" alt="Voltar" title="Voltar" immediate="true"/></td>
			<% /* <td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarDisciplinaMBean.imprimir }" image="/img/consolidacao/printer.png" alt="Imprimir" title="Imprimir"/></td> */ %>
				<c:choose>
				<c:when test="${!consolidarDisciplinaMBean.parcial}">
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarDisciplinaMBean.consolidar }" rendered="#{!consolidarDisciplinaMBean.parcial}" image="/img/consolidacao/disk_blue_ok.png" alt="Finalizar (Consolidar)" title="Finalizar (Consolidar)"/></td>
				</c:when>
				<c:otherwise>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarDisciplinaMBean.parcial }" rendered="#{consolidarDisciplinaMBean.parcial}" image="/img/consolidacao/disk_yellow.png" alt="Consolidar Parcialmente" title="Consolidar Parcialmente"/></td>
				</c:otherwise>
				</c:choose>
			</tr>
			<tr>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarDisciplinaMBean.selecionarDisciplina }" value="Voltar" immediate="true"/></td>
			<%/* <td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarDisciplinaMBean.imprimir }" value="Imprimir"/></td> */%>
				<c:choose>
				<c:when test="${!consolidarDisciplinaMBean.parcial}">
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarDisciplinaMBean.consolidar }" rendered="#{!consolidarDisciplinaMBean.parcial}" value="Finalizar (Consolidar)"/></td>
				</c:when>
				<c:otherwise>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarDisciplinaMBean.parcial }" rendered="#{consolidarDisciplinaMBean.parcial}" value="Consolidar Parcialmente"/></td>
				</c:otherwise>
				</c:choose>
			</tr>
			</table>
		</div>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
