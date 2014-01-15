<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<jwr:style src="/css/jquery-ui/css/jquery-ui-1.8.4.custom.css" media="all"/>

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

.calendar{
	vertical-align:middle;
	padding-bottom:1px;
	margin-left:-3px;
	border: none !important;
}

table.gradeHorarios input{
	border: none !important;
}
-->
</style>

<a4j:keepAlive beanName="buscaTurmaBean"/>
<a4j:keepAlive beanName="horarioTurmaBean"/>

<f:view>

	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
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
	<h:form id="formHorarios" >
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<h:inputText id="horariosString" value="#{horarioTurmaBean.horariosString}" style="display:none;">
		<a4j:support event="onchange" onsubmit="true" actionListener="#{horarioTurmaBean.controleDados}"/> 
	</h:inputText>
	<div class="descricaoOperacao">
	<p>
		<b>Caro usuário</b>, esta tela irá auxiliar na escolha dos horários da turma.
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
		<br/>		
	</c:if>	
	<p>
		Segundo o regulamento de graduação, a porcentagem de aulas ministradas deverá estar entre 
		${horarioTurmaBean.porcentagemMinNumAulas}% e ${horarioTurmaBean.porcentagemMaxNumAulas}% da porcentagem relativa ao 
		número de aulas definidas pela carga horária do componente.
		Ao adicionar horários na turma, a barra de porcentagem abaixo da Grade de Horários irá mostrar a
		porcentagem do número de aulas de acordo com as datas início e fim da turma.   
		<c:if test="${!horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel && ((turmaGraduacaoBean.passivelEdicao && turmaGraduacaoBean.obj.disciplina.modulo) || turmaGraduacaoBean.turmaEad || acesso.dae || acesso.ppg
		             || (acesso.chefeDepartamento && turmaGraduacaoBean.obj.turmaFerias))}">	
		Após os horários serem selecionados, é possível arrastar a barra de porcentagem para configurar as datas início e fim
		da turma afim de se adequarem a regra do regulamento.
		</c:if>
		<c:if test="${horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel}">	
		Como a turma possui horários flexíveis não será possível arrastar a barra de porcentagem. Para modificar a porcentagem de aulas ministradas 
		adicione ou remova horários flexíveis.
		</c:if>
	</p>
	<br/>
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
							<h:inputText id="dataInicioHorario" value="#{horarioTurmaBean.periodoInicio}" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10">
								<f:converter converterId="convertData"/>
							</h:inputText>	
							<img src="/sigaa/img/calendario.png" class="calendar" onclick="showPeriodoInicio();"/>
							à
							<h:inputText id="dataFimHorario" value="#{horarioTurmaBean.periodoFim}" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10">
								<f:converter converterId="convertData"/>
							</h:inputText>
							<img src="/sigaa/img/calendario.png" class="calendar" onclick="showPeriodoFim();"/>
							<script>
								function showPeriodoInicio () {
									jQuery("#formHorarios\\:dataInicioHorario").datepicker("show");
								}
								function showPeriodoFim () {
									jQuery("#formHorarios\\:dataFimHorario").datepicker("show");
								}
							</script>			
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
											actionListener="#{ horarioTurmaBean.parseExpressaoHorario }" onclick="desmarcarHorarios();">
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
								<c:set value="#{true}" var="habilitarSabado"></c:set>
								<c:set value="#{horarioTurmaBean.habilitarDomingo}" var="habilitarDomingo"></c:set>
								
								
								<%@include file="/graduacao/horario_turma/horario_graduacao.jsp"%>
								
								
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
</table>
<br/>

	<h:panelGroup id="panelMsgErros"> 
		<h:outputText value="#{horarioTurmaBean.mensagemErroHorario}" style="color:#AA0000;margin:10px;text-align:center;display:block;font-weight:bold;" escape="false" />
	</h:panelGroup>
	<table class="formulario" width="95%" id="tabelaDatas" >
		<caption>Datas da Turma</caption>
		<tr>
			<th class="required" width="40%">
			<h:outputText value="Início-Fim:" rendered="#{!horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel}"/>
			<h:outputText value="Porcentagem de Aulas:" rendered="#{horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel}"/>	
			</th>
			<td>
				<h:panelGroup id="panelDatas"> 
				
					<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/jquery/jquery.js"></script>
					<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/jquery/plugins/ui/jquery-ui.custom.js"></script>
					
					<h:panelGroup  style="margin-left:7px;" rendered="#{!horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel || acesso.administradorDAE}">
					<h:inputText id="dataInicio" value="#{horarioTurmaBean.dataTurmaInicio}" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10" 
								 disabled="#{ !((turmaGraduacaoBean.passivelEdicao && turmaGraduacaoBean.obj.disciplina.modulo) || turmaGraduacaoBean.turmaEad || acesso.dae || acesso.ppg
					             || (acesso.chefeDepartamento && turmaGraduacaoBean.obj.turmaFerias)) }">
					             <f:converter converterId="convertData"/>
					</h:inputText>             
					<img src="/sigaa/img/calendario.png" class="calendar" onclick="showCalendarInicio();"/>
					
					<h:inputText id="dataFim" value="#{horarioTurmaBean.dataTurmaFim}" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10"
								 disabled="#{ !((turmaGraduacaoBean.passivelEdicao && turmaGraduacaoBean.obj.disciplina.modulo) || turmaGraduacaoBean.turmaEad || acesso.dae || acesso.ppg
					             || (acesso.chefeDepartamento && turmaGraduacaoBean.obj.turmaFerias)) }">
					             <f:converter converterId="convertData"/>
					</h:inputText>             
					<img src="/sigaa/img/calendario.png" class="calendar" onclick="showCalendarFim();"/>
					<br/>
					</h:panelGroup>
					
					<h:inputText id="atualizarSlider"  value="#{horarioTurmaBean.porcentagemAulas}" style="display:none;">
						<a4j:support event="onclick" oncomplete="slide('#{horarioTurmaBean.porcentagemAulas}');" onsubmit="true" 
									 reRender="panelDatas,panelMsgErros" actionListener="#{horarioTurmaBean.calcularPorcentagem}"/> 
					</h:inputText>
					<script>
						function showCalendarInicio () {
							jQuery("#formHorarios\\:dataInicio").datepicker("show");
						}
						function showCalendarFim () {
							jQuery("#formHorarios\\:dataFim").datepicker("show");
						}
					</script>
					<h:inputText id="porcentagem" value="#{horarioTurmaBean.porcentagemAulas}" maxlength="3" size="2" style="display:none;">
						<a4j:support event="onchange" oncomplete="slide('#{horarioTurmaBean.porcentagemAulas}');" onsubmit="true" 
									 reRender="panelDatas" actionListener="#{horarioTurmaBean.calcularDataFim}"/> 
					</h:inputText>

					<div id="slider" style="margin-left:7px;margin-top:10px;width:50%;display:inline-block"></div>
					<h:graphicImage value="/ava/img/accept.png" rendered="#{horarioTurmaBean.porcentagemAulas >= horarioTurmaBean.porcentagemMinNumAulas && horarioTurmaBean.porcentagemAulas <= horarioTurmaBean.porcentagemMaxNumAulas}"/>
					<h:graphicImage value="/ava/img/cancel.png" rendered="#{horarioTurmaBean.porcentagemAulas < horarioTurmaBean.porcentagemMinNumAulas || horarioTurmaBean.porcentagemAulas > horarioTurmaBean.porcentagemMaxNumAulas}"/>
					<span style="vertical-align:top;padding-top:10px;">
					
					<h:outputText id="porcOutput" value="#{horarioTurmaBean.porcentagemAulas}" rendered="#{horarioTurmaBean.porcentagemAulas <= horarioTurmaBean.porcentagemMaxNumAulas}"/>
					<h:outputText value="120+" rendered="#{horarioTurmaBean.porcentagemAulas > horarioTurmaBean.porcentagemMaxNumAulas}"/>
					<span>%</span>
					<ufrn:help>
						<p>A porcentagem de aulas ministradas deverá estar entre ${horarioTurmaBean.porcentagemMinNumAulas}% e ${horarioTurmaBean.porcentagemMaxNumAulas}% da porcentagem relativa ao 
						número de aulas definidas pela carga horária do componente.</p>
					</ufrn:help>
					</span>
				</h:panelGroup>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
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

