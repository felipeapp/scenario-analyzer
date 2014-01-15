<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<a4j:keepAlive beanName="turmaTraducaoMBean"/>
<f:view>

<h2> <ufrn:subSistema /> &gt; Listagem de Turmas</h2>
	
<h:form id="formulario">
	<table class="formulario" style="width: 80%">
	  <caption>Critérios de busca</caption>
 		<tbody>
			<tr>
				<td width="3%"></td>
				<td width="11%" class="obrigatorio"><label for="checkComponente" onclick="$('formulario:checkComponente').checked = !$('formulario:checkComponente').checked;">Disciplina:</label></td>
				<td>
					<h:inputHidden id="idDisciplina" value="#{turmaTraducaoMBean.obj.disciplina.id}"></h:inputHidden>
					<h:inputText id="nomeDisciplina" value="#{turmaTraducaoMBean.obj.disciplina.nome}" size="60" onclick="$('formulario:checkComponente').checked = !$('formulario:checkComponente').checked;"/>
						<ajax:autocomplete
						source="formulario:nomeDisciplina" target="formulario:idDisciplina"
						baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
						indicator="indicatorDisciplina" minimumCharacters="3"
						parser="new ResponseXmlToHtmlListParser()" 
						parameters="nivel= ,todosOsProgramas=true"/>
						<span id="indicatorDisciplina" style="display:none; ">
						<img src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>
			
			<tr>
				<td><h:selectBooleanCheckbox value="#{turmaTraducaoMBean.filtroAnoPeriodo}" id="checkAnoPeriodo" styleClass="noborder"/> </td>
				<td><label for="checkAnoPeriodo" onclick="$('formulario:checkAnoPeriodo').checked = !$('formulario:checkAnoPeriodo').checked;">Ano-Período:</label></td>
				<td>
					<h:inputText value="#{turmaTraducaoMBean.obj.ano}" onfocus="$('formulario:checkAnoPeriodo').checked = true;" size="4" maxlength="4" 
								id="inputAno" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" style="text-align:right;" /> .
					<h:inputText value="#{turmaTraducaoMBean.obj.periodo}" onfocus="$('formulario:checkAnoPeriodo').checked = true;" size="1" maxlength="1" 
								id="inputPeriodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" style="text-align:right;"/>
				</td>
			</tr>
			
		</tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{turmaTraducaoMBean.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{turmaTraducaoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
	
<c:if test="${not empty turmaTraducaoMBean.resultadosBusca}">	
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar<br />
	</div>
	
	<table class="listagem">
	  <caption>Turma(s) Encontrado(s) (${fn:length(turmaTraducaoMBean.resultadosBusca)})</caption>
		<thead>
			<tr>
				<th width="30%">Disciplina</th>
				<th width="10%">Código</th>
				<th width="10%">Ano-Período</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="linha" items="#{turmaTraducaoMBean.resultadosBusca}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td> ${linha.disciplina.codigoNome} </td>
					<td> ${linha.codigo} </td>
					<td> ${linha.anoPeriodo} </td>
					<td width="2%" align="right">
						<h:commandLink action="#{turmaTraducaoMBean.selecionar}">
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Selecionar"/>
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>	
	
</h:form>
<br/><br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>