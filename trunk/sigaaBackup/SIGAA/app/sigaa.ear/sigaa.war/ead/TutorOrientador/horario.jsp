<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Tem certeza que deseja remover este horário de atendimento?')) return false" scope="request"/>
<style>
	.direita {
		text-align: right;
	}
	table.formulario thead tr th.direita {
		text-align: right;
	}
	
</style>

<f:view>
<c:if test="${acesso.coordenadorPolo}">
	<%@include file="/portais/cpolo/menu_cpolo.jsp" %>
</c:if>

<h2><ufrn:subSistema /> > Horário de Trabalho de Tutores</h2>

<h:form id="out">

	<div class="infoAltRem" align="center">
		<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: Adicionar Horário
	    <h:graphicImage value="/img/delete.png" style="overflow: visible;"/>: Remover Horário <br/>
	</div>

<h:messages showDetail="true"/>
<h:outputText value="#{ tutorOrientador.create }"/>
<table class="formulario" width="70%">
<caption>Horário do Tutor</caption>

<c:if test="${acesso.coordenadorPolo == true}">
				<tr>
					<th class="required">Tutores:</th>
					<td>
					<a4j:region>
							<h:selectOneMenu id="tipo" valueChangeListener="#{tutorOrientador.definirHorarioTutorPeloCoordenadorPolo}">
								<f:selectItems value="#{tutorOrientador.allTutoresCombo}" />
								<a4j:support event="onchange" reRender="out"/>
							</h:selectOneMenu>
							<a4j:status>
				                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
				            </a4j:status>
					</a4j:region>
					</td>
				</tr>

	<tr><th style="font-weight:bold;">Curso: </th><td>${ tutorOrientador.obj.poloCurso.curso.descricao }</td></tr>
	
	<tr><td colspan="2">
	<table class="formulario" width="100%">
	<caption>Horários</caption>
	<tr>
		<td><label class="required">Dia:</label> 
		    <h:selectOneMenu value="#{ tutorOrientador.horario.dia }">
		    	<f:selectItems value="#{ tutorOrientador.diasSemana }"/>
		   	</h:selectOneMenu>
		</td>
		<td><label class="required">Hora Início:</label>
			<h:selectOneMenu value="#{ tutorOrientador.horario.horaInicio }">
				<f:selectItems value="#{ tutorOrientador.horas }"/>
			</h:selectOneMenu>
		</td>
		<td><label class="required">Hora Fim: </label> 
			<h:selectOneMenu value="#{ tutorOrientador.horario.horaFim }">
				<f:selectItems value="#{ tutorOrientador.horas }"/>
			</h:selectOneMenu>
		</td>
		<td width="8%">
			<a4j:commandButton image="/img/adicionar.gif" actionListener="#{ tutorOrientador.adicionarHorario }"
							   title="Adicionar Horário"  reRender="tabelaHorariosCoord,mensagensCoord"/> 
		</td>
	</tr>
	<tr>
		<td align="center" colspan="4">
			<a4j:outputPanel id="mensagensCoord">
				<h:outputText value="#{tutorOrientador.mensagemSucesso}" style="text-align: center; color: green; font-weight: bold;"/>
				<h:outputText value="#{tutorOrientador.mensagemErro}"    style="text-align: center; color: red; font-weight: bold;"/>
			</a4j:outputPanel>
		</td>
	</tr>
	
	<tr><td colspan="4">
	
	<a4j:outputPanel id="tabelaHorariosCoord">
		<c:if test="${tutorOrientador.horarios.rowCount > 0}">
			<br>
			
				<h:dataTable var="hora" value="#{ tutorOrientador.horarios }" width="100%" rowClasses="linhaPar, linhaImpar"
							 columnClasses=",direita,direita,direita">
					<h:column>
						<f:facet name="header"><h:outputText value="Dia da Semana"/></f:facet>
						<h:outputText value="#{ hora.diaDesc }"/>
					</h:column>
					<h:column headerClass="direita">
					<f:facet name="header"><h:outputText value="Hora Início"/></f:facet>
						<h:outputText value="#{ hora.horaInicio }h"/>
					</h:column>
					<h:column headerClass="direita">
						<f:facet name="header"><h:outputText value="Hora Fim"/></f:facet>
						<h:outputText value="#{ hora.horaFim }h"/>
					</h:column>
					<h:column>
						<a4j:commandButton image="/img/delete.png"  actionListener="#{ tutorOrientador.removerHorario }" title="Remover Horário" 
									       alt="Remover Horário" onclick="#{confirmDelete}" reRender="tabelaHorariosCoord,mensagensCoord">
							<f:param name="idHorario" value="#{hora.id}"></f:param>
						</a4j:commandButton>
					</h:column>
				</h:dataTable>
		</c:if>
		
	
		<c:if test="${tutorOrientador.horarios.rowCount == 0}">
			<p style="text-align: center; margin: 1%; color: red; font-weight: bold;">Nenhum horário definido</p>
		</c:if>
	</a4j:outputPanel>
	</td>
	</tr>
	</table>
	</td></tr>