function desmarcarHorarios() {
	var lista = getEl(document).getChildrenByTagName('input');
	for (i = 0; i < lista.size(); i++) {
		if( lista[i].dom.type == 'checkbox'){
			lista[i].dom.value = false;
		}
	}
}

function limparHorarios() {
	var lista = getEl(document).getChildrenByTagName('input');
	var horariosString = ""; 

	// Limpa os horários selecionados
	for (i = 0; i < lista.size(); i++) {
		if( lista[i].dom.type == 'checkbox')
			lista[i].dom.checked = false;

	}

	// Remarcar os horários presentes no bean, utilizado na atualização
	<c:forEach items='${horariosMarcados}' var='marcado'>
		marcaCheckBox('formHorarios:${marcado}${sufixo}');
	</c:forEach>

	// Grava os horários selecionados na página pra fazer uma verificação com o bean;
	for (i = 0; i < lista.size(); i++) {
		if( lista[i].dom.type == 'checkbox' && lista[i].dom.checked){
			var aux = lista[i].dom.id;
			var auxVector = aux.split(":");
			horariosString += auxVector[1] + ";";  
		}	
	}

	// Envia as informações do horário para o bean
	document.getElementById("formHorarios:horariosString").value = horariosString;
	JQuery("#formHorarios\\:horariosString").trigger("change");
}

