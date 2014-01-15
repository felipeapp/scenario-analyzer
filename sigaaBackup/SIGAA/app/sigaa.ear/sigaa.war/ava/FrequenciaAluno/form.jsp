
<%@include file="/ava/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" media="all" href="/sigaa/css/turma-virtual/frequencia.css"/>

<f:view>
<a4j:keepAlive beanName="frequenciaAluno" />
<%@include file="/ava/menu.jsp" %>

<h:form>


<c:set var="freq" value="#{ frequenciaAluno.listagem }"/>
<script type="text/javascript">
function marcarFalta(elem) {
	var maxFaltas = J("#maxFaltas").val();
	J("select", elem.parentNode.parentNode).val(maxFaltas);
}

function marcarPresenca(elem) {
	J("select", elem.parentNode.parentNode).val(0);
}

function todosAusentes (){

	var maxFaltas = J("#maxFaltas").val();
	
	J(".selectFreq").each(function (){
		J(this).val(maxFaltas);
	});
}

function todosPresentes (){
	J(".selectFreq").each(function (){
		J(this).val(0);
	});
}

</script>

<fieldset>
<legend>Lançar frequência da <h:outputText rendered="#{ frequenciaAluno.turma.turmaAgrupadora != null }" value="subturma #{frequenciaAluno.turma.codigo}" /><h:outputText rendered="#{ frequenciaAluno.turma.turmaAgrupadora == null }" value="Turma" /></legend>


<div class="descricaoOperacao">
<p>Através deste recurso é possível marcar as presenças dos alunos da turma, preenchendo automaticamente a folha de frequência do diário de classe. Para
preencher este formulário, selecione um dia de aula nos calendários a esquerda e, em seguida, marque uma das opções na lista ao lado de cada aluno, 
indicando se ele esteve presente ou indicando quantas aulas ele perdeu no dia selecionado. Lembrando que cada horário de 50 minutos perdido 
corresponde a uma falta. Se o aluno tiver faltado a aula toda, basta clicar na imagem ao lado da lista para marcar a falta.</p>

<c:if test="${ frequenciaAluno.turmaChamadaBiometrica }">
	<br/>
	As frequencias dos discentes podem ser <b> lançadas pelo professor manualmente para cada aluno ou os próprios discentes podem registrar sua presença através </b> de computadores
	instalados em sala de aula. O ícone abaixo representa os discentes que registraram sua presença através da própria digital. <br><br>
	
	<center> <img src="${ ctx }/img/digital1.png" alt="Presença registrada por digital" title="Presença registrada por digital"/> </center>
</c:if>

</div>

<c:if test="${ empty frequenciaAluno.turma.horarios and !frequenciaAluno.turma.turmaEnsinoIndividual }">
<p class="semHorario">Esta turma não está com os horários definidos.</p>
</c:if>

