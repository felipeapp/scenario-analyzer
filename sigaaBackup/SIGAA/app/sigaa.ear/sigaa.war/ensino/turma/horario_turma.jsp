<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
<!--
.left {
	text-align: left;
	border-spacing: 3px;
}

.center {
	text-align: center;
	border-spacing: 3px;
}

.right{
	text-align: right;
	border-spacing: 3px;
}
-->
</style>

<a4j:keepAlive beanName="buscaTurmaBean"/>
<a4j:keepAlive beanName="horarioTurmaBean"/>

<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<c:if test="${acesso.chefeDepartamento}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	<h2 class="title"><ufrn:subSistema />  &gt; 
	<c:if test="${not empty horarioTurmaBean.tituloOperacao}">
		${horarioTurmaBean.tituloOperacao}  &gt; 
	</c:if>
 	Definir Horários</h2>
	<h:form id="formHorarios">
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<div class="descricaoOperacao">
	<p>
		Caro usuário, esta tela irá auxiliar na escolha dos horários da turma.
	</p>
	<br/>
	<c:if test="${horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel}">
		<p>
			O componente escolhido permite que a turma possua horários flexíveis, ou seja, podemos definir a data início e fim de cada horário. Dessa forma a turma poderá ter n-Horários.
		</p>
		<p>
			Por Exemplo, no período de: <b>10/08/${ horarioTurmaBean.anoAtual } à 10/09/${ horarioTurmaBean.anoAtual }</b> seu horário será <b>2T123</b>
			e de <b>11/09/${ horarioTurmaBean.anoAtual } até 16/12/${ horarioTurmaBean.anoAtual }</b> será <b>3T123</b>
		</p>
		<p>
			Defina o início - fim do período e marque o horário desejado na grade. Em seguida clique em <i>Adicionar Horário</i>.
			<br/>
			Repita este processo até cadastrar todos os horários. Quando finalizar, clique em <i>Próximo Passo</i> para continuar o cadastro da Turma. 
		</p>
	</c:if>	
	</div>
	
	<c:set var="turma" value="${horarioTurmaBean.obj}" />
	<%@include file="/ensino/turma/info_turma.jsp"%>




					