var JQuery = jQuery.noConflict();

function slide (beggin){
	JQuery("#slider").slider(
		{
			value: beggin,
			min: 0,
			max: ${horarioTurmaBean.porcentagemMaxNumAulas},
			disabled: ${horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel || !((turmaGraduacaoBean.passivelEdicao && turmaGraduacaoBean.obj.disciplina.modulo) || turmaGraduacaoBean.turmaEad || acesso.dae || acesso.ppg
		             || (acesso.chefeDepartamento && turmaGraduacaoBean.obj.turmaFerias))},
			slide: function(event,ui){ JQuery("#formHorarios\\:porcentagem").val(ui.value); JQuery("#formHorarios\\:porcOutput").html(ui.value);  },
			stop: function(event,ui){ JQuery("#formHorarios\\:porcentagem").trigger("change") }
		}		
	);
	JQuery( "#formHorarios\\:dataInicio" ).datepicker(
		{
			 dateFormat: "dd/mm/yy",
			 nextText: 'Próximo',
			 prevText: 'Anterior',
			 monthNames: ["Janeiro","Fevereiro","Março","Abril","Maio",
						   "Junho","Julho","Agosto","Setembro","Outubro",
						   "Novembro","Dezembro"],
			 dayNamesMin: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"],
			 onSelect: function(dateText, inst) { JQuery("#formHorarios\\:atualizarSlider").trigger("click") }			     
		}
	);
	JQuery( "#formHorarios\\:dataFim" ).datepicker(
		{
			 dateFormat: "dd/mm/yy",
			 nextText: 'Próximo',
			 prevText: 'Anterior',
			 monthNames: ["Janeiro","Fevereiro","Março","Abril","Maio",
						   "Junho","Julho","Agosto","Setembro","Outubro",
						   "Novembro","Dezembro"],
			 dayNamesMin: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"],
			 onSelect: function(dateText, inst) { JQuery("#formHorarios\\:atualizarSlider").trigger("click") }			     
		}
	);
};	

function initCalendariosHorarioFlexivel (){
	JQuery( "#formHorarios\\:dataInicioHorario" ).datepicker(
			{
				 dateFormat: "dd/mm/yy",
				 nextText: 'Próximo',
				 prevText: 'Anterior',
				 monthNames: ["Janeiro","Fevereiro","Março","Abril","Maio",
							   "Junho","Julho","Agosto","Setembro","Outubro",
							   "Novembro","Dezembro"],
				 dayNamesMin: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"]
			}
		);
	JQuery( "#formHorarios\\:dataFimHorario" ).datepicker(
			{
				 dateFormat: "dd/mm/yy",
				 nextText: 'Próximo',
				 prevText: 'Anterior',
				 monthNames: ["Janeiro","Fevereiro","Março","Abril","Maio",
							   "Junho","Julho","Agosto","Setembro","Outubro",
							   "Novembro","Dezembro"],
				 dayNamesMin: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"]
			}
		);
};

JQuery(document).ready(function () {
	slide('${horarioTurmaBean.porcentagemAulas}');
	initCalendariosHorarioFlexivel();
	limparHorarios();
});
</script>