<div id="corpo" class="corpo">
	<div id="calendarios" class="calendario">
	
		<c:set var="calendario" value=""/>
		<c:set var="iniciado" value="false"/>
	
		<c:forEach items="#{ frequenciaAluno.calendarios }" var="c">
			<c:if test="${calendario != c.nomeMes}">
				<c:set var="calendario" value="#{ c.nomeMes }"/>
				<h:outputText value="</table>" escape="false" rendered="#{ iniciado }" />
				<h:outputText value="<table><caption>#{ c.nomeMes }</caption><tr><th style='color:#FF0000;'>D</th><th>S</th><th>T</th><th>Q</th><th>Q</th><th>S</th><th>S</th></tr>" escape="false" />
				<c:set var="iniciado" value="false"/>
			</c:if>
			
			<c:if test="${ c.diaDaSemana == 0 }">
				<h:outputText value="<tr>" escape="false" />
			</c:if>
			
			<td style="
				<h:outputText value="border:2px solid #5588FF;background:#EEFEFF;" rendered="#{ c.dia == frequenciaAluno.dataSelecionada.date && c.mes == frequenciaAluno.dataSelecionada.month && c.ano == frequenciaAluno.dataSelecionada.year + 1900 }" />
				<h:outputText value="background:#FFFF99;" rendered="#{ c.semAula }" />
				<h:outputText value="color:     #FF0000;" rendered="#{ c.feriado }" />
				<h:outputText value="background:#6CDF46;" rendered="#{ c.frequenciaLancada == true }" />
			">
				<h:outputText value="#{ c.dia }" rendered="#{ c.dia > 0 && !c.exibirLink }" />
				<h:commandLink action="#{ frequenciaAluno.lancar }" value="#{ c.dia }" rendered="#{ c.dia > 0 && c.exibirLink }">
					<f:param name="dia" value="#{c.dia}" />
					<f:param name="mes" value="#{c.mes}" />
					<f:param name="ano" value="#{c.ano}" />
				</h:commandLink>
			</td>
			
			<c:if test="${ c.diaDaSemana == 6 }">
				<h:outputText value="</tr>" escape="false" />
			</c:if>
			
		</c:forEach>
		<h:outputText value="</table>" escape="false" />
		
	</div>
	<div id="frequencia" class="frequencia">
	
		<div style="margin-left:auto;margin-right:auto;border:1px solid #CCCCCC;margin-bottom:10px;width:70%;padding:10px;">
			<table id="legenda">
				<caption style="text-align:left;font-weight:bold;margin-bottom:10px;">Legendas:</caption>
				<tr>
					<td class="feriado">1</td><td style="width:60px;">: Feriado</td>	
					<td class="cancelada">1</td><td style="width:100px;">: Aula Cancelada</td>
					<td class="lancada">1</td><td>: Presenças lançadas</td>
				</tr>
			</table>
			<c:if test="${ not empty freq}">
				<div align="center" style="margin-top:10px;">
					<img src="/sigaa/ava/img/user_ok.png">: Marcar Presença
					<img src="/sigaa/img/monitoria/user1_delete.png">: Marcar Ausência
				</div>
			</c:if>
		</div>
	
		<c:if test="${ not empty freq }">
			<h4 style="text-align: center">Lista de Frequência - <fmt:formatDate value="${ frequenciaAluno.dataSelecionada }" pattern="dd/MM/yyyy"/></h4>
		</c:if>
		<br/>
		
		<c:if test="${frequenciaAluno.semAula}">
			<h4 style="text-align: center;color:red;">Aula Cancelada</h4>
		</c:if>
		
		<c:set var="max" value="${ frequenciaAluno.maxFaltas }"/>	
			
		<c:if test="${!frequenciaAluno.semAula  && not empty freq}">
		
		<rich:dataTable rowKeyVar="numero" var="freqAluno" style="margin-top:5px;" styleClass="listing" value="#{freq}" id="alunos" columnClasses="width5,width90, dummy, width90, icon, icon, icon" rowClasses="linhaImpar,linhaPar" rendered="#{ not empty freq }">
		
		<t:column>
			<f:facet name="header"><f:verbatim>#</f:verbatim></f:facet>
			<div align="right"><h:outputText value="#{numero+1}"/></div>
		</t:column>
		
		<t:column>
		<f:facet name="header"><f:verbatim>Matrícula</f:verbatim></f:facet>
		<h:outputText value="#{freqAluno.discente.matricula}" style="color: red;" rendered="#{freqAluno.faltas == frequenciaAluno.maxFaltas}" />
		<h:outputText value="#{freqAluno.discente.matricula}" rendered="#{freqAluno.faltas < frequenciaAluno.maxFaltas}" />
		</t:column>
		
		<t:column>
		<f:facet name="header"><f:verbatim><div align="left">Nome</div></f:verbatim></f:facet>
		<h:outputText value="#{freqAluno.discente.pessoa.nome}" style="color: red;" rendered="#{freqAluno.faltas == frequenciaAluno.maxFaltas}" />
		<h:outputText value="#{freqAluno.discente.pessoa.nome}" rendered="#{freqAluno.faltas < frequenciaAluno.maxFaltas}" />						
		</t:column>
		
		<t:column>
		<h:selectOneMenu value="#{freqAluno.faltas}" id="frequencia" styleClass="selectFreq">
		<f:selectItems value="#{frequenciaAluno.presencasCombo}" />
		</h:selectOneMenu>
		</t:column>
		
		<t:column rendered="#{frequenciaAluno.turmaChamadaBiometrica}">
		<h:graphicImage value="/img/digital2.png" alt="Presença registrada por digital" title="Presença registrada por digital do Discente" rendered="#{freqAluno.tipoCaptcaoFrequencia == 'D'}" />
		<h:graphicImage value="/img/ponteiro.png" alt="Presença registrada pelo professor" title="Presença registrada pela Turma Virtual" rendered="#{freqAluno.tipoCaptcaoFrequencia == 'T'}" />
		</t:column>
		
		<t:column>
		<f:facet name="header">
		<h:graphicImage value="/ava/img/user_ok.png" alt="Todos Presentes" style="cursor: pointer;" title="Todos Presentes" onclick="todosPresentes();"/>
		</f:facet>
		<f:verbatim>
		<img src="${ ctx }/ava/img/user_ok.png" alt="Presente" style="cursor: pointer;" title="Marcar Presença" onclick="marcarPresenca(this);"/>
		</f:verbatim>
		</t:column>
		
		<t:column style="width:20px;">
		<f:facet name="header">
		<h:graphicImage value="/img/monitoria/user1_delete.png" alt="Todos Ausentes" style="cursor: pointer;" title="Todos Ausentes" onclick="todosAusentes();"/>
		</f:facet>
		<f:verbatim>
		<img src="${ ctx }/img/monitoria/user1_delete.png" alt="Ausente" style="cursor: pointer;" title="Marcar Ausência" onclick="marcarFalta(this);"/>
		</f:verbatim>
		</t:column>
		
		</rich:dataTable> 

		<div align="center">
		<h:commandButton value="Gravar Frequências" action="#{ frequenciaAluno.cadastrar }" rendered="#{ not empty freq }"/> &nbsp;
		<h:commandButton value="Remover Frequências deste dia" action="#{ frequenciaAluno.remover }" rendered="#{ not empty freq }" onclick="#{confirmDelete}"/> &nbsp; 
		<a4j:outputPanel rendered="#{!frequenciaAluno.diaTemFrequencia}">
			<h:commandButton id="cancelarAula" value="Cancelar Aula" action="#{frequenciaAluno.cancelarAula}" immediate="true" onclick="return confirm('Deseja realmente cancelar esta aula?')" onchange="submit()"/>
			<ufrn:help>É possível indicar que não haverá aula. Neste caso não será necessário o lançamento da frequência
	   		 e os discentes não poderão notificar falta do docente.</ufrn:help>
		</a4j:outputPanel>
		<h:commandButton value="Cancelar" action="#{ frequenciaAluno.cancelar }" onclick="return confirm('Deseja realmente cancelar esta operação? Todos os dados não salvos serão perdidos!')" rendered="#{ not empty freq }"/>
		</div>
		</c:if>
	</div>
</div>

<input type="hidden" id="maxFaltas" value="${ frequenciaAluno.maxFaltas }"/>

</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp"%>