</c:if>

<c:if test="${acesso.coordenadorPolo == false}">

	<tr><th style="font-weight:bold;">Nome: </th><td>${ tutorOrientador.obj.pessoa.nome }</td></tr>
	<tr><th style="font-weight:bold;">Polo: </th><td>${ tutorOrientador.obj.poloCurso.polo.cidade.nomeUF }</td></tr>
	<tr><th style="font-weight:bold;">Curso: </th><td>${ tutorOrientador.obj.poloCurso.curso.descricao }</td></tr>
	
	<tr><td colspan="2">
	<table class="formulario" width="100%">
	<caption>Horários</caption>
	<tr>
		<td><label class="required">Dia:</label>
			<h:selectOneMenu value="#{ tutorOrientador.horario.dia }">
				<f:selectItems value="#{ tutorOrientador.diasSemana }"/>
			</h:selectOneMenu>
		</td>
		<td><label class="required">Hora Início:</label> 
			<h:selectOneMenu value="#{ tutorOrientador.horario.horaInicio }">
				<f:selectItems value="#{ tutorOrientador.horas }"/>
			</h:selectOneMenu>
		</td>
		<td><label class="required">Hora Fim:</label> 
			<h:selectOneMenu value="#{ tutorOrientador.horario.horaFim }">
				<f:selectItems value="#{ tutorOrientador.horas }"/>
			</h:selectOneMenu>
		</td>
		<td width="8%"><a4j:commandButton image="/img/adicionar.gif" actionListener="#{ tutorOrientador.adicionarHorario }"
										  title="Adicionar Horário"  reRender="tabelaHorariosDocente,mensagens"/> </td>
	</tr>
	<tr>
		<td align="center" colspan="4">
			<a4j:outputPanel id="mensagens">
				<h:outputText value="#{tutorOrientador.mensagemSucesso}" style="text-align: center; color: green; font-weight: bold;"/>
				<h:outputText value="#{tutorOrientador.mensagemErro}"    style="text-align: center; color: red; font-weight: bold;"/>
			</a4j:outputPanel>
		</td>
	</tr>
	<tr><td colspan="4">
	<a4j:outputPanel id="tabelaHorariosDocente">
		<br>
		<c:if test="${tutorOrientador.horarios.rowCount > 0}">
			<h:dataTable var="hora" value="#{ tutorOrientador.horarios }" width="100%" rowClasses="linhaPar, linhaImpar"
						 columnClasses=",direita,direita,direita">
				<h:column>
					<f:facet name="header"><h:outputText value="Dia da Semana"/></f:facet>
					<h:outputText value="#{ hora.diaDesc }" />
				</h:column>
				<h:column headerClass="direita">
				<f:facet name="header"><h:outputText value="Hora Início"/></f:facet>
					<h:outputText value="#{ hora.horaInicio }h" />
				</h:column>
				<h:column headerClass="direita">
					<f:facet name="header"><h:outputText value="Hora Fim"/></f:facet>
					<h:outputText value="#{ hora.horaFim }h" />
				</h:column>
				<h:column>
					<a4j:commandButton image="/img/delete.png" actionListener="#{ tutorOrientador.removerHorario }"
									   title="Remover Horário" alt="Remover Horário" onclick="#{confirmDelete}" reRender="tabelaHorariosDocente,mensagens">
						<f:param name="idHorario" value="#{hora.id}"></f:param>
					</a4j:commandButton>
				</h:column>
			</h:dataTable>
		</c:if>
						
		<c:if test="${tutorOrientador.horarios.rowCount == 0}">
			<p style="text-align: center; margin: 1%; color: red; font-weight: bold;">Nenhum horário definido</p>
		</c:if>
	</a4j:outputPanel>
	
	</td>
	</tr>
	</table>
	</td></tr>
</c:if>
			<tfoot>
				<tr><td colspan="2">
				<h:commandButton value="Confirmar Horários" action="#{ tutorOrientador.cadastrarHorario }"/>
				<h:commandButton value="Cancelar" action="#{ tutorOrientador.cancelar }" onclick="return(confirm('Deseja realmente cancelar a operação?'));"/>
				</td></tr>
			</tfoot>
			</table>
			<br/>
	<div align="center">
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</div>
	<br/>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