<table class="formulario" width="95%" id="tabelaDeHorarios" border="1">
	<caption>Horário da Turma</caption>
		<c:if test="${horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel && horarioTurmaBean.podeAlterarHorarios}">
			<tr>
				<td>
					<table class="subFormulario" width="100%">
					<caption>Inicio e Fim do Horário</caption>
					<tr>
						<th class="obrigatorio">
							Período do Horário:
						</th>
						<td width="25%">
							<t:inputCalendar value="#{horarioTurmaBean.periodoInicio}" renderAsPopup="true" size="10" maxlength="10" id="dataInicioHorario"
								popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)">
								<f:converter converterId="convertData"/>
							</t:inputCalendar>
							à
							<t:inputCalendar value="#{horarioTurmaBean.periodoFim}" renderAsPopup="true" size="10" maxlength="10" id="dataFimHorario"
									popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)">
								<f:converter converterId="convertData"/>
							</t:inputCalendar>					
						</td>
						<td style="text-align: left;">
							<h:commandButton action="#{horarioTurmaBean.periodoCompleto}" value="Usar o mesmo período da Turma" />
						</td>
					</tr>
					</table>
				</td>
			</tr>
		</c:if>
		
		<tr>
			<td>
				<t:div id="painel">			
					<table class="subFormulario" width="100%" id="tabelaGradeDeHorarios">
					
					<c:if test="${!horarioTurmaBean.mostrarOpcaoMudarGradeHorarios}">
						<caption>Grade de Horários</caption>
					</c:if>
						
						<c:if test="${horarioTurmaBean.mostrarOpcaoMudarGradeHorarios}">
							<tr>						
								<td> 								
								<br/>								
								<b> Grade de Horários</b>							
									<a4j:region>
								    	<h:selectOneMenu value="#{horarioTurmaBean.unidadeGrade.id}" id="idUnidadeDaGrade"  
											valueChangeListener="#{horarioTurmaBean.carregarGrade }" style="width: 40%">										
											<f:selectItems value="#{horarioTurmaBean.allGradesHorario}" id="itensUnidadeGrade"/>
											<a4j:support event="onchange" reRender="painel"/>						
										</h:selectOneMenu>
										<a4j:status>
											<f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
									    </a4j:status>
									</a4j:region>									
									<br/>
									<br/>								
								</td>						
							</tr>
						</c:if>
						<c:if test="${horarioTurmaBean.podeAlterarHorarios}">	
							<tr>
								<td style="text-align: center;">
									<a4j:region>
										 Expressão do Horário:
										<h:inputText size="20" maxlength="20" id="expressaoHorario" onkeyup="CAPS(this);"  
											value="#{ horarioTurmaBean.expressaoHorario }"/>
										<h:commandButton id="defineTabelaHorario" value="Atualizar Grade de Horários"
											actionListener="#{ horarioTurmaBean.parseExpressaoHorario }" >
										</h:commandButton>
										<ufrn:help>Informe uma expressão para o horário como, por exemplo, 
										246M12 ou 35T34. A expressão do horário poderá conter mais de um 
										intervalo como, por exemplo, 246M12 35T34. O horário na tabela abaixo 
										será atualizado com esta expressão, caso seja válida.</ufrn:help>
									</a4j:region>
								</td>
							</tr>	
						</c:if>
							
							<tr>
								<td align="center">
								<c:set value="${horarioTurmaBean.horariosGrade }" var="horarios"></c:set>
								<c:set value="${horarioTurmaBean.horariosMarcados }" var="horariosMarcados"></c:set>
								<c:set value="true" var="habilitarSabado"></c:set>
								<c:set value="${horarioTurmaBean.habilitarDomingo}" var="habilitarDomingo"></c:set>
								
								<%@include file="/WEB-INF/jsp/include/horario.jsp"%>
								
								
								</td>
							</tr>
							
							<c:if test="${horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel && horarioTurmaBean.podeAlterarHorarios}">
							<tfoot>
								<tr>
									<td>
									<center>
										<h:commandButton value="Adicionar Horário" actionListener="#{ horarioTurmaBean.adicionarHorario }"/>
									</center>
									</td>
								</tr>
							</tfoot>
							</c:if>
					</table>
				
				</t:div>
						
			</td>
		</tr>
		<c:if test="${horarioTurmaBean.modelGrupoHorarios.rowCount > 0}">
		<tr>
			<td>
				
					<c:if test="${horarioTurmaBean.podeAlterarHorarios}">
					<div class="infoAltRem" style="width: 100%">
						<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover Período 
					</div>	
					</c:if>
					<h:dataTable value="#{horarioTurmaBean.modelGrupoHorarios}" var="h"  styleClass="listagem" headerClass="center, right" rowClasses="linhaPar, linhaImpar" columnClasses="center, right">
						<f:facet name="caption">
							<h:outputText value="Períodos Adicionados"/>
						</f:facet>	
		
						<h:column headerClass="center">
							<f:facet name="header">
								<h:panelGroup>
									<div class="center"><h:outputText value="Período"/></div>
								</h:panelGroup>
							</f:facet>
								<h:outputText value="#{h.periodo.inicio}" /> - <h:outputText value="#{h.periodo.fim}" />
						</h:column>				
					
						<h:column headerClass="right">
							<f:facet name="header">
								<h:panelGroup>
									<div class="right"><h:outputText value="Horário" /></div>
								</h:panelGroup> 
							</f:facet>
							<div style="text-align: right;">
								<h:outputText value="#{h.horarioFormatado}" />
							</div>
						</h:column>
						
						<h:column>
							<h:commandButton id="alterar" actionListener="#{horarioTurmaBean.removerPeriodoHorarioFlexivel}" image="/img/delete.gif" onclick="#{confirmDelete}" 
							rendered="#{horarioTurmaBean.podeAlterarHorarios}"/>
						</h:column>						
					</h:dataTable>
				
			</td>
		</tr>
		</c:if>
		<tfoot>
			<tr>
				<td>
				<center>
					<h:commandButton value="<< Passo Anterior" action="#{ horarioTurmaBean.voltarPassoAnterior }" id="btnVoltar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ horarioTurmaBean.cancelar }" id="btnCancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{ horarioTurmaBean.submeterHorarios }" id="btnAvancar"/>
				</center>
				</td>
			</tr>
		</tfoot>		
</table>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script>
<%-- DESATIVANDO OS CHECKBOX NECESSARIOS --%>
var lista = getEl(document).getChildrenByTagName('input');
for (i = 0; i < lista.size(); i++) {
	<c:if test="${!horarioTurmaBean.podeAlterarHorarios}">
		if( lista[i].dom.type == 'checkbox'){
			lista[i].dom.disabled = true;
		}
	</c:if>
}
</script>