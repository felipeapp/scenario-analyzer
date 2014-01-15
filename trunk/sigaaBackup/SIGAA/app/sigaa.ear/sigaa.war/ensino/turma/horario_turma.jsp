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
 	Definir Hor�rios</h2>
	<h:form id="formHorarios">
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<div class="descricaoOperacao">
	<p>
		Caro usu�rio, esta tela ir� auxiliar na escolha dos hor�rios da turma.
	</p>
	<br/>
	<c:if test="${horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel}">
		<p>
			O componente escolhido permite que a turma possua hor�rios flex�veis, ou seja, podemos definir a data in�cio e fim de cada hor�rio. Dessa forma a turma poder� ter n-Hor�rios.
		</p>
		<p>
			Por Exemplo, no per�odo de: <b>10/08/${ horarioTurmaBean.anoAtual } � 10/09/${ horarioTurmaBean.anoAtual }</b> seu hor�rio ser� <b>2T123</b>
			e de <b>11/09/${ horarioTurmaBean.anoAtual } at� 16/12/${ horarioTurmaBean.anoAtual }</b> ser� <b>3T123</b>
		</p>
		<p>
			Defina o in�cio - fim do per�odo e marque o hor�rio desejado na grade. Em seguida clique em <i>Adicionar Hor�rio</i>.
			<br/>
			Repita este processo at� cadastrar todos os hor�rios. Quando finalizar, clique em <i>Pr�ximo Passo</i> para continuar o cadastro da Turma. 
		</p>
	</c:if>	
	</div>
	
	<c:set var="turma" value="${horarioTurmaBean.obj}" />
	<%@include file="/ensino/turma/info_turma.jsp"%>




					


<table class="formulario" width="95%" id="tabelaDeHorarios" border="1">
	<caption>Hor�rio da Turma</caption>
		<c:if test="${horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel && horarioTurmaBean.podeAlterarHorarios}">
			<tr>
				<td>
					<table class="subFormulario" width="100%">
					<caption>Inicio e Fim do Hor�rio</caption>
					<tr>
						<th class="obrigatorio">
							Per�odo do Hor�rio:
						</th>
						<td width="25%">
							<t:inputCalendar value="#{horarioTurmaBean.periodoInicio}" renderAsPopup="true" size="10" maxlength="10" id="dataInicioHorario"
								popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)">
								<f:converter converterId="convertData"/>
							</t:inputCalendar>
							�
							<t:inputCalendar value="#{horarioTurmaBean.periodoFim}" renderAsPopup="true" size="10" maxlength="10" id="dataFimHorario"
									popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)">
								<f:converter converterId="convertData"/>
							</t:inputCalendar>					
						</td>
						<td style="text-align: left;">
							<h:commandButton action="#{horarioTurmaBean.periodoCompleto}" value="Usar o mesmo per�odo da Turma" />
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
						<caption>Grade de Hor�rios</caption>
					</c:if>
						
						<c:if test="${horarioTurmaBean.mostrarOpcaoMudarGradeHorarios}">
							<tr>						
								<td> 								
								<br/>								
								<b> Grade de Hor�rios</b>							
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
										 Express�o do Hor�rio:
										<h:inputText size="20" maxlength="20" id="expressaoHorario" onkeyup="CAPS(this);"  
											value="#{ horarioTurmaBean.expressaoHorario }"/>
										<h:commandButton id="defineTabelaHorario" value="Atualizar Grade de Hor�rios"
											actionListener="#{ horarioTurmaBean.parseExpressaoHorario }" >
										</h:commandButton>
										<ufrn:help>Informe uma express�o para o hor�rio como, por exemplo, 
										246M12 ou 35T34. A express�o do hor�rio poder� conter mais de um 
										intervalo como, por exemplo, 246M12 35T34. O hor�rio na tabela abaixo 
										ser� atualizado com esta express�o, caso seja v�lida.</ufrn:help>
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
										<h:commandButton value="Adicionar Hor�rio" actionListener="#{ horarioTurmaBean.adicionarHorario }"/>
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
						<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover Per�odo 
					</div>	
					</c:if>
					<h:dataTable value="#{horarioTurmaBean.modelGrupoHorarios}" var="h"  styleClass="listagem" headerClass="center, right" rowClasses="linhaPar, linhaImpar" columnClasses="center, right">
						<f:facet name="caption">
							<h:outputText value="Per�odos Adicionados"/>
						</f:facet>	
		
						<h:column headerClass="center">
							<f:facet name="header">
								<h:panelGroup>
									<div class="center"><h:outputText value="Per�odo"/></div>
								</h:panelGroup>
							</f:facet>
								<h:outputText value="#{h.periodo.inicio}" /> - <h:outputText value="#{h.periodo.fim}" />
						</h:column>				
					
						<h:column headerClass="right">
							<f:facet name="header">
								<h:panelGroup>
									<div class="right"><h:outputText value="Hor�rio" /></div>
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
					<h:commandButton value="Pr�ximo Passo >>" action="#{ horarioTurmaBean.submeterHorarios }" id="btnAvancar"/>
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