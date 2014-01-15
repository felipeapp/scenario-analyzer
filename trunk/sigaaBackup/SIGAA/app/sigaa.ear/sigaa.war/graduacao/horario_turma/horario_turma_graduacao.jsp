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
 	Definir Hor�rios</h2>
	<h:form id="formHorarios" >
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<h:inputText id="horariosString" value="#{horarioTurmaBean.horariosString}" style="display:none;">
		<a4j:support event="onchange" onsubmit="true" actionListener="#{horarioTurmaBean.controleDados}"/> 
	</h:inputText>
	<div class="descricaoOperacao">
	<p>
		<b>Caro usu�rio</b>, esta tela ir� auxiliar na escolha dos hor�rios da turma.
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
		<br/>		
	</c:if>	
	<p>
		Segundo o regulamento de gradua��o, a porcentagem de aulas ministradas dever� estar entre 
		${horarioTurmaBean.porcentagemMinNumAulas}% e ${horarioTurmaBean.porcentagemMaxNumAulas}% da porcentagem relativa ao 
		n�mero de aulas definidas pela carga hor�ria do componente.
		Ao adicionar hor�rios na turma, a barra de porcentagem abaixo da Grade de Hor�rios ir� mostrar a
		porcentagem do n�mero de aulas de acordo com as datas in�cio e fim da turma.   
		<c:if test="${!horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel && ((turmaGraduacaoBean.passivelEdicao && turmaGraduacaoBean.obj.disciplina.modulo) || turmaGraduacaoBean.turmaEad || acesso.dae || acesso.ppg
		             || (acesso.chefeDepartamento && turmaGraduacaoBean.obj.turmaFerias))}">	
		Ap�s os hor�rios serem selecionados, � poss�vel arrastar a barra de porcentagem para configurar as datas in�cio e fim
		da turma afim de se adequarem a regra do regulamento.
		</c:if>
		<c:if test="${horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel}">	
		Como a turma possui hor�rios flex�veis n�o ser� poss�vel arrastar a barra de porcentagem. Para modificar a porcentagem de aulas ministradas 
		adicione ou remova hor�rios flex�veis.
		</c:if>
	</p>
	<br/>
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
							<h:inputText id="dataInicioHorario" value="#{horarioTurmaBean.periodoInicio}" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10">
								<f:converter converterId="convertData"/>
							</h:inputText>	
							<img src="/sigaa/img/calendario.png" class="calendar" onclick="showPeriodoInicio();"/>
							�
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
											actionListener="#{ horarioTurmaBean.parseExpressaoHorario }" onclick="desmarcarHorarios();">
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
</table>
<br/>

	<h:panelGroup id="panelMsgErros"> 
		<h:outputText value="#{horarioTurmaBean.mensagemErroHorario}" style="color:#AA0000;margin:10px;text-align:center;display:block;font-weight:bold;" escape="false" />
	</h:panelGroup>
	<table class="formulario" width="95%" id="tabelaDatas" >
		<caption>Datas da Turma</caption>
		<tr>
			<th class="required" width="40%">
			<h:outputText value="In�cio-Fim:" rendered="#{!horarioTurmaBean.obj.disciplina.permiteHorarioFlexivel}"/>
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
						<p>A porcentagem de aulas ministradas dever� estar entre ${horarioTurmaBean.porcentagemMinNumAulas}% e ${horarioTurmaBean.porcentagemMaxNumAulas}% da porcentagem relativa ao 
						n�mero de aulas definidas pela carga hor�ria do componente.</p>
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

	// Limpa os hor�rios selecionados
	for (i = 0; i < lista.size(); i++) {
		if( lista[i].dom.type == 'checkbox')
			lista[i].dom.checked = false;

	}

	// Remarcar os hor�rios presentes no bean, utilizado na atualiza��o
	<c:forEach items='${horariosMarcados}' var='marcado'>
		marcaCheckBox('formHorarios:${marcado}${sufixo}');
	</c:forEach>

	// Grava os hor�rios selecionados na p�gina pra fazer uma verifica��o com o bean;
	for (i = 0; i < lista.size(); i++) {
		if( lista[i].dom.type == 'checkbox' && lista[i].dom.checked){
			var aux = lista[i].dom.id;
			var auxVector = aux.split(":");
			horariosString += auxVector[1] + ";";  
		}	
	}

	// Envia as informa��es do hor�rio para o bean
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
			 nextText: 'Pr�ximo',
			 prevText: 'Anterior',
			 monthNames: ["Janeiro","Fevereiro","Mar�o","Abril","Maio",
						   "Junho","Julho","Agosto","Setembro","Outubro",
						   "Novembro","Dezembro"],
			 dayNamesMin: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"],
			 onSelect: function(dateText, inst) { JQuery("#formHorarios\\:atualizarSlider").trigger("click") }			     
		}
	);
	JQuery( "#formHorarios\\:dataFim" ).datepicker(
		{
			 dateFormat: "dd/mm/yy",
			 nextText: 'Pr�ximo',
			 prevText: 'Anterior',
			 monthNames: ["Janeiro","Fevereiro","Mar�o","Abril","Maio",
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
				 nextText: 'Pr�ximo',
				 prevText: 'Anterior',
				 monthNames: ["Janeiro","Fevereiro","Mar�o","Abril","Maio",
							   "Junho","Julho","Agosto","Setembro","Outubro",
							   "Novembro","Dezembro"],
				 dayNamesMin: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"]
			}
		);
	JQuery( "#formHorarios\\:dataFimHorario" ).datepicker(
			{
				 dateFormat: "dd/mm/yy",
				 nextText: 'Pr�ximo',
				 prevText: 'Anterior',
				 monthNames: ["Janeiro","Fevereiro","Mar�o","Abril","Maio",
